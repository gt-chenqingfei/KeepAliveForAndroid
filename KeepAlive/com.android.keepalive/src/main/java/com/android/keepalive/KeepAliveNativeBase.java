package com.android.keepalive;

import android.content.Context;

import com.android.keepalive.strategy.IKeepAliveStrategy;

/**
 * @author qingfie.chen
 * @since 2017-11-29
 */
public class KeepAliveNativeBase {
    /**
     * used for native
     */
    protected Context mContext;

    public KeepAliveNativeBase(Context context) {
        this.mContext = context;
    }

    /**
     * When native call back
     */
    protected void onDaemonDead() {
        StrategyContext.getStrategy().onDaemonDead();
    }

}
