package com.belahza.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity {
    private LinearLayout emergencyList;
    private final List<EmergencyData.Emergency> all = EmergencyData.emergencies();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.premiumBars(this);

        LinearLayout content = Ui.vertical(this);
        content.setLayoutDirection(LanguageManager.layoutDirection(this));
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);

        content.addView(header());

        if (EmergencyMode.isActive(this)) {
            LinearLayout active = Ui.card(this);
            active.setBackground(Ui.bgStroke(this,0xFFFFF3F3,22,Ui.RED,1));
            active.setOnClickListener(v -> startActivity(new Intent(this,ActiveEmergencyActivity.class)));
            active.setContentDescription(LanguageManager.t(this,"active_title"));
            active.addView(Ui.text(this,LanguageManager.t(this,"active_title"),20,Ui.RED,Typeface.BOLD));
            active.addView(Ui.text(this,EmergencyMode.emergencyType(this),14,Ui.INK,Typeface.BOLD),Ui.mlp(this,-1,-2,0,5,0,0));
            active.addView(Ui.text(this,LanguageManager.t(this,"continue"),13,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,6,0,0));
            content.addView(active,Ui.mlp(this,-1,-2,0,14,0,0));
        }

        content.addView(sosHero(),Ui.mlp(this,-1,-2,0,16,0,18));

        LinearLayout primary = Ui.vertical(this);
        primary.addView(publicAction(LanguageManager.t(this,"emergency_numbers"),"140 · 112 · 125 · 175","☎",Ui.NAVY,NumbersActivity.class),Ui.mlp(this,-1,-2,0,0,0,10));
        primary.addView(publicAction(LanguageManager.t(this,"trusted_contacts"),LanguageManager.t(this,"alert_contacts"),"◎",Ui.GREEN,TrustedContactsActivity.class),Ui.mlp(this,-1,-2,0,0,0,10));
        primary.addView(publicAction(LanguageManager.t(this,"medical_id"),LanguageManager.t(this,"medical_privacy"),"✚",Ui.RED,MedicalProfileActivity.class),Ui.mlp(this,-1,-2,0,0,0,10));
        primary.addView(publicAction(LanguageManager.t(this,"settings"),LanguageManager.t(this,"language"),"⚙",Ui.NAVY,SettingsActivity.class),Ui.mlp(this,-1,-2,0,0,0,18));
        content.addView(primary);

        content.addView(Ui.text(this,LanguageManager.t(this,"search_title"),27,Ui.NAVY,Typeface.BOLD));
        EditText search = new EditText(this);
        search.setHint(LanguageManager.t(this,"search_hint"));
        search.setSingleLine(true);
        search.setTextSize(16);
        search.setPadding(Ui.dp(this,18),0,Ui.dp(this,18),0);
        search.setMinHeight(Ui.dp(this,56));
        search.setBackground(Ui.bgStroke(this,android.graphics.Color.WHITE,20,Ui.LINE,1));
        search.setContentDescription(LanguageManager.t(this,"search_hint"));
        content.addView(search,Ui.mlp(this,-1,-2,0,10,0,6));
        content.addView(Ui.text(this,LanguageManager.t(this,"search_help"),12,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,0,0,14));

        emergencyList = Ui.vertical(this);
        emergencyList.setLayoutDirection(LanguageManager.layoutDirection(this));
        content.addView(emergencyList);
        render("");

        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s,int start,int count,int after) {}
            @Override public void onTextChanged(CharSequence s,int start,int before,int count) { render(s == null ? "" : s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        TextView footer = Ui.text(this,LanguageManager.t(this,"guide_disclaimer"),12,Ui.MUTED,Typeface.NORMAL);
        footer.setGravity(Gravity.CENTER);
        content.addView(footer,Ui.mlp(this,-1,-2,0,18,0,0));
        setContentView(Ui.scroll(this,content));
    }

    @Override protected void onResume() {
        super.onResume();
    }

    private View header() {
        LinearLayout row = Ui.horizontal(this);
        row.setLayoutDirection(LanguageManager.layoutDirection(this));
        ImageView logo = Ui.logo(this);
        logo.setContentDescription("be lahza");
        row.addView(logo,Ui.mlp(this,Ui.dp(this,68),Ui.dp(this,68),0,0,12,0));
        LinearLayout words = Ui.vertical(this);
        words.addView(Ui.text(this,"be lahza",30,Ui.NAVY,Typeface.BOLD));
        words.addView(Ui.text(this,LanguageManager.t(this,"app_tagline"),14,Ui.MUTED,Typeface.NORMAL));
        row.addView(words,Ui.lp(0,-2));
        ((LinearLayout.LayoutParams)words.getLayoutParams()).weight=1;
        Button language = Ui.button(this,LanguageManager.languageName(this),android.graphics.Color.WHITE,Ui.NAVY);
        language.setContentDescription(LanguageManager.t(this,"choose_language"));
        language.setOnClickListener(v -> startActivity(new Intent(this,LanguageActivity.class)));
        row.addView(language,Ui.mlp(this,Ui.dp(this,112),Ui.dp(this,48),10,0,0,0));
        return row;
    }

    private View sosHero() {
        LinearLayout hero = Ui.vertical(this);
        hero.setLayoutDirection(LanguageManager.layoutDirection(this));
        hero.setPadding(Ui.dp(this,20),Ui.dp(this,20),Ui.dp(this,20),Ui.dp(this,20));
        hero.setBackground(Ui.gradient(this,Ui.RED,Ui.RED_DARK,30));
        hero.addView(Ui.text(this,LanguageManager.t(this,"sos"),34,android.graphics.Color.WHITE,Typeface.BOLD));
        TextView body = Ui.text(this,LanguageManager.t(this,"sos_desc"),15,android.graphics.Color.WHITE,Typeface.NORMAL);
        body.setAlpha(.95f);
        hero.addView(body,Ui.mlp(this,-1,-2,0,6,0,16));

        Button start = Ui.button(this,LanguageManager.t(this,"start_sos"),android.graphics.Color.WHITE,Ui.RED);
        start.setContentDescription(LanguageManager.t(this,"start_sos"));
        start.setOnClickListener(v -> {
            Intent i = new Intent(this, EmergencyCountdownActivity.class);
            i.putExtra(EmergencyData.EXTRA_KEY,"emergency");
            i.putExtra("emergency_type","Emergency");
            startActivity(i);
        });
        hero.addView(start,Ui.lp(-1,Ui.dp(this,58)));

        LinearLayout calls = Ui.horizontal(this);
        calls.setLayoutDirection(LanguageManager.layoutDirection(this));
        Button call140 = Ui.button(this,LanguageManager.t(this,"call_140"),0x33FFFFFF,android.graphics.Color.WHITE);
        Button call112 = Ui.button(this,LanguageManager.t(this,"call_112"),0x33FFFFFF,android.graphics.Color.WHITE);
        call140.setOnClickListener(v -> Dialer.call(this,"140"));
        call112.setOnClickListener(v -> Dialer.call(this,"112"));
        calls.addView(call140,weight(0,Ui.dp(this,50),0,10,6,0));
        calls.addView(call112,weight(0,Ui.dp(this,50),6,10,0,0));
        hero.addView(calls);
        return hero;
    }

    private View publicAction(String title,String subtitle,String icon,int accent,Class<?> target) {
        LinearLayout card = Ui.horizontal(this);
        card.setLayoutDirection(LanguageManager.layoutDirection(this));
        card.setPadding(Ui.dp(this,16),Ui.dp(this,15),Ui.dp(this,16),Ui.dp(this,15));
        card.setBackground(Ui.bgStroke(this,android.graphics.Color.WHITE,22,Ui.LINE,1));
        card.setMinHeight(Ui.dp(this,76));
        card.setContentDescription(title + ". " + subtitle);
        card.setOnClickListener(v -> startActivity(new Intent(this,target)));
        TextView symbol = Ui.text(this,icon,25,accent,Typeface.BOLD);
        symbol.setGravity(Gravity.CENTER);
        card.addView(symbol,Ui.mlp(this,Ui.dp(this,42),Ui.dp(this,42),0,0,14,0));
        LinearLayout text = Ui.vertical(this);
        text.addView(Ui.text(this,title,17,Ui.NAVY,Typeface.BOLD));
        text.addView(Ui.text(this,subtitle,13,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,3,0,0));
        card.addView(text,Ui.lp(0,-2));
        ((LinearLayout.LayoutParams)text.getLayoutParams()).weight=1;
        TextView arrow = Ui.text(this,LanguageManager.isArabic(this) ? "‹" : "›",28,Ui.MUTED,Typeface.NORMAL);
        card.addView(arrow);
        return card;
    }

    private void render(String query) {
        emergencyList.removeAllViews();
        List<EmergencyData.Emergency> filtered = SmartSearch.filter(this,all,query);
        for (EmergencyData.Emergency emergency : filtered) {
            emergencyList.addView(emergencyCard(emergency),Ui.mlp(this,-1,-2,0,0,0,10));
        }
        if (filtered.isEmpty()) {
            emergencyList.addView(Ui.text(this,LanguageManager.t(this,"no_match"),15,Ui.MUTED,Typeface.NORMAL));
        }
    }

    private View emergencyCard(EmergencyData.Emergency emergency) {
        LinearLayout card = Ui.horizontal(this);
        card.setLayoutDirection(LanguageManager.layoutDirection(this));
        card.setPadding(Ui.dp(this,16),Ui.dp(this,15),Ui.dp(this,16),Ui.dp(this,15));
        card.setBackground(Ui.bgStroke(this,android.graphics.Color.WHITE,22,Ui.LINE,1));
        card.setMinHeight(Ui.dp(this,104));
        String title = LanguageManager.emergencyTitle(this,emergency);
        String summary = LanguageManager.emergencyLine(this,emergency);
        card.setContentDescription(title + ". " + summary);
        card.setOnClickListener(v -> {
            Intent i = new Intent(this,GuideActivity.class);
            i.putExtra(EmergencyData.EXTRA_KEY,emergency.key);
            startActivity(i);
        });

        TextView marker = Ui.text(this,"!",20,android.graphics.Color.WHITE,Typeface.BOLD);
        marker.setGravity(Gravity.CENTER);
        marker.setBackground(Ui.bgDp(this,emergency.accent,18));
        card.addView(marker,Ui.mlp(this,Ui.dp(this,40),Ui.dp(this,40),0,0,14,0));

        LinearLayout text = Ui.vertical(this);
        TextView category = Ui.text(this,LanguageManager.emergencyCategory(this,emergency),11,emergency.accent,Typeface.BOLD);
        category.setAllCaps(true);
        text.addView(category);
        text.addView(Ui.text(this,title,18,Ui.NAVY,Typeface.BOLD),Ui.mlp(this,-1,-2,0,3,0,2));
        text.addView(Ui.text(this,summary,13,Ui.MUTED,Typeface.NORMAL));
        card.addView(text,Ui.lp(0,-2));
        ((LinearLayout.LayoutParams)text.getLayoutParams()).weight=1;
        return card;
    }

    private LinearLayout.LayoutParams weight(int w,int h,int l,int t,int r,int b) {
        LinearLayout.LayoutParams lp = Ui.mlp(this,w,h,l,t,r,b);
        lp.weight=1;
        return lp;
    }
}
