package com.example.whatspoppin.ui.eventlist;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.whatspoppin.Event;
import com.example.whatspoppin.R;
import com.squareup.picasso.Picasso;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventDetailsFragment extends AppCompatActivity {

    private String eventName;
    private TextView eventNameTV, eventSummaryTV, eventDetails, eventSource;
    private ImageView eventImage;
    private Button linkBtn;
    private ArrayList<Event> eventArrayList = new ArrayList<Event>();
    private Event event;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_eventdetails);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        eventArrayList = (ArrayList<Event>) args.getSerializable("EVENTLIST");
        eventName = intent.getExtras().getString("eventName");

        eventNameTV = findViewById(R.id.details_eventName);
        eventSummaryTV = findViewById(R.id.details_eventSummary);
        eventImage = findViewById(R.id.details_eventImage);
        eventDetails = findViewById(R.id.details_eventDateTime);
        eventSource = findViewById(R.id.details_eventSource);
        linkBtn = findViewById(R.id.details_eventLink);

        for (Event e : eventArrayList) {
            if (e.getEventName().trim().equals(eventName.trim())) {
                event = e;
                break;
            }
        }

        URL url = null;
        try {
            Picasso.get().load("https://www." + event.getEventImageUrl()).into(eventImage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        eventNameTV.setText(eventName);
        String startDate = formatDate(event.getEvent_datetime_start());
        String endDate = formatDate(event.getEvent_datetime_end());
        eventDetails.setText(startDate + " - " + endDate + "\n" + event.getEventLocationSummary());
        eventSummaryTV.setText(event.getEventDescription());
        eventSource.setText("Source: " + event.getEventSource());

        linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getEventUrl()));
                startActivity(browserIntent);
            }
        });
    }

    private String formatDate(String eventDate){
        String dateString = null;

        String[] months = new String[12];
        months[0] = "Jan";
        months[1] = "Feb";
        months[2] = "Mar";
        months[3] = "Apr";
        months[4] = "May";
        months[5] = "Jun";
        months[6] = "Jul";
        months[7] = "Aug";
        months[8] = "Sep";
        months[9] = "Oct";
        months[10] = "Nov";
        months[11] = "Dec";

        try {
            Date date = simpleDateFormat.parse(eventDate);
            int weekday = date.getDay();
            int day = date.getDate();
            int month = date.getMonth();
            int year = date.getYear() + 1900;
            int hour = date.getHours();
            int min = date.getMinutes();

            if(hour < 12){
                if(min == 0){
                    dateString = day + " " + months[month] + " " + year + ", " + hour + "AM" ;
                }else{
                    dateString = day + " " + months[month] + " " + year + ", " + hour + "." + min + "AM" ;
                }
            } else{
                hour = hour - 12;
                if(min == 0){
                    dateString = day + " " + months[month] + " " + year + ", " + hour + "PM" ;
                }else{
                    dateString = day + " " + months[month] + " " + year + ", " + hour + "." + min + "PM" ;
                }
            }
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }
}
