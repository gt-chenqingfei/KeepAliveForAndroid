package com.android.keepalive.strategy;

import com.android.keepalive.KeepAliveConfigurations;

import android.content.Context;

/**
 * define strategy method
 */
public interface IKeepAliveStrategy {
    /**
     * Initialization some files or other when 1st time
     *
     * @param context
     * @return
     */
    boolean onInitialization(Context context);

    /**
     * When Persistent process create
     *
     * @param context
     * @param configs
     */
    void onPersistentCreate(Context context, KeepAliveConfigurations configs);

    /**
     * When DaemonAssistant process create
     *
     * @param context
     * @param configs
     */
    void onDaemonAssistantCreate(Context context, KeepAliveConfigurations configs);

    /**
     * When watches the process dead which it watched
     */
    void onDaemonDead();

}
