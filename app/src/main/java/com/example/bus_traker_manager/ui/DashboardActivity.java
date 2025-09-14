package com.example.bus_traker_manager.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.model.Driver;
import com.example.bus_traker_manager.services.FirebaseService;
import com.example.bus_traker_manager.services.LocationService;
import com.example.bus_traker_manager.services.AnalyticsService;
import com.example.bus_traker_manager.services.PerformanceMonitoringService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.TextView;
import android.widget.ImageView;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int QR_SCAN_REQUEST_CODE = 1002;
    
    private MaterialToolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialCardView profileCard;
    private MaterialTextView driverNameText;
    private MaterialTextView busNumberText;
    private MaterialTextView statusText;
    private MaterialButton trackingButton;
    private MaterialCardView stopsButton, routeButton, leaveButton, logoutButton, qrScanButton;
    
    // Navigation drawer header views
    private TextView navHeaderDriverName;
    private TextView navHeaderDriverId;
    private TextView navHeaderBusNumber;
    private ImageView navHeaderProfileImage;
    
    private FirebaseService firebaseService;
    private LocationService locationService;
    private AnalyticsService analyticsService;
    private PerformanceMonitoringService performanceService;
    private Driver currentDriver;
    private boolean isTracking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        
        firebaseService = new FirebaseService();
        locationService = new LocationService(this);
        
        initViews();
        setupToolbar();
        checkAuthentication();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        profileCard = findViewById(R.id.profileCard);
        driverNameText = findViewById(R.id.driverNameText);
        busNumberText = findViewById(R.id.busNumberText);
        statusText = findViewById(R.id.statusText);
        trackingButton = findViewById(R.id.trackingButton);
        stopsButton = findViewById(R.id.stopsButton);
        routeButton = findViewById(R.id.routeButton);
        leaveButton = findViewById(R.id.leaveButton);
        logoutButton = findViewById(R.id.logoutButton);
        qrScanButton = findViewById(R.id.qrScanButton);
        
        // Initialize navigation drawer header views
        View headerView = navigationView.getHeaderView(0);
        navHeaderDriverName = headerView.findViewById(R.id.navHeaderDriverName);
        navHeaderDriverId = headerView.findViewById(R.id.navHeaderDriverId);
        navHeaderBusNumber = headerView.findViewById(R.id.navHeaderBusNumber);
        navHeaderProfileImage = headerView.findViewById(R.id.navHeaderProfileImage);
        
        setupClickListeners();
        setupNavigationDrawer();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.driver_dashboard_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }
    }

    private void setupClickListeners() {
        trackingButton.setOnClickListener(v -> toggleTracking());
        stopsButton.setOnClickListener(v -> openStopsManagement());
        routeButton.setOnClickListener(v -> openRouteMap());
        leaveButton.setOnClickListener(v -> openLeaveApplication());
        logoutButton.setOnClickListener(v -> showLogoutDialog());
        qrScanButton.setOnClickListener(v -> openQRScanner());
    }

    private void setupNavigationDrawer() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            // Close drawer first
            drawerLayout.closeDrawers();
            
            if (itemId == R.id.nav_dashboard) {
                // Already on dashboard
                return true;
            } else if (itemId == R.id.nav_route_map) {
                openRouteMap();
                return true;
            } else if (itemId == R.id.nav_stops_management) {
                openStopsManagement();
                return true;
            } else if (itemId == R.id.nav_trip_history) {
                openTripHistory();
                return true;
            } else if (itemId == R.id.nav_leave_application) {
                openLeaveApplication();
                return true;
            } else if (itemId == R.id.nav_qr_scanner) {
                openQRScanner();
                return true;
            } else if (itemId == R.id.nav_map_selection) {
                openMapSelection();
                return true;
            } else if (itemId == R.id.nav_settings) {
                openSettings();
                return true;
            } else if (itemId == R.id.nav_notifications) {
                openNotifications();
                return true;
            } else if (itemId == R.id.nav_help) {
                openHelp();
                return true;
            } else if (itemId == R.id.nav_about) {
                openAbout();
                return true;
            } else if (itemId == R.id.nav_profile) {
                openProfile();
                return true;
            } else if (itemId == R.id.nav_logout) {
                showLogoutDialog();
                return true;
            }
            
            return false;
        });
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void checkAuthentication() {
        FirebaseUser user = firebaseService.getCurrentUser();
        if (user == null) {
            navigateToLogin();
            return;
        }
        
        loadDriverData(user.getUid());
    }

    private void loadDriverData(String driverId) {
        Log.d("DashboardActivity", "Loading driver data for ID: " + driverId);
        
        firebaseService.getDriverData(driverId, new FirebaseService.OnDriverListener() {
            @Override
            public void onSuccess(Driver driver) {
                Log.d("DashboardActivity", "Driver data loaded successfully: " + driver.getName());
                currentDriver = driver;
                updateUI();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("DashboardActivity", "Failed to load driver data: " + e.getMessage());
                Toast.makeText(DashboardActivity.this, 
                    "Driver data not found. Please contact administrator", 
                    Toast.LENGTH_LONG).show();
                // Don't navigate to login immediately, let user see the error
            }
        });
    }

    private void updateUI() {
        if (currentDriver != null) {
            driverNameText.setText(currentDriver.getName());
            busNumberText.setText(getString(R.string.bus_format, currentDriver.getBusNo()));
            statusText.setText(getString(R.string.status_format, currentDriver.getStatus()));
            
            // Update navigation drawer header
            updateNavigationDrawerHeader();
            
            // Update tracking button based on current status
            isTracking = "online".equals(currentDriver.getStatus());
            updateTrackingButton();
            
            // Enable navigation buttons
            enableNavigationButtons(true);
        } else {
            // Show loading state
            driverNameText.setText("Loading...");
            busNumberText.setText("Loading...");
            statusText.setText("Loading...");
            
            // Update navigation drawer header with loading state
            if (navHeaderDriverName != null) {
                navHeaderDriverName.setText("Loading...");
                navHeaderDriverId.setText("Loading...");
                navHeaderBusNumber.setText("Loading...");
            }
            
            // Disable navigation buttons
            enableNavigationButtons(false);
        }
    }
    
    private void updateNavigationDrawerHeader() {
        if (navHeaderDriverName != null && currentDriver != null) {
            navHeaderDriverName.setText(currentDriver.getName());
            navHeaderDriverId.setText("ID: " + currentDriver.getDriverId());
            navHeaderBusNumber.setText("Bus: " + currentDriver.getBusNo());
        }
    }

    private void enableNavigationButtons(boolean enable) {
        stopsButton.setEnabled(enable);
        routeButton.setEnabled(enable);
        leaveButton.setEnabled(enable);
        qrScanButton.setEnabled(enable);
        
        // Change alpha to show disabled state
        float alpha = enable ? 1.0f : 0.5f;
        stopsButton.setAlpha(alpha);
        routeButton.setAlpha(alpha);
        leaveButton.setAlpha(alpha);
        qrScanButton.setAlpha(alpha);
    }

    private void updateTrackingButton() {
        if (isTracking) {
            trackingButton.setText(getString(R.string.stop_tracking));
            trackingButton.setIconResource(R.drawable.ic_stop);
        } else {
            trackingButton.setText(getString(R.string.start_tracking));
            trackingButton.setIconResource(R.drawable.ic_play);
        }
    }

    private void toggleTracking() {
        if (currentDriver == null) {
            Toast.makeText(this, getString(R.string.driver_data_not_loaded), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!locationService.checkLocationPermission()) {
            requestLocationPermission();
            return;
        }

        if (isTracking) {
            stopTracking();
        } else {
            startTracking();
        }
    }

    private void startTracking() {
        locationService.startLocationTracking(currentDriver.getDriverId(), new LocationService.OnLocationListener() {
            @Override
            public void onLocationUpdate(double lat, double lng, float accuracy, float speed) {
                // Location updates are handled automatically by LocationService
            }

            @Override
            public void onTrackingStarted() {
                isTracking = true;
                currentDriver.setStatus("online");
                updateUI();
                Toast.makeText(DashboardActivity.this, getString(R.string.gps_tracking_started), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(DashboardActivity.this, "Tracking error: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void stopTracking() {
        locationService.stopLocationTracking();
        isTracking = false;
        currentDriver.setStatus("offline");
        updateUI();
        Toast.makeText(this, getString(R.string.gps_tracking_stopped), Toast.LENGTH_SHORT).show();
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void openStopsManagement() {
        Log.d("DashboardActivity", "Opening stops management. Current driver: " + (currentDriver != null ? currentDriver.getName() : "null"));
        
        if (currentDriver == null) {
            Toast.makeText(this, "Driver data not loaded. Please wait...", Toast.LENGTH_SHORT).show();
            // Try to reload driver data
            FirebaseUser user = firebaseService.getCurrentUser();
            if (user != null) {
                loadDriverData(user.getUid());
            }
            return;
        }
        
        try {
            Intent intent = new Intent(this, StopsManagementActivity.class);
            intent.putExtra("busNo", currentDriver.getBusNo());
            startActivity(intent);
            Log.d("DashboardActivity", "Successfully started StopsManagementActivity");
        } catch (Exception e) {
            Log.e("DashboardActivity", "Error starting StopsManagementActivity: " + e.getMessage());
            Toast.makeText(this, "Error opening stops management: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openRouteMap() {
        Log.d("DashboardActivity", "Opening route map. Current driver: " + (currentDriver != null ? currentDriver.getName() : "null"));
        
        if (currentDriver == null) {
            Toast.makeText(this, "Driver data not loaded. Please wait...", Toast.LENGTH_SHORT).show();
            // Try to reload driver data
            FirebaseUser user = firebaseService.getCurrentUser();
            if (user != null) {
                loadDriverData(user.getUid());
            }
            return;
        }
        
        try {
            Intent intent = new Intent(this, RouteMapActivity.class);
            intent.putExtra("busNo", currentDriver.getBusNo());
            startActivity(intent);
            Log.d("DashboardActivity", "Successfully started RouteMapActivity");
        } catch (Exception e) {
            Log.e("DashboardActivity", "Error starting RouteMapActivity: " + e.getMessage());
            Toast.makeText(this, "Error opening route map: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openLeaveApplication() {
        Log.d("DashboardActivity", "Opening leave application. Current driver: " + (currentDriver != null ? currentDriver.getName() : "null"));
        
        if (currentDriver == null) {
            Toast.makeText(this, "Driver data not loaded. Please wait...", Toast.LENGTH_SHORT).show();
            // Try to reload driver data
            FirebaseUser user = firebaseService.getCurrentUser();
            if (user != null) {
                loadDriverData(user.getUid());
            }
            return;
        }
        
        try {
            Intent intent = new Intent(this, LeaveApplicationActivity.class);
            intent.putExtra("driverId", currentDriver.getDriverId());
            intent.putExtra("driverName", currentDriver.getName());
            intent.putExtra("busNo", currentDriver.getBusNo());
            startActivity(intent);
            Log.d("DashboardActivity", "Successfully started LeaveApplicationActivity");
        } catch (Exception e) {
            Log.e("DashboardActivity", "Error starting LeaveApplicationActivity: " + e.getMessage());
            Toast.makeText(this, "Error opening leave application: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("No", null)
                .show();
    }

    private void logout() {
        if (isTracking) {
            stopTracking();
        }
        firebaseService.signOut();
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, can start tracking
                if (currentDriver != null) {
                    startTracking();
                }
            } else {
                Toast.makeText(this, getString(R.string.location_permission_required_for_tracking), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    private void openQRScanner() {
        Log.d("DashboardActivity", "Opening QR scanner. Current driver: " + (currentDriver != null ? currentDriver.getName() : "null"));
        
        if (currentDriver == null) {
            Toast.makeText(this, "Driver data not loaded. Please wait...", Toast.LENGTH_SHORT).show();
            // Try to reload driver data
            FirebaseUser user = firebaseService.getCurrentUser();
            if (user != null) {
                loadDriverData(user.getUid());
            }
            return;
        }
        
        try {
            Intent intent = new Intent(this, BusQRScannerActivity.class);
            intent.putExtra("driver", currentDriver);
            startActivityForResult(intent, QR_SCAN_REQUEST_CODE);
            Log.d("DashboardActivity", "Successfully started BusQRScannerActivity");
        } catch (Exception e) {
            Log.e("DashboardActivity", "Error starting BusQRScannerActivity: " + e.getMessage());
            Toast.makeText(this, "Error opening QR scanner: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == QR_SCAN_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getBooleanExtra("busAssigned", false)) {
                String busNumber = data.getStringExtra("busNumber");
                String busId = data.getStringExtra("busId");
                
                Toast.makeText(this, "Bus " + busNumber + " assigned successfully!", Toast.LENGTH_LONG).show();
                
                // Reload driver data to get updated bus information
                FirebaseUser user = firebaseService.getCurrentUser();
                if (user != null) {
                    loadDriverData(user.getUid());
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isTracking) {
            locationService.stopLocationTracking();
        }
    }
    
    // Navigation drawer methods
    private void openTripHistory() {
        Intent intent = new Intent(this, TripHistoryActivity.class);
        startActivity(intent);
    }
    
    private void openMapSelection() {
        Intent intent = new Intent(this, MapSelectionActivity.class);
        startActivity(intent);
    }
    
    private void openNotifications() {
        Toast.makeText(this, "Notifications feature coming soon", Toast.LENGTH_SHORT).show();
    }
    
    private void openHelp() {
        Toast.makeText(this, "Help feature coming soon", Toast.LENGTH_SHORT).show();
    }
    
    private void openAbout() {
        Toast.makeText(this, "About feature coming soon", Toast.LENGTH_SHORT).show();
    }
    
    private void openProfile() {
        Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == android.R.id.home) {
            drawerLayout.openDrawer(navigationView);
            return true;
        } else if (itemId == R.id.action_location_sharing) {
            openLocationSharing();
            return true;
        } else if (itemId == R.id.action_notifications) {
            openNotifications();
            return true;
        } else if (itemId == R.id.action_settings) {
            openSettings();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void openLocationSharing() {
        Intent intent = new Intent(this, LocationSharingActivity.class);
        startActivity(intent);
    }
}
