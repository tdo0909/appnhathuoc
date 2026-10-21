package com.example.appnhathuoc.model;

import java.io.Serializable;

public class ReviewItem implements Serializable {
    private int id;
    private String userName;
    private int rating;
    private String comment;
    private String date;

    public ReviewItem() {}

    public ReviewItem(int id, String userName, int rating, String comment, String date) {
        this.id = id;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    public int getId() { return id; }
    public String getUserName() { return userName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getDate() { return date; }
}