package com.belahza.app;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class PermissionSetupActivity extends Activity {
    private LinearLayout statusBox;

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
        content.addView(Ui.text(this,LanguageManager.t(this,"permissions"),30,Ui.NAVY,Typeface.BOLD));

        statusBox = Ui.vertical(this);
        content.addView(statusBox,Ui.mlp(this,-1,-2,0,14,0,14));

        Button camera = Ui.button(this,LanguageManager.t(this,"grant")+" "+LanguageManager.t(this,"camera_permission"),android.graphics.Color.WHITE,Ui.NAVY);
        Button location = Ui.button(this,LanguageManager.t(this,"grant")+" "+LanguageManager.t(this,"location_permission"),android.graphics.Color.WHITE,Ui.NAVY);
        Button notification = Ui.button(this,LanguageManager.t(this,"grant")+" "+LanguageManager.t(this,"notification_permission"),android.graphics.Color.WHITE,Ui.NAVY);
        camera.setOnClickListener(v -> requestPermissions(new String[]{Manifest.permission.CAMERA},2001));
        location.setOnClickListener(v -> requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},2002));
        notification.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= 33) requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},2003);
        });
        content.addView(camera,Ui.mlp(this,-1,Ui.dp(this,54),0,0,0,10));
        content.addView(location,Ui.mlp(this,-1,Ui.dp(this,54),0,0,0,10));
        content.addView(notification,Ui.lp(-1,Ui.dp(this,54)));

        setContentView(Ui.scroll(this,content));
        render();
    }

    private void render() {
        statusBox.removeAllViews();
        addStatus(LanguageManager.t(this,"camera_permission"),granted(Manifest.permission.CAMERA));
        addStatus(LanguageManager.t(this,"location_permission"),granted(Manifest.permission.ACCESS_FINE_LOCATION)||granted(Manifest.permission.ACCESS_COARSE_LOCATION));
        addStatus(LanguageManager.t(this,"notification_permission"),Build.VERSION.SDK_INT<33||granted(Manifest.permission.POST_NOTIFICATIONS));
    }

    private void addStatus(String label,boolean granted) {
        LinearLayout card = Ui.card(this);
        card.addView(Ui.text(this,label,17,Ui.NAVY,Typeface.BOLD));
        card.addView(Ui.text(this,LanguageManager.t(this,granted?"allowed":"missing"),14,granted?Ui.GREEN:Ui.RED,Typeface.BOLD),Ui.mlp(this,-1,-2,0,5,0,0));
        statusBox.addView(card,Ui.mlp(this,-1,-2,0,0,0,8));
    }

    private boolean granted(String permission) {
        return ContextCompat.checkSelfPermission(this,permission)==PackageManager.PERMISSION_GRANTED;
    }

    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] results) {
        super.onRequestPermissionsResult(requestCode,permissions,results);
        render();
    }
}
