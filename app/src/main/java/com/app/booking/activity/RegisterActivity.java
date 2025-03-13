package com.app.booking.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.booking.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Die RegisterActivity ermöglicht es Benutzern, sich mit einer E-Mail-Adresse und einem Passwort zu registrieren.
 * Nach erfolgreicher Registrierung wird der Benutzername gesetzt und zur Hauptseite weitergeleitet.
 */
public class RegisterActivity extends AppCompatActivity {

    // Firebase-Authentifizierung
    private FirebaseAuth auth;

    // UI-Elemente für Benutzereingaben
    private EditText etEmail, etPassword;
    private TextInputLayout emailLayout, passwordLayout;
    private Button btnRegister;

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     * Initialisiert die UI-Elemente, Firebase-Instanzen und setzt die Event-Listener.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Setzt die Farbe der Navigationsleiste
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color60));

        // Firebase Auth-Instanz initialisieren
        auth = FirebaseAuth.getInstance();

        // UI-Elemente aus dem Layout referenzieren
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        btnRegister = findViewById(R.id.btn_register);

        // Button zunächst deaktivieren
        btnRegister.setEnabled(false);
        btnRegister.setAlpha(0.5f); // Button ausgegraut

        // TextWatcher hinzufügen, um Eingaben zu überwachen und den Button zu aktivieren/deaktivieren
        etEmail.addTextChangedListener(registerTextWatcher);
        etPassword.addTextChangedListener(registerTextWatcher);

        // Klick-Listener für den Registrierungs-Button
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            registerWithEmail(email, password);
        });

        // Klick-Listener für "Bereits registriert? Einloggen"
        TextView btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Registriert den Benutzer mit der eingegebenen E-Mail-Adresse und dem Passwort.
     * Falls die Registrierung erfolgreich ist, wird der Benutzername gesetzt und zur Hauptseite navigiert.
     * @param email    Die eingegebene E-Mail-Adresse.
     * @param password Das eingegebene Passwort.
     */
    private void registerWithEmail(String email, String password) {
        String name = ((EditText) findViewById(R.id.et_name)).getText().toString().trim();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Benutzer erfolgreich erstellt
                        FirebaseUser user = auth.getCurrentUser();

                        if (user != null) {
                            // Benutzerprofil mit dem eingegebenen Namen aktualisieren
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Toast.makeText(RegisterActivity.this, "Registrierung erfolgreich!", Toast.LENGTH_SHORT).show();
                                            navigateToMain();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Fehler beim Setzen des Namens", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    } else {
                        // Fehlerbehandlung bei der Registrierung
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            emailLayout.setError("E-Mail-Adresse wird bereits verwendet");
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registrierung fehlgeschlagen!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Navigiert zur Hauptseite der App (MainActivity).
     */
    private void navigateToMain() {
        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        finish(); // Beendet die RegisterActivity, damit der Benutzer nicht zurückkehren kann
    }

    /**
     * TextWatcher zur Überwachung der E-Mail- und Passwort-Eingaben.
     * Aktiviert den Registrierungs-Button nur, wenn die Eingaben gültig sind.
     */
    private final TextWatcher registerTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String emailInput = etEmail.getText().toString().trim();
            String passwordInput = etPassword.getText().toString().trim();

            // Button aktivieren, wenn E-Mail und Passwort gültig sind
            boolean isValid = isValidEmail(emailInput) && isValidPassword(passwordInput);
            btnRegister.setEnabled(isValid);
            btnRegister.setAlpha(isValid ? 1.0f : 0.5f);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            emailLayout.setError(null);  // Fehler zurücksetzen, wenn der Benutzer die Eingabe ändert
            passwordLayout.setError(null);

            LinearLayout registerLayout = findViewById(R.id.register_layout);
            registerLayout.invalidate();
        }
    };

    /**
     * Überprüft, ob die eingegebene E-Mail-Adresse ein gültiges Format hat.
     * @param email Die zu überprüfende E-Mail-Adresse.
     * @return true, wenn die E-Mail gültig ist, sonst false.
     */
    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Überprüft, ob das eingegebene Passwort gültig ist.
     * Die Mindestanforderung ist eine Länge von 6 Zeichen.
     * @param password Das zu überprüfende Passwort.
     * @return true, wenn das Passwort gültig ist, sonst false.
     */
    private boolean isValidPassword(String password) {
        return password.length() >= 6;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // super.onBackPressed(); verhindert, dass die App durch Zurück-Drücken geschlossen wird
    }
}