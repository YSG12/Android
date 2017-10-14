package com.stav.ideastreet.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.view.View;
import android.os.Process;
import android.widget.Toast;

import com.stav.ideastreet.global.IdeaStreetApplication;

/**
 * @author stav
 * @date 2017/9/4 17:50
 */
public class UIUtils {
    public static Context getContext() {
        return IdeaStreetApplication.getContext();
    }
    public static Handler getHandler() {
        return IdeaStreetApplication.getHandler();
    }
    public  static int getMainThreadId() {
        return IdeaStreetApplication.getMainThreadId();
    }

    /*加载资源文件*/

    //获取字符串
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    //获取字符串数组
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    //获取图片
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    //获取颜色
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }
    //根据id获取颜色的状态选择器
    public static ColorStateList getColorStateList(int mTabTextColorResId) {
        return getContext().getResources().getColorStateList(mTabTextColorResId);
    }

    //获取尺寸
    public static int getDimen(int id) {
        return getContext().getResources().getDimensionPixelSize(id);   //返回具体像素值
    }

    /* dip和px的互相转换 */
    public static int dip2px(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }
    public static int px2dip(float px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / density);
    }

    /*加载布局文件*/
    public static View inflate(int id) {
        return View.inflate(getContext(),id,null);
    }

    /*判断是否运行在主线程*/
    public static boolean isRunningThread() {
        //获取当前线程id，如果当前线程id和主线程id相同，那么当前就是主线程
        int myTid = Process.myTid();
        if (myTid == getMainThreadId()) {
            return true;
        }
        return false;
    }

    /* 运行在主线程 */
    public static void runOnUiThread(Runnable r) {
        if (isRunningThread()) {
            //已经是主线程，直接运行
            r.run();
        } else {
            //如果是主线程，借助handler让其运行在主线程
            getHandler().post(r);
        }
    }

    /**
     * 展示吐司
     */
    public static void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
