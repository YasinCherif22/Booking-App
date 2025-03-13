package com.app.booking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.booking.model.Tour;

import java.util.ArrayList;
import java.util.List;

/**
 * Die LocalDatabaseHelper-Klasse verwaltet die lokale SQLite-Datenbank für Favoriten.
 * Sie ermöglicht das Hinzufügen, Entfernen und Abrufen von favorisierten Touren.
 */
public class LocalDatabaseHelper extends SQLiteOpenHelper {

    // Datenbank-Name und -Version
    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 2;  // Erhöhen, wenn Schema geändert wird

    // Tabellen- und Spaltennamen
    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_FIREBASE_ID = "firebaseId";  // Eindeutige ID aus Firebase
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_LOCATION = "location";
    private static final String COLUMN_IMAGE_URL = "imageUrl";

    /**
     * Konstruktor für die Datenbankverwaltung.
     * @param context Der Kontext der Anwendung.
     */
    public LocalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Erstellt die Favoriten-Tabelle in der SQLite-Datenbank.
     * @param db Die SQLite-Datenbankinstanz.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FIREBASE_ID + " TEXT UNIQUE, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_LOCATION + " TEXT, "
                + COLUMN_IMAGE_URL + " TEXT, "
                + COLUMN_RATING + " FLOAT)";
        db.execSQL(CREATE_FAVORITES_TABLE);
    }

    /**
     * Aktualisiert die Datenbank, wenn die Version geändert wird.
     * @param db Die SQLite-Datenbankinstanz.
     * @param oldVersion Die alte Version der Datenbank.
     * @param newVersion Die neue Version der Datenbank.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Tabelle löschen und neu erstellen (für einfache Schemaänderungen)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    /**
     * Fügt eine Tour zu den Favoriten hinzu.
     * @param tour Die Tour, die als Favorit gespeichert werden soll.
     */
    public void addFavorite(Tour tour) {
        if (isFavorite(tour.getId())) {
            return; // Tour ist bereits als Favorit gespeichert
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIREBASE_ID, tour.getId());
        values.put(COLUMN_TITLE, tour.getTitle());
        values.put(COLUMN_RATING, tour.getRating());
        values.put(COLUMN_DESCRIPTION, tour.getDescription());
        values.put(COLUMN_LOCATION, tour.getLocation());
        values.put(COLUMN_IMAGE_URL, tour.getImageUrl());

        db.insert(TABLE_FAVORITES, null, values);
        db.close();
    }

    /**
     * Entfernt eine Tour aus den Favoriten.
     * @param firebaseId Die ID der Tour aus Firebase.
     */
    public void removeFavorite(String firebaseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, COLUMN_FIREBASE_ID + "=?", new String[]{firebaseId});
        db.close();
    }

    /**
     * Überprüft, ob eine Tour als Favorit gespeichert ist.
     * @param firebaseId Die ID der Tour aus Firebase.
     * @return true, wenn die Tour ein Favorit ist, sonst false.
     */
    public boolean isFavorite(String firebaseId) {
        if (firebaseId == null) {
            return false;  // Verhindert eine Abfrage mit null-Wert
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES, null, COLUMN_FIREBASE_ID + "=?", new String[]{firebaseId}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    /**
     * Ruft alle favorisierten Touren aus der Datenbank ab.
     * @return Eine Liste mit allen gespeicherten Favoriten.
     */
    public List<Tour> getFavoriteTours() {
        List<Tour> tourList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_FAVORITES, new String[]{
                        COLUMN_FIREBASE_ID,
                        COLUMN_TITLE,
                        COLUMN_RATING,
                        COLUMN_DESCRIPTION,
                        COLUMN_LOCATION,
                        COLUMN_IMAGE_URL},
                null, null, null, null, COLUMN_TITLE + " ASC"); // Sortiert nach Titel

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Tour tour = new Tour();
                tour.setId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIREBASE_ID)));
                tour.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)));
                tour.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow(COLUMN_RATING)));
                tour.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                tour.setLocation(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION)));
                tour.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
                tourList.add(tour);
            }
            cursor.close();
        }

        db.close();
        return tourList;
    }
}