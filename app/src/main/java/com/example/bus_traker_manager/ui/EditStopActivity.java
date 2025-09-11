package com.example.bus_traker_manager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.model.Stop;
import com.example.bus_traker_manager.services.FirebaseService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditStopActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private TextInputEditText stopNameInput;
    private MaterialButton selectLocationButton;
    private MaterialButton saveButton;
    
    private FirebaseService firebaseService;
    private Stop currentStop;
    private double selectedLat = 0.0;
    private double selectedLng = 0.0;
    private boolean locationChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stop);
        
        currentStop = (Stop) getIntent().getSerializableExtra("stop");
        if (currentStop == null) {
            Toast.makeText(this, getString(R.string.stop_info_not_provided), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        firebaseService = new FirebaseService();
        selectedLat = currentStop.getStopLat();
        selectedLng = currentStop.getStopLng();
        
        initViews();
        setupToolbar();
        setupListeners();
        populateFields();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        stopNameInput = findViewById(R.id.stopNameInput);
        selectLocationButton = findViewById(R.id.selectLocationButton);
        saveButton = findViewById(R.id.saveButton);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.edit_stop_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupListeners() {
        selectLocationButton.setOnClickListener(v -> openMapSelection());
        saveButton.setOnClickListener(v -> saveStop());
    }

    private void populateFields() {
        stopNameInput.setText(currentStop.getStopName());
        selectLocationButton.setText(String.format(getString(R.string.location_format), selectedLat, selectedLng));
    }

    private void openMapSelection() {
        Intent intent = new Intent(this, MapSelectionActivity.class);
        startActivityForResult(intent, 1001);
    }

    private void saveStop() {
        String stopName = stopNameInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(stopName)) {
            stopNameInput.setError(getString(R.string.stop_name_required));
            stopNameInput.requestFocus();
            return;
        }
        
        // Show loading state
        saveButton.setEnabled(false);
                    saveButton.setText(getString(R.string.saving));
        
        // Create updated stop object
        currentStop.setName(stopName);
        if (currentStop.getLocation() == null) {
            currentStop.setLocation(new Stop.LocationInfo(selectedLat, selectedLng, ""));
        } else {
            currentStop.getLocation().setLatitude(selectedLat);
            currentStop.getLocation().setLongitude(selectedLng);
        }
        currentStop.setUpdatedAt(System.currentTimeMillis());
        
        firebaseService.updateStop(currentStop, new FirebaseService.OnSuccessListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(EditStopActivity.this, 
                    getString(R.string.stop_updated_successfully), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                saveButton.setEnabled(true);
                saveButton.setText(getString(R.string.save_changes));
                Toast.makeText(EditStopActivity.this, 
                    getString(R.string.failed_to_update_stop, e.getMessage()), 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            selectedLat = data.getDoubleExtra("latitude", selectedLat);
            selectedLng = data.getDoubleExtra("longitude", selectedLng);
            locationChanged = true;
            
            selectLocationButton.setText(String.format(getString(R.string.location_format), selectedLat, selectedLng));
            selectLocationButton.setIconResource(R.drawable.ic_check);
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
