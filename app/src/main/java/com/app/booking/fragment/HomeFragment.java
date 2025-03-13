package com.app.booking.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.booking.model.Tour;
import com.app.booking.R;
import com.app.booking.adapter.TourAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Das HomeFragment zeigt eine Liste von Touren an, die aus Firestore geladen werden.
 * Die Liste wird nach Bewertung sortiert.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private TourAdapter adapter;
    private final List<Tour> tourList = new ArrayList<>();
    private FirebaseFirestore db;
    private ProgressBar tourLoading;

    /**
     * Wird aufgerufen, wenn die View für das Fragment erstellt wird.
     * Initialisiert die UI-Elemente und lädt die Tour-Daten.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Layout für das Fragment inflaten
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // RecyclerView initialisieren
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Adapter initialisieren und setzen
        adapter = new TourAdapter(getActivity(), tourList);
        recyclerView.setAdapter(adapter);

        // Ladebalken initialisieren
        tourLoading = view.findViewById(R.id.garden_loading);

        // Firestore-Instanz initialisieren
        db = FirebaseFirestore.getInstance();

        // Falls die Liste leer ist, lade Touren aus Firestore
        if (tourList.isEmpty()) {
            loadTours();
        } else {
            tourLoading.setVisibility(View.GONE);
        }

        return view;
    }

    /**
     * Lädt die Tour-Daten aus Firestore.
     * Zeigt den Ladebalken an, während die Daten geladen werden.
     */
    private void loadTours() {
        tourLoading.setVisibility(View.VISIBLE);

        db.collection("tours")
                .get()
                .addOnCompleteListener(task -> {
                    // Ladebalken ausblenden, sobald das Laden abgeschlossen ist
                    tourLoading.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        handleTourResults(task.getResult());
                    } else {
                        // Falls die Anfrage fehlschlägt, könnte hier eine Fehlermeldung angezeigt werden.
                    }
                });
    }

    /**
     * Verarbeitet die geladenen Tour-Daten aus Firestore und aktualisiert die Liste.
     * @param querySnapshot Das Firestore QuerySnapshot mit den Tour-Dokumenten.
     */
    private void handleTourResults(QuerySnapshot querySnapshot) {
        if (querySnapshot != null && !querySnapshot.isEmpty()) {
            tourList.clear(); // Vorhandene Liste leeren

            // Iteriere durch die Firestore-Dokumente und erstelle Tour-Objekte
            for (DocumentSnapshot document : querySnapshot) {
                Tour tour = document.toObject(Tour.class);
                if (tour != null) {
                    tour.setId(document.getId()); // Setze die Firestore-ID als Tour-ID
                    tourList.add(tour);
                }
            }

            // Sortiere die Touren nach Bewertung (höchste zuerst)
            tourList.sort((t1, t2) -> Float.compare(t2.getRating(), t1.getRating()));

            // Adapter benachrichtigen, dass sich die Daten geändert haben
            adapter.notifyDataSetChanged();
        }
    }
}