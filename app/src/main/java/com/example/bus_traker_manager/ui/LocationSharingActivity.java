package com.example.bus_traker_manager.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.manager.LocationSharingManager;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationSharingActivity extends AppCompatActivity implements LocationSharingManager.LocationSharingListener {
    private static final String TAG = "LocationSharingActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private LocationSharingManager locationSharingManager;
    private Driver currentDriver;
    private Route currentRoute;
    
    // UI Components
    private Switch switchLocationSharing;
    private TextView textLocationStatus;
    private TextView textCurrentLocation;
    private TextView textLastUpdate;
    private TextView textAccuracy;
    private TextView textSpeed;
    private TextView textBearing;
    private TextView textNearbyStops;
    private TextView textRouteProgress;
    private Button buttonRefreshLocation;
    private Button buttonViewOnMap;
    private Button buttonViewHistory;
    
    private SimpleDateFormat dateFormat;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_sharing);
        
        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Location Sharing");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        // Initialize components
        initializeViews();
        initializeLocationSharing();
        loadDriverData();
        
        dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    }
    
    private void initializeViews() {
        switchLocationSharing = findViewById(R.id.switch_location_sharing);
        textLocationStatus = findViewById(R.id.text_location_status);
        textCurrentLocation = findViewById(R.id.text_current_location);
        textLastUpdate = findViewById(R.id.text_last_update);
        textAccuracy = findViewById(R.id.text_accuracy);
        textSpeed = findViewById(R.id.text_speed);
        textBearing = findViewById(R.id.text_bearing);
        textNearbyStops = findViewById(R.id.text_nearby_stops);
        textRouteProgress = findViewById(R.id.text_route_progress);
        buttonRefreshLocation = findViewById(R.id.button_refresh_location);
        buttonViewOnMap = findViewById(R.id.button_view_on_map);
        buttonViewHistory = findViewById(R.id.button_view_history);
        
        // Setup listeners
        switchLocationSharing.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startLocationSharing();
            } else {
                stopLocationSharing();
            }
        });
        
        buttonRefreshLocation.setOnClickListener(v -> refreshLocation());
        buttonViewOnMap.setOnClickListener(v -> viewLocationOnMap());
        buttonViewHistory.setOnClickListener(v -> viewLocationHistory());
    }
    
    private void initializeLocationSharing() {
        locationSharingManager = new LocationSharingManager(this);
        locationSharingManager.setLocationSharingListener(this);
    }
    
    private void loadDriverData() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference driverRef = FirebaseDatabase.getInstance()
                .getReference("drivers")
                .child(driverId);
        
        driverRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentDriver = dataSnapshot.getValue(Driver.class);
                    if (currentDriver != null) {
                        loadRouteData(currentDriver.getRouteId());
                    }
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LocationSharingActivity.this, 
                        "Failed to load driver data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void loadRouteData(String routeId) {
        if (routeId == null) return;
        
        DatabaseReference routeRef = FirebaseDatabase.getInstance()
                .getReference("routes")
                .child(routeId);
        
        routeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentRoute = dataSnapshot.getValue(Route.class);
                }
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LocationSharingActivity.this, 
                        "Failed to load route data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void startLocationSharing() {
        if (!checkLocationPermissions()) {
            requestLocationPermissions();
            return;
        }
        
        if (currentDriver == null) {
            Toast.makeText(this, "Driver data not loaded", Toast.LENGTH_SHORT).show();
            return;
        }
        
        locationSharingManager.startLocationSharing(currentDriver, currentRoute);
        updateUI();
    }
    
    private void stopLocationSharing() {
        if (currentDriver != null) {
            locationSharingManager.stopLocationSharing(currentDriver);
        }
        updateUI();
    }
    
    private void refreshLocation() {
        if (locationSharingManager.isSharing()) {
            // Get last known location
            Location lastLocation = locationSharingManager.getLastSharedLocation();
            if (lastLocation != null) {
                updateLocationDisplay(lastLocation);
            } else {
                Toast.makeText(this, "No location data available", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Location sharing is not active", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void viewLocationOnMap() {
        Location lastLocation = locationSharingManager.getLastSharedLocation();
        if (lastLocation != null) {
            Intent intent = new Intent(this, RouteMapActivity.class);
            intent.putExtra("latitude", lastLocation.getLatitude());
            intent.putExtra("longitude", lastLocation.getLongitude());
            startActivity(intent);
        } else {
            Toast.makeText(this, "No location data available", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void viewLocationHistory() {
        Intent intent = new Intent(this, LocationHistoryActivity.class);
        startActivity(intent);
    }
    
    private boolean checkLocationPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (switchLocationSharing.isChecked()) {
                    startLocationSharing();
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
                switchLocationSharing.setChecked(false);
            }
        }
    }
    
    private void updateUI() {
        boolean isSharing = locationSharingManager.isSharing();
        
        switchLocationSharing.setChecked(isSharing);
        textLocationStatus.setText(isSharing ? "Sharing Location" : "Not Sharing");
        textLocationStatus.setTextColor(ContextCompat.getColor(this, 
                isSharing ? android.R.color.holo_green_dark : android.R.color.holo_red_dark));
        
        if (isSharing) {
            Location lastLocation = locationSharingManager.getLastSharedLocation();
            if (lastLocation != null) {
                updateLocationDisplay(lastLocation);
            }
        } else {
            clearLocationDisplay();
        }
    }
    
    private void updateLocationDisplay(Location location) {
        textCurrentLocation.setText(LocationUtils.formatLocation(location));
        textLastUpdate.setText(dateFormat.format(new Date()));
        textAccuracy.setText(String.format("±%.1f m", location.getAccuracy()));
        textSpeed.setText(LocationUtils.formatSpeed(location.getSpeed()));
        textBearing.setText(LocationUtils.formatBearing(location.getBearing()));
    }
    
    private void clearLocationDisplay() {
        textCurrentLocation.setText("Not available");
        textLastUpdate.setText("--:--:--");
        textAccuracy.setText("--");
        textSpeed.setText("--");
        textBearing.setText("--");
        textNearbyStops.setText("No nearby stops");
        textRouteProgress.setText("0/0 stops completed");
    }
    
    // LocationSharingListener implementation
    @Override
    public void onLocationShared(LocationPoint location) {
        runOnUiThread(() -> {
            textCurrentLocation.setText(LocationUtils.formatLocation(
                    new Location("") {{
                        setLatitude(location.getLatitude());
                        setLongitude(location.getLongitude());
                    }}
            ));
            textLastUpdate.setText(dateFormat.format(new Date(location.getTimestamp())));
            textAccuracy.setText(String.format("±%.1f m", location.getAccuracy()));
            textSpeed.setText(LocationUtils.formatSpeed(location.getSpeed()));
            textBearing.setText(LocationUtils.formatBearing(location.getBearing()));
        });
    }
    
    @Override
    public void onLocationShareError(String error) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Location error: " + error, Toast.LENGTH_SHORT).show();
            switchLocationSharing.setChecked(false);
        });
    }
    
    @Override
    public void onNearbyStopsUpdated(List<RouteStop> nearbyStops) {
        runOnUiThread(() -> {
            if (nearbyStops.isEmpty()) {
                textNearbyStops.setText("No nearby stops");
            } else {
                StringBuilder stopsText = new StringBuilder();
                for (int i = 0; i < Math.min(nearbyStops.size(), 3); i++) {
                    if (i > 0) stopsText.append(", ");
                    stopsText.append(nearbyStops.get(i).getName());
                }
                if (nearbyStops.size() > 3) {
                    stopsText.append("...");
                }
                textNearbyStops.setText(stopsText.toString());
            }
        });
    }
    
    @Override
    public void onRouteProgressUpdated(int progress, int totalStops) {
        runOnUiThread(() -> {
            textRouteProgress.setText(String.format("%d/%d stops completed", progress, totalStops));
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationSharingManager != null) {
            locationSharingManager.setLocationSharingListener(null);
        }
    }
}
