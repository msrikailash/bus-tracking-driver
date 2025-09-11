package com.example.bus_traker_manager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.model.Driver;
import com.example.bus_traker_manager.model.Stop;
import com.example.bus_traker_manager.services.FirebaseService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class AddStopActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private TextInputEditText stopNameInput;
    private TextInputEditText stopCodeInput;
    private TextInputEditText descriptionInput;
    private TextInputEditText landmarkInput;
    private MaterialButton selectLocationButton;
    private MaterialButton saveButton;
    private MaterialTextView locationText;
    private MaterialTextView loadingText;
    private View progressBar;

    private FirebaseService firebaseService;
    private Driver currentDriver;
    private double selectedLatitude = 0.0;
    private double selectedLongitude = 0.0;
    private String selectedAddress = "";

    private static final int LOCATION_SELECTION_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stop);

        firebaseService = new FirebaseService();
        
        // Get driver data from intent
        currentDriver = (Driver) getIntent().getSerializableExtra("driver");
        if (currentDriver == null) {
            Toast.makeText(this, "Driver information not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        stopNameInput = findViewById(R.id.stopNameInput);
        stopCodeInput = findViewById(R.id.stopCodeInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        landmarkInput = findViewById(R.id.landmarkInput);
        selectLocationButton = findViewById(R.id.selectLocationButton);
        saveButton = findViewById(R.id.saveButton);
        locationText = findViewById(R.id.locationText);
        loadingText = findViewById(R.id.loadingText);
        progressBar = findViewById(R.id.progressBar);

        // Set default stop code based on bus number
        if (currentDriver != null && currentDriver.getBusNo() != null) {
            String defaultStopCode = currentDriver.getBusNo() + "_STOP";
            stopCodeInput.setText(defaultStopCode);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.add_new_stop_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupListeners() {
        selectLocationButton.setOnClickListener(v -> openLocationSelection());
        saveButton.setOnClickListener(v -> saveStop());
    }

    private void openLocationSelection() {
        Intent intent = new Intent(this, MapSelectionActivity.class);
        startActivityForResult(intent, LOCATION_SELECTION_REQUEST);
    }

    private void saveStop() {
        String stopName = stopNameInput.getText().toString().trim();
        String stopCode = stopCodeInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String landmark = landmarkInput.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(stopName)) {
            stopNameInput.setError(getString(R.string.stop_name_required));
            return;
        }

        if (selectedLatitude == 0.0 && selectedLongitude == 0.0) {
            Toast.makeText(this, getString(R.string.please_select_location), Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(true);

        // Create comprehensive Stop object
        Stop stop = new Stop();
        stop.setName(stopName);
        stop.setBusNo(currentDriver.getBusNo());
        stop.setStopCode(stopCode.isEmpty() ? null : stopCode);
        stop.setDescription(description.isEmpty() ? null : description);
        stop.setLandmark(landmark.isEmpty() ? null : landmark);
        stop.setStopType("pickup_drop"); // Default type
        stop.setCreatedBy(currentDriver.getDriverId());

        // Set location information
        Stop.LocationInfo locationInfo = new Stop.LocationInfo(selectedLatitude, selectedLongitude, selectedAddress);
        stop.setLocation(locationInfo);

        // Set default schedule
        Stop.ScheduleInfo scheduleInfo = new Stop.ScheduleInfo();
        scheduleInfo.setMorningPickup("07:30");
        scheduleInfo.setMorningDrop("08:15");
        scheduleInfo.setAfternoonPickup("14:30");
        scheduleInfo.setAfternoonDrop("15:15");
        scheduleInfo.setEveningPickup("17:30");
        scheduleInfo.setEveningDrop("18:15");
        stop.setSchedule(scheduleInfo);

        // Set status information
        Stop.StatusInfo statusInfo = new Stop.StatusInfo();
        statusInfo.setCurrentStatus("pending");
        statusInfo.setIsActive(true);
        stop.setStatus(statusInfo);

        // Set tracking information
        Stop.TrackingInfo trackingInfo = new Stop.TrackingInfo();
        trackingInfo.setMaxCapacity(50);
        trackingInfo.setWeatherCondition("clear");
        trackingInfo.setTrafficCondition("normal");
        stop.setTracking(trackingInfo);

        // Set metadata
        Stop.MetadataInfo metadataInfo = new Stop.MetadataInfo();
        metadataInfo.setTags(java.util.Arrays.asList("main_stop", "junction"));
        stop.setMetadata(metadataInfo);

        // Save to Firebase
        firebaseService.addStop(stop, new FirebaseService.OnSuccessListener() {
            @Override
            public void onSuccess() {
                setLoadingState(false);
                Toast.makeText(AddStopActivity.this, getString(R.string.stop_added_successfully), Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                setLoadingState(false);
                String errorMessage = getString(R.string.failed_to_add_stop, e.getMessage());
                Toast.makeText(AddStopActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        loadingText.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        saveButton.setEnabled(!isLoading);
        selectLocationButton.setEnabled(!isLoading);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == LOCATION_SELECTION_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedLatitude = data.getDoubleExtra("latitude", 0.0);
            selectedLongitude = data.getDoubleExtra("longitude", 0.0);
            selectedAddress = data.getStringExtra("address");
            
            if (selectedAddress == null || selectedAddress.isEmpty()) {
                selectedAddress = String.format("Location: %.6f, %.6f", selectedLatitude, selectedLongitude);
            }
            
            locationText.setText(selectedAddress);
            locationText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
