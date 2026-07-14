package com.belahza.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OfflineFallbackActivity extends Activity {
    private TextView queue, message;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        Ui.premiumBars(this);
        LinearLayout content = Ui.vertical(this);
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);

        Button back = Ui.button(this,"← Back",android.graphics.Color.WHITE,Ui.NAVY);
        back.setOnClickListener(v -> finish());
        content.addView(back,Ui.mlp(this,Ui.dp(this,110),Ui.dp(this,46),0,0,0,14));
        content.addView(Ui.text(this,"Offline emergency fallback",30,Ui.NAVY,Typeface.BOLD));
        content.addView(Ui.text(this,"When backend or internet is unavailable, be lahza keeps the emergency useful: local Medical ID, guides, numbers, queued sync, and shareable alert message.",14,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,4,0,18));

        LinearLayout q = Ui.card(this);
        q.addView(Ui.text(this,"Queued encrypted sync actions",18,Ui.NAVY,Typeface.BOLD));
        queue = Ui.text(this,"",13,Ui.MUTED,Typeface.NORMAL);
        q.addView(queue,Ui.mlp(this,-1,-2,0,8,0,10));
        Button retry = Ui.button(this,"Retry backend sync",Ui.RED,android.graphics.Color.WHITE);
        q.addView(retry,Ui.lp(-1,Ui.dp(this,52)));
        content.addView(q,Ui.mlp(this,-1,-2,0,0,0,14));

        LinearLayout msg = Ui.card(this);
        msg.addView(Ui.text(this,"Fallback alert message",18,Ui.NAVY,Typeface.BOLD));
        message = Ui.text(this,buildFallback(),14,Ui.INK,Typeface.NORMAL);
        msg.addView(message,Ui.mlp(this,-1,-2,0,8,0,12));
        Button share = Ui.button(this,"Share fallback alert",Ui.NAVY,android.graphics.Color.WHITE);
        msg.addView(share,Ui.lp(-1,Ui.dp(this,52)));
        content.addView(msg);

        retry.setOnClickListener(v -> EmergencySyncQueue.retryAll(this, (ok, body) -> { render(); Toast.makeText(this, body, Toast.LENGTH_LONG).show(); }));
        share.setOnClickListener(v -> Dialer.share(this, buildFallback()));
        setContentView(Ui.scroll(this,content));
        render();
    }

    private void render() {
        queue.setText(EmergencySyncQueue.preview(this));
        message.setText(buildFallback());
    }

    private String buildFallback() {
        StringBuilder sb = new StringBuilder();
        sb.append("BE LAHZA EMERGENCY FALLBACK\n");
        sb.append("Emergency type: ").append(EmergencyMode.emergencyType(this)).append("\n");
        String loc = EmergencyMode.emergencyLocation(this);
        if (!loc.trim().isEmpty()) sb.append("Location: ").append(loc).append("\n");
        String note = EmergencyMode.emergencyNote(this);
        if (!note.trim().isEmpty()) sb.append("Note: ").append(note).append("\n");
        sb.append("\nMedical ID:\n").append(MedicalProfile.emergencyText(this));
        sb.append("\n\nPlease call me and emergency services if needed.");
        return sb.toString();
    }
}
