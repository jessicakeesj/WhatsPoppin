package com.example.whatspoppin.ui.eventlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.whatspoppin.Event;
import com.example.whatspoppin.R;
import com.example.whatspoppin.ui.eventlist.EventListViewModel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class EventDetailsFragment extends AppCompatActivity {

    private String eventName;
    private TextView eventNameTV, eventSummaryTV, eventDateTime;
    private ImageView eventImage;
    private ArrayList<Event> eventArrayList = new ArrayList<Event>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_eventdetails);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        eventArrayList = (ArrayList<Event>) args.getSerializable("EVENTLIST");
        eventName = intent.getExtras().getString("eventName");

        eventNameTV = (TextView) findViewById(R.id.details_eventName);
        eventSummaryTV = (TextView) findViewById(R.id.details_eventSummary);
        eventImage = (ImageView) findViewById(R.id.details_eventImage);
        eventDateTime = (TextView) findViewById(R.id.details_eventDateTime);

        for(Event e : eventArrayList){
            if(e.getEventName().equals(eventName)){

                URL url = null;
                try {
                    url = new URL(e.getEventImageUrl());
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    eventImage.setImageBitmap(bmp);
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                eventNameTV.setText(eventName);
                eventDateTime.setText(e.getEvent_datetime_start() + " - " + e.getEvent_datetime_end() +
                        "\n" + e.getEventLocationSummary());
                eventSummaryTV.setText(e.getEventDescription());
            }else{
                eventNameTV.setText("NOT FOUND");
            }
        }
    }

    /*public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //EventListViewModel eventListViewModel = ViewModelProviders.of(this).get(EventListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_eventdetails, container, false);

        //Bundle bundle = getArguments();
        //eventName = bundle.getString("eventName");
        return root;
    }*/

    public void onViewCreated (View view, Bundle savedInstanceState) {
        eventNameTV = (TextView) view.findViewById(R.id.details_eventName);
        eventNameTV.setText("Hello");
    }
}
