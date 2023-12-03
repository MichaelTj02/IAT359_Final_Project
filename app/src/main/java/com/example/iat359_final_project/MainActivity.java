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
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 1;
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 2;
    private SensorManager sensorManager;
    private Sensor stepCounter;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private SensorEventListener stepListener;
    private TextView stepCounterTextView;
    private SpeechRecognizer speechRecognizer;
    private boolean isCounterStarted = false;
    private int totalSteps;
    private int finalTotalSteps;
    private int stepOffset = 0;
    private Database db;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCheckLogs = findViewById(R.id.btnCheckLogs);
        Button btnViewMap = findViewById(R.id.btnViewMap);
        Button btnStartSession = findViewById(R.id.btnStartSession);
        Button btnLocInfo = findViewById(R.id.btnViewInformation);

//        sessionTitleEditText = findViewById(R.id.sessionTitleEditText);

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
                Intent intent = new Intent(MainActivity.this, StepCounterActivity.class);
                startActivity(intent);
            }
        });
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

        // For Activity Recognition
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION);

// For Record Audio
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                startListening();
            }

            @Override
            public void onError(int error) {
                startListening();
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String spokenText = matches.get(0); // Get the first recognized speech
                    processVoiceCommand(spokenText); // Process the recognized text
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        startVoiceRecognition();

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
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister listeners when activity is onPause
        if (accelerometer != null && gyroscope != null) {
            sensorManager.unregisterListener(this);
        }
        sensorManager.unregisterListener(stepListener);

        speechRecognizer.stopListening();
//        if (speechRecognizer != null) {
//
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION:
                // Handle activity recognition permission result
                break;
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO:
                // Handle record audio permission result
                break;
            // Other cases for additional permissions if needed
        }
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
                        String query = "https://www.google.com/search?q=" + city + " weather today";
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


    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something");
        speechRecognizer.startListening(intent);
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something");
        speechRecognizer.startListening(intent);
    }

    private void processVoiceCommand(String spokenText) {
        spokenText = spokenText.toLowerCase(); // Convert the spoken text to lowercase for easier comparison

        // Check for specific commands or keywords and launch corresponding activities
        if (spokenText.contains("check logs")) {
            startActivity(new Intent(MainActivity.this, ViewLogsActivity.class));
        } else if (spokenText.contains("view map")) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        } else if (spokenText.contains("start session")) {
            Intent intent = new Intent(MainActivity.this, StepCounterActivity.class);
            startActivity(intent);
        } else if (spokenText.contains("view information")) {
            performWebSearch(); // Invoke the method you've implemented for searching information
        }
        else {
            // Handle unrecognized commands or provide feedback to the user
            Toast.makeText(this, "Command not recognized", Toast.LENGTH_SHORT).show();
        }
    }


}