package com.android.keepalive;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.keepalive.service.SecureReceiver;
import com.android.keepalive.service.SecureService;

/**
 * If you want keep app live, you need extends this application
 *
 * @author qingfei.chen
 * @since 2017-11-29 16:46:51
 */
public abstract class KeepAliveApplication extends MultiDexApplication implements KeepAliveConfigurations.DaemonListener {
    /**
     * Daemon SDK needs the Daemon Configurations contains two process information </br>
     * see {@link KeepAliveConfigurations} and {@link KeepAliveConfigurations.DaemonConfiguration}
     *
     * @return KeepAliveConfigurations
     */
    protected abstract KeepAliveConfigurations.DaemonConfiguration hookService();

    private IKeepAliveManager mkeepAliveManager;
    static KeepAliveApplication daemonApplication;

    @Override
    public final void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        context.getPackageName();
        mkeepAliveManager = new KeepAliveManager(buildDaemonConfigurations());
        mkeepAliveManager.onAttachBaseContext(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        daemonApplication = this;
    }

    protected KeepAliveConfigurations buildDaemonConfigurations() {
        KeepAliveConfigurations.DaemonConfiguration daemonConfiguration =
                new KeepAliveConfigurations.DaemonConfiguration(
                        getPackageName() + ":Secure",
                        SecureService.class.getCanonicalName(),
                        SecureReceiver.class.getCanonicalName());

        KeepAliveConfigurations.DaemonConfiguration persistentConfiguration = hookService();
        return new KeepAliveConfigurations(persistentConfiguration, daemonConfiguration, this);
    }

    @Override
    public void onPersistentStart(Context context) {
        Log.v("m", "onPersistentStart");
    }

    @Override
    public void onDaemonAssistantStart(Context context) {
        Log.v("m", "onDaemonAssistantStart");
    }

    @Override
    public void onWatchDaemonDead() {
        Log.v("m", "onWatchDaemonDead");
    }

    public static KeepAliveApplication getApp() {
        return daemonApplication;
    }
}
