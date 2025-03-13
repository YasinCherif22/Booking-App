package com.app.booking.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.booking.R;
import com.app.booking.activity.TourDetailActivity;
import com.app.booking.model.Tour;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Der TourAdapter dient als Adapter für die RecyclerView-Liste von Touren.
 * Er verwaltet die Anzeige der Touren und setzt Click-Listener für die Detailansicht.
 */
public class TourAdapter extends RecyclerView.Adapter<TourAdapter.TourViewHolder> {

    private final Context context;
    private final List<Tour> tourList;

    /**
     * Konstruktor für den Adapter.
     * @param context Der Kontext der Anwendung.
     * @param tourList Die Liste der Tour-Objekte.
     */
    public TourAdapter(Context context, List<Tour> tourList) {
        this.context = context;
        this.tourList = tourList;
    }

    /**
     * Erstellt und gibt eine neue ViewHolder-Instanz zurück.
     */
    @NonNull
    @Override
    public TourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tour, parent, false);
        return new TourViewHolder(view);
    }

    /**
     * Befüllt den ViewHolder mit den Daten der jeweiligen Tour.
     */
    @Override
    public void onBindViewHolder(@NonNull TourViewHolder holder, int position) {
        Tour tour = tourList.get(position);

        // Falls die Tour-ID null oder leer ist, setze eine Standard-ID
        if (tour.getId() == null || tour.getId().isEmpty()) {
            tour.setId("default-id-" + position);
        }

        // Setze Tour-Titel und -Ort
        holder.tourTitle.setText(tour.getTitle());
        holder.tourLocation.setText(tour.getLocation());

        // Lade das Tour-Bild mit Glide oder zeige ein Platzhalterbild
        if (tour.getImageUrl() != null && !tour.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(tour.getImageUrl())
                    .into(holder.tourImage);
        } else {
            holder.tourImage.setImageResource(R.drawable.image_placeholder);
        }

        // Setze einen Klick-Listener für die Detailansicht der Tour
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TourDetailActivity.class);
            intent.putExtra("tour", tour); // Tour muss Parcelable sein
            context.startActivity(intent);
        });

        // Standardmäßig die Empfehlungsauszeichnung ausblenden
        holder.recommended.setVisibility(View.GONE);

        // Berechnung der Bewertung und Rundung auf halbe Sterne
        float rating = tour.getRating();
        float roundedRating = Math.round(rating * 2) / 2.0f;

        // Wenn die Bewertung 5 ist, die Empfehlungsauszeichnung anzeigen
        if (roundedRating == 5) {
            holder.recommended.setVisibility(View.VISIBLE);
        }

        // Sternebewertung setzen
        updateStarRating(holder.stars, roundedRating);
    }

    /**
     * Gibt die Anzahl der Touren in der Liste zurück.
     */
    @Override
    public int getItemCount() {
        return tourList.size();
    }

    /**
     * Aktualisiert die Sternebewertung basierend auf dem gerundeten Wert.
     * @param stars Ein Array von ImageViews für die Sterne.
     * @param roundedRating Die gerundete Bewertung der Tour.
     */
    private void updateStarRating(ImageView[] stars, float roundedRating) {
        for (int i = 0; i < 5; i++) {
            if (roundedRating >= i + 1) {
                stars[i].setImageResource(R.drawable.pho_star_fill); // Voll gefüllter Stern
            } else if (roundedRating >= i + 0.5f) {
                stars[i].setImageResource(R.drawable.pho_star_half_fill); // Halb gefüllter Stern
            } else {
                stars[i].setImageResource(R.drawable.pho_star); // Leerer Stern
            }
        }
    }

    /**
     * ViewHolder-Klasse zur Optimierung der RecyclerView-Leistung.
     */
    public static class TourViewHolder extends RecyclerView.ViewHolder {

        TextView tourTitle, tourLocation;
        ImageView tourImage;
        ImageView[] stars = new ImageView[5];
        CardView recommended;

        /**
         * Konstruktor des ViewHolders, initialisiert alle UI-Elemente.
         */
        public TourViewHolder(@NonNull View itemView) {
            super(itemView);
            tourTitle = itemView.findViewById(R.id.tour_title);
            tourLocation = itemView.findViewById(R.id.tour_location);
            tourImage = itemView.findViewById(R.id.tour_image);
            recommended = itemView.findViewById(R.id.recommended);

            // Initialisierung der Sterne-Icons
            stars[0] = itemView.findViewById(R.id.star1);
            stars[1] = itemView.findViewById(R.id.star2);
            stars[2] = itemView.findViewById(R.id.star3);
            stars[3] = itemView.findViewById(R.id.star4);
            stars[4] = itemView.findViewById(R.id.star5);
        }
    }
}