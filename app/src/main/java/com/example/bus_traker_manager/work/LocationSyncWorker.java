package com.example.bus_traker_manager.work;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.bus_traker_manager.data.AppDatabase;
import com.example.bus_traker_manager.data.entity.LocationEntity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationSyncWorker extends Worker {
    public LocationSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "bus_tracker_db").build();
        DatabaseReference locationsRef = FirebaseDatabase.getInstance().getReference("locations");
        try {
            List<LocationEntity> cachedLocations = db.locationDao().getAll();
            for (LocationEntity e : cachedLocations) {
                if (e.tripId == null || e.tripId.isEmpty()) continue;
                Map<String, Object> loc = new HashMap<>();
                loc.put("lat", e.lat);
                loc.put("lng", e.lng);
                loc.put("speed", e.speed);
                loc.put("bearing", e.bearing);
                loc.put("accuracy", e.accuracy);
                loc.put("timestamp", e.timestamp);
                loc.put("batteryPct", e.batteryPct);
                locationsRef.child(e.tripId).push().setValue(loc);
            }
            db.locationDao().deleteAll();
            return Result.success();
        } catch (Exception ex) {
            return Result.retry();
        }
    }
}

