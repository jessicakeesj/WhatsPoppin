package com.example.whatspoppin.viewmodel;

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
    private DocumentReference usersDoc;

    private MutableLiveData<ArrayList<String>> eventCategories = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> categoriesSelected = new MutableLiveData<>();
    private MutableLiveData<Boolean> receiveNotification = new MutableLiveData<>();
    private MutableLiveData<Boolean> showNearbyEvents = new MutableLiveData<>();

    public EditPreferencesViewModel() {
        // get current login user
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null)
            usersDoc = db.collection("users").document(currentUser.getUid());
        getFireStoreEventsData();
        getFireStoreUserData();
    }

    public LiveData<ArrayList<String>> getEventCategories() {
        return eventCategories;
    }

    public LiveData<ArrayList<String>> getSelectedCategories() {
        return categoriesSelected;
    }

    public LiveData<Boolean> getReceiveNotification() {
        return receiveNotification;
    }

    public LiveData<Boolean> getShowNearbyEvents() {
        return showNearbyEvents;
    }

    private void getFireStoreUserData() { // get user preferences settings
        final ArrayList<String> selectedCategories = new ArrayList<>();
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
                                selectedCategories.add(categoryName);
                        }
                        categoriesSelected.setValue(selectedCategories);
                        receiveNotification.setValue((boolean) document.getBoolean("receiveNotification"));
                        showNearbyEvents.setValue((boolean) document.getBoolean("showNearbyEvents"));
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
                            if (!eventCategory.contains(category)) eventCategory.add(category);
                        } else
                            Log.d("", "No such document");
                        Log.d("", document.getId() + " => " + document.getData());
                    }
                    eventCategories.setValue(eventCategory);
                } else
                    Log.w("EventListFirestore", "Error getting documents.", task.getException());
            }
        });
    }

    public void updateCategoryFireStore(ArrayList<String> categories_Selected) {
        categoriesSelected.setValue(categories_Selected);
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

    public void updateReceiveNotificationFireStore(boolean value) {
        receiveNotification.setValue(value);
        usersDoc.update("receiveNotification", receiveNotification.getValue())
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

    public void updateShowNearbyEventFireStore(boolean value) {
        showNearbyEvents.setValue(value);
        usersDoc.update("showNearbyEvents", showNearbyEvents.getValue())
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

//    checks firestore for realtime updates
//    public void realtimeFireStoreData() {
//        usersDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("Listen", "Listen failed.", e);
//                    return;
//                }
//                if (snapshot != null && snapshot.exists()) {
//                    getEventCategories();
//                    getPreferenceFirestore();
//                } else {
//                    getEventCategories();
//                    getPreferenceFirestore();
//                }
//            }
//        });
//    }
}