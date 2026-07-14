package com.belahza.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class FirebasePhoneLoginActivity extends Activity {
    private EditText phone,code;
    private TextView status;
    private String verificationId = "";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.premiumBars(this);

        LinearLayout content = Ui.vertical(this);
        content.setLayoutDirection(LanguageManager.layoutDirection(this));
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);

        Button back = Ui.button(this,LanguageManager.t(this,"back"),android.graphics.Color.WHITE,Ui.NAVY);
        back.setOnClickListener(v -> finish());
        content.addView(back,Ui.mlp(this,Ui.dp(this,110),Ui.dp(this,48),0,0,0,14));
        content.addView(Ui.text(this,LanguageManager.t(this,"connected_status"),30,Ui.NAVY,Typeface.BOLD));
        content.addView(Ui.text(this,"Sign in by phone to connect trusted contacts and receive emergency notifications.",14,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,5,0,16));

        phone = field(LanguageManager.t(this,"phone_number"),SecureLocalStore.get(this,"user_phone"));
        code = field("SMS code","");
        content.addView(phone,Ui.mlp(this,-1,Ui.dp(this,58),0,0,0,10));
        content.addView(code,Ui.mlp(this,-1,Ui.dp(this,58),0,0,0,14));

        Button send = Ui.button(this,"Send verification code",Ui.RED,android.graphics.Color.WHITE);
        Button verify = Ui.button(this,"Verify and connect",Ui.GREEN,android.graphics.Color.WHITE);
        content.addView(send,Ui.lp(-1,Ui.dp(this,58)));
        content.addView(verify,Ui.mlp(this,-1,Ui.dp(this,58),0,10,0,0));

        status = Ui.text(this,AuthSession.hasBearerToken(this) ? "Connected" : "Not connected",13,Ui.MUTED,Typeface.BOLD);
        content.addView(status,Ui.mlp(this,-1,-2,0,14,0,0));

        send.setOnClickListener(v -> sendOtp());
        verify.setOnClickListener(v -> verify());
        setContentView(Ui.scroll(this,content));
    }

    private EditText field(String hint,String value) {
        EditText edit = new EditText(this);
        edit.setHint(hint);
        edit.setText(value == null ? "" : value);
        edit.setTextSize(16);
        edit.setSingleLine(true);
        edit.setPadding(Ui.dp(this,16),0,Ui.dp(this,16),0);
        edit.setBackground(Ui.bgStroke(this,android.graphics.Color.WHITE,18,Ui.LINE,1));
        return edit;
    }

    private void sendOtp() {
        try {
            FirebaseApp app = FirebaseApp.initializeApp(this);
            if (app == null) {
                status.setText("Connected service is not configured in this build.");
                return;
            }
            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                    .setPhoneNumber(phone.getText().toString().trim())
                    .setTimeout(60L,TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override public void onVerificationCompleted(PhoneAuthCredential credential) {
                            signIn(credential);
                        }

                        @Override public void onVerificationFailed(FirebaseException e) {
                            status.setText("Verification failed. Check the phone number and connection.");
                        }

                        @Override public void onCodeSent(String id,PhoneAuthProvider.ForceResendingToken token) {
                            verificationId=id;
                            status.setText("Code sent.");
                        }
                    }).build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        } catch(Exception e) {
            status.setText("Connected service is not configured in this build.");
        }
    }

    private void verify() {
        if (verificationId.isEmpty()) {
            Toast.makeText(this,"Send the verification code first.",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            signIn(PhoneAuthProvider.getCredential(verificationId,code.getText().toString().trim()));
        } catch(Exception e) {
            status.setText("Invalid verification code.");
        }
    }

    private void signIn(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null || task.getResult().getUser() == null) {
                status.setText("Sign-in failed.");
                return;
            }
            task.getResult().getUser().getIdToken(true).addOnCompleteListener(idTask -> {
                if (!idTask.isSuccessful() || idTask.getResult() == null) {
                    status.setText("Could not create a secure session.");
                    return;
                }
                ConnectedBackendClient.exchangeFirebaseIdTokenAsync(this,idTask.getResult().getToken(),(ok,body) -> {
                    if (!ok) {
                        status.setText("The connected service is unavailable.");
                        return;
                    }
                    try {
                        JSONObject json = new JSONObject(body);
                        String backendToken=json.optString("token");
                        String userId=json.optString("userId");
                        AuthSession.save(this,phone.getText().toString(),userId,backendToken,AuthSession.apiBaseUrl(this));
                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                            SecureLocalStore.put(this,"fcm_device_token",token);
                            ConnectedBackendClient.registerDeviceTokenAsync(this,token,null);
                        });
                        status.setText("Connected.");
                    } catch(Exception e) {
                        status.setText("Could not complete sign-in.");
                    }
                });
            });
        });
    }
}
