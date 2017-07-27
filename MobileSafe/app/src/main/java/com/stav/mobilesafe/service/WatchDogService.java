package com.stav.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

import com.stav.mobilesafe.activity.EnterPsdActivity;
import com.stav.mobilesafe.db.dao.AppLockDao;

import java.util.List;
import java.util.Observable;

public class WatchDogService extends Service {
    private boolean isWatch;
    private AppLockDao mDao;
    private List<String> mPackagenameList;
    private InnerReceiver mInnerReceiver;
    private String mSkipPackagename;
    private MyContentObservable mContentObservable;
    public WatchDogService() {
    }

    @Override
    public void onCreate() {
        //维护一个开门狗的死循环，让其时刻检测现在开启的应用是否为程序锁拦截应用
        mDao = AppLockDao.getInstance(this);
        isWatch = true;
        watch();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SKIP");
        mInnerReceiver = new InnerReceiver();
        registerReceiver(mInnerReceiver,intentFilter);
        //注册一个内容观察者，观察数据的变化，一旦数据删除或者添加，则需要让mPackagenameList重新获取一次数据
        mContentObservable = new MyContentObservable(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://applock/change"),true,mContentObservable);
        super.onCreate();
    }
    class MyContentObservable extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObservable(Handler handler) {
            super(handler);
        }

        //一旦数据库发生改变，调用该方法，重新获取包名所在集合的数据
        @Override
        public void onChange(boolean selfChange) {
            new Thread(){
                @Override
                public void run() {
                    mPackagenameList = mDao.findAll();
                }
            }.start();
            super.onChange(selfChange);
        }
    }
    class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取发送广播过程中传递过来的包名，跳过包名检测过程
            mSkipPackagename = intent.getStringExtra("packagename");
        }
    }

    private void watch() {
        //1.子线程中，开启一个可控死循环
        new Thread(){
            @Override
            public void run() {
                mPackagenameList = mDao.findAll();
                while(isWatch) {
                    //2.检测现在正在开启的应用，任务栈
                    //3.获取activity管理者对象
                    ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                    //4.获取正在开启任务栈
                    List<ActivityManager.RunningTaskInfo> runningTasks = am.getRunningTasks(1);
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
                    //5.获取栈顶的activity，然后在获取此activity所在应用的包名
                    String packageName = runningTaskInfo.topActivity.getPackageName();
                    //6.拿此包名在已加锁的包名集合中去对比，如果包含此包名，则需要弹出拦截页面
                    if (mPackagenameList.contains(packageName)) {

                        //如果已经解锁就不需要去弹出解锁页面
                        if (!packageName.equals(mSkipPackagename)) {
                            //7.弹出拦截页面
                            Intent intent = new Intent(getApplicationContext(),EnterPsdActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.putExtra("packagename",packageName);
                            startActivity(intent);
                        }
                    }
                    //睡眠一下,时间片转轮
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        //停止看门狗循环
        isWatch = false;
        //注销广播接收者
        if (mInnerReceiver != null) {
            unregisterReceiver(mInnerReceiver);
        }
        super.onDestroy();
        //注销内容观察者
        if (mContentObservable != null) {
            getContentResolver().unregisterContentObserver(mContentObservable);
        }
    }

}
