package com.android.keepalive.account;

import android.accounts.Account;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class SyncAccountService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static EcalendarSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SyncAccountService", "onCreate");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new EcalendarSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("SyncAccountService", "onBind");
        return sSyncAdapter.getSyncAdapterBinder();
    }

    static class EcalendarSyncAdapter extends AbstractThreadedSyncAdapter {
        public EcalendarSyncAdapter(Context context, boolean autoInitialize) {
            super(context, autoInitialize);
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority,
                                  ContentProviderClient provider, SyncResult syncResult) {

            Log.d("SyncAccountService", "onPerformSync");
            getContext().getContentResolver().
                    notifyChange(KeepAliveAccountProvider.getContentURI(), null, false);
        }
    }
}