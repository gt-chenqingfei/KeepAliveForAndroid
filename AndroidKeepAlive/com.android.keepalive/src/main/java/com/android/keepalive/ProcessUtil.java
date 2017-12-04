package com.android.keepalive;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * @author qingfei.chen
 * @since 2017/11/29.
 */

public class ProcessUtil {

    public static String getProcessName(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int pid = android.os.Process.myPid();
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (pid == info.pid) {
                return info.processName;
            }
        }
        return null;
    }

}
