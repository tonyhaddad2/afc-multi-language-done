package com.belahza.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProductionTestActivity extends Activity {
    private TextView log;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        Ui.premiumBars(this);
        LinearLayout content = Ui.vertical(this);
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);
        Button back = Ui.button(this,"← Back",android.graphics.Color.WHITE,Ui.NAVY);
        back.setOnClickListener(v -> finish());
        content.addView(back,Ui.mlp(this,Ui.dp(this,110),Ui.dp(this,46),0,0,0,14));
        content.addView(Ui.text(this,"Two-phone production test",30,Ui.NAVY,Typeface.BOLD));
        content.addView(Ui.text(this,"Use this after backend + Firebase are configured. Phone A triggers the test emergency; Phone B opens the trusted receiver and marks seen/helping.",14,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,4,0,18));

        Button health = Ui.button(this,"1. Check backend health",Ui.NAVY,android.graphics.Color.WHITE);
        Button token = Ui.button(this,"2. Register FCM token",Ui.GREEN,android.graphics.Color.WHITE);
        Button med = Ui.button(this,"3. Sync Medical ID",Ui.RED,android.graphics.Color.WHITE);
        Button trigger = Ui.button(this,"4. Trigger test emergency",Ui.RED,android.graphics.Color.WHITE);
        Button receiver = Ui.button(this,"5. Open trusted receiver",Ui.NAVY,android.graphics.Color.WHITE);
        Button lock = Ui.button(this,"6. Test lock-screen Medical ID",Ui.GREEN,android.graphics.Color.WHITE);
        Button background = Ui.button(this,"7. Open live emergency screen",Ui.NAVY,android.graphics.Color.WHITE);

        content.addView(health,Ui.mlp(this,-1,Ui.dp(this,52),0,0,0,10));
        content.addView(token,Ui.mlp(this,-1,Ui.dp(this,52),0,0,0,10));
        content.addView(med,Ui.mlp(this,-1,Ui.dp(this,52),0,0,0,10));
        content.addView(trigger,Ui.mlp(this,-1,Ui.dp(this,52),0,0,0,10));
        content.addView(receiver,Ui.mlp(this,-1,Ui.dp(this,52),0,0,0,10));
        content.addView(lock,Ui.mlp(this,-1,Ui.dp(this,52),0,0,0,10));
        content.addView(background,Ui.mlp(this,-1,Ui.dp(this,52),0,0,0,14));

        LinearLayout card = Ui.card(this);
        card.addView(Ui.text(this,"Test log",18,Ui.NAVY,Typeface.BOLD));
        log = Ui.text(this,"No test run yet.",13,Ui.MUTED,Typeface.NORMAL);
        card.addView(log,Ui.mlp(this,-1,-2,0,8,0,0));
        content.addView(card);

        health.setOnClickListener(v -> ConnectedBackendClient.healthAsync(this,(ok,body)->set("Backend health: " + body)));
        token.setOnClickListener(v -> {
            String fcm = SecureLocalStore.get(this,"fcm_device_token");
            if (fcm.trim().isEmpty()) set("No FCM token found yet. Configure Firebase and reopen app.");
            else ConnectedBackendClient.registerDeviceTokenAsync(this,fcm,(ok,body)->set(ok ? "FCM token registered." : "FCM registration failed: " + body));
        });
        med.setOnClickListener(v -> ConnectedBackendClient.syncMedicalIdAsync(this,(ok,body)->set(ok ? "Medical ID synced encrypted." : "Medical ID sync failed: " + body)));
        trigger.setOnClickListener(v -> EmergencyMode.trigger(this,"Production test","Two-phone end-to-end test",""));
        receiver.setOnClickListener(v -> startActivity(new Intent(this,TrustedEmergencyActivity.class)));
        lock.setOnClickListener(v -> {
            EmergencyMode.ensureNotificationPermission(this);
            startActivity(new Intent(this,EmergencyMedicalIdActivity.class));
        });
        background.setOnClickListener(v -> startActivity(new Intent(this,ActiveEmergencyActivity.class)));
        setContentView(Ui.scroll(this,content));
    }

    private void set(String s) {
        log.setText(s + "\n\nQueue:\n" + EmergencySyncQueue.preview(this));
    }
}
