package com.stav.zhbj.utils;

import android.graphics.Bitmap;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * 内存缓存
 * Created by Administrator on 2017/7/25.
 */
public class MemoryCacheUtils {

    private HashMap<String,SoftReference<Bitmap>> mMemoryCache = new HashMap<>();

    /**
     * 写缓存
     */
    public void setMemoryCache(String url,Bitmap bitmap) {
//        mMemoryCache.put(url,bitmap);
        SoftReference<Bitmap> softReference = new SoftReference<Bitmap>(bitmap);    //使用软引用将bitmap包装起来
        mMemoryCache.put(url,softReference);
    }
    /**
     * 读缓存
     */
    public Bitmap
    getMemoryCache(String url) {
        SoftReference<Bitmap> softReference = mMemoryCache.get(url);
        if (softReference != null) {
            Bitmap bitmap = softReference.get();
            return bitmap;
        }
        return null;
    }
}
