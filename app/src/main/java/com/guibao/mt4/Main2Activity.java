package com.guibao.mt4;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Vibrator;
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
    Ringtone ringtone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_main2);

        Button btn = (Button)findViewById(R.id.btnStop);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player!= null) player.stop();
                finish();
            }
        });

        startVibrator();

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(Main2Activity.this,notification);
        ringtone.play();

        startMedia();

        try {
            Thread.sleep(5000);//这里必须延迟  否者屏保不响
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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


    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            handler.postDelayed(runnable, TIME); //每隔1s执行

            PlaySound(Main2Activity.this);

            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            ringtone = RingtoneManager.getRingtone(Main2Activity.this,notification);
            ringtone.play();
        }
    };


    @Override
    protected void onStop() {
        if (player!= null) player.stop();
        if (ringtone!= null) ringtone.stop();
        if(vibrator!=null)  vibrator.cancel();
        handler.removeCallbacks(runnable);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 唤醒屏幕
        acquireWakeLock();
    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseWakeLock();
    }
    PowerManager.WakeLock mWakelock;
    /**
     * 唤醒屏幕
     */
    private void acquireWakeLock() {
        if (mWakelock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_DIM_WAKE_LOCK, this.getClass()
                    .getCanonicalName());
            mWakelock.acquire();
        }
    }

    /**
     * 释放锁屏
     */
    private void releaseWakeLock() {
        if (mWakelock != null && mWakelock.isHeld()) {
            mWakelock.release();
            mWakelock = null;
        }
    }

    /**
     * 开始播放铃声
     */
    private void startMedia() {
        try {
            player.setDataSource(this,
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            player.prepare();
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    Vibrator vibrator;
    /**
     * 震动
     */
    private void startVibrator() {
        /**
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         *
         */
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = { 500, 1000, 500, 1000 }; // 停止 开启 停止 开启
        vibrator.vibrate(pattern, 0);
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
