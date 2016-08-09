package com.runrunfast.homegym.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runrunfast.homegym.bean.Course;
import com.runrunfast.homegym.bean.Course.ActionId;
import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.course.CourseInfo;
import com.runrunfast.homegym.utils.Const;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class CourseDao {
	private volatile static CourseDao instance;
	private static Object lockObject = new Object();
	
	public static CourseDao getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new CourseDao();
				}
			}
		}
		return instance;
	}
	
	public static String getCourseTableSql(){
		String sql = "create table if not exists " + Const.TABLE_COURSE
				+ " (" + Const.DB_KEY_ID + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_COURSE_ID + " TEXT,"
				+ Const.DB_KEY_COURSE_NAME + " TEXT,"
				+ Const.DB_KEY_ACTION_IDS + " TEXT,"
				+ Const.DB_KEY_DATE_NUM + " TEXT,"
				+ Const.DB_KEY_DATE_ACTION_IDS + " TEXT,"
				+ Const.DB_KEY_COURSE_RECOMMEND + " TEXT,"
				+ Const.DB_KEY_COURSE_QUALITY + " TEXT,"
				+ Const.DB_KEY_COURSE_NEW + " TEXT"
				+ ");";
		return sql;
	}
	
	public synchronized void saveCourseInfoToDb(Context context, CourseInfo courseInfo) {
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_COURSE_ID, courseInfo.courseId);
			values.put(Const.DB_KEY_COURSE_NAME, courseInfo.courseName);
			values.put(Const.DB_KEY_ACTION_IDS, courseInfo.actionIds.toString());
			values.put(Const.DB_KEY_DATE_NUM, courseInfo.dateNumList.toString());
			values.put(Const.DB_KEY_DATE_ACTION_IDS, courseInfo.dateActionIdList.toString());
			values.put(Const.DB_KEY_COURSE_RECOMMEND, (courseInfo.isRecommend ? 1 : 0));
			values.put(Const.DB_KEY_COURSE_QUALITY, courseInfo.courseQuality);
			values.put(Const.DB_KEY_COURSE_NEW, (courseInfo.isNew ? 1 : 0));
			
			c = db.query(Const.TABLE_COURSE, null, Const.DB_KEY_COURSE_ID + " = ?",
					new String[] { String.valueOf(courseInfo.courseId) }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_COURSE, values, Const.DB_KEY_COURSE_ID + " = ?",
						new String[] { String.valueOf(courseInfo.courseId) });
			}else{
				db.insert(Const.TABLE_COURSE, null, values);
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
	
	public synchronized ArrayList<CourseInfo> getCourseInfoListFromDb(Context context) {
		ArrayList<CourseInfo> coursetInfoList = new ArrayList<CourseInfo>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_COURSE, null, null, null, null, null, null);
			if(null != c && c.getCount() > 0){
				while (c.moveToNext()) {
					CourseInfo courseInfo = new CourseInfo();
					
					courseInfo.courseId = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					courseInfo.courseName = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
					
					String tempActionIdString = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_IDS));
					String defaultActionIdString = tempActionIdString.substring(1, tempActionIdString.length() - 1).replace(" ", "");
					courseInfo.actionIds = Arrays.asList( defaultActionIdString.split(",") );
					
					String tempDateNumString = c.getString(c.getColumnIndex(Const.DB_KEY_DATE_NUM));
					String defaultDateNumString = tempDateNumString.substring(1, tempDateNumString.length() - 1).replace(" ", "");
					courseInfo.dateNumList = Arrays.asList( defaultDateNumString.split(",") );
					
					String tempDateActionIdString = c.getString(c.getColumnIndex(Const.DB_KEY_DATE_ACTION_IDS));
					String defaultDateActionIdString = tempDateActionIdString.substring(1, tempDateActionIdString.length() - 1).replace(" ", "");
					courseInfo.dateActionIdList = Arrays.asList( defaultDateActionIdString.split(";") );
					
					courseInfo.isRecommend = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_RECOMMEND)) == 1 ? true : false;
					courseInfo.courseQuality = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_QUALITY));
					courseInfo.isNew = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_NEW)) == 1 ? true : false;
					
					coursetInfoList.add(courseInfo);
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
		return coursetInfoList;
	}
	
	public synchronized ArrayList<CourseInfo> getRecommedCourseInfoListFromDb(Context context) {
		ArrayList<CourseInfo> coursetInfoList = new ArrayList<CourseInfo>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_COURSE, null, Const.DB_KEY_COURSE_RECOMMEND + "=?", new String[]{ String.valueOf(1) }, null, null, null);
			if(null != c && c.getCount() > 0){
				while (c.moveToNext()) {
					CourseInfo courseInfo = new CourseInfo();
					
					courseInfo.courseId = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					courseInfo.courseName = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
					
					String tempActionIdString = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_IDS));
					String defaultActionIdString = tempActionIdString.substring(1, tempActionIdString.length() - 1).replace(" ", "");
					courseInfo.actionIds = Arrays.asList( defaultActionIdString.split(",") );
					
					String tempDateNumString = c.getString(c.getColumnIndex(Const.DB_KEY_DATE_NUM));
					String defaultDateNumString = tempDateNumString.substring(1, tempDateNumString.length() - 1).replace(" ", "");
					courseInfo.dateNumList = Arrays.asList( defaultDateNumString.split(",") );
					
					String tempDateActionIdString = c.getString(c.getColumnIndex(Const.DB_KEY_DATE_ACTION_IDS));
					String defaultDateActionIdString = tempDateActionIdString.substring(1, tempDateActionIdString.length() - 1).replace(" ", "");
					courseInfo.dateActionIdList = Arrays.asList( defaultDateActionIdString.split(";") );
					
					courseInfo.isRecommend = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_RECOMMEND)) == 1 ? true : false;
					courseInfo.courseQuality = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_QUALITY));
					courseInfo.isNew = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_NEW)) == 1 ? true : false;
					
					coursetInfoList.add(courseInfo);
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
		return coursetInfoList;
	}
	
	public synchronized CourseInfo getCourseInfoFromDb(Context context, String courseId) {
		CourseInfo courseInfo = null;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_COURSE, null, Const.DB_KEY_COURSE_ID + "=?", new String[]{ courseId }, null, null, null);
			if(null != c && c.getCount() > 0){
				c.moveToNext();
				
				courseInfo = new CourseInfo();
				
				courseInfo.courseId = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
				courseInfo.courseName = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
				
				String tempActionIdString = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_IDS));
				String defaultActionIdString = tempActionIdString.substring(1, tempActionIdString.length() - 1).replace(" ", "");
				courseInfo.actionIds = Arrays.asList( defaultActionIdString.split(",") );
				
				String tempDateNumString = c.getString(c.getColumnIndex(Const.DB_KEY_DATE_NUM));
				String defaultDateNumString = tempDateNumString.substring(1, tempDateNumString.length() - 1).replace(" ", "");
				courseInfo.dateNumList = Arrays.asList( defaultDateNumString.split(",") );
				
				String tempDateActionIdString = c.getString(c.getColumnIndex(Const.DB_KEY_DATE_ACTION_IDS));
				String defaultDateActionIdString = tempDateActionIdString.substring(1, tempDateActionIdString.length() - 1).replace(" ", "");
				courseInfo.dateActionIdList = Arrays.asList( defaultDateActionIdString.split(",") );
				
				courseInfo.isRecommend = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_RECOMMEND)) == 1 ? true : false;
				courseInfo.courseQuality = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_QUALITY));
				courseInfo.isNew = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_NEW)) == 1 ? true : false;
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
	
	// 新的开始
	public String getCourseTableSqlString(){
		String sql = "create table if not exists " + Const.TABLE_COURSE
				+ " (" + Const.DB_KEY_ID + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_COURSE_ID + " TEXT,"
				+ Const.DB_KEY_COURSE_NAME + " TEXT,"
				+ Const.DB_KEY_COURSE_RECOMMEND + " INTEGER,"
				+ Const.DB_KEY_COURSE_QUALITY + " INTEGER,"
				+ Const.DB_KEY_COURSE_NEW + " INTEGER,"
				+ Const.DB_KEY_ACTION_IDS + " TEXT,"
				+ Const.DB_KEY_COURSE_DETAIL + " TEXT"
				+ ");";
		return sql;
	}
	
	public synchronized void saveCourseToDb(Context context, Course course) {
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			Gson gson = new Gson();
			
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_COURSE_ID, course.course_id);
			values.put(Const.DB_KEY_COURSE_NAME, course.course_name);
			values.put(Const.DB_KEY_COURSE_RECOMMEND, course.course_recommend);
			values.put(Const.DB_KEY_COURSE_QUALITY, course.course_quality);
			values.put(Const.DB_KEY_COURSE_NEW, course.course_new);
			
			String actionIdsJson = gson.toJson(course.action_ids);
			values.put(Const.DB_KEY_ACTION_IDS, actionIdsJson);
			
			String jsonCourseDetail = gson.toJson(course.course_detail);
			values.put(Const.DB_KEY_COURSE_DETAIL, jsonCourseDetail);
			
			c = db.query(Const.TABLE_COURSE, null, Const.DB_KEY_COURSE_ID + " = ?",
					new String[] { course.course_id }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_COURSE, values, Const.DB_KEY_COURSE_ID + " = ?",
						new String[] { course.course_id });
			}else{
				db.insert(Const.TABLE_COURSE, null, values);
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

	public synchronized ArrayList<Course> getCourseListFromDb(Context context) {
		ArrayList<Course> coursetList = new ArrayList<Course>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_COURSE, null, null, null, null, null, null);
			if(null != c && c.getCount() > 0){
				Gson gson = new Gson();
				Type typeActionIds = new TypeToken<Collection<ActionId>>(){}.getType();
				Type typeCourseDetail = new TypeToken<Collection<CourseDetail>>(){}.getType();
				while (c.moveToNext()) {
					Course course = new Course();
					
					course.course_id = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					course.course_name = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
					course.course_recommend = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_RECOMMEND));
					course.course_quality = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_QUALITY));
					course.course_new = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_NEW));
					
					String jsonActionIds = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_IDS));
					course.action_ids = gson.fromJson(jsonActionIds, typeActionIds);
					
					String jsonCourseDetail = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_DETAIL));
					course.course_detail = gson.fromJson(jsonCourseDetail, typeCourseDetail);
							
					coursetList.add(course);
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
		return coursetList;
	}
	
	public synchronized ArrayList<Course> getRecommedCourseListFromDb(Context context) {
		ArrayList<Course> coursetList = new ArrayList<Course>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_COURSE, null, Const.DB_KEY_COURSE_RECOMMEND + "=?", new String[]{ String.valueOf(Course.RECOMMED_COURSE) }, null, null, null);
			if(null != c && c.getCount() > 0){
				Gson gson = new Gson();
				Type typeActionIds = new TypeToken<Collection<ActionId>>(){}.getType();
				Type typeCourseDetail = new TypeToken<Collection<CourseDetail>>(){}.getType();
				while (c.moveToNext()) {
					Course course = new Course();
					
					course.course_id = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					course.course_name = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
					course.course_recommend = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_RECOMMEND));
					course.course_quality = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_QUALITY));
					course.course_new = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_NEW));
					
					String jsonActionIds = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_IDS));
					course.action_ids = gson.fromJson(jsonActionIds, typeActionIds);
					
					String jsonCourseDetail = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_DETAIL));
					course.course_detail = gson.fromJson(jsonCourseDetail, typeCourseDetail);
							
					coursetList.add(course);
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
		return coursetList;
	}
	
	public synchronized Course getCourseFromDb(Context context, String courseId) {
		Course course = null;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_COURSE, null, Const.DB_KEY_COURSE_ID + "=?", new String[]{ courseId }, null, null, null);
			if(null != c && c.getCount() > 0){
				Gson gson = new Gson();
				Type typeActionIds = new TypeToken<Collection<ActionId>>(){}.getType();
				Type typeCourseDetail = new TypeToken<Collection<CourseDetail>>(){}.getType();
				
				c.moveToNext();
				
				course = new Course();
				
				course.course_id = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
				course.course_name = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
				course.course_recommend = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_RECOMMEND));
				course.course_quality = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_QUALITY));
				course.course_new = c.getInt(c.getColumnIndex(Const.DB_KEY_COURSE_NEW));
				
				String jsonActionIds = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_IDS));
				course.action_ids = gson.fromJson(jsonActionIds, typeActionIds);
				
				String jsonCourseDetail = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_DETAIL));
				course.course_detail = gson.fromJson(jsonCourseDetail, typeCourseDetail);
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
		return course;
	}
	
}
