package com.keep.alive;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.android.keepalive.service.PersistentService;

public class PushService extends PersistentService {
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public Class getServiceClass() {
        return this.getClass();
    }

    public void onCreate() {
        super.onCreate();
        Log.v("PushService", "onCreate");
    }


}
