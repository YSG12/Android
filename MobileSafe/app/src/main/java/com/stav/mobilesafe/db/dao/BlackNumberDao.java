package com.stav.mobilesafe.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.stav.mobilesafe.db.BlackNumberOpenHelper;
import com.stav.mobilesafe.db.domain.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/3.
 */

public class BlackNumberDao {
    private BlackNumberOpenHelper blackNumberOpenHelper;
    //BlackNumberDao 单例模式
    //1.私有化构造方法
    private BlackNumberDao(Context context){

        //创建数据库以及其表结构
        blackNumberOpenHelper = new BlackNumberOpenHelper(context);
    }
    //2.声明一个当前类的对象
    private static BlackNumberDao blackNumberDao = null;
    //3.提供一个静态方法，如果当前对象为空，创建一个新的
    public static BlackNumberDao getInstance(Context context) {
        if (blackNumberDao == null){
            blackNumberDao = new BlackNumberDao(context);
        }
        return blackNumberDao;
    }

    /**
     * 增加一个条目
     * @param phone 拦截的电话号码
     * @param mode  拦截类型（1：短信 2：电话 3：所有）
     */
    public void insert(String phone,String mode) {
        //开启数据库，准备做写入操作
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        db.insert("blacknumber",null,values);

        db.close();
    }

    /**
     * 从数据库删除一条电话号码
     * @param phone 删除一条电话号码
     */
    public void delete(String phone) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();

        db.delete("blacknumber","phone=?",new String[]{phone});
        db.close();
    }

    /**
     * 修改数据库里面的信息
     * @param phone 更新拦截模式的电话号码
     * @param mode  修改拦截类型
     */
    public void update(String phone,String mode) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mode",mode);
        db.update("blacknumber",contentValues,"phone=?",new String[]{phone});
        db.close();
    }

    /**
     *  查询数据库信息
     * @return 查询到数据库中所有的号码以及拦截类型所在的集合
     */
    public List<BlackNumberInfo> findAll(){
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("blacknumber", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();

        return blackNumberList;
    }

    /**
     * 每次查询10条数据
     * @param index 查询的索引值
     */
    public List<BlackNumberInfo> find(int index) {
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,10;",new String[]{index+""});
        List<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
        while (cursor.moveToNext()){
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.phone = cursor.getString(0);
            blackNumberInfo.mode = cursor.getString(1);
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();

        return blackNumberList;
    }

    /**
     * 数据库总条目数
     * @return
     */
    public int getCount() {
        int count = 0;
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber;",null);
        if (cursor.moveToNext()){
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    /**
     *
     * @param phone 查询的电话号码
     * @return 传入电话号码的拦截模式 1短信 2电话 3所有 0没有
     */
    public int getMode(String phone) {

        int mode = 0;
        SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("blacknumber",new String[]{"mode"},"phone=?",new String[]{phone},null,null,null);
        if (cursor.moveToNext()){
            mode = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return mode;
    }
}
