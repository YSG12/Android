package com.stav.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.stav.mobilesafe.utils.ConstantValue;
import com.stav.mobilesafe.utils.SpUtil;

public class BootReceiver extends BroadcastReceiver {

    private static final String tag = "BootReceiver";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(tag,"开机了");

        //1.获取手机开机后的sim卡的序列号
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();
        //2.sp中储存的序列卡号
        String sim_number = SpUtil.getString(context, ConstantValue.SIM_NUMBER, "");
        //3.比对
        if (!simSerialNumber.equals(sim_number)){
            //4.发送短信给联系人号码
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage("15555218135",null,"SIM Changed!",null,null);
        }
    }
}
