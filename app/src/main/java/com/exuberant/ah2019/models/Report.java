package com.exuberant.ah2019.models;

import java.util.List;

public class Report {

    String reportId;
    String userId;
    String authorName;
    String parentId;
    double latitude;
    double longitude;
    String postalCode;
    String reportType;
    String title;
    String description;
    String date;
    String time;
    List<String> images;
    List<String> upVotes;
    List<String> downVotes;
    boolean status;

    public Report() {
    }

    public Report(String reportId, String userId, String authorName, String parentId, double latitude, double longitude, String postalCode, String reportType, String title, String description, String date, String time, List<String> images, List<String> upVotes, List<String> downVotes, boolean status) {
        this.reportId = reportId;
        this.userId = userId;
        this.authorName = authorName;
        this.parentId = parentId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postalCode = postalCode;
        this.reportType = reportType;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.images = images;
        this.upVotes = upVotes;
        this.downVotes = downVotes;
        this.status = status;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getUpVotes() {
        return upVotes;
    }

    public void setUpVotes(List<String> upVotes) {
        this.upVotes = upVotes;
    }

    public List<String> getDownVotes() {
        return downVotes;
    }

    public void setDownVotes(List<String> downVotes) {
        this.downVotes = downVotes;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
