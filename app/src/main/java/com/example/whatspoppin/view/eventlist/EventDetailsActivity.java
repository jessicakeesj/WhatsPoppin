package com.example.whatspoppin.view.eventlist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import com.example.whatspoppin.R;
import com.example.whatspoppin.model.Event;
import com.example.whatspoppin.view.authentication.SignIn;
import com.example.whatspoppin.viewmodel.EventDetailsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventDetailsActivity extends AppCompatActivity {

    private TextView eventNameTV, eventSummaryTV, eventDetails, eventSource;
    private ImageView eventImage, bookmarkImg;
    private Button linkBtn;
    private Event event;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Event> bookmarkList = new ArrayList<>();
    private DocumentReference usersDoc;
    private FirebaseUser currentUser;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private EventDetailsViewModel eventDetailsViewModel;
    private Date now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventdetails);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }

        eventDetailsViewModel = ViewModelProviders.of(this).get(EventDetailsViewModel.class);
        final EventDetailsViewModel model = ViewModelProviders.of(this).get(EventDetailsViewModel.class);
        bookmarkList.clear();
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        event = (Event) args.getSerializable("EVENT");
        bookmarkList = (ArrayList<Event>) args.getSerializable("BOOKMARKLIST");

        //set view
        eventNameTV = findViewById(R.id.details_eventName);
        eventSummaryTV = findViewById(R.id.details_eventSummary);
        eventImage = findViewById(R.id.details_eventImage);
        eventDetails = findViewById(R.id.details_eventDateTime);
        eventSource = findViewById(R.id.details_eventSource);
        linkBtn = findViewById(R.id.details_eventLink);
        bookmarkImg = (ImageView) findViewById(R.id.details_bookmark);

        try {
            if(event.getEventImageUrl().contains("http")){
                Picasso.get().load(event.getEventImageUrl()).into(eventImage);
            }else{
                Picasso.get().load("https://www." + event.getEventImageUrl()).into(eventImage);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        eventNameTV.setText(event.getEventName().trim());
        String startDate = formatDate(event.getEvent_datetime_start());
        String endDate = formatDate(event.getEvent_datetime_end());
        String detailsString;
        if (event.getEventAddress() == null &&  event.getEventAddress() == "null"){
            detailsString = "<b> Event Details: </b>" + "<br>" + startDate + " - " + endDate;
        }else{
            detailsString = "<b> Event Details: </b>" + "<br>" + startDate + " - " + endDate + "<br>" + event.getEventAddress();
        }
        eventDetails.setText(Html.fromHtml(detailsString));
        eventSummaryTV.setText(event.getEventDescription());
        eventSource.setText("Source: " + event.getEventSource());

        if(bookmarkList.size()== 0){
            bookmarkImg.setImageResource(R.drawable.ic_unmarked);
        }else{
            for(Event e : bookmarkList){
                if(e.getEventName().trim().equals(event.getEventName().trim())){
                    bookmarkImg.setImageResource(R.drawable.ic_marked);
                    break;
                }else{
                    bookmarkImg.setImageResource(R.drawable.ic_unmarked);
                }
            }
        }

        try {
            now = new Date();
            Date eventDate = simpleDateFormat.parse(event.getEvent_datetime_end());
            if(eventDate.before(now)){
                linkBtn.setText("Event Expired");
                linkBtn.setEnabled(false);
            }else{

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(event.getEventUrl()));
                startActivity(browserIntent);
            }
        });

        bookmarkImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap marked = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_marked);
                Bitmap current = ((BitmapDrawable)bookmarkImg.getDrawable()).getBitmap();
                if (current.sameAs(marked)) {
                    Event mock = null;
                    for(Event e : bookmarkList){
                        if(e.getEventName().trim().equals(event.getEventName().trim())){
                            mock = e;
                        }
                    }
                    if(mock!=null){
                        bookmarkList.remove(mock);
                        model.updateBookmarksFirestore(bookmarkList);
                        bookmarkImg.setImageResource(R.drawable.ic_unmarked);
                    }
                }else{
                    if(!bookmarkList.contains(event)){
                        bookmarkList.add(event);
                        model.updateBookmarksFirestore(bookmarkList);
                    }
                    bookmarkImg.setImageResource(R.drawable.ic_marked);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        } else if (item.getItemId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();

            // Launching the login activity
            Intent intent = new Intent(getApplication(), SignIn.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
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
