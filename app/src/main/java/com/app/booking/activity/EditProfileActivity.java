package com.app.booking.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.booking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

/**
 * Die EditProfileActivity ermöglicht es dem Benutzer, seinen Anzeigenamen zu ändern.
 * Die Änderung erfolgt über Firebase Authentication.
 */
public class EditProfileActivity extends AppCompatActivity {

    // UI-Elemente
    private EditText etName;
    private Button btnSave;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private ImageView backBtn;

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     * Initialisiert die UI-Elemente und setzt Event-Listener.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Farbe der Navigationsleiste ändern
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color60));

        // Firebase Authentication-Instanz initialisieren
        auth = FirebaseAuth.getInstance();

        // UI-Elemente initialisieren
        etName = findViewById(R.id.et_name);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar_reset);
        backBtn = findViewById(R.id.backButton);

        // Anfangs: Button deaktivieren
        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f); // Button ausgegraut

        // Zurück-Button: Schließt die aktuelle Aktivität
        backBtn.setOnClickListener(v -> finish());

        // Aktuellen Benutzer abrufen
        FirebaseUser currentUser = auth.getCurrentUser();

        // Falls kein Benutzer angemeldet ist, Fehlermeldung anzeigen und Aktivität beenden
        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(EditProfileActivity.this, "Benutzer nicht angemeldet oder keine E-Mail vorhanden", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        // Aktuellen Anzeigenamen abrufen und ins Textfeld setzen
        String displayNameFinal = currentUser.getDisplayName();
        etName.setText(displayNameFinal);

        // TextWatcher zur Überwachung der Namenseingabe
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Button aktivieren, falls der Name nicht leer ist und sich geändert hat
                if (!etName.getText().toString().trim().isEmpty() && !Objects.equals(displayNameFinal, etName.getText().toString().trim())) {
                    btnSave.setEnabled(true);
                    btnSave.setAlpha(1.0f); // Button aktiv
                } else {
                    btnSave.setEnabled(false);
                    btnSave.setAlpha(0.5f); // Button ausgegraut
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Klick-Listener für den Speichern-Button
        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();

            if (!newName.isEmpty()) {
                changeDisplayName(newName);
            }
        });
    }

    /**
     * Ändert den Anzeigenamen des Benutzers.
     * @param newName Der neue Anzeigename.
     */
    private void changeDisplayName(String newName) {
        // Button deaktivieren und Fortschrittsbalken anzeigen
        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);
        progressBar.setVisibility(View.VISIBLE);

        // Aktuellen Benutzer abrufen
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // Benutzerprofil mit dem neuen Anzeigennamen aktualisieren
            user.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Erfolgsmeldung anzeigen und Aktivität beenden
                            Toast.makeText(getApplicationContext(), "Benutzername aktualisiert", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // Falls die Aktualisierung fehlschlägt, Fehlermeldung anzeigen
                            Toast.makeText(getApplicationContext(), "Aktualisierung des Benutzernamens fehlgeschlagen", Toast.LENGTH_SHORT).show();

                            // Button wieder aktivieren und Fortschrittsbalken ausblenden
                            btnSave.setEnabled(true);
                            btnSave.setAlpha(1.0f);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }
}