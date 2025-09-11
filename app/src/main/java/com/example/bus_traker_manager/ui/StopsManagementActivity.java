package com.example.bus_traker_manager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.model.Stop;
import com.example.bus_traker_manager.services.FirebaseService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class StopsManagementActivity extends AppCompatActivity {
    private MaterialToolbar toolbar;
    private RecyclerView stopsRecyclerView;
    private FloatingActionButton addStopFab;
    
    private FirebaseService firebaseService;
    private StopsAdapter stopsAdapter;
    private List<Stop> stopsList;
    private String busNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops_management);
        
        busNo = getIntent().getStringExtra("busNo");
        if (busNo == null) {
            Toast.makeText(this, getString(R.string.bus_number_not_provided), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        firebaseService = new FirebaseService();
        stopsList = new ArrayList<>();
        
        initViews();
        setupToolbar();
        setupRecyclerView();
        loadStops();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        stopsRecyclerView = findViewById(R.id.stopsRecyclerView);
        addStopFab = findViewById(R.id.addStopFab);
        
        addStopFab.setOnClickListener(v -> openAddStopDialog());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.manage_stops));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        stopsAdapter = new StopsAdapter(stopsList, new StopsAdapter.OnStopClickListener() {
            @Override
            public void onStopClick(Stop stop) {
                // Show stop details or navigate to stop details activity
                Toast.makeText(StopsManagementActivity.this, "Stop: " + stop.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditStop(Stop stop) {
                Intent intent = new Intent(StopsManagementActivity.this, EditStopActivity.class);
                intent.putExtra("stop", stop);
                intent.putExtra("busNo", busNo); // Pass busNo to EditStopActivity
                startActivity(intent);
            }

            @Override
            public void onDeleteStop(Stop stop) {
                new AlertDialog.Builder(StopsManagementActivity.this)
                        .setTitle(getString(R.string.delete_stop_title))
                        .setMessage(String.format(getString(R.string.delete_stop_message), stop.getName()))
                        .setPositiveButton("Delete", (dialog, which) -> {
                            deleteStop(stop);
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        
        stopsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stopsRecyclerView.setAdapter(stopsAdapter);
    }

    private void loadStops() {
        firebaseService.getStopsForBus(busNo, new FirebaseService.OnStopsListener() {
            @Override
            public void onSuccess(List<Stop> stops) {
                stopsList.clear();
                stopsList.addAll(stops);
                stopsAdapter.notifyDataSetChanged();
                
                if (stops.isEmpty()) {
                    // Show empty state
                    findViewById(R.id.emptyStateLayout).setVisibility(View.VISIBLE);
                    stopsRecyclerView.setVisibility(View.GONE);
                } else {
                    findViewById(R.id.emptyStateLayout).setVisibility(View.GONE);
                    stopsRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(StopsManagementActivity.this, 
                    getString(R.string.failed_to_load_stops, e.getMessage()), 
                    Toast.LENGTH_LONG).show();
            }
        });
    }

    private void openAddStopDialog() {
        Intent intent = new Intent(this, AddStopActivity.class);
        intent.putExtra("busNo", busNo);
        startActivity(intent);
    }

    private void deleteStop(Stop stop) {
        firebaseService.deleteStop(stop.getBusNo(), stop.getId(), new FirebaseService.OnSuccessListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(StopsManagementActivity.this, 
                    getString(R.string.stop_deleted_successfully), Toast.LENGTH_SHORT).show();
                loadStops();
            }

            @Override
            public void onFailure(Exception e) {
                String errorMessage = String.format(getString(R.string.failed_to_delete_stop), e.getMessage());
                Toast.makeText(StopsManagementActivity.this, errorMessage, Toast.LENGTH_LONG).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        loadStops(); // Reload stops when returning from add/edit activities
    }
}
