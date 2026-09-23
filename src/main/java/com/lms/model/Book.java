package com.lms.model;

import java.sql.Timestamp;

public class Book {
    private int id;
    private String title;
    private String author;
    private String isbn;
    private String status;
    private Timestamp addedAt;

    public Book() {}

    public Book(int id, String title, String author, String isbn, String status, Timestamp addedAt) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = status;
        this.addedAt = addedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getAddedAt() { return addedAt; }
    public void setAddedAt(Timestamp addedAt) { this.addedAt = addedAt; }
    
    @Override
    public String toString() {
        return title + " by " + author;
    }
}
