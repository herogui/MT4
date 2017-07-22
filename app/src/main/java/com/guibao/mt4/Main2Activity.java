package com.guibao.mt4;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Calendar;
import java.util.Random;

public class Main2Activity extends Activity {
    MediaPlayer player;int TIME = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_main2);

        handler.postDelayed(runnable, TIME); //每隔1s执行
//        try {
//            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//            if (player == null)
//                player = new MediaPlayer();
//            player.setDataSource(Main2Activity.this, uri);
//            player.setLooping(false); //循环播放
//            player.prepare();
//            player.start();
//        }catch (Exception exp)
//        {}

        Button btn = (Button)findViewById(R.id.btnStop);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player!= null) player.stop();
                  finish();
            }
        });
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            handler.postDelayed(runnable, TIME); //每隔1s执行

            PlaySound(Main2Activity.this);
        }
    };


    @Override
    protected void onStop() {
        if (player!= null) player.stop();
        handler.removeCallbacks(runnable);
        super.onStop();
    }

    public static int PlaySound(final Context context) {
        NotificationManager mgr = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification nt = new Notification();
        nt.defaults = Notification.DEFAULT_SOUND;
        int soundId = new Random(System.currentTimeMillis())
                .nextInt(Integer.MAX_VALUE);
        mgr.notify(soundId, nt);
        return soundId;
    }
}
