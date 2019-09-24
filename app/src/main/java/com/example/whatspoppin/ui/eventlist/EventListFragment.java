package com.example.whatspoppin.ui.eventlist;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventListFragment extends ListFragment {
    private ArrayList<Event> eventArrayList = new ArrayList<Event>();
    private EventListViewModel eventListViewModel;
    private EventAdapter eventAdapter;
    private ListView eventList;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbRef = database.getReference("events");

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
        getData();
        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tv = (TextView) view.findViewById(R.id.text_eventName);
                Toast.makeText(getActivity().getApplicationContext(), tv.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getData(){
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