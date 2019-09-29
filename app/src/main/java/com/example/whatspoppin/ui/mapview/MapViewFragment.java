package com.example.whatspoppin.ui.mapview;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.whatspoppin.Event;
import com.example.whatspoppin.R;
import com.example.whatspoppin.ui.eventlist.EventDetailsFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import java.util.Map;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    private ArrayList<Event> eventArrayList = new ArrayList<>();
    private ArrayList<MapMarkers> mapMarkers = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private DocumentReference usersDoc;

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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }

        getFireStoreData();
        //realtimeFireStoreData();
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mapview, container, false);

//        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
//        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        double longitude = location.getLongitude();
//        double latitude = location.getLatitude();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment
        mapFragment.getMapAsync(this);

        rootView.findViewById(R.id.fab_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (MapMarkers mm : mapMarkers) {
                    if (mm.getEventType() == MapMarkers.type.All) {
                        mm.getmMarker().setVisible(true);
                    } else {
                        mm.getmMarker().setVisible(false);
                    }
                }
            }
        });

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

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //checks firestore for realtime updates
/*    public void realtimeFireStoreData() {
        usersDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Listen", "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    //Log.d(TAG, "Current data: " + snapshot.getData());
                    getFireStoreData();
                } else {
                    //Log.d(TAG, "Current data: null");
                    getFireStoreData();
                }
            }
        });
    }*/

    private void getFireStoreData() {
        eventArrayList.clear();
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

                            Event event = new Event(name, address, category, description, datetime_start, datetime_end, url,
                                    imageUrl, lng, lat, location_summary, source);
                            eventArrayList.add(event);

                            if (!(lat == null && lng == null)) {
                                Marker markerAll = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                                        .title(event.getEventName())
                                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.placeholder)));
                                mapMarkers.add(new MapMarkers(event, markerAll, MapMarkers.type.All));

                                // Temporary
                                Marker markerBookmark = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                                        .title(event.getEventName())
                                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.bookmark)));
                                mapMarkers.add(new MapMarkers(event, markerBookmark, MapMarkers.type.Bookmark));
                                markerBookmark.setVisible(false);

                                Marker markerReco = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)))
                                        .title(event.getEventName())
                                        .icon(bitmapDescriptorFromVector(getActivity(), R.drawable.best)));
                                mapMarkers.add(new MapMarkers(event, markerReco, MapMarkers.type.Recommend));
                                markerReco.setVisible(false);

                                JSONObject obj = new JSONObject();
                                try {
                                    obj.put("event", event);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                markerAll.setTag(obj);
                                markerBookmark.setTag(obj);
                                markerReco.setTag(obj);
                            }
                        } else {
                            Log.d("saveToEventArrayList", "No such document");
                        }
                        Log.d("EventListFirestore", document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.w("EventListFirestore", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public void displayToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                JSONObject obj = (JSONObject) marker.getTag();
                try {
                    Event event = (Event) obj.get("event");
                    Intent intent = new Intent(getContext(), EventDetailsFragment.class);
                    Bundle args = new Bundle();
                    args.putSerializable("EVENTLIST", eventArrayList);
                    intent.putExtra("BUNDLE", args);
                    intent.putExtra("eventName", event.getEventName());
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