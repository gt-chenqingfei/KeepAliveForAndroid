package com.keep.alive;


import com.android.keepalive.KeepAliveConfigurations;
import com.keep.alive.service.PushReceiver;
import com.keep.alive.service.PushService;
import com.android.keepalive.KeepAliveApplication;

public class ApplicationManager extends KeepAliveApplication {
    static ApplicationManager app = null;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    @Override
    protected KeepAliveConfigurations.DaemonConfiguration hookService() {
        return new KeepAliveConfigurations.DaemonConfiguration(
                getPackageName() + ":pushservice",
                PushService.class.getCanonicalName(),
                PushReceiver.class.getCanonicalName());
    }

    public static ApplicationManager getApp() {
        return app;
    }
}