package com.app.booking.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.booking.R;
import com.app.booking.fragment.FavoritesFragment;
import com.app.booking.fragment.HomeFragment;
import com.app.booking.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Die MainActivity ist die Hauptseite der App und verwaltet die Navigation zwischen
 * den Haupt-Fragmenten: Home, Favoriten und Profil.
 */
public class MainActivity extends AppCompatActivity {

    // Firebase-Authentifizierung
    private FirebaseAuth auth;

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     * Initialisiert die UI-Elemente, überprüft die Authentifizierung und setzt das Start-Fragment.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Farbe der Navigationsleiste anpassen
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color30));

        // Firebase Auth-Instanz initialisieren
        auth = FirebaseAuth.getInstance();

        // Falls kein Benutzer angemeldet ist, zur Login-Seite weiterleiten
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Standardmäßig HomeFragment setzen, falls keine vorherige Instanz existiert
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        // Navigation-Listener für die untere Navigationsleiste einrichten
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Bestimmen, welches Fragment geladen werden soll
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_favorites) {
                selectedFragment = new FavoritesFragment();
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            // Falls ein gültiges Fragment ausgewählt wurde, es im Container ersetzen
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });
    }

    /**
     * Überschreibt die Zurück-Taste, um das ungewollte Verlassen der App zu verhindern.
     * Falls eine Aktion beim Zurückdrücken erforderlich ist, kann sie hier implementiert werden.
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); verhindert, dass die App durch Zurück-Drücken geschlossen wird
    }
}