package com.example.bus_traker_manager.services;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class AnalyticsService {
    private static final String TAG = "AnalyticsService";
    private FirebaseAnalytics firebaseAnalytics;
    private FirebaseCrashlytics crashlytics;
    private Context context;
    
    public AnalyticsService(Context context) {
        this.context = context;
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        this.crashlytics = FirebaseCrashlytics.getInstance();
    }
    
    // User engagement events
    public void logLogin(String method) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.METHOD, method);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
        Log.d(TAG, "Login event logged: " + method);
    }
    
    public void logLogout() {
        firebaseAnalytics.logEvent("logout", null);
        Log.d(TAG, "Logout event logged");
    }
    
    public void logTrackingStarted(String busNo) {
        Bundle bundle = new Bundle();
        bundle.putString("bus_number", busNo);
        firebaseAnalytics.logEvent("tracking_started", bundle);
        Log.d(TAG, "Tracking started event logged for bus: " + busNo);
    }
    
    public void logTrackingStopped(String busNo, long durationMinutes) {
        Bundle bundle = new Bundle();
        bundle.putString("bus_number", busNo);
        bundle.putLong("duration_minutes", durationMinutes);
        firebaseAnalytics.logEvent("tracking_stopped", bundle);
        Log.d(TAG, "Tracking stopped event logged for bus: " + busNo);
    }
    
    public void logStopAdded(String busNo, String stopName) {
        Bundle bundle = new Bundle();
        bundle.putString("bus_number", busNo);
        bundle.putString("stop_name", stopName);
        firebaseAnalytics.logEvent("stop_added", bundle);
        Log.d(TAG, "Stop added event logged: " + stopName);
    }
    
    public void logStopEdited(String busNo, String stopName) {
        Bundle bundle = new Bundle();
        bundle.putString("bus_number", busNo);
        bundle.putString("stop_name", stopName);
        firebaseAnalytics.logEvent("stop_edited", bundle);
        Log.d(TAG, "Stop edited event logged: " + stopName);
    }
    
    public void logStopDeleted(String busNo, String stopName) {
        Bundle bundle = new Bundle();
        bundle.putString("bus_number", busNo);
        bundle.putString("stop_name", stopName);
        firebaseAnalytics.logEvent("stop_deleted", bundle);
        Log.d(TAG, "Stop deleted event logged: " + stopName);
    }
    
    public void logLeaveApplied(String driverId, String reason) {
        Bundle bundle = new Bundle();
        bundle.putString("driver_id", driverId);
        bundle.putString("reason", reason);
        firebaseAnalytics.logEvent("leave_applied", bundle);
        Log.d(TAG, "Leave applied event logged for driver: " + driverId);
    }
    
    public void logRouteViewed(String busNo) {
        Bundle bundle = new Bundle();
        bundle.putString("bus_number", busNo);
        firebaseAnalytics.logEvent("route_viewed", bundle);
        Log.d(TAG, "Route viewed event logged for bus: " + busNo);
    }
    
    // Error tracking
    public void logError(String errorType, String errorMessage, Exception exception) {
        Bundle bundle = new Bundle();
        bundle.putString("error_type", errorType);
        bundle.putString("error_message", errorMessage);
        firebaseAnalytics.logEvent("app_error", bundle);
        
        // Log to Crashlytics
        crashlytics.setCustomKey("error_type", errorType);
        crashlytics.setCustomKey("error_message", errorMessage);
        if (exception != null) {
            crashlytics.recordException(exception);
        }
        
        Log.e(TAG, "Error logged: " + errorType + " - " + errorMessage);
    }
    
    // Performance tracking
    public void logAppStartTime(long startTimeMs) {
        Bundle bundle = new Bundle();
        bundle.putLong("start_time_ms", startTimeMs);
        firebaseAnalytics.logEvent("app_start_time", bundle);
        Log.d(TAG, "App start time logged: " + startTimeMs + "ms");
    }
    
    public void logLocationUpdateTime(long updateTimeMs) {
        Bundle bundle = new Bundle();
        bundle.putLong("update_time_ms", updateTimeMs);
        firebaseAnalytics.logEvent("location_update_time", bundle);
        Log.d(TAG, "Location update time logged: " + updateTimeMs + "ms");
    }
    
    // User properties
    public void setUserProperty(String propertyName, String propertyValue) {
        firebaseAnalytics.setUserProperty(propertyName, propertyValue);
        Log.d(TAG, "User property set: " + propertyName + " = " + propertyValue);
    }
    
    public void setUserId(String userId) {
        firebaseAnalytics.setUserId(userId);
        crashlytics.setUserId(userId);
        Log.d(TAG, "User ID set: " + userId);
    }
}
