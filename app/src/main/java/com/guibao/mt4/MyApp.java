package com.guibao.mt4;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by xiaohai on 2017/7/16.
 */
public class MyApp extends Application {
    private String mainUrl = "47.92.74.112";
    private int port = 8888;
    private static MyApp sInstance;
    private  String tradeStatus="";
    @Override
    public void onCreate() {
        super.onCreate();


        sInstance = this;
    }
    public static MyApp getInstance() {
        return sInstance;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public  int getPort(){
        return  port;
    }

    public  String getTradeStatus() {
        return  this.tradeStatus;
    }

    public  void setTradeStatus(String _tradeStatus){
        this.tradeStatus = _tradeStatus;
    }
}
