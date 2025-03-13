package com.app.booking.model;


public class Booking {
    private String id;

    private String tourId;
    private String userId;

    private String firstName;
    private String secondName;
    private Integer memberAmount;
    private String date;
    private String contactInformation;
    private String requirements;

    public Booking() {}

    public Booking(String id, String tourId, String userId, String firstName, String secondName, Integer memberAmount, String date, String contactInformation, String requirements) {
        this.id = id;

        this.tourId = tourId;
        this.userId = userId;

        this.firstName = firstName;
        this.secondName = secondName;

        this.memberAmount = memberAmount;

        this.date = date;
        this.contactInformation = contactInformation;
        this.requirements = requirements;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public Integer getMemberAmount() {
        return memberAmount;
    }

    public void setMemberAmount(Integer memberAmount) {
        this.memberAmount = memberAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
}