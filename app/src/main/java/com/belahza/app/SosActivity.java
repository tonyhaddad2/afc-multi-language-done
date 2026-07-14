package com.belahza.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

public class SosActivity extends Activity {
    private static final int LOCATION_REQUEST = 411;
    private TextView locationStatus;
    private EditText note;
    private String locationLink = "";
    private String emergencyType = "Emergency";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.premiumBars(this);

        String key = getIntent().getStringExtra(EmergencyData.EXTRA_KEY);
        if (key != null && !key.isEmpty()) {
            EmergencyData.Emergency emergency = EmergencyData.get(key);
            emergencyType = LanguageManager.emergencyTitle(this,emergency);
        }

        LinearLayout content = Ui.vertical(this);
        content.setLayoutDirection(LanguageManager.layoutDirection(this));
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);

        Button back = Ui.button(this,LanguageManager.t(this,"back"),android.graphics.Color.WHITE,Ui.NAVY);
        back.setOnClickListener(v -> finish());
        content.addView(back,Ui.mlp(this,Ui.dp(this,110),Ui.dp(this,48),0,0,0,14));
        content.addView(Ui.text(this,LanguageManager.t(this,"sos"),32,Ui.RED,Typeface.BOLD));
        content.addView(Ui.text(this,LanguageManager.t(this,"sos_desc"),14,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,5,0,18));

        LinearLayout locationCard = Ui.card(this);
        locationCard.addView(Ui.text(this,LanguageManager.t(this,"location"),18,Ui.NAVY,Typeface.BOLD));
        locationStatus = Ui.text(this,LanguageManager.t(this,"location_stale"),14,Ui.MUTED,Typeface.NORMAL);
        locationCard.addView(locationStatus,Ui.mlp(this,-1,-2,0,8,0,10));
        Button refresh = Ui.button(this,LanguageManager.t(this,"refresh"),android.graphics.Color.WHITE,Ui.NAVY);
        refresh.setOnClickListener(v -> requestLocation());
        locationCard.addView(refresh,Ui.lp(-1,Ui.dp(this,52)));
        content.addView(locationCard,Ui.mlp(this,-1,-2,0,0,0,14));

        note = new EditText(this);
        note.setHint(LanguageManager.t(this,"notes"));
        note.setTextSize(15);
        note.setMinHeight(Ui.dp(this,86));
        note.setPadding(Ui.dp(this,16),Ui.dp(this,10),Ui.dp(this,16),Ui.dp(this,10));
        note.setBackground(Ui.bgStroke(this,android.graphics.Color.WHITE,18,Ui.LINE,1));
        content.addView(note,Ui.mlp(this,-1,-2,0,0,0,14));

        Button start = Ui.button(this,LanguageManager.t(this,"start_sos"),Ui.RED,android.graphics.Color.WHITE);
        start.setOnClickListener(v -> {
            Intent i = new Intent(this,EmergencyCountdownActivity.class);
            i.putExtra("emergency_type",emergencyType);
            i.putExtra("emergency_note",note.getText().toString());
            i.putExtra("emergency_location",locationLink);
            startActivity(i);
        });
        content.addView(start,Ui.lp(-1,Ui.dp(this,60)));

        Button call = Ui.button(this,LanguageManager.t(this,"call_140"),android.graphics.Color.WHITE,Ui.RED);
        call.setOnClickListener(v -> Dialer.call(this,"140"));
        content.addView(call,Ui.mlp(this,-1,Ui.dp(this,56),0,10,0,0));

        Button share = Ui.button(this,LanguageManager.t(this,"share_fallback"),android.graphics.Color.WHITE,Ui.NAVY);
        share.setOnClickListener(v -> Dialer.share(this,buildFallback()));
        content.addView(share,Ui.mlp(this,-1,Ui.dp(this,54),0,10,0,0));

        setContentView(Ui.scroll(this,content));
        EmergencyMode.ensureNotificationPermission(this);
        requestLocation();
    }

    private void requestLocation() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST);
            return;
        }
        locationStatus.setText("Locating…");
        try {
            CancellationTokenSource token = new CancellationTokenSource();
            LocationServices.getFusedLocationProviderClient(this)
                    .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,token.getToken())
                    .addOnSuccessListener(this::updateLocation)
                    .addOnFailureListener(e -> locationStatus.setText(LanguageManager.t(this,"location_stale")));
        } catch(SecurityException e) {
            locationStatus.setText(LanguageManager.t(this,"location_stale"));
        }
    }

    private void updateLocation(Location location) {
        if (location == null) {
            locationStatus.setText(LanguageManager.t(this,"location_stale"));
            return;
        }
        locationLink = "https://maps.google.com/?q="+location.getLatitude()+","+location.getLongitude();
        locationStatus.setText(LanguageManager.t(this,"location_fresh")+" · ±"+Math.round(location.getAccuracy())+" m\n"+locationLink);
    }

    private String buildFallback() {
        StringBuilder out = new StringBuilder();
        out.append("BE LAHZA EMERGENCY\n");
        out.append("Situation: ").append(emergencyType).append("\n");
        if (!locationLink.isEmpty()) out.append("Location: ").append(locationLink).append("\n");
        if (!note.getText().toString().trim().isEmpty()) out.append("Note: ").append(note.getText().toString().trim()).append("\n");
        out.append("\nMedical ID:\n").append(MedicalProfile.emergencyText(this));
        return out.toString();
    }

    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] results) {
        super.onRequestPermissionsResult(requestCode,permissions,results);
        if (requestCode == LOCATION_REQUEST) requestLocation();
    }
}
