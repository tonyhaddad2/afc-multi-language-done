package com.belahza.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class TrustedContactsActivity extends Activity {
    private LinearLayout invitesBox,contactsBox;
    private TextView status;
    private EditText phone;

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
        content.addView(Ui.text(this,LanguageManager.t(this,"trusted_contacts"),30,Ui.NAVY,Typeface.BOLD));

        status = Ui.text(this,AuthSession.hasBearerToken(this) ? "Connected" : "Connect your account to receive in-app alerts. Saved phone contacts remain available for sharing.",13,Ui.MUTED,Typeface.NORMAL);
        content.addView(status,Ui.mlp(this,-1,-2,0,5,0,16));

        LinearLayout add = Ui.card(this);
        add.addView(Ui.text(this,LanguageManager.t(this,"add_contact"),18,Ui.NAVY,Typeface.BOLD));
        phone = new EditText(this);
        phone.setHint(LanguageManager.t(this,"phone_number"));
        phone.setSingleLine(true);
        phone.setTextSize(15);
        phone.setPadding(Ui.dp(this,16),0,Ui.dp(this,16),0);
        phone.setBackground(Ui.bgStroke(this,android.graphics.Color.WHITE,18,Ui.LINE,1));
        add.addView(phone,Ui.mlp(this,-1,Ui.dp(this,56),0,10,0,10));
        Button invite = Ui.button(this,LanguageManager.t(this,"send_invite"),Ui.RED,android.graphics.Color.WHITE);
        invite.setOnClickListener(v -> sendInvite());
        add.addView(invite,Ui.lp(-1,Ui.dp(this,54)));
        content.addView(add,Ui.mlp(this,-1,-2,0,0,0,14));

        content.addView(Ui.text(this,LanguageManager.t(this,"pending_invites"),18,Ui.NAVY,Typeface.BOLD));
        invitesBox = Ui.vertical(this);
        content.addView(invitesBox,Ui.mlp(this,-1,-2,0,8,0,16));

        content.addView(Ui.text(this,LanguageManager.t(this,"your_contacts"),18,Ui.NAVY,Typeface.BOLD));
        contactsBox = Ui.vertical(this);
        content.addView(contactsBox,Ui.mlp(this,-1,-2,0,8,0,14));

        Button refresh = Ui.button(this,LanguageManager.t(this,"refresh"),android.graphics.Color.WHITE,Ui.NAVY);
        refresh.setOnClickListener(v -> refresh());
        content.addView(refresh,Ui.lp(-1,Ui.dp(this,54)));

        LinearLayout fallback = Ui.card(this);
        fallback.addView(Ui.text(this,"Local SMS/share fallback",17,Ui.NAVY,Typeface.BOLD));
        fallback.addView(Ui.text(this,MedicalProfile.trustedContactsText(this),13,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,7,0,8));
        Button editLocal = Ui.button(this,LanguageManager.t(this,"medical_id"),android.graphics.Color.WHITE,Ui.NAVY);
        editLocal.setOnClickListener(v -> startActivity(new Intent(this,MedicalProfileActivity.class)));
        fallback.addView(editLocal,Ui.lp(-1,Ui.dp(this,52)));
        content.addView(fallback,Ui.mlp(this,-1,-2,0,14,0,0));

        setContentView(Ui.scroll(this,content));
        refresh();
    }

    private void sendInvite() {
        if (!AuthSession.hasBearerToken(this)) {
            status.setText("Connect your account first.");
            startActivity(new Intent(this,FirebasePhoneLoginActivity.class));
            return;
        }
        String value = phone.getText().toString().trim();
        if (value.length() < 4) {
            Toast.makeText(this,LanguageManager.t(this,"phone_number"),Toast.LENGTH_SHORT).show();
            return;
        }
        ConnectedBackendClient.inviteTrustedContactAsync(this,value,(ok,body) -> {
            status.setText(ok ? "Invitation sent." : "Invitation failed: "+body);
            if (ok) {
                phone.setText("");
                refresh();
            }
        });
    }

    private void refresh() {
        invitesBox.removeAllViews();
        contactsBox.removeAllViews();
        if (!AuthSession.hasBearerToken(this)) {
            invitesBox.addView(Ui.text(this,"No connected account.",13,Ui.MUTED,Typeface.NORMAL));
            contactsBox.addView(Ui.text(this,"No connected account.",13,Ui.MUTED,Typeface.NORMAL));
            return;
        }

        ConnectedBackendClient.listTrustedInvitesAsync(this,(ok,body) -> {
            if (!ok) {
                invitesBox.addView(Ui.text(this,"Could not load invitations.",13,Ui.MUTED,Typeface.NORMAL));
                return;
            }
            try {
                JSONArray invites = new JSONObject(body).optJSONArray("invites");
                if (invites == null || invites.length() == 0) {
                    invitesBox.addView(Ui.text(this,"No pending invitations.",13,Ui.MUTED,Typeface.NORMAL));
                    return;
                }
                for (int i=0;i<invites.length();i++) {
                    JSONObject invite = invites.optJSONObject(i);
                    if (invite != null) invitesBox.addView(inviteCard(invite),Ui.mlp(this,-1,-2,0,0,0,8));
                }
            } catch(Exception e) {
                invitesBox.addView(Ui.text(this,"Invalid invitation response.",13,Ui.MUTED,Typeface.NORMAL));
            }
        });

        ConnectedBackendClient.listTrustedContactsAsync(this,(ok,body) -> {
            if (!ok) {
                contactsBox.addView(Ui.text(this,"Could not load contacts.",13,Ui.MUTED,Typeface.NORMAL));
                return;
            }
            try {
                JSONArray contacts = new JSONObject(body).optJSONArray("contacts");
                if (contacts == null || contacts.length() == 0) {
                    contactsBox.addView(Ui.text(this,"No connected trusted contacts.",13,Ui.MUTED,Typeface.NORMAL));
                    return;
                }
                for (int i=0;i<contacts.length();i++) {
                    JSONObject contact = contacts.optJSONObject(i);
                    if (contact != null) contactsBox.addView(contactCard(contact),Ui.mlp(this,-1,-2,0,0,0,8));
                }
            } catch(Exception e) {
                contactsBox.addView(Ui.text(this,"Invalid contact response.",13,Ui.MUTED,Typeface.NORMAL));
            }
        });
    }

    private LinearLayout inviteCard(JSONObject invite) {
        LinearLayout card = Ui.card(this);
        String id = invite.optString("inviteId");
        String name = invite.optString("ownerName","be lahza user");
        String phoneValue = invite.optString("ownerPhone","");
        card.addView(Ui.text(this,name,17,Ui.NAVY,Typeface.BOLD));
        if (!phoneValue.isEmpty()) card.addView(Ui.text(this,phoneValue,13,Ui.MUTED,Typeface.NORMAL));

        LinearLayout row = Ui.horizontal(this);
        Button accept = Ui.button(this,LanguageManager.t(this,"accept"),Ui.GREEN,android.graphics.Color.WHITE);
        Button reject = Ui.button(this,LanguageManager.t(this,"reject"),android.graphics.Color.WHITE,Ui.RED);
        accept.setOnClickListener(v -> ConnectedBackendClient.acceptTrustedContactAsync(this,id,(ok,body) -> refresh()));
        reject.setOnClickListener(v -> ConnectedBackendClient.rejectTrustedContactAsync(this,id,(ok,body) -> refresh()));
        row.addView(accept,weight(0,Ui.dp(this,50),0,10,6,0));
        row.addView(reject,weight(0,Ui.dp(this,50),6,10,0,0));
        card.addView(row);
        return card;
    }

    private LinearLayout contactCard(JSONObject contact) {
        LinearLayout card = Ui.card(this);
        String userId = contact.optString("contactUserId");
        String name = contact.optString("name","Trusted contact");
        String phoneValue = contact.optString("phone","");
        card.addView(Ui.text(this,name,17,Ui.NAVY,Typeface.BOLD));
        if (!phoneValue.isEmpty()) card.addView(Ui.text(this,phoneValue,13,Ui.MUTED,Typeface.NORMAL));
        Button remove = Ui.button(this,LanguageManager.t(this,"remove"),android.graphics.Color.WHITE,Ui.RED);
        remove.setOnClickListener(v -> ConnectedBackendClient.removeTrustedContactAsync(this,userId,(ok,body) -> refresh()));
        card.addView(remove,Ui.mlp(this,-1,Ui.dp(this,48),0,10,0,0));
        return card;
    }

    private LinearLayout.LayoutParams weight(int w,int h,int l,int t,int r,int b) {
        LinearLayout.LayoutParams lp = Ui.mlp(this,w,h,l,t,r,b);
        lp.weight=1;
        return lp;
    }
}
