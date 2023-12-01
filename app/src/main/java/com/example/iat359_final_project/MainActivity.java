package com.example.iat359_final_project;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
    private int finalTotalSteps;
    private int stepOffset = 0;
    private EditText sessionTitleEditText;

    private Database db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCheckLogs = findViewById(R.id.btnCheckLogs);
        Button btnViewMap = findViewById(R.id.btnViewMap);
        Button btnStartSession = findViewById(R.id.btnStartSession);
        Button btnLocInfo = findViewById(R.id.btnViewInformation);

//        sessionTitleEditText = findViewById(R.id.sessionTitleEditText);
//
//        stepCounterTextView = (TextView) findViewById(R.id.stepCounterText);

        db = new Database(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        btnCheckLogs.setOnClickListener(new View.OnClickListener() { // set check log button onClick
            @Override
            public void onClick(View v) {
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
//                if (!isCounterStarted) {
//                    isCounterStarted = true;
//                    stepOffset = 0; // Reset the step offset
//                    sensorManager.registerListener(stepListener, stepCounter, SensorManager.SENSOR_DELAY_UI);
//                }
                Intent intent = new Intent(MainActivity.this, StepCounterActivity.class);
                startActivity(intent);
            }
        });

//        btnFinishSession.setOnClickListener(new View.OnClickListener() { // set finish session onClick
//            @Override
//            public void onClick(View v) {
//                //finishSession();
//            }
//        });

        btnLocInfo.setOnClickListener(new View.OnClickListener() { // set search info button onClick
            @Override
            public void onClick(View v) {
                performWebSearch(); // Replace with actual location data if available
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor == accelerometer) {
//            // Handle accelerometer data
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//            // Process accelerometer data for step detection
//        } else if (event.sensor == gyroscope) {
//            // Handle gyroscope data
//            float x = event.values[0];
//            float y = event.values[1];
//            float z = event.values[2];
//            // Process gyroscope data for step detection
//        }
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

    protected void updateStepCountDisplay(int totalSteps) { // update text based on steps
        stepCounterTextView.setText(String.valueOf(totalSteps));
        System.out.println(totalSteps);
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

//    private void finishSession() {
//        if (isCounterStarted) {
//            isCounterStarted = false;
//            int totalFinishSessionSteps = getCurrentSteps();
//            String sessionTitle = sessionTitleEditText.getText().toString();
//
//            String location = "Vancouver";
//            db.insertData(location, String.valueOf(totalFinishSessionSteps));
//            //db.insertData(location, sessionTitle, String.valueOf(totalFinishSessionSteps));
//
//            stepCounterTextView.setText("Session finished. Steps: " + totalFinishSessionSteps);
//            sensorManager.unregisterListener(stepListener);
//            resetSteps();
//        }
//    }

    private void resetSteps() {
        totalSteps = 0;
        finalTotalSteps = 0;
    }

    private void performWebSearch(String query) {
        System.out.println("search");
        Uri searchUri = Uri.parse("https://www.google.com/search?q=" + Uri.encode(query));
        Intent intent = new Intent(Intent.ACTION_VIEW, searchUri);
        startActivity(intent);
    }

    private void performWebSearch() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        String city = getCityFromLocation(latitude, longitude);

                        // Search the city on Google
                        String query = "https://www.google.com/search?q=" + city;
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(query));
                        startActivity(intent);
                    }
                });
    }

    private String getCityFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this);
        String city = "";

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                city = addresses.get(0).getLocality();
                // You can also retrieve more information like country, postal code, etc. from the Address object
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return city;
    }


//    public void startSession (View v) {
//        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI);
//    }
}