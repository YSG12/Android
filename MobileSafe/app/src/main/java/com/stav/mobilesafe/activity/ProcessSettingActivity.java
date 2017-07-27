package com.stav.mobilesafe.activity;

import android.app.Service;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.service.LockScreenService;
import com.stav.mobilesafe.utils.ConstantValue;
import com.stav.mobilesafe.utils.ServiceUtil;
import com.stav.mobilesafe.utils.SpUtil;


public class ProcessSettingActivity extends AppCompatActivity {
    private CheckBox cb_show_system;
    private CheckBox cb_lock_screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_setting);
        
        initSystemShow();
        initLockScreenClear();
    }

    private void initLockScreenClear() {
        cb_lock_screen = (CheckBox) findViewById(R.id.cb_lock_screen);
        //根据锁屏清理服务是否开启，决定是否单选框选中
        boolean isRunning = ServiceUtil.isRunning(this, "com.stav.mobilesafe.service.LockScreenService");
        if (isRunning){
            cb_lock_screen.setText("锁屏清理已开启");
        }else {
            cb_lock_screen.setText("锁屏清理已关闭");
        }
        //cb_lock_screen选中状态的维护
        cb_lock_screen.setChecked(isRunning);
        //对选中状态进行监听
        cb_lock_screen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //isChecked作为是否被选中的状态
                if (isChecked) {
                    cb_lock_screen.setText("锁屏清理已开启");
                    //开启服务
                    startService(new Intent(getApplicationContext(),LockScreenService.class));
                } else {
                    cb_lock_screen.setText("锁屏清理已关闭");
                    //关闭服务
                    stopService(new Intent(getApplicationContext(),LockScreenService.class));
                }
            }
        });
    }

    private void initSystemShow() {
        cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
        //对之前储存过得状态进行回显
        boolean showSystem = SpUtil.getBoolean(this, ConstantValue.SHOW_SYSTEM, false);
        cb_show_system.setChecked(showSystem);
        //单选框显示状态
        if (showSystem) {
            cb_show_system.setText("显示系统进程");
        } else {
            cb_show_system.setText("隐藏系统进程");
        }
        //对选中状态进行监听
        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //isChecked作为是否被选中的状态
                if (isChecked) {
                    cb_show_system.setText("显示系统进程");
                } else {
                    cb_show_system.setText("隐藏系统进程");
                }
                SpUtil.putBoolean(getApplicationContext(), ConstantValue.SHOW_SYSTEM,isChecked);

            }
        });
    }
}
