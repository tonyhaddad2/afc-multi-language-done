package com.belahza.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EmergencyHistoryActivity extends Activity {
    private TextView output;
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b); Ui.premiumBars(this);
        LinearLayout content = Ui.vertical(this);
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);
        Button back = Ui.button(this,"← Back",android.graphics.Color.WHITE,Ui.NAVY); back.setOnClickListener(v -> finish());
        content.addView(back,Ui.mlp(this,Ui.dp(this,110),Ui.dp(this,46),0,0,0,14));
        content.addView(Ui.text(this,"Emergency history",30,Ui.NAVY,Typeface.BOLD));
        content.addView(Ui.text(this,"Backend emergency sessions, status updates, and audit-backed sensitive access checks. Local emergency mode remains available offline.",14,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,4,0,18));
        Button load = Ui.button(this,"Load my emergency history",Ui.RED,android.graphics.Color.WHITE);
        output = Ui.text(this,"Not loaded.",13,Ui.MUTED,Typeface.NORMAL);
        content.addView(load,Ui.lp(-1,Ui.dp(this,52)));
        LinearLayout card = Ui.card(this); card.addView(Ui.text(this,"History",18,Ui.NAVY,Typeface.BOLD)); card.addView(output,Ui.mlp(this,-1,-2,0,8,0,0)); content.addView(card,Ui.mlp(this,-1,-2,0,14,0,0));
        load.setOnClickListener(v -> ConnectedBackendClient.emergencyHistoryAsync(this,(ok,body)->output.setText(ok ? body : "Could not load history: " + body)));
        setContentView(Ui.scroll(this,content));
    }
}
