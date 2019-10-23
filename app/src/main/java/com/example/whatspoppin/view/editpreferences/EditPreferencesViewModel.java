package com.example.whatspoppin.view.editpreferences;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EditPreferencesViewModel extends ViewModel {

    //firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private DocumentReference usersDoc;

    private MutableLiveData<ArrayList<String>> eventCategories;
    private ArrayList<String> categoriesSelected;
    private boolean receiveNotification = false;
    private boolean showNearbyEvents = false;

    public EditPreferencesViewModel() {
        // get current login user
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }
        getFireStoreEventsData();
        getFireStoreUserData();
    }

    public LiveData<ArrayList<String>> getEventCategories() {
        if (eventCategories == null) {
            eventCategories = new MutableLiveData<ArrayList<String>>();
            getFireStoreEventsData();
        }
        return eventCategories;
    }

    public ArrayList<String> getSelectedCategories() {
        if (categoriesSelected == null) {
            categoriesSelected = new ArrayList<String>();
            getFireStoreUserData();
        }
        return categoriesSelected;
    }

    public void unselectCategory(String categoryName) {
        categoriesSelected.remove(categoryName);
    }

    public void selectCategory(String categoryName) {
        categoriesSelected.add(categoryName);
    }

    public void setReceiveNotification(boolean value) {
        receiveNotification = value;
    }

    public void setShowNearbyEvents(boolean value) {
        showNearbyEvents = value;
    }

    public boolean getReceiveNotification() {
        return receiveNotification;
    }

    public boolean getShowNearbyEvents() {
        return showNearbyEvents;
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
                                categoriesSelected.add(categoryName);
                        } else categoriesSelected = null;
                        receiveNotification = document.getBoolean("receiveNotification");
                        showNearbyEvents = document.getBoolean("showNearbyEvents");

                    } else
                        Log.d("getUserDetails", "No such document");
                } else
                    Log.d("getUserDetails", "get failed with ", task.getException());
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

    public void updateCategoryFirestore(ArrayList<String> categories_Selected) {
        usersDoc.update("interests", categories_Selected)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("updatePreferences", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("updatePreferences", "Error updating document", e);
                    }
                });
    }

    public void updateReceiveNotificationFirestore(boolean receiveNotification) {
        usersDoc.update("receiveNotification", receiveNotification)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("updatePreferences", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("updatePreferences", "Error updating document", e);
                    }
                });
    }

    public void updateShowNearbyEventFirestore(boolean showNearbyEvents) {
        usersDoc.update("showNearbyEvents", showNearbyEvents)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("updatePreferences", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("updatePreferences", "Error updating document", e);
                    }
                });
    }
}