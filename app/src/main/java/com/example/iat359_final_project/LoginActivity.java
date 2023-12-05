package com.example.iat359_final_project;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LoginActivity extends AppCompatActivity {
    private EditText usernameEditText, passwordEditText;
    private TextView signUpText;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 2;
    private SharedPreferences sharedPreferences;
    public static final String DEFAULT = "not available";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check for RECORD_AUDIO permission
        if (!checkAudioPermission()) {
            requestAudioPermission();
        }

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        usernameEditText = findViewById(R.id.editTextUserLogin);
        passwordEditText = findViewById(R.id.editTextPassLogin);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> loginUser());

        signUpText = findViewById(R.id.signUpText);
        signUpText.setOnClickListener(v -> {
            // Start the SignupActivity when the text is clicked
            Intent signUpIntent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(signUpIntent);
        });
    }

    private boolean checkAudioPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestAudioPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                // Permission granted, you can proceed with your app logic
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                // Permission denied, handle accordingly (disable functionality, show a message, etc.)
            }
        }
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