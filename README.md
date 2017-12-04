
#KeepAliveForAndroid

### 项目依赖
1.  依赖 com.android.keepalive  库

### 代码集成
#### 创建Service 和Receiver
1.  新建一个要保护的Service，如有无需新建
2.  新建一个空的Receiver 用于拉活的receiver必须和Service 是同一进程 ，receiver 中不要做任何处理
####  Application 代码集成 
   1. 继承KeepAliveApplication 
   2. 实现 hookService方法
   3. 在hookService方法中配置要保活的Service进程
``` java
package com.keep.alive;
import com.android.keepalive.KeepAliveApplication;
import com.android.keepalive.KeepAliveConfigurations;
//继承KeepAliveApplication 
public class MyApplication extends KeepAliveApplication {

        @Override
        public void onCreate() {
            super.onCreate();
        }
//实现 hookService方法
        @Override
        protected KeepAliveConfigurations.DaemonConfiguration hookService() {
        //:pushservice 要保活服务进程的名称，要和service进程一致，
        //PushService新建一个要保活的服务，如有就把要保护的服务
        //设置上需要保活的服务可以是主进程，也可以是另一个进程
        //PushReceiver 新建一个Receiver类名自己定义
            return new KeepAliveConfigurations.DaemonConfiguration(
                    getPackageName() + ":pushservice",
                    PushService.class.getCanonicalName(),
                    PushReceiver.class.getCanonicalName());
        }

}
```

### 包名替换 
1.打开com.android.keepalive 项目res/value/string.xml
```java
 <string name="account_auth_type">com.keep.alive.account</string>
    <string name="account_auth_provider">com.keep.alive.account.provider</string>
    //替换com.keep.alive为自己项目的包名 例如：
     <string name="account_auth_type">com.myapp.packet.account</string>
    <string name="account_auth_provider">com.myapp.packet.account.provider</string>
```
