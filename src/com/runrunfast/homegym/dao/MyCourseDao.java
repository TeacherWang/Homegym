package com.runrunfast.homegym.dao;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.course.CourseInfo;
import com.runrunfast.homegym.utils.Const;

public class MyCourseDao {
	private volatile static MyCourseDao instance;
	private static Object lockObject = new Object();
	
	public static MyCourseDao getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new MyCourseDao();
				}
			}
		}
		return instance;
	}
	
	public static String getCourseTableSql(){
		String sql = "create table if not exists " + Const.TABLE_MY_COURSE
				+ " (" + Const.DB_KEY_ROW + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_UID + " TEXT,"
				+ Const.DB_KEY_COURSE_ID + " TEXT,"
				+ Const.DB_KEY_START_DATE + " TEXT"
				+ ");";
		return sql;
	}
	
	public synchronized void saveMyCourseInfo(Context context, String uid, CourseInfo courseInfo){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_UID, uid);
			values.put(Const.DB_KEY_COURSE_ID, courseInfo.courseId);
			values.put(Const.DB_KEY_START_DATE, courseInfo.startDate);
			
			c = db.query(Const.TABLE_MY_COURSE, null, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =?",
					new String[] { uid, courseInfo.courseId }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_MY_COURSE, values, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =?",
						new String[] { AccountMgr.getInstance().mUserInfo.strAccountId, courseInfo.courseId });
			}else{
				db.insert(Const.TABLE_MY_COURSE, null, values);
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
	
	public ArrayList<CourseInfo> getMyCourseInfoList(Context context){
		ArrayList<CourseInfo> courseInfoList = new ArrayList<CourseInfo>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_COURSE, null, null, null, null, null, null);
			if(null != c && c.getCount() > 0){
				while (c.moveToNext()) {
					String courseId = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					
					CourseInfo courseInfo = CourseDao.getInstance().getCourseInfoFromDb(context, courseId);
					
					if(courseInfo != null){
						courseInfo.startDate = c.getString(c.getColumnIndex(Const.DB_KEY_START_DATE));
						courseInfoList.add(courseInfo);
					}
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
		return courseInfoList;
	}
}
