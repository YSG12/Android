package com.stav.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2017/6/8.
 */

/**
 * 流转换成字符串
 * is 流对象
 * @return 流转换成的字符串 返回null代表异常
 */
public class StreamUtil {
    public static String streamToString(InputStream is) {
        //1.在读取的过程中 将读取的内容存储值缓存中，然后一次性的转换成字符串
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //2.读流的操作，读到没有位置（循环）
        byte[] buffer = new byte[1024];
        //3.记录读取内容的临时变量
        int temp = -1;
        try {
            while ((temp = is.read(buffer)) != -1) {
                bos.write(buffer,0,temp);
            }

            //返回读取的数据
            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }
}
