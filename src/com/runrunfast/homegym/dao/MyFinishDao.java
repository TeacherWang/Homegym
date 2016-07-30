package com.runrunfast.homegym.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.runrunfast.homegym.record.RecordDataUnit;
import com.runrunfast.homegym.utils.Const;

import java.util.ArrayList;

public class MyFinishDao {
	private volatile static MyFinishDao instance;
	private static Object lockObject = new Object();
	
	public static MyFinishDao getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new MyFinishDao();
				}
			}
		}
		return instance;
	}
	
	public static String getFinishTableSql(){
		String sql = "create table if not exists " + Const.TABLE_FINISH
				+ " (" + Const.DB_KEY_ROW + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_UID + " TEXT,"
				+ Const.DB_KEY_DATE + " TEXT,"
				+ Const.DB_KEY_COURSE_ID + " TEXT,"
				+ Const.DB_KEY_COURSE_NAME + " TEXT,"
				+ Const.DB_KEY_ACTION_ID + " TEXT,"
				+ Const.DB_KEY_ACTION_NAME + " TEXT,"
				+ Const.DB_KEY_FINISH_GROUP_NUM + " INTEGER,"
				+ Const.DB_KEY_FINISH_KCAL + " INTEGER,"
				+ Const.DB_KEY_FINISH_TIME + " INTEGER"
				+ ");";
		return sql;
	}
	
	public synchronized void saveFinishInfoToDb( Context context, String uid, RecordDataUnit recordDataDate ){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_UID, uid);
			values.put(Const.DB_KEY_DATE, recordDataDate.strDate);
			values.put(Const.DB_KEY_COURSE_ID, recordDataDate.strCoursId);
			values.put(Const.DB_KEY_COURSE_NAME, recordDataDate.strCourseName);
			values.put(Const.DB_KEY_ACTION_ID, recordDataDate.actionId);
			values.put(Const.DB_KEY_ACTION_NAME, recordDataDate.actionName);
			values.put(Const.DB_KEY_FINISH_GROUP_NUM, recordDataDate.iGroupCount);
			values.put(Const.DB_KEY_FINISH_KCAL, recordDataDate.iTotalKcal);
			values.put(Const.DB_KEY_FINISH_TIME, recordDataDate.iConsumeTime);
			
			c = db.query(Const.TABLE_FINISH, null, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_DATE + " = ? and " + Const.DB_KEY_COURSE_ID + " =? and " + Const.DB_KEY_ACTION_ID + " = ?",
					new String[] { uid, recordDataDate.strDate, recordDataDate.strCoursId, recordDataDate.actionId }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据:把之前的结果跟现在的结果相加合并
				// 
				
				
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
	
	public synchronized RecordDataUnit getFinishInfo( Context context, String uid, String strDate, String courseId, String actionId ){
		RecordDataUnit recordDataUnit = null;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_FINISH, null, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_DATE + " =? and " + Const.DB_KEY_COURSE_ID + " =? and " + Const.DB_KEY_ACTION_ID + " =? ",
					new String[]{ uid, strDate, courseId, actionId }, null, null, null);
			if(null != c && c.getCount() > 0){
				c.moveToNext();
				
				recordDataUnit = new RecordDataUnit();
				recordDataUnit.strDate = c.getString(c.getColumnIndex(Const.DB_KEY_DATE));
				recordDataUnit.strCoursId = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
				recordDataUnit.strCourseName = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
				recordDataUnit.actionId = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
				recordDataUnit.actionName = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_NAME));
				recordDataUnit.iGroupCount = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_GROUP_NUM));
				recordDataUnit.iTotalKcal = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_KCAL));
				recordDataUnit.iConsumeTime = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TIME));
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
		return recordDataUnit;
	}
	
	public synchronized ArrayList<RecordDataUnit> getFinishInfo(Context context, String uid, String strDate){
		ArrayList<RecordDataUnit> recordDataUnitList = new ArrayList<RecordDataUnit>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_FINISH, null, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_DATE + " =?",
					new String[]{ uid, strDate }, null, null, null);
			if(null != c && c.getCount() > 0){
				while (c.moveToNext() ) {
					RecordDataUnit recordDataUnit = new RecordDataUnit();
					recordDataUnit.strDate = c.getString(c.getColumnIndex(Const.DB_KEY_DATE));
					recordDataUnit.strCoursId = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					recordDataUnit.strCourseName = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
					recordDataUnit.actionId = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
					recordDataUnit.actionName = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_NAME));
					recordDataUnit.iGroupCount = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_GROUP_NUM));
					recordDataUnit.iTotalKcal = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_KCAL));
					recordDataUnit.iConsumeTime = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TIME));
					
					recordDataUnitList.add(recordDataUnit);
				}
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
		return recordDataUnitList;
	}
	
}
