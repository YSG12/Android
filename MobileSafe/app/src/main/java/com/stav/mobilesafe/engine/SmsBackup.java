package com.stav.mobilesafe.engine;


import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/7/6.
 */

public class SmsBackup {
    private static int index = 0;
    //需要用的对象 上下文环境，备份文件夹的路径，进度条所在的对话框对象用于对象的更新
    //ProgressDialog pd 进度对话框 ProgressBar pd 进度条
    public static void backup(Context ctx, String path, CallBack callback) {
        FileOutputStream fos = null;
        Cursor cursor = null;
        try {
            //1.获取备份短信写入的文件
            File file = new File(path);
            //2.获取内容解析器
            cursor = ctx.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address", "date", "type", "body"}, null, null, null);
            //3.文件相应的输入流
            fos = new FileOutputStream(file);
            //4.序列化数据库中读取的数据，放置到xml中
            XmlSerializer serializer = Xml.newSerializer();
            //5.给此xml做相应的设置
            serializer.setOutput(fos,"utf-8");
            //DTD（xml规范）
            serializer.startDocument("utf-8",true);
            serializer.startTag(null,"smss");

            //6.文件备份总数指定
            //A 对话框 指定对话框进度条的总数 B 进度条 指定进度条的总数
            if (callback != null){
                callback.setMax(cursor.getCount());
            }

            //7.读取数据库中的每一行的数据写入到xml中
            while (cursor.moveToNext()) {
                serializer.startTag(null,"sms");

                serializer.startTag(null,"address");
                serializer.text(cursor.getString(0));
                serializer.endTag(null,"address");

                serializer.startTag(null,"date");
                serializer.text(cursor.getString(1));
                serializer.endTag(null,"date");

                serializer.startTag(null,"type");
                serializer.text(cursor.getString(2));
                serializer.endTag(null,"type");

                serializer.startTag(null,"body");
                serializer.text(cursor.getString(3));
                serializer.endTag(null,"body");

                serializer.endTag(null,"sms");

                //8.每循环一次就需要叠加进度条
                index++;
                Thread.sleep(100);
                //progressdialog可以在子线程中更新进度条的百分比
                //A 对话框 指定对话框进度条的当前百分比 B 进度条 指定进度条的指定对话框进度条的当前百分比
                if (callback != null) {
                    callback.setProgress(index);
                }
            }

            serializer.endTag(null,"smss");
            serializer.endDocument();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && fos != null) {
                cursor.close();
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
    //回调
    //1.定义一个接口
    //2.定义接口中未实现的业务逻辑方法（总数，当前百分比）
    //3.创建一个实现了此接口的类的对象（至备份短信的工具类中）
    //4.获取传递进来的对象，再合适的地方做方法的调用
    public interface CallBack {
        //短信总数设置为实现方法（由自己决定是用 对话框.setMax(max) 还是进度条。setMax(max)）
        void setMax(int max);
        //备份过程中短信百分比更新（由自己决定是用 对话框.setProgress(max) 还是进度条.setProgress（max））
        void setProgress(int index);
    }
}
