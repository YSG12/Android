package com.stav.ideastreet.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;

/**
 * @author stav
 * @date 2017/9/4 17:40
 */
public class IdeaStreetApplication extends Application {
    private static Context mContext;

    private static Handler mHandler;
    private static int mainThreadId;

    public static Context getContext() {
        return mContext;
    }

    public static Handler getHandler() {
        return mHandler;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mHandler = new Handler();
        //当前线程ID，此处是主线程ID
        mainThreadId = Process.myPid();
    }
}
