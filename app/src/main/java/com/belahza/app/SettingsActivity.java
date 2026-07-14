package com.belahza.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.premiumBars(this);

        LinearLayout content = Ui.vertical(this);
        content.setLayoutDirection(LanguageManager.layoutDirection(this));
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);

        Button back = Ui.button(this,LanguageManager.t(this,"back"),android.graphics.Color.WHITE,Ui.NAVY);
        back.setOnClickListener(v -> finish());
        content.addView(back,Ui.mlp(this,Ui.dp(this,110),Ui.dp(this,48),0,0,0,14));
        content.addView(Ui.text(this,LanguageManager.t(this,"settings"),30,Ui.NAVY,Typeface.BOLD));

        add(content,LanguageManager.t(this,"language"),LanguageManager.languageName(this),LanguageActivity.class);
        add(content,LanguageManager.t(this,"connected_status"),AuthSession.hasBearerToken(this) ? "Connected" : "Not connected",FirebasePhoneLoginActivity.class);
        add(content,LanguageManager.t(this,"medical_id"),LanguageManager.t(this,"medical_privacy"),MedicalProfileActivity.class);
        add(content,LanguageManager.t(this,"medical_privacy"),LanguageManager.t(this,"privacy_fields"),MedicalIdPrivacyActivity.class);
        add(content,LanguageManager.t(this,"permissions"),LanguageManager.t(this,"location_permission")+" · "+LanguageManager.t(this,"notification_permission"),PermissionSetupActivity.class);

        LinearLayout status = Ui.card(this);
        status.addView(Ui.text(this,LanguageManager.t(this,"connected_status"),18,Ui.NAVY,Typeface.BOLD));
        String connected = AuthSession.hasBearerToken(this) ? "Connected" : "Not connected — local emergency fallback remains available";
        status.addView(Ui.text(this,connected,13,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,7,0,0));
        content.addView(status,Ui.mlp(this,-1,-2,0,4,0,14));

        if (BuildConfig.DEBUG) {
            Button dev = Ui.button(this,LanguageManager.t(this,"developer_tools"),Ui.NAVY,android.graphics.Color.WHITE);
            dev.setOnClickListener(v -> startActivity(new Intent(this,DeveloperHubActivity.class)));
            content.addView(dev,Ui.lp(-1,Ui.dp(this,54)));
            content.addView(Ui.text(this,LanguageManager.t(this,"developer_only"),12,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,5,0,0));
        }

        setContentView(Ui.scroll(this,content));
    }

    private void add(LinearLayout parent,String title,String subtitle,Class<?> target) {
        LinearLayout card = Ui.card(this);
        card.setContentDescription(title + ". " + subtitle);
        card.setOnClickListener(v -> startActivity(new Intent(this,target)));
        card.addView(Ui.text(this,title,18,Ui.NAVY,Typeface.BOLD));
        card.addView(Ui.text(this,subtitle,13,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,4,0,0));
        parent.addView(card,Ui.mlp(this,-1,-2,0,12,0,0));
    }
}
