package com.app.booking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.booking.R;

/**
 * Die ConfirmationActivity dient als Bestätigungsseite nach einer erfolgreichen Aktion.
 * Sobald der Benutzer auf den OK-Button klickt, wird er zur Hauptseite (MainActivity) weitergeleitet.
 */
public class ConfirmationActivity extends AppCompatActivity {

    // UI-Element für den OK-Button
    private Button btn_ok;

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     * Initialisiert die UI-Elemente und setzt den Klick-Listener für den OK-Button.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        // Setzt die Farbe der Navigationsleiste
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color60));

        // OK-Button aus dem Layout referenzieren
        btn_ok = findViewById(R.id.btn_ok);

        // Klick-Listener für den OK-Button
        btn_ok.setOnClickListener(v -> {
            // Wechsel zur MainActivity nach Bestätigung
            startActivity(new Intent(ConfirmationActivity.this, MainActivity.class));
            finish(); // Schließt die aktuelle Aktivität, damit sie nicht im Back-Stack bleibt
        });
    }
}