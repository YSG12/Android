package com.stav.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.db.domain.ProcessInfo;
import com.stav.mobilesafe.engine.ProcessInfoProvider;
import com.stav.mobilesafe.receiver.MyAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;



public class UpdateWidgetService extends Service {
    private Timer mTimer;
    private InnerReceiver mInnerReceiver;
    private IntentFilter mIntentFilter;
    private static final String tag = "UpdateWidgetService";


    public UpdateWidgetService() {
    }

    @Override
    public void onCreate() {
        //管理进程总数和借用内存数更新（定时器）
        startTimer();
        //注册开锁和解锁广播接收者
        mIntentFilter = new IntentFilter();
        //开锁action
        mIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        //解锁action
        mIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);

        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver,mIntentFilter);

        super.onCreate();
    }

    class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //开启定时更新的任务
                startTimer();
            } else{
                //关闭定时任务
                cancelTimerTask();
            }
        }
    }

    private void cancelTimerTask() {
        //mTimer中cancel方法取消定时任务方法
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void startTimer() {
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //UI定时刷新
                updateAppWidget();
            }
        }, 0 ,5000);
    }



    private void updateAppWidget() {
        //1.获取AppWidget对象
        AppWidgetManager aWM = AppWidgetManager.getInstance(this);
        //2.获取窗体小部件布局转换成View对象(包名，对应的布局文件)
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.example_appwidget);
        //3.给窗体小部件布View对象，内部空间赋值
        remoteViews.setTextViewText(R.id.tv_process_count,"进程总数："+ ProcessInfoProvider.getProcessCount(this));
        //4.显示可用内存大小
        String strAvailSpace = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailSpace(this));
        remoteViews.setTextViewText(R.id.tv_process_memory,"可用内存："+strAvailSpace);

        //点击窗体小部件，进入应用(在那个控件上响应，延期的意图)
        Intent intent = new Intent("android.intent.action.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.ll_root,pendingIntent);

        //通过言其意图发送广播，在广播接受者中杀死进程 匹配规则看action
        Intent broadcastIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROGRESS");
        PendingIntent broadcastPendingIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.bt_clear, broadcastPendingIntent);

        //上下文环境，窗体小部件对应广播接受者的字节码文件
        ComponentName componentName = new ComponentName(this, MyAppWidgetProvider.class);
        //更新窗体小部件
        aWM.updateAppWidget(componentName, remoteViews);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mInnerReceiver != null) {
            unregisterReceiver(mInnerReceiver);
        }
        //调用onDestroy即关闭服务，关闭服务的方法在移除最后一个小窗提示调用，定时任务也没必要维护
        cancelTimerTask();
        super.onDestroy();
    }
}
