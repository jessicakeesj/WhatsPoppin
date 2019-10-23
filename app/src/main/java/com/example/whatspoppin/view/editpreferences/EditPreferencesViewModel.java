package com.example.whatspoppin.view.editpreferences;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditPreferencesViewModel extends ViewModel {

    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private DocumentReference usersDoc;

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<String>> eventCategories;
    private MutableLiveData<String> categoriesSelected;
    private boolean receiveNotification = false;
    private boolean showNearbyEvents = false;

    public EditPreferencesViewModel() {
        // get current login user
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }
    }

    public LiveData<ArrayList<String>> getEventCategories() {
        if (eventCategories == null) {
            eventCategories = new MutableLiveData<ArrayList<String>>();
            getFireStoreEventsData();
        }
        return eventCategories;
    }
    public LiveData<String> getSelectedCategories() {
        if (categoriesSelected == null) {
            categoriesSelected = new MutableLiveData<String>();
            getFireStoreUserData();
        }
        return categoriesSelected;
    }

    private void getFireStoreUserData() { // get user preferences settings
        usersDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // get user selected categories
                        String b = String.valueOf(document.get("interests"));
                        if (b != "null" || b != null || b != "[]") {
                            ArrayList<String> bkm = (ArrayList<String>) document.get("interests");
                            for (String categoryName : bkm)
                                categoriesSelected.setValue(categoryName);
                        } else categoriesSelected = null;
                        receiveNotification = document.getBoolean("receiveNotification");
                        showNearbyEvents = document.getBoolean("showNearbyEvents");

                    } else {
                        Log.d("getUserDetails", "No such document");
                    }
                } else {
                    Log.d("getUserDetails", "get failed with ", task.getException());
                }
            }
        });
    }

    private void getFireStoreEventsData() {
        // get list of all events
        final ArrayList<String> eventCategory = new ArrayList<>();
        db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document != null) {
                            String category = document.getString("category");
                            if (!eventCategory.contains(category)) {
                                eventCategory.add(category);
                            }
                        } else {
                            Log.d("", "No such document");
                        }
                        Log.d("", document.getId() + " => " + document.getData());
                    }
                    eventCategories.setValue(eventCategory);
                } else {
                    Log.w("EventListFirestore", "Error getting documents.", task.getException());
                }
            }
        });
    }
}