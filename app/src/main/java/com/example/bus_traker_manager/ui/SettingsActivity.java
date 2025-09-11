package com.example.bus_traker_manager.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.services.FirebaseService;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingsActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        setupToolbar();
        
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, new SettingsFragment())
                .commit();
        }
    }
    
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.settings));
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
    
    public static class SettingsFragment extends PreferenceFragmentCompat {
        private FirebaseService firebaseService;
        
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            firebaseService = new FirebaseService();
            
            setupPreferences();
        }
        
        private void setupPreferences() {
            // Theme preference
            ListPreference themePreference = findPreference("theme");
            if (themePreference != null) {
                themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    String themeValue = (String) newValue;
                    applyTheme(themeValue);
                    return true;
                });
            }
            
            // Location update frequency
            ListPreference locationFrequency = findPreference("location_frequency");
            if (locationFrequency != null) {
                locationFrequency.setOnPreferenceChangeListener((preference, newValue) -> {
                    String frequency = (String) newValue;
                    Toast.makeText(getContext(), 
                        getString(R.string.location_frequency_updated, frequency), 
                        Toast.LENGTH_SHORT).show();
                    return true;
                });
            }
            
            // Battery optimization
            SwitchPreferenceCompat batteryOptimization = findPreference("battery_optimization");
            if (batteryOptimization != null) {
                batteryOptimization.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean enabled = (Boolean) newValue;
                    Toast.makeText(getContext(), 
                        enabled ? getString(R.string.battery_optimization_enabled) : 
                                getString(R.string.battery_optimization_disabled), 
                        Toast.LENGTH_SHORT).show();
                    return true;
                });
            }
            
            // Notifications
            SwitchPreferenceCompat notifications = findPreference("notifications_enabled");
            if (notifications != null) {
                notifications.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean enabled = (Boolean) newValue;
                    Toast.makeText(getContext(), 
                        enabled ? getString(R.string.notifications_enabled) : 
                                getString(R.string.notifications_disabled), 
                        Toast.LENGTH_SHORT).show();
                    return true;
                });
            }
            
            // Clear cache
            Preference clearCache = findPreference("clear_cache");
            if (clearCache != null) {
                clearCache.setOnPreferenceClickListener(preference -> {
                    clearAppCache();
                    return true;
                });
            }
            
            // Export data
            Preference exportData = findPreference("export_data");
            if (exportData != null) {
                exportData.setOnPreferenceClickListener(preference -> {
                    exportUserData();
                    return true;
                });
            }
            
            // About
            Preference about = findPreference("about");
            if (about != null) {
                about.setOnPreferenceClickListener(preference -> {
                    showAboutDialog();
                    return true;
                });
            }
            
            // Logout
            Preference logout = findPreference("logout");
            if (logout != null) {
                logout.setOnPreferenceClickListener(preference -> {
                    performLogout();
                    return true;
                });
            }
        }
        
        private void applyTheme(String themeValue) {
            switch (themeValue) {
                case "light":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case "dark":
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case "system":
                default:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
            }
        }
        
        private void clearAppCache() {
            // Clear app cache
            Toast.makeText(getContext(), getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show();
        }
        
        private void exportUserData() {
            // Export user data to CSV
            Toast.makeText(getContext(), getString(R.string.data_export_started), Toast.LENGTH_SHORT).show();
        }
        
        private void showAboutDialog() {
            // Show about dialog
            Toast.makeText(getContext(), getString(R.string.app_version_info), Toast.LENGTH_LONG).show();
        }
        
        private void performLogout() {
            firebaseService.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        }
    }
}