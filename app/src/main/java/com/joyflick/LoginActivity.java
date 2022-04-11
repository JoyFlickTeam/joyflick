package com.joyflick;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button Login;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
/*
        if(ParseUser.getCurrentUser() != null){
            goMainActivity();
        }

 */

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        Login = findViewById(R.id.Login);
        register = findViewById(R.id.register);

        getSupportActionBar().hide();

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Login button pressed");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username,password);
            }
        });
       /*
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Register button pressed");
                goRegisterPage();
            }
        });

        */

    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to log in user" + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with loginUser" + e.toString());
                    Toast.makeText(LoginActivity.this, "Issue with login user", Toast.LENGTH_SHORT).show();
                    return;
                }
                // login is successful
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
            }
        });
    }
    /*
    private void goRegisterPage() {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
        finish();
    }

     */

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();

    }
}// end of the class