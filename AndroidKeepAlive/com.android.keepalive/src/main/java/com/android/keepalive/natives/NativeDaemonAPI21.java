package com.android.keepalive.natives;

import com.android.keepalive.KeepAliveNativeBase;

import android.content.Context;

/**
 * native code to watch each other when api over 21 (contains 21)
 */
public class NativeDaemonAPI21 extends KeepAliveNativeBase {

    public NativeDaemonAPI21(Context context) {
        super(context);
    }

    static {
        try {
            System.loadLibrary("keepalive_api21");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public native void doDaemon(String indicatorSelfPath, String indicatorDaemonPath, String observerSelfPath, String observerDaemonPath);
}
