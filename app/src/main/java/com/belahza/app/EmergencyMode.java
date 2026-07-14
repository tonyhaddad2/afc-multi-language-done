package com.belahza.app;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.util.UUID;

public final class EmergencyMode {
    private static final String CHANNEL_ID = "emergency_medical_id";
    private static final int NOTIFICATION_ID = 140140;

    private EmergencyMode() {}

    public static void ensureNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},3314);
        }
    }

    public static synchronized void trigger(Context context,String emergencyType,String note,String location) {
        if (isActive(context)) {
            openActive(context);
            return;
        }

        String type = clean(emergencyType,"Emergency");
        String safeNote = clean(note,"");
        String safeLocation = clean(location,"");
        String requestId = UUID.randomUUID().toString();

        SecureLocalStore.put(context,"active_emergency","true");
        SecureLocalStore.put(context,"active_emergency_state","creating");
        SecureLocalStore.put(context,"active_emergency_type",type);
        SecureLocalStore.put(context,"active_emergency_note",safeNote);
        SecureLocalStore.put(context,"active_emergency_location",safeLocation);
        SecureLocalStore.put(context,"active_emergency_started_at",String.valueOf(System.currentTimeMillis()));
        SecureLocalStore.put(context,"active_emergency_request_id",requestId);
        SecureLocalStore.put(context,"active_emergency_id","");
        SecureLocalStore.put(context,"active_emergency_notified","0");
        SecureLocalStore.put(context,"active_emergency_delivery",LanguageManager.t(context,"sending"));

        showMedicalIdNotification(context);
        startLocationService(context);
        openActive(context);

        ConnectedBackendClient.createEmergencyAsync(context,type,safeNote,safeLocation,requestId,(ok,body) -> {
            if (!isActive(context) || !requestId.equals(requestId(context))) return;
            if (ok) {
                try {
                    JSONObject json = new JSONObject(body);
                    String id = json.optString("emergencyId","");
                    int notified = json.optInt("notified",0);
                    SecureLocalStore.put(context,"active_emergency_id",id);
                    SecureLocalStore.put(context,"active_emergency_state","active");
                    SecureLocalStore.put(context,"active_emergency_notified",String.valueOf(notified));
                    SecureLocalStore.put(context,"active_emergency_delivery",LanguageManager.t(context,"notified")+": "+notified);
                    EmergencySyncQueue.retryAll(context,null);
                } catch (Exception e) {
                    SecureLocalStore.put(context,"active_emergency_state","offline");
                    SecureLocalStore.put(context,"active_emergency_delivery",LanguageManager.t(context,"offline_queued"));
                    EmergencySyncQueue.enqueueEmergency(context,requestId,type,safeNote,safeLocation);
                }
            } else {
                SecureLocalStore.put(context,"active_emergency_state","offline");
                SecureLocalStore.put(context,"active_emergency_delivery",LanguageManager.t(context,"offline_queued"));
                EmergencySyncQueue.enqueueEmergency(context,requestId,type,safeNote,safeLocation);
            }
        });
    }

    public static synchronized void attachServerEmergency(Context context,String requestId,String emergencyId,int notified) {
        if (!isActive(context) || !requestId.equals(requestId(context))) return;
        SecureLocalStore.put(context,"active_emergency_id",emergencyId == null ? "" : emergencyId);
        SecureLocalStore.put(context,"active_emergency_state","active");
        SecureLocalStore.put(context,"active_emergency_notified",String.valueOf(notified));
        SecureLocalStore.put(context,"active_emergency_delivery",LanguageManager.t(context,"notified")+": "+notified);
    }

    public static synchronized void resolve(Context context) {
        if (!isActive(context)) return;
        String id = emergencyId(context);
        SecureLocalStore.put(context,"active_emergency_state","resolving");
        if (!id.isEmpty()) {
            ConnectedBackendClient.resolveEmergencyAsync(context,id,(ok,body) -> clear(context));
        } else clear(context);
    }

    public static synchronized void cancel(Context context) {
        if (!isActive(context)) return;
        String id = emergencyId(context);
        SecureLocalStore.put(context,"active_emergency_state","cancelling");
        if (!id.isEmpty()) {
            ConnectedBackendClient.cancelEmergencyAsync(context,id,(ok,body) -> clear(context));
        } else clear(context);
    }

    private static void clear(Context context) {
        SecureLocalStore.put(context,"active_emergency","false");
        SecureLocalStore.put(context,"active_emergency_state","idle");
        SecureLocalStore.put(context,"active_emergency_id","");
        SecureLocalStore.put(context,"active_emergency_request_id","");
        try { context.stopService(new Intent(context,LiveLocationService.class)); } catch (Exception ignored) {}
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.cancel(NOTIFICATION_ID);
    }

    private static void openActive(Context context) {
        Intent i = new Intent(context,ActiveEmergencyActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(i);
    }

    public static boolean isActive(Context context) {
        return "true".equals(SecureLocalStore.get(context,"active_emergency"));
    }

    public static String state(Context context) { return clean(SecureLocalStore.get(context,"active_emergency_state"),"idle"); }
    public static String emergencyId(Context context) { return clean(SecureLocalStore.get(context,"active_emergency_id"),""); }
    public static String requestId(Context context) { return clean(SecureLocalStore.get(context,"active_emergency_request_id"),""); }
    public static String emergencyType(Context context) { return clean(SecureLocalStore.get(context,"active_emergency_type"),"Emergency"); }
    public static String emergencyNote(Context context) { return clean(SecureLocalStore.get(context,"active_emergency_note"),""); }
    public static String emergencyLocation(Context context) { return clean(SecureLocalStore.get(context,"active_emergency_location"),""); }
    public static String delivery(Context context) { return clean(SecureLocalStore.get(context,"active_emergency_delivery"),LanguageManager.t(context,"waiting_backend")); }
    public static int notifiedCount(Context context) { try { return Integer.parseInt(SecureLocalStore.get(context,"active_emergency_notified")); } catch(Exception e) { return 0; } }
    public static long startedAt(Context context) { try { return Long.parseLong(SecureLocalStore.get(context,"active_emergency_started_at")); } catch(Exception e) { return System.currentTimeMillis(); } }
    public static long lastLocationAt(Context context) { try { return Long.parseLong(SecureLocalStore.get(context,"active_location_at")); } catch(Exception e) { return 0; } }
    public static String lastLocationText(Context context) { return clean(SecureLocalStore.get(context,"active_location_text"),emergencyLocation(context)); }

    public static void setLatestLocation(Context context,double lat,double lng,float accuracy,long timeMs) {
        SecureLocalStore.put(context,"active_location_lat",String.valueOf(lat));
        SecureLocalStore.put(context,"active_location_lng",String.valueOf(lng));
        SecureLocalStore.put(context,"active_location_accuracy",String.valueOf(accuracy));
        SecureLocalStore.put(context,"active_location_at",String.valueOf(timeMs));
        SecureLocalStore.put(context,"active_location_text","https://maps.google.com/?q="+lat+","+lng);
    }

    public static String fallbackMessage(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("BE LAHZA EMERGENCY\n");
        sb.append("Situation: ").append(emergencyType(context)).append("\n");
        String location = lastLocationText(context);
        if (!location.isEmpty()) sb.append("Location: ").append(location).append("\n");
        String note = emergencyNote(context);
        if (!note.isEmpty()) sb.append("Note: ").append(note).append("\n");
        sb.append("\nMedical ID:\n").append(MedicalProfile.emergencyText(context));
        sb.append("\n\nPlease call me and emergency services.");
        return sb.toString();
    }

    private static void startLocationService(Context context) {
        Intent service = new Intent(context,LiveLocationService.class);
        if (Build.VERSION.SDK_INT >= 26) context.startForegroundService(service);
        else context.startService(service);
    }

    private static void showMedicalIdNotification(Context context) {
        createChannel(context);
        Intent open = new Intent(context,EmergencyMedicalIdActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,NOTIFICATION_ID,open,
                Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setSmallIcon(R.drawable.be_lahza_logo)
                .setContentTitle("be lahza")
                .setContentText("Emergency Medical ID available — tap to view")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setOngoing(true)
                .setContentIntent(pi);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(NOTIFICATION_ID,builder.build());
    }

    private static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Emergency Medical ID",NotificationManager.IMPORTANCE_HIGH);
                channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
                nm.createNotificationChannel(channel);
            }
        }
    }

    private static String clean(String value,String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }
}
