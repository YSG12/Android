package com.stav.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.service.LocationService;
import com.stav.mobilesafe.utils.ConstantValue;
import com.stav.mobilesafe.utils.SpUtil;
import com.stav.mobilesafe.utils.ToastUtil;

import static android.content.Context.DEVICE_POLICY_SERVICE;

public class SmsReceiver extends BroadcastReceiver {
    private SmsMessage smsMessage;
    private ComponentName mDeviceAdminSample;
    private DevicePolicyManager mDPM;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        //获取设备管理者对象
        mDPM = (DevicePolicyManager) context.getSystemService(DEVICE_POLICY_SERVICE);
        // (上下文环境,自己配置广播接受者字节码)
        mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);

        //1.判断是否开启了防盗保护
        boolean open_security = SpUtil.getBoolean(context, ConstantValue.OPEN_SECURITY, false);
        if (open_security) {
            //2.获取短信内容
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            //3.循环遍历短信过程
            for (Object object : objects) {
                //4.获取短信对象
                String format = intent.getStringExtra("format");
                if(Build.VERSION.SDK_INT < 23){
                    smsMessage = SmsMessage.createFromPdu((byte[])object) ;
                }else{
                    smsMessage = SmsMessage.createFromPdu((byte[])object,format) ;
                }
                //5.获取短信独享的基本信息
                String originatingAddress = smsMessage.getOriginatingAddress();
                String messageBody = smsMessage.getMessageBody();
                //判断是否半酣播放音乐的关键字
                if (messageBody.contains("#*alarm*#")) {
                    System.out.println("452");
                    //7.播放音乐（准备音乐，MediaPlayer）
                    MediaPlayer mediaPlayer = new MediaPlayer().create(context, R.raw.ylzs);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
                if (messageBody.contains("#*location*#")) {
                    //8.开启获取位置服务
                    context.startService(new Intent(context,LocationService.class));
                }
                if (messageBody.contains("#*lockscreen*#")) {
                    //9.锁屏
                    //判断激活的状态
                    boolean adminActive = mDPM.isAdminActive(mDeviceAdminSample);
                    if(adminActive){
                        mDPM.lockNow();
                        //设置开机密码
                        mDPM.resetPassword("123456", 0);
                    }else{
                        ToastUtil.show(context,"请输入电话号码");
                    }
                }
                if (messageBody.contains("#*wipedata*#")) {

                    boolean adminActive = mDPM.isAdminActive(mDeviceAdminSample);
                    if(adminActive){
                        mDPM.wipeData(0);
                        //除了清除手机数据外,还清除sd卡中的数据
//			mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                    }else{
                        ToastUtil.show(context, "必须先激活,后清除");
                    }
                }
            }
        }

    }
}
