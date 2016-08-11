package com.runrunfast.homegym.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.bean.Course;
import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.bean.MyCourse.DayProgress;
import com.runrunfast.homegym.course.CourseInfo;
import com.runrunfast.homegym.utils.Const;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class MyCourseDao {
	private final String TAG = "MyCourseDao";
	
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
				+ " (" + Const.DB_KEY_ID + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_UID + " TEXT,"
				+ Const.DB_KEY_COURSE_ID + " TEXT,"
				+ Const.DB_KEY_START_DATE + " TEXT,"
				+ Const.DB_KEY_PROGRESS + " INTEGER,"
				+ ");";
		return sql;
	}
	
	public synchronized void saveMyCourseInfo(Context context, String uid, CourseInfo courseInfo){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			Gson gson = new Gson();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_UID, uid);
			values.put(Const.DB_KEY_COURSE_ID, courseInfo.courseId);
			values.put(Const.DB_KEY_START_DATE, courseInfo.startDate);
			values.put(Const.DB_KEY_PROGRESS, courseInfo.courseProgress);
//			values.put(Const.DB_KEY_DAY_PROGRESS, )
			
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
	
	public synchronized ArrayList<CourseInfo> getMyCourseInfoList(Context context){
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
						courseInfo.isMyCourse = true;
						courseInfo.startDate = c.getString(c.getColumnIndex(Const.DB_KEY_START_DATE));
						courseInfo.courseProgress = c.getInt(c.getColumnIndex(Const.DB_KEY_PROGRESS));
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
	
	public synchronized CourseInfo getMyCourseInfo(Context context, String courseId){
		CourseInfo courseInfo = null;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_COURSE, null, Const.DB_KEY_COURSE_ID + "=?", new String[]{ courseId }, null, null, null);
			if(null != c && c.getCount() > 0){
				c.moveToNext();
				
				courseInfo = CourseDao.getInstance().getCourseInfoFromDb(context, courseId);
				
				if(courseInfo != null){
					courseInfo.startDate = c.getString(c.getColumnIndex(Const.DB_KEY_START_DATE));
					courseInfo.courseProgress = c.getInt(c.getColumnIndex(Const.DB_KEY_PROGRESS));
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
		return courseInfo;
	}
	
	public synchronized void deleteMyCourse(Context context, String uid, String courseId ){
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_COURSE, null, Const.DB_KEY_UID + "=? and " + Const.DB_KEY_COURSE_ID + "=?", new String[]{ uid, courseId }, null, null, null);
			if(null != c && c.getCount() > 0){
				db.delete(Const.TABLE_MY_COURSE, Const.DB_KEY_UID + "=? and " + Const.DB_KEY_COURSE_ID + "=?", new String[]{ uid, courseId });
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
	
	// 
	public String getCourseTableSqlStr(){
		String sql = "create table if not exists " + Const.TABLE_MY_COURSE
				+ " (" + Const.DB_KEY_ID + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_UID + " TEXT,"
				+ Const.DB_KEY_COURSE_ID + " TEXT,"
				+ Const.DB_KEY_COURSE_NAME + " TEXT,"
				+ Const.DB_KEY_COURSE_RECOMMEND + " INTEGER,"
				+ Const.DB_KEY_COURSE_QUALITY + " INTEGER,"
				+ Const.DB_KEY_COURSE_NEW + " INTEGER,"
				+ Const.DB_KEY_COURSE_DETAIL + " TEXT,"
				+ Const.DB_KEY_START_DATE + " TEXT,"
				+ Const.DB_KEY_PROGRESS + " INTEGER,"
				+ Const.DB_KEY_DAY_PROGRESS + " TEXT"
				+ ");";
		return sql;
	}
	
	public synchronized void saveMyCourseToDb(Context context, String uid, MyCourse myCourse){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_UID, uid);
			values.put(Const.DB_KEY_COURSE_ID, myCourse.course_id);
			values.put(Const.DB_KEY_START_DATE, myCourse.start_date);
			values.put(Const.DB_KEY_PROGRESS, myCourse.progress);
			
			values.put(Const.DB_KEY_COURSE_NAME, myCourse.course_name);
			values.put(Const.DB_KEY_COURSE_RECOMMEND, myCourse.course_recommend);
			values.put(Const.DB_KEY_COURSE_QUALITY, myCourse.course_quality);
			values.put(Const.DB_KEY_COURSE_NEW, myCourse.course_new);
			
			Gson gson = new Gson();
			
//			String actionIdsJson = gson.toJson(myCourse.action_ids);
//			values.put(Const.DB_KEY_ACTION_IDS, actionIdsJson);
			
			String jsonCourseDetail = gson.toJson(myCourse.course_detail);
			values.put(Const.DB_KEY_COURSE_DETAIL, jsonCourseDetail);
			
			String dayProgressJson = gson.toJson(myCourse.day_progress);
			values.put(Const.DB_KEY_DAY_PROGRESS, dayProgressJson);
			
			c = db.query(Const.TABLE_MY_COURSE, null, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =?",
					new String[] { uid, myCourse.course_id }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_MY_COURSE, values, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =?",
						new String[] { uid, myCourse.course_id });
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
	
	public synchronized void saveMyCourseDayProgress(Context context, String uid, MyCourse myCourse){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			Gson gson = new Gson();
			
			String dayProgressJson = gson.toJson(myCourse.day_progress);
			values.put(Const.DB_KEY_DAY_PROGRESS, dayProgressJson);
			
			c = db.query(Const.TABLE_MY_COURSE, null, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =?",
					new String[] { uid, myCourse.course_id }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_MY_COURSE, values, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =?",
						new String[] { uid, myCourse.course_id });
			}else{
				Log.e(TAG, "saveMyCourseDayProgress, not find my course! courseId = " + myCourse.course_id);
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
	
	public synchronized ArrayList<Course> getMyCourseListFromDb(Context context){
		ArrayList<Course> myCourseList = new ArrayList<Course>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_COURSE, null, null, null, null, null, null);
			if(null != c && c.getCount() > 0){
				Gson gson = new Gson();
//				Type typeActionIds = new TypeToken<Collection<ActionId>>(){}.getType();
				Type typeCourseDetail = new TypeToken<Collection<CourseDetail>>(){}.getType();
				Type typeDayProgress = new TypeToken<Collection<DayProgress>>(){}.getType();
				while (c.moveToNext()) {
					MyCourse myCourse = new MyCourse();
					
					myCourse.uid = c.getString(c.getColumnIndex(Const.DB_KEY_UID));
					myCourse.course_id = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					myCourse.course_name = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
					myCourse.course_recommend = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_RECOMMEND));
					myCourse.course_quality = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_QUALITY));
					myCourse.course_new = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_NEW));
					myCourse.start_date = c.getString(c.getColumnIndex(Const.DB_KEY_START_DATE));
					myCourse.progress = c.getInt(c.getColumnIndex(Const.DB_KEY_PROGRESS));
					
//					String jsonActionIds = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_IDS));
//					myCourse.action_ids = gson.fromJson(jsonActionIds, typeActionIds);
					
					String jsonCourseDetail = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_DETAIL));
					myCourse.course_detail = gson.fromJson(jsonCourseDetail, typeCourseDetail);
					
					String jsonDayProgressString = c.getString(c.getColumnIndex(Const.DB_KEY_DAY_PROGRESS));
					myCourse.day_progress = gson.fromJson(jsonDayProgressString, typeDayProgress);
					
					myCourseList.add(myCourse);
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
		return myCourseList;
	}
	
	public synchronized MyCourse getMyCourseFromDb(Context context, String courseId){
		MyCourse myCourse = null;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_COURSE, null, Const.DB_KEY_COURSE_ID + "=?", new String[]{ courseId }, null, null, null);
			if(null != c && c.getCount() > 0){
				Gson gson = new Gson();
				Type typeCourseDetail = new TypeToken<Collection<CourseDetail>>(){}.getType();
				Type typeDayProgress = new TypeToken<Collection<DayProgress>>(){}.getType();
				
				c.moveToNext();
				myCourse = new MyCourse();
				
				myCourse.uid = c.getString(c.getColumnIndex(Const.DB_KEY_UID));
				myCourse.course_id = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
				myCourse.course_name = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
				myCourse.course_recommend = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_RECOMMEND));
				myCourse.course_quality = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_QUALITY));
				myCourse.course_new = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_NEW));
				myCourse.start_date = c.getString(c.getColumnIndex(Const.DB_KEY_START_DATE));
				myCourse.progress = c.getInt(c.getColumnIndex(Const.DB_KEY_PROGRESS));
				
				String jsonCourseDetail = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_DETAIL));
				myCourse.course_detail = gson.fromJson(jsonCourseDetail, typeCourseDetail);
				
				String jsonDayProgressString = c.getString(c.getColumnIndex(Const.DB_KEY_DAY_PROGRESS));
				myCourse.day_progress = gson.fromJson(jsonDayProgressString, typeDayProgress);
				
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
		return myCourse;
	}
	
	public synchronized void deleteMyCourseFromDb(Context context, String uid, String courseId ){
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_COURSE, null, Const.DB_KEY_UID + "=? and " + Const.DB_KEY_COURSE_ID + "=?", new String[]{ uid, courseId }, null, null, null);
			if(null != c && c.getCount() > 0){
				db.delete(Const.TABLE_MY_COURSE, Const.DB_KEY_UID + "=? and " + Const.DB_KEY_COURSE_ID + "=?", new String[]{ uid, courseId });
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
