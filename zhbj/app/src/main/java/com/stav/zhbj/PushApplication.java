package com.stav.zhbj;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2017/7/26.
 */

public class PushApplication extends Application {
        @Override
        public void onCreate() {
            super.onCreate();
            JPushInterface.setDebugMode(true);
            JPushInterface.init(this);
        }
    }

