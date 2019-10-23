package com.example.whatspoppin.view.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.text.TextUtils;
import android.widget.Toast;
import com.example.whatspoppin.model.AccountUser;
import com.example.whatspoppin.model.Event;
import com.example.whatspoppin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    private Button signup, back;
    private String email, password;
    private EditText emailTV, pwdTV;
    private boolean checkAuth = false;
    private static final String TAG = "SignUp Activity";
    private AccountUser user;
    private ArrayList<Event> bookmarks = new ArrayList<Event>();
    private ArrayList<String> interests = new ArrayList<String>();
    private boolean receiveNotification;
    private boolean showNearbyEvents;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        emailTV = findViewById(R.id.su_emailInput);
        pwdTV = findViewById(R.id.su_pwdInput);
        signup = (Button) findViewById(R.id.SignUpBtn);
        back = (Button) findViewById(R.id.BackBtn);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignIn.class);
                startActivity(intent);
            }
        });
    }

    private void registerNewUser() {
        email = emailTV.getText().toString();
        password = pwdTV.getText().toString();
        bookmarks.clear();
        interests.clear();
        receiveNotification = false;
        showNearbyEvents = false;


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email!", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        //check for valid email and password
        if(validateEmail(email) && password.length() >= 8 && password.length() <= 20){
            checkAuth = true;
        }else {
            if(!validateEmail(email)){
                Toast.makeText(getApplicationContext(), "Registration Failed. Please enter a valid email address!", Toast.LENGTH_SHORT).show();
            }else if(password.length() < 8 || password.length() > 20) {
                Toast.makeText(getApplicationContext(), "Registration Failed. Password should be between 8 and 20 characters!", Toast.LENGTH_SHORT).show();
            }
        }

        //allow signup if email and password is valid
        if(checkAuth){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();

                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser != null){
                                    String userId = currentUser.getUid();
                                    String email = currentUser.getEmail();
                                    user = new AccountUser(userId, email, bookmarks,interests, receiveNotification, showNearbyEvents);
                                    setFireStoreData(user);
                                }

                                Intent intent = new Intent(SignUp.this, SignIn.class);
                                startActivity(intent);
                            }else{
                                Log.e(TAG, "onComplete: Failed=" + task.getException().getMessage());
                                Toast.makeText(getApplicationContext(), "Registration failed!" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    public void setFireStoreData(AccountUser user) {
        db.collection("users").document(user.getUserId()).set(user);
    }

    //validate email address
    public static boolean validateEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }
}
