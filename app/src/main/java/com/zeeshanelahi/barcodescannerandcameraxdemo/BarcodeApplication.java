package com.zeeshanelahi.barcodescannerandcameraxdemo;

import android.app.Application;

import com.zeeshanelahi.barcodescannerandcameraxdemo.model.InternetBroadCastReceiver;

public class BarcodeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //InternetBroadCastReceiver.getInstance().activeBroadCast(this);
    }
}
