package com.guibao.mt4;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;

import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by xiaohai on 2017/6/27.
 */
public class MyService extends Service {
    private static final String TAG = "MyService";
    MediaPlayer player;
    Boolean bRing = false;
    private int TIME = 1000*5;//1分钟执行一次

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        handler.postDelayed(runnable, TIME); //每隔1s执行

        new Thread() {
            public void run() {
                connectSocket("lots");
            }
        }.start();
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                handler.postDelayed(runnable, TIME); //每隔1s执行

                Calendar c = Calendar.getInstance();

                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                if(MyApp.getInstance().getTradeStatus().equals("Square")) {
                    if (hour > 15 && hour < 24 && c.get(Calendar.DAY_OF_WEEK) != 7 && c.get(Calendar.DAY_OF_WEEK) != 1)
                    {
                        if (minute % 30==0)
                        {
                            if (!bRing) {

                                //KeyguardManager km = (KeyguardManager) MyService.this.getSystemService(Context.KEYGUARD_SERVICE);
                               // if (km.inKeyguardRestrictedInputMode())
                                {
                                    Intent it = new Intent(MyService.this, Main2Activity.class);
                                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    MyService.this.startActivity(it);
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
    };



    public void connectSocket(String s) {
        Socket socket;
        InputStream is;
        OutputStream os;
        DataInputStream dis;
        try {
            socket = new Socket(MyApp.getInstance().getMainUrl(), MyApp.getInstance().getPort());
            //获得对应socket的输入/输出流
            is = socket.getInputStream();
            os = socket.getOutputStream(); //建立数据流
            dis = new DataInputStream(is);
            DataOutputStream dos = new DataOutputStream(os);
            dos.write(s.getBytes());
            dos.flush();
            //获取输入流
            int result = is.available();
            while (result == 0) {
                result = is.available();
            }
            byte[] data = new byte[result];
            is.read(data);
            s = new String(data, "gb2312");//设定字符


            Message msg = myHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putString("key", s);
            msg.setData(b);    // 向消息中添加数据
            myHandler.sendMessage(msg);    // 向Handler发送消息，更新UI

            dis.close(); //关闭数据输入流
            dos.close(); //关闭数据输出流
            is.close(); //关闭输入流
            os.close(); //关闭输出流
            socket.close(); //关闭socket
        } catch (Exception e) {
            System.out.println("Error:" + e);
        }
    }

    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            String result = msg.getData().getString("key");

            if(result.equals("no")||result.equals("ok")) {
            }
            else {
                MyApp.getInstance().setTradeStatus(result);
            }
        }
    };

    @Override
    public void onDestroy() {

        Log.i(TAG, "onDestroy");
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Log.i(TAG, "onStart");
    }


}
