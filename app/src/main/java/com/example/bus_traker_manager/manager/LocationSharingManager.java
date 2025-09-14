package com.example.bus_traker_manager.manager;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.example.bus_traker_manager.model.Driver;
import com.example.bus_traker_manager.model.LocationPoint;
import com.example.bus_traker_manager.model.Route;
import com.example.bus_traker_manager.model.Route.RouteStop;
import com.example.bus_traker_manager.util.LocationUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationSharingManager {
    private static final String TAG = "LocationSharingManager";
    
    private Context context;
    private DatabaseReference databaseRef;
    private String driverId;
    private LocationUtils locationManager;
    private LocationSharingListener listener;
    
    // Location sharing state
    private boolean isSharing = false;
    private Location lastSharedLocation;
    private long lastShareTime = 0;
    private static final long SHARE_INTERVAL = 10000; // 10 seconds
    
    public interface LocationSharingListener {
        void onLocationShared(LocationPoint location);
        void onLocationShareError(String error);
        void onNearbyStopsUpdated(List<RouteStop> nearbyStops);
        void onRouteProgressUpdated(int progress, int totalStops);
    }
    
    public LocationSharingManager(Context context) {
        this.context = context;
        this.databaseRef = FirebaseDatabase.getInstance().getReference();
        this.driverId = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        this.locationManager = new LocationUtils(context);
        
        setupLocationListener();
    }
    
    public void setLocationSharingListener(LocationSharingListener listener) {
        this.listener = listener;
    }
    
    private void setupLocationListener() {
        locationManager.setLocationListener(new LocationUtils.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (isSharing) {
                    shareLocation(location);
                }
            }
            
            @Override
            public void onLocationError(String error) {
                if (listener != null) {
                    listener.onLocationShareError(error);
                }
            }
        });
    }
    
    public void startLocationSharing(Driver driver, Route route) {
        if (isSharing) {
            Log.d(TAG, "Location sharing already started");
            return;
        }
        
        if (driverId == null) {
            Log.e(TAG, "Driver ID is null, cannot start location sharing");
            if (listener != null) {
                listener.onLocationShareError("Driver not authenticated");
            }
            return;
        }
        
        if (!locationManager.checkLocationPermissions()) {
            Log.e(TAG, "Location permissions not granted");
            if (listener != null) {
                listener.onLocationShareError("Location permissions not granted");
            }
            return;
        }
        
        if (!locationManager.isLocationEnabled()) {
            Log.e(TAG, "Location services are disabled");
            if (listener != null) {
                listener.onLocationShareError("Location services are disabled");
            }
            return;
        }
        
        // Start location updates
        locationManager.startLocationUpdates();
        isSharing = true;
        
        // Update driver status
        updateDriverStatus(driver, true);
        
        // Start monitoring route progress
        if (route != null) {
            startRouteProgressMonitoring(route);
        }
        
        Log.d(TAG, "Location sharing started");
    }
    
    public void stopLocationSharing(Driver driver) {
        if (!isSharing) {
            Log.d(TAG, "Location sharing not started");
            return;
        }
        
        // Stop location updates
        locationManager.stopLocationUpdates();
        isSharing = false;
        
        // Update driver status
        updateDriverStatus(driver, false);
        
        // Clear location data
        clearLocationData();
        
        Log.d(TAG, "Location sharing stopped");
    }
    
    private void shareLocation(Location location) {
        if (!isSharing || driverId == null) {
            return;
        }
        
        // Check if enough time has passed since last share
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShareTime < SHARE_INTERVAL) {
            return;
        }
        
        // Check if location has changed significantly
        if (lastSharedLocation != null) {
            float distance = location.distanceTo(lastSharedLocation);
            if (distance < 10) { // Less than 10 meters
                return;
            }
        }
        
        // Create location point
        LocationPoint locationPoint = new LocationPoint(
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                location.getSpeed(),
                location.getBearing(),
                currentTime
        );
        
        // Upload to Firebase
        uploadLocationToFirebase(locationPoint);
        
        // Update state
        lastSharedLocation = location;
        lastShareTime = currentTime;
        
        // Notify listener
        if (listener != null) {
            listener.onLocationShared(locationPoint);
        }
        
        Log.d(TAG, "Location shared: " + location.getLatitude() + ", " + location.getLongitude());
    }
    
    private void uploadLocationToFirebase(LocationPoint locationPoint) {
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("latitude", locationPoint.getLatitude());
        locationData.put("longitude", locationPoint.getLongitude());
        locationData.put("accuracy", locationPoint.getAccuracy());
        locationData.put("speed", locationPoint.getSpeed());
        locationData.put("bearing", locationPoint.getBearing());
        locationData.put("timestamp", locationPoint.getTimestamp());
        locationData.put("isActive", true);
        
        // Update current location
        databaseRef.child("drivers").child(driverId).child("currentLocation").setValue(locationData);
        
        // Add to location history
        databaseRef.child("drivers").child(driverId).child("locationHistory")
                .push().setValue(locationData);
        
        // Update last seen timestamp
        databaseRef.child("drivers").child(driverId).child("lastSeen").setValue(System.currentTimeMillis());
    }
    
    private void updateDriverStatus(Driver driver, boolean isActive) {
        if (driverId == null) return;
        
        Map<String, Object> statusData = new HashMap<>();
        statusData.put("isActive", isActive);
        statusData.put("lastSeen", System.currentTimeMillis());
        statusData.put("isSharingLocation", isActive);
        
        databaseRef.child("drivers").child(driverId).updateChildren(statusData);
    }
    
    private void startRouteProgressMonitoring(Route route) {
        if (route == null || route.getStops() == null) return;
        
        // Monitor location updates to track route progress
        locationManager.setLocationListener(new LocationUtils.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (isSharing) {
                    shareLocation(location);
                    updateRouteProgress(route, location);
                }
            }
            
            @Override
            public void onLocationError(String error) {
                if (listener != null) {
                    listener.onLocationShareError(error);
                }
            }
        });
    }
    
    private void updateRouteProgress(Route route, Location currentLocation) {
        List<RouteStop> stops = route.getStops();
        if (stops == null || stops.isEmpty()) return;
        
        List<RouteStop> nearbyStops = new ArrayList<>();
        int completedStops = 0;
        
        for (RouteStop stop : stops) {
            double distance = LocationUtils.calculateDistance(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude(),
                    stop.getLatitude(),
                    stop.getLongitude()
            );
            
            // Consider stop as nearby if within 100 meters
            if (distance <= 100) {
                nearbyStops.add(stop);
            }
            
            // Consider stop as completed if within 50 meters
            if (distance <= 50) {
                completedStops++;
            }
        }
        
        // Notify listeners
        if (listener != null) {
            listener.onNearbyStopsUpdated(nearbyStops);
            listener.onRouteProgressUpdated(completedStops, stops.size());
        }
    }
    
    private void clearLocationData() {
        if (driverId == null) return;
        
        // Clear current location
        databaseRef.child("drivers").child(driverId).child("currentLocation").removeValue();
        
        // Update status
        Map<String, Object> statusData = new HashMap<>();
        statusData.put("isActive", false);
        statusData.put("isSharingLocation", false);
        statusData.put("lastSeen", System.currentTimeMillis());
        
        databaseRef.child("drivers").child(driverId).updateChildren(statusData);
    }
    
    public void getDriverLocation(String driverId, LocationCallback callback) {
        databaseRef.child("drivers").child(driverId).child("currentLocation")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            LocationPoint locationPoint = dataSnapshot.getValue(LocationPoint.class);
                            if (locationPoint != null) {
                                callback.onLocationReceived(locationPoint);
                            } else {
                                callback.onLocationError("Failed to parse location data");
                            }
                        } else {
                            callback.onLocationError("Driver location not available");
                        }
                    }
                    
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onLocationError("Failed to get driver location: " + databaseError.getMessage());
                    }
                });
    }
    
    public interface LocationCallback {
        void onLocationReceived(LocationPoint location);
        void onLocationError(String error);
    }
    
    public boolean isSharing() {
        return isSharing;
    }
    
    public Location getLastSharedLocation() {
        return lastSharedLocation;
    }
    
    public long getLastShareTime() {
        return lastShareTime;
    }
}
