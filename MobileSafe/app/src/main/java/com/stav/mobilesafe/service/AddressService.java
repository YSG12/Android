package com.stav.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Telephony;
import android.support.annotation.IntDef;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.engine.AddressDao;
import com.stav.mobilesafe.utils.ConstantValue;
import com.stav.mobilesafe.utils.SpUtil;

public class AddressService extends Service {

    private TelephonyManager mTM;
    private View mViewToast;
    private int[] mDrawableIds;
    private int startX;
    private int startY;
    private WindowManager mWM;
    private String mAddress;
    private TextView tv_toast;
    private MyPhoneStateListener mPhoneStateListener;
    private  static final String tag = "AddressService";
    private int mScreenWidth;
    private int mScreenHeight;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            tv_toast.setText(mAddress);
        }
    };
    private InnerOutCallReceiver mInnerOutCallReceiver;

    public AddressService() {
    }


    @Override
    public void onCreate() {
        //第一次开启服务以后，就需要管理吐司的显示
        //电话状态的监听
        //1.电话管理者对象
        mTM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //2.监听电话状态
        mPhoneStateListener = new MyPhoneStateListener();
        mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        //手机屏幕宽长度的获取
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenHeight = mWM.getDefaultDisplay().getHeight();
        mScreenWidth = mWM.getDefaultDisplay().getWidth();

        //监听拨出电话的广播过滤条件
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        //创建广播接收者
        mInnerOutCallReceiver = new InnerOutCallReceiver();
        registerReceiver(mInnerOutCallReceiver,intentFilter);
        super.onCreate();
    }

    private class InnerOutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //接收到此广播后，需要显示自定义吐司，显示播出归属地号码
            //获取播出电话号码的字符串
            String phone = getResultData();
            showToast(phone);
        }

    }

    class MyPhoneStateListener extends PhoneStateListener {

        //手动重写，电话状态发送改变会触发的方法

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //空闲状态（移除吐司）
                    Log.i(tag,"空闲状态");
                    if (mWM !=null && mViewToast != null) {
                        mWM.removeView(mViewToast);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态 拨打或者通话
                    Log.i(tag,"接听状态");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(tag,"响铃状态");
                    //来电状态 响铃（展示吐司）
                    showToast(incomingNumber);
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 通话过程中吐司的展示
     */
    private void showToast(String incomingNumber) {

        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
//        params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        //在响铃的时候显示吐司
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.setTitle("Toast");
        //指定吐司所在位置(将吐司指定在左上角)
        params.gravity = Gravity.LEFT + Gravity.TOP;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE; //默认能被触摸
        //吐司显示效果（吐司布局文件）将吐司挂在在手机窗体上
        mViewToast = View.inflate(this, R.layout.toast_view, null);
        tv_toast = (TextView) mViewToast.findViewById(R.id.tv_toast);



        //给响铃的状态设置拖拽事件
        mViewToast.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int moveX = (int) event.getRawX();
                        int moveY = (int) event.getRawY();
                        int disX = moveX - startX;
                        int disY = moveY - startY;

                        params.x = params.x + disX;
                        params.y = params.y + disY;

                        //容错处理
                        if (params.x < 0){
                            params.x = 0;
                        }
                        if (params.y < 0){
                            params.y = 0;
                        }
                        if (params.x > mScreenWidth-mViewToast.getWidth()){
                            params.x = mScreenWidth-mViewToast.getWidth();
                        }
                        if (params.y > mScreenHeight-mViewToast.getHeight()-40) {
                            params.y = mScreenHeight-mViewToast.getHeight()-40;
                        }

                        //告知窗体吐司需要按照手势的移动，去做位置的更新
                        mWM.updateViewLayout(mViewToast, params);

                        //重置一次起始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        SpUtil.putInt(getApplicationContext(),ConstantValue.LOCAITION_X,params.x);
                        SpUtil.putInt(getApplicationContext(),ConstantValue.LOCAITION_Y,params.y);

                        break;
                    default:
                        break;
                }
                    return true;
            }
        });

        //读取sp中储存的吐司位置的xy坐标
        //params.x 表示吐司左上角的x的坐标 params.y 表示吐司左上角的y的坐标
        params.x = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCAITION_X, 0);
        params.y = SpUtil.getInt(getApplicationContext(), ConstantValue.LOCAITION_Y, 0);

        //从sp中获取色值文字的索引，匹配图片，用作展示
        mDrawableIds = new int[]{R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green};

        int toastStyleIndex = SpUtil.getInt(getApplicationContext(), ConstantValue.TOAST_STYLE, 0);
        tv_toast.setBackgroundResource(mDrawableIds[toastStyleIndex]);

        //获取窗体对象
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        //在窗体挂载在一个view（权限）
        mWM.addView(mViewToast,mParams);

        //获取到来电号码以后，需要做来电号码查询
        query(incomingNumber);
    }

    /**
     * 号码归属地查询
     * @param incomingNumber 来电号码
     */
    private void query(final String incomingNumber) {
        new Thread(){
            @Override
            public void run() {
                mAddress = AddressDao.getAddress(incomingNumber);
                mHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        //取消对电话状态的监听(开启服务的时候监听电话的对象)
        if (mTM != null && mPhoneStateListener != null) {
            mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        if (mInnerOutCallReceiver != null){
            //去电广播接收者的注销过程
            unregisterReceiver(mInnerOutCallReceiver);
        }
        super.onDestroy();
    }



}
