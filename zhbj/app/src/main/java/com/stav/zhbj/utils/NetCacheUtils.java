package com.stav.zhbj.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络缓存
 * Created by Administrator on 2017/7/25.
 */

public class NetCacheUtils {

    private Bitmap bitmap;
    private ImageView iv;
    private String url;

    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        mLocalCacheUtils = localCacheUtils;
        mMemoryCacheUtils = memoryCacheUtils;
    }

    public void getBitmapFromNet(ImageView iv, String url) {
        //AsyncTask异步封装的工具，可以实现异步请求及主界面更新，对线程池+handler的封装
        new BitmapTask().execute(iv,url); //启动AsyncTask
    }

    /**
     * 三个泛型的意义
     * 第一个：doInBackground里的参数
     * 第二个：onProgressUpdate里的参数
     * 第二个：onPostExecute里的参数类型及onProgressUpdate里的参数
     */
    class BitmapTask extends AsyncTask<Object,Integer,Bitmap> {

        private String url;

        //1.预加载，运行在主线程
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //2.正在加载，运行在子线程（核心） 可以直接异步请求
        @Override
        protected Bitmap doInBackground(Object... params) {
            iv = (ImageView) params[0];
            url = (String) params[1];

            iv.setTag(url); //给url设置一个标记，将当前iv和url绑定在一起

            //开始下载图片
            bitmap = download(url);

            return bitmap;
        }

        //3.更新进度的方法，运行在主线程
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        //4.加载结束，运行在主线程（核心），可以直接更新UI
        @Override
        protected void onPostExecute(Bitmap result) {

            if (result != null) {
                //设置图片给imageView
                //由于listView的重用机制导致imageView对象可能被多个item共用，可能将错误的图片设置给了imageView对象，
                // 所以需要在此处校验，判断是否是正确图片
                String url = (String) iv.getTag();
                if (url.equals(this.url)) { //判断图片绑定的url是否是当前bitmap的url，如果是说明图片正确
                    iv.setImageBitmap(result);
                    //写本地缓存
                    mLocalCacheUtils.setLocalCache(url,result);
                    mMemoryCacheUtils.setMemoryCache(url,result);
                }
            }

            super.onPostExecute(result);
        }
    }

    //下载图片
    private Bitmap download(String url) {
        HttpURLConnection conn = null;

        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);   //连接超时
            conn.setReadTimeout(5000);  //读取超时

            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = conn.getInputStream();
                //根据输入流生成bitmap对象
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return null;
    }

}
