package com.android.keepalive.strategy;

import java.io.File;
import java.io.IOException;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.android.keepalive.KeepAliveConfigurations;
import com.android.keepalive.natives.NativeDaemonAPI21;

/**
 * the strategy in android API 21.
 */
public class StrategyV21 implements IKeepAliveStrategy {
    private final static String INDICATOR_DIR_NAME = "indicators";
    private final static String INDICATOR_PERSISTENT_FILENAME = "indicator_p";
    private final static String INDICATOR_DAEMON_ASSISTANT_FILENAME = "indicator_d";
    private final static String OBSERVER_PERSISTENT_FILENAME = "observer_p";
    private final static String OBSERVER_DAEMON_ASSISTANT_FILENAME = "observer_d";

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private KeepAliveConfigurations mConfigs;

    @Override
    public boolean onInitialization(Context context) {
        Log.d("KeepAliveStrategy21", "onInitialization");
        return initIndicators(context);
    }

    @Override
    public void onPersistentCreate(final Context context, KeepAliveConfigurations configs) {
        Log.d("KeepAliveStrategy21", "onPersistentCreate");
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(context.getPackageName(), configs.DAEMON_ASSISTANT_CONFIG.SERVICE_NAME);
        intent.setComponent(componentName);
        context.startService(intent);

        initAlarm(context, configs.PERSISTENT_CONFIG.SERVICE_NAME);

        Thread t = new Thread() {
            @Override
            public void run() {
                File indicatorDir = context.getDir(INDICATOR_DIR_NAME, Context.MODE_PRIVATE);
                new NativeDaemonAPI21(context).doDaemon(
                        new File(indicatorDir, INDICATOR_PERSISTENT_FILENAME).getAbsolutePath(),
                        new File(indicatorDir, INDICATOR_DAEMON_ASSISTANT_FILENAME).getAbsolutePath(),
                        new File(indicatorDir, OBSERVER_PERSISTENT_FILENAME).getAbsolutePath(),
                        new File(indicatorDir, OBSERVER_DAEMON_ASSISTANT_FILENAME).getAbsolutePath());
            }
        };
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();

        if (configs != null && configs.LISTENER != null) {
            this.mConfigs = configs;
            configs.LISTENER.onPersistentStart(context);
        }
    }

    @Override
    public void onDaemonAssistantCreate(final Context context, KeepAliveConfigurations configs) {
        Log.d("KeepAliveStrategy21", "onDaemonAssistantCreate");
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName(context.getPackageName(), configs.PERSISTENT_CONFIG.SERVICE_NAME);
        intent.setComponent(componentName);
        context.startService(intent);

        initAlarm(context, configs.PERSISTENT_CONFIG.SERVICE_NAME);

        Thread t = new Thread() {
            public void run() {
                File indicatorDir = context.getDir(INDICATOR_DIR_NAME, Context.MODE_PRIVATE);
                new NativeDaemonAPI21(context).doDaemon(
                        new File(indicatorDir, INDICATOR_DAEMON_ASSISTANT_FILENAME).getAbsolutePath(),
                        new File(indicatorDir, INDICATOR_PERSISTENT_FILENAME).getAbsolutePath(),
                        new File(indicatorDir, OBSERVER_DAEMON_ASSISTANT_FILENAME).getAbsolutePath(),
                        new File(indicatorDir, OBSERVER_PERSISTENT_FILENAME).getAbsolutePath());
            }

            ;
        };
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();

        if (configs != null && configs.LISTENER != null) {
            this.mConfigs = configs;
            configs.LISTENER.onDaemonAssistantStart(context);
        }
    }

    @Override
    public void onDaemonDead() {
        Log.d("KeepAliveStrategy21", "onDaemonDead");
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 100, mPendingIntent);

        if (mConfigs != null && mConfigs.LISTENER != null) {
            mConfigs.LISTENER.onWatchDaemonDead();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    private void initAlarm(Context context, String serviceName) {
        if (mAlarmManager == null) {
            mAlarmManager = ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
        }
        if (mPendingIntent == null) {
            Intent intent = new Intent();
            ComponentName component = new ComponentName(context.getPackageName(), serviceName);
            intent.setComponent(component);
            intent.setFlags(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES);
            mPendingIntent = PendingIntent.getService(context, 0, intent, 0);
        }
        mAlarmManager.cancel(mPendingIntent);
    }


    private boolean initIndicators(Context context) {
        File dirFile = context.getDir(INDICATOR_DIR_NAME, Context.MODE_PRIVATE);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        try {
            createNewFile(dirFile, INDICATOR_PERSISTENT_FILENAME);
            createNewFile(dirFile, INDICATOR_DAEMON_ASSISTANT_FILENAME);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void createNewFile(File dirFile, String fileName) throws IOException {
        File file = new File(dirFile, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

}
