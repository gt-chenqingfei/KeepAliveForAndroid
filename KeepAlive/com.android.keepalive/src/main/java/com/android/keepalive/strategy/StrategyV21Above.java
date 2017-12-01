package com.android.keepalive.strategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.android.keepalive.KeepAliveConfigurations;
import com.android.keepalive.natives.NativeDaemonAPI20;

/**
 * the strategy in android API below 21.
 */
public class StrategyV21Above implements IKeepAliveStrategy {
    private final String BINARY_DEST_DIR_NAME = "bin";
    private final String BINARY_FILE_NAME = "keepalive";

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;

    @Override
    public boolean onInitialization(Context context) {
        Log.d("KeepAliveStrategy21Less", "onInitialization");
        return installBinary(context);
    }

    @Override
    public void onPersistentCreate(final Context context, final KeepAliveConfigurations configs) {
        Log.d("KeepAliveStrategy21Less", "onPersistentCreate");
        initAlarm(context, configs.DAEMON_ASSISTANT_CONFIG.SERVICE_NAME);
        Thread t = new Thread() {
            public void run() {
                File binaryFile = new File(context.getDir(BINARY_DEST_DIR_NAME, Context.MODE_PRIVATE), BINARY_FILE_NAME);
                try {
                    new NativeDaemonAPI20(context).doDaemon(
                            context.getPackageName(),
                            configs.DAEMON_ASSISTANT_CONFIG.SERVICE_NAME,
                            binaryFile.getAbsolutePath());
                } catch (UnsatisfiedLinkError error) {
                    //java.lang.UnsatisfiedLinkError: Native method not found: com.android.keeplive.nativ.NativeDaemonAPI20.doDaemon:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
                    error.printStackTrace();
                }
            }
        };
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();

        if (configs != null && configs.LISTENER != null) {
            configs.LISTENER.onPersistentStart(context);
        }
    }

    @Override
    public void onDaemonAssistantCreate(Context context, KeepAliveConfigurations configs) {
        Log.d("KeepAliveStrategy21Less", "onDaemonAssistantCreate");
        Intent intent = new Intent();
        ComponentName component = new ComponentName(context.getPackageName(), configs.PERSISTENT_CONFIG.SERVICE_NAME);
        intent.setComponent(component);
        context.startService(intent);
        if (configs != null && configs.LISTENER != null) {
            configs.LISTENER.onWatchDaemonDead();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    @Override
    public void onDaemonDead() {
        Log.d("KeepAliveStrategy21Less", "onDaemonAssistantCreate");
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 100, mPendingIntent);
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


    private boolean installBinary(Context context) {
        String binaryDirName = null;
        String abi = Build.CPU_ABI;
        if (abi.startsWith("armeabi-v7a")) {
            binaryDirName = "armeabi-v7a";
        } else if (abi.startsWith("x86")) {
            binaryDirName = "x86";
        } else {
            binaryDirName = "armeabi";
        }
        return install(context, BINARY_DEST_DIR_NAME, binaryDirName, BINARY_FILE_NAME);
    }


    private boolean install(Context context, String destDirName, String assetsDirName, String filename) {
        File file = new File(context.getDir(destDirName, Context.MODE_PRIVATE), filename);
        if (file.exists()) {
            return true;
        }
        try {
            copyAssets(context, (TextUtils.isEmpty(assetsDirName) ? "" : (assetsDirName + File.separator)) + filename, file, "700");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void copyAssets(Context context, String assetsFilename, File file, String mode) throws IOException, InterruptedException {
        AssetManager manager = context.getAssets();
        final InputStream is = manager.open(assetsFilename);
        copyFile(file, is, mode);
    }

    private void copyFile(File file, InputStream is, String mode) throws IOException, InterruptedException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        final String abspath = file.getAbsolutePath();
        final FileOutputStream out = new FileOutputStream(file);
        byte buf[] = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.close();
        is.close();
        Runtime.getRuntime().exec("chmod " + mode + " " + abspath).waitFor();
    }
}
