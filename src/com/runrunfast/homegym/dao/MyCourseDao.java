package com.runrunfast.homegym.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runrunfast.homegym.bean.Course;
import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.bean.MyCourse.DayProgress;
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
				+ Const.DB_KEY_COURSE_IMG_URL + " TEXT,"
				+ Const.DB_KEY_COURSE_IMG_LOCAL + " TEXT,"
				+ Const.DB_KEY_START_DATE + " TEXT,"
				+ Const.DB_KEY_COURSE_PERIOD + " INTEGER,"
				+ Const.DB_KEY_PROGRESS + " INTEGER,"
				+ Const.DB_KEY_DAY_PROGRESS + " TEXT"
				+ ");";
		return sql;
	}
	
	/**
	  * @Method: saveMyCourseToDb
	  * @Description: 保存我的课程，除了图片本地路径course_img_local
	  * @param context
	  * @param uid
	  * @param myCourse	
	  * 返回类型：void 
	  */
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
			values.put(Const.DB_KEY_COURSE_IMG_URL, myCourse.course_img_url);
			values.put(Const.DB_KEY_COURSE_PERIOD, myCourse.course_period);
			
			Gson gson = new Gson();
			
			String jsonCourseDetail = gson.toJson(myCourse.course_detail);
			values.put(Const.DB_KEY_COURSE_DETAIL, jsonCourseDetail);
			
			String dayProgressJson = gson.toJson(myCourse.day_progress);
			values.put(Const.DB_KEY_DAY_PROGRESS, dayProgressJson);
			
			c = db.query(Const.TABLE_MY_COURSE, null, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =? and " + Const.DB_KEY_START_DATE + " =?" ,
					new String[] { uid, myCourse.course_id, myCourse.start_date }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_MY_COURSE, values, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =? and " + Const.DB_KEY_START_DATE + " =?",
						new String[] { uid, myCourse.course_id, myCourse.start_date });
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
	
	public synchronized void saveMyCourseImgLocalToDb(Context context, String uid, String courseId, String imgLocal){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_UID, uid);
			values.put(Const.DB_KEY_COURSE_ID, courseId);
			values.put(Const.DB_KEY_COURSE_IMG_LOCAL, imgLocal);
			
			c = db.query(Const.TABLE_MY_COURSE, null, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =?",
					new String[] { uid, courseId }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_MY_COURSE, values, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =?",
						new String[] { uid, courseId });
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
	
	/**
	  * @Method: saveMyCourseDayProgress
	  * @Description: 保存每天的进度
	  * @param context
	  * @param uid
	  * @param myCourse	
	  * 返回类型：void 
	  */
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
	
	public synchronized void saveMyCourseProgress(Context context, String uid, String courseId, int progress){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_PROGRESS, progress);
			
			c = db.query(Const.TABLE_MY_COURSE, null, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =?",
					new String[] { uid, courseId }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_MY_COURSE, values, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =?",
						new String[] { uid, courseId });
			}else{
				Log.e(TAG, "saveMyCourseDayProgress, not find my course! courseId = " + courseId);
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
	
	public synchronized ArrayList<Course> getMyCourseListFromDb(Context context, String uid){
		ArrayList<Course> myCourseList = new ArrayList<Course>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(true, Const.TABLE_MY_COURSE, null, Const.DB_KEY_UID + " = ? ", new String[]{ uid }, Const.DB_KEY_COURSE_ID, null, Const.DB_KEY_START_DATE + " DESC", null);
			
//			c = db.query(Const.TABLE_MY_COURSE, null, null, null, null, null, null);
			if(null != c && c.getCount() > 0){
				Gson gson = new Gson();
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
					myCourse.course_img_url = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_IMG_URL));
					myCourse.course_img_local = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_IMG_LOCAL));
					myCourse.course_period = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_PERIOD));
					
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
	
	public synchronized MyCourse getMyCourseFromDb(Context context, String uid, String courseId){
		MyCourse myCourse = null;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_COURSE, null, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_COURSE_ID + " =?", new String[]{ uid, courseId }, null, null, null);
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
				myCourse.course_img_url = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_IMG_URL));
				myCourse.course_img_local = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_IMG_LOCAL));
				myCourse.course_period = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_PERIOD));
				
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
	
	public synchronized void deleteMyCourseFromDb(Context context, String uid, String courseId, String startDate ){
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_COURSE, null, Const.DB_KEY_UID + "=? and " + Const.DB_KEY_COURSE_ID + "=? and " + Const.DB_KEY_START_DATE + "=?", new String[]{ uid, courseId, startDate }, null, null, null);
			if(null != c && c.getCount() > 0){
				db.delete(Const.TABLE_MY_COURSE, Const.DB_KEY_UID + "=? and " + Const.DB_KEY_COURSE_ID + "=? and " + Const.DB_KEY_START_DATE + "=?", new String[]{ uid, courseId, startDate });
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
