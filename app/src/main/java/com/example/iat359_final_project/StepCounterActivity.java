package com.example.iat359_final_project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StepCounterActivity extends AppCompatActivity implements SensorEventListener, OnMapReadyCallback {
    private static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 1; // permission for step counter
    private SensorManager sensorManager;
    private Sensor stepCounter;
    private MapView mapView;
    private GoogleMap mMap;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private SensorEventListener stepListener;
    private TextView stepCounterTextView;
    private boolean isCounterStarted = false;
    private int totalSteps;
    private int finalTotalSteps;
    private int stepOffset = 0;
    private EditText sessionTitleEditText;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 123;
    private static final float DEFAULT_ZOOM_LEVEL = 15f;
    private Database db;
    private Polyline currentPolyline;
    private List<LatLng> pathCoordinates = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        Button finish = findViewById(R.id.btnFinish);

        EditText titleText = findViewById(R.id.sessionTitleEditText);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }

        stepCounterTextView = (TextView) findViewById(R.id.stepCounterText);

        db = new Database(this);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        checkLocationPermission();

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

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishSession();
                Intent intent = new Intent(StepCounterActivity.this, MainActivity.class);
                startActivity(intent);
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
        mapView.onResume();

        if (!isCounterStarted) {
            isCounterStarted = true;
            stepOffset = 0; // Reset the step offset
            sensorManager.registerListener(stepListener, stepCounter, SensorManager.SENSOR_DELAY_UI);
        }

        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister listener when activity is onPause
        sensorManager.unregisterListener(stepListener);
        mapView.onPause();

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

            // Check for location permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Get current location
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                String city = getCityFromLocation(latitude, longitude);

                                EditText sessionTitleEditText = findViewById(R.id.sessionTitleEditText);
                                String sessionTitle = sessionTitleEditText.getText().toString();

                                // Insert data into the database along with the session title and city
                                db.insertData(sessionTitle, city, String.valueOf(totalFinishSessionSteps));

                                stepCounterTextView.setText("Session finished. Steps: " + totalFinishSessionSteps);
                                sensorManager.unregisterListener(stepListener);

                                // Draw the user's path on the map (Assuming 'pathCoordinates' contains LatLng objects)
                                if (!pathCoordinates.isEmpty() && mMap != null) {
                                    mMap.addPolyline(new PolylineOptions()
                                            .addAll(pathCoordinates)
                                            .width(12)
                                            .color(Color.BLUE)
                                            .geodesic(true));
                                }
                                resetSteps();
                            }
                        });
            } else {
                // Handle the case where permissions are not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    private void resetSteps() {
        totalSteps = 0;
        finalTotalSteps = 0;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                if (isCounterStarted) {
                    pathCoordinates.add(latLng);
                    if (currentPolyline != null) {
                        currentPolyline.remove(); // Remove the existing line before adding a new segment
                    }
                    currentPolyline = mMap.addPolyline(new PolylineOptions()
                            .addAll(pathCoordinates)
                            .width(12)
                            .color(Color.RED)
                            .geodesic(true));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            showUserLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showUserLocation();
                if (mMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Handle permissions if required
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Log.d("StepCounterActivity", "Permission denied");
            }
        }
    }

    private void showUserLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("StepCounterActivity", "Location permission denied");
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null && mMap != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng userLatLng = new LatLng(latitude, longitude);

                mMap.addMarker(new MarkerOptions().position(userLatLng).title("You are here"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, DEFAULT_ZOOM_LEVEL));
            } else {
                Log.d("StepCounterActivity", "Location is null or mMap is null");
            }
        }).addOnFailureListener(this, e -> {
            Log.e("StepCounterActivity", "Error getting location", e);
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


    // Method to stop location updates
    private void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    // Create a LocationRequest for location updates
    private LocationRequest getLocationRequest() {
        return LocationRequest.create()
                .setInterval(5000) // Update interval in milliseconds (e.g., every 5 seconds)
                .setFastestInterval(2000) // Fastest update interval
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // Method to update the path on the map
    private void updatePathOnMap() {
        if (mMap != null && !pathCoordinates.isEmpty()) {
            if (currentPolyline != null) {
                currentPolyline.remove(); // Remove the existing line before adding a new segment
            }
            currentPolyline = mMap.addPolyline(new PolylineOptions()
                    .addAll(pathCoordinates)
                    .width(12)
                    .color(Color.RED)
                    .geodesic(true));
        }
    }

    // This method will receive location updates and update the path
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    if (locationResult.getLastLocation() != null) {
                        LatLng updatedLatLng = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                        pathCoordinates.add(updatedLatLng);
                        updatePathOnMap();
                    }
                }
            };

            fusedLocationClient.requestLocationUpdates(getLocationRequest(), locationCallback, null);
        }
    }


}