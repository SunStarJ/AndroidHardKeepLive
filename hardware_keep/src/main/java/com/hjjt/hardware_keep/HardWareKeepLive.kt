package com.hjjt.hardware_keep

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 *
 * @ProjectName:    AndroidHardKeepLive
 * @Package:        com.hjjt.hardware_keep
 * @ClassName:      HardWareKeepLive
 * @Description:
 * @Author:         孙浩
 * @CreateDate:     2023/5/8 15:13
 */
class HardWareKeepLive private constructor(val builder: Builder) {
    private val TAG = "HardWareKeepLive"

    var context: Context? = null
        private set
    private var keepAliveUrl = "${builder.baseUrl}/isLive"
    private var checkAlive = true
    private var retryTime = builder.retryTimes
    private var lost = false
    private val restartReceiverAction = "com.hjjt.hardware.RestartReceiver"
    private val restartReceiverPackage = "com.hjjt.hardware"
    private val restartReceiverClass = "com.hjjt.hardware.broadcastrec.RestartReceiver"

    private val okhttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS).build()

    fun startCheckHardWareLive(ctx: Context) {
        startCheckHardWareLive(ctx, 3000)
    }

    // 确认硬件是否存活
    fun startCheckHardWareLive(ctx: Context, timeMillis: Long) {
        this.context = ctx
        checkAlive = true
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                while (checkAlive) {
                    try {
                        val response = okhttpClient.newCall(createRequest()).execute()
                        if (response.isSuccessful) {
                            print("硬件存活")
                        } else {
                            restartAndroidServer()
                            print("硬件不存活")
                        }
                        response.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        restartAndroidServer()
                        print("硬件不存活")
                    }
                    delay(timeMillis)
                }
            }
        }
    }

    private fun print(message: String) {
        if (builder.isDebug) {
            // 获取年月日时分秒
            val time = System.currentTimeMillis()
            // 转换成 YYYY-MM-dd HH:mm:ss 格式
            val timeStr = android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", time)
            Log.e(TAG, "${timeStr}-$message")
        }
    }

    private fun restartAndroidServer() {
        // 失去连接
        if (lost) { // 如果状态已经是丢失，不再二次发送广播
            checkTimes()
            return
        }
        builder.connectListener?.lostConnect()
        //发送广播拉起另外一个app
        val intent = Intent()
        intent.action = restartReceiverAction
        intent.component = ComponentName(
            restartReceiverPackage,
            restartReceiverClass
        )
        context?.sendBroadcast(intent)
        lost = true

    }

    private fun checkTimes() {
        if (retryTime >= 3) {
            builder.connectListener?.unConnect()
            checkAlive = false
            return
        }
        retryTime++
        builder.connectListener?.retryConnect(retryTime)
    }

    private fun createRequest(): Request = Request.Builder().url(keepAliveUrl).build()

    class Builder {
        var connectListener: ConnectListener? = null
            private set
        var baseUrl = ""
            private set
        var isDebug = false
            private set
        var retryTimes = 3
            private set

        fun setConnectCallback(connectListener: ConnectListener): Builder {
            this.connectListener = connectListener
            return this
        }

        fun build(): HardWareKeepLive {
            return HardWareKeepLive(this)
        }

        fun setBaseUrl(url: String): Builder {
            this.baseUrl = url
            return this
        }

        fun setRetryTimes(times: Int): Builder {
            this.retryTimes = times
            return this
        }

        fun setIsDebug(isDebug: Boolean): Builder {
            this.isDebug = isDebug
            return this
        }
    }

    interface ConnectListener {
        /** 丢失连接 */
        fun lostConnect()

        /** 尝试重连 */
        fun reConnected()

        /**
         * 重连次数
         * @param times 重连次数
         * */
        fun retryConnect(times: Int)

        /** 连接失败，彻底无法连接 */
        fun unConnect()
    }

}
