package com.stav.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Administrator on 2017/6/30.
 */

public class ServiceUtil {

    /**
     * 判断是服务是否运行
     * @param serviceName 服务名称
     * @return true 运行， false 没有运行
     */
    public static boolean isRunning(Context ctx, String serviceName) {
        //1.获取activityManager管理者对象，可以获取当前手机所有正在运行的服务
        ActivityManager mAM = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取手机中正在运行的服务的集合(多少个服务)
        List<ActivityManager.RunningServiceInfo> runningServices = mAM.getRunningServices(1000);
        //3.便利获取的所有的服务集合，拿到每一个服务的名称，和传递过来的类作比对，说明服务正在运行
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            //4.获取每一个真正的运行服务的名称
            if (serviceName.equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
