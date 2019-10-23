package com.example.whatspoppin.viewmodel;

import android.util.Log;
import android.widget.ListView;
import com.example.whatspoppin.adapter.EventAdapter;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EventListViewModel extends ViewModel {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private ArrayList<Event> events = new ArrayList<Event>();
    private ArrayList<Event> bookmarks = new ArrayList<Event>();
    private MutableLiveData<ArrayList<Event>> eventArrayList;
    private MutableLiveData<ArrayList<Event>> bookmarkArrayList;
    private DocumentReference usersDoc;
    private FirebaseUser currentUser;

    public EventListViewModel() {
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
                    getFireStoreEventsData();
                    getFireStoreBookmarksData();
                } else {
                    getFireStoreEventsData();
                    getFireStoreBookmarksData();
                }
            }
        });
    }

    private void getFireStoreEventsData() {
        events.clear();
        // get list of all events
        final ArrayList<String> eventCategory = new ArrayList<>();
        db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    events.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document != null) {
                            String name = document.getId();
                            String address = document.getString("address");
                            String category = document.getString("category");
                            String description = document.getString("description");
                            String datetime_start = document.getString("datetime_start");
                            String datetime_end = document.getString("datetime_end");
                            String url = document.getString("url");
                            String imageUrl = document.getString("imageUrl");
                            String lng = document.get("lng") == null ? "null" : document.get("lng").toString();
                            String lat = document.get("lat") == null ? "null" : document.get("lat").toString();
                            String location_summary = document.getString("location_summary");
                            String source = document.getString("source");

                            Event event = new Event(name, address, category, description, datetime_start, datetime_end, url,
                                    imageUrl, lng, lat, location_summary, source);
                            events.add(event);
                        } else {
                            events = null;
                            Log.d("", "No such document");
                        }
                        Log.d("", document.getId() + " => " + document.getData());
                    }
                    eventArrayList.setValue(events);
                } else {
                    Log.w("EventListFirestore", "Error getting documents.", task.getException());
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