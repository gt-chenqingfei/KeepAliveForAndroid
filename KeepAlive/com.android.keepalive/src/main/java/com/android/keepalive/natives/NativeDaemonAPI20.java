package com.android.keepalive.natives;

import com.android.keepalive.KeepAliveNativeBase;

import android.content.Context;

/**
 * native code to watch each other when api under 20 (contains 20)
 */
public class NativeDaemonAPI20 extends KeepAliveNativeBase {

    public NativeDaemonAPI20(Context context) {
        super(context);
    }

    static {
        try {
            System.loadLibrary("keepalive_api20");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public native void doDaemon(String pkgName, String svcName, String daemonPath);

}
