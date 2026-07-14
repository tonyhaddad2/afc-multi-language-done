package com.belahza.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MedicalProfileActivity extends Activity {
    private EditText fullName,blood,allergies,conditions,medications,notes;
    private final EditText[] contactNames = new EditText[3];
    private final EditText[] contactPhones = new EditText[3];

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
        content.addView(Ui.text(this,LanguageManager.t(this,"medical_id"),30,Ui.NAVY,Typeface.BOLD));
        content.addView(Ui.text(this,"Saved locally using Android Keystore encryption. You choose which fields are visible during an emergency.",14,Ui.MUTED,Typeface.NORMAL),Ui.mlp(this,-1,-2,0,5,0,16));

        fullName = field(LanguageManager.t(this,"name"),MedicalProfile.get(this,MedicalProfile.FULL_NAME));
        blood = field(LanguageManager.t(this,"blood_type"),MedicalProfile.get(this,MedicalProfile.BLOOD_TYPE));
        allergies = field(LanguageManager.t(this,"allergies"),MedicalProfile.get(this,MedicalProfile.ALLERGIES));
        conditions = field(LanguageManager.t(this,"conditions"),MedicalProfile.get(this,MedicalProfile.CONDITIONS));
        medications = field(LanguageManager.t(this,"medications"),MedicalProfile.get(this,MedicalProfile.MEDICATIONS));
        notes = field(LanguageManager.t(this,"notes"),MedicalProfile.get(this,MedicalProfile.NOTES));

        for (EditText edit : new EditText[]{fullName,blood,allergies,conditions,medications,notes}) {
            content.addView(edit,Ui.mlp(this,-1,Ui.dp(this,58),0,0,0,10));
        }

        content.addView(Ui.text(this,LanguageManager.t(this,"emergency_contacts"),20,Ui.NAVY,Typeface.BOLD),Ui.mlp(this,-1,-2,0,10,0,10));
        for (int i=0;i<3;i++) {
            contactNames[i] = field(LanguageManager.t(this,"name"),SecureLocalStore.get(this,"trusted_name_"+(i+1)));
            contactPhones[i] = field(LanguageManager.t(this,"phone_number"),SecureLocalStore.get(this,"trusted_phone_"+(i+1)));
            content.addView(contactNames[i],Ui.mlp(this,-1,Ui.dp(this,56),0,0,0,6));
            content.addView(contactPhones[i],Ui.mlp(this,-1,Ui.dp(this,56),0,0,0,12));
        }

        Button privacy = Ui.button(this,LanguageManager.t(this,"medical_privacy"),android.graphics.Color.WHITE,Ui.NAVY);
        privacy.setOnClickListener(v -> startActivity(new Intent(this,MedicalIdPrivacyActivity.class)));
        content.addView(privacy,Ui.mlp(this,-1,Ui.dp(this,54),0,4,0,10));

        Button save = Ui.button(this,LanguageManager.t(this,"save"),Ui.RED,android.graphics.Color.WHITE);
        save.setOnClickListener(v -> save());
        content.addView(save,Ui.lp(-1,Ui.dp(this,58)));

        setContentView(Ui.scroll(this,content));
    }

    private EditText field(String hint,String value) {
        EditText edit = new EditText(this);
        edit.setHint(hint);
        edit.setText(value == null ? "" : value);
        edit.setTextSize(15);
        edit.setSingleLine(false);
        edit.setPadding(Ui.dp(this,16),0,Ui.dp(this,16),0);
        edit.setBackground(Ui.bgStroke(this,android.graphics.Color.WHITE,18,Ui.LINE,1));
        return edit;
    }

    private void save() {
        MedicalProfile.set(this,MedicalProfile.FULL_NAME,fullName.getText().toString());
        MedicalProfile.set(this,MedicalProfile.BLOOD_TYPE,blood.getText().toString());
        MedicalProfile.set(this,MedicalProfile.ALLERGIES,allergies.getText().toString());
        MedicalProfile.set(this,MedicalProfile.CONDITIONS,conditions.getText().toString());
        MedicalProfile.set(this,MedicalProfile.MEDICATIONS,medications.getText().toString());
        MedicalProfile.set(this,MedicalProfile.NOTES,notes.getText().toString());
        for (int i=0;i<3;i++) {
            SecureLocalStore.put(this,"trusted_name_"+(i+1),contactNames[i].getText().toString());
            SecureLocalStore.put(this,"trusted_phone_"+(i+1),contactPhones[i].getText().toString());
        }
        ConnectedBackendClient.syncMedicalIdAsync(this,null);
        Toast.makeText(this,LanguageManager.t(this,"save"),Toast.LENGTH_SHORT).show();
    }
}
