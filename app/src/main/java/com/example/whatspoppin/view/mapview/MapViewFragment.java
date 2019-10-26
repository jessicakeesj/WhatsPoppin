package com.example.whatspoppin.view.mapview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.whatspoppin.model.Event;
import com.example.whatspoppin.R;
import com.example.whatspoppin.view.eventlist.EventDetailsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.GridBasedAlgorithm;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MapViewFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private ArrayList<Event> eventArrayList = new ArrayList<>();
    private ArrayList<Event> bookmarkArrayList = new ArrayList<>();
    private ArrayList<MapCluster> clusterMarkers = new ArrayList<>();
    private ArrayList<MapCluster> clusterBookmarkMarkers = new ArrayList<>();
    private ArrayList<MapCluster> clusterRecommendedMarkers = new ArrayList<>();
    private ArrayList<String> userBookmarks = new ArrayList<>();
    private ArrayList<String> userPreferences = new ArrayList<>();
    private boolean showNearbyEvent = false;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoogleMap mMap;
    private DocumentReference usersDoc;
    private FirebaseUser currentUser;
    private ClusterManager<MapCluster> mClusterManager;
    private double userLat = 1.3521;
    private double userLng = 103.8198;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    private Marker userMarker;
    private FusedLocationProviderClient fusedLocationClient;
    private float currentZoomLevel = 17;

    public MapViewFragment() { // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // check location permission
        if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
        }
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
        startLocationUpdates();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mapview, container, false);

        // firebase user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null)
            usersDoc = db.collection("users").document(currentUser.getUid());

        getFireStoreEvents();

        // get location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            userLat = location.getLatitude();
                            userLng = location.getLongitude();
                            LatLng position = new LatLng(userLat, userLng);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, currentZoomLevel));
                            if (userMarker == null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, currentZoomLevel));
                                userMarker = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(userLat, userLng))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mm_position)));
                            } else{
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, currentZoomLevel));
                                userMarker.setPosition(position);
                            }
                        } else
                            displayToast("Location not enabled.");
                    }
                });

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

    // Trigger new location updates at interval
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(getContext());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
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
                        } else
                            Log.d("saveToEventArrayList", "No such document");
                    }
                    getFireStoreUser();
                } else
                    Log.w("EventListFirestore", "Error getting documents.", task.getException());
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
                            for (HashMap<String, String> bevent : bkm)
                                userBookmarks.add(bevent.get("eventName"));

                        }
                        // get preferences list
                        userPreferences.addAll((ArrayList<String>) document.get("interests"));
                        showNearbyEvent = (boolean) document.get("showNearbyEvents");

                        // add markers
                        createEventsMapMarkers();
                    } else
                        Log.d("getUserDetails", "No such document");
                } else
                    Log.d("getUserDetails", "get failed with ", task.getException());
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
            if (!(event.getEventLatitude() == null && event.getEventLongitude() == null)) {
                Double lat = Double.parseDouble(event.getEventLatitude());
                Double lng = Double.parseDouble(event.getEventLongitude());
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
                } else if ((lat >= userLat - 0.005 && lat <= userLat + 0.005) &&
                        (lng >= userLng - 0.005 && lng <= userLng + 0.005) && showNearbyEvent) {
                    markerType = MapCluster.EventType.Recommend;
                }
                MapCluster mm = new MapCluster(name, lat, lng, desc, event, markerType);

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
        // User location marker
        userMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(userLat, userLng))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mm_position)));
        mClusterManager.cluster();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.clear(); //clear old markers
        userMarker = null;
        setUpClusterer(mMap);

        // Map Ui Settings
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMapToolbarEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setRotateGesturesEnabled(false);

        // Default position
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLat, userLng), 21));

        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MapCluster>() {
            @Override
            public boolean onClusterClick(Cluster<MapCluster> cluster) {
                if (cluster.getSize() > 20) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), 16));
                } else if (cluster.getSize() > 6)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), 15));
                else
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), 17));
                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(mClusterManager);
        // Trigger when marker in cluster is clicked
        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<MapCluster>() {
            @Override
            public void onClusterItemInfoWindowClick(MapCluster mapCluster) {
                Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("EVENT", mapCluster.getEvent());
                args.putSerializable("BOOKMARKLIST", bookmarkArrayList);
                intent.putExtra("BUNDLE", args);
                startActivity(intent);
            }
        });
    }

    private void setUpClusterer(GoogleMap mMap) {
        mClusterManager = new ClusterManager<MapCluster>(getContext(), mMap);
        mClusterManager.setAlgorithm(new GridBasedAlgorithm<MapCluster>());
        MapClusterRenderer clusterRenderer = new MapClusterRenderer(getContext(), mMap, mClusterManager);
        mClusterManager.setRenderer(clusterRenderer);

        // Point the map's listeners at the listeners implemented by the cluster manager.
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
    }

    public void displayToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        userLat = location.getLatitude();
        userLng = location.getLongitude();
        LatLng position = new LatLng(userLat, userLng);
        if(mMap != null){
            if (userMarker == null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, currentZoomLevel));
                userMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(userLat, userLng))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mm_position)));
            } else
                userMarker.setPosition(position);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class MapClusterRenderer extends DefaultClusterRenderer<MapCluster> implements GoogleMap.OnCameraIdleListener {
        private final Context mContext;

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
            return super.shouldRenderAsCluster(cluster) && currentZoomLevel <= 15;
        }

        @Override
        public void onCameraIdle() {
            currentZoomLevel = mMap.getCameraPosition().zoom;
        }
    }
}