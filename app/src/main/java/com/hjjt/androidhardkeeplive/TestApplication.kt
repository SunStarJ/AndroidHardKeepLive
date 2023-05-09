package com.hjjt.androidhardkeeplive

import android.app.Application
import android.util.Log
import com.hjjt.hardware_keep.HardWareKeepLive

/**
 *
 * @ProjectName:    AndroidHardKeepLive
 * @Package:        com.hjjt.androidhardkeeplive
 * @ClassName:      TestApplication
 * @Description:
 * @Author:         孙浩
 * @CreateDate:     2023/5/8 16:03
 */
class TestApplication : Application() {
    val TAG = "TestApplication"

    override fun onCreate() {
        super.onCreate()
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
    }
}