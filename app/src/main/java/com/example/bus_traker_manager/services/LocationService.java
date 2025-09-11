package com.example.bus_traker_manager.services;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationService {
    private static final String TAG = "LocationService";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final long UPDATE_INTERVAL = 5000; // 5 seconds
    private static final long FASTEST_INTERVAL = 3000; // 3 seconds

    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final FirebaseService firebaseService;
    private LocationCallback locationCallback;
    private String currentDriverId;
    private boolean isTracking = false;

    public LocationService(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.firebaseService = new FirebaseService();
    }

    public boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
               ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    public void startLocationTracking(String driverId, OnLocationListener listener) {
        if (!checkLocationPermission()) {
            listener.onError("Location permission not granted");
            return;
        }

        this.currentDriverId = driverId;
        this.isTracking = true;

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
                .setIntervalMillis(UPDATE_INTERVAL)
                .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && isTracking) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lng = location.getLongitude();
                        float accuracy = location.getAccuracy();
                        float speed = location.getSpeed();

                        // Update Firebase
                        firebaseService.updateDriverLocation(currentDriverId, lat, lng);

                        // Notify listener
                        listener.onLocationUpdate(lat, lng, accuracy, speed);
                        
                        Log.d(TAG, "Location updated: " + lat + ", " + lng + " (accuracy: " + accuracy + "m)");
                    }
                }
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Location tracking started");
                        listener.onTrackingStarted();
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to start location tracking", e);
                        listener.onError("Failed to start location tracking: " + e.getMessage());
                    });
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception when requesting location updates", e);
            listener.onError("Location permission denied");
        }
    }

    public void stopLocationTracking() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            locationCallback = null;
        }
        
        if (currentDriverId != null) {
            firebaseService.updateDriverStatus(currentDriverId, "offline");
        }
        
        isTracking = false;
        Log.d(TAG, "Location tracking stopped");
    }

    public boolean isTracking() {
        return isTracking;
    }

    public void getLastLocation(OnLocationListener listener) {
        if (!checkLocationPermission()) {
            listener.onError("Location permission not granted");
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        listener.onLocationUpdate(
                                location.getLatitude(),
                                location.getLongitude(),
                                location.getAccuracy(),
                                location.getSpeed()
                        );
                    } else {
                        listener.onError("Unable to get current location");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get last location", e);
                    listener.onError("Failed to get location: " + e.getMessage());
                });
    }

    public interface OnLocationListener {
        void onLocationUpdate(double lat, double lng, float accuracy, float speed);
        void onTrackingStarted();
        void onError(String error);
    }
}
