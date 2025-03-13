package com.app.booking.activity;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.app.booking.R;

/**
 * Die SettingsActivity ermöglicht es dem Benutzer, verschiedene Einstellungen vorzunehmen,
 * darunter das Wechseln des Dark-Modes und das Anzeigen der aktuellen App-Version.
 */
public class SettingsActivity extends AppCompatActivity {

    // UI-Elemente
    private ImageButton backBtn;
    private TextView versiontxt;

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     * Initialisiert die UI-Elemente, setzt den Dark-Mode-Dialog und zeigt die App-Version an.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Edge-to-Edge Modus aktivieren
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Navigationsleiste farblich anpassen
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color60));

        // UI-Elemente initialisieren
        backBtn = findViewById(R.id.backButton);
        versiontxt = findViewById(R.id.version_text);

        // Klick-Listener für die Zurück-Schaltfläche
        backBtn.setOnClickListener(v -> finish());

        // App-Version abrufen und anzeigen
        try {
            versiontxt.setText("Version " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            versiontxt.setText("Version ?");
            throw new RuntimeException(e);
        }

        // Klick-Listener für die Dark-Mode-Einstellung
        LinearLayout darkMode = findViewById(R.id.dark_mode);
        darkMode.setOnClickListener(v -> showCustomDarkModeDialog());
    }

    /**
     * Zeigt einen Dialog für die Auswahl des Dark-Modes an.
     * Die Auswahl wird in den SharedPreferences gespeichert und das Theme der App angepasst.
     */
    private void showCustomDarkModeDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialog)
                .setTitle(getString(R.string.dark_mode))
                .setItems(new String[]{
                        getString(R.string.on),
                        getString(R.string.off),
                        getString(R.string.system_default)
                }, (dialogg, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("user_preferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // Je nach Auswahl den Dark-Mode setzen
                    switch (which) {
                        case 0: // "On" ausgewählt
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                            editor.putInt("dark_mode", AppCompatDelegate.MODE_NIGHT_YES);
                            break;
                        case 1: // "Off" ausgewählt
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                            editor.putInt("dark_mode", AppCompatDelegate.MODE_NIGHT_NO);
                            break;
                        case 2: // "System default" ausgewählt
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            editor.putInt("dark_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                            break;
                    }
                    editor.apply(); // Änderungen speichern
                    dialogg.dismiss();
                    recreate(); // Aktivität neu laden, um die Änderungen anzuwenden
                })
                .setNegativeButton(R.string.cancel, null)
                .create();

        dialog.show();

        // Farbe der "Abbrechen"-Schaltfläche anpassen
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.accentBlue));
    }
}