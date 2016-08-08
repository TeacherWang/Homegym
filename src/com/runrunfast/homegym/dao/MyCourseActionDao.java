package com.runrunfast.homegym.dao;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.course.ActionInfo;
import com.runrunfast.homegym.utils.Const;

public class MyCourseActionDao {
	private volatile static MyCourseActionDao instance;
	private static Object lockObject = new Object();
	
	public static MyCourseActionDao getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new MyCourseActionDao();
				}
			}
		}
		return instance;
	}
	
	public static String getMyCourseTableSql(){
		String sql = "create table if not exists " + Const.TABLE_MY_COURSE_ACTION
				+ " (" + Const.DB_KEY_ID + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_UID + " TEXT,"
				+ Const.DB_KEY_COURSE_ID + " TEXT,"
				+ Const.DB_KEY_ACTION_ID + " TEXT,"
				+ Const.DB_KEY_DEFAULT_GROUP_NUM + " INTEGER,"
				+ Const.DB_KEY_DEFAULT_COUNT + " TEXT,"
				+ Const.DB_KEY_DEFAULT_TOOL_WEIGHT + " TEXT,"
				+ Const.DB_KEY_DEFAULT_BURNING + " TEXT,"
				+ Const.DB_KEY_ACTION_DEFAULT_TOTAL_KCAL + " INTEGER,"
				+ Const.DB_KEY_DEFAULT_TIME + " TEXT"
				+ ");";
		return sql;
	}
	
	public synchronized void saveMyCourseActionInfo(Context context, String courseId, ActionInfo actionInfo){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_UID, AccountMgr.getInstance().mUserInfo.strAccountId);
			values.put(Const.DB_KEY_COURSE_ID, courseId);
			values.put(Const.DB_KEY_ACTION_ID, actionInfo.strActionId);
			values.put(Const.DB_KEY_DEFAULT_TIME, actionInfo.iTime);
			values.put(Const.DB_KEY_DEFAULT_GROUP_NUM, actionInfo.defaultGroupNum);
			values.put(Const.DB_KEY_DEFAULT_COUNT, actionInfo.defaultCountList.toString());
			values.put(Const.DB_KEY_DEFAULT_TOOL_WEIGHT, actionInfo.defaultToolWeightList.toString());
			values.put(Const.DB_KEY_DEFAULT_BURNING, actionInfo.defaultBurningList.toString());
			values.put(Const.DB_KEY_ACTION_DEFAULT_TOTAL_KCAL, actionInfo.iDefaultTotalKcal);
			
			c = db.query(Const.TABLE_MY_COURSE_ACTION, null, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " = ? and " + Const.DB_KEY_ACTION_ID + " = ?",
					new String[] { AccountMgr.getInstance().mUserInfo.strAccountId, courseId, actionInfo.strActionId }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_MY_COURSE_ACTION, values, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =? and " + Const.DB_KEY_ACTION_ID + " = ?",
						new String[] { AccountMgr.getInstance().mUserInfo.strAccountId, courseId, actionInfo.strActionId });
			}else{
				db.insert(Const.TABLE_MY_COURSE_ACTION, null, values);
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
	
	public synchronized ActionInfo getMyCourseActionInfo(Context context, String uid, String courseId, String actionId){
		ActionInfo actionInfo = null;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_COURSE_ACTION, null, 
					Const.DB_KEY_UID + " =? and " + Const.DB_KEY_COURSE_ID + "=? and " + Const.DB_KEY_ACTION_ID + "=?",
					new String[]{ uid, courseId, actionId }, null, null, null);
			if(null != c && c.getCount() > 0){
				c.moveToNext();
				
				actionInfo = new ActionInfo();
				
				actionInfo.strCourseId = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
				actionInfo.strActionId = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
				actionInfo.defaultGroupNum = c.getInt(c.getColumnIndex(Const.DB_KEY_DEFAULT_GROUP_NUM));
				
				String tempCountString = c.getString(c.getColumnIndex(Const.DB_KEY_DEFAULT_COUNT));
				String countString = tempCountString.substring(1, tempCountString.length() - 1).replace(" ", "");
				actionInfo.defaultCountList = Arrays.asList(countString.split(","));
				
				String tempToolWeightString = c.getString(c.getColumnIndex(Const.DB_KEY_DEFAULT_TOOL_WEIGHT));
				String toolWeightString = tempToolWeightString.substring(1, tempToolWeightString.length() - 1).replace(" ", "");
				actionInfo.defaultToolWeightList = Arrays.asList(toolWeightString.split(","));
				
				String tempBurningString = c.getString(c.getColumnIndex(Const.DB_KEY_DEFAULT_BURNING));
				String burningString = tempBurningString.substring(1, tempBurningString.length() - 1).replace(" ", "");
				actionInfo.defaultBurningList = Arrays.asList(burningString.split(","));
				
				actionInfo.iTime = c.getInt(c.getColumnIndex(Const.DB_KEY_DEFAULT_TIME));
				actionInfo.iDefaultTotalKcal = c.getInt(c.getColumnIndex(Const.DB_KEY_ACTION_DEFAULT_TOTAL_KCAL));
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
		return actionInfo;
	}
	
	public synchronized void deleteMyCourseAction(Context context, String uid, String courseId){
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_COURSE_ACTION, null, Const.DB_KEY_UID + "=? and " + Const.DB_KEY_COURSE_ID + "=?", new String[]{ uid, courseId }, null, null, null);
			if(null != c && c.getCount() > 0){
				db.delete(Const.TABLE_MY_COURSE_ACTION, Const.DB_KEY_UID + "=? and " + Const.DB_KEY_COURSE_ID + "=?", new String[]{ uid, courseId });
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
