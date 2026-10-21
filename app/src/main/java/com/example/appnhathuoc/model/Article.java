package com.example.appnhathuoc.model;

import java.io.Serializable;

public class Article implements Serializable {
    private String id;
    private String title;
    private String summary;
    private String content;
    private String author;
    private String date;
    private int imageResId;
    private String imageUrl;

    public Article() {}

    public Article(String id, String title, String summary, String content, String author, String date, int imageResId) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.author = author;
        this.date = date;
        this.imageResId = imageResId;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public String getDate() { return date; }
    public int getImageResId() { return imageResId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}