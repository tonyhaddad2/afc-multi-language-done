package com.belahza.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;

public class ActiveEmergencyActivity extends Activity {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private TextView timer,delivery,location,responses,identifier;
    private boolean polling = false;

    private final Runnable tick = new Runnable() {
        @Override public void run() {
            renderLocal();
            pollServer();
            handler.postDelayed(this,5000);
        }
    };

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.premiumBars(this);

        if (!EmergencyMode.isActive(this)) {
            Toast.makeText(this,LanguageManager.t(this,"no_active_emergency"),Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        LinearLayout content = Ui.vertical(this);
        content.setLayoutDirection(LanguageManager.layoutDirection(this));
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);

        content.addView(Ui.text(this,LanguageManager.t(this,"active_title"),30,Ui.RED,Typeface.BOLD));
        content.addView(Ui.text(this,LanguageManager.t(this,"active_desc"),14,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,5,0,16));

        LinearLayout status = Ui.card(this);
        timer = Ui.text(this,"00:00",44,Ui.NAVY,Typeface.BOLD);
        timer.setGravity(Gravity.CENTER);
        status.addView(timer);
        status.addView(Ui.text(this,EmergencyMode.emergencyType(this),20,Ui.RED,Typeface.BOLD),Ui.mlp(this,-1,-2,0,10,0,0));
        identifier = Ui.text(this,"",11,Ui.MUTED,Typeface.NORMAL);
        status.addView(identifier,Ui.mlp(this,-1,-2,0,6,0,0));
        content.addView(status,Ui.mlp(this,-1,-2,0,0,0,12));

        content.addView(section(LanguageManager.t(this,"delivery")));
        delivery = Ui.text(this,"",14,Ui.INK,Typeface.BOLD);
        content.addView(infoCard(delivery),Ui.mlp(this,-1,-2,0,6,0,12));

        content.addView(section(LanguageManager.t(this,"location")));
        location = Ui.text(this,"",14,Ui.INK,Typeface.NORMAL);
        content.addView(infoCard(location),Ui.mlp(this,-1,-2,0,6,0,12));

        content.addView(section(LanguageManager.t(this,"responses")));
        responses = Ui.text(this,"No responses yet.",14,Ui.INK,Typeface.NORMAL);
        content.addView(infoCard(responses),Ui.mlp(this,-1,-2,0,6,0,16));

        LinearLayout row = Ui.horizontal(this);
        Button map = Ui.button(this,LanguageManager.t(this,"open_map"),Ui.NAVY,android.graphics.Color.WHITE);
        Button medical = Ui.button(this,LanguageManager.t(this,"open_medical_id"),Ui.RED,android.graphics.Color.WHITE);
        map.setOnClickListener(v -> openMap());
        medical.setOnClickListener(v -> startActivity(new Intent(this,EmergencyMedicalIdActivity.class)));
        row.addView(map,weight(0,Ui.dp(this,54),0,0,6,0));
        row.addView(medical,weight(0,Ui.dp(this,54),6,0,0,0));
        content.addView(row);

        Button share = Ui.button(this,LanguageManager.t(this,"share_fallback"),android.graphics.Color.WHITE,Ui.NAVY);
        share.setOnClickListener(v -> Dialer.share(this,EmergencyMode.fallbackMessage(this)));
        content.addView(share,Ui.mlp(this,-1,Ui.dp(this,54),0,10,0,0));

        Button call = Ui.button(this,LanguageManager.t(this,"call_140"),Ui.RED,android.graphics.Color.WHITE);
        call.setOnClickListener(v -> Dialer.call(this,"140"));
        content.addView(call,Ui.mlp(this,-1,Ui.dp(this,58),0,10,0,0));

        Button safe = Ui.button(this,LanguageManager.t(this,"mark_safe"),Ui.GREEN,android.graphics.Color.WHITE);
        safe.setOnClickListener(v -> {
            EmergencyMode.resolve(this);
            Toast.makeText(this,LanguageManager.t(this,"mark_safe"),Toast.LENGTH_SHORT).show();
            finish();
        });
        content.addView(safe,Ui.mlp(this,-1,Ui.dp(this,58),0,12,0,0));

        Button cancel = Ui.button(this,LanguageManager.t(this,"false_alarm"),android.graphics.Color.WHITE,Ui.RED);
        cancel.setOnClickListener(v -> {
            EmergencyMode.cancel(this);
            finish();
        });
        content.addView(cancel,Ui.mlp(this,-1,Ui.dp(this,54),0,10,0,0));

        setContentView(Ui.scroll(this,content));
        renderLocal();
        handler.post(tick);
    }

    private TextView section(String value) {
        return Ui.text(this,value,15,Ui.MUTED,Typeface.BOLD);
    }

    private LinearLayout infoCard(TextView text) {
        LinearLayout card = Ui.card(this);
        card.addView(text);
        return card;
    }

    private void renderLocal() {
        if (!EmergencyMode.isActive(this)) {
            finish();
            return;
        }

        long elapsed = Math.max(0,(System.currentTimeMillis()-EmergencyMode.startedAt(this))/1000);
        timer.setText(String.format(Locale.US,"%02d:%02d",elapsed/60,elapsed%60));
        delivery.setText(EmergencyMode.delivery(this));

        String id = EmergencyMode.emergencyId(this);
        identifier.setText(id.isEmpty() ? LanguageManager.t(this,"waiting_backend") : "Session "+id);

        long updated = EmergencyMode.lastLocationAt(this);
        long age = updated <= 0 ? Long.MAX_VALUE : System.currentTimeMillis()-updated;
        String freshness = age <= 60000 ? LanguageManager.t(this,"location_fresh") : LanguageManager.t(this,"location_stale");
        String link = EmergencyMode.lastLocationText(this);
        location.setText(freshness + (link.isEmpty() ? "" : "\n"+link));
    }

    private void pollServer() {
        String id = EmergencyMode.emergencyId(this);
        if (id.isEmpty() || polling || !AuthSession.hasBearerToken(this)) return;
        polling = true;
        ConnectedBackendClient.getEmergencyAsync(this,id,(ok,body) -> {
            polling = false;
            if (!ok) return;
            try {
                JSONObject json = new JSONObject(body);
                JSONArray updates = json.optJSONArray("statusUpdates");
                if (updates == null || updates.length() == 0) {
                    responses.setText("No responses yet.");
                    return;
                }
                StringBuilder out = new StringBuilder();
                for (int i=0;i<updates.length();i++) {
                    JSONObject update = updates.optJSONObject(i);
                    if (update == null) continue;
                    String status = update.optString("status");
                    if ("seen".equals(status)) status = LanguageManager.t(this,"seen");
                    else if ("helping".equals(status)) status = LanguageManager.t(this,"helping");
                    else if ("cannot_help".equals(status)) status = LanguageManager.t(this,"cannot_help");
                    out.append("• ").append(status).append("\n");
                }
                responses.setText(out.toString().trim());
            } catch(Exception ignored) {}
        });
    }

    private void openMap() {
        String value = EmergencyMode.lastLocationText(this);
        if (value.startsWith("http")) {
            startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(value)));
        } else Toast.makeText(this,LanguageManager.t(this,"location_stale"),Toast.LENGTH_SHORT).show();
    }

    private LinearLayout.LayoutParams weight(int w,int h,int l,int t,int r,int b) {
        LinearLayout.LayoutParams lp = Ui.mlp(this,w,h,l,t,r,b);
        lp.weight=1;
        return lp;
    }

    @Override protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
