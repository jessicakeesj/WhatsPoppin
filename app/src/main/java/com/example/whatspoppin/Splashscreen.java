package com.example.whatspoppin;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.content.Intent;
import android.os.Bundle;

import com.example.whatspoppin.view.authentication.SignIn;
import com.google.firebase.auth.FirebaseAuth;

public class Splashscreen extends AppCompatActivity {
    private ProgressBar progressBar;
    private int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        progressBar = findViewById(R.id.pBar);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
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
}
