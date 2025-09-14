package com.example.bus_traker_manager.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.services.FirebaseService;
import com.example.bus_traker_manager.model.Driver;
import com.example.bus_traker_manager.model.LocationPoint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationService extends Service implements LocationListener {
    private static final String TAG = "LocationService";
    private static final String CHANNEL_ID = "location_tracking_channel";
    private static final int NOTIFICATION_ID = 1001;
    
    // Location update intervals
    private static final long MIN_TIME_BW_UPDATES = 5000; // 5 seconds
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    
    private LocationManager locationManager;
    private FirebaseService firebaseService;
    private DatabaseReference locationRef;
    private Driver currentDriver;
    
    private boolean isTracking = false;
    private Location lastKnownLocation;
    private long lastUpdateTime = 0;
    
    // Binder for activity communication
    private final IBinder binder = new LocationBinder();
    
    public class LocationBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "LocationService created");
        
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        firebaseService = new FirebaseService();
        
        // Initialize Firebase location reference
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        locationRef = FirebaseDatabase.getInstance()
                .getReference("drivers")
                .child(driverId)
                .child("currentLocation");
        
        createNotificationChannel();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "LocationService started");
        
        if (intent != null) {
            String action = intent.getAction();
            if ("START_TRACKING".equals(action)) {
                startLocationTracking();
            } else if ("STOP_TRACKING".equals(action)) {
                stopLocationTracking();
            }
        }
        
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationTracking();
        Log.d(TAG, "LocationService destroyed");
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Tracking",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Tracks bus location in real-time");
            channel.setShowBadge(false);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, com.example.bus_traker_manager.ui.DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Bus Location Tracking")
                .setContentText("Tracking bus location in real-time")
                .setSmallIcon(R.drawable.ic_location)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
    
    public void startLocationTracking() {
        if (isTracking) {
            Log.d(TAG, "Location tracking already started");
            return;
        }
        
        if (!checkLocationPermissions()) {
            Log.e(TAG, "Location permissions not granted");
            return;
        }
        
        try {
            // Start foreground service
            startForeground(NOTIFICATION_ID, createNotification());
            
            // Request location updates from GPS
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this,
                        Looper.getMainLooper()
                );
            }
            
            // Request location updates from Network
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                        this,
                        Looper.getMainLooper()
                );
            }
            
            isTracking = true;
            Log.d(TAG, "Location tracking started");
            
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception while starting location tracking", e);
        }
    }
    
    public void stopLocationTracking() {
        if (!isTracking) {
            Log.d(TAG, "Location tracking not started");
            return;
        }
        
        try {
            locationManager.removeUpdates(this);
            isTracking = false;
            
            // Stop foreground service
            stopForeground(true);
            stopSelf();
            
            Log.d(TAG, "Location tracking stopped");
            
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception while stopping location tracking", e);
        }
    }
    
    private boolean checkLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d(TAG, "Location changed: " + location.getLatitude() + ", " + location.getAccuracy());
        
        // Update location if it's more accurate or significantly different
        if (lastKnownLocation == null || 
            location.getAccuracy() < lastKnownLocation.getAccuracy() ||
            location.distanceTo(lastKnownLocation) > MIN_DISTANCE_CHANGE_FOR_UPDATES) {
            
            lastKnownLocation = location;
            lastUpdateTime = System.currentTimeMillis();
            
            // Upload location to Firebase
            uploadLocationToFirebase(location);
            
            // Update notification
            updateNotification(location);
        }
    }
    
    private void uploadLocationToFirebase(Location location) {
        if (locationRef == null) return;
        
        LocationPoint locationPoint = new LocationPoint(
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                location.getSpeed(),
                location.getBearing(),
                System.currentTimeMillis()
        );
        
        locationRef.setValue(locationPoint)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Location uploaded to Firebase successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to upload location to Firebase", e);
                });
    }
    
    private void updateNotification(Location location) {
        String locationText = String.format(Locale.getDefault(), 
                "Lat: %.6f, Lng: %.6f", 
                location.getLatitude(), 
                location.getLongitude());
        
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Bus Location Tracking")
                .setContentText(locationText)
                .setSmallIcon(R.drawable.ic_location)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
    
    @Override
    public void onProviderEnabled(@NonNull String provider) {
        Log.d(TAG, "Provider enabled: " + provider);
    }
    
    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.d(TAG, "Provider disabled: " + provider);
    }
    
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "Provider status changed: " + provider + " - " + status);
    }
    
    // Public methods for activity communication
    public boolean isTracking() {
        return isTracking;
    }
    
    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }
    
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    public void setCurrentDriver(Driver driver) {
        this.currentDriver = driver;
    }
    
    // Static methods for starting/stopping service
    public static void startLocationTracking(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction("START_TRACKING");
        context.startService(intent);
    }
    
    public static void stopLocationTracking(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction("STOP_TRACKING");
        context.startService(intent);
    }
}
