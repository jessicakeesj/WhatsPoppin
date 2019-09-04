package com.example.whatspoppin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;

public class SignUp extends AppCompatActivity implements View.OnClickListener{
    private Button signup, back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signup=(Button)findViewById(R.id.SignUpBtn);
        back=(Button)findViewById(R.id.BackBtn);
        signup.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent r=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(r);
        // Handle button clicks
        if(v.getId()==R.id.BackBtn){
            Intent send=new Intent(getApplicationContext(),SignIn.class);
            startActivity(send);
            this.finish();
        }
    }
}
