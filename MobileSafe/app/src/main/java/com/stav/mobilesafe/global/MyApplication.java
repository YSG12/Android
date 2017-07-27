package com.stav.mobilesafe.global;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2017/7/13.
 */

public class MyApplication extends Application {

    private static final String tag = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        //捕获全局(应用任意模块)异常
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                //在捕获异常之后，处理的方法
                e.printStackTrace();
                Log.i(tag, "捕获到了一个程序的异常");
                //将捕获的异常储存到sd卡中
                String path = Environment.getExternalStorageDirectory().getAbsoluteFile() + File.separator + "errorlog";
                File file = new File(path);
                try {
                    PrintWriter printWriter = new PrintWriter(file);
                   e.printStackTrace(printWriter);
                    printWriter.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }

                //上传公司服务器 结束应用
                System.exit(0);
            }
        });
    }
}
