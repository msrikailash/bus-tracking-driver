package com.example.bus_traker_manager.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.bus_traker_manager.R;
import com.example.bus_traker_manager.ui.DashboardActivity;

public class NotificationService {
    private static final String CHANNEL_ID_TRACKING = "tracking_channel";
    private static final String CHANNEL_ID_NOTIFICATIONS = "notifications_channel";
    private static final String CHANNEL_ID_ALERTS = "alerts_channel";
    
    private Context context;
    private NotificationManagerCompat notificationManager;
    
    public NotificationService(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannels();
    }
    
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Tracking channel
            NotificationChannel trackingChannel = new NotificationChannel(
                CHANNEL_ID_TRACKING,
                "GPS Tracking",
                NotificationManager.IMPORTANCE_LOW
            );
            trackingChannel.setDescription("GPS tracking notifications");
            
            // Notifications channel
            NotificationChannel notificationsChannel = new NotificationChannel(
                CHANNEL_ID_NOTIFICATIONS,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationsChannel.setDescription("General app notifications");
            
            // Alerts channel
            NotificationChannel alertsChannel = new NotificationChannel(
                CHANNEL_ID_ALERTS,
                "Alerts",
                NotificationManager.IMPORTANCE_HIGH
            );
            alertsChannel.setDescription("Important alerts and warnings");
            
            notificationManager.createNotificationChannel(trackingChannel);
            notificationManager.createNotificationChannel(notificationsChannel);
            notificationManager.createNotificationChannel(alertsChannel);
        }
    }
    
    public void showTrackingNotification(String title, String content, int notificationId) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_TRACKING)
            .setSmallIcon(R.drawable.ic_location)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false);
        
        notificationManager.notify(notificationId, builder.build());
    }
    
    public void showGeneralNotification(String title, String content, int notificationId) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_NOTIFICATIONS)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);
        
        notificationManager.notify(notificationId, builder.build());
    }
    
    public void showAlertNotification(String title, String content, int notificationId) {
        Intent intent = new Intent(context, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setSmallIcon(R.drawable.ic_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(new long[]{0, 500, 200, 500});
        
        notificationManager.notify(notificationId, builder.build());
    }
    
    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }
    
    public void cancelAllNotifications() {
        notificationManager.cancelAll();
    }
}
