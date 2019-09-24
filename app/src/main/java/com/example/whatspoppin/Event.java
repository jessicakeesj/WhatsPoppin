package com.example.whatspoppin;

public class Event {
    private String eventName;
    private String eventAddress;
    private String eventCategory;
    private String eventDescription;
    private String event_datetime_start;
    private String event_datetime_end;
    private String eventUrl;
    private String eventImageUrl;
    private String eventLongitude;
    private String eventLatitude;
    private String eventLocationSummary;
    private String eventSource;

    private String eventDate;

    public Event(){

    }

    public Event(String eventName, String eventDate) {
        this.eventName = eventName;
        this.eventDate = eventDate;
    }

    public Event(String eventName, String eventAddress, String eventCategory,
                 String eventDescription, String event_datetime_start, String event_datetime_end,
                 String eventUrl, String eventImageUrl, String eventLongitude, String eventLatitude,
                 String eventLocationSummary, String eventSource) {
        this.eventName = eventName;
        this.eventAddress = eventAddress;
        this.eventCategory = eventCategory;
        this.eventDescription = eventDescription;
        this.event_datetime_start = event_datetime_start;
        this.event_datetime_end = event_datetime_end;
        this.eventUrl = eventUrl;
        this.eventImageUrl = eventImageUrl;
        this.eventLongitude = eventLongitude;
        this.eventLatitude = eventLatitude;
        this.eventLocationSummary = eventLocationSummary;
        this.eventSource = eventSource;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public String getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(String eventCategory) {
        this.eventCategory = eventCategory;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEvent_datetime_start() {
        return event_datetime_start;
    }

    public void setEvent_datetime_start(String event_datetime_start) {
        this.event_datetime_start = event_datetime_start;
    }

    public String getEvent_datetime_end() {
        return event_datetime_end;
    }

    public void setEvent_datetime_end(String event_datetime_end) {
        this.event_datetime_end = event_datetime_end;
    }

    public String getEventUrl() {
        return eventUrl;
    }

    public void setEventUrl(String eventUrl) {
        this.eventUrl = eventUrl;
    }

    public String getEventImageUrl() {
        return eventImageUrl;
    }

    public void setEventImageUrl(String eventImageUrl) {
        this.eventImageUrl = eventImageUrl;
    }

    public String getEventLongitude() {
        return eventLongitude;
    }

    public void setEventLongitude(String eventLongitude) {
        this.eventLongitude = eventLongitude;
    }

    public String getEventLatitude() {
        return eventLatitude;
    }

    public void setEventLatitude(String eventLatitude) {
        this.eventLatitude = eventLatitude;
    }

    public String getEventLocationSummary() {
        return eventLocationSummary;
    }

    public void setEventLocationSummary(String eventLocationSummary) {
        this.eventLocationSummary = eventLocationSummary;
    }

    public String getEventSource() {
        return eventSource;
    }

    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }
}
