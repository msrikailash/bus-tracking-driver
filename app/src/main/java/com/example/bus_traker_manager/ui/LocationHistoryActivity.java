package com.example.bus_traker_manager.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.adapter.LocationHistoryAdapter;
import com.example.bus_traker_manager.model.LocationPoint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LocationHistoryActivity extends AppCompatActivity {
    private static final String TAG = "LocationHistoryActivity";
    
    private RecyclerView recyclerView;
    private TextView textEmpty;
    private LocationHistoryAdapter adapter;
    private List<LocationPoint> locationHistory;
    
    private DatabaseReference locationHistoryRef;
    private SimpleDateFormat dateFormat;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_history);
        
        // Setup action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Location History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        initializeViews();
        setupRecyclerView();
        loadLocationHistory();
        
        dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
    }
    
    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view_location_history);
        textEmpty = findViewById(R.id.text_empty);
    }
    
    private void setupRecyclerView() {
        locationHistory = new ArrayList<>();
        adapter = new LocationHistoryAdapter(locationHistory, dateFormat);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    
    private void loadLocationHistory() {
        String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        locationHistoryRef = FirebaseDatabase.getInstance()
                .getReference("drivers")
                .child(driverId)
                .child("locationHistory");
        
        locationHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locationHistory.clear();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    LocationPoint locationPoint = snapshot.getValue(LocationPoint.class);
                    if (locationPoint != null) {
                        locationHistory.add(locationPoint);
                    }
                }
                
                // Sort by timestamp (newest first)
                Collections.sort(locationHistory, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
                
                adapter.notifyDataSetChanged();
                updateEmptyState();
            }
            
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LocationHistoryActivity.this, 
                        "Failed to load location history", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void updateEmptyState() {
        if (locationHistory.isEmpty()) {
            textEmpty.setVisibility(TextView.VISIBLE);
            recyclerView.setVisibility(RecyclerView.GONE);
        } else {
            textEmpty.setVisibility(TextView.GONE);
            recyclerView.setVisibility(RecyclerView.VISIBLE);
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
