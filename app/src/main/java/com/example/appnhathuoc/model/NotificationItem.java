package com.example.appnhathuoc.model;

import java.io.Serializable;

public class NotificationItem implements Serializable {
    private String id;
    private String title;
    private String message;
    private String time;
    private String type; // "PROMOTION", "ORDER", "SYSTEM"
    private boolean isRead;
    private String imageUrl;

    public NotificationItem() {}

    public NotificationItem(String id, String title, String message, String time, String type, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.time = time;
        this.type = type;
        this.isRead = isRead;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public String getType() { return type; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}