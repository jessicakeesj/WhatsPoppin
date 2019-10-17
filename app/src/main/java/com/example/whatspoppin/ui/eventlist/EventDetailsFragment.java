package com.example.whatspoppin.ui.eventlist;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.whatspoppin.Event;
import com.example.whatspoppin.R;
import com.example.whatspoppin.ui.authentication.SignIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EventDetailsFragment extends AppCompatActivity {

    private String eventName;
    private TextView eventNameTV, eventSummaryTV, eventDetails, eventSource;
    private ImageView eventImage, bookmarkImg;
    private Button linkBtn;
    private ArrayList<Event> eventArrayList = new ArrayList<Event>();
    private Event event;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Event> bookmarkList = new ArrayList<>();
    private DocumentReference usersDoc;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_eventdetails);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            usersDoc = db.collection("users").document(currentUser.getUid());
        }

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

        String detailsString = "<b> Event Details: </b>" + "<br>" + startDate + " - " + endDate + "<br>" + event.getEventLocationSummary();
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
                        updateBookmarksFirestore();
                        bookmarkImg.setImageResource(R.drawable.ic_unmarked);
                    }
                }else{
                    if(!bookmarkList.contains(event)){
                        bookmarkList.add(event);
                        updateBookmarksFirestore();
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


    public void updateBookmarksFirestore(){
        usersDoc
                .update("bookmarks", bookmarkList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("updateBookmarks", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("updateBookmarks", "Error updating document", e);
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
