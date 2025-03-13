package com.app.booking.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.booking.LocalDatabaseHelper;
import com.app.booking.R;
import com.app.booking.adapter.TourAdapter;
import com.app.booking.model.Tour;

import java.util.ArrayList;
import java.util.List;

/**
 * Das FavoritesFragment zeigt die Liste der favorisierten Touren des Benutzers an.
 * Die Touren werden aus einer lokalen SQLite-Datenbank geladen.
 */
public class FavoritesFragment extends Fragment {

    private TourAdapter adapter;                  // Adapter für die RecyclerView
    private List<Tour> favoriteTourList;          // Liste der favorisierten Touren
    private TextView noFavoritesText;             // TextView, das angezeigt wird, wenn keine Favoriten vorhanden sind
    private LocalDatabaseHelper databaseHelper;   // Lokale SQLite-Datenbank

    /**
     * Wird aufgerufen, wenn die View für das Fragment erstellt wird.
     * Initialisiert die UI-Elemente und lädt die Favoriten aus der Datenbank.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Layout für das Fragment inflaten
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        // RecyclerView und LayoutManager initialisieren
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // TextView für den Fall, dass keine Favoriten vorhanden sind
        noFavoritesText = view.findViewById(R.id.no_favorites_text);
        noFavoritesText.setVisibility(View.GONE);  // Standardmäßig ausblenden

        // Liste der Favoriten und Adapter initialisieren
        favoriteTourList = new ArrayList<>();
        adapter = new TourAdapter(requireContext(), favoriteTourList);
        recyclerView.setAdapter(adapter);

        // Lokale Datenbank-Instanz initialisieren
        databaseHelper = new LocalDatabaseHelper(requireContext());

        // Favoriten aus der Datenbank laden und anzeigen
        loadFavoriteTours();

        return view;
    }

    /**
     * Lädt die favorisierten Touren aus der lokalen Datenbank.
     * Falls keine Favoriten vorhanden sind, wird ein Hinweis angezeigt.
     */
    private void loadFavoriteTours() {
        // Liste der Favoriten leeren und neu befüllen
        favoriteTourList.clear();
        favoriteTourList.addAll(databaseHelper.getFavoriteTours());

        // Überprüfen, ob die Favoritenliste leer ist
        if (favoriteTourList.isEmpty()) {
            noFavoritesText.setVisibility(View.VISIBLE);  // Hinweis anzeigen, wenn keine Favoriten vorhanden sind
        } else {
            noFavoritesText.setVisibility(View.GONE);     // Hinweis ausblenden, wenn Favoriten vorhanden sind
        }

        // RecyclerView aktualisieren
        adapter.notifyDataSetChanged();
    }
}