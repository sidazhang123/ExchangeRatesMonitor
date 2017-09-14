package com.example.hacunamatata.exchangerates.aud;

/**
 * Created by Hacunamatata on 2016/12/30.
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import com.example.hacunamatata.exchangerates.common.alarm_activity;
import com.example.hacunamatata.exchangerates.common.StartupReceiver;
import com.example.hacunamatata.exchangerates.common.global_variables;
import com.example.hacunamatata.exchangerates.common.ten_points;


public class cn_au_service extends Service {
    Intent activityIntent;

    private class start extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            // do long/network operation

            SharedPreferences thresholds = getSharedPreferences("thresholds",
                    Activity.MODE_PRIVATE);             //initialise Sharedprefs
            SharedPreferences rates = getSharedPreferences("rates",
                    Activity.MODE_PRIVATE);             //initialise Sharedprefs
            float lower = Float.parseFloat(thresholds.getString("aud_lower", "495"));


            float higher = Float.parseFloat(thresholds.getString("aud_higher", "510"));


            Context context = getApplicationContext();

            try {
                cn_au_RE web = new cn_au_RE();
                String data = web.webdata();

                if (!data.equals("Err")) {
                    ten_points queue = new ten_points(context, "aud");
                    if(!queue.getHead().equals(data)){
                    queue.push(data);}
                    String[] array = data.split("\\|");

                    Float i = Float.parseFloat(array[0]);
                    if (i <= lower) {
                        activityIntent = new Intent(cn_au_service.this, alarm_activity.class);
                        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activityIntent.putExtra("msg", "AUD走低:\n" + array[0]);
                        activityIntent.putExtra("timestamp", array[1]);
                        startActivity(activityIntent);
                    } else if (i >= higher) {
                        activityIntent = new Intent(cn_au_service.this, alarm_activity.class);
                        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activityIntent.putExtra("msg", "AUD升高:\n" + array[0]);
                        activityIntent.putExtra("timestamp", array[1]);
                        startActivity(activityIntent);
                    }

                } else {

                    Intent sevice = new Intent(cn_au_service.this, cn_au_service.class);
                    startService(sevice);
                }


            } catch (Exception e) {
                Intent sevice = new Intent(cn_au_service.this, cn_au_service.class);
                startService(sevice);
            }


            Intent intent2 = new Intent(context, StartupReceiver.class);
            PendingIntent sender = PendingIntent
                    .getBroadcast(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            long interval = thresholds.getLong("aud_interval", 30);
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000 * interval, sender);//reschedule the alarm for the next 30min period
            if (global_variables.wakeLock != null && global_variables.wakeLock.isHeld()) {          //release the partial lock if it is held
                global_variables.wakeLock.release();
                global_variables.wakeLock = null;
            }
            return "OK";
        }
    }

    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        SharedPreferences thresholds = getSharedPreferences("thresholds",
                Activity.MODE_PRIVATE);
        boolean onOff = thresholds.getBoolean("aud_switch", true);
        if (!onOff) {
            return;
        }
        if (global_variables.wakeLock != null && !global_variables.wakeLock.isHeld()) {
            PowerManager.WakeLock wakeLock;
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, cn_au_service.class.getName());
            wakeLock.acquire();
            global_variables.wakeLock = wakeLock;
        }
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        start MyTask = new start();
        MyTask.execute();
        return START_STICKY;
    }

    @Override

    public void onDestroy() {
        super.onDestroy();
    }
}
