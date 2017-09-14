package com.example.hacunamatata.exchangerates.common;

/**
 * Created by Hacunamatata on 2016/12/30.
 */
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.example.hacunamatata.exchangerates.R;

public class alarm_activity extends Activity {
    MediaPlayer mp;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        final Window win = getWindow();
        Vibrator vibrator;

        vibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{300, 500}, 0);  //vibrate 300ms every 500ms
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);            //turn screen on
        setContentView(R.layout.alarm_activity);
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");
            wl.acquire();
            wl.release();
        }
        Intent intent = getIntent();
        String rate = intent.getStringExtra("msg");
        String time = intent.getStringExtra("timestamp");
        TextView msg = (TextView)findViewById(R.id.msg);
        TextView timestamp = (TextView)findViewById(R.id.timestamp);
        msg.setText(rate);
        timestamp.setText(time);

        Context mContext = getApplicationContext();
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);  //get default ringing music
        mp=new MediaPlayer();
        try {
            mp.setDataSource(mContext, uri);
            mp.setLooping(true);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
        Button button = (Button)findViewById(R.id.exitbutton);
        button.setOnClickListener(new View.OnClickListener() {     //cancel vibration and stop mediaplayer when exit button is clicked
            public void onClick(View v) {
                Vibrator vibrator;
                vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.cancel();
                if (mp != null) {
                    mp.stop();
                    mp.release();
                }
                alarm_activity.this.finish();
            }
        });
    }
}