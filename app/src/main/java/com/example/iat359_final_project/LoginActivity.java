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
    public static final String DEFAULT = "not available";
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
        SharedPreferences sharedPrefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String username = sharedPrefs.getString("username", DEFAULT);
        String password = sharedPrefs.getString("password", DEFAULT);

        if (usernameEditText.getText().toString().equals(username) && passwordEditText.getText().toString().equals(password))
        {
            Toast.makeText(this, "Login Success", Toast.LENGTH_LONG).show();
            Intent intent= new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else
        {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("username", DEFAULT);
            editor.putString("password", DEFAULT);
            editor.commit();

            Toast.makeText(this, "Credentials Reset", Toast.LENGTH_LONG).show();
            Intent intent= new Intent(this, SignupActivity.class);
            startActivity(intent);
        }
    }
}