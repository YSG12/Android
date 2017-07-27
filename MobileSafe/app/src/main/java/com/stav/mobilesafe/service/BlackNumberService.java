package com.stav.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.ITelephony;

import com.stav.mobilesafe.db.dao.BlackNumberDao;

import java.lang.reflect.Method;

import static com.stav.mobilesafe.R.drawable.phone;


public class BlackNumberService extends Service {
    private InnerSmsReceiver mInnerSmsReceiver;
    private SmsMessage smsMessage;
    private BlackNumberDao mDao;
    private TelephonyManager mTM;
    private MyPhoneStateListener mPhoneStateListener;
    private MyContentObserver mContentObserver;

    public BlackNumberService() {
    }

    @Override
    public void onCreate() {

        mDao = BlackNumberDao.getInstance(getApplicationContext());
        //拦截短信
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED_2");
        intentFilter.addAction("android.provider.Telephony.GSM_SMS_RECEIVED");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        intentFilter.setPriority(2147483647);
        mInnerSmsReceiver = new InnerSmsReceiver();
        registerReceiver(mInnerSmsReceiver,intentFilter);

        //监听电话的状态
        //1.电话管理者对象
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //2.监听电话状态
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);


        super.onCreate();
    }

    class MyPhoneStateListener extends PhoneStateListener {

        //手动重写，电话状态发送改变会触发的方法

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态（移除吐司）

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态 拨打或者通话

                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //挂断电话aidl
                    endCall(incomingNumber);
                    System.out.println("123");

                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    //挂断电话的方法
    public void endCall(String phone) {
//        ITelephony.Stub.asInterface(ServiceManage.getService(Context.TELEPHONY_SERVICE));

        int mode = mDao.getMode(phone);
        if (mode == 2 || mode == 3) {
            //拦截电话
            try {
                //1.获取ServiceManager文件
                Class<?> aClass = Class.forName("android.os.ServiceManager");
                //2.获取方法
                Method method = aClass.getMethod("getService", String.class);
                //反复调用此方法
                IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                //4.调用获取aidl文件对象方法
                ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
                //5.调用在aidl中隐藏的endCall方法
                iTelephony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //6.通过内容观察者，观察数据库的变化
            mContentObserver = new MyContentObserver(new Handler(),phone);
            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"),true,mContentObserver);

        }
    }

    /**
     * 创建内容观察者
     */
    private class MyContentObserver extends ContentObserver {
        private final String phone;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler, String phone) {
            super(handler);
            this.phone = phone;
        }

        //数据库发生改变的时候会调用此方法
        @Override
        public void onChange(boolean selfChange) {
            //删除此被拦截电话号码的通话记录
            getContentResolver().delete(Uri.parse("content://call_log/calls"),"number=?",new String[]{phone});

            super.onChange(selfChange);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信的内容，获取发送短信电话号码
            //如果电话号码在黑名单中，并且拦截模式为1或3
            //1.获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //2.循环遍历短信过程
            for (Object object : objects) {
                //3.获取短信对象
                String format = intent.getStringExtra("format");
                if(Build.VERSION.SDK_INT < 23){
                    smsMessage = SmsMessage.createFromPdu((byte[])object);
                }else{
                    smsMessage = SmsMessage.createFromPdu((byte[])object,format) ;
                }
                //4.获取短信独享的基本信息
                String originatingAddress = smsMessage.getOriginatingAddress();

                int mode = mDao.getMode(originatingAddress);

                if (mode == 1 || mode == 3) {
                    //拦截短信 中断广播接收者
                    abortBroadcast();
                }
            }
        }
    }
    @Override
    public void onDestroy() {
        //注销广播
        if (mInnerSmsReceiver != null) {
            unregisterReceiver(mInnerSmsReceiver);
        }
        //注销内容观察者
        if (mContentObserver != null) {
            getContentResolver().unregisterContentObserver(mContentObserver);
        }
        //取消对电话状态的监听
        if (mPhoneStateListener != null) {
            mTM.listen(mPhoneStateListener,PhoneStateListener.LISTEN_NONE);
        }
        super.onDestroy();
    }


}
