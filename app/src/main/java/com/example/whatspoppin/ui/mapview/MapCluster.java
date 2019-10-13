package com.example.whatspoppin.ui.mapview;

import android.graphics.drawable.Drawable;

import com.example.whatspoppin.Event;
import com.example.whatspoppin.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MapCluster implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private Event event;
    private EventType eventType;
    private int markerDrawable;
    enum EventType {All, Bookmark, Recommend}

    public MapCluster(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public MapCluster(String title, double lat, double lng, String snippet, Event e, EventType et) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        event = e;
        eventType = et;
        if (et == EventType.Bookmark) {
            markerDrawable = R.drawable.mm_bookmark;
        } else if (et == EventType.Recommend) { // recommended event markers
            markerDrawable = R.drawable.mm_recommended;
        } else {
            markerDrawable = R.drawable.mm_normal;
        }
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }


    public Event getEvent() {
        return event;
    }

    public int getMarkerDrawable() {
        return markerDrawable;
    }

}