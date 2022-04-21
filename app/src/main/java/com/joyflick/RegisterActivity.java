package com.joyflick;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "RegisterActivity";
    private EditText rUsername;
    private EditText rPassword;
    private Button rLogin;
    private Button rSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rUsername = findViewById(R.id.rUsername);
        rPassword = findViewById(R.id.rPassword);
        rLogin = findViewById(R.id.rLogin);
        rSignup = findViewById(R.id.rSignup);

        getSupportActionBar().hide();

        rLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Button login clicked");
                goLoginPage();
            }
        });

        rSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Attempting to signup" );
                String username = rUsername.getText().toString();
                String password = rPassword.getText().toString();
                signupUser(username, password);

            }
        });



    }// end of the oncreate

    private void signupUser(String username, String password) {
        Log.i(TAG, "Attempting to sign up user" + username);
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with signup" + e.toString());
                    Toast.makeText(RegisterActivity.this, "Issue with signing up, please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                // signup success
                goLoginPage();
                Toast.makeText(RegisterActivity.this, "Sign up success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goLoginPage() {
        Intent i = new Intent(this,LoginActivity.class);
        startActivity(i);
        finish();

    }
}// end of the class