package com.example.iat359_final_project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class StepCounterActivity extends AppCompatActivity implements SensorEventListener {
    private static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 1;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private Sensor stepCounter;
    private SensorEventListener stepListener;
    private TextView stepCounterTextView;
    private boolean isCounterStarted = false;
    private int totalSteps;
    private int finalTotalSteps;
    private int stepOffset = 0;
    private Database db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        //stepCounterTextView = (TextView) findViewById(R.id.counterText);

        db = new Database(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

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
                        totalSteps = currentSteps;
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // Handle sensor accuracy changes if needed
            }
        };

        // ask user permission for step counter feature
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION);
        }

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
            if (isCounterStarted) {
                if (stepOffset == 0) {
                    stepOffset = (int) event.values[0];
                }
                int currentSteps = (int) event.values[0] - stepOffset;
                finalTotalSteps = currentSteps; // Update finalTotalSteps continuously
                stepCounterTextView.setText("Steps: " + currentSteps);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle changes in sensor accuracy if needed
    }


    private int getCurrentSteps() {
        return totalSteps;
    }

    private void finishSession() {
        if (isCounterStarted) {
            isCounterStarted = false;
            int totalFinishSessionSteps = getCurrentSteps();
            // Save to database
//            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(Va));
            String location = "Vancouver";
            db.insertData(location, String.valueOf(totalFinishSessionSteps));

            stepCounterTextView.setText("Session finished. Steps: " + totalFinishSessionSteps);
            sensorManager.unregisterListener(stepListener);
            resetSteps();
        }
    }

    private void resetSteps() {
        totalSteps = 0;
        finalTotalSteps = 0;
    }
}