package com.guibao.mt4;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Random;

/**
 * Created by xiaohai on 2017/7/21.
 */
public class AlarmReceiver extends BroadcastReceiver {
    boolean bRing = false;
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "闹铃响了, 可以做点事情了~~", Toast.LENGTH_LONG).show();


        try {
            Calendar c = Calendar.getInstance();

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            if(MyApp.getInstance().getTradeStatus().equals("Square")) {
                if (hour > 15 && hour < 24 && c.get(Calendar.DAY_OF_WEEK) != 7 && c.get(Calendar.DAY_OF_WEEK) != 1)
                {
                    if (minute % 30<2)
                    {
                        if (!bRing) {

                            //KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
                            //if (km.inKeyguardRestrictedInputMode())
                            {
                                Intent it = new Intent(context, Main2Activity.class);
                                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(it);
                            }

                            bRing = true;
                        }
                    } else {
                        bRing = false;
                        //if (player!= null) player.stop();
                    }
                }
            }//f(MyApp.getInstance().getTradeStatus().length()=="Squre") {
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("exception...");
        }
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