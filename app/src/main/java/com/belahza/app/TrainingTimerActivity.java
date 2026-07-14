package com.belahza.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TrainingTimerActivity extends Activity {
    private final Handler handler=new Handler(Looper.getMainLooper()); private boolean running=false; private int bpm=110; private long startMs; private TextView beatView,timerView,guidance,bpmText; private ProgressBar progress; private ToneGenerator tone;
    private final Runnable tick=new Runnable(){@Override public void run(){if(!running)return;long elapsed=System.currentTimeMillis()-startMs;int sec=(int)(elapsed/1000);int remaining=Math.max(0,120-sec);timerView.setText(String.format(java.util.Locale.US,"%02d:%02d",remaining/60,remaining%60));progress.setProgress(Math.min(120,sec));int beat=(int)((elapsed/(60000f/bpm))%30)+1;beatView.setText(String.valueOf(beat));guidance.setText(beat==30?"Give breaths only if trained. Keep pauses short.":"Push hard and fast. Let chest fully recoil.");pulse();handler.postDelayed(this,(long)(60000f/bpm));}};
    @Override protected void onCreate(Bundle b){super.onCreate(b);Ui.premiumBars(this);tone=new ToneGenerator(AudioManager.STREAM_MUSIC,55);LinearLayout root=Ui.vertical(this);root.setPadding(Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,18),Ui.dp(this,28));root.setGravity(Gravity.CENTER_HORIZONTAL);root.setBackgroundColor(Ui.SOFT);Button back=Ui.button(this,"← Back",android.graphics.Color.WHITE,Ui.NAVY);back.setOnClickListener(v->finish());root.addView(back,Ui.mlp(this,Ui.dp(this,110),Ui.dp(this,46),0,0,0,14));TextView title=Ui.text(this,"CPR Rhythm Trainer",30,Ui.NAVY,Typeface.BOLD);title.setGravity(Gravity.CENTER);TextView sub=Ui.text(this,"Practice at 100–120 compressions/min. Default: 110 bpm. Training mode only.",15,Ui.MUTED,Typeface.NORMAL);sub.setGravity(Gravity.CENTER);root.addView(title,Ui.lp(-1,-2));root.addView(sub,Ui.mlp(this,-1,-2,0,4,0,18));LinearLayout circle=Ui.vertical(this);circle.setGravity(Gravity.CENTER);circle.setPadding(Ui.dp(this,24),Ui.dp(this,24),Ui.dp(this,24),Ui.dp(this,24));circle.setBackground(Ui.gradient(this,Ui.RED,Ui.RED_DARK,180));beatView=Ui.text(this,"0",70,android.graphics.Color.WHITE,Typeface.BOLD);beatView.setGravity(Gravity.CENTER);TextView beatLabel=Ui.text(this,"compression count",13,android.graphics.Color.WHITE,Typeface.BOLD);beatLabel.setGravity(Gravity.CENTER);beatLabel.setAlpha(.9f);circle.addView(beatView);circle.addView(beatLabel);root.addView(circle,Ui.mlp(this,Ui.dp(this,250),Ui.dp(this,250),0,0,0,20));timerView=Ui.text(this,"02:00",36,Ui.NAVY,Typeface.BOLD);timerView.setGravity(Gravity.CENTER);root.addView(timerView,Ui.lp(-1,-2));progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);progress.setMax(120);root.addView(progress,Ui.mlp(this,-1,Ui.dp(this,10),0,8,0,18));guidance=Ui.text(this,"Press Start and practice on a mannequin or training pillow.",16,Ui.NAVY,Typeface.BOLD);guidance.setGravity(Gravity.CENTER);root.addView(guidance,Ui.mlp(this,-1,-2,0,0,0,18));LinearLayout controls=Ui.horizontal(this);Button slower=Ui.button(this,"− BPM",android.graphics.Color.WHITE,Ui.NAVY);Button start=Ui.button(this,"Start",Ui.RED,android.graphics.Color.WHITE);Button faster=Ui.button(this,"+ BPM",android.graphics.Color.WHITE,Ui.NAVY);controls.addView(slower,weight(0,Ui.dp(this,54),0,0,6,0));controls.addView(start,weight(0,Ui.dp(this,54),6,0,6,0));controls.addView(faster,weight(0,Ui.dp(this,54),6,0,0,0));root.addView(controls);bpmText=Ui.text(this,bpm+" bpm",18,Ui.MUTED,Typeface.BOLD);bpmText.setGravity(Gravity.CENTER);root.addView(bpmText,Ui.mlp(this,-1,-2,0,16,0,0));slower.setOnClickListener(v->{bpm=Math.max(100,bpm-5);bpmText.setText(bpm+" bpm");});faster.setOnClickListener(v->{bpm=Math.min(120,bpm+5);bpmText.setText(bpm+" bpm");});start.setOnClickListener(v->{running=!running;start.setText(running?"Stop":"Start");if(running){startMs=System.currentTimeMillis();handler.removeCallbacks(tick);tick.run();}else handler.removeCallbacks(tick);});setContentView(root);}    
    private void pulse(){try{tone.startTone(ToneGenerator.TONE_PROP_BEEP,55);}catch(Exception ignored){}try{Vibrator v=(Vibrator)getSystemService(VIBRATOR_SERVICE);if(v!=null){if(Build.VERSION.SDK_INT>=26)v.vibrate(VibrationEffect.createOneShot(35,VibrationEffect.DEFAULT_AMPLITUDE));else v.vibrate(35);}}catch(Exception ignored){}}
    @Override protected void onDestroy(){handler.removeCallbacksAndMessages(null);if(tone!=null)tone.release();super.onDestroy();}
    private LinearLayout.LayoutParams weight(int w,int h,int l,int t,int r,int b){LinearLayout.LayoutParams lp=Ui.mlp(this,w,h,l,t,r,b);lp.weight=1;return lp;}
}
