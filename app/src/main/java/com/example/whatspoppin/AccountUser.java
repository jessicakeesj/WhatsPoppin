package com.example.whatspoppin;

import java.util.ArrayList;

public class AccountUser {
    private String userId;
    private String userEmail;
    private ArrayList<Event> bookmarks = new ArrayList<>();

    public AccountUser(){

    }

    public AccountUser(String userId, String userEmail) {
        this.userId = userId;
        this.userEmail = userEmail;
    }

    public AccountUser(String userId, String userEmail, ArrayList<Event> bookmarks) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.bookmarks = bookmarks;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public ArrayList<Event> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(ArrayList<Event> bookmarks) {
        this.bookmarks = bookmarks;
    }
}
