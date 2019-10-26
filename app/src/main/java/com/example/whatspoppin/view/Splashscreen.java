package com.example.whatspoppin.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.ProgressBar;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.whatspoppin.R;
import com.example.whatspoppin.model.Event;
import com.example.whatspoppin.view.authentication.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Splashscreen extends AppCompatActivity {
    private ProgressBar progressBar;
    private int progress;
    private ArrayList<Event> eventArrayList = new ArrayList<>();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        progressBar = findViewById(R.id.pBar);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    getFireStoreEvents();
                    for (progress = 10; progress < 100; progress = progress + 10) {
                        sleep(200); //run for 2 secs then sleep
                        progressBar.setProgress(progress);
                    }
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    Intent intent;
                    if (auth.getCurrentUser() != null) { // user is logged in
                        intent = new Intent(getApplicationContext(), NavDrawer.class);
                    }else{ // user not logged in
                        intent = new Intent(getApplicationContext(), SignIn.class);
                    }
                    //to direct it to this activity after the splash screen finishes
                    startActivity(intent);
                    finish(); //to stop it from rerunning

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();//to start the method
    }

    private void getFireStoreEvents() {
        final ArrayList<String> outdatedEvents = new ArrayList<>();
        final Date now = new Date();
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

                            try {
                                Date eventDate = simpleDateFormat.parse(datetime_end);
                                if(eventDate.before(now)){
                                    outdatedEvents.add(name);
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else
                            Log.d("saveToEventArrayList", "No such document");
                    }
                    updateFireStore(outdatedEvents);
                } else
                    Log.w("EventListFirestore", "Error getting documents.", task.getException());
            }
        });
    }

    public void updateFireStore(ArrayList<String> outdated){
        for(String name : outdated){
            db.collection("events").document(name)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
    }
}
