package com.belahza.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EmergencyCountdownActivity extends Activity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView count;
    private int seconds = 3;
    private boolean completed = false;

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            if (completed) return;
            count.setText(String.valueOf(seconds));
            if (seconds <= 0) {
                completed = true;
                startEmergency();
                return;
            }
            seconds--;
            handler.postDelayed(this,1000);
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.premiumBars(this);

        if (EmergencyMode.isActive(this)) {
            startActivity(new Intent(this,ActiveEmergencyActivity.class));
            finish();
            return;
        }

        LinearLayout root = Ui.vertical(this);
        root.setLayoutDirection(LanguageManager.layoutDirection(this));
        root.setGravity(Gravity.CENTER);
        root.setPadding(Ui.dp(this,26),Ui.dp(this,26),Ui.dp(this,26),Ui.dp(this,26));
        root.setBackgroundColor(Ui.SOFT);

        root.addView(Ui.text(this,LanguageManager.t(this,"countdown_title"),28,Ui.RED,Typeface.BOLD));
        root.addView(Ui.text(this,LanguageManager.t(this,"countdown_desc"),15,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,8,0,22));
        count = Ui.text(this,"3",72,Ui.RED,Typeface.BOLD);
        count.setGravity(Gravity.CENTER);
        root.addView(count,Ui.mlp(this,-1,-2,0,0,0,22));

        Button cancel = Ui.button(this,LanguageManager.t(this,"cancel"),android.graphics.Color.WHITE,Ui.RED);
        cancel.setOnClickListener(v -> {
            completed = true;
            handler.removeCallbacksAndMessages(null);
            finish();
        });
        root.addView(cancel,Ui.mlp(this,-1,Ui.dp(this,58),0,0,0,12));

        Button call = Ui.button(this,LanguageManager.t(this,"call_140"),Ui.RED,android.graphics.Color.WHITE);
        call.setOnClickListener(v -> Dialer.call(this,"140"));
        root.addView(call,Ui.lp(-1,Ui.dp(this,58)));

        setContentView(root);
        handler.post(tick);
    }

    private void startEmergency() {
        String type = getIntent().getStringExtra("emergency_type");
        String note = getIntent().getStringExtra("emergency_note");
        String location = getIntent().getStringExtra("emergency_location");
        EmergencyMode.trigger(this,type,note,location);
        finish();
    }

    @Override protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
