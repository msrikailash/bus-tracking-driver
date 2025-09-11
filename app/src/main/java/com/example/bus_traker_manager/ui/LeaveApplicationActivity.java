package com.example.bus_traker_manager.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.model.Leave;
import com.example.bus_traker_manager.services.FirebaseService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LeaveApplicationActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private TextInputEditText reasonInput;
    private MaterialButton dateButton;
    private MaterialButton submitButton;
    private MaterialTextView selectedDateText;
    
    private FirebaseService firebaseService;
    private String driverId;
    private String driverName;
    private String busNo;
    private Date selectedDate;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_application);
        
        driverId = getIntent().getStringExtra("driverId");
        driverName = getIntent().getStringExtra("driverName");
        busNo = getIntent().getStringExtra("busNo");
        
        if (driverId == null || driverName == null || busNo == null) {
            Toast.makeText(this, getString(R.string.driver_info_not_provided), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        firebaseService = new FirebaseService();
        calendar = Calendar.getInstance();
        
        initViews();
        setupToolbar();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        reasonInput = findViewById(R.id.reasonInput);
        dateButton = findViewById(R.id.dateButton);
        submitButton = findViewById(R.id.submitButton);
        selectedDateText = findViewById(R.id.selectedDateText);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.apply_leave_title));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupListeners() {
        dateButton.setOnClickListener(v -> showDatePicker());
        submitButton.setOnClickListener(v -> submitLeave());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    
                    selectedDate = calendar.getTime();
                    updateDateDisplay();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        if (selectedDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(selectedDate);
            selectedDateText.setText(formattedDate);
            selectedDateText.setVisibility(android.view.View.VISIBLE);
        }
    }

    private void submitLeave() {
        String reason = reasonInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(reason)) {
            reasonInput.setError(getString(R.string.provide_leave_reason));
            reasonInput.requestFocus();
            return;
        }
        
        if (selectedDate == null) {
            Toast.makeText(this, getString(R.string.select_leave_date), Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading state
        submitButton.setEnabled(false);
                    submitButton.setText(getString(R.string.submitting));
        
        firebaseService.applyLeave(driverId, reason, selectedDate, driverName, busNo, new FirebaseService.OnLeaveListener() {
            @Override
            public void onSuccess(Leave leave) {
                Toast.makeText(LeaveApplicationActivity.this, 
                    getString(R.string.leave_submitted_successfully), Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(Exception e) {
                submitButton.setEnabled(true);
                submitButton.setText(getString(R.string.submit_application));
                Toast.makeText(LeaveApplicationActivity.this, 
                    getString(R.string.failed_to_submit_leave, e.getMessage()), 
                    Toast.LENGTH_LONG).show();
            }
        });
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
