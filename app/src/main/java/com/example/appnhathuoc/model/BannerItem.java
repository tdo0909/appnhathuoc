package com.example.appnhathuoc.model;

public class BannerItem {
    private String id;
    private String title;
    private String subtitle;
    private String actionText;
    private int backgroundResId;
    private String imageUrl;

    public BannerItem() {}

    public BannerItem(String id, String title, String subtitle, String actionText, int backgroundResId) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.actionText = actionText;
        this.backgroundResId = backgroundResId;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getActionText() { return actionText; }
    public int getBackgroundResId() { return backgroundResId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}