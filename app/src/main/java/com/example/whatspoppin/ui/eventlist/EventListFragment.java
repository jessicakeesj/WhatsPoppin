package com.example.whatspoppin.ui.eventlist;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.example.whatspoppin.model.Event;
import com.example.whatspoppin.R;
import com.example.whatspoppin.adapter.EventAdapter;
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

public class EventListFragment extends ListFragment {
    private ArrayList<Event> eventArrayList = new ArrayList<Event>();
    private ArrayList<Event> bookmarkArrayList = new ArrayList<Event>();
    private EventAdapter eventAdapter;
    private ListView eventList;
    private EditText search;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private DocumentReference usersDoc;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_eventlist, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        eventList = (ListView) view.findViewById(R.id.eventList_list);
        search = (EditText) view.findViewById(R.id.eventlist_inputSearch);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }

        eventArrayList.clear();
        bookmarkArrayList.clear();

        getFireStoreData();
        getBookmarksFirestore();
        realtimeFireStoreData();

        eventAdapter = new EventAdapter(getActivity(), eventArrayList);
        eventList.setAdapter(eventAdapter);
        eventAdapter.notifyDataSetChanged();

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Event clickedEvent = (Event) adapterView.getItemAtPosition(position);
                // open event details
                try{
                    Intent intent = new Intent(getContext(), EventDetailsFragment.class);
                    Bundle args = new Bundle();
                    args.putSerializable("EVENT", clickedEvent);
                    args.putSerializable("BOOKMARKLIST", bookmarkArrayList);
                    intent.putExtra("BUNDLE", args);
                    startActivity(intent);
                }catch(Exception e ){
                    Log.e("START ACTIVITY", e.getMessage());
                }
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                EventListFragment.this.eventAdapter.getFilter().filter(s);
                eventList.setAdapter(eventAdapter);
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //checks firestore for realtime updates
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
                    getFireStoreData();
                    getBookmarksFirestore();
                } else {
                    getFireStoreData();
                    getBookmarksFirestore();
                }
            }
        });
    }

    public void getFireStoreData() {
        eventArrayList.clear();
        db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int i = 0;
                if (task.isSuccessful()) {
                    eventArrayList.clear();
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
                            eventArrayList.add(event);
                        } else {
                            Log.d("saveToEventArrayList", "No such document");
                        }
                        Log.d("EventListFirestore", document.getId() + " => " + document.getData());
                    }
                    if(getActivity()!=null){
                        eventAdapter = new EventAdapter(getActivity(), eventArrayList);
                        eventList.setAdapter(eventAdapter);
                        eventAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.w("EventListFirestore", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public void getBookmarksFirestore(){
        bookmarkArrayList.clear();
        usersDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        bookmarkArrayList.clear();
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
                                bookmarkArrayList.add(event);
                            }
                        }else{
                            bookmarkArrayList = null;
                        }
                        Log.d("getBookmarks", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("getBookmarks", "No such document");
                    }
                } else {
                    Log.d("getBookmarks", "get failed with ", task.getException());
                }
            }
        });
    }
}