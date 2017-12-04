package com.android.keepalive.strategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.android.keepalive.KeepAliveConfigurations;
import com.android.keepalive.natives.NativeDaemonAPI20;

/**
 * The strategy for XiaoMi.
 *
 * @author qingfei.chen
 * @since 2017-11-30
 */
public class StrategyVMi implements IKeepAliveStrategy {
    private final String BINARY_DEST_DIR_NAME = "bin";
    private final String BINARY_FILE_NAME = "keepalive";

    private IBinder mRemote;
    private Parcel mServiceData;
    private KeepAliveConfigurations mConfigs;

    @Override
    public boolean onInitialization(Context context) {
        Log.d("KeepAliveStrategyMi", "onInitialization");
        return installBinary(context);
    }

    @Override
    public void onPersistentCreate(final Context context, final KeepAliveConfigurations configs) {
        Log.d("KeepAliveStrategyMi", "onPersistentCreate");
        initAmsBinder();
        initServiceParcel(context, configs.DAEMON_ASSISTANT_CONFIG.SERVICE_NAME);
        Thread t = new Thread() {
            public void run() {
                File binaryFile = new File(context.getDir(BINARY_DEST_DIR_NAME, Context.MODE_PRIVATE), BINARY_FILE_NAME);
                new NativeDaemonAPI20(context).doDaemon(
                        context.getPackageName(),
                        configs.DAEMON_ASSISTANT_CONFIG.SERVICE_NAME,
                        binaryFile.getAbsolutePath());
            }

            ;
        };
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();

        if (configs.LISTENER != null) {
            this.mConfigs = configs;
            configs.LISTENER.onPersistentStart(context);
        }
    }


    @Override
    public void onDaemonAssistantCreate(Context context, KeepAliveConfigurations configs) {
        Log.d("KeepAliveStrategyMi", "onDaemonAssistantCreate");
        Intent intent = new Intent();
        ComponentName component = new ComponentName(context.getPackageName(), configs.PERSISTENT_CONFIG.SERVICE_NAME);
        intent.setComponent(component);
        context.startService(intent);
        if (configs.LISTENER != null) {
            configs.LISTENER.onWatchDaemonDead();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onDaemonDead() {
        Log.d("KeepAliveStrategyMi", "onDaemonDead");
        if (startServiceByAmsBinder()) {

            if (mConfigs != null && mConfigs.LISTENER != null) {
                mConfigs.LISTENER.onWatchDaemonDead();
            }

            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void initAmsBinder() {
        Class<?> activityManagerNative;
        try {
            activityManagerNative = Class.forName("android.app.ActivityManagerNative");
            Object amn = activityManagerNative.getMethod("getDefault").invoke(activityManagerNative);
            Field mRemoteField = amn.getClass().getDeclaredField("mRemote");
            mRemoteField.setAccessible(true);
            mRemote = (IBinder) mRemoteField.get(amn);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


    @SuppressLint("Recycle")
// when process dead, we should save time to restart and kill self, don`t take a waste of time to recycle
    private void initServiceParcel(Context context, String serviceName) {
        Intent intent = new Intent();
        ComponentName component = new ComponentName(context.getPackageName(), serviceName);
        intent.setComponent(component);

        //get handle
//          UserHandle userHandle = android.os.Process.myUserHandle();
//          int handle = (Integer) userHandle.getClass().getMethod("getIdentifier").invoke(userHandle);

        //write pacel
        mServiceData = Parcel.obtain();
        mServiceData.writeInterfaceToken("android.app.IActivityManager");
        mServiceData.writeStrongBinder(null);
//          mServiceData.writeStrongBinder(callerBinder);
        intent.writeToParcel(mServiceData, 0);
        mServiceData.writeString(null);
//          mServiceData.writeString(intent.resolveTypeIfNeeded(context.getContentResolver()));
        mServiceData.writeInt(0);
//          mServiceData.writeInt(handle);

    }

    private boolean startServiceByAmsBinder() {
        try {
            if (mRemote == null || mServiceData == null) {
                Log.e("Daemon", "REMOTE IS NULL or PARCEL IS NULL !!!");
                return false;
            }
            mRemote.transact(34, mServiceData, null, 0);//START_SERVICE_TRANSACTION = 34
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean installBinary(Context context) {
        String binaryDirName = null;
//		String abi = Build.CPU_ABI;
//		if (abi.startsWith("armeabi-v7a")) {
//			binaryDirName = "armeabi-v7a";
//		}else if(abi.startsWith("x86")) {
//			binaryDirName = "x86";
//		}else{
//			binaryDirName = "armeabi";
//		}
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
