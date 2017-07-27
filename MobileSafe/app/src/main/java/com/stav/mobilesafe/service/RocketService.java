package com.stav.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.activity.BackgroundActivity;
import com.stav.mobilesafe.utils.ConstantValue;
import com.stav.mobilesafe.utils.SpUtil;

import java.security.MessageDigest;

public class RocketService extends Service {
    private WindowManager mWM;
    private int mScreenHeight;
    private int mScreenWidth;
    private View mRocketView;
    private int startX;
    private int startY;
    private ImageView iv_rocket;
    private WindowManager.LayoutParams params;
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            params.y = (Integer) msg.obj;
            //告知船体更新火箭view的所在位置
            mWM.updateViewLayout(mRocketView,mParams);
        }
    };
    public RocketService() {
    }

    @Override
    public void onCreate() {

        //手机屏幕宽长度的获取
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
        mScreenHeight = mWM.getDefaultDisplay().getHeight();
        mScreenWidth = mWM.getDefaultDisplay().getWidth();

        //开启火箭
        showRocket();

        super.onDestroy();
    }

    /**
     * 开启小火箭
     */
    private void showRocket() {

        //自定义小火箭吐司
        params = mParams;
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
        mRocketView = View.inflate(this, R.layout.rocket_view, null);
        iv_rocket = (ImageView) mRocketView.findViewById(R.id.iv_rocket);
        AnimationDrawable animationDrawable = (AnimationDrawable) iv_rocket.getBackground();
        animationDrawable.start();
        mWM.addView(mRocketView,params);

        mRocketView.setOnTouchListener(new View.OnTouchListener() {
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
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > mScreenWidth - mRocketView.getWidth()) {
                            params.x = mScreenWidth - mRocketView.getWidth();
                        }
                        if (params.y > mScreenHeight - mRocketView.getHeight()) {
                            params.y = mScreenHeight - mRocketView.getHeight();
                        }

                        //告知窗体吐司需要按照手势的移动，去做位置的更新
                        mWM.updateViewLayout(mRocketView, params);

                        //重置一次起始坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        if (params.x > mScreenWidth/3 && params.x<mScreenWidth/3*2&&params.y>mScreenHeight*3/4) {
                            //火箭发射
                            sendRocket();
                            //开启产生尾气的activity
                            Intent intent = new Intent(getApplicationContext(), BackgroundActivity.class);
                            //开启火箭后，关闭了唯一的activity对应的任务栈，所以在此处需要告知新开启的activity开辟一个新的任务栈
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                      break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 发射火箭
     */
    private void sendRocket() {
        //在向上的移动过程中，一直减少y轴的大小 直到减少到为0为止
        //在主线程中不能去睡眠，可能会导致主线程阻塞
        new Thread(){
            @Override
            public void run() {
                for (int i=0; i < 11; i++) {
                    int height = mScreenHeight*3/4 - i * (mScreenHeight*3/4/10);
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = Message.obtain();
                    msg.obj = height;
                    mHandler.sendMessage(msg);
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
        if (mWM != null && mRocketView != null) {
            mWM.removeView(mRocketView);
        }
        super.onDestroy();
    }
}
