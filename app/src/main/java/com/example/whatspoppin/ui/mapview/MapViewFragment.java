package com.example.whatspoppin.ui.mapview;

import android.content.Context;
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
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    private ArrayList<Event> eventArrayList = new ArrayList<>();
    private ArrayList<Event> bookmarkArrayList = new ArrayList<>();
    private ArrayList<MapCluster> clusterMarkers = new ArrayList<>();
    private ArrayList<MapCluster> clusterBookmarkMarkers = new ArrayList<>();
    private ArrayList<MapCluster> clusterRecommendedMarkers = new ArrayList<>();
    private ArrayList<String> userBookmarks = new ArrayList<>();
    private ArrayList<String> userPreferences = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoogleMap mMap;
    private DocumentReference usersDoc;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ClusterManager<MapCluster> mClusterManager;

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
//        realtimeFireStoreData();
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
                mClusterManager.clearItems();
                mClusterManager.addItems(new ArrayList<>(clusterMarkers));
                mClusterManager.cluster();
            }
        });

        // show recommended event markers, hide the rest
        rootView.findViewById(R.id.fab_recommended).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClusterManager.clearItems();
                mClusterManager.addItems(new ArrayList<>(clusterRecommendedMarkers));
                mClusterManager.cluster();
            }
        });

        // show bookmark markers, hide the rest
        rootView.findViewById(R.id.fab_bookmarks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClusterManager.clearItems();
                mClusterManager.addItems(new ArrayList<>(clusterBookmarkMarkers));
                mClusterManager.cluster();
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
                            String description = document.getString("description") == null ? "" : document.getString("description");
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
                    getFireStoreUser();
                } else {
                    Log.w("EventListFirestore", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public void getFireStoreUser() { // get user bookmarks & preference
        userBookmarks.clear();
        userPreferences.clear();
        if (mMap != null) mMap.clear();

        usersDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        userBookmarks = new ArrayList<>();
                        userPreferences = new ArrayList<>();

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
                        createEventsMapMarkers();

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

    private void createEventsMapMarkers() {
        // clear lists
        bookmarkArrayList.clear();
        clusterRecommendedMarkers.clear();
        clusterBookmarkMarkers.clear();
        clusterMarkers.clear();

        // add markers
        for (Event event : eventArrayList) {
            String lat = event.getEventLatitude();
            String lng = event.getEventLongitude();
            if (!(lat == null && lng == null)) {
                String name = event.getEventName();
                String desc = event.getEventDescription();
                if (desc.length() > 50 && desc.length() >= name.length()) { // show some description only
                    desc = desc.substring(0, name.length()) + "...";
                }

                MapCluster.EventType markerType = MapCluster.EventType.All;
                if (userBookmarks.contains(name)) { // bookmarked event markers
                    bookmarkArrayList.add(event);
                    markerType = MapCluster.EventType.Bookmark;
                } else if (userPreferences.contains(event.getEventCategory())) { // recommended event markers
                    markerType = MapCluster.EventType.Recommend;
                }
                MapCluster mm = new MapCluster(name, Double.parseDouble(lat), Double.parseDouble(lng),
                        desc, event, markerType);

                // add an instance of the marker to list
                MapCluster mc = mm;
                if (markerType == MapCluster.EventType.Bookmark) {
                    clusterBookmarkMarkers.add(mc);
                } else if (markerType == MapCluster.EventType.Recommend) {
                    clusterRecommendedMarkers.add(mc);
                }
                clusterMarkers.add(mc);

                // add marker to cluster
                mClusterManager.addItem(mm);
            }
        }
        mClusterManager.cluster();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.clear(); //clear old markers
        setUpClusterer(mMap);

        // Map Ui Settings
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMapToolbarEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setRotateGesturesEnabled(false);

        // Default position
        LatLng position = new LatLng(1.3521, 103.8198);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 21));


        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MapCluster>() {
            @Override
            public boolean onClusterClick(Cluster<MapCluster> cluster) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), 17));
                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(mClusterManager);
        // Trigger when marker in cluster is clicked
        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MapCluster>() {
            @Override
            public void onClusterItemInfoWindowClick(MapCluster mapCluster) {
                Intent intent = new Intent(getContext(), EventDetailsFragment.class);
                Bundle args = new Bundle();
                args.putSerializable("EVENT", mapCluster.getEvent());
                args.putSerializable("BOOKMARKLIST", bookmarkArrayList);
                intent.putExtra("BUNDLE", args);
                startActivity(intent);
            }
        });
    }


    private void setUpClusterer(GoogleMap mMap) {
        mClusterManager.clearItems();
        mClusterManager = new ClusterManager<MapCluster>(getContext(), mMap);
        mClusterManager.setAlgorithm(new GridBasedAlgorithm<MapCluster>());
        MapClusterRenderer clusterRenderer = new MapClusterRenderer(getContext(), mMap, mClusterManager);
        mClusterManager.setRenderer(clusterRenderer);

        // Point the map's listeners at the listeners implemented by the cluster manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
    }


    private class MapClusterRenderer extends DefaultClusterRenderer<MapCluster> implements GoogleMap.OnCameraIdleListener {
        private final Context mContext;
        private float currentZoomLevel;

        public MapClusterRenderer(Context context, GoogleMap map, ClusterManager<MapCluster> clusterManager) {
            super(context, map, clusterManager);
            mContext = context;
        }

        @Override
        protected void onBeforeClusterItemRendered(MapCluster mc, MarkerOptions markerOptions) {
            // set the map marker icon
            markerOptions.icon(BitmapDescriptorFactory.fromResource(mc.getMarkerDrawable()));
        }

        @Override
        protected boolean shouldRenderAsCluster(final Cluster<MapCluster> cluster) {
            return super.shouldRenderAsCluster(cluster) && currentZoomLevel < 16;
        }

        @Override
        public void onCameraIdle() {
            currentZoomLevel = mMap.getCameraPosition().zoom;
        }
    }
}