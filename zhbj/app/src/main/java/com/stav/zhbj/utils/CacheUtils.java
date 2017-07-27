package com.stav.zhbj.utils;

import android.content.Context;

/**
 * 网络缓存工具类
 * Created by Administrator on 2017/7/22.
 */

public class CacheUtils {
    /**
     * 以url为key，以json为value，保存在本地
     * @param url
     * @param json
     */
    public static void setCache(String url, String json, Context ctx) {
        //也可以用文件缓存，以MD5（URl）为文件名，以jsonweineirong
        PrefUtils.setString(ctx,url,json);
    }

    /**
     * 获取缓存
     * @param url
     * @param ctx
     * @return
     */
    public static String getCache(String url, Context ctx) {
        //文件缓存：查找有没有一个文件叫做MD5（URl）的，说明有缓存
        return PrefUtils.getString(ctx,url,null);
    }
}
