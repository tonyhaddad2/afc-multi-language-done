package com.belahza.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.List;

public class AnimationLessonActivity extends Activity {
    private EmergencyData.Emergency emergency;
    private List<String> steps;
    private int index = 0;
    private TextView stepNumber,stepText;
    private ProgressBar progress;
    private VideoView video;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.premiumBars(this);
        emergency = EmergencyData.get(getIntent().getStringExtra(EmergencyData.EXTRA_KEY));
        steps = EmergencyTranslations.steps(LanguageManager.current(this),emergency);

        LinearLayout root = Ui.vertical(this);
        root.setLayoutDirection(LanguageManager.layoutDirection(this));
        root.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,24));
        root.setBackgroundColor(Ui.SOFT);

        Button close = Ui.button(this,LanguageManager.t(this,"close"),android.graphics.Color.WHITE,Ui.NAVY);
        close.setOnClickListener(v -> finish());
        root.addView(close,Ui.mlp(this,Ui.dp(this,130),Ui.dp(this,48),0,0,0,12));

        root.addView(Ui.text(this,LanguageManager.emergencyTitle(this,emergency),27,Ui.NAVY,Typeface.BOLD));
        root.addView(Ui.text(this,LanguageManager.t(this,"animation_desc"),13,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,5,0,12));

        video = new VideoView(this);
        video.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+emergency.videoRes));
        video.setOnPreparedListener(player -> {
            player.setLooping(true);
            player.setVolume(0f,0f);
            video.start();
        });
        video.setOnErrorListener((MediaPlayer player,int what,int extra) -> true);
        root.addView(video,Ui.mlp(this,-1,0,0,0,0,14));
        ((LinearLayout.LayoutParams)video.getLayoutParams()).weight=1;

        LinearLayout panel = Ui.card(this);
        stepNumber = Ui.text(this,"",13,emergency.accent,Typeface.BOLD);
        stepText = Ui.text(this,"",23,Ui.NAVY,Typeface.BOLD);
        stepText.setGravity(Gravity.CENTER_VERTICAL);
        progress = new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        progress.setMax(steps.size());
        panel.addView(stepNumber);
        panel.addView(stepText,Ui.mlp(this,-1,-2,0,6,0,12));
        panel.addView(progress,Ui.lp(-1,Ui.dp(this,10)));

        LinearLayout controls = Ui.horizontal(this);
        Button previous = Ui.button(this,LanguageManager.t(this,"previous"),android.graphics.Color.WHITE,Ui.NAVY);
        Button play = Ui.button(this,LanguageManager.t(this,"play_pause"),Ui.NAVY,android.graphics.Color.WHITE);
        Button next = Ui.button(this,LanguageManager.t(this,"next"),Ui.RED,android.graphics.Color.WHITE);
        previous.setOnClickListener(v -> { if(index>0) index--; render(); });
        next.setOnClickListener(v -> { if(index<steps.size()-1) index++; render(); });
        play.setOnClickListener(v -> { if(video.isPlaying()) video.pause(); else video.start(); });
        controls.addView(previous,weight(0,Ui.dp(this,50),0,12,6,0));
        controls.addView(play,weight(0,Ui.dp(this,50),6,12,6,0));
        controls.addView(next,weight(0,Ui.dp(this,50),6,12,0,0));
        panel.addView(controls);
        root.addView(panel);

        setContentView(root);
        render();
    }

    private void render() {
        stepNumber.setText(LanguageManager.t(this,"lesson_step")+" "+(index+1)+" / "+steps.size());
        stepText.setText(steps.get(index));
        progress.setProgress(index+1);
    }

    private LinearLayout.LayoutParams weight(int w,int h,int l,int t,int r,int b) {
        LinearLayout.LayoutParams lp=Ui.mlp(this,w,h,l,t,r,b);
        lp.weight=1;
        return lp;
    }
}
