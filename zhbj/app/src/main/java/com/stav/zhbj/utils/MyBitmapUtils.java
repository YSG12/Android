package com.stav.zhbj.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.stav.zhbj.R;

/**
 * 自定义三级缓存图片加载工具
 * Created by Administrator on 2017/7/25.
 */

public class MyBitmapUtils {

    private MemoryCacheUtils mMemoryCacheUtils;
    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private Bitmap bitmap;

    public MyBitmapUtils() {
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils();
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils,mMemoryCacheUtils);
    }

    public void display(ImageView iv, String url) {

        //设置默认图片
        iv.setBackgroundResource(R.mipmap.pic_item_list_default);

        //优先从内存中加载图片，速度最快，不浪费流量
        Bitmap bitmap = mMemoryCacheUtils.getMemoryCache(url);
        if (bitmap != null) {
            iv.setImageBitmap(bitmap);
            System.out.println("从内存中加载图片对象");
            return;
        }

        //其次从本地（sdcard）加载图片，速度快，不浪费流量
        bitmap = mLocalCacheUtils.getLocalCache(url);
        if (bitmap != null){
            iv.setImageBitmap(bitmap);
            //写内存缓存
            mMemoryCacheUtils.setMemoryCache(url, bitmap);
            return;
        }
        //最后从网络加载下载图片，速度慢，浪费流量

        mNetCacheUtils.getBitmapFromNet(iv,url);

    }
}
