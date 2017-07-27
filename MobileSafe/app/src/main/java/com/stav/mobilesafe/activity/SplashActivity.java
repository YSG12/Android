package com.stav.mobilesafe.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.bitmap.factory.BitmapFactory;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.stav.mobilesafe.R;
import com.stav.mobilesafe.utils.ConstantValue;
import com.stav.mobilesafe.utils.SpUtil;
import com.stav.mobilesafe.utils.StreamUtil;
import com.stav.mobilesafe.utils.ToastUtil;

import net.youmi.android.AdManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Stream;

public class SplashActivity extends AppCompatActivity {
    //更新新版本的状态码
    private static final int UPDATE_VERSION = 100;
    //进入程序主界面
    private static final int ENTER_HOME = 101;
    //url地址出错状态码
    private static final int URL_ERROR = 102;
    //IO流出错状态码
    private static final int IO_ERROR = 103;
    //json出错状态码
    private static final int JSON_ERROR = 104;

    private TextView tv_version_name;
    private int mLocalVersionCode;
    private String mVersionDes;
    private String mDownloadUrl;
    private RelativeLayout rl_root;
    protected static final String tag = "SplashActivity";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_VERSION:
                    //弹出对话框，提示用户更新
                    showUpdateDialog();

                    break;
                case ENTER_HOME:
                    //进入程序主界面，activity跳转过程
                    enterHome();
                    break;
                case URL_ERROR:
                    ToastUtil.show(SplashActivity.this, "URL异常");
                    enterHome();
                    break;
                case IO_ERROR:
                    ToastUtil.show(SplashActivity.this, "读取异常");
                    enterHome();
                    break;
                case JSON_ERROR:
                    ToastUtil.show(SplashActivity.this, "JSON解析异常");
                    enterHome();
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * 弹出对话框，提示用户更新
     */
    private void showUpdateDialog() {
        //对话框是依赖于activity存在的
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("版本更新");
        builder.setMessage(mVersionDes);
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
            httpUtils.download(mDownloadUrl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功
                    Log.i(tag, "下载成功");
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

    /**
     * 开启一个activity后，返回调用的方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        enterHome();
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        //去除当前activity的title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);


        AdManager.getInstance(this).init("da9ae548471e870b", "3c60b828e2ca29bb", true);

        //初始化UI
        initUI();

        //初始化数据
        initData();

        //初始化动画
        initAnimation();

        //初始化数据库
        initDB();

        if (!SpUtil.getBoolean(this, ConstantValue.HAS_SHORTCUT, false)) {
            //生成快捷方式
            initShortCut();
        }

    }

    /**
     * 生成快捷方式
     */
    /**
     * 生成快捷方式
     */
    private void initShortCut() {
        //1,给intent维护图标,名称
        Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        //维护图标
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, android.graphics.BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        //名称
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "黑马卫士");
        //2,点击快捷方式后跳转到的activity
        //2.1维护开启的意图对象
        Intent shortCutIntent = new Intent("android.intent.action.HOME");
        shortCutIntent.addCategory("android.intent.category.DEFAULT");

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
        //3,发送广播
        sendBroadcast(intent);
        //4,告知sp已经生成快捷方式
        SpUtil.putBoolean(this, ConstantValue.HAS_SHORTCUT, true);
    }

    /**
     * 初始化数据库
     */
    private void initDB()  {
        //1.归属地数据库拷贝过程
        initAddress("address.db");
        //2.常用电话数据库拷贝过程
        initAddress("commonnum.db");
        //3.k拷贝病毒数据库
        initAddress("antivirus.db");
    }

    /**
     * 拷贝数据库值file文件夹下
     *
     */
    private void initAddress(String dbName) {
        //1.在files文件下穿件同名数据库文件过程
        File files = getFilesDir();
        File file = new File(files, dbName);
        if (file.exists()) {
            return;
        }
        InputStream stream = null;
        FileOutputStream fos = null;
        //2.输入流读取第三方资产目录的文件
        try{
            stream = getAssets().open(dbName);
            //3.将读取的内容写入到指定的文件件的文件中去
            fos = new FileOutputStream(file);
            //4.每次的读取内容的大小
            byte[] bs= new byte[1024];
            int temp = -1;
            while ((temp = stream.read(bs))!=-1){
                fos.write(bs,0,temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream!=null&&fos!=null) {
                try {
                    stream.close();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    /**
     * 添加淡入页面的方法
     */
    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(3000);
        rl_root.startAnimation(alphaAnimation);

    }

    /**
     * 进入应用程序的主界面
     */
    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //在开启一个新的界面后，将导航界面关闭（使导航界面只可见一次）
        finish();

    }

    /**
     * 获取数据方法
     */
    private void initData() {
        //1.应用版本名称
        tv_version_name.setText("版本名称 " + getVersionName());
        //检测（本地版本号和服务器版本号对比）是否有更新，如果有更新提示客户更新，
        //2.获取本地版本号
        mLocalVersionCode = getVersionCode();
        //3.获取服务器的版本号（客户端发请求，服务器给响应，（json，xml））
        //http：//www.oxxx.com/update.json?key=value 返回200 请求成功 通过流的方式读取数据
        //json中内容包含：更新版本的版本名称 新版本的描述信息 服务器版本号 新版本apk下载地址

        //检测版本号
        if (SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false)) {
            checkVersion();
        } else {
            //直接进入应用程序主界面
            //消息机制
            //mHandler.sendMessageDelayed(msg, 4000);
            //在发送消息4秒后去处理ENTER_HOME状态码指向的消息
            mHandler.sendEmptyMessageDelayed(ENTER_HOME,4000);
        }
    }

    /**
     * 检测版本号
     */
    private void checkVersion() {
        //开启子线程
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//            }
//        }.start();

        new Thread() {
            @Override
            public void run() {
                //发送请求数据 参数则为请求json的地址
                //http://192.168.1.103:8080/update.json 测试阶段不是最优
                //仅限于模拟器访问电脑tomcat
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    //1.封装URL地址
                    URL url = new URL("http://47.94.129.228/android/update.json");
                    //2.开启一个链接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //3.设置常见参数（请求头）
                    connection.setConnectTimeout(2000);//请求超时
                    connection.setReadTimeout(2000);//读取超时
                    //默认就是get请求方式
                    //connection.setRequestMethod("POST");
                    //4.获取请求成功的响应码
                    if (connection.getResponseCode() == 200) {

                        //5.以流的方式，将数据获取下来
                        InputStream is = connection.getInputStream();
                        //6.将流转换成字符串（工具类封装）
                        String json = StreamUtil.streamToString(is);
//                        Log.i(tag,json);
                        //7.json的解析
                        JSONObject jsonObject = new JSONObject(json);
                        String versionName = jsonObject.getString("versionName");
                        mVersionDes = jsonObject.getString("versionDes");
                        String versionCode = jsonObject.getString("versionCode");
                        mDownloadUrl = jsonObject.getString("downloadUrl");

                        //debug
                        Log.i(tag, versionName);
                        Log.i(tag, mVersionDes);
                        Log.i(tag, versionCode);
                        Log.i(tag, mDownloadUrl);

                        //8.对比版本号（服务器版本号大于>本地版本号，提示用户更新）
                        if (mLocalVersionCode < Integer.parseInt(versionCode)) {
                            //提示用户更新，淡出对话框（UI），消息机制
                            msg.what = UPDATE_VERSION;

                        } else {
                            //不需要更新主界面
                            msg.what = ENTER_HOME;


                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    msg.what = URL_ERROR;
                } catch (IOException e) {
                    e.printStackTrace();
                    msg.what = IO_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    msg.what = JSON_ERROR;
                } finally {
                    //指定睡眠时间，请求网络的时长不超过4s则不作处理
                    //请求网络的时长小鱼4，强制让其睡眠4s中
                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime < 4000) {
                        try {
                            Thread.sleep(4000 - (endTime - startTime));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    mHandler.sendMessage(msg);
                }

            }
        }.start();
    }

    /**
     * 返回版本号
     *
     * @return 非0则表示获取成功
     */
    public int getVersionCode() {
        //1.包管理者对象packageManager
        PackageManager pm = getPackageManager();
        //2.从包的管理者对象中，获取指定报名的基本信息,传0表示获取基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //获取版本号
            return packageInfo.versionCode;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @return 应用版本名称
     * 获取版本名称：清单文件 返回null表示异常
     */
    public String getVersionName() {
        //1.包管理者对象packageManager
        PackageManager pm = getPackageManager();
        //2.从包的管理者对象中，获取指定报名的基本信息,传0表示获取基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //获取版本名称
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化UI方法
     */
    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
    }
}
