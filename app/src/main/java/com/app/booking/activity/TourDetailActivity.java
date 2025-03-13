package com.app.booking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.app.booking.LocalDatabaseHelper;
import com.app.booking.R;
import com.app.booking.model.Tour;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

/**
 * Die TourDetailActivity zeigt die detaillierten Informationen einer Tour an.
 * Der Benutzer kann die Tour als Favorit speichern oder eine Buchung starten.
 */
public class TourDetailActivity extends AppCompatActivity {

    // UI-Elemente
    private ImageView ivTourImage;
    private TextView tvTourName, tvTourDescription;
    private ExtendedFloatingActionButton bookButton;
    private FrameLayout backBtn, favorBtn;
    private ImageView[] stars = new ImageView[5];
    private CardView recommended;

    // Lokale Datenbank für Favoriten
    private LocalDatabaseHelper databaseHelper;

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     * Initialisiert die UI-Elemente, lädt die Tour-Daten und setzt Event-Listener.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        // Navigationsleiste farblich anpassen
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color60));

        // Datenbank-Instanz initialisieren
        databaseHelper = new LocalDatabaseHelper(this);

        // UI-Elemente referenzieren
        backBtn = findViewById(R.id.backBtn);
        favorBtn = findViewById(R.id.favorBtn);
        ivTourImage = findViewById(R.id.iv_tour_image);
        tvTourName = findViewById(R.id.tv_tour_name);
        tvTourDescription = findViewById(R.id.tv_tour_description);
        bookButton = findViewById(R.id.bookButton);
        recommended = findViewById(R.id.recommended);

        // Sterne für die Bewertung referenzieren
        stars[0] = findViewById(R.id.star1);
        stars[1] = findViewById(R.id.star2);
        stars[2] = findViewById(R.id.star3);
        stars[3] = findViewById(R.id.star4);
        stars[4] = findViewById(R.id.star5);

        // Tour-Objekt aus der vorherigen Aktivität abrufen
        Tour tour = getIntent().getParcelableExtra("tour");

        // Setze die Tour-Daten in die UI-Elemente
        tvTourName.setText(tour.getTitle());
        tvTourDescription.setText(tour.getDescription());

        // Tour-Bild mit Glide laden oder Platzhalter setzen
        if (tour.getImageUrl() != null && !tour.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(tour.getImageUrl())
                    .into(ivTourImage);
        } else {
            ivTourImage.setImageResource(R.drawable.image_placeholder); // Fallback-Bild
        }

        // Zurück-Button: Schließt die aktuelle Aktivität
        backBtn.setOnClickListener(v -> finish());

        // Favoriten-Button: Zeigt den aktuellen Favoritenstatus und ermöglicht das Umschalten
        setFavoriteIcon(favorBtn, tour.getId());
        favorBtn.setOnClickListener(v -> {
            if (databaseHelper.isFavorite(tour.getId())) {
                databaseHelper.removeFavorite(tour.getId());
            } else {
                databaseHelper.addFavorite(tour);
            }
            setFavoriteIcon(favorBtn, tour.getId());
        });

        // Klick-Listener für den Buchen-Button
        bookButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FormActivity.class);
            intent.putExtra("tour", tour);
            startActivity(intent);
        });

        // Bewertung der Tour setzen und ggf. das "Empfohlen"-Banner anzeigen
        setTourRating(tour.getRating());
    }

    /**
     * Aktualisiert das Favoriten-Symbol basierend auf dem Status in der lokalen Datenbank.
     * @param favorBtn Das Favoriten-Button-Element.
     * @param id Die ID der Tour.
     */
    private void setFavoriteIcon(FrameLayout favorBtn, String id) {
        ImageView icon = (ImageView) favorBtn.getChildAt(1);
        if (databaseHelper.isFavorite(id)) {
            icon.setImageResource(R.drawable.pho_heart_fill); // Favorit (gefüllt)
        } else {
            icon.setImageResource(R.drawable.pho_heart); // Kein Favorit (ungefüllt)
        }
    }

    /**
     * Setzt die Sternebewertung für die Tour und zeigt das "Empfohlen"-Banner, falls notwendig.
     * @param rating Die Bewertung der Tour als Float-Wert.
     */
    private void setTourRating(float rating) {
        recommended.setVisibility(View.GONE); // Standardmäßig ausblenden

        // Bewertung auf 0.5er-Schritte runden
        float roundedRating = Math.round(rating * 2) / 2.0f;

        // Falls die Tour die höchste Bewertung hat, das "Empfohlen"-Banner anzeigen
        if (roundedRating == 5) {
            recommended.setVisibility(View.VISIBLE);
        }

        // Sterne entsprechend der Bewertung setzen
        for (int i = 0; i < 5; i++) {
            if (roundedRating >= i + 1) {
                stars[i].setImageResource(R.drawable.pho_star_fill); // Voller Stern
            } else if (roundedRating >= i + 0.5f) {
                stars[i].setImageResource(R.drawable.pho_star_half_fill); // Halb gefüllter Stern
            } else {
                stars[i].setImageResource(R.drawable.pho_star); // Leerer Stern
            }
        }
    }
}