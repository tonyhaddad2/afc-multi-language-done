package com.belahza.app;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class LiveLocationService extends Service {
    private static final String CHANNEL_ID = "live_location_sharing";
    private static final int NOTIFICATION_ID = 140141;

    private FusedLocationProviderClient fused;
    private LocationCallback callback;
    private long lastSentAt = 0;
    private Location lastSentLocation;

    @Override public void onCreate() {
        super.onCreate();
        createChannel();
        fused = LocationServices.getFusedLocationProviderClient(this);
        callback = new LocationCallback() {
            @Override public void onLocationResult(LocationResult result) {
                if (result == null) return;
                Location location = result.getLastLocation();
                if (location != null) handle(location);
            }
        };
    }

    @Override public int onStartCommand(Intent intent,int flags,int startId) {
        startForeground(NOTIFICATION_ID,notification().build());
        startUpdates();
        return START_STICKY;
    }

    private void startUpdates() {
        if (!EmergencyMode.isActive(this)) {
            stopSelf();
            return;
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            LocationRequest request = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,15000)
                    .setMinUpdateIntervalMillis(8000)
                    .setMaxUpdateDelayMillis(30000)
                    .setMinUpdateDistanceMeters(5)
                    .build();
            fused.requestLocationUpdates(request,callback,getMainLooper());
            fused.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) handle(location);
            });
        } catch (SecurityException ignored) {}
    }

    private void handle(Location location) {
        if (!EmergencyMode.isActive(this)) {
            stopSelf();
            return;
        }

        long now = System.currentTimeMillis();
        long age = Build.VERSION.SDK_INT >= 17
                ? Math.max(0,(SystemClock.elapsedRealtimeNanos()-location.getElapsedRealtimeNanos())/1000000L)
                : Math.max(0,now-location.getTime());

        if (age > 120000 || location.getAccuracy() > 250f) return;

        EmergencyMode.setLatestLocation(this,location.getLatitude(),location.getLongitude(),location.getAccuracy(),now);

        boolean moved = lastSentLocation == null || lastSentLocation.distanceTo(location) >= 5f;
        boolean interval = now-lastSentAt >= 15000;
        if (!moved && !interval) return;

        lastSentAt = now;
        lastSentLocation = new Location(location);
        String emergencyId = EmergencyMode.emergencyId(this);
        int battery = BatteryUtils.batteryPercent(this);

        if (emergencyId.isEmpty()) {
            EmergencySyncQueue.enqueueLocation(this,"",location.getLatitude(),location.getLongitude(),location.getAccuracy(),battery);
            return;
        }

        ConnectedBackendClient.updateLocationAsync(
                this,
                emergencyId,
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                battery,
                (ok,body) -> {
                    if (!ok) {
                        EmergencySyncQueue.enqueueLocation(
                                this,
                                emergencyId,
                                location.getLatitude(),
                                location.getLongitude(),
                                location.getAccuracy(),
                                battery
                        );
                    } else {
                        EmergencySyncQueue.retryAll(this,null);
                    }
                }
        );
    }

    private NotificationCompat.Builder notification() {
        Intent open = new Intent(this,ActiveEmergencyActivity.class);
        PendingIntent pi = PendingIntent.getActivity(
                this,
                NOTIFICATION_ID,
                open,
                Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT
        );
        return new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.be_lahza_logo)
                .setContentTitle("be lahza")
                .setContentText("Live emergency location sharing is active")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOngoing(true)
                .setContentIntent(pi);
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Live emergency location",NotificationManager.IMPORTANCE_HIGH);
                channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                nm.createNotificationChannel(channel);
            }
        }
    }

    @Override public void onDestroy() {
        try { if (fused != null && callback != null) fused.removeLocationUpdates(callback); } catch(Exception ignored) {}
        super.onDestroy();
    }

    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }
}
