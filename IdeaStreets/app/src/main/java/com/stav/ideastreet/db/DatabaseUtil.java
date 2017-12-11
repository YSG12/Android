package com.stav.ideastreet.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.stav.ideastreet.base.BaseApplication;
import com.stav.ideastreet.bean.MyUser;
import com.stav.ideastreet.db.DBHelper.FavTable;
import com.stav.ideastreet.bean.Post;
import com.stav.ideastreet.util.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.BmobUser;


public class DatabaseUtil {
	private static final String TAG="DatabaseUtil";

	private static DatabaseUtil instance;

	/** 数据库帮助类 **/
	private DBHelper dbHelper;

	public synchronized static DatabaseUtil getInstance(Context context) {
		if(instance == null) {
			instance=new DatabaseUtil(context);
		}
		return instance;
	}

	/**
	 * 初始化
	 * @param context
	 */
	private DatabaseUtil(Context context) {
		dbHelper=new DBHelper(context);
	}

	/**
	 * 销毁
	 */
	public static void destory() {
		if(instance != null) {
			instance.onDestory();
		}
	}

	/**
	 * 销毁
	 */
	public void onDestory() {
		instance=null;
		if(dbHelper != null) {
			dbHelper.close();
			dbHelper=null;
		}
	}


	public void deleteFav(Post qy){
		Cursor cursor=null;
		String where = FavTable.USER_ID+" = '"+BmobUser.getCurrentUser().getObjectId()
				+"' AND "+FavTable.OBJECT_ID+" = '"+qy.getObjectId()+"'";
		cursor=dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
		if(cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			int isLove = cursor.getInt(cursor.getColumnIndex(FavTable.IS_LOVE));
			if(isLove==0){
				dbHelper.delete(DBHelper.TABLE_NAME, where, null);
			}else{
				ContentValues cv = new ContentValues();
				cv.put(FavTable.IS_FAV, 0);
				dbHelper.update(DBHelper.TABLE_NAME, cv, where, null);
			}
		}
		if(cursor != null) {
			cursor.close();
			dbHelper.close();
		}
	}


	public boolean isLoved(Post qy){
		Cursor cursor = null;
		String where = FavTable.USER_ID+" = '"+BmobUser.getCurrentUser().getObjectId()
				+"' AND "+FavTable.OBJECT_ID+" = '"+qy.getObjectId()+"'";
		cursor=dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
		if(cursor!=null && cursor.getCount()>0){
			cursor.moveToFirst();
			if(cursor.getInt(cursor.getColumnIndex(FavTable.IS_LOVE))==1){
				return true;
			}
		}
		return false;
	}

	public boolean isFav(Post qy){
		Cursor cursor = null;
		String where = FavTable.USER_ID+" = '"+BmobUser.getCurrentUser().getObjectId()
				+"' AND "+FavTable.OBJECT_ID+" = '"+qy.getObjectId()+"'";
		cursor=dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
		if(cursor!=null && cursor.getCount()>0){
			cursor.moveToFirst();
			if(cursor.getInt(cursor.getColumnIndex(FavTable.IS_FAV))==1){
				return true;
			}
		}
		return false;
	}

	public long insertFav(Post qy){
		long uri = 0;
		Cursor cursor=null;
		String where = FavTable.USER_ID+" = '"+BmobUser.getCurrentUser().getObjectId()
				+"' AND "+FavTable.OBJECT_ID+" = '"+qy.getObjectId()+"'";
		cursor=dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
		if(cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			ContentValues conv = new ContentValues();
			conv.put(FavTable.IS_FAV, 1);
			conv.put(FavTable.IS_LOVE, 1);
			dbHelper.update(DBHelper.TABLE_NAME, conv, where, null);
		}else{
			ContentValues cv = new ContentValues();
			cv.put(FavTable.USER_ID, BmobUser.getCurrentUser().getObjectId());
			cv.put(FavTable.OBJECT_ID, qy.getObjectId());
			cv.put(FavTable.IS_LOVE, qy.isMyLove()==true?1:0);
			cv.put(FavTable.IS_FAV,qy.isMyFav()==true?1:0);
			uri = dbHelper.insert(DBHelper.TABLE_NAME, null, cv);
		}
		if(cursor != null) {
			cursor.close();
			dbHelper.close();
		}
		return uri;
	}

//	    public int deleteFav(QiangYu qy){
//	    	int row = 0;
//	    	String where = FavTable.USER_ID+" = "+qy.getAuthor().getObjectId()
//	    			+" AND "+FavTable.OBJECT_ID+" = "+qy.getObjectId();
//	    	row = dbHelper.delete(DBHelper.TABLE_NAME, where, null);
//	    	return row;
//	    }


	/**
	 * 设置内容的收藏状态
	 * @param
	 * @param lists
	 */
	public List<Post> setFav(List<Post> lists) {
		Cursor cursor=null;
		if(lists != null && lists.size() > 0) {
			for(Iterator iterator=lists.iterator(); iterator.hasNext();) {
				Post content=(Post)iterator.next();
				String where = FavTable.USER_ID+" = '"+BaseApplication.getInstance().getCurrentUser().getObjectId()//content.getAuthor().getObjectId()
						+"' AND "+FavTable.OBJECT_ID+" = '"+content.getObjectId()+"'";
				cursor=dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
				if(cursor != null && cursor.getCount() > 0) {
					cursor.moveToFirst();
					if(cursor.getInt(cursor.getColumnIndex(FavTable.IS_FAV))==1){
						content.setMyFav(true);
					}else{
						content.setMyFav(false);
					}
					if(cursor.getInt(cursor.getColumnIndex(FavTable.IS_LOVE))==1){
						content.setMyLove(true);
					}else{
						content.setMyLove(false);
					}
				}
				Log.i(TAG,content.isMyFav()+".."+content.isMyLove());
			}
		}
		if(cursor != null) {
			cursor.close();
			dbHelper.close();
		}
		return lists;
	}

	/**
	 * 设置内容的收藏状态
	 * @param
	 * @param lists
	 */
	public List<Post> setFavInFav(List<Post> lists) {
		Cursor cursor=null;
		if(lists != null && lists.size() > 0) {
			for(Iterator iterator=lists.iterator(); iterator.hasNext();) {
				Post content=(Post)iterator.next();
				content.setMyFav(true);
				String where = FavTable.USER_ID+" = '"+BaseApplication.getInstance().getCurrentUser().getObjectId()
						+"' AND "+FavTable.OBJECT_ID+" = '"+content.getObjectId()+"'";
				cursor=dbHelper.query(DBHelper.TABLE_NAME, null, where, null, null, null, null);
				if(cursor != null && cursor.getCount() > 0) {
					cursor.moveToFirst();
					if(cursor.getInt(cursor.getColumnIndex(FavTable.IS_LOVE))==1){
						content.setMyLove(true);
					}else{
						content.setMyLove(false);
					}
				}
				Log.i(TAG,content.isMyFav()+".."+content.isMyLove());
			}
		}
		if(cursor != null) {
			cursor.close();
			dbHelper.close();
		}
		return lists;
	}


	public ArrayList<Post> queryFav() {
		ArrayList<Post> contents=null;
		// ContentResolver resolver = context.getContentResolver();
		Cursor cursor=dbHelper.query(DBHelper.TABLE_NAME, null, null, null, null, null, null);
		Log.i(TAG, cursor.getCount() + "");
		if(cursor == null) {
			return null;
		}
		contents=new ArrayList<Post>();
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Post content=new Post();
			content.setMyFav(cursor.getInt(3)==1?true:false);
			content.setMyLove(cursor.getInt(4)==1?true:false);
			Log.i(TAG,cursor.getColumnIndex("isfav")+".."+cursor.getColumnIndex("islove")+".."+content.isMyFav()+"..."+content.isMyLove());
			contents.add(content);
		}
		if(cursor != null) {
			cursor.close();
		}
		// if (contents.size() > 0) {
		// return contents;
		// }
		return contents;
	}

}
