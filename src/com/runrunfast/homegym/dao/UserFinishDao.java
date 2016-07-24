package com.runrunfast.homegym.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.utils.Const;

public class UserFinishDao {
	private volatile static UserFinishDao instance;
	private static Object lockObject = new Object();
	
	public static UserFinishDao getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new UserFinishDao();
				}
			}
		}
		return instance;
	}
	
	public static String getUserFinishTableSql(){
		String sql = "create table if not exists " + Const.TABLE_FINISH
				+ " (" + Const.DB_KEY_ROW + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_UID + " TEXT,"
				+ Const.DB_KEY_COURSE_ID + " TEXT,"
				+ Const.DB_KEY_ACTION_ID + " TEXT,"
				+ Const.DB_KEY_DATE + " TEXT,"
				+ Const.DB_KEY_FINISH_GROUP_NUM + " TEXT,"
				+ Const.DB_KEY_FINISH_COUNT + " TEXT,"
				+ Const.DB_KEY_FINISH_KCAL + " TEXT"
				+ ");";
		return sql;
	}
	
	public void saveUserFinishToDb(Context context, UserInfo userInfo){
		Cursor c = null;
		SQLiteDatabase db = null;
//		try {
//			DBOpenHelper dbHelper = new DBOpenHelper(context);
//			db = dbHelper.getWritableDatabase();
//			ContentValues values = new ContentValues();
//			
//			values.put(Const.DB_KEY_UID, userInfo.strAccountId);
//			values.put(Const.DB_KEY_NICK, userInfo.strNickName);
//			values.put(Const.DB_KEY_SEX, userInfo.strSex);
//			values.put(Const.DB_KEY_BIRTH, userInfo.strBirthday);
//			values.put(Const.DB_KEY_WEIGHT, userInfo.strWeight);
//			values.put(Const.DB_KEY_HEIGHT, userInfo.strHeight);
//			values.put(Const.DB_KEY_CITY, userInfo.strCity);
//			
//			c = db.query(Const.TABLE_FINISH, null, Const.DB_KEY_UID + " = ?",
//					new String[] { userInfo.strAccountId }, null, null, null);
//			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
//				db.update(Const.TABLE_FINISH, values, Const.DB_KEY_UID + " = ?",
//						new String[] { userInfo.strAccountId });
//			}else{
//				db.insert(Const.TABLE_FINISH, null, values);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally{
//			if(c != null){
//				c.close();
//			}
//			if(db != null){
//				db.close();
//			}
//		}
	}
	
}
