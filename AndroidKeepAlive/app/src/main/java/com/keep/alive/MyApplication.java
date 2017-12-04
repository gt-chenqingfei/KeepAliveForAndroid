package com.keep.alive;


import com.android.keepalive.KeepAliveApplication;
import com.android.keepalive.KeepAliveConfigurations;

public class MyApplication extends KeepAliveApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected KeepAliveConfigurations.DaemonConfiguration hookService() {
        return new KeepAliveConfigurations.DaemonConfiguration(
                getPackageName() + ":PushService",
                PushService.class.getCanonicalName()
        );
    }

}