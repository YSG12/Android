package com.stav.mobilesafe.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.engine.SmsBackup;

import org.w3c.dom.Text;

import java.io.File;

public class AToolsActivity extends AppCompatActivity {
    private TextView tv_query_phone_address;
    private TextView tv_sms_backup;
    private ProgressBar pb_bar;
    private TextView tv_commonnumber_query;
    private TextView tv_app_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);

        //电话归属地查询方法
        initPhoneAddress();
        //短信备份的方法
        initSmsBackup();
        //常用号码查询
        initCommonNumberQuery();
        //程序锁
        initAppLock();
    }


    private void initAppLock() {
        tv_app_lock = (TextView) findViewById(R.id.tv_app_lock);
        tv_app_lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AppLockActivity.class));
            }
        });
    }

    private void initCommonNumberQuery() {
        tv_commonnumber_query = (TextView) findViewById(R.id.tv_commonnumber_query);
        tv_commonnumber_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CommonNumberQueryActivity.class));
            }
        });
    }

    /**
     * 短信备份
     */
    private void initSmsBackup() {
        tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
//        pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
        tv_sms_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSmsBackupDialog();
            }
        });
    }

    /**
     * 弹出短信备份进度条对话框
     */
    private void showSmsBackupDialog() {
        //1.传建一个带进度条的对话框
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.ic_launcher);
        progressDialog.setTitle("短信备份");
        //2.制定进度条的样式为水平
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //3.展示进度条
        progressDialog.show();

        //4.获取系统短信
        new Thread(){
            @Override
            public void run() {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sms.xml";
                SmsBackup.backup(getApplicationContext(), path, new SmsBackup.CallBack() {
                    @Override
                    public void setMax(int max) {
                        progressDialog.setMax(max);
//                        pb_bar.setMax(max);
                    }

                    @Override
                    public void setProgress(int index) {
                        progressDialog.setProgress(index);
//                        pb_bar.setProgress(index);
                    }
                });
//                progressDialog.dismiss();
            }
        }.start();
    }

    /**
     * 电话归属地查询方法
     */
    private void initPhoneAddress() {
        tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
        tv_query_phone_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),QueryAddressActivity.class));
            }
        });
    }
}
