package com.stav.ideastreet.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.stav.ideastreet.R;
import com.stav.ideastreet.base.ParentWithNaviActivity;

import java.io.File;

import cn.bmob.v3.BmobUser;

public class SettingsActivity extends ParentWithNaviActivity implements View.OnClickListener {

    private LinearLayout ll_about,ll_update,ll_logout;

    protected static final String tag = "SettingsActivity";

    /**
     * 设置actionBar
     * @return
     */
    @Override
    protected String title() {
        return "设置";
    }

    @Override
    public ParentWithNaviActivity.ToolBarListener setToolBarListener() {
        return new ParentWithNaviActivity.ToolBarListener() {
            //退出该页面
            @Override
            public void clickLeft() {
                finish();
            }

            //发表微博
            @Override
            public void clickRight() {
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initNaviView();
        initUI();

    }

    private void initUI() {
        ll_about = (LinearLayout) findViewById(R.id.ll_about);
        ll_update = (LinearLayout) findViewById(R.id.ll_update);
        ll_logout = (LinearLayout) findViewById(R.id.ll_logout);
        ll_about.setOnClickListener(this);
        ll_update.setOnClickListener(this);
        ll_logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_about:
                startActivity(new Intent(getApplicationContext(),AboutActivity.class));
                break;
            case R.id.ll_update:
                showUpdateDialog();
                break;
            case R.id.ll_logout:
                logOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                break;
        }
    }
    /**
     * 清除本地用户
     */
    private void logOut() {
        BmobUser.logOut();
    }

    /**
     * 弹出对话框，提示用户更新
     */
    private void showUpdateDialog() {
        //对话框是依赖于activity存在的
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("版本更新");
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载apk，apk的链接地址
                downloadApk();
            }
        });
        builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消对话框
                enterHome();

            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //即使用户点击取消也能进入主页面
                enterHome();
                dialog.dismiss();

            }
        });
        builder.show();
    }
    /**
     * 进入应用程序的主界面
     */
    private void enterHome() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //在开启一个新的界面后，将导航界面关闭（使导航界面只可见一次）
        finish();

    }

    /**
     * 下载apk的方法
     */
    private void downloadApk() {
        //apk的下载地址，放置apk所在的路径

        //1.判断sd卡是否可用，是否挂载上
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //2.获取sd路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mobilesafe.apk";
            //3.发送请求，获取apk，并且放置指定路径
            HttpUtils httpUtils = new HttpUtils();
            //4.发送请求，传递参数（下载地址，下载应用放置位置）
            httpUtils.download("http://47.94.129.228/android/mobilesafe.apk", path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功
                    Log.i(tag,"下载成功");
                    File file = responseInfo.result;
                    //提示用户安装

                    installApk(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    //下载失败
                    Log.i(tag, "下载失败");

                }

                //刚刚下载的方法
                @Override
                public void onStart() {
                    Log.i(tag, "刚刚开始下载");

                    super.onStart();
                }

                //下载过程中的方法（下载apk的总大小，下载位置，是否在下载）
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    Log.i(tag, "下载中。。。");
                    Log.i(tag, "total=" + total);
                    Log.i(tag, "current=" + current);

                    super.onLoading(total, current, isUploading);
                }

            });
        }
    }

    /**
     * 安装对应apk
     *
     * @param file 安装文件
     */
    private void installApk(File file) {
        //系统应用界面，源码，安装apk的入口

//        <action android:name="android.intent.action.VIEW" />
//        <category android:name="android.intent.category.DEFAULT" />
//        <data android:scheme="content" />
//        <data android:scheme="file" />
//        <data android:mimeType="application/vnd.android.package-archive" />
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
//        //文件作为数据源
//        intent.setData(Uri.fromFile(file));
//        //设置安装的类型
//        intent.setType("application/vnd.android.package-archive");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, 0);

    }


}
