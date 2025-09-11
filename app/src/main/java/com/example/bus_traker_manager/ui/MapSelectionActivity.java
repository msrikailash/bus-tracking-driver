package com.example.bus_traker_manager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bus_traker_manager.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import android.widget.ImageButton;

public class MapSelectionActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MaterialToolbar toolbar;
    private MaterialButton confirmButton;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private ImageButton myLocationButton;
    
    private GoogleMap mMap;
    private Marker selectedMarker;
    private LatLng selectedLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_selection);
        
        initViews();
        setupToolbar();
        setupMap();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        confirmButton = findViewById(R.id.confirmButton);
        zoomInButton = findViewById(R.id.zoomInButton);
        zoomOutButton = findViewById(R.id.zoomOutButton);
        myLocationButton = findViewById(R.id.myLocationButton);
        
        confirmButton.setOnClickListener(v -> confirmLocation());
        zoomInButton.setOnClickListener(v -> zoomIn());
        zoomOutButton.setOnClickListener(v -> zoomOut());
        myLocationButton.setOnClickListener(v -> goToMyLocation());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.select_location_title));
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
        
        // Enable zoom controls and gestures
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        
        // Set default location (you can customize this)
        LatLng defaultLocation = new LatLng(17.3850, 78.4867); // Hyderabad, India
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));
        
        // Handle map clicks
        mMap.setOnMapClickListener(latLng -> {
            if (selectedMarker != null) {
                selectedMarker.remove();
            }
            
            selectedLocation = latLng;
            selectedMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.selected_location))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            
            confirmButton.setEnabled(true);
            confirmButton.setText(getString(R.string.confirm_location));
        });
        
        // Initially disable confirm button
        confirmButton.setEnabled(false);
    }

    private void confirmLocation() {
        if (selectedLocation != null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("latitude", selectedLocation.latitude);
            resultIntent.putExtra("longitude", selectedLocation.longitude);
            setResult(RESULT_OK, resultIntent);
            finish();
        } else {
            Toast.makeText(this, getString(R.string.please_select_location), Toast.LENGTH_SHORT).show();
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
    
    private void goToMyLocation() {
        if (mMap != null) {
            // Enable my location if not already enabled
            try {
                mMap.setMyLocationEnabled(true);
                // The map will automatically move to user's location when my location is enabled
            } catch (SecurityException e) {
                // Handle permission not granted
                Toast.makeText(this, getString(R.string.location_permission_required), Toast.LENGTH_SHORT).show();
            }
        }
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
