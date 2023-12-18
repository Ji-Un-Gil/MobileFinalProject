package com.example.application;

public class Post {
    String id;
    String title;
    String publishedDate;

    public Post(String id, String title, String publishedDate) {
        this.id = id;
        this.title = title;
        this.publishedDate = publishedDate;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getPublishedDate() {
        return publishedDate;
    }
}
