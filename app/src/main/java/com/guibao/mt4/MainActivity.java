package com.guibao.mt4;


        import java.io.BufferedReader;
        import java.io.BufferedWriter;
        import java.io.DataInputStream;
        import java.io.DataOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStream;
        import java.io.OutputStreamWriter;
        import java.io.PrintWriter;
        import java.net.Socket;
        import java.net.UnknownHostException;

        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.ComponentName;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.view.Menu;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;
        import android.util.Log;

public class MainActivity extends Activity {
    private TextView textview;
    private EditText edtext;
    private Button butBuy;
    private Button btnSell;

    private static final String HOST = "47.92.74.112";
    //private static final String HOST = "192.168.1.103";
    private static final int PORT = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isConn(getApplicationContext())){
            setNetworkMethod(MainActivity.this);
        }

        butBuy = (Button) findViewById(R.id.btnBuy);
        btnSell = (Button) findViewById(R.id.btnSell);

        new Thread() {
            public void run() {
                connectSocket("lots");
            }
        }.start();


        butBuy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               // handler2.sendEmptyMessage(0);

                new AlertDialog.Builder(MainActivity.this).setTitle("")//设置对话框标题

                        .setMessage("是否买入？")//设置显示的内容

                        .setPositiveButton("买了", new DialogInterface.OnClickListener() {//添加确定按钮


                            @Override

                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                new Thread() {
                                    public void run() {
                                        connectSocket("buy");
                                    }
                                }.start();


                            }

                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加返回按钮


                    @Override

                    public void onClick(DialogInterface dialog, int which) {//响应事件


                    }

                }).show();//在按键响应事件中显示此对话框


            }
        });

        // 发送信息
        btnSell.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this).setTitle("系统提示")//设置对话框标题

                        .setMessage("是否卖空？")//设置显示的内容

                        .setPositiveButton("卖了", new DialogInterface.OnClickListener() {//添加确定按钮


                            @Override

                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                new Thread() {
                                    public void run() {
                                        connectSocket("sell");
                                    }
                                }.start();


                            }

                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加返回按钮


                    @Override

                    public void onClick(DialogInterface dialog, int which) {//响应事件


                    }

                }).show();//在按键响应事件中显示此对话框
            }
        });


    }

    public void connectSocket(String s) {
        Socket socket;
        InputStream is;
        OutputStream os;
        DataInputStream dis;
        try {
            socket = new Socket(HOST, PORT);
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
            String res = "";

            if(result.equals("no")||result.equals("ok")) {
                if (result.equals("no")) res = "交易遇到问题";
                else if (result.equals("ok")) res = "恭喜你，交易成功啦";
                new AlertDialog.Builder(MainActivity.this).setTitle("")//设置对话框标题

                        .setMessage(res)//设置显示的内容

                        .setPositiveButton("关闭", new DialogInterface.OnClickListener() {//添加确定按钮


                            @Override

                            public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件



                                new Handler().postDelayed(new Runnable(){

                                    public void run() {
                                        //刷新数据
                                        new Thread() {
                                            public void run() {
                                                connectSocket("lots");
                                            }
                                        }.start();
                                    }

                                },3000);
                            }  

                        }).show();//在按键响应事件中显示此对话框
            }
            else {
                 TextView tv = (TextView)findViewById(R.id.txt);
                 tv.setText(result);
            }
        }
    };


    /*
        * 判断网络连接是否已开
        * 2012-08-20
        *true 已打开  false 未打开
        * */
    public static boolean isConn(Context context){
        boolean bisConnFlag=false;
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if (network != null) {
            bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
        }
        return bisConnFlag;
    }

        /*
        * 打开设置网络界面
        * */
        public static void setNetworkMethod(final Context context) {
            //提示对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("网络设置提示").setMessage("网络连接不可用,是否进行设置?").setPositiveButton("设置", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    Intent intent = null;
                    //判断手机系统的版本  即API大于10 就是3.0或以上版本
                    if(android.os.Build.VERSION.SDK_INT > 10) {
                        intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                    } else {
                        intent = new Intent();
                        ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
                    intent.setComponent(component);
                    intent.setAction("android.intent.action.VIEW");
                }
                context.startActivity(intent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        }).show();
    }


}
