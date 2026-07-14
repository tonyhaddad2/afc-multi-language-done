package com.belahza.app;

import com.belahza.app.BuildConfig;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DeveloperHubActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.premiumBars(this);

        if (!BuildConfig.DEBUG) {
            finish();
            return;
        }

        LinearLayout content = Ui.vertical(this);
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);

        Button back = Ui.button(this,"Back",android.graphics.Color.WHITE,Ui.NAVY);
        back.setOnClickListener(v -> finish());
        content.addView(back,Ui.mlp(this,Ui.dp(this,110),Ui.dp(this,48),0,0,0,14));
        content.addView(Ui.text(this,"Developer hub",30,Ui.NAVY,Typeface.BOLD));
        content.addView(Ui.text(this,"Debug-only backend, Firebase, operations, and end-to-end testing tools. These screens are intentionally hidden from release users.",14,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,5,0,18));

        add(content,"Backend connection",AuthActivity.class);
        add(content,"Firebase phone login",FirebasePhoneLoginActivity.class);
        add(content,"Production test",ProductionTestActivity.class);
        add(content,"Operations dashboard",AdminDashboardActivity.class);
        add(content,"Remote configuration",RemoteConfigActivity.class);
        add(content,"Offline queue",OfflineFallbackActivity.class);
        add(content,"Emergency history",EmergencyHistoryActivity.class);

        setContentView(Ui.scroll(this,content));
    }

    private void add(LinearLayout parent,String title,Class<?> target) {
        Button button = Ui.button(this,title,android.graphics.Color.WHITE,Ui.NAVY);
        button.setOnClickListener(v -> startActivity(new Intent(this,target)));
        parent.addView(button,Ui.mlp(this,-1,Ui.dp(this,54),0,0,0,10));
    }
}
