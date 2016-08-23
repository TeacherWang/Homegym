package com.runrunfast.homegym.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.runrunfast.homegym.record.TotalRecord;
import com.runrunfast.homegym.utils.Const;

public class MyTotalRecordDao {
	
	private volatile static MyTotalRecordDao instance;
	private static Object lockObject = new Object();
	
	public static MyTotalRecordDao getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new MyTotalRecordDao();
				}
			}
		}
		return instance;
	}
	
	public String getMyTotalRecordSqlStr(){
		String sql = "create table if not exists " + Const.TABLE_MY_TOTAL_RECORD
				+ " (" + Const.DB_KEY_ID + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_UID + " TEXT,"
				+ Const.DB_KEY_TOTAL_KCAL + " INTEGER,"
				+ Const.DB_KEY_TOTAL_TIME + " INTEGER,"
				+ Const.DB_KEY_TOTAL_DAYS + " INTEGER,"
				+ Const.DB_KEY_TOTAL_FOOD + " INTEGER"
				+ ");";
		return sql;
	}

	public synchronized void saveMyTotalRecordToDb(Context context, TotalRecord totalRecord){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_UID, totalRecord.uid);
			values.put(Const.DB_KEY_TOTAL_KCAL, totalRecord.total_kcal);
			values.put(Const.DB_KEY_TOTAL_TIME, totalRecord.total_time);
			values.put(Const.DB_KEY_TOTAL_DAYS, totalRecord.total_days);
			values.put(Const.DB_KEY_TOTAL_FOOD, totalRecord.total_food);
			
			c = db.query(Const.TABLE_MY_TOTAL_RECORD, null, Const.DB_KEY_UID + " = ? ",
					new String[] { totalRecord.uid }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，删除
				db.delete(Const.TABLE_MY_TOTAL_RECORD, Const.DB_KEY_UID + " = ? ", new String[] { totalRecord.uid });
			}
			// 再插入新的
			db.insert(Const.TABLE_MY_TOTAL_RECORD, null, values);
			
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
	
	public synchronized TotalRecord getMyTotalRecordFromDb(Context context, String uid){
		TotalRecord totalRecord = new TotalRecord();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_TOTAL_RECORD, null, Const.DB_KEY_UID + "=?", new String[]{ uid }, null, null, null);
			if(null != c && c.getCount() > 0){
				c.moveToNext();
				
				totalRecord.uid = uid;
				totalRecord.total_kcal = c.getInt(c.getColumnIndex(Const.DB_KEY_TOTAL_KCAL));
				totalRecord.total_time = c.getInt(c.getColumnIndex(Const.DB_KEY_TOTAL_TIME));
				totalRecord.total_days = c.getInt(c.getColumnIndex(Const.DB_KEY_TOTAL_DAYS));
				totalRecord.total_food = c.getInt(c.getColumnIndex(Const.DB_KEY_TOTAL_FOOD));
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
		return totalRecord;
	}
	
}
