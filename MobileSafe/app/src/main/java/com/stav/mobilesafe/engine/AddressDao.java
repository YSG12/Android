package com.stav.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.stav.mobilesafe.utils.ToastUtil;

/**
 * Created by Administrator on 2017/6/29.
 */

public class AddressDao {
    //1.指定访问数据可的路径
    public static String path = "data/data/com.stav.mobilesafe/files/address.db";
    private static String mAddress = "未知号码";

    /**
     * 传递一个电话号码，开启数据库链接，进行访问，返回一个归属地
     * @param phone 查询电话号码
     */
    public static String getAddress(String phone) {
        mAddress = "未知号码";
        //正则表达式，匹配手机号码
        String regularExpression = "^1[3-8]\\d{9}";
        //2.开启数据库链接（只读方式）
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        if(phone.matches(regularExpression)){
            phone = phone.substring(0,7);
            //3.数据库查询
            Cursor cursor = db.query("data1", new String[]{"outkey"}, "id=?", new String[]{phone}, null, null, null);
            //查到即可
            if (cursor.moveToNext()){
                String outkey = cursor.getString(0);
                //5.通过data1查询到的结果，作为外键查询data2
                Cursor indexCursor = db.query("data2", new String[]{"location"}, "id=?", new String[]{outkey}, null, null, null);
                if (indexCursor.moveToNext()){
                    //6.获取查询到的电话归属地
                    mAddress = indexCursor.getString(0);
                    System.out.println(mAddress);
                }
            } else {
                mAddress = "未知号码";
            }
        }else {
            int length = phone.length();
            switch (length){
                case 3:
                    mAddress = "紧急电话";
                    break;
                case 4:
                    mAddress = "模拟器电话";
                    break;
                case 5:
                    mAddress = "服务电话";
                    break;
                case 7:
                    mAddress = "本地电话";
                    break;
                case 11:
                    //（3+8）区号+座机号码 查询data2
                    String area = phone.substring(1, 3);
                    Cursor cursor = db.query("data2", new String[]{"location"}, "area=?", new String[]{area}, null, null, null);
                    if (cursor.moveToNext()){
                        mAddress = cursor.getString(0);
                    } else {
                        mAddress = "未知号码";
                    }
                    break;
                case 12:
                    //（4+8）区号+座机号码 查询data2
                    String area1 = phone.substring(1, 4);
                    Cursor cursor1 = db.query("data2", new String[]{"location"}, "area=?", new String[]{area1}, null, null, null);
                    if (cursor1.moveToNext()){
                        mAddress = cursor1.getString(0);
                    } else {
                        mAddress = "未知号码";
                    }
                    break;
                default:
                    mAddress = "未知号码";
                    break;
            }
        }
        return mAddress;
    }
}
