package com.belahza.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NumbersActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.premiumBars(this);
        LinearLayout content=Ui.vertical(this);
        content.setLayoutDirection(LanguageManager.layoutDirection(this));
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);

        Button back=Ui.button(this,LanguageManager.t(this,"back"),android.graphics.Color.WHITE,Ui.NAVY);
        back.setOnClickListener(v->finish());
        content.addView(back,Ui.mlp(this,Ui.dp(this,110),Ui.dp(this,48),0,0,0,14));
        content.addView(Ui.text(this,LanguageManager.t(this,"emergency_numbers"),30,Ui.NAVY,Typeface.BOLD));
        content.addView(Ui.text(this,LanguageManager.t(this,"numbers_desc"),15,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,5,0,18));

        for(EmergencyData.NumberItem item:EmergencyData.numbers()) {
            content.addView(numberCard(item),Ui.mlp(this,-1,-2,0,0,0,12));
        }

        TextView tip=Ui.text(this,LanguageManager.t(this,"number_tip"),13,Ui.MUTED,Typeface.BOLD);
        tip.setGravity(Gravity.CENTER);
        content.addView(tip,Ui.mlp(this,-1,-2,0,14,0,0));
        setContentView(Ui.scroll(this,content));
    }

    private LinearLayout numberCard(EmergencyData.NumberItem item) {
        LinearLayout card=Ui.card(this);
        card.setLayoutDirection(LanguageManager.layoutDirection(this));
        LinearLayout top=Ui.horizontal(this);
        top.setLayoutDirection(LanguageManager.layoutDirection(this));
        TextView title=Ui.text(this,translatedTitle(item.number,item.title),18,Ui.NAVY,Typeface.BOLD);
        TextView number=Ui.text(this,item.number,30,item.accent,Typeface.BOLD);
        top.addView(title,Ui.lp(0,-2));
        ((LinearLayout.LayoutParams)title.getLayoutParams()).weight=1;
        top.addView(number);
        card.addView(top);
        card.addView(Ui.text(this,translatedSubtitle(item.number,item.subtitle),14,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,4,0,12));

        LinearLayout actions=Ui.horizontal(this);
        Button call=Ui.button(this,LanguageManager.t(this,"call"),item.accent,android.graphics.Color.WHITE);
        Button copy=Ui.button(this,LanguageManager.t(this,"copy"),android.graphics.Color.WHITE,Ui.NAVY);
        call.setOnClickListener(v->Dialer.call(this,item.number));
        copy.setOnClickListener(v->Dialer.copy(this,item.title,item.number));
        actions.addView(call,weight(0,Ui.dp(this,50),0,0,6,0));
        actions.addView(copy,weight(0,Ui.dp(this,50),6,0,0,0));
        card.addView(actions);
        return card;
    }

    private String translatedTitle(String number,String fallback) {
        String lang=LanguageManager.current(this);
        if(LanguageManager.AR.equals(lang)) {
            if("140".equals(number))return"الصليب الأحمر اللبناني";
            if("112".equals(number))return"قوى الأمن الداخلي";
            if("125".equals(number))return"الدفاع المدني";
            if("175".equals(number))return"فوج الإطفاء";
            if("1701".equals(number))return"الجيش اللبناني";
            if("1787".equals(number))return"وزارة الصحة";
            if("1744".equals(number))return"حالة الطرق";
        }
        if(LanguageManager.FR.equals(lang)) {
            if("140".equals(number))return"Croix-Rouge libanaise";
            if("112".equals(number))return"Forces de sécurité intérieure";
            if("125".equals(number))return"Défense civile";
            if("175".equals(number))return"Pompiers";
            if("1701".equals(number))return"Armée libanaise";
            if("1787".equals(number))return"Ministère de la Santé";
            if("1744".equals(number))return"État des routes";
        }
        return fallback;
    }

    private String translatedSubtitle(String number,String fallback) {
        String lang=LanguageManager.current(this);
        if(LanguageManager.AR.equals(lang)) {
            if("140".equals(number))return"إسعاف وطوارئ طبية";
            if("112".equals(number))return"شرطة وخطر مباشر";
            if("125".equals(number))return"إنقاذ وحرائق وفيضانات";
            if("175".equals(number))return"استجابة للحرائق";
        }
        if(LanguageManager.FR.equals(lang)) {
            if("140".equals(number))return"Ambulance et urgence médicale";
            if("112".equals(number))return"Police et danger immédiat";
            if("125".equals(number))return"Secours, incendie et inondation";
            if("175".equals(number))return"Intervention incendie";
        }
        return fallback;
    }

    private LinearLayout.LayoutParams weight(int w,int h,int l,int t,int r,int b) {
        LinearLayout.LayoutParams lp=Ui.mlp(this,w,h,l,t,r,b);
        lp.weight=1;
        return lp;
    }
}
