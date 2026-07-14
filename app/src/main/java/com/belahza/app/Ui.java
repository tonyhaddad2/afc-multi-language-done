package com.belahza.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public final class Ui {
    public static final int RED = Color.rgb(231, 25, 36);
    public static final int RED_DARK = Color.rgb(172, 13, 30);
    public static final int NAVY = Color.rgb(7, 29, 51);
    public static final int INK = Color.rgb(16, 19, 26);
    public static final int MUTED = Color.rgb(91, 103, 123);
    public static final int SOFT = Color.rgb(247, 248, 252);
    public static final int CARD = Color.WHITE;
    public static final int LINE = Color.rgb(226, 230, 238);
    public static final int GREEN = Color.rgb(20, 149, 92);
    private Ui() {}
    public static int dp(Context c, float v) { return (int) (v * c.getResources().getDisplayMetrics().density + 0.5f); }
    public static void premiumBars(Activity a) { Window w = a.getWindow(); w.setStatusBarColor(NAVY); w.setNavigationBarColor(Color.WHITE); if (Build.VERSION.SDK_INT >= 26) w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR); }
    public static GradientDrawable bg(int color, float radiusPx) { GradientDrawable g = new GradientDrawable(); g.setColor(color); g.setCornerRadius(radiusPx); return g; }
    public static GradientDrawable bgDp(Context c, int color, float radiusDp) { return bg(color, dp(c, radiusDp)); }
    public static GradientDrawable bgStroke(Context c, int color, float radiusDp, int strokeColor, float strokeDp) { GradientDrawable g = bgDp(c, color, radiusDp); g.setStroke(dp(c, strokeDp), strokeColor); return g; }
    public static GradientDrawable gradient(Context c, int start, int end, float radiusDp) { GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{start, end}); g.setCornerRadius(dp(c, radiusDp)); return g; }
    public static TextView text(Context c, String value, int sp, int color, int style) { TextView t = new TextView(c); t.setText(value); t.setTextSize(sp); t.setTextColor(color); t.setTypeface(Typeface.DEFAULT, style); t.setLineSpacing(dp(c,2), 1.0f); return t; }
    public static TextView label(Context c, String value) { TextView t = text(c, value, 12, MUTED, Typeface.BOLD); t.setLetterSpacing(0.08f); t.setAllCaps(true); return t; }
    public static Button button(Context c, String label, int bgColor, int textColor) { Button b = new Button(c); b.setText(label); b.setAllCaps(false); b.setTextSize(15); b.setTextColor(textColor); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD); b.setPadding(dp(c,14),0,dp(c,14),0); b.setMinHeight(dp(c,48)); GradientDrawable gd = bgDp(c, bgColor, 18); if (Build.VERSION.SDK_INT >= 21) b.setBackground(new RippleDrawable(ColorStateList.valueOf(Color.argb(42,0,0,0)), gd, null)); else b.setBackground(gd); return b; }
    public static LinearLayout vertical(Context c) { LinearLayout l = new LinearLayout(c); l.setOrientation(LinearLayout.VERTICAL); return l; }
    public static LinearLayout horizontal(Context c) { LinearLayout l = new LinearLayout(c); l.setOrientation(LinearLayout.HORIZONTAL); l.setGravity(Gravity.CENTER_VERTICAL); return l; }
    public static LinearLayout card(Context c) { LinearLayout card = vertical(c); card.setPadding(dp(c,18), dp(c,16), dp(c,18), dp(c,16)); card.setBackground(bgStroke(c, CARD, 26, LINE, 1)); if (Build.VERSION.SDK_INT >= 21) card.setElevation(dp(c,2)); return card; }
    public static ScrollView scroll(Context c, LinearLayout content) { ScrollView s = new ScrollView(c); s.setFillViewport(false); s.setBackgroundColor(SOFT); s.addView(content, new ScrollView.LayoutParams(-1, -2)); return s; }
    public static LinearLayout.LayoutParams lp(int w, int h) { return new LinearLayout.LayoutParams(w, h); }
    public static LinearLayout.LayoutParams mlp(Context c, int w, int h, int l, int t, int r, int b) { LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w, h); p.setMargins(dp(c,l), dp(c,t), dp(c,r), dp(c,b)); return p; }
    public static ImageView logo(Context c) { ImageView iv = new ImageView(c); iv.setImageResource(R.drawable.be_lahza_logo); iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE); iv.setAdjustViewBounds(true); iv.setContentDescription("be lahza"); return iv; }
}
