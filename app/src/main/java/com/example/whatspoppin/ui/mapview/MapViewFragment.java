package com.example.whatspoppin.ui.mapview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.whatspoppin.Event;
import com.example.whatspoppin.R;
import com.example.whatspoppin.ui.eventlist.EventDetailsFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    private ArrayList<Event> eventArrayList = new ArrayList<>();
    private ArrayList<Event> bookmarkArrayList = new ArrayList<>();
    private ArrayList<MapMarkers> mapMarkers = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoogleMap mMap;
    private DocumentReference usersDoc;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

//    private LocationManager locationManager;

    public MapViewFragment() { // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }
        getFireStoreEvents();
        realtimeFireStoreData();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mapview, container, false);

//        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(this);

        // show all
        rootView.findViewById(R.id.fab_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (MapMarkers mm : mapMarkers) {
                    mm.getmMarker().setVisible(true);
                }
            }
        });

        // show recommended event markers, hide the rest
        rootView.findViewById(R.id.fab_recommended).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (MapMarkers mm : mapMarkers) {
                    if (mm.getEventType() == MapMarkers.type.Recommend) {
                        mm.getmMarker().setVisible(true);
                    } else {
                        mm.getmMarker().setVisible(false);
                    }
                }
            }
        });

        // show bookmark markers, hide the rest
        rootView.findViewById(R.id.fab_bookmarks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (MapMarkers mm : mapMarkers) {
                    if (mm.getEventType() == MapMarkers.type.Bookmark) {
                        mm.getmMarker().setVisible(true);
                    } else {
                        mm.getmMarker().setVisible(false);
                    }
                }
            }
        });

        return rootView;
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
                    Log.d("SNAPSHOT", snapshot.toString());
                    getFireStoreEvents();
                } else {
                    getFireStoreEvents();
                }
            }
        });
    }

    private void getFireStoreEvents() {
        eventArrayList.clear();

        // get list of all events
        db.collection("events").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                            String lng = document.get("lng") == null ? null : document.get("lng").toString();
                            String lat = document.get("lat") == null ? null : document.get("lat").toString();
                            String location_summary = document.getString("location_summary");
                            String source = document.getString("source");

                            Event event = new Event(name, address, category, description, datetime_start,
                                    datetime_end, url, imageUrl, lng, lat, location_summary, source);
                            eventArrayList.add(event);
                        } else {
                            Log.d("saveToEventArrayList", "No such document");
                        }
                        Log.d("EventListFirestore", document.getId() + " => " + document.getData());
                    }
                    createEventsMapMarkers();
                } else {
                    Log.w("EventListFirestore", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public void createEventsMapMarkers() { // get user bookmarks & preference
        bookmarkArrayList.clear();
        mapMarkers.clear();
        if (mMap != null) mMap.clear();

        usersDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ArrayList<String> userBookmarks = new ArrayList<>();
                        ArrayList<String> userPreferences = new ArrayList<>();

                        // get bookmarks list
                        String b = String.valueOf(document.get("bookmarks"));
                        if (b != "null" || b != null || b != "[]") {
                            ArrayList<HashMap<String, String>> bkm = (ArrayList<HashMap<String, String>>) document.get("bookmarks");
                            for (HashMap<String, String> testMap : bkm) {
                                userBookmarks.add(testMap.get("eventName"));
                            }
                        }
                        // get preferences list
                        userPreferences.addAll((ArrayList<String>) document.get("interests"));

                        // add markers
                        for (Event event : eventArrayList) {
                            String lat = event.getEventLatitude();
                            String lng = event.getEventLongitude();
                            if (!(lat == null && lng == null)) {

                                int markerDrawable = R.drawable.mm_normal;
                                MapMarkers.type markerType = MapMarkers.type.All;

                                if (userBookmarks.contains(event.getEventName())) { // bookmarked event markers
                                    bookmarkArrayList.add(event);
                                    markerDrawable = R.drawable.mm_bookmark;
                                    markerType = MapMarkers.type.Bookmark;
                                } else if (userPreferences.contains(event.getEventCategory())) { // recommended event markers
                                    markerDrawable = R.drawable.mm_recommended;
                                    markerType = MapMarkers.type.Recommend;
                                }

                                Marker mapMarker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                                        .title(event.getEventName())
                                        .icon(BitmapDescriptorFactory.fromResource(markerDrawable)));
                                mapMarkers.add(new MapMarkers(event, mapMarker, markerType));
                                mapMarker.setVisible(true);

                                JSONObject obj = new JSONObject();
                                try {
                                    obj.put("event", event);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mapMarker.setTag(obj);
                            }
                        }
                        Log.d("getUserDetails", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("getUserDetails", "No such document");
                    }
                } else {
                    Log.d("getUserDetails", "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.clear(); //clear old markers

        // Map Ui Settings
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMapToolbarEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setRotateGesturesEnabled(false);

        LatLng position = new LatLng(1.3521, 103.8198);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 17));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                JSONObject obj = (JSONObject) marker.getTag();
                try {
                    Intent intent = new Intent(getContext(), EventDetailsFragment.class);
                    Event event = (Event) obj.get("event");
                    Bundle args = new Bundle();
                    args.putSerializable("EVENT", event);
                    args.putSerializable("BOOKMARKLIST", bookmarkArrayList);
                    intent.putExtra("BUNDLE", args);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

class MapMarkers {
    private Marker mMarker;
    private Event event;
    private type eventType;

    public MapMarkers(Event event, Marker marker, type t) {
        this.event = event;
        this.mMarker = marker;
        this.eventType = t;
    }

    public Marker getmMarker() {
        return this.mMarker;
    }

    public Event getEvent() {
        return this.event;
    }

    public type getEventType() {
        return this.eventType;
    }

    enum type {All, Bookmark, Recommend}
}