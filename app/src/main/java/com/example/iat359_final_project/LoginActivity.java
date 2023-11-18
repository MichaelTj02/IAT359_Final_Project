package com.example.iat359_final_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        usernameEditText = findViewById(R.id.editTextUserLogin);
        passwordEditText = findViewById(R.id.editTextPassLogin);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        String storedPassword = sharedPreferences.getString(username, "");

        if (password.equals(storedPassword) && !storedPassword.isEmpty()) {
            // Passwords match, proceed to main activity or desired screen
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        } else {
            // Incorrect credentials
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }
}