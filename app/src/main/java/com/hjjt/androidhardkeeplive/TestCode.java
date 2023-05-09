package com.hjjt.androidhardkeeplive;

import android.content.Context;

import com.hjjt.hardware_keep.HardWareKeepLive;

/**
 * @ProjectName: AndroidHardKeepLive
 * @Package: com.hjjt.androidhardkeeplive
 * @ClassName: TestCode
 * @Description:
 * @Author: 孙浩
 * @CreateDate: 2023/5/8 16:17
 */
public class TestCode {

    public void test(Context context){
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
        builder.build().startCheckHardWareLive(context); // 开始检测 默认 3000ms 检测一次
    }
}
