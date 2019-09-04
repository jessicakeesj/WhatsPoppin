package com.example.whatspoppin;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.ProgressBar;

import android.content.Intent;
import android.os.Bundle;

public class Splashscreen extends AppCompatActivity {
    private ProgressBar progressBar;
    int progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        progressBar = (ProgressBar) findViewById(R.id.pBar);
        Thread thread=new Thread(){
            @Override
            public void run() {
                for (progress=30;progress<300;progress=progress+10){
                    try {
                        sleep(3000); //run for 3 secs then sleep
                        progressBar.setProgress(progress);
                        //sleep(3000); //run for 3 secs then sleep
                        Intent intent= new Intent(getApplicationContext(),MainActivity.class); //to direct it to this activity after the splash screen finishes
                        startActivity(intent);
                        finish(); //to stop it from rerunning
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();//to start the method
    }
}
