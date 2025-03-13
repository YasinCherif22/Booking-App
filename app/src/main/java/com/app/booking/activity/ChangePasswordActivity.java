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

/**
 * Die ChangePasswordActivity ermöglicht es dem Benutzer, sein Passwort zu ändern.
 * Die Änderung erfolgt über Firebase Authentication.
 */
public class ChangePasswordActivity extends AppCompatActivity {

    // UI-Elemente
    private EditText etOldPassword, etNewPassword;
    private Button btnSave;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private ImageView backBtn;

    /**
     * Wird beim Erstellen der Aktivität aufgerufen.
     * Initialisiert die UI-Komponenten und setzt Event-Listener.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Ändert die Farbe der Navigationsleiste
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color60));

        // Firebase Authentication-Instanz initialisieren
        auth = FirebaseAuth.getInstance();

        // UI-Elemente initialisieren
        etOldPassword = findViewById(R.id.et_old_password);
        etNewPassword = findViewById(R.id.et_new_password);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar_reset);
        backBtn = findViewById(R.id.backButton);

        // Anfangs: Speichern-Button deaktivieren
        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f); // Button ausgegraut

        // Zurück-Button: Schließt die aktuelle Aktivität
        backBtn.setOnClickListener(v -> finish());

        // TextWatcher zur Überwachung der Passwort-Eingaben
        TextWatcher passwordWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Aktiviert den Speichern-Button nur, wenn beide Felder ausgefüllt sind
                if (!etOldPassword.getText().toString().trim().isEmpty() &&
                        !etNewPassword.getText().toString().trim().isEmpty()) {
                    btnSave.setEnabled(true);
                    btnSave.setAlpha(1.0f); // Button aktiv
                } else {
                    btnSave.setEnabled(false);
                    btnSave.setAlpha(0.5f); // Button ausgegraut
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };

        // TextWatcher zu den Eingabefeldern hinzufügen
        etOldPassword.addTextChangedListener(passwordWatcher);
        etNewPassword.addTextChangedListener(passwordWatcher);

        // Klick-Listener für den Speichern-Button
        btnSave.setOnClickListener(v -> {
            String oldPassword = etOldPassword.getText().toString().trim();
            String newPassword = etNewPassword.getText().toString().trim();

            if (!oldPassword.isEmpty() && !newPassword.isEmpty()) {
                changePassword(oldPassword, newPassword);
            } else {
                Toast.makeText(ChangePasswordActivity.this, "Bitte altes und neues Passwort eingeben", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Ändert das Passwort des Benutzers nach vorheriger Authentifizierung.
     * @param oldPassword Das aktuelle Passwort des Benutzers.
     * @param newPassword Das neue Passwort, das gesetzt werden soll.
     */
    private void changePassword(String oldPassword, String newPassword) {
        // Deaktiviert den Button und zeigt den Ladebalken an
        btnSave.setEnabled(false);
        btnSave.setAlpha(0.5f);
        progressBar.setVisibility(View.VISIBLE);

        FirebaseUser currentUser = auth.getCurrentUser();

        // Überprüfen, ob ein Benutzer angemeldet ist und eine E-Mail-Adresse hat
        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(ChangePasswordActivity.this, "Benutzer nicht angemeldet oder keine E-Mail vorhanden", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            btnSave.setEnabled(true);
            btnSave.setAlpha(1f);
            return;
        }

        String email = currentUser.getEmail();

        // Benutzer muss sich zuerst mit altem Passwort authentifizieren
        auth.signInWithEmailAndPassword(email, oldPassword)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Falls Authentifizierung erfolgreich war, Passwort aktualisieren
                        currentUser.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                Toast.makeText(ChangePasswordActivity.this, "Passwort erfolgreich geändert!", Toast.LENGTH_SHORT).show();
                                finish(); // Schließt die Aktivität nach erfolgreicher Änderung
                            } else {
                                Toast.makeText(ChangePasswordActivity.this, "Fehler beim Ändern des Passworts", Toast.LENGTH_SHORT).show();
                                btnSave.setEnabled(true);
                                btnSave.setAlpha(1f);
                            }
                        });
                    } else {
                        // Falls die Authentifizierung fehlschlägt
                        btnSave.setEnabled(true);
                        btnSave.setAlpha(1f);
                        Toast.makeText(ChangePasswordActivity.this, "Anmeldung fehlgeschlagen! Überprüfen Sie Ihr aktuelles Passwort.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}