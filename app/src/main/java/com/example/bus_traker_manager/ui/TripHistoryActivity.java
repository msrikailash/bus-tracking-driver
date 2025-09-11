package com.example.bus_traker_manager.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.model.Trip;
import com.example.bus_traker_manager.services.FirebaseService;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class TripHistoryActivity extends AppCompatActivity implements FirebaseService.OnTripsListener {
    
    private RecyclerView tripsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TripHistoryAdapter tripsAdapter;
    private List<Trip> tripsList;
    private View emptyStateLayout;
    
    private FirebaseService firebaseService;
    private String currentDriverId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);
        
        setupToolbar();
        initializeViews();
        setupRecyclerView();
        setupSwipeRefresh();
        
        firebaseService = new FirebaseService();
        currentDriverId = firebaseService.getCurrentUser() != null ? 
            firebaseService.getCurrentUser().getUid() : null;
        
        if (currentDriverId != null) {
            loadTripHistory();
        } else {
            Toast.makeText(this, getString(R.string.user_not_authenticated), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.trip_history));
        }
    }
    
    private void initializeViews() {
        tripsRecyclerView = findViewById(R.id.tripsRecyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        tripsList = new ArrayList<>();
    }
    
    private void setupRecyclerView() {
        tripsAdapter = new TripHistoryAdapter(tripsList);
        tripsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tripsRecyclerView.setAdapter(tripsAdapter);
    }
    
    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadTripHistory);
        swipeRefreshLayout.setColorSchemeResources(
            R.color.primary_blue,
            R.color.accent_green,
            R.color.accent_orange
        );
    }
    
    private void loadTripHistory() {
        swipeRefreshLayout.setRefreshing(true);
        firebaseService.getTripHistoryForDriver(currentDriverId, this);
    }
    
    @Override
    public void onTripsLoaded(List<Trip> trips) {
        swipeRefreshLayout.setRefreshing(false);
        tripsList.clear();
        tripsList.addAll(trips);
        tripsAdapter.notifyDataSetChanged();
        
        if (trips.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            tripsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            tripsRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    @Override
    public void onTripsLoadFailed(String error) {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(this, getString(R.string.failed_to_load_trips, error), Toast.LENGTH_SHORT).show();
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