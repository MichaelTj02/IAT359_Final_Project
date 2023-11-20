package com.example.iat359_final_project;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

public class StepCountingService extends Service implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private int stepCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
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
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] values = event.values;
            float x = values[0];
            float y = values[1];
            float z = values[2];

            // Simple step counting logic (for illustration purposes)
            if (isStepDetected(x, y, z)) {
                stepCount++;
                // Update your database or UI with the new step count
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle changes in sensor accuracy if needed
    }

    // Custom method to detect a step (simplified, a more complex algorithm is usually needed)
    private boolean isStepDetected(float x, float y, float z) {
        // Add your step detection algorithm here
        // This is a simple example, not an accurate step detection algorithm
        // You may need to implement more sophisticated logic
        // For example, detecting peaks in acceleration changes
        return (Math.abs(x) > 10 || Math.abs(y) > 10 || Math.abs(z) > 10);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
