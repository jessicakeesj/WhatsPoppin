package com.example.whatspoppin.ui.mapview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.whatspoppin.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewFragment extends Fragment {

    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_mapview, container,false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);  //use SuppoprtMapFragment for using in fragment instead of activity  MapFragment = activity   SupportMapFragment = fragment

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                mMap.clear(); //clear old markers

                // Map Ui Settings
                UiSettings mUiSettings = mMap.getUiSettings();
                mUiSettings.setZoomControlsEnabled(false);
                mUiSettings.setMapToolbarEnabled(false);
                mUiSettings.setMyLocationButtonEnabled(true);
                mUiSettings.setRotateGesturesEnabled(false);

//                CameraPosition googlePlex = CameraPosition.builder()
//                        .target(new LatLng(1.3521,103.8198))
//                        .zoom(15)
//                        .bearing(0)
//                        .tilt(45)
//                        .build();

                LatLng position = new LatLng(1.3521, 103.8198);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 12));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(1.3521, 103.8198))
                        .title("Spider Man")
                        .icon(bitmapDescriptorFromVector(getActivity(),R.drawable.ic_menu_map)));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.4629101,-122.2449094))
                        .title("Iron Man")
                        .snippet("His Talent : Plenty of money"));

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(37.3092293,-122.1136845))
                        .title("Captain America"));
            }
        });

        rootView.findViewById(R.id.fab_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { displayToast("All clicked"); }
        });

        rootView.findViewById(R.id.fab_recommended).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { displayToast("recommended clicked"); }
        });

        rootView.findViewById(R.id.fab_bookmarks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { displayToast("bookmarks clicked"); }
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


    public void displayToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

}