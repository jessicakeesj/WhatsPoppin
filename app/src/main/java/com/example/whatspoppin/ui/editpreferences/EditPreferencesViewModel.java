package com.example.whatspoppin.ui.editpreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditPreferencesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EditPreferencesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is share fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}