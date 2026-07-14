package com.belahza.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {
    @Override protected void onCreate(Bundle b){ super.onCreate(b); Ui.premiumBars(this); LinearLayout root=Ui.vertical(this); root.setGravity(Gravity.CENTER); root.setPadding(Ui.dp(this,28),Ui.dp(this,28),Ui.dp(this,28),Ui.dp(this,28)); root.setBackgroundColor(Ui.SOFT); ImageView logo=Ui.logo(this); root.addView(logo,Ui.mlp(this,Ui.dp(this,250),Ui.dp(this,250),0,0,0,16)); TextView title=Ui.text(this,"be lahza",36,Ui.NAVY,Typeface.BOLD); title.setGravity(Gravity.CENTER); root.addView(title,Ui.lp(-1,-2)); TextView tag=Ui.text(this,"في لحظة. حياة ممكن تنقذ.\nIn a moment. A life can be saved.",15,Ui.MUTED,Typeface.NORMAL); tag.setGravity(Gravity.CENTER); root.addView(tag,Ui.mlp(this,-1,-2,0,12,0,0)); setContentView(root); new Handler(Looper.getMainLooper()).postDelayed(()->{startActivity(new Intent(this,MainActivity.class));finish();},900); }
}
