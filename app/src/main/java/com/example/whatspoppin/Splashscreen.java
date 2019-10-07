package com.example.whatspoppin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.ProgressBar;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

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
                        sleep(300); //run for 3 secs then sleep
                        progressBar.setProgress(progress);
                    }
                    Intent intent = new Intent(getApplicationContext(), SignIn.class); //to direct it to this activity after the splash screen finishes
//                    Intent intent = new Intent(getApplicationContext(), NavDrawer.class); //to direct it to this activity after the splash screen finishes
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
