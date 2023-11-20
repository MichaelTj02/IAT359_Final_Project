package com.example.iat359_final_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCheckLogs = findViewById(R.id.btnCheckLogs);
        Button btnViewMap = findViewById(R.id.btnViewMap);
        Button btnStartSession = findViewById(R.id.btnStartSession);

        btnCheckLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click to check logs
                startActivity(new Intent(MainActivity.this, ViewLogsActivity.class));
            }
        });

        btnViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click to view map
                // Implement this functionality when the map activity is implemented
            }
        });

        btnStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click to start session
                // Implement functionality to start session and track data
                // This might involve starting a service or background operation
            }
        });
    }
}