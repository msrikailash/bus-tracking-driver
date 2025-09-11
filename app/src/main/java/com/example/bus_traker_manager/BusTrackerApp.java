package com.example.bus_traker_manager;

import android.app.Application;
import android.util.Log;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.example.bus_traker_manager.work.LocationSyncWorker;
import java.util.concurrent.TimeUnit;

public class BusTrackerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseApp.initializeApp(this);
            FirebaseDatabase.getInstance().setPersistenceEnabled(true); // Enable offline
        } catch (Exception e) {
            Log.e("BusTrackerApp", "Firebase init failed", e);
        }
        scheduleSync();
    }

    private void scheduleSync() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest syncWork = new PeriodicWorkRequest.Builder(LocationSyncWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("location_sync", ExistingPeriodicWorkPolicy.KEEP, syncWork);
    }
}

