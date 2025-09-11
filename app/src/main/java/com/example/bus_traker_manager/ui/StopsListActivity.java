package com.example.bus_traker_manager.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.model.Stop;
import com.example.bus_traker_manager.services.FirebaseService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import java.util.ArrayList;
import java.util.List;

public class StopsListActivity extends AppCompatActivity implements StopsAdapter.OnStopClickListener {
    private RecyclerView recyclerView;
    private StopsAdapter adapter;
    private List<Stop> stopList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private FirebaseService firebaseService;
    private String busNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops_list);
        
        busNo = getIntent().getStringExtra("busNo");
        if (busNo == null) {
            Toast.makeText(this, getString(R.string.bus_number_not_provided), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        firebaseService = new FirebaseService();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        recyclerView = findViewById(R.id.recyclerViewStops);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StopsAdapter(stopList, this);
        recyclerView.setAdapter(adapter);
        
        fetchCurrentLocation();
    }

    private void fetchCurrentLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    currentLocation = location;
                }
                fetchStops();
            });
        } catch (SecurityException e) {
            Toast.makeText(this, getString(R.string.location_permission_required), Toast.LENGTH_SHORT).show();
            fetchStops();
        }
    }

    private void fetchStops() {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
            return;
        }
        
        firebaseService.getStopsForBus(busNo, new FirebaseService.OnStopsListener() {
            @Override
            public void onSuccess(List<Stop> stops) {
                stopList.clear();
                stopList.addAll(stops);
                adapter.notifyDataSetChanged();
                
                if (stops.isEmpty()) {
                    findViewById(R.id.emptyStateLayout).setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    findViewById(R.id.emptyStateLayout).setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(StopsListActivity.this, getString(R.string.failed_to_load_stops, e.getMessage()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStopClick(Stop stop) {
        // Handle stop click - show details or navigate to map
        Toast.makeText(this, "Stop: " + stop.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEditStop(Stop stop) {
        // Handle edit stop - navigate to EditStopActivity
        Intent intent = new Intent(this, EditStopActivity.class);
        intent.putExtra("stop", stop);
        startActivity(intent);
    }

    @Override
    public void onDeleteStop(Stop stop) {
        // Handle delete stop
        new AlertDialog.Builder(this)
            .setTitle("Delete Stop")
            .setMessage("Are you sure you want to delete this stop?")
            .setPositiveButton("Delete", (dialog, which) -> {
                firebaseService.deleteStop(stop.getBusNo(), stop.getStopId(), new FirebaseService.OnSuccessListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(StopsListActivity.this, getString(R.string.stop_deleted_successfully), Toast.LENGTH_SHORT).show();
                        fetchStops(); // Reload the list
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(StopsListActivity.this, getString(R.string.failed_to_delete_stop, e.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
