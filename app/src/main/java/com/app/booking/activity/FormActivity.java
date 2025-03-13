package com.app.booking.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.app.booking.R;
import com.app.booking.model.Booking;
import com.app.booking.model.Tour;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Die FormActivity ermöglicht es dem Benutzer, eine Tour-Buchung vorzunehmen.
 * Die Buchung wird in der Firebase Firestore-Datenbank gespeichert.
 */
public class FormActivity extends AppCompatActivity {

    // Firebase-Instanzen für die Authentifizierung und Datenbank
    private FirebaseFirestore db;
    private FirebaseUser user;

    // UI-Elemente für Benutzereingaben
    private TextInputEditText tvFirstName, tvSecondName, tvMemberAmount, tvDate, tvContactInformation, tvRequirements;
    private Button saveButton;

    // Kalenderinstanz für das Datumsauswahl-Dialogfeld
    private Calendar calendar;

    /**
     * Wird aufgerufen, wenn die Aktivität erstellt wird.
     * Initialisiert die UI-Elemente, Firebase-Instanzen und setzt die Event-Listener.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Edge-to-Edge Modus aktivieren
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // Navigationsleiste farblich anpassen
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color60));

        // Firebase-Instanzen initialisieren
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // UI-Elemente aus dem Layout referenzieren
        tvFirstName = findViewById(R.id.et_name_first);
        tvSecondName = findViewById(R.id.et_name_second);
        tvMemberAmount = findViewById(R.id.et_member_amount);
        tvDate = findViewById(R.id.et_date);
        tvContactInformation = findViewById(R.id.et_contact_information);
        tvRequirements = findViewById(R.id.et_requirements);

        // Tour-Objekt aus der vorherigen Aktivität abrufen
        Tour tour = getIntent().getParcelableExtra("tour");

        // Kalenderinstanz initialisieren
        calendar = Calendar.getInstance();

        // Klick-Listener für das Datumseingabefeld
        tvDate.setOnClickListener(v -> showDatePickerDialog());

        // Klick-Listener für den Speichern-Button
        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> saveBookingInfo(tour));

        // Klick-Listener für die Zurück-Schaltfläche
        ImageButton backBtn = findViewById(R.id.backButton);
        backBtn.setOnClickListener(v -> finish());
    }

    /**
     * Zeigt einen DatePickerDialog an, um ein Buchungsdatum auszuwählen.
     */
    private void showDatePickerDialog() {

        int theme = R.style.DatePickerTheme;

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                theme,
                (view, year, month, dayOfMonth) -> {
                    // Gewähltes Datum in die Kalenderinstanz setzen
                    calendar.set(year, month, dayOfMonth);
                    // Datum formatieren und ins Textfeld setzen
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    tvDate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Mindestdatum auf das heutige Datum setzen (Vergangene Daten deaktivieren)
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.show();
    }

    /**
     * Speichert die Buchungsinformationen in der Firestore-Datenbank.
     * @param tour Das Tour-Objekt, das der Benutzer bucht.
     */
    private void saveBookingInfo(Tour tour) {
        // Speichern-Button deaktivieren, um Mehrfachklicks zu vermeiden
        saveButton.setEnabled(false);
        saveButton.setAlpha(0.5f);

        // Zufällige Buchungs-ID generieren
        String bookingID = UUID.randomUUID().toString();

        // Benutzereingaben abrufen und sicherstellen, dass sie nicht null sind
        String firstName = Objects.requireNonNull(tvFirstName.getText()).toString().trim();
        String secondName = Objects.requireNonNull(tvSecondName.getText()).toString().trim();
        String memberAmount = Objects.requireNonNull(tvMemberAmount.getText()).toString().trim();
        String date = Objects.requireNonNull(tvDate.getText()).toString().trim();
        String contactInformation = Objects.requireNonNull(tvContactInformation.getText()).toString().trim();
        String requirements = Objects.requireNonNull(tvRequirements.getText()).toString().trim();

        // Falls keine speziellen Anforderungen eingegeben wurden, Standardwert setzen
        if (requirements.isEmpty()) {
            requirements = "Keine";
        }

        // Überprüfung, ob alle erforderlichen Felder ausgefüllt sind
        if (firstName.isEmpty() || secondName.isEmpty() || memberAmount.isEmpty() || date.isEmpty() || contactInformation.isEmpty()) {
            Toast.makeText(this, "Alle Eingaben sind erforderlich!", Toast.LENGTH_SHORT).show();
            saveButton.setEnabled(true);
            saveButton.setAlpha(1f);
            return;
        }

        // Sicherstellen, dass die Anzahl der Mitglieder eine gültige Zahl ist
        int members;
        try {
            members = Integer.parseInt(memberAmount);
            if (members <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Bitte eine gültige Anzahl an Personen eingeben!", Toast.LENGTH_SHORT).show();
            saveButton.setEnabled(true);
            saveButton.setAlpha(1f);
            return;
        }

        // Buchungsobjekt erstellen
        Booking booking = new Booking(bookingID, tour.getId(), user.getUid(), firstName, secondName, members, date, contactInformation, requirements);

        // Buchung in Firestore speichern
        db.collection("bookings").document(bookingID)
                .set(booking)
                .addOnSuccessListener(s -> {
                    // Erfolgsmeldung anzeigen und zur Bestätigungsseite navigieren
                    Toast.makeText(this, "Tour erfolgreich gebucht!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FormActivity.this, ConfirmationActivity.class));
                    finish(); // Schließt die aktuelle Aktivität
                })
                .addOnFailureListener(e -> {
                    // Falls das Speichern fehlschlägt, Fehlermeldung anzeigen und Button wieder aktivieren
                    Toast.makeText(this, "Fehler: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    saveButton.setEnabled(true);
                    saveButton.setAlpha(1f);
                });
    }
}