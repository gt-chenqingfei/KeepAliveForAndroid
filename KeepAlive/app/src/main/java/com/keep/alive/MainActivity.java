package com.keep.alive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.android.keepalive.KeepAliveManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        KeepAliveManager.startJobScheduler();
        KeepAliveManager.addAccount();

    }

    private void initView() {
        RelativeLayout rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        if (!KeepAliveManager.isIgnoringBatteryOptimizations(this)) {
            IgnoringBatteryHeadTips headTips = new IgnoringBatteryHeadTips(this);
            rl_root.addView(headTips.getRoot());
        }
    }
}
