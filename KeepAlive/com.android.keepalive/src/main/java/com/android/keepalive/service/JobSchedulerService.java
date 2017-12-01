package com.android.keepalive.service;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * JobScheduler保活
 * 该方案主要适用于 Android5.0 以上版本手机。
 * 该方案在 Android5.0 以上版本中不受 forcestop 影响，被强制停止的应用依然可以被拉活，
 * 在Android5.0 以上版本拉活效果非常好。
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {

    public static final String TAG = "JobSchedulerService";

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(TAG, "JobSchedulerService onStartJob");
        try {
            startService(new Intent(this, SecureService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "JobSchedulerService onStopJob");
        return false;
    }
}
