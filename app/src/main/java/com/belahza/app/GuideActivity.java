package com.belahza.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.List;

public class GuideActivity extends Activity {
    private EmergencyData.Emergency emergency;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.premiumBars(this);
        emergency = EmergencyData.get(getIntent().getStringExtra(EmergencyData.EXTRA_KEY));

        LinearLayout content = Ui.vertical(this);
        content.setLayoutDirection(LanguageManager.layoutDirection(this));
        content.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));
        content.setBackgroundColor(Ui.SOFT);

        Button back = Ui.button(this,LanguageManager.t(this,"back"),android.graphics.Color.WHITE,Ui.NAVY);
        back.setOnClickListener(v -> finish());
        content.addView(back,Ui.mlp(this,Ui.dp(this,110),Ui.dp(this,48),0,0,0,14));

        content.addView(Ui.text(this,LanguageManager.emergencyTitle(this,emergency),30,Ui.NAVY,Typeface.BOLD));
        if (!LanguageManager.isArabic(this)) {
            content.addView(Ui.text(this,emergency.arabic,20,emergency.accent,Typeface.BOLD),Ui.mlp(this,-1,-2,0,2,0,6));
        }
        content.addView(Ui.text(this,LanguageManager.emergencyLine(this,emergency),15,Ui.MUTED,Typeface.NORMAL));

        LinearLayout urgent = Ui.vertical(this);
        urgent.setPadding(Ui.dp(this,16),Ui.dp(this,15),Ui.dp(this,16),Ui.dp(this,15));
        urgent.setBackground(Ui.gradient(this,emergency.accent,Ui.RED_DARK,24));
        urgent.addView(Ui.text(this,LanguageManager.t(this,"emergency_first"),18,android.graphics.Color.WHITE,Typeface.BOLD));
        TextView urgentBody = Ui.text(this,LanguageManager.t(this,"emergency_first_desc"),14,android.graphics.Color.WHITE,Typeface.NORMAL);
        urgentBody.setAlpha(.95f);
        urgent.addView(urgentBody,Ui.mlp(this,-1,-2,0,5,0,12));
        LinearLayout urgentActions = Ui.horizontal(this);
        Button call = Ui.button(this,LanguageManager.t(this,"call")+" "+emergency.callTarget,android.graphics.Color.WHITE,emergency.accent);
        Button alert = Ui.button(this,LanguageManager.t(this,"alert_contacts"),0x33FFFFFF,android.graphics.Color.WHITE);
        call.setOnClickListener(v -> Dialer.call(this,emergency.callTarget));
        alert.setOnClickListener(v -> {
            Intent i = new Intent(this,SosActivity.class);
            i.putExtra(EmergencyData.EXTRA_KEY,emergency.key);
            startActivity(i);
        });
        urgentActions.addView(call,weight(0,Ui.dp(this,52),0,0,6,0));
        urgentActions.addView(alert,weight(0,Ui.dp(this,52),6,0,0,0));
        urgent.addView(urgentActions);
        content.addView(urgent,Ui.mlp(this,-1,-2,0,18,0,16));

        LinearLayout visual = Ui.card(this);
        visual.addView(Ui.text(this,LanguageManager.t(this,"animation"),19,Ui.NAVY,Typeface.BOLD));
        visual.addView(Ui.text(this,LanguageManager.t(this,"animation_desc"),13,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,5,0,10));
        VideoView video = new VideoView(this);
        Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+emergency.videoRes);
        video.setVideoURI(uri);
        MediaController controller = new MediaController(this);
        controller.setAnchorView(video);
        video.setMediaController(controller);
        video.setOnPreparedListener(player -> {
            player.setLooping(true);
            player.setVolume(0f,0f);
            video.start();
        });
        video.setOnErrorListener((MediaPlayer player,int what,int extra) -> true);
        visual.addView(video,Ui.mlp(this,-1,Ui.dp(this,230),0,0,0,10));

        Button lesson = Ui.button(this,LanguageManager.t(this,"full_lesson"),Ui.NAVY,android.graphics.Color.WHITE);
        lesson.setOnClickListener(v -> {
            Intent i = new Intent(this,AnimationLessonActivity.class);
            i.putExtra(EmergencyData.EXTRA_KEY,emergency.key);
            startActivity(i);
        });
        visual.addView(lesson,Ui.lp(-1,Ui.dp(this,54)));
        content.addView(visual,Ui.mlp(this,-1,-2,0,0,0,16));

        if ("cardiac".equals(emergency.key) || "drowning".equals(emergency.key)) {
            LinearLayout coach = Ui.card(this);
            coach.addView(Ui.text(this,LanguageManager.t(this,"ai_coach"),18,Ui.NAVY,Typeface.BOLD));
            coach.addView(Ui.text(this,LanguageManager.t(this,"ai_limits"),13,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,5,0,10));
            Button open = Ui.button(this,LanguageManager.t(this,"ai_coach"),Ui.RED,android.graphics.Color.WHITE);
            open.setOnClickListener(v -> {
                Intent i = new Intent(this,AiCoachActivity.class);
                i.putExtra(EmergencyData.EXTRA_KEY,emergency.key);
                startActivity(i);
            });
            coach.addView(open,Ui.lp(-1,Ui.dp(this,54)));
            content.addView(coach,Ui.mlp(this,-1,-2,0,0,0,16));
        }

        content.addView(Ui.text(this,LanguageManager.t(this,"what_to_do"),19,Ui.NAVY,Typeface.BOLD),Ui.mlp(this,-1,-2,0,2,0,8));
        List<String> steps = EmergencyTranslations.steps(LanguageManager.current(this),emergency);
        for (int i=0;i<steps.size();i++) {
            content.addView(stepCard(String.valueOf(i+1),steps.get(i),false),Ui.mlp(this,-1,-2,0,0,0,9));
        }

        content.addView(Ui.text(this,LanguageManager.t(this,"do_not"),19,Ui.RED,Typeface.BOLD),Ui.mlp(this,-1,-2,0,14,0,8));
        for (String item : EmergencyTranslations.donts(LanguageManager.current(this),emergency)) {
            content.addView(stepCard("!",item,true),Ui.mlp(this,-1,-2,0,0,0,9));
        }

        TextView disclaimer = Ui.text(this,LanguageManager.t(this,"guide_disclaimer"),12,Ui.MUTED,Typeface.NORMAL);
        disclaimer.setGravity(Gravity.CENTER);
        content.addView(disclaimer,Ui.mlp(this,-1,-2,0,18,0,0));
        setContentView(Ui.scroll(this,content));
    }

    private LinearLayout stepCard(String badge,String text,boolean danger) {
        LinearLayout card = Ui.horizontal(this);
        card.setLayoutDirection(LanguageManager.layoutDirection(this));
        card.setPadding(Ui.dp(this,14),Ui.dp(this,14),Ui.dp(this,14),Ui.dp(this,14));
        card.setBackground(Ui.bgStroke(this,android.graphics.Color.WHITE,20,danger?0xFFFFD0D0:Ui.LINE,1));
        TextView number = Ui.text(this,badge,16,android.graphics.Color.WHITE,Typeface.BOLD);
        number.setGravity(Gravity.CENTER);
        number.setBackground(Ui.bgDp(this,danger?Ui.RED:emergency.accent,18));
        card.addView(number,Ui.mlp(this,Ui.dp(this,36),Ui.dp(this,36),0,0,12,0));
        TextView body = Ui.text(this,text,15,danger?Ui.RED_DARK:Ui.INK,danger?Typeface.BOLD:Typeface.NORMAL);
        card.addView(body,Ui.lp(0,-2));
        ((LinearLayout.LayoutParams)body.getLayoutParams()).weight=1;
        return card;
    }

    private LinearLayout.LayoutParams weight(int w,int h,int l,int t,int r,int b) {
        LinearLayout.LayoutParams lp = Ui.mlp(this,w,h,l,t,r,b);
        lp.weight=1;
        return lp;
    }
}
