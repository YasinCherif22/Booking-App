package com.app.booking.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.app.booking.R;
import com.app.booking.activity.AccountActivity;
import com.app.booking.activity.LoginActivity;
import com.app.booking.activity.SettingsActivity;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Das ProfileFragment zeigt Benutzerdetails an und ermöglicht den Zugriff auf Konto- und Einstellungen.
 * Je nach Anmeldestatus werden unterschiedliche UI-Elemente angezeigt.
 */
public class ProfileFragment extends Fragment {

    // Firebase-Authentifizierung
    private FirebaseAuth auth;

    // UI-Elemente
    private TextView userName, userDetails;
    private RelativeLayout userProfileLabel, userLoginLabel, accountOption, settingsOption;
    private Button loginButton;

    /**
     * Wird aufgerufen, wenn die View für das Fragment erstellt wird.
     * Initialisiert die UI-Elemente und setzt Klick-Listener für Navigationen.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Layout für das Fragment inflaten
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Firebase Auth-Instanz initialisieren
        auth = FirebaseAuth.getInstance();

        // UI-Elemente referenzieren
        userName = view.findViewById(R.id.profile_name);
        userDetails = view.findViewById(R.id.profile_details);
        userProfileLabel = view.findViewById(R.id.userProfile);
        userLoginLabel = view.findViewById(R.id.userLogin);
        accountOption = view.findViewById(R.id.account);
        settingsOption = view.findViewById(R.id.settings);
        loginButton = view.findViewById(R.id.btn_login);

        // Klick-Listener für den Anmelde-Button
        loginButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));

        // Klick-Listener für die Konto-Einstellungen
        accountOption.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AccountActivity.class);
            startActivity(intent);
        });

        // Klick-Listener für die allgemeinen Einstellungen
        settingsOption.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        return view;
    }

    /**
     * Wird aufgerufen, wenn das Fragment wieder sichtbar wird.
     * Aktualisiert die Benutzerdaten in der UI.
     */
    @Override
    public void onResume() {
        super.onResume();
        updateUserInfo();
    }

    /**
     * Aktualisiert die Benutzerdaten basierend auf dem Anmeldestatus.
     * Zeigt oder versteckt entsprechende UI-Elemente.
     */
    private void updateUserInfo() {
        if (auth.getCurrentUser() != null) {
            // Benutzer ist angemeldet, Details anzeigen
            userName.setText(auth.getCurrentUser().getDisplayName() != null ?
                    auth.getCurrentUser().getDisplayName() : "Benutzername nicht verfügbar");

            userDetails.setText(auth.getCurrentUser().getEmail() != null ?
                    auth.getCurrentUser().getEmail() : "E-Mail nicht verfügbar");

            // Konto- und Profildaten sichtbar machen
            accountOption.setVisibility(View.VISIBLE);
            userProfileLabel.setVisibility(View.VISIBLE);
            userLoginLabel.setVisibility(View.GONE);
        } else {
            // Benutzer ist nicht angemeldet, Login-Optionen anzeigen
            accountOption.setVisibility(View.GONE);
            userProfileLabel.setVisibility(View.GONE);
            userLoginLabel.setVisibility(View.VISIBLE);
        }
    }
}