package com.belahza.app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public final class BatteryUtils {
    private BatteryUtils() {}
    public static int batteryPercent(Context context) {
        try {
            Intent i = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (i == null) return -1;
            int level = i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = i.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            if (level < 0 || scale <= 0) return -1;
            return Math.round(level * 100f / scale);
        } catch(Exception e) { return -1; }
    }
}
