package com.example.whatspoppin.ui.mapview;

import com.example.whatspoppin.model.Event;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Event>> eventsArrayList;
    private MutableLiveData<ArrayList<Event>> userBookmarkEvents;
    private MutableLiveData<ArrayList<String>> userPreferenceCategory;

    public MapViewViewModel() {
        eventsArrayList = new MutableLiveData<ArrayList<Event>>();

    }

    public LiveData<ArrayList<Event>> getEventsList() {
        return eventsArrayList;
    }

    public LiveData<ArrayList<Event>> getUserBookmarkEvents() {
        return userBookmarkEvents;
    }
}