package com.example.bus_traker_manager.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.ui.DashboardActivity;
import com.google.android.gms.location.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import androidx.room.Room;
import com.example.bus_traker_manager.data.AppDatabase;
import com.example.bus_traker_manager.data.entity.LocationEntity;

public class LocationForegroundService extends Service {
    public static final String ACTION_PAUSE = "com.example.bus_traker_manager.ACTION_PAUSE";
    public static final String ACTION_END = "com.example.bus_traker_manager.ACTION_END";

    private static final String CHANNEL_ID = "location_channel";
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private DatabaseReference locationsRef, statusRef, tripsRef;
    private String tripId;
    private String busId;
    private AppDatabase db;
    private boolean updatesActive = true;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, getNotification());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationsRef = FirebaseDatabase.getInstance().getReference("locations");
        statusRef = FirebaseDatabase.getInstance().getReference("status");
        tripsRef = FirebaseDatabase.getInstance().getReference("trips");
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "bus_tracker_db").build();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    sendLocationToFirebase(location);
                }
            }
        };
        requestLocationUpdates();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_PAUSE.equals(action)) {
                pauseUpdates();
            } else if (ACTION_END.equals(action)) {
                endTripAndStop();
            } else {
                tripId = intent.getStringExtra("tripId");
                busId = intent.getStringExtra("busId");
            }
        }
        // Update notification to reflect current state
        Notification n = getNotification();
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(1, n);
        return START_STICKY;
    }

    private void requestLocationUpdates() {
        LocationRequest request = LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopSelf();
            return;
        }
        fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());
        updatesActive = true;
    }

    private void pauseUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        updatesActive = false;
    }

    private void resumeUpdates() {
        if (!updatesActive) {
            requestLocationUpdates();
        }
    }

    private void endTripAndStop() {
        try {
            pauseUpdates();
            if (tripId != null) {
                tripsRef.child(tripId).child("endAt").setValue(System.currentTimeMillis());
                tripsRef.child(tripId).child("status").setValue("ended");
            }
        } catch (Exception ignored) {}
        stopSelf();
    }

    private void sendLocationToFirebase(@NonNull Location location) {
        if (tripId == null || busId == null) return;
        if (!isNetworkAvailable()) {
            cacheLocationLocally(location);
            return;
        }
        Map<String, Object> locData = new HashMap<>();
        locData.put("lat", location.getLatitude());
        locData.put("lng", location.getLongitude());
        locData.put("speed", location.getSpeed());
        locData.put("bearing", location.getBearing());
        locData.put("accuracy", location.getAccuracy());
        locData.put("timestamp", System.currentTimeMillis());
        locData.put("batteryPct", getBatteryPercentage());
        locationsRef.child(tripId).push().setValue(locData);
        statusRef.child(busId).setValue(locData);
    }

    private int getBatteryPercentage() {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = registerReceiver(null, iFilter);
        if (batteryStatus == null) return -1;
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        if (level < 0 || scale <= 0) return -1;
        return (int) ((level / (float) scale) * 100);
    }

    private void cacheLocationLocally(Location location) {
        new Thread(() -> {
            LocationEntity entity = new LocationEntity();
            entity.tripId = tripId;
            entity.lat = location.getLatitude();
            entity.lng = location.getLongitude();
            entity.speed = location.getSpeed();
            entity.bearing = location.getBearing();
            entity.accuracy = location.getAccuracy();
            entity.timestamp = System.currentTimeMillis();
            entity.batteryPct = getBatteryPercentage();
            db.locationDao().insert(entity);
        }).start();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private Notification getNotification() {
        Intent openIntent = new Intent(this, DashboardActivity.class);
        PendingIntent contentPending = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(this, LocationForegroundService.class).setAction(ACTION_PAUSE);
        PendingIntent pausePending = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent endIntent = new Intent(this, LocationForegroundService.class).setAction(ACTION_END);
        PendingIntent endPending = PendingIntent.getService(this, 2, endIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Bus Tracker Running")
                .setContentText(updatesActive ? "Tracking active" : "Tracking paused")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(contentPending)
                .setOngoing(true)
                .addAction(new NotificationCompat.Action(0, updatesActive ? "Pause" : "Resume", updatesActive ? pausePending : PendingIntent.getService(this, 3, new Intent(this, LocationForegroundService.class), PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT)))
                .addAction(new NotificationCompat.Action(0, "End", endPending));
        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Location Service", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}
