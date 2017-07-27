package com.stav.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/29.
 */

public class VirusDao {
    //1.指定访问数据可的路径
    public static String path = "data/data/com.stav.mobilesafe/files/antivirus.db";

    //2.开启数据库，查询数据库中表对应的md5码
    public static List<String> getVirusList() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
        List<String> virusList = new ArrayList<>();
        while (cursor.moveToNext()) {
            virusList.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return virusList;
    }

}
