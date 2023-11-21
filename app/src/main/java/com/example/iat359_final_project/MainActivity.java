package com.example.iat359_final_project;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 1; // permission for step counter
    private SensorManager sensorManager;
    private Sensor stepCounter;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private SensorEventListener stepListener;
    private TextView stepCounterTextView;
    private boolean isCounterStarted = false;
    private int totalSteps;
    private int stepOffset = 0;

    private Database db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCheckLogs = findViewById(R.id.btnCheckLogs);
        Button btnViewMap = findViewById(R.id.btnViewMap);
        Button btnStartSession = findViewById(R.id.btnStartSession);
        Button btnFinishSession = findViewById(R.id.btnFinishSession);
        Button btnLocInfo = findViewById(R.id.btnInfo);

        stepCounterTextView = (TextView) findViewById(R.id.stepCounterText);

        db = new Database(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        btnCheckLogs.setOnClickListener(new View.OnClickListener() { // set check log button onClick
            @Override
            public void onClick(View v) {
                // Handle click to check logs
                startActivity(new Intent(MainActivity.this, ViewLogsActivity.class));
            }
        });

        btnViewMap.setOnClickListener(new View.OnClickListener() { // set map button onClick
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        btnStartSession.setOnClickListener(new View.OnClickListener() { // set start session onClick
            @Override
            public void onClick(View v) {
                if (!isCounterStarted) {
                    isCounterStarted = true;
                    stepOffset = 0; // Reset the step offset
                    sensorManager.registerListener(stepListener, stepCounter, SensorManager.SENSOR_DELAY_UI);
                }
            }
        });

        btnFinishSession.setOnClickListener(new View.OnClickListener() { // set finish session onClick
            @Override
            public void onClick(View v) {
                finishSession();
            }
        });

        btnLocInfo.setOnClickListener(new View.OnClickListener() { // set search info button onClick
            @Override
            public void onClick(View v) {
                performWebSearch("Vancouver"); // Replace with actual location data if available
            }
        });


        stepListener = new SensorEventListener() { // step counter sensor listener
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
                    if (isCounterStarted) {
                        if (stepOffset == 0) {
                            // Initialize step offset
                            stepOffset = (int) event.values[0];
                        }
                        // Calculate current steps since the button was pressed
                        int currentSteps = (int) event.values[0] - stepOffset;
                        stepCounterTextView.setText("Steps: " + currentSteps);
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Handle sensor accuracy changes if needed
            }
        };

        // ask user permission for step counter feature
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            // Handle accelerometer data
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            // Process accelerometer data for step detection
        } else if (event.sensor == gyroscope) {
            // Handle gyroscope data
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            // Process gyroscope data for step detection
        }
        if (event.sensor == stepCounter) {
            // Each event represents a step taken
            totalSteps = (int) event.values[0];

            System.out.println(totalSteps);

            // Update UI
            updateStepCountDisplay(totalSteps);
        }
    }

    private void updateStepCountDisplay(int totalSteps) { // update text based on steps
        stepCounterTextView.setText(String.valueOf(totalSteps));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register various sensor listeners
        if (accelerometer != null && gyroscope != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
        sensorManager.registerListener(stepListener, stepCounter, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister listeners when activity is onPause
        if (accelerometer != null && gyroscope != null) {
            sensorManager.unregisterListener(this);
        }
        sensorManager.unregisterListener(stepListener);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        // request user permission for steps counter
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, now you can access the step counter sensor
                } else {
                    // Permission denied, you can't access the step counter sensor
                }
                return;
            }
        }
    }

    private int getCurrentSteps() {
        return totalSteps;
    }

    private void finishSession() {
        if (isCounterStarted) {
            isCounterStarted = false;
            int totalSteps = getCurrentSteps();
            // Save to database
//            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(Va));
            String location = "Vancouver";
            db.insertData("Vancouver", String.valueOf(totalSteps));

            stepCounterTextView.setText("Session finished. Steps: " + totalSteps);
            sensorManager.unregisterListener(stepListener);
            resetSteps();
        }
    }

    private void resetSteps() {
        totalSteps = 0;
    }

    private void performWebSearch(String query) {
        System.out.println("search");
        Uri searchUri = Uri.parse("https://www.google.com/search?q=" + Uri.encode(query));
        Intent intent = new Intent(Intent.ACTION_VIEW, searchUri);
        startActivity(intent);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        }
    }


//    public void startSession (View v) {
//        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI);
//    }
}