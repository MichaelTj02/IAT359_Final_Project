package com.example.iat359_final_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity  {
    private EditText usernameEditText, passwordEditText;
    private TextView loginText;
    private SharedPreferences sharedPreferences;
    public static final String DEFAULT = "not available";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernameEditText = findViewById(R.id.editTextUsername);
        passwordEditText = findViewById(R.id.editTextPassword);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", DEFAULT);
        String password = sharedPreferences.getString("password", DEFAULT);

        if (username.equals(DEFAULT) || password.equals(DEFAULT)) {
        }
        else
        {
            Intent intent= new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        Button signupButton = findViewById(R.id.signupButton);
        signupButton.setOnClickListener(v -> signUpUser());

        // clickable login text if user have an account already
        loginText = findViewById(R.id.loginText);
        loginText.setOnClickListener(v -> {
            // Start the LoginActivity when the text is clicked
            Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        });
    }

    private void signUpUser() { // method to save the entered username and password to shared prefs
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString("username", usernameEditText.getText().toString());
        editor.putString("password", passwordEditText.getText().toString());
        editor.commit();

        Intent intent= new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

}