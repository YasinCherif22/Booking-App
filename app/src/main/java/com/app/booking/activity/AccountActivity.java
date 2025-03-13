package com.app.booking.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.booking.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Die AccountActivity verwaltet Benutzerkontofunktionen wie Profilbearbeitung, Passwortänderung,
 * Abmeldung und Kontolöschung. Sie interagiert mit Firebase Authentication und Firestore.
 */
public class AccountActivity extends AppCompatActivity {

    // Firebase-Authentifizierung und Datenbank-Instanzen
    private FirebaseUser user;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // UI-Komponente für die Zurück-Schaltfläche
    private ImageButton backBtn;

    /**
     * Methode wird aufgerufen, wenn die Aktivität startet.
     * Holt den aktuellen angemeldeten Benutzer aus Firebase.
     */
    @Override
    public void onStart() {
        super.onStart();
        user = auth.getCurrentUser();
    }

    /**
     * Wird beim Erstellen der Aktivität aufgerufen.
     * Initialisiert Firebase-Instanzen, UI-Komponenten und setzt Klick-Listener.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Setzt die Farbe der Navigationsleiste
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color60));

        // Initialisieren von Firebase-Authentifizierung und Firestore-Datenbank
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialisieren der Zurück-Schaltfläche und Setzen eines Klick-Listeners
        backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(v -> finish());

        // Überprüft, ob ein Benutzer angemeldet ist. Falls nicht, schließt sich die Aktivität.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }

        // Klick-Listener für die Profilbearbeitung
        RelativeLayout editProfileOption = findViewById(R.id.edit_profile);
        editProfileOption.setOnClickListener(v -> {
            startActivity(new Intent(AccountActivity.this, EditProfileActivity.class));
        });

        // Klick-Listener für Passwortänderung
        RelativeLayout changePasswordOption = findViewById(R.id.change_password);
        changePasswordOption.setOnClickListener(v -> {
            startActivity(new Intent(AccountActivity.this, ChangePasswordActivity.class));
        });

        // Klick-Listener für die Abmeldung
        RelativeLayout logoutOption = findViewById(R.id.logout_option);
        logoutOption.setOnClickListener(v -> showLogoutConfirmationDialog());

        // Klick-Listener für die Konto-Löschung
        RelativeLayout deleteAccountOption = findViewById(R.id.delete_option);
        deleteAccountOption.setOnClickListener(v -> showDeleteAccountConfirmationDialog());
    }

    /**
     * Zeigt einen Bestätigungsdialog zur Abmeldung des Benutzers.
     * Bei Bestätigung wird der Benutzer abgemeldet und zur Login-Aktivität weitergeleitet.
     */
    private void showLogoutConfirmationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialog)
                .setTitle(R.string.logout)
                .setMessage(R.string.logout_message)
                .setPositiveButton(R.string.logout, (dialogInterface, which) -> {
                    // Benutzer abmelden
                    FirebaseAuth.getInstance().signOut();
                    // Zur Login-Aktivität wechseln
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show(); // Dialog anzeigen

        // Setzt Farben für die Schaltflächen im Dialog
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.accentBlue));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.textColor));
    }

    /**
     * Zeigt einen Bestätigungsdialog für die Konto-Löschung.
     * Falls bestätigt, wird die Methode deleteAccount() aufgerufen.
     */
    private void showDeleteAccountConfirmationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this, R.style.CustomDialog)
                .setTitle(R.string.delete_account)
                .setMessage(R.string.delete_account_message)
                .setPositiveButton(R.string.delete_account, (dialogInterface, which) -> {
                    deleteAccount();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();

        // Setzt Farben für die Schaltflächen im Dialog
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.accentBlue));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.accentRed));
    }

    /**
     * Löscht das Konto des aktuellen Benutzers.
     * Falls erfolgreich, wird der Benutzer zur Login-Aktivität weitergeleitet.
     * Falls fehlgeschlagen, wird eine Fehlermeldung angezeigt.
     */
    private void deleteAccount() {
        if (user != null) {
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Account und Daten erfolgreich gelöscht", Toast.LENGTH_SHORT).show();
                    // Nach der Konto-Löschung zur Login-Aktivität wechseln
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Konto konnte nicht gelöscht werden", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}