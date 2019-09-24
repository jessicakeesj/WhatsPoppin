package com.example.whatspoppin.ui.eventlist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class EventDetailsViewModel {
    private MutableLiveData<String> mText;

    public EventDetailsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
