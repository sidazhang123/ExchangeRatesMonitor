package com.example.hacunamatata.exchangerates.common;

/**
 * Created by Hacunamatata on 2016/12/30.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import com.example.hacunamatata.exchangerates.aud.cn_au_service;
import com.example.hacunamatata.exchangerates.usd.cn_us_service;
import com.example.hacunamatata.exchangerates.usd_aud.us_au_service;

public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        SharedPreferences thresholds = context.getSharedPreferences("thresholds",
                Activity.MODE_PRIVATE);

        if (thresholds.getBoolean("aud_switch", true)) {
            Intent intent1 = new Intent(context, cn_au_service.class);
            context.startService(intent1);
            PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, cn_au_service.class.getName());
            wakeLock.acquire();

        }
        if (thresholds.getBoolean("usd_switch", true)) {
            Intent intent1 = new Intent(context, cn_us_service.class);
            context.startService(intent1);
            PowerManager.WakeLock wakeLock1 = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, cn_us_service.class.getName());
            wakeLock1.acquire();

        }
        if (thresholds.getBoolean("usaud_switch", true)) {
            Intent intent1 = new Intent(context, us_au_service.class);
            context.startService(intent1);
            PowerManager.WakeLock wakeLock2 = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, us_au_service.class.getName());
            wakeLock2.acquire();

        }


    }
}