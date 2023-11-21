package com.example.iat359_final_project;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
    private int stepOffset = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCheckLogs = findViewById(R.id.btnCheckLogs);
        Button btnViewMap = findViewById(R.id.btnViewMap);
        Button btnStartSession = findViewById(R.id.btnStartSession);

        stepCounterTextView = (TextView) findViewById(R.id.stepCounterText);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

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
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        btnStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCounterStarted) {
                    isCounterStarted = true;
                    stepOffset = 0; // Reset the step offset
                    sensorManager.registerListener(stepListener, stepCounter, SensorManager.SENSOR_DELAY_UI);
                }
            }
        });

        stepListener = new SensorEventListener() {
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
            int totalSteps = (int) event.values[0];

            System.out.println(totalSteps);

            // Update your UI or logic with the total steps
            updateStepCountDisplay(totalSteps);
        }
    }

    private void updateStepCountDisplay(int totalSteps) {
        TextView stepCountTextView = findViewById(R.id.stepCounterText);
        stepCountTextView.setText(String.valueOf(totalSteps));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null && gyroscope != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        }
        sensorManager.registerListener(stepListener, stepCounter, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
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


//    public void startSession (View v) {
//        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI);
//    }
}