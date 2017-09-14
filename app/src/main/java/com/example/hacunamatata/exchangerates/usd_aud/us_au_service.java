package com.example.hacunamatata.exchangerates.usd_aud;

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
import com.example.hacunamatata.exchangerates.common.StartupReceiver;
import com.example.hacunamatata.exchangerates.common.alarm_activity;
import com.example.hacunamatata.exchangerates.common.global_variables;
import com.example.hacunamatata.exchangerates.common.ten_points;


public class us_au_service extends Service {
    Intent activityIntent;

    private class start extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            // do long/network operation

            SharedPreferences thresholds = getSharedPreferences("thresholds",
                    Activity.MODE_PRIVATE);             //initialise Sharedprefs
            SharedPreferences rates = getSharedPreferences("rates",
                    Activity.MODE_PRIVATE);             //initialise Sharedprefs
            float lower = Float.parseFloat(thresholds.getString("usaud_lower", "1"));


            float higher = Float.parseFloat(thresholds.getString("usaud_higher", "2"));


            Context context = getApplicationContext();

            try {
                us_au_RE web = new us_au_RE();
                String data = web.webdata();

                if (!data.equals("Err")) {
                    ten_points queue = new ten_points(context, "usaud");
                    if(!queue.getHead().equals(data)){
                        queue.push(data);}
                    String[] array = data.split("\\|");

                    Float i = Float.parseFloat(array[0]);
                    if (i <= lower) {
                        activityIntent = new Intent(us_au_service.this, alarm_activity.class);
                        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activityIntent.putExtra("msg", "澳对美升值:\n" + array[0]);
                        activityIntent.putExtra("timestamp", array[1]);
                        startActivity(activityIntent);
                    } else if (i >= higher) {
                        activityIntent = new Intent(us_au_service.this, alarm_activity.class);
                        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activityIntent.putExtra("msg", "澳对美贬值:\n" + array[0]);
                        activityIntent.putExtra("timestamp", array[1]);
                        startActivity(activityIntent);
                    }

                } else {

                    Intent sevice = new Intent(us_au_service.this, us_au_service.class);
                    startService(sevice);
                }


            } catch (Exception e) {
                Intent sevice = new Intent(us_au_service.this, us_au_service.class);
                startService(sevice);
            }


            Intent intent2 = new Intent(context, StartupReceiver.class);
            PendingIntent sender = PendingIntent
                    .getBroadcast(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            long interval = thresholds.getLong("usaud_interval", 30);
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000 * interval, sender);//reschedule the alarm for the next 30min period
            if (global_variables.wakeLock1 != null && global_variables.wakeLock1.isHeld()) {          //release the partial lock if it is held
                global_variables.wakeLock1.release();
                global_variables.wakeLock1 = null;
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
        boolean onOff = thresholds.getBoolean("usaud_switch", true);
        if (!onOff) {
            return;
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
