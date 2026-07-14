package com.belahza.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class AlertActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this,SosActivity.class);
        intent.putExtras(getIntent());
        startActivity(intent);
        finish();
    }
}
