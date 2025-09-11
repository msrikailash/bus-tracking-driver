package com.example.bus_traker_manager.services;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PerformanceMonitoringService {
    private static final String TAG = "PerformanceMonitor";
    private static final long MONITORING_INTERVAL_MS = 30000; // 30 seconds
    
    private Context context;
    private AnalyticsService analyticsService;
    private ScheduledExecutorService executorService;
    private Handler mainHandler;
    private boolean isMonitoring = false;
    
    // Performance metrics
    private long lastMemoryUsage = 0;
    private long lastCpuUsage = 0;
    private int locationUpdateCount = 0;
    private long totalLocationUpdateTime = 0;
    
    public PerformanceMonitoringService(Context context) {
        this.context = context;
        this.analyticsService = new AnalyticsService(context);
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public void startMonitoring() {
        if (isMonitoring) {
            return;
        }
        
        isMonitoring = true;
        Log.d(TAG, "Starting performance monitoring");
        
        executorService.scheduleAtFixedRate(this::collectPerformanceMetrics, 
            0, MONITORING_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }
    
    public void stopMonitoring() {
        if (!isMonitoring) {
            return;
        }
        
        isMonitoring = false;
        Log.d(TAG, "Stopping performance monitoring");
        
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    private void collectPerformanceMetrics() {
        try {
            // Memory usage
            long memoryUsage = getMemoryUsage();
            if (memoryUsage > lastMemoryUsage * 1.5) { // 50% increase
                Log.w(TAG, "Memory usage increased significantly: " + memoryUsage + " MB");
                analyticsService.logError("performance", "High memory usage: " + memoryUsage + " MB", null);
            }
            lastMemoryUsage = memoryUsage;
            
            // CPU usage
            long cpuUsage = getCpuUsage();
            if (cpuUsage > 80) { // 80% CPU usage
                Log.w(TAG, "High CPU usage detected: " + cpuUsage + "%");
                analyticsService.logError("performance", "High CPU usage: " + cpuUsage + "%", null);
            }
            lastCpuUsage = cpuUsage;
            
            // Battery usage
            int batteryLevel = getBatteryLevel();
            if (batteryLevel < 15) { // Low battery
                Log.w(TAG, "Low battery detected: " + batteryLevel + "%");
                // Trigger battery optimization
                optimizeForLowBattery();
            }
            
            // Network performance
            checkNetworkPerformance();
            
            Log.d(TAG, String.format("Performance metrics - Memory: %d MB, CPU: %d%%, Battery: %d%%", 
                memoryUsage, cpuUsage, batteryLevel));
            
        } catch (Exception e) {
            Log.e(TAG, "Error collecting performance metrics: " + e.getMessage());
            analyticsService.logError("performance_monitoring", e.getMessage(), e);
        }
    }
    
    private long getMemoryUsage() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        
        Debug.MemoryInfo debugMemoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(debugMemoryInfo);
        
        return debugMemoryInfo.getTotalPss() / 1024; // Convert to MB
    }
    
    private long getCpuUsage() {
        // Simple CPU usage estimation
        // In a real implementation, you might use more sophisticated methods
        return Math.round(Math.random() * 100); // Placeholder
    }
    
    private int getBatteryLevel() {
        // Get battery level using BatteryManager
        android.os.BatteryManager batteryManager = (android.os.BatteryManager) 
            context.getSystemService(Context.BATTERY_SERVICE);
        if (batteryManager != null) {
            return batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }
        return 100; // Default to full battery if unable to read
    }
    
    private void checkNetworkPerformance() {
        // Monitor network request times and failures
        if (locationUpdateCount > 0) {
            long averageUpdateTime = totalLocationUpdateTime / locationUpdateCount;
            if (averageUpdateTime > 5000) { // 5 seconds
                Log.w(TAG, "Slow location updates detected: " + averageUpdateTime + "ms average");
                analyticsService.logError("performance", "Slow location updates: " + averageUpdateTime + "ms", null);
            }
        }
    }
    
    private void optimizeForLowBattery() {
        Log.d(TAG, "Optimizing for low battery");
        // Reduce location update frequency
        // Disable non-essential features
        // Show notification to user about battery optimization
        mainHandler.post(() -> {
            // Post to main thread for UI updates
        });
    }
    
    public void recordLocationUpdateTime(long updateTimeMs) {
        locationUpdateCount++;
        totalLocationUpdateTime += updateTimeMs;
        
        if (updateTimeMs > 10000) { // 10 seconds
            Log.w(TAG, "Slow location update: " + updateTimeMs + "ms");
            analyticsService.logError("performance", "Slow location update: " + updateTimeMs + "ms", null);
        }
    }
    
    public void recordMemoryWarning() {
        Log.w(TAG, "Memory warning received");
        analyticsService.logError("performance", "Memory warning received", null);
        
        // Trigger garbage collection
        System.gc();
        
        // Clear caches if available
        clearCaches();
    }
    
    private void clearCaches() {
        // Clear image caches, temporary data, etc.
        Log.d(TAG, "Clearing caches due to memory pressure");
    }
    
    public void shutdown() {
        stopMonitoring();
    }
}







