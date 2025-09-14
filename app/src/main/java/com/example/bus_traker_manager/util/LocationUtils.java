package com.example.bus_traker_manager.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LocationUtils {
    private static final String TAG = "LocationManager";
    private static final long UPDATE_INTERVAL = 10000; // 10 seconds
    private static final long FASTEST_INTERVAL = 5000; // 5 seconds
    private static final float SMALLEST_DISPLACEMENT = 10.0f; // 10 meters
    
    private Context context;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationListener listener;
    
    public interface LocationListener {
        void onLocationChanged(Location location);
        void onLocationError(String error);
    }
    
    public LocationUtils(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }
    
    public void setLocationListener(LocationListener listener) {
        this.listener = listener;
    }
    
    public boolean checkLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) 
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    
    public void getLastKnownLocation() {
        if (!checkLocationPermissions()) {
            if (listener != null) {
                listener.onLocationError("Location permissions not granted");
            }
            return;
        }
        
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null && listener != null) {
                            listener.onLocationChanged(location);
                        } else {
                            if (listener != null) {
                                listener.onLocationError("No last known location available");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to get last known location", e);
                        if (listener != null) {
                            listener.onLocationError("Failed to get location: " + e.getMessage());
                        }
                    });
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception while getting last known location", e);
            if (listener != null) {
                listener.onLocationError("Location permission denied");
            }
        }
    }
    
    public void startLocationUpdates() {
        if (!checkLocationPermissions()) {
            if (listener != null) {
                listener.onLocationError("Location permissions not granted");
            }
            return;
        }
        
        if (!isLocationEnabled()) {
            if (listener != null) {
                listener.onLocationError("Location services are disabled");
            }
            return;
        }
        
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL)
                .setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                
                for (Location location : locationResult.getLocations()) {
                    if (listener != null) {
                        listener.onLocationChanged(location);
                    }
                }
            }
        };
        
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            Log.d(TAG, "Location updates started");
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception while starting location updates", e);
            if (listener != null) {
                listener.onLocationError("Location permission denied");
            }
        }
    }
    
    public void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            locationCallback = null;
            Log.d(TAG, "Location updates stopped");
        }
    }
    
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }
    
    public static String formatLocation(Location location) {
        if (location == null) return "Unknown";
        return String.format("%.6f, %.6f", location.getLatitude(), location.getLongitude());
    }
    
    public static String formatDistance(double distanceInMeters) {
        if (distanceInMeters < 1000) {
            return String.format("%.0f m", distanceInMeters);
        } else {
            return String.format("%.2f km", distanceInMeters / 1000);
        }
    }
    
    public static String formatSpeed(float speedInMps) {
        if (speedInMps < 0) return "0 km/h";
        float speedInKmph = speedInMps * 3.6f;
        return String.format("%.1f km/h", speedInKmph);
    }
    
    public static String formatBearing(float bearing) {
        if (bearing < 0) return "Unknown";
        
        String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
                              "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        
        int index = (int) ((bearing + 11.25) / 22.5) % 16;
        return directions[index];
    }
}
