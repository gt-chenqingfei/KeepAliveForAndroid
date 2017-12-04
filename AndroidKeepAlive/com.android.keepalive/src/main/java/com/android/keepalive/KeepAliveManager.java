package com.android.keepalive;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.android.keepalive.account.SyncAccountUtils;
import com.android.keepalive.service.JobSchedulerService;
import com.android.keepalive.service.SecureService;
import com.android.keepalive.strategy.IKeepAliveStrategy;

/**
 * @author qingfei.chen
 * @since 2017-11-29
 */
public class KeepAliveManager implements IKeepAliveManager {
    public static final int IGNORINGBATTERY = 9999;
    private static final long JOB_SCHEDULER_INTERVAL_MILLIS = 60 * 1000;
    private KeepAliveConfigurations mConfigurations;

    public KeepAliveManager(KeepAliveConfigurations configurations) {
        this.mConfigurations = configurations;
    }

    @Override
    public void onAttachBaseContext(Context context) {
        if (mConfigurations == null) {
            Log.e("KeepAliveManager", "hookService error !");
            return;
        }

        if (mConfigurations.PERSISTENT_CONFIG == null) {
            Log.e("KeepAliveManager", "You must config persistent service at hookService method!");
            return;
        }

        String processName = ProcessUtil.getProcessName(context);
        String packageName = context.getPackageName();

        Log.d("KeepAliveManager", "onAttachBaseContext processName:" +
                processName + ",packageName:" + packageName);

        if (TextUtils.isEmpty(processName)) {
            return;
        }

        if (processName.startsWith(mConfigurations.PERSISTENT_CONFIG.PROCESS_NAME)) {
            StrategyContext.getStrategy().onPersistentCreate(context, mConfigurations);
        } else if (processName.startsWith(mConfigurations.DAEMON_ASSISTANT_CONFIG.PROCESS_NAME)) {
            KeepAliveManager.startJobScheduler(context);
            KeepAliveManager.addAccount(context);
            StrategyContext.getStrategy().onDaemonAssistantCreate(context, mConfigurations);
        } else if (processName.startsWith(packageName)) {
            StrategyContext.getStrategy().onInitialization(context);
            try {
                context.startService(new Intent(context, SecureService.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void startJobScheduler(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int job = 1;
                JobInfo.Builder builder = new JobInfo.Builder(job,
                        new ComponentName(context, JobSchedulerService.class));
                builder.setPeriodic(JOB_SCHEDULER_INTERVAL_MILLIS);
                builder.setPersisted(true);

                JobScheduler jobScheduler = (JobScheduler) context.
                        getSystemService(Context.JOB_SCHEDULER_SERVICE);
                jobScheduler.schedule(builder.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addAccount(Context context) {
        try {
            SyncAccountUtils.createSyncAccount(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否忽略电池优化判断
     *
     * @param activity
     * @return
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static boolean isIgnoringBatteryOptimizations(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String packageName = activity.getPackageName();
                PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
                return pm.isIgnoringBatteryOptimizations(packageName);
            } catch (Exception e) {
                e.printStackTrace();
                return true;
            } catch (NoSuchFieldError error) {
                error.printStackTrace();
                return true;
            }
        } else {
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void openIgnoringBatteryOptimizations(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String packageName = activity.getPackageName();
                PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    activity.startActivityForResult(intent, IGNORINGBATTERY);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (NoSuchFieldError error) {
                error.printStackTrace();
            }
        }
    }

}
