package com.example.bus_traker_manager.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.model.Stop;
import com.example.bus_traker_manager.services.FirebaseService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.button.MaterialButton;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class RouteMapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MaterialToolbar toolbar;
    private CardView routeInfoCard;
    private TextView routeInfoText;
    private FloatingActionButton centerMapButton;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private MaterialButton assignBusButton;
    
    private GoogleMap mMap;
    private FirebaseService firebaseService;
    private String busNo;
    private List<Stop> stopsList;
    private List<Marker> stopMarkers;
    private Polyline routePolyline;
    private Marker busLocationMarker;
    private Marker startPointMarker;
    private Marker endPointMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);
        
        busNo = getIntent().getStringExtra("busNo");
        if (busNo == null || busNo.isEmpty()) {
            Toast.makeText(this, "No bus assigned to driver. Please scan a QR code to assign a bus first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        // Check if bus number is valid (not "N/A" or similar)
        if (busNo.equals("N/A") || busNo.equals("Not Assigned")) {
            Toast.makeText(this, "Driver not assigned to any bus. Please scan a QR code to assign a bus first.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        
        firebaseService = new FirebaseService();
        stopsList = new ArrayList<>();
        stopMarkers = new ArrayList<>();
        
        initViews();
        setupToolbar();
        setupMap();
        loadStops();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        routeInfoCard = findViewById(R.id.routeInfoCard);
        routeInfoText = findViewById(R.id.routeInfoText);
        centerMapButton = findViewById(R.id.centerMapButton);
        zoomInButton = findViewById(R.id.zoomInButton);
        zoomOutButton = findViewById(R.id.zoomOutButton);
        assignBusButton = findViewById(R.id.assignBusButton);
        
        // Set initial route info
        updateRouteInfo();
        
        // Center map button click listener
        centerMapButton.setOnClickListener(v -> centerMapOnRoute());
        
        // Zoom control button click listeners
        zoomInButton.setOnClickListener(v -> zoomIn());
        zoomOutButton.setOnClickListener(v -> zoomOut());
        
        // Assign bus button click listener
        assignBusButton.setOnClickListener(v -> openQRScanner());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.bus_route_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        
        // Set default location (you can customize this)
        LatLng defaultLocation = new LatLng(17.3850, 78.4867); // Hyderabad, India
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
        
        // Enable zoom gestures but disable default zoom controls (we have custom ones)
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        
        // Set map click listener to show stop info
        mMap.setOnMarkerClickListener(marker -> {
            showStopInfo(marker);
            return true;
        });
    }

    private void loadStops() {
        // Show loading message
        Toast.makeText(this, "Loading stops for bus: " + busNo, Toast.LENGTH_SHORT).show();
        
        firebaseService.getStopsForBus(busNo, new FirebaseService.OnStopsListener() {
            @Override
            public void onSuccess(List<Stop> stops) {
                stopsList.clear();
                stopsList.addAll(stops);
                
                if (stops.isEmpty()) {
                    Toast.makeText(RouteMapActivity.this, 
                        "No stops found for bus " + busNo + ". Please add stops first.", 
                        Toast.LENGTH_LONG).show();
                    updateRouteInfo();
                } else {
                    Toast.makeText(RouteMapActivity.this, 
                        "Loaded " + stops.size() + " stops for bus " + busNo, 
                        Toast.LENGTH_SHORT).show();
                    displayStopsOnMap();
                    updateRouteInfo();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(RouteMapActivity.this, 
                    "Failed to load stops: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
                Log.e("RouteMapActivity", "Error loading stops for bus " + busNo, e);
            }
        });
    }

    private void displayStopsOnMap() {
        if (mMap == null || stopsList.isEmpty()) {
            return;
        }
        
        // Clear existing markers and polyline
        for (Marker marker : stopMarkers) {
            marker.remove();
        }
        stopMarkers.clear();
        
        if (routePolyline != null) {
            routePolyline.remove();
        }
        
        if (busLocationMarker != null) {
            busLocationMarker.remove();
        }
        
        if (startPointMarker != null) {
            startPointMarker.remove();
        }
        
        if (endPointMarker != null) {
            endPointMarker.remove();
        }
        
        // Add markers for each stop
        PolylineOptions polylineOptions = new PolylineOptions()
                .color(Color.parseColor("#2196F3")) // Blue color
                .width(8f)
                .geodesic(true);
        
        for (int i = 0; i < stopsList.size(); i++) {
            Stop stop = stopsList.get(i);
            LatLng latLng = new LatLng(stop.getStopLat(), stop.getStopLng());
            
            // Create custom marker with stop number
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(stop.getName())
                    .snippet(getString(R.string.stop_number, i + 1))
                    .icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(i)));
            
            // Add additional info to marker
            if (stop.getStopCode() != null && !stop.getStopCode().isEmpty()) {
                String snippet = String.format("%s - %s", 
                    stop.getStopCode(), 
                    stop.getDescription() != null ? stop.getDescription() : "");
                markerOptions.snippet(snippet);
            }
            
            Marker marker = mMap.addMarker(markerOptions);
            stopMarkers.add(marker);
            polylineOptions.add(latLng);
        }
        
        // Add start point marker (first stop)
        if (!stopsList.isEmpty()) {
            Stop firstStop = stopsList.get(0);
            LatLng startLocation = new LatLng(firstStop.getStopLat(), firstStop.getStopLng());
            startPointMarker = mMap.addMarker(new MarkerOptions()
                    .position(startLocation)
                    .title(getString(R.string.route_start) + ": " + firstStop.getName())
                    .snippet(getString(R.string.starting_point))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        
        // Add end point marker (last stop)
        if (stopsList.size() > 1) {
            Stop lastStop = stopsList.get(stopsList.size() - 1);
            LatLng endLocation = new LatLng(lastStop.getStopLat(), lastStop.getStopLng());
            endPointMarker = mMap.addMarker(new MarkerOptions()
                    .position(endLocation)
                    .title(getString(R.string.route_end) + ": " + lastStop.getName())
                    .snippet(getString(R.string.final_destination))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        }
        
        // Draw route polyline
        if (polylineOptions.getPoints().size() > 1) {
            routePolyline = mMap.addPolyline(polylineOptions);
        }
        
        // Add bus location marker (current position) - initially hidden until real location is available
        // This will be updated when actual bus location is received
        if (!stopsList.isEmpty()) {
            // For now, place it at the start point, but it will be updated with real location
            LatLng startLocation = new LatLng(stopsList.get(0).getStopLat(), stopsList.get(0).getStopLng());
            busLocationMarker = mMap.addMarker(new MarkerOptions()
                    .position(startLocation)
                    .title(getString(R.string.bus_current_location))
                    .snippet("Bus location will be updated when tracking starts")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            
            // Initially hide the bus marker until we get real location data
            busLocationMarker.setVisible(false);
        }
        
        // Fit camera to show all stops
        if (!stopsList.isEmpty()) {
            fitCameraToShowAllStops();
        }
    }
    
    private void updateBusLocation(double lat, double lng) {
        if (busLocationMarker != null) {
            LatLng newLocation = new LatLng(lat, lng);
            busLocationMarker.setPosition(newLocation);
            busLocationMarker.setVisible(true); // Show the bus marker when location is available
            
            // Find the nearest stop
            Stop nearestStop = findNearestStop(lat, lng);
            if (nearestStop != null) {
                busLocationMarker.setSnippet(getString(R.string.next_stop_info, nearestStop.getName()));
            }
        }
    }
    
    private Stop findNearestStop(double lat, double lng) {
        if (stopsList.isEmpty()) return null;
        
        Stop nearestStop = stopsList.get(0);
        double minDistance = Double.MAX_VALUE;
        
        for (Stop stop : stopsList) {
            double distance = calculateDistance(lat, lng, stop.getStopLat(), stop.getStopLng());
            if (distance < minDistance) {
                minDistance = distance;
                nearestStop = stop;
            }
        }
        
        return nearestStop;
    }

    private float getMarkerColor(int stopIndex) {
        // Use different colors for different stops
        float[] colors = {
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_YELLOW
        };
        return colors[stopIndex % colors.length];
    }

    private void fitCameraToShowAllStops() {
        if (stopsList.isEmpty()) return;
        
        // Calculate bounds
        double minLat = Double.MAX_VALUE, maxLat = Double.MIN_VALUE;
        double minLng = Double.MAX_VALUE, maxLng = Double.MIN_VALUE;
        
        for (Stop stop : stopsList) {
            minLat = Math.min(minLat, stop.getStopLat());
            maxLat = Math.max(maxLat, stop.getStopLat());
            minLng = Math.min(minLng, stop.getStopLng());
            maxLng = Math.max(maxLng, stop.getStopLng());
        }
        
        // Add padding
        double latPadding = (maxLat - minLat) * 0.1;
        double lngPadding = (maxLng - minLng) * 0.1;
        
        LatLng southwest = new LatLng(minLat - latPadding, minLng - lngPadding);
        LatLng northeast = new LatLng(maxLat + latPadding, maxLng + lngPadding);
        
        // Fit camera to bounds
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
            new com.google.android.gms.maps.model.LatLngBounds(southwest, northeast), 100));
    }

    private void centerMapOnRoute() {
        if (!stopsList.isEmpty()) {
            fitCameraToShowAllStops();
        }
    }
    
    private void zoomIn() {
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
        }
    }
    
    private void zoomOut() {
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.zoomOut());
        }
    }
    
    private void openQRScanner() {
        // Navigate back to dashboard to scan QR code
        Toast.makeText(this, "Please go to Dashboard and scan QR code to assign a bus", Toast.LENGTH_LONG).show();
        finish();
    }

    private void showStopInfo(Marker marker) {
        // Find the stop for this marker
        for (int i = 0; i < stopMarkers.size(); i++) {
            if (stopMarkers.get(i).equals(marker)) {
                Stop stop = stopsList.get(i);
                showStopDetails(stop, i + 1);
                break;
            }
        }
    }

    private void showStopDetails(Stop stop, int stopNumber) {
        String stopInfo = String.format("%s\n%s: %s\n%s: %s\n%s: %s", 
            stop.getName(),
            getString(R.string.stop_number_label), stopNumber,
            getString(R.string.stop_code_label), stop.getStopCode() != null ? stop.getStopCode() : "N/A",
            getString(R.string.description_label), stop.getDescription() != null ? stop.getDescription() : "N/A"
        );
        
        Toast.makeText(this, stopInfo, Toast.LENGTH_LONG).show();
    }

    private void updateRouteInfo() {
        if (routeInfoText != null && assignBusButton != null) {
            if (stopsList.isEmpty()) {
                String noStopsMessage = String.format("Bus %s: No stops configured\n\nPlease add stops to this bus route first.", busNo);
                routeInfoText.setText(noStopsMessage);
                routeInfoCard.setVisibility(View.VISIBLE);
                assignBusButton.setVisibility(View.VISIBLE);
            } else {
                double totalDistance = calculateTotalRouteDistance();
                int estimatedTime = calculateEstimatedTime(totalDistance);
                
                String startPoint = stopsList.get(0).getName();
                String endPoint = stopsList.get(stopsList.size() - 1).getName();
                
                String info = String.format("%s: %s\n%s: %d\n%s: %.1f km\n%s: %d min\n%s: %s\n%s: %s\n%s: %s", 
                    getString(R.string.bus_number_label), busNo,
                    getString(R.string.total_stops_label), stopsList.size(),
                    getString(R.string.route_distance), totalDistance,
                    getString(R.string.estimated_duration), estimatedTime,
                    getString(R.string.route_status_label), getString(R.string.route_active),
                    getString(R.string.start_point), startPoint,
                    getString(R.string.end_point), endPoint
                );
                routeInfoText.setText(info);
                routeInfoCard.setVisibility(View.VISIBLE);
                assignBusButton.setVisibility(View.GONE);
            }
        }
    }
    
    private double calculateTotalRouteDistance() {
        if (stopsList.size() < 2) return 0.0;
        
        double totalDistance = 0.0;
        for (int i = 0; i < stopsList.size() - 1; i++) {
            Stop currentStop = stopsList.get(i);
            Stop nextStop = stopsList.get(i + 1);
            
            double distance = calculateDistance(
                currentStop.getStopLat(), currentStop.getStopLng(),
                nextStop.getStopLat(), nextStop.getStopLng()
            );
            totalDistance += distance;
        }
        
        return totalDistance;
    }
    
    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        // Haversine formula to calculate distance between two points
        final double R = 6371; // Earth's radius in kilometers
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    private int calculateEstimatedTime(double distanceKm) {
        // Assume average speed of 30 km/h in urban areas
        double averageSpeedKmh = 30.0;
        double timeHours = distanceKm / averageSpeedKmh;
        return (int) Math.round(timeHours * 60); // Convert to minutes
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
