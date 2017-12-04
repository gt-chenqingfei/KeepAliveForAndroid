package com.android.keepalive.service;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.android.keepalive.KeepAliveActivity;
import com.android.keepalive.KeepAliveApplication;

/**
 * This is a daemon that monitors the lives of other processes
 *
 * @author qingfei.chen
 */

public class SecureService extends Service {

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();

        Log.v("SecureService", "onCreate");
        keepAlive();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        Log.v("SecureService", "onDestroy");

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            /**被关闭时再启动服务，确保不被杀死*/
            @Override
            public void run() {
                try {
                    SecureService.this.startService(new Intent(SecureService.this, SecureService.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 100);
        super.onDestroy();
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("SecureService", "onStartCommand");
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    private void keepAlive() {
        try {
            Notification notification = new Notification();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            startForeground(0, notification); // 设置为前台服务避免kill，Android4.3及以上需要设置id为0时通知栏才不显示该通知；
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }




}
