package com.app.booking.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Tour implements Parcelable {
    private String id;

    private String title;
    private Float  rating;
    private String description;

    private String imageUrl;

    private String location;
    private double latitude;
    private double longitude;

    // Leerer Konstruktor erforderlich für Firestore
    public Tour() {}

    public Tour(String id, String title, Float  rating, String description, String imageUrl, String location, double latitude, double longitude) {
        this.id = id;

        this.title = title;
        this.rating = rating;
        this.description = description;

        this.imageUrl = imageUrl;

        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }







    protected Tour(Parcel in) {
        id = in.readString();
        title = in.readString();
        rating = in.readFloat();
        description = in.readString();
        imageUrl = in.readString();
        location = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeFloat(rating);
        dest.writeString(description);
        dest.writeString(imageUrl);
        dest.writeString(location);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Tour> CREATOR = new Creator<Tour>() {
        @Override
        public Tour createFromParcel(Parcel in) {
            return new Tour(in);
        }

        @Override
        public Tour[] newArray(int size) {
            return new Tour[size];
        }
    };


}