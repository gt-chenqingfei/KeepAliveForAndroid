package com.android.keepalive;

import android.content.Context;

/**
 * @author qingfei.chen
 * @since 2017-11-29
 */
public interface IKeepAliveManager {
    /**
     * Override this method by {@link android.app.Application}</br></br>
     * Do super.attchBaseContext() first
     *
     * @param base application context
     */
    void onAttachBaseContext(Context base);
}
