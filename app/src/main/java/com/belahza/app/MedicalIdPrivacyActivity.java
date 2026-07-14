package com.belahza.app;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MedicalIdPrivacyActivity extends Activity {
    private final Map<String,CheckBox> boxes = new LinkedHashMap<>();

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
        content.addView(Ui.text(this,LanguageManager.t(this,"medical_privacy"),30,Ui.NAVY,Typeface.BOLD));
        content.addView(Ui.text(this,LanguageManager.t(this,"privacy_fields"),14,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,5,0,16));

        add(content,MedicalProfile.FULL_NAME,LanguageManager.t(this,"name"));
        add(content,MedicalProfile.BLOOD_TYPE,LanguageManager.t(this,"blood_type"));
        add(content,MedicalProfile.ALLERGIES,LanguageManager.t(this,"allergies"));
        add(content,MedicalProfile.CONDITIONS,LanguageManager.t(this,"conditions"));
        add(content,MedicalProfile.MEDICATIONS,LanguageManager.t(this,"medications"));
        add(content,MedicalProfile.NOTES,LanguageManager.t(this,"notes"));
        add(content,MedicalProfile.EMERGENCY_CONTACTS,LanguageManager.t(this,"emergency_contacts"));

        Button save = Ui.button(this,LanguageManager.t(this,"save"),Ui.RED,android.graphics.Color.WHITE);
        save.setOnClickListener(v -> {
            Set<String> selected = new LinkedHashSet<>();
            for (Map.Entry<String,CheckBox> entry : boxes.entrySet()) if (entry.getValue().isChecked()) selected.add(entry.getKey());
            MedicalProfile.setVisibleFields(this,selected);
            ConnectedBackendClient.syncMedicalIdAsync(this,null);
            Toast.makeText(this,LanguageManager.t(this,"save"),Toast.LENGTH_SHORT).show();
        });
        content.addView(save,Ui.mlp(this,-1,Ui.dp(this,58),0,16,0,0));
        setContentView(Ui.scroll(this,content));
    }

    private void add(LinearLayout parent,String field,String label) {
        CheckBox box = new CheckBox(this);
        box.setText(label);
        box.setTextSize(16);
        box.setTextColor(Ui.INK);
        box.setChecked(MedicalProfile.visibleFields(this).contains(field));
        box.setMinHeight(Ui.dp(this,52));
        boxes.put(field,box);
        parent.addView(box);
    }
}
