package com.exuberant.bluraven.models;

import java.util.List;

public class Comment {

    String commentId;
    String userId;
    String text;
    String date;
    String time;
    List<String> images;

    public Comment() {
    }

    public Comment(String commentId, String userId, String text, String date, String time, List<String> images) {
        this.commentId = commentId;
        this.userId = userId;
        this.text = text;
        this.date = date;
        this.time = time;
        this.images = images;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
