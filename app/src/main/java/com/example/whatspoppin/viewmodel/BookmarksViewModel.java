package com.example.whatspoppin.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.whatspoppin.model.Event;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.util.ArrayList;
import java.util.HashMap;

public class BookmarksViewModel extends ViewModel {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private ArrayList<Event> events = new ArrayList<Event>();
    private ArrayList<Event> bookmarks = new ArrayList<Event>();
    private MutableLiveData<ArrayList<Event>> eventArrayList;
    private MutableLiveData<ArrayList<Event>> bookmarkArrayList;
    private DocumentReference usersDoc;
    private FirebaseUser currentUser;

    public BookmarksViewModel() {
        // get current login user
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }
    }

    public LiveData<ArrayList<Event>> getEventList() {
        if (eventArrayList == null) {
            eventArrayList = new MutableLiveData<ArrayList<Event>>();
            realtimeFireStoreData();
        }
        return eventArrayList;
    }
    public LiveData<ArrayList<Event>> getBookmarkList() {
        if (bookmarkArrayList == null) {
            bookmarkArrayList = new MutableLiveData<ArrayList<Event>>();
            realtimeFireStoreData();
        }
        return bookmarkArrayList;
    }

    public void realtimeFireStoreData() {
        usersDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listen", "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    getFireStoreBookmarksData();
                } else {
                    getFireStoreBookmarksData();
                }
            }
        });
    }

    private void getFireStoreBookmarksData() {
        bookmarks.clear();
        usersDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        bookmarks.clear();
                        String email = document.getString("userEmail");
                        String b = String.valueOf(document.get("bookmarks"));
                        if(b != "null" || b != null || b != "[]"){
                            ArrayList<HashMap<String,String>> bkm= (ArrayList<HashMap<String,String>>) document.get("bookmarks");
                            for(HashMap<String,String> testMap : bkm){
                                String name = testMap.get("eventName");
                                String address = testMap.get("eventAddress");
                                String category = testMap.get("eventCategory");
                                String description = testMap.get("eventDescription");
                                String datetime_start = testMap.get("event_datetime_start");
                                String datetime_end = testMap.get("event_datetime_end");
                                String url = testMap.get("eventUrl");
                                String imageUrl = testMap.get("eventImageUrl");
                                String lng = testMap.get("eventLongtitude") == null ? "null" : testMap.get("eventLongtitude").toString();
                                String lat = testMap.get("eventLatitude") == null ? "null" : testMap.get("eventLatitude").toString();
                                String location_summary = testMap.get("eventLocationSummary");
                                String source = testMap.get("eventSource");

                                Event event = new Event(name, address, category, description, datetime_start, datetime_end, url,
                                        imageUrl, lng, lat, location_summary, source);
                                bookmarks.add(event);
                            }
                        }else{
                            bookmarks = null;
                        }
                        Log.d("getBookmarks", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("getBookmarks", "No such document");
                    }
                    bookmarkArrayList.setValue(bookmarks);
                } else {
                    Log.d("getBookmarks", "get failed with ", task.getException());
                }
            }
        });
    }
}
