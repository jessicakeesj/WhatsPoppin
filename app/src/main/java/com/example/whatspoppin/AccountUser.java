package com.example.whatspoppin;

import java.util.ArrayList;

public class AccountUser {
    private String userId;
    private String userEmail;
    private ArrayList<Event> bookmarks = new ArrayList<Event>();
    private ArrayList<String> interests = new ArrayList<String>();
    private boolean receiveNotification;

    public AccountUser(){

    }

    public AccountUser(String userId, String userEmail, ArrayList<Event> bookmarks, ArrayList<String> interests,
                       boolean receiveNotification) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.bookmarks = bookmarks;
        this.interests = interests;
        this.receiveNotification = receiveNotification;
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

    public ArrayList<String> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public boolean getReceiveNotification() {
        return receiveNotification;
    }

    public void setReceiveNotification(boolean receiveNotification) {
        this.receiveNotification = receiveNotification;
    }
}
