package com.example.bus_traker_manager.services;

import android.util.Log;

import com.example.bus_traker_manager.model.Driver;
import com.example.bus_traker_manager.model.Leave;
import com.example.bus_traker_manager.model.Stop;
import com.example.bus_traker_manager.model.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseService {
    private static final String TAG = "FirebaseService";
    private final FirebaseDatabase database;
    private final FirebaseAuth auth;
    private final DatabaseReference driversRef;
    private final DatabaseReference stopsRef;
    private final DatabaseReference leavesRef;
    private final DatabaseReference tripsRef;

    public FirebaseService() {
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        driversRef = database.getReference("drivers");
        stopsRef = database.getReference("stops");
        leavesRef = database.getReference("leaves");
        tripsRef = database.getReference("trips");
    }

    // Authentication methods
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void signInWithEmailAndPassword(String email, String password, OnAuthListener listener) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess(auth.getCurrentUser());
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    public void signOut() {
        auth.signOut();
    }

    // Driver methods
    public void getDriverData(String driverId, OnDriverListener listener) {
        driversRef.child(driverId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Driver driver = dataSnapshot.getValue(Driver.class);
                    if (driver != null) {
                        driver.setDriverId(driverId);
                        listener.onSuccess(driver);
                    } else {
                        listener.onFailure(new Exception("Failed to parse driver data"));
                    }
                } else {
                    listener.onFailure(new Exception("Driver not found"));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure(databaseError.toException());
            }
        });
    }

    public void updateDriverLocation(String driverId, double lat, double lng) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("lat", lat);
        updates.put("lng", lng);
        updates.put("status", "online");
        
        driversRef.child(driverId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Location updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update location", e));
    }

    public void updateDriverStatus(String driverId, String status) {
        driversRef.child(driverId).child("status").setValue(status)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Status updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update status", e));
    }

    public void updateDriverBusAssignment(String driverId, String busId, String busNumber, OnSuccessListener listener) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("busNo", busNumber);
        updates.put("busId", busId);
        updates.put("status", "assigned");
        
        driversRef.child(driverId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Bus assignment updated successfully");
                    listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update bus assignment", e);
                    listener.onFailure(e);
                });
    }

    // Stop methods with comprehensive structure
    public void getStopsForBus(String busNo, OnStopsListener listener) {
        stopsRef.child(busNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Stop> stops = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot stopSnapshot : dataSnapshot.getChildren()) {
                        Stop stop = stopSnapshot.getValue(Stop.class);
                        if (stop != null) {
                            stop.setId(stopSnapshot.getKey());
                            stops.add(stop);
                        }
                    }
                }
                listener.onSuccess(stops);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure(databaseError.toException());
            }
        });
    }

    public void addStop(Stop stop, OnSuccessListener listener) {
        if (stop.getBusNo() == null || stop.getBusNo().isEmpty()) {
            listener.onFailure(new Exception("Bus number is required"));
            return;
        }

        // Generate stop ID if not provided
        if (stop.getId() == null || stop.getId().isEmpty()) {
            String stopId = "stop_" + System.currentTimeMillis();
            stop.setId(stopId);
        }

        // Set creation metadata
        stop.setCreatedAt(System.currentTimeMillis());
        stop.setUpdatedAt(System.currentTimeMillis());
        if (stop.getCreatedBy() == null || stop.getCreatedBy().isEmpty()) {
            FirebaseUser currentUser = getCurrentUser();
            if (currentUser != null) {
                stop.setCreatedBy(currentUser.getUid());
            }
        }

        // Generate stop code if not provided
        if (stop.getStopCode() == null || stop.getStopCode().isEmpty()) {
            String stopCode = generateStopCode(stop.getName(), stop.getBusNo());
            stop.setStopCode(stopCode);
        }

        // Initialize nested objects if not already set
        if (stop.getLocation() == null) {
            stop.setLocation(new Stop.LocationInfo());
        }
        if (stop.getSchedule() == null) {
            stop.setSchedule(new Stop.ScheduleInfo());
        }
        if (stop.getStatus() == null) {
            stop.setStatus(new Stop.StatusInfo());
        }
        if (stop.getTracking() == null) {
            stop.setTracking(new Stop.TrackingInfo());
        }
        if (stop.getMetadata() == null) {
            stop.setMetadata(new Stop.MetadataInfo());
        }

        stopsRef.child(stop.getBusNo()).child(stop.getId()).setValue(stop)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void updateStop(Stop stop, OnSuccessListener listener) {
        if (stop.getId() == null || stop.getId().isEmpty()) {
            listener.onFailure(new Exception("Stop ID is required"));
            return;
        }

        // Update timestamp
        stop.setUpdatedAt(System.currentTimeMillis());

        stopsRef.child(stop.getBusNo()).child(stop.getId()).setValue(stop)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void deleteStop(String busNo, String stopId, OnSuccessListener listener) {
        stopsRef.child(busNo).child(stopId).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    // Update stop status (reached, skipped, etc.)
    public void updateStopStatus(String busNo, String stopId, String status, String notes, OnSuccessListener listener) {
        Stop.StatusInfo statusInfo = new Stop.StatusInfo();
        statusInfo.setCurrentStatus(status);
        statusInfo.setLastStatusUpdate(System.currentTimeMillis());
        
        // Add to status history
        Stop.StatusInfo.StatusHistory historyEntry = new Stop.StatusInfo.StatusHistory(
            status, 
            System.currentTimeMillis(), 
            getCurrentUser() != null ? getCurrentUser().getUid() : "unknown", 
            notes
        );
        
        List<Stop.StatusInfo.StatusHistory> history = new ArrayList<>();
        history.add(historyEntry);
        statusInfo.setStatusHistory(history);

        // Update specific status fields based on status
        switch (status) {
            case "reached":
                statusInfo.setReached(true);
                statusInfo.setSkipped(false);
                break;
            case "skipped":
                statusInfo.setReached(false);
                statusInfo.setSkipped(true);
                break;
            case "maintenance":
                statusInfo.setMaintenanceRequired(true);
                statusInfo.setMaintenanceNotes(notes);
                break;
        }

        stopsRef.child(busNo).child(stopId).child("status").setValue(statusInfo)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    // Update stop tracking information
    public void updateStopTracking(String busNo, String stopId, Stop.TrackingInfo trackingInfo, OnSuccessListener listener) {
        trackingInfo.setEstimatedArrivalTime(System.currentTimeMillis() + (trackingInfo.getEstimatedDuration() * 60 * 1000));
        
        stopsRef.child(busNo).child(stopId).child("tracking").setValue(trackingInfo)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    // Update stop location
    public void updateStopLocation(String busNo, String stopId, double latitude, double longitude, String address, OnSuccessListener listener) {
        Stop.LocationInfo locationInfo = new Stop.LocationInfo(latitude, longitude, address);
        
        stopsRef.child(busNo).child(stopId).child("location").setValue(locationInfo)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    // Update stop schedule
    public void updateStopSchedule(String busNo, String stopId, Stop.ScheduleInfo scheduleInfo, OnSuccessListener listener) {
        stopsRef.child(busNo).child(stopId).child("schedule").setValue(scheduleInfo)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    // Helper method to generate stop code
    private String generateStopCode(String stopName, String busNo) {
        if (stopName == null || stopName.isEmpty()) {
            return "STOP" + System.currentTimeMillis();
        }
        
        // Generate code from stop name (e.g., "Velangi Bus Stop" -> "VBS")
        String[] words = stopName.split("\\s+");
        StringBuilder code = new StringBuilder();
        
        for (String word : words) {
            if (!word.isEmpty()) {
                code.append(word.charAt(0));
            }
        }
        
        // Add timestamp to make it unique
        return code.toString().toUpperCase() + System.currentTimeMillis() % 1000;
    }

    // Get stops with specific status
    public void getStopsByStatus(String busNo, String status, OnStopsListener listener) {
        stopsRef.child(busNo).orderByChild("status/currentStatus").equalTo(status)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Stop> stops = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot stopSnapshot : dataSnapshot.getChildren()) {
                                Stop stop = stopSnapshot.getValue(Stop.class);
                                if (stop != null) {
                                    stop.setId(stopSnapshot.getKey());
                                    stops.add(stop);
                                }
                            }
                        }
                        listener.onSuccess(stops);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailure(databaseError.toException());
                    }
                });
    }

    // Get active stops only
    public void getActiveStops(String busNo, OnStopsListener listener) {
        stopsRef.child(busNo).orderByChild("status/isActive").equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Stop> stops = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot stopSnapshot : dataSnapshot.getChildren()) {
                                Stop stop = stopSnapshot.getValue(Stop.class);
                                if (stop != null) {
                                    stop.setId(stopSnapshot.getKey());
                                    stops.add(stop);
                                }
                            }
                        }
                        listener.onSuccess(stops);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onFailure(databaseError.toException());
                    }
                });
    }

    // Leave methods
    public void applyLeave(String driverId, String reason, Date leaveDate, String driverName, String busNo, OnLeaveListener listener) {
        String leaveId = leavesRef.push().getKey();
        if (leaveId != null) {
            Leave leave = new Leave(leaveId, driverId, reason, leaveDate, driverName, busNo);
            leavesRef.child(leaveId).setValue(leave)
                    .addOnSuccessListener(aVoid -> listener.onSuccess(leave))
                    .addOnFailureListener(listener::onFailure);
        } else {
            listener.onFailure(new Exception("Failed to generate leave ID"));
        }
    }

    public void getLeavesForDriver(String driverId, OnLeavesListener listener) {
        leavesRef.orderByChild("driverId").equalTo(driverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Leave> leaves = new ArrayList<>();
                for (DataSnapshot leaveSnapshot : dataSnapshot.getChildren()) {
                    Leave leave = leaveSnapshot.getValue(Leave.class);
                    if (leave != null) {
                        leave.setLeaveId(leaveSnapshot.getKey());
                        leaves.add(leave);
                    }
                }
                listener.onSuccess(leaves);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure(databaseError.toException());
            }
        });
    }

    // Listener interfaces
    public interface OnAuthListener {
        void onSuccess(FirebaseUser user);
        void onFailure(Exception e);
    }

    public interface OnDriverListener {
        void onSuccess(Driver driver);
        void onFailure(Exception e);
    }

    public interface OnStopsListener {
        void onSuccess(List<Stop> stops);
        void onFailure(Exception e);
    }

    public interface OnStopListener {
        void onSuccess(Stop stop);
        void onFailure(Exception e);
    }

    public interface OnLeaveListener {
        void onSuccess(Leave leave);
        void onFailure(Exception e);
    }

    public interface OnTripsListener {
        void onTripsLoaded(List<Trip> trips);
        void onTripsLoadFailed(String error);
    }

    public interface OnLeavesListener {
        void onSuccess(List<Leave> leaves);
        void onFailure(Exception e);
    }

    public interface OnSuccessListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    // Trip methods
    public void getTripHistoryForDriver(String driverId, OnTripsListener listener) {
        tripsRef.orderByChild("driverUid").equalTo(driverId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Trip> trips = new ArrayList<>();
                for (DataSnapshot tripSnapshot : dataSnapshot.getChildren()) {
                    Trip trip = tripSnapshot.getValue(Trip.class);
                    if (trip != null) {
                        trip.setTripId(tripSnapshot.getKey());
                        trips.add(trip);
                    }
                }
                listener.onTripsLoaded(trips);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onTripsLoadFailed(databaseError.getMessage());
            }
        });
    }
}
