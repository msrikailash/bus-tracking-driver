package com.example.bus_traker_manager.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.bus_traker_manager.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);
        navView.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                selected = new HomeFragment();
            } else if (itemId == R.id.menu_stops) {
                selected = new StopsListFragment();
            } else if (itemId == R.id.menu_report) {
                selected = new ReportIssueFragment();
            } else if (itemId == R.id.menu_settings) {
                selected = new SettingsFragment();
            }
            if (selected != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selected)
                        .commit();
                return true;
            }
            return false;
        });
        // Set default
        navView.setSelectedItemId(R.id.menu_home);
    }
}

