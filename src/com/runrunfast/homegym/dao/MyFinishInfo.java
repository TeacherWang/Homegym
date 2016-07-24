package com.runrunfast.homegym.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.record.RecordDataDate;
import com.runrunfast.homegym.record.RecordDataUnit;
import com.runrunfast.homegym.utils.Const;

public class MyFinishInfo {
	private volatile static MyFinishInfo instance;
	private static Object lockObject = new Object();
	
	public static MyFinishInfo getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new MyFinishInfo();
				}
			}
		}
		return instance;
	}
	
	public static String getCourseTableSql(){
		String sql = "create table if not exists " + Const.TABLE_FINISH
				+ " (" + Const.DB_KEY_ROW + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_UID + " TEXT,"
				+ Const.DB_KEY_COURSE_ID + " TEXT,"
				+ Const.DB_KEY_ACTION_ID + " TEXT,"
				+ Const.DB_KEY_DATE + " TEXT,"
				+ Const.DB_KEY_FINISH_GROUP_NUM + " INTEGER,"
				+ Const.DB_KEY_FINISH_KCAL + " INTEGER,"
				+ Const.DB_KEY_FINISH_TIME + " INTEGER"
				+ ");";
		return sql;
	}
	
	public synchronized void saveFinishInfoToDb(Context context, String uid, RecordDataUnit recordDataDate ){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_UID, uid);
			values.put(Const.DB_KEY_COURSE_ID, recordDataDate.strCoursId);
			values.put(Const.DB_KEY_ACTION_ID, recordDataDate.actionId);
			values.put(Const.DB_KEY_DATE, recordDataDate.strDate);
			values.put(Const.DB_KEY_FINISH_GROUP_NUM, recordDataDate.iGroupCount);
			values.put(Const.DB_KEY_FINISH_KCAL, recordDataDate.iTotalKcal);
			values.put(Const.DB_KEY_FINISH_TIME, recordDataDate.iConsumeTime);
			
			c = db.query(Const.TABLE_FINISH, null, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =? and " + Const.DB_KEY_ACTION_ID + " = ?",
					new String[] { uid, recordDataDate.strCoursId, recordDataDate.actionId }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_FINISH, values, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =? and " + Const.DB_KEY_ACTION_ID + " = ?",
						new String[] { uid, recordDataDate.strCoursId, recordDataDate.actionId });
			}else{
				db.insert(Const.TABLE_FINISH, null, values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(c != null){
				c.close();
			}
			if(db != null){
				db.close();
			}
		}
	}
	
}
