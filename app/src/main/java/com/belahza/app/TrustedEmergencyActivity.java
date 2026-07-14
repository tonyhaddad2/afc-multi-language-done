package com.belahza.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class TrustedEmergencyActivity extends Activity {
    private String emergencyId = "";
    private TextView title,status,owner,location,medical,responses;
    private double lat = Double.NaN, lng = Double.NaN;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.premiumBars(this);

        emergencyId = getIntent().getStringExtra("emergencyId");
        if (emergencyId == null) emergencyId = "";

        LinearLayout content = Ui.vertical(this);
        content.setLayoutDirection(LanguageManager.layoutDirection(this));
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);

        Button back = Ui.button(this,LanguageManager.t(this,"back"),android.graphics.Color.WHITE,Ui.NAVY);
        back.setOnClickListener(v -> finish());
        content.addView(back,Ui.mlp(this,Ui.dp(this,110),Ui.dp(this,48),0,0,0,14));

        title = Ui.text(this,LanguageManager.t(this,"incoming_emergency"),30,Ui.RED,Typeface.BOLD);
        content.addView(title);

        LinearLayout summary = Ui.card(this);
        owner = Ui.text(this,"",18,Ui.NAVY,Typeface.BOLD);
        status = Ui.text(this,"",14,Ui.RED,Typeface.BOLD);
        summary.addView(owner);
        summary.addView(status,Ui.mlp(this,-1,-2,0,6,0,0));
        content.addView(summary,Ui.mlp(this,-1,-2,0,12,0,12));

        LinearLayout locationCard = Ui.card(this);
        locationCard.addView(Ui.text(this,LanguageManager.t(this,"location"),17,Ui.NAVY,Typeface.BOLD));
        location = Ui.text(this,"",14,Ui.MUTED,Typeface.NORMAL);
        locationCard.addView(location,Ui.mlp(this,-1,-2,0,7,0,8));
        Button directions = Ui.button(this,LanguageManager.t(this,"directions"),Ui.NAVY,android.graphics.Color.WHITE);
        directions.setOnClickListener(v -> openDirections());
        locationCard.addView(directions,Ui.lp(-1,Ui.dp(this,52)));
        content.addView(locationCard,Ui.mlp(this,-1,-2,0,0,0,12));

        LinearLayout medicalCard = Ui.card(this);
        medicalCard.addView(Ui.text(this,LanguageManager.t(this,"medical_information"),17,Ui.NAVY,Typeface.BOLD));
        medical = Ui.text(this,"",14,Ui.INK,Typeface.NORMAL);
        medicalCard.addView(medical,Ui.mlp(this,-1,-2,0,7,0,0));
        content.addView(medicalCard,Ui.mlp(this,-1,-2,0,0,0,12));

        LinearLayout responseCard = Ui.card(this);
        responseCard.addView(Ui.text(this,LanguageManager.t(this,"responses"),17,Ui.NAVY,Typeface.BOLD));
        responses = Ui.text(this,"",13,Ui.MUTED,Typeface.NORMAL);
        responseCard.addView(responses,Ui.mlp(this,-1,-2,0,7,0,0));
        content.addView(responseCard,Ui.mlp(this,-1,-2,0,0,0,12));

        LinearLayout actions = Ui.horizontal(this);
        Button seen = Ui.button(this,LanguageManager.t(this,"seen"),Ui.NAVY,android.graphics.Color.WHITE);
        Button helping = Ui.button(this,LanguageManager.t(this,"helping"),Ui.GREEN,android.graphics.Color.WHITE);
        actions.addView(seen,weight(0,Ui.dp(this,54),0,0,6,0));
        actions.addView(helping,weight(0,Ui.dp(this,54),6,0,0,0));
        content.addView(actions);

        Button cannot = Ui.button(this,LanguageManager.t(this,"cannot_help"),android.graphics.Color.WHITE,Ui.RED);
        cannot.setOnClickListener(v -> mark("cannot_help"));
        content.addView(cannot,Ui.mlp(this,-1,Ui.dp(this,52),0,10,0,0));

        Button call = Ui.button(this,LanguageManager.t(this,"call_140"),Ui.RED,android.graphics.Color.WHITE);
        call.setOnClickListener(v -> Dialer.call(this,"140"));
        content.addView(call,Ui.mlp(this,-1,Ui.dp(this,58),0,10,0,0));

        seen.setOnClickListener(v -> mark("seen"));
        helping.setOnClickListener(v -> mark("helping"));

        setContentView(Ui.scroll(this,content));

        if (emergencyId.isEmpty()) {
            status.setText("Missing emergency link.");
            medical.setText("Open this screen from the be lahza emergency notification.");
        } else load();
    }

    private void load() {
        ConnectedBackendClient.getEmergencyAsync(this,emergencyId,(ok,body) -> {
            if (!ok) {
                status.setText("Emergency unavailable or you are not authorized.");
                medical.setText(body);
                return;
            }
            try {
                JSONObject json = new JSONObject(body);
                String type = json.optString("emergencyType","Emergency");
                String state = json.optString("status","active");
                JSONObject profile = json.optJSONObject("ownerProfile");
                owner.setText(profile == null ? type : profile.optString("name","be lahza user")+"\n"+type);
                status.setText(state);

                JSONObject latest = json.optJSONObject("latestLocation");
                if (latest != null) {
                    lat = latest.optDouble("lat",Double.NaN);
                    lng = latest.optDouble("lng",Double.NaN);
                    location.setText(Double.isNaN(lat) ? LanguageManager.t(this,"location_stale") :
                            LanguageManager.t(this,"location_fresh")+"\nhttps://maps.google.com/?q="+lat+","+lng);
                } else location.setText(LanguageManager.t(this,"location_stale"));

                JSONObject med = json.optJSONObject("medicalId");
                medical.setText(formatMedical(med));

                JSONArray updates = json.optJSONArray("statusUpdates");
                StringBuilder out = new StringBuilder();
                if (updates != null) {
                    for (int i=0;i<updates.length();i++) {
                        JSONObject update = updates.optJSONObject(i);
                        if (update != null) out.append("• ").append(update.optString("status")).append("\n");
                    }
                }
                responses.setText(out.length() == 0 ? "No responses yet." : out.toString().trim());
            } catch(Exception e) {
                status.setText("Invalid emergency response.");
            }
        });
    }

    private String formatMedical(JSONObject med) {
        if (med == null) return "No Medical ID shared.";
        StringBuilder out = new StringBuilder();
        add(out,LanguageManager.t(this,"name"),med.optString(MedicalProfile.FULL_NAME));
        add(out,LanguageManager.t(this,"blood_type"),med.optString(MedicalProfile.BLOOD_TYPE));
        add(out,LanguageManager.t(this,"allergies"),med.optString(MedicalProfile.ALLERGIES));
        add(out,LanguageManager.t(this,"conditions"),med.optString(MedicalProfile.CONDITIONS));
        add(out,LanguageManager.t(this,"medications"),med.optString(MedicalProfile.MEDICATIONS));
        add(out,LanguageManager.t(this,"notes"),med.optString(MedicalProfile.NOTES));
        return out.length() == 0 ? "No Medical ID shared." : out.toString().trim();
    }

    private void add(StringBuilder out,String label,String value) {
        if (value != null && !value.trim().isEmpty()) out.append(label).append(": ").append(value.trim()).append("\n");
    }

    private void mark(String value) {
        if (emergencyId.isEmpty()) return;
        ConnectedBackendClient.markEmergencyStatusAsync(this,emergencyId,value,"",(ok,body) -> {
            Toast.makeText(this,ok ? LanguageManager.t(this,value.equals("seen") ? "seen" : value.equals("helping") ? "helping" : "cannot_help") : body,Toast.LENGTH_SHORT).show();
            if (ok) load();
        });
    }

    private void openDirections() {
        if (Double.isNaN(lat) || Double.isNaN(lng)) {
            Toast.makeText(this,LanguageManager.t(this,"location_stale"),Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("geo:"+lat+","+lng+"?q="+lat+","+lng)));
    }

    private LinearLayout.LayoutParams weight(int w,int h,int l,int t,int r,int b) {
        LinearLayout.LayoutParams lp = Ui.mlp(this,w,h,l,t,r,b);
        lp.weight=1;
        return lp;
    }
}
