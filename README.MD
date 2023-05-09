## Android 硬件守护程序

#### 引入

#### 注意事项

Android 9.0后 http 请求不再支持，需要添加http请求过滤，防止系统拦截

- 主工程res/xml目录下创建 network_security_config.xml

- 内部添加如下代码
  
  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <network-security-config>
      <base-config cleartextTrafficPermitted="true" />
  </network-security-config>
  
  ```



- 清单文件中添加相关配置
  
  ```xml
  <application
          android:allowBackup="true"
          android:dataExtractionRules="@xml/data_extraction_rules"
          android:fullBackupContent="@xml/backup_rules"
          android:icon="@mipmap/ic_launcher"
          android:label="@string/app_name"
          android:roundIcon="@mipmap/ic_launcher_round"
          android:supportsRtl="true"
          android:name=".TestApplication"
          android:networkSecurityConfig="@xml/network_security_config"  
          android:theme="@style/Theme.AndroidHardKeepLive"
          tools:targetApi="31">
  
  ```

#### 使用

- kotlin
  
  ```kotlin
  HardWareKeepLive.Builder() // 初始化builder
              .setBaseUrl("http://127.0.0.1:11220") //设置接口地址
              .setIsDebug(true) //设置是否是debug模式，用于内部打印
              .setRetryTimes(3) //设置重试次数,不设置默认三次
              .setConnectCallback(object : HardWareKeepLive.ConnectListener { //设置连接状态回调
                  override fun lostConnect() {
                      // 连接丢失，内部自动重连
                      Log.e(TAG, "lostConnect: ")
                  }
  
                  override fun reConnected() {
                      // 重连成功
                      Log.e(TAG, "reConnected: ")
                  }
  
                  override fun retryConnect(times: Int) {
                      // 重连中
                      Log.e(TAG, "retryConnect: $times")
                  }
  
                  override fun unConnect() {
                      // 彻底无法连接，此时应该代码重启进程
                      Log.e(TAG, "unConnect: ")
                  }
              })
              .build() // 构造liveCheck
              .startCheckHardWareLive(this, 1000) // 开始检查 1000ms一次
  ```

- java
  
  ```java
  HardWareKeepLive.Builder builder = new HardWareKeepLive.Builder() // 构造builder
                  .setIsDebug(false) // 设置debug模式
                  .setBaseUrl("http://127.0.0.1:11220") // 设置接口地址
                  .setConnectCallback(new HardWareKeepLive.ConnectListener() { // 设置连接状态回调 见 kotlin 说明
                      @Override
                      public void lostConnect() {
  
                      }
  
                      @Override
                      public void reConnected() {
  
                      }
  
                      @Override
                      public void retryConnect(int times) {
  
                      }
  
                      @Override
                      public void unConnect() {
  
                      }
                  });
          builder.build().startCheckHardWareLive(c); // 开始检测 默认 3000ms 检测一次
  ```


