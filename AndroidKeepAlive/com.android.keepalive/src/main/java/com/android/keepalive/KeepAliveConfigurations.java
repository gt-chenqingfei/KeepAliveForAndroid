package com.android.keepalive;

import android.content.Context;

/**
 * The configurations of KeepAlive SDK, contains two process configuration.
 *
 * @author qingfei.chen
 * @since 2017-11-28
 */
public class KeepAliveConfigurations {

    public final DaemonConfiguration PERSISTENT_CONFIG;
    public final DaemonConfiguration DAEMON_ASSISTANT_CONFIG;
    public final DaemonListener LISTENER;

    public KeepAliveConfigurations(DaemonConfiguration persistentConfig, DaemonConfiguration daemonAssistantConfig) {
        this.PERSISTENT_CONFIG = persistentConfig;
        this.DAEMON_ASSISTANT_CONFIG = daemonAssistantConfig;
        this.LISTENER = null;
    }

    public KeepAliveConfigurations(DaemonConfiguration persistentConfig, DaemonConfiguration daemonAssistantConfig, DaemonListener listener) {
        this.PERSISTENT_CONFIG = persistentConfig;
        this.DAEMON_ASSISTANT_CONFIG = daemonAssistantConfig;
        this.LISTENER = listener;
    }


    /**
     * the configuration of a daemon process, contains process name, service name and receiver name if Android 6.0
     */
    public static class DaemonConfiguration {

        public final String PROCESS_NAME;
        public final String SERVICE_NAME;
        public String RECEIVER_NAME;

        public DaemonConfiguration(String processName, String serviceName, String receiverName) {
            this.PROCESS_NAME = processName;
            this.SERVICE_NAME = serviceName;
            this.RECEIVER_NAME = receiverName;
        }

        public DaemonConfiguration(String processName, String serviceName) {
            this.PROCESS_NAME = processName;
            this.SERVICE_NAME = serviceName;
        }
    }

    /**
     * listener of daemon for external
     */
    public interface DaemonListener {
        void onPersistentStart(Context context);

        void onDaemonAssistantStart(Context context);

        void onWatchDaemonDead();
    }
}
