package com.example.whatspoppin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

public class SignIn extends AppCompatActivity implements View.OnClickListener {
    private Button login;
    private Button create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        login=(Button)findViewById(R.id.SignInBtn);
        create=(Button)findViewById(R.id.SignUpBtn);
        login.setOnClickListener(this);
        create.setOnClickListener(this);
    }

    private long mLastClickTime = 0;

    @Override
    public void onClick(View v) {
        // Preventing multiple clicks, using threshold of 1 second
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        // Handle button clicks
        if(v.getId()==R.id.SignInBtn){
            Intent send=new Intent(getApplicationContext(),NavDrawer.class);
            startActivity(send);
            this.finish();
        }
        else if(v.getId()==R.id.SignUpBtn){
            Intent c=new Intent(getApplicationContext(),SignUp.class);
            startActivity(c);
        }
    }
}
