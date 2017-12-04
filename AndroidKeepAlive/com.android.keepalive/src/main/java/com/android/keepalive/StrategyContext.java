package com.android.keepalive;

import android.os.Build;

import com.android.keepalive.strategy.IKeepAliveStrategy;
import com.android.keepalive.strategy.StrategyV21;
import com.android.keepalive.strategy.StrategyV21Above;
import com.android.keepalive.strategy.StrategyV22;
import com.android.keepalive.strategy.StrategyV23;
import com.android.keepalive.strategy.StrategyVMi;

/**
 * All about strategy on different device here
 *
 * @author qingfei.chen
 */
public class StrategyContext {

    private static IKeepAliveStrategy mDaemonStrategy;

    /**
     * Get the strategy for this device
     *
     * @return the daemon strategy for this device
     */
    static IKeepAliveStrategy getStrategy() {
        if (mDaemonStrategy != null) {
            return mDaemonStrategy;
        }
        int sdk = Build.VERSION.SDK_INT;
        switch (sdk) {
            case 23:
                mDaemonStrategy = new StrategyV23();
                break;

            case 22:
                mDaemonStrategy = new StrategyV22();
                break;

            case 21:
                if ("MX4 Pro".equalsIgnoreCase(Build.MODEL)) {
                    mDaemonStrategy = new StrategyV21Above();
                } else {
                    mDaemonStrategy = new StrategyV21();
                }
                break;

            default:
                if (Build.MODEL != null && Build.MODEL.toLowerCase().startsWith("mi")) {
                    mDaemonStrategy = new StrategyVMi();
                } else if (Build.MODEL != null && Build.MODEL.toLowerCase().startsWith("a31")) {
                    mDaemonStrategy = new StrategyV21();
                } else {
                    mDaemonStrategy = new StrategyV21Above();
                }
                break;
        }
        return mDaemonStrategy;
    }
}