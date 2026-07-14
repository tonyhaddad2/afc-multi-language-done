package com.belahza.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LanguageActivity extends Activity {
    private TextView selected;

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
        content.addView(Ui.text(this,LanguageManager.t(this,"choose_language"),30,Ui.NAVY,Typeface.BOLD));
        content.addView(Ui.text(this,LanguageManager.t(this,"choose_language_desc"),14,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,5,0,18));

        selected = Ui.text(this,"",15,Ui.RED,Typeface.BOLD);
        content.addView(selected,Ui.mlp(this,-1,-2,0,0,0,10));

        content.addView(languageButton("English",LanguageManager.EN),Ui.mlp(this,-1,Ui.dp(this,58),0,0,0,10));
        content.addView(languageButton("Français",LanguageManager.FR),Ui.mlp(this,-1,Ui.dp(this,58),0,0,0,10));
        content.addView(languageButton("العربية",LanguageManager.AR),Ui.lp(-1,Ui.dp(this,58)));

        setContentView(Ui.scroll(this,content));
        render();
    }

    private Button languageButton(String label,String language) {
        Button button = Ui.button(this,label,android.graphics.Color.WHITE,Ui.NAVY);
        button.setContentDescription(label);
        button.setOnClickListener(v -> {
            LanguageManager.set(this,language);
            Toast.makeText(this,LanguageManager.t(this,"save"),Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this,MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
        return button;
    }

    private void render() {
        selected.setText(LanguageManager.t(this,"selected") + ": " + LanguageManager.languageName(this));
    }
}
