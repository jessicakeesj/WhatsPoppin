package com.example.whatspoppin;

import com.example.whatspoppin.view.authentication.SignUp;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.Assert;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;

public class signupTest {
    private FirebaseAuth mAuth;
    @Test
    public void testIsEmailValid() {
        String testEmail = "qwerty@gmail.com";
        Assert.assertThat(String.format("Email Validity Test failed for %s ", testEmail), SignUp.checkEmailForValidity(testEmail), is(true));
    }
}
