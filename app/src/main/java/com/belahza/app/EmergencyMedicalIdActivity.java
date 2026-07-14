package com.belahza.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EmergencyMedicalIdActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.premiumBars(this);

        if (!EmergencyMode.isActive(this)) {
            Toast.makeText(this,LanguageManager.t(this,"no_active_emergency"),Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        LinearLayout content = Ui.vertical(this);
        content.setLayoutDirection(LanguageManager.layoutDirection(this));
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);

        TextView title = Ui.text(this,LanguageManager.t(this,"medical_id"),30,Ui.RED,Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        content.addView(title);
        content.addView(Ui.text(this,EmergencyMode.emergencyType(this),19,Ui.NAVY,Typeface.BOLD),Ui.mlp(this,-1,-2,0,8,0,14));

        LinearLayout medical = Ui.card(this);
        medical.addView(Ui.text(this,LanguageManager.t(this,"medical_information"),19,Ui.NAVY,Typeface.BOLD));
        medical.addView(Ui.text(this,MedicalProfile.emergencyText(this),16,Ui.INK,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,8,0,0));
        content.addView(medical);

        Button call140 = Ui.button(this,LanguageManager.t(this,"call_140"),Ui.RED,android.graphics.Color.WHITE);
        call140.setOnClickListener(v -> Dialer.call(this,"140"));
        content.addView(call140,Ui.mlp(this,-1,Ui.dp(this,58),0,14,0,10));

        Button close = Ui.button(this,LanguageManager.t(this,"close"),android.graphics.Color.WHITE,Ui.NAVY);
        close.setOnClickListener(v -> finish());
        content.addView(close,Ui.lp(-1,Ui.dp(this,54)));

        setContentView(Ui.scroll(this,content));
    }
}
