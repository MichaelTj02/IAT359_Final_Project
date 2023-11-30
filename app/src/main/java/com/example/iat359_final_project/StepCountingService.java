package com.example.iat359_final_project;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.TextView;

public class StepCountingService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 1;
    private Sensor stepCounter;
    private SensorEventListener stepListener;
    private TextView stepCounterTextView;
    private boolean isCounterStarted = false;
    private int totalSteps;
    private int finalTotalSteps;
    private int stepOffset = 0;
    private Database db;

    private int stepCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Process accelerometer data for step detection
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            stepCount = (int) event.values[0];
            // Update your database or UI with the new step count
            // You may send broadcast or use interfaces to notify your activities about the step count changes
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle changes in sensor accuracy if needed
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
