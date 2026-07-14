package com.belahza.app;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class BeLahzaFirebaseMessagingService extends FirebaseMessagingService {
    private static final String EMERGENCY_CHANNEL = "emergency_alerts";
    private static final String INVITE_CHANNEL = "trusted_contact_invites";

    @Override public void onNewToken(String token) {
        SecureLocalStore.put(this,"fcm_device_token",token);
        if (AuthSession.hasBearerToken(this)) ConnectedBackendClient.registerDeviceTokenAsync(this,token,null);
    }

    @Override public void onMessageReceived(RemoteMessage message) {
        String type = message.getData().get("messageType");
        if ("trusted_invite".equals(type)) {
            showInvite();
            return;
        }
        showEmergency(message.getData().get("emergencyId"),message.getData().get("status"));
    }

    private void showEmergency(String emergencyId,String status) {
        createChannel(EMERGENCY_CHANNEL,"Emergency alerts",NotificationManager.IMPORTANCE_HIGH);
        Intent open = new Intent(this,TrustedEmergencyActivity.class);
        open.putExtra("emergencyId",emergencyId == null ? "" : emergencyId);
        open.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(this,140142,open,
                Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,EMERGENCY_CHANNEL)
                .setSmallIcon(R.drawable.be_lahza_logo)
                .setContentTitle(status == null || status.isEmpty() ? "Emergency alert" : "Emergency "+status)
                .setContentText("Tap to view authorized emergency information.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setContentIntent(pi);
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(140142,builder.build());
    }

    private void showInvite() {
        createChannel(INVITE_CHANNEL,"Trusted contact invitations",NotificationManager.IMPORTANCE_DEFAULT);
        Intent open = new Intent(this,TrustedContactsActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this,140143,open,
                Build.VERSION.SDK_INT >= 23 ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE : PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,INVITE_CHANNEL)
                .setSmallIcon(R.drawable.be_lahza_logo)
                .setContentTitle("Trusted contact invitation")
                .setContentText("Open be lahza to accept or reject.")
                .setAutoCancel(true)
                .setContentIntent(pi);
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(140143,builder.build());
    }

    private void createChannel(String id,String name,int importance) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.createNotificationChannel(new NotificationChannel(id,name,importance));
        }
    }
}
