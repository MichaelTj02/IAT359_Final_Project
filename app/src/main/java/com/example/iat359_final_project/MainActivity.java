package com.example.iat359_final_project;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepDetector;
    private Sensor accelerometer;
    private Sensor gyroscope;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCheckLogs = findViewById(R.id.btnCheckLogs);
        Button btnViewMap = findViewById(R.id.btnViewMap);
        Button btnStartSession = findViewById(R.id.btnStartSession);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
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
                // Handle click to start session
                // Implement functionality to start session and track data
                // This might involve starting a service or background operation
            }
        });
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
        if (event.sensor == stepDetector) {
            // Each event represents a step taken
            int totalSteps = (int) event.values[0];

            Log.d("StepCounter", "Total steps: " + totalSteps);

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (accelerometer != null && gyroscope != null && stepDetector != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void startSession (View v) {
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI);
    }
}