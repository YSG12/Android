package com.stav.mobilesafe.activity;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.stav.mobilesafe.R;

public class BackgroundActivity extends Activity {

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            finish();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        ImageView iv_bottom = (ImageView) findViewById(R.id.iv_bottom);
        ImageView iv_top = (ImageView) findViewById(R.id.iv_top);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        iv_bottom.startAnimation(alphaAnimation);
        iv_top.startAnimation(alphaAnimation);
        mHandler.sendEmptyMessageDelayed(0,1000);
    }
}
