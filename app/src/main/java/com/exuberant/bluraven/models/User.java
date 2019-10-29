package com.exuberant.bluraven.models;

import java.util.List;

public class User {

    String displayName;
    String userId;
    String email;
    String photoUrl;
    List<String> reports;
    String postalCode;
    String locality;
    double userRating;

    public User() {
    }

    public User(String displayName, String userId, String email, String photoUrl, List<String> reports, String postalCode, String locality, double userRating) {
        this.displayName = displayName;
        this.userId = userId;
        this.email = email;
        this.photoUrl = photoUrl;
        this.reports = reports;
        this.postalCode = postalCode;
        this.locality = locality;
        this.userRating = userRating;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<String> getReports() {
        return reports;
    }

    public void setReports(List<String> reports) {
        this.reports = reports;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }
}
