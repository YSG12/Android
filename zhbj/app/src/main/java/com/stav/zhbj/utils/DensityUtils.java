package com.stav.zhbj.utils;

import android.content.Context;

/**
 * Created by Administrator on 2017/7/26.
 */

public class DensityUtils {

    public static int dis2px(float dip, Context ctx) {
        float density = ctx.getResources().getDisplayMetrics().density;
        int px = (int) (dip*density + 0.5f);    //四舍五入
        return px;
    }
    public static float px2dip(int px, Context ctx){
        float density = ctx.getResources().getDisplayMetrics().density;
        float dp = px / density;
        return dp;
    }
}


