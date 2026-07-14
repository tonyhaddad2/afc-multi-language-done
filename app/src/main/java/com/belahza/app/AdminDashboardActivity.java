package com.belahza.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdminDashboardActivity extends Activity {
    private TextView output;
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        Ui.premiumBars(this);
        LinearLayout content = Ui.vertical(this);
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);
        Button back = Ui.button(this,"← Back",android.graphics.Color.WHITE,Ui.NAVY);
        back.setOnClickListener(v -> finish());
        content.addView(back,Ui.mlp(this,Ui.dp(this,110),Ui.dp(this,46),0,0,0,14));
        content.addView(Ui.text(this,"Developer operations",30,Ui.NAVY,Typeface.BOLD));
        content.addView(Ui.text(this,"Production monitoring hooks for backend health, remote config, emergency counts, push status, and encrypted-data access audit state.",14,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,4,0,18));
        Button health = Ui.button(this,"Check backend health",Ui.RED,android.graphics.Color.WHITE);
        Button metrics = Ui.button(this,"Load admin metrics",Ui.NAVY,android.graphics.Color.WHITE);
        Button config = Ui.button(this,"Load remote config",Ui.GREEN,android.graphics.Color.WHITE);
        output = Ui.text(this,"No request yet.",13,Ui.MUTED,Typeface.NORMAL);
        content.addView(health,Ui.mlp(this,-1,Ui.dp(this,52),0,0,0,10));
        content.addView(metrics,Ui.mlp(this,-1,Ui.dp(this,52),0,0,0,10));
        content.addView(config,Ui.mlp(this,-1,Ui.dp(this,52),0,0,0,14));
        LinearLayout card = Ui.card(this);
        card.addView(Ui.text(this,"Response",18,Ui.NAVY,Typeface.BOLD));
        card.addView(output,Ui.mlp(this,-1,-2,0,8,0,0));
        content.addView(card);
        health.setOnClickListener(v -> ConnectedBackendClient.healthAsync(this,(ok,body)->output.setText(body)));
        metrics.setOnClickListener(v -> ConnectedBackendClient.getAsync(this,"/v1/admin/metrics",(ok,body)->output.setText(ok ? body : "Admin request failed: " + body)));
        config.setOnClickListener(v -> ConnectedBackendClient.getAppConfigAsync(this,(ok,body)->output.setText(ok ? body : "Config request failed: " + body)));
        setContentView(Ui.scroll(this,content));
    }
}
