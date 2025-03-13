package com.app.booking.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.booking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Die LoginActivity ermöglicht es Benutzern, sich mit ihrer E-Mail und ihrem Passwort anzumelden.
 * Falls der Benutzer bereits eingeloggt ist, wird er direkt zur Hauptseite weitergeleitet.
 */
public class LoginActivity extends AppCompatActivity {

    // Firebase-Authentifizierung
    private FirebaseAuth auth;

    // UI-Elemente für Benutzereingaben
    private EditText etEmail, etPassword;
    private Button btnLogin;

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     * Initialisiert die UI-Elemente, Firebase-Instanzen und setzt die Event-Listener.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Setzt die Farbe der Navigationsleiste
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color60));

        // Firebase Auth-Instanz initialisieren
        auth = FirebaseAuth.getInstance();

        // Falls der Benutzer bereits angemeldet ist, direkt zur Hauptseite weiterleiten
        if (auth.getCurrentUser() != null) {
            navigateToMain();
        }

        // UI-Elemente aus dem Layout referenzieren
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        // Login-Button zunächst deaktivieren, bis gültige Eingaben vorliegen
        btnLogin.setEnabled(false);
        btnLogin.setAlpha(0.5f); // Button ausgegraut

        // TextWatcher für E-Mail- und Passwort-Eingabe aktivieren
        etEmail.addTextChangedListener(loginTextWatcher);
        etPassword.addTextChangedListener(loginTextWatcher);

        // Klick-Listener für den Login-Button
        btnLogin.setOnClickListener(v -> {
            // Button während der Anmeldung deaktivieren, um Mehrfachklicks zu vermeiden
            btnLogin.setEnabled(false);
            btnLogin.setAlpha(0.5f);

            // Benutzereingaben abrufen
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Anmeldung mit Firebase durchführen
            signInWithEmail(email, password);
        });

        // Klick-Listener für "Passwort vergessen"
        TextView forgotPassword = findViewById(R.id.tv_forgot_password);
        forgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class)));

        // Klick-Listener für "Registrieren"
        TextView btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish(); // Schließt die LoginActivity, um nach der Registrierung nicht zurückzukehren
        });
    }

    /**
     * Meldet den Benutzer mit der eingegebenen E-Mail-Adresse und dem Passwort bei Firebase an.
     * @param email    Die eingegebene E-Mail-Adresse.
     * @param password Das eingegebene Passwort.
     */
    private void signInWithEmail(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Erfolgreiche Anmeldung
                        Toast.makeText(LoginActivity.this, "Erfolgreich angemeldet!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        // Anmeldung fehlgeschlagen, Button wieder aktivieren
                        btnLogin.setEnabled(true);
                        btnLogin.setAlpha(1f);

                        Toast.makeText(LoginActivity.this, "Anmeldung fehlgeschlagen!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Navigiert zur Hauptseite der App (MainActivity).
     */
    private void navigateToMain() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish(); // Beendet die LoginActivity, damit der Benutzer nicht zurückkehren kann
    }

    /**
     * TextWatcher zur Überwachung der E-Mail- und Passwort-Eingaben.
     * Aktiviert den Login-Button nur, wenn die Eingaben gültig sind.
     */
    private final TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String emailInput = etEmail.getText().toString().trim();
            String passwordInput = etPassword.getText().toString().trim();

            // Überprüft, ob die E-Mail-Adresse gültig ist und ob ein Passwort eingegeben wurde
            boolean isValidInput = isValidEmail(emailInput) && !passwordInput.isEmpty();

            // Button aktivieren oder deaktivieren je nach Eingabevalidität
            btnLogin.setEnabled(isValidInput);
            btnLogin.setAlpha(isValidInput ? 1.0f : 0.5f);
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    /**
     * Überprüft, ob die eingegebene E-Mail-Adresse ein gültiges Format hat.
     * @param email Die zu überprüfende E-Mail-Adresse.
     * @return true, wenn die E-Mail gültig ist, sonst false.
     */
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); verhindert, dass die App durch Zurück-Drücken geschlossen wird
    }
}