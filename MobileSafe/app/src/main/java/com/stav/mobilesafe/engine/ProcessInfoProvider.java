package com.stav.mobilesafe.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.os.Process;

import com.stav.mobilesafe.R;
import com.stav.mobilesafe.db.domain.ProcessInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/7.
 */

public class ProcessInfoProvider {
    //获取进程总数
    public static int getProcessCount(Context ctx) {
        //1.获取ActivityManager
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取正在运行的进程集合
        List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        return runningAppProcesses.size();
    }

    /**
     * 获取可用内存大小
     * @param ctx 上下文环境
     * @return 返回可用的内存大小 单位为bytes 返回0说明异常
     */
    public static long getAvailSpace(Context ctx) {
        //1.获取ActivityManager
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.构建存储可用内存的对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //3.给memoryInfo对象（可用内存）值
        am.getMemoryInfo(memoryInfo);
        //获取memoryInfo中相应可用内存的大小
        return memoryInfo.availMem;
    }

    /**
     * 获取总内存大小(Api > 16)
     * @param ctx 上下文环境
     * @return 返回总内存大小 bytes
     */
    public static long getTotalSpace(Context ctx) {
//        //1.获取ActivityManager
//        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
//        //2.构建存储可用内存的对象
//        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
//        //3.给memoryInfo对象（可用内存）值
//        am.getMemoryInfo(memoryInfo);
//        //获取memoryInfo中相应可用内存的大小
//        return memoryInfo.totalMem;

        // 内容大小写入文件中，读取proc/meminfo文件，读取第一行，获取数字字符，转换成bytes返回
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader("proc/meminfo");
            bufferedReader = new BufferedReader(fileReader);
            String lineOne = bufferedReader.readLine();
            //将字符串转成字符的数组
            char[] charArray = lineOne.toCharArray();
            //循环遍历每一个字符，如果次字符的ASCII码在0到9的区域内，说明次字符有效
            StringBuffer stringBuffer = new StringBuffer();
            for (char c : charArray) {
                if (c >= '0' && c <= '9'){
                    stringBuffer.append(c);
                }
            }
            //"51300" bytes
            return Long.parseLong(stringBuffer.toString()) *1024;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileReader != null && bufferedReader != null){
                try {
                    fileReader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     * 获取进程信息
     * @param ctx   上下文环境
     * @return 当前手机正在运行的进程
     */
    public static List<ProcessInfo> getProcessInfo(Context ctx) {
        //获取进程相关信息
        ArrayList<ProcessInfo> processInfos = new ArrayList<>();
        //1.ActivityManeger 管理者对象
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = ctx.getPackageManager();
        //2.获取正在运行的进程集合
        List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //3.循环遍历上诉集合，获取进程相关信息（名称，包名，图标，使用的内存大小，是否为系统进程）
        for (RunningAppProcessInfo info : runningAppProcesses) {
            //4.获取进程的名称 == 应用的包名
            ProcessInfo processInfo = new ProcessInfo();
            processInfo.packageName = info.processName;
            int pid = info.pid;
            //5.获取进程占用的内存大小(传递一个进程对应的pid数组)
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
            //6返回数组中索引位置为0的对象，为当前进程的内存信息的对象
            Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
            //7.获取已使用的大小
            processInfo.memSize = memoryInfo.getTotalPrivateDirty()*1024;
            //8.获取应用名称
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
                //8.获取应用名称
                processInfo.name = applicationInfo.loadLabel(pm).toString();
                //9.获取应用图标
                processInfo.icon = applicationInfo.loadIcon(pm);
                //10.判断是否为系统进程
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
                    processInfo.isSystem = true;
                } else {
                    processInfo.isSystem = false;
                }
            } catch (PackageManager.NameNotFoundException e) {
                //需要处理
                processInfo.name = info.processName;
                processInfo.icon = ctx.getResources().getDrawable(R.drawable.ic_launcher);
                processInfo.isSystem = true;
                e.printStackTrace();
            }
            processInfos.add(processInfo);
        }
        return processInfos;
    }

    /**
     * 杀死后台进程的方法
     * @param ctx 上下文环境
     * @param processInfo 杀死进程所在的javabean对象
     */
    public static void killProcess(Context ctx, ProcessInfo processInfo) {
        //1.获取ActivityManager对象
        ActivityManager  am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.杀死制定包名的进程(权限)
        am.killBackgroundProcesses(processInfo.getPackageName());
    }

    /**
     * 杀死所有进程
     * @param ctx 上上下文进程
     */
    public static void killAll(Context ctx) {
        //1.获取ActivityManager对象
        ActivityManager  am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        //2.获取正在运行的进程集合
        List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
        //循环遍历所有的进程，并且杀死
        for (RunningAppProcessInfo info : runningAppProcesses) {
            //除了手机卫士以外的进程都需要被杀死
            if (info.processName.equals(ctx.getPackageName())){
                //如果匹配到了手机卫士，则跳出循环
                continue;
            }
            am.killBackgroundProcesses(info.processName);
        }
    }
}
