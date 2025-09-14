package com.example.bus_traker_manager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.example.bus_traker_manager.model.LocationPoint;
import com.example.bus_traker_manager.service.LocationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationUpdateReceiver extends BroadcastReceiver {
    private static final String TAG = "LocationUpdateReceiver";
    
    public static final String ACTION_LOCATION_UPDATE = "com.example.bus_traker_manager.LOCATION_UPDATE";
    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_LATITUDE = "latitude";
    public static final String EXTRA_LONGITUDE = "longitude";
    public static final String EXTRA_ACCURACY = "accuracy";
    public static final String EXTRA_SPEED = "speed";
    public static final String EXTRA_BEARING = "bearing";
    public static final String EXTRA_TIMESTAMP = "timestamp";
    
    private DatabaseReference locationRef;
    
    public LocationUpdateReceiver() {
        // Initialize Firebase reference
        String driverId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        
        if (driverId != null) {
            locationRef = FirebaseDatabase.getInstance()
                    .getReference("drivers")
                    .child(driverId)
                    .child("currentLocation");
        }
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !ACTION_LOCATION_UPDATE.equals(intent.getAction())) {
            return;
        }
        
        Log.d(TAG, "Received location update broadcast");
        
        // Extract location data from intent
        double latitude = intent.getDoubleExtra(EXTRA_LATITUDE, 0.0);
        double longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0);
        float accuracy = intent.getFloatExtra(EXTRA_ACCURACY, 0.0f);
        float speed = intent.getFloatExtra(EXTRA_SPEED, 0.0f);
        float bearing = intent.getFloatExtra(EXTRA_BEARING, 0.0f);
        long timestamp = intent.getLongExtra(EXTRA_TIMESTAMP, System.currentTimeMillis());
        
        // Validate location data
        if (latitude == 0.0 && longitude == 0.0) {
            Log.w(TAG, "Invalid location data received");
            return;
        }
        
        // Create LocationPoint object
        LocationPoint locationPoint = new LocationPoint(
                latitude,
                longitude,
                accuracy,
                speed,
                bearing,
                timestamp
        );
        
        // Upload to Firebase
        uploadLocationToFirebase(locationPoint);
        
        // Log location update
        Log.d(TAG, String.format("Location updated: %.6f, %.6f (accuracy: %.1fm)", 
                latitude, longitude, accuracy));
    }
    
    private void uploadLocationToFirebase(LocationPoint locationPoint) {
        if (locationRef == null) {
            Log.e(TAG, "Firebase location reference is null");
            return;
        }
        
        locationRef.setValue(locationPoint)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Location uploaded to Firebase successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload location to Firebase", e);
                });
    }
    
    public static void sendLocationUpdate(Context context, Location location) {
        Intent intent = new Intent(ACTION_LOCATION_UPDATE);
        intent.putExtra(EXTRA_LATITUDE, location.getLatitude());
        intent.putExtra(EXTRA_LONGITUDE, location.getLongitude());
        intent.putExtra(EXTRA_ACCURACY, location.getAccuracy());
        intent.putExtra(EXTRA_SPEED, location.getSpeed());
        intent.putExtra(EXTRA_BEARING, location.getBearing());
        intent.putExtra(EXTRA_TIMESTAMP, System.currentTimeMillis());
        
        context.sendBroadcast(intent);
    }
    
    public static void sendLocationUpdate(Context context, LocationPoint locationPoint) {
        Intent intent = new Intent(ACTION_LOCATION_UPDATE);
        intent.putExtra(EXTRA_LATITUDE, locationPoint.getLatitude());
        intent.putExtra(EXTRA_LONGITUDE, locationPoint.getLongitude());
        intent.putExtra(EXTRA_ACCURACY, locationPoint.getAccuracy());
        intent.putExtra(EXTRA_SPEED, locationPoint.getSpeed());
        intent.putExtra(EXTRA_BEARING, locationPoint.getBearing());
        intent.putExtra(EXTRA_TIMESTAMP, locationPoint.getTimestamp());
        
        context.sendBroadcast(intent);
    }
}
