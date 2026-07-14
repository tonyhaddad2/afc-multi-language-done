
package com.belahza.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class AuthActivity extends Activity {
    private EditText name, phone, token, api;
    private TextView status;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        Ui.premiumBars(this);
        LinearLayout content = Ui.vertical(this);
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);
        Button back = Ui.button(this,"← Back",android.graphics.Color.WHITE,Ui.NAVY);
        back.setOnClickListener(v -> finish());
        content.addView(back,Ui.mlp(this,Ui.dp(this,110),Ui.dp(this,46),0,0,0,14));
        content.addView(Ui.text(this,"Production connection",30,Ui.NAVY,Typeface.BOLD));
        content.addView(Ui.text(this,"Connect the app to the deployed encrypted backend. Production login should use Firebase/Auth; developer builds can request a dev token from non-production backend.",14,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,4,0,18));

        name = field("Full name", SecureLocalStore.get(this,"user_name"));
        phone = field("Phone number", SecureLocalStore.get(this,"user_phone"));
        token = field("Bearer token / JWT", SecureLocalStore.get(this,"auth_bearer_token"));
        api = field("API base URL", AuthSession.apiBaseUrl(this));
        content.addView(name,Ui.mlp(this,-1,Ui.dp(this,54),0,0,0,10));
        content.addView(phone,Ui.mlp(this,-1,Ui.dp(this,54),0,0,0,10));
        content.addView(token,Ui.mlp(this,-1,Ui.dp(this,84),0,0,0,10));
        content.addView(api,Ui.mlp(this,-1,Ui.dp(this,54),0,0,0,16));

        Button save = Ui.button(this,"Save connection",Ui.RED,android.graphics.Color.WHITE);
        Button devToken = Ui.button(this,"Request dev token",Ui.GREEN,android.graphics.Color.WHITE);
        Button test = Ui.button(this,"Test backend",Ui.NAVY,android.graphics.Color.WHITE);
        Button clear = Ui.button(this,"Clear session",android.graphics.Color.WHITE,Ui.NAVY);
        content.addView(save,Ui.lp(-1,Ui.dp(this,54)));
        content.addView(devToken,Ui.mlp(this,-1,Ui.dp(this,54),0,10,0,0));
        content.addView(test,Ui.mlp(this,-1,Ui.dp(this,54),0,10,0,0));
        content.addView(clear,Ui.mlp(this,-1,Ui.dp(this,54),0,10,0,0));
        status = Ui.text(this,"Status: " + (AuthSession.hasBearerToken(this) ? "token saved" : "not connected"),13,Ui.MUTED,Typeface.BOLD);
        content.addView(status,Ui.mlp(this,-1,-2,0,14,0,0));

        save.setOnClickListener(v -> saveSession(true));
        devToken.setOnClickListener(v -> {
            AuthSession.save(this, phone.getText().toString(), name.getText().toString(), token.getText().toString(), api.getText().toString());
            ConnectedBackendClient.requestDevTokenAsync(this, phone.getText().toString().replace("+","").replace(" ",""), phone.getText().toString(), (ok, body) -> {
                if (ok) {
                    try {
                        JSONObject json = new JSONObject(body);
                        token.setText(json.optString("token", ""));
                        saveSession(false);
                        status.setText("Dev token saved. Do not enable dev-token endpoint in production.");
                    } catch(Exception e) { status.setText("Token parse failed: " + body); }
                } else status.setText("Dev token failed: " + body);
            });
        });
        test.setOnClickListener(v -> { saveSession(false); ConnectedBackendClient.healthAsync(this, (ok, body) -> status.setText(ok ? "Backend reachable: " + body : "Backend test failed: " + body)); });
        clear.setOnClickListener(v -> { AuthSession.clear(this); status.setText("Status: cleared"); Toast.makeText(this,"Session cleared",Toast.LENGTH_SHORT).show(); });
        setContentView(Ui.scroll(this,content));
    }

    private EditText field(String hint, String value) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setText(value == null ? "" : value);
        e.setTextSize(15);
        e.setSingleLine(false);
        e.setPadding(Ui.dp(this,16),0,Ui.dp(this,16),0);
        e.setBackground(Ui.bgStroke(this,android.graphics.Color.WHITE,18,Ui.LINE,1));
        return e;
    }

    private void saveSession(boolean toast) {
        AuthSession.save(this, phone.getText().toString(), name.getText().toString(), token.getText().toString(), api.getText().toString());
        status.setText("Status: saved. Backend URL: " + AuthSession.apiBaseUrl(this));
        ConnectedBackendClient.createUserProfileAsync(this, name.getText().toString(), phone.getText().toString(), null);
        if (toast) Toast.makeText(this,"Connection saved",Toast.LENGTH_SHORT).show();
    }
}
