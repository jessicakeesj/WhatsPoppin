package com.example.whatspoppin.ui.eventlist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.ViewModelProviders;
import com.example.whatspoppin.Event;
import com.example.whatspoppin.EventAdapter;
import com.example.whatspoppin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventListFragment extends ListFragment {
    private ArrayList<Event> eventArrayList = new ArrayList<Event>();
    private EventListViewModel eventListViewModel;
    private EventAdapter eventAdapter;
    private ListView eventList;
    private List<String> eventNameList = new ArrayList<>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("events");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        eventListViewModel = ViewModelProviders.of(this).get(EventListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_eventlist, container, false);
        return root;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        eventList = (ListView) view.findViewById(R.id.list_eventList);
        //getFirebaseData();
        getFireStoreData();
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view.findViewById(R.id.text_eventName);
                //Toast.makeText(getActivity().getApplicationContext(), tv.getText().toString(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), EventDetailsFragment.class);
                Bundle args = new Bundle();
                args.putSerializable("EVENTLIST",(Serializable)eventArrayList);
                intent.putExtra("BUNDLE",args);
                intent.putExtra("eventName", tv.getText().toString());
                startActivity(intent);
            }
        });
    }

    public void getFireStoreData(){
        eventArrayList.clear();

        db.collection("events")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
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

                                    String lng, lat;
                                    if(document.get("lng") == null){
                                        lng = "null";
                                    }else{
                                        lng = document.get("lng").toString();
                                    }
                                    if(document.get("lat") == null){
                                        lat = "null";
                                    }else{
                                        lat = document.get("lat").toString();
                                    }

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
                            eventAdapter = new EventAdapter(getActivity().getApplicationContext(), eventArrayList);
                            eventList.setAdapter(eventAdapter);
                            eventAdapter.notifyDataSetChanged();
                        } else {
                            Log.w("EventListFirestore", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void getFirebaseData(){
        eventArrayList.clear();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.child("name").getValue(String.class);
                    String address = ds.child("address").getValue(String.class);
                    String category = ds.child("category").getValue(String.class);
                    String description = ds.child("description").getValue(String.class);
                    String datetime_start = ds.child("datetime_start").getValue(String.class);
                    String datetime_end = ds.child("datetime_end").getValue(String.class);
                    String url = ds.child("url").getValue(String.class);
                    String imageUrl = ds.child("imageUrl").getValue(String.class);
                    String lng = ds.child("lng").getValue(float.class).toString();
                    String lat = ds.child("lat").getValue(float.class).toString();
                    String location_summary = ds.child("location_summary").getValue(String.class);
                    String source = ds.child("source").getValue(String.class);

                    Event event = new Event(name, address, category, description, datetime_start, datetime_end, url,
                            imageUrl, lng, lat, location_summary, source);

                    Log.d("EVENT", String.valueOf(event));
                    eventArrayList.add(event);
                }
                eventAdapter = new EventAdapter(getActivity().getApplicationContext(), eventArrayList);
                eventList.setAdapter(eventAdapter);
                eventAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }
}