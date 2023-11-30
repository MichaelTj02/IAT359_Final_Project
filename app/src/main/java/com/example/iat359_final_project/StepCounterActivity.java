package com.example.iat359_final_project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class StepCounterActivity extends AppCompatActivity implements SensorEventListener {
    private static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 1; // permission for step counter
    private SensorManager sensorManager;
    private Sensor stepCounter;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private SensorEventListener stepListener;
    private TextView stepCounterTextView;
    private boolean isCounterStarted = false;
    private int totalSteps;
    private int finalTotalSteps;
    private int stepOffset = 0;
    private EditText sessionTitleEditText;

    private Database db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        Button btnFinishSession = findViewById(R.id.btnFinishSession);


        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        stepCounterTextView = (TextView) findViewById(R.id.stepCounterText);

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

        btnFinishSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishSession();
            }
        });

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
        // register sensor listener
        sensorManager.registerListener(stepListener, stepCounter, SensorManager.SENSOR_DELAY_UI);

        if (!isCounterStarted) {
            isCounterStarted = true;
            stepOffset = 0; // Reset the step offset
            sensorManager.registerListener(stepListener, stepCounter, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister listener when activity is onPause
        sensorManager.unregisterListener(stepListener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
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

    }
    

    private int getCurrentSteps() {
        return totalSteps;
    }

    private void finishSession() {
        if (isCounterStarted) {
            isCounterStarted = false;
            int totalFinishSessionSteps = getCurrentSteps();
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