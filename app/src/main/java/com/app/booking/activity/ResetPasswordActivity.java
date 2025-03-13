package com.app.booking.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
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

/**
 * Die ResetPasswordActivity ermöglicht es dem Benutzer, eine E-Mail zum Zurücksetzen des Passworts zu senden.
 * Der Benutzer gibt seine E-Mail-Adresse ein, und Firebase sendet eine E-Mail mit weiteren Anweisungen.
 */
public class ResetPasswordActivity extends AppCompatActivity {

    // UI-Elemente
    private EditText etEmail;
    private Button btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private ImageView backBtn;

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     * Initialisiert die UI-Elemente und Firebase-Instanzen und setzt die Event-Listener.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Setzt die Farbe der Navigationsleiste
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color60));

        // Firebase Auth-Instanz initialisieren
        auth = FirebaseAuth.getInstance();

        // UI-Elemente aus dem Layout referenzieren
        etEmail = findViewById(R.id.et_email_reset);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        progressBar = findViewById(R.id.progress_bar_reset);
        backBtn = findViewById(R.id.backButton);

        // Anfangs: Button deaktivieren
        btnResetPassword.setEnabled(false);
        btnResetPassword.setAlpha(0.5f); // Button ausgegraut

        // Zurück-Button Klick-Listener
        backBtn.setOnClickListener(v -> finish());

        // TextWatcher hinzufügen, um die E-Mail-Eingabe zu überwachen
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Überprüfung der E-Mail-Validität
                if (isValidEmail(charSequence.toString())) {
                    btnResetPassword.setEnabled(true);
                    btnResetPassword.setAlpha(1.0f); // Button aktivieren
                } else {
                    btnResetPassword.setEnabled(false);
                    btnResetPassword.setAlpha(0.5f); // Button ausgegraut
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Klick-Listener für den Passwort-Zurücksetzen-Button
        btnResetPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (!email.isEmpty()) {
                resetPassword(email);
            } else {
                Toast.makeText(ResetPasswordActivity.this, "Bitte eine gültige E-Mail-Adresse eingeben", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sendet eine Passwort-Zurücksetzen-E-Mail an die eingegebene Adresse.
     * @param email Die eingegebene E-Mail-Adresse.
     */
    private void resetPassword(String email) {
        // Button deaktivieren und Fortschrittsbalken anzeigen
        btnResetPassword.setEnabled(false);
        btnResetPassword.setAlpha(0.5f);
        progressBar.setVisibility(View.VISIBLE);

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    // Fortschrittsbalken ausblenden
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        // Erfolgreich gesendet
                        Toast.makeText(ResetPasswordActivity.this, "E-Mail zum Zurücksetzen des Passworts wurde gesendet", Toast.LENGTH_SHORT).show();
                        finish();  // Schließt die Aktivität nach erfolgreichem Senden
                    } else {
                        // Fehler aufgetreten, Button wieder aktivieren
                        btnResetPassword.setEnabled(true);
                        btnResetPassword.setAlpha(1.0f);
                        Toast.makeText(ResetPasswordActivity.this, "Fehler beim Zurücksetzen des Passworts. Bitte überprüfe die eingegebene E-Mail-Adresse.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Überprüft, ob die eingegebene E-Mail-Adresse ein gültiges Format hat.
     * @param email Die zu überprüfende E-Mail-Adresse.
     * @return true, wenn die E-Mail gültig ist, sonst false.
     */
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}