package com.example.bus_traker_manager.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.data.AppDatabase;
import com.example.bus_traker_manager.data.entity.LocationEntity;
import com.example.bus_traker_manager.work.LocationSyncWorker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import java.util.Collections;
import java.util.List;

public class DiagnosticsActivity extends AppCompatActivity {
    private TextView textDiagnostics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnostics);
        textDiagnostics = findViewById(R.id.textDiagnostics);
        Button buttonSyncNow = findViewById(R.id.buttonSyncNow);
        buttonSyncNow.setOnClickListener(v -> triggerSyncNow());
        loadDiagnostics();
    }

    private void triggerSyncNow() {
        WorkManager.getInstance(this).enqueue(OneTimeWorkRequest.from(LocationSyncWorker.class));
        // Delay refresh a bit to allow sync
        textDiagnostics.postDelayed(this::loadDiagnostics, 2000);
    }

    private void loadDiagnostics() {
        new Thread(() -> {
            AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "bus_tracker_db").build();
            List<LocationEntity> all = db.locationDao().getAll();
            int queueSize = all.size();
            Collections.reverse(all);
            List<LocationEntity> last = all.size() > 20 ? all.subList(0, 20) : all;
            StringBuilder sb = new StringBuilder();
            sb.append("Cached locations: ").append(queueSize).append("\n\n");
            int idx = 1;
            for (LocationEntity e : last) {
                sb.append(idx++).append(") ").append(e.lat).append(", ").append(e.lng)
                  .append(" spd=").append(e.speed)
                  .append(" acc=").append(e.accuracy)
                  .append(" ts=").append(e.timestamp).append("\n");
            }
            runOnUiThread(() -> textDiagnostics.setText(sb.toString()));
        }).start();
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
                String current = textDiagnostics.getText().toString();
                textDiagnostics.setText(current + "\nFCM token: " + token);
            }
        });
    }
}
