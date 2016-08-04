package com.runrunfast.homegym.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.runrunfast.homegym.record.BaseRecordData;
import com.runrunfast.homegym.record.RecordDataUnit;
import com.runrunfast.homegym.utils.Const;

import java.util.ArrayList;
import java.util.Arrays;

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
				+ Const.DB_KEY_PLAN_DATE + " TEXT,"
				+ Const.DB_KEY_COURSE_ID + " TEXT,"
				+ Const.DB_KEY_COURSE_NAME + " TEXT,"
				+ Const.DB_KEY_ACTION_ID + " TEXT,"
				+ Const.DB_KEY_ACTION_NAME + " TEXT,"
				+ Const.DB_KEY_FINISH_GROUP_NUM + " INTEGER,"
				+ Const.DB_KEY_FINISH_TOTAL_COUNT + " INTEGER,"
				+ Const.DB_KEY_FINISH_TOTAL_KCAL + " INTEGER,"
				+ Const.DB_KEY_FINISH_TOTAL_TIME + " INTEGER,"
				+ Const.DB_KEY_FINISH_COUNT_SET + " TEXT,"
				+ Const.DB_KEY_FINISH_TOOLWEIGHT_SET + " TEXT,"
				+ Const.DB_KEY_FINISH_BURNING_SET + " TEXT,"
				+ Const.DB_KEY_DATE_PROGRESS + " INTEGER,"
				+ Const.DB_KEY_ACTUAL_DATE + " TEXT"
				+ ");";
		return sql;
	}
	
	public synchronized void saveFinishInfoToDb( Context context, String uid, RecordDataUnit recordDataDate, String strCurrentDate ){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_UID, uid);
			values.put(Const.DB_KEY_PLAN_DATE, recordDataDate.strDate);
			values.put(Const.DB_KEY_COURSE_ID, recordDataDate.strCoursId);
			values.put(Const.DB_KEY_COURSE_NAME, recordDataDate.strCourseName);
			values.put(Const.DB_KEY_ACTION_ID, recordDataDate.actionId);
			values.put(Const.DB_KEY_ACTION_NAME, recordDataDate.actionName);
			values.put(Const.DB_KEY_FINISH_GROUP_NUM, recordDataDate.iGroupCount);
			values.put(Const.DB_KEY_FINISH_TOTAL_COUNT, recordDataDate.iCount);
			values.put(Const.DB_KEY_FINISH_TOTAL_KCAL, recordDataDate.iTotalKcal);
			values.put(Const.DB_KEY_FINISH_TOTAL_TIME, recordDataDate.iConsumeTime);
			values.put(Const.DB_KEY_FINISH_COUNT_SET, recordDataDate.finishCountList.toString());
			values.put(Const.DB_KEY_FINISH_TOOLWEIGHT_SET, recordDataDate.finishToolWeightList.toString());
			values.put(Const.DB_KEY_FINISH_BURNING_SET, recordDataDate.finishBurningList.toString());
			values.put(Const.DB_KEY_ACTUAL_DATE, strCurrentDate);
			
			c = db.query(Const.TABLE_FINISH, null, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_PLAN_DATE + " = ? and " + Const.DB_KEY_COURSE_ID + " =? and " + Const.DB_KEY_ACTION_ID + " = ?",
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
	
	public synchronized int getFinishDayNum( Context context, String uid ){
		int dayNum = 0;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_FINISH, new String[]{ Const.DB_KEY_DISTINCT + Const.DB_KEY_ACTUAL_DATE }, Const.DB_KEY_UID + " =? ",
					new String[]{ uid }, null, null, null);
			if(null != c && c.getCount() > 0){
				dayNum = c.getCount();
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
		return dayNum;
	}
	
	/**
	  * @Method: getFinishDayNumDependMonth
	  * @Description: 获取指定月份的完成的天数
	  * @param context
	  * @param uid
	  * @param strActualDateOfYearMonth 指定月份，格式：2016-08
	  * @return	
	  * 返回类型：int 
	  */
	public synchronized int getFinishDayNumDependMonth( Context context, String uid, String strActualDateOfYearMonth ){
		int dayNum = 0;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_FINISH, new String[]{ Const.DB_KEY_DISTINCT + Const.DB_KEY_ACTUAL_DATE }, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_ACTUAL_DATE + " like ? ",
					new String[]{ uid, "%" + strActualDateOfYearMonth + "%" }, null, null, null);
			if(null != c && c.getCount() > 0){
				dayNum = c.getCount();
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
		return dayNum;
	}
	
	public synchronized RecordDataUnit getFinishInfo( Context context, String uid, String strDate, String courseId, String actionId ){
		RecordDataUnit recordDataUnit = null;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_FINISH, null, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_PLAN_DATE + " =? and " + Const.DB_KEY_COURSE_ID + " =? and " + Const.DB_KEY_ACTION_ID + " =? ",
					new String[]{ uid, strDate, courseId, actionId }, null, null, null);
			if(null != c && c.getCount() > 0){
				c.moveToNext();
				
				recordDataUnit = new RecordDataUnit();
				recordDataUnit.strDate = c.getString(c.getColumnIndex(Const.DB_KEY_PLAN_DATE));
				recordDataUnit.strCoursId = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
				recordDataUnit.strCourseName = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
				recordDataUnit.actionId = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
				recordDataUnit.actionName = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_NAME));
				recordDataUnit.iGroupCount = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_GROUP_NUM));
				recordDataUnit.iCount = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_COUNT));
				recordDataUnit.iTotalKcal = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_KCAL));
				recordDataUnit.iConsumeTime = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_TIME));
				
				String tempCountString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
				String finishCountString = tempCountString.substring(1, tempCountString.length() - 1).replace(" ", "");
				recordDataUnit.finishCountList = Arrays.asList( finishCountString.split(",") );
				
				String tempToolWeightString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
				String finishToolWeightString = tempToolWeightString.substring(1, tempToolWeightString.length() - 1).replace(" ", "");
				recordDataUnit.finishToolWeightList = Arrays.asList( finishToolWeightString.split(",") );
				
				String tempBurningString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
				String finishBurningString = tempBurningString.substring(1, tempBurningString.length() - 1).replace(" ", "");
				recordDataUnit.finishBurningList = Arrays.asList( finishBurningString.split(",") );
				
				recordDataUnit.actualDate = c.getString(c.getColumnIndex(Const.DB_KEY_ACTUAL_DATE));
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
	
	/**
	  * @Method: getFinishInfoList
	  * @Description: 获取所有完成的信息
	  * @param context
	  * @param uid
	  * @return	
	  * 返回类型：ArrayList<BaseRecordData> 
	  */
	public synchronized ArrayList<BaseRecordData> getFinishInfoList(Context context, String uid){
		ArrayList<BaseRecordData> baseRecordDataList = new ArrayList<BaseRecordData>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_FINISH, null, Const.DB_KEY_UID + " =? ",
					new String[]{ uid }, null, null, null);
			if(null != c && c.getCount() > 0){
				while (c.moveToNext() ) {
					RecordDataUnit recordDataUnit = new RecordDataUnit();
					recordDataUnit.strDate = c.getString(c.getColumnIndex(Const.DB_KEY_PLAN_DATE));
					recordDataUnit.strCoursId = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					recordDataUnit.strCourseName = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
					recordDataUnit.actionId = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
					recordDataUnit.actionName = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_NAME));
					recordDataUnit.iGroupCount = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_GROUP_NUM));
					recordDataUnit.iCount = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_COUNT));
					recordDataUnit.iTotalKcal = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_KCAL));
					recordDataUnit.iConsumeTime = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_TIME));
					
					String tempCountString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
					String finishCountString = tempCountString.substring(1, tempCountString.length() - 1).replace(" ", "");
					recordDataUnit.finishCountList = Arrays.asList( finishCountString.split(",") );
					
					String tempToolWeightString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
					String finishToolWeightString = tempToolWeightString.substring(1, tempToolWeightString.length() - 1).replace(" ", "");
					recordDataUnit.finishToolWeightList = Arrays.asList( finishToolWeightString.split(",") );
					
					String tempBurningString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
					String finishBurningString = tempBurningString.substring(1, tempBurningString.length() - 1).replace(" ", "");
					recordDataUnit.finishBurningList = Arrays.asList( finishBurningString.split(",") );
					
					recordDataUnit.actualDate = c.getString(c.getColumnIndex(Const.DB_KEY_ACTUAL_DATE));
					
					baseRecordDataList.add(recordDataUnit);
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
		return baseRecordDataList;
	}
	
	/**
	  * @Method: getFinishInfoList
	  * @Description: 根据指定条件获取所有记录信息
	  * @param context
	  * @param uid
	  * @param courseId
	  * @param strActualDate
	  * @return	
	  * 返回类型：ArrayList<BaseRecordData> 
	  */
	public synchronized ArrayList<BaseRecordData> getFinishInfoList(Context context, String uid, String courseId, String strActualDate){
		ArrayList<BaseRecordData> baseRecordDataList = new ArrayList<BaseRecordData>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_FINISH, null, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_COURSE_ID + " = ? and " + Const.DB_KEY_ACTUAL_DATE + " = ? ",
					new String[]{ uid, courseId, strActualDate }, null, null, null);
			if(null != c && c.getCount() > 0){
				while (c.moveToNext() ) {
					RecordDataUnit recordDataUnit = new RecordDataUnit();
					recordDataUnit.strDate = c.getString(c.getColumnIndex(Const.DB_KEY_PLAN_DATE));
					recordDataUnit.strCoursId = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					recordDataUnit.strCourseName = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
					recordDataUnit.actionId = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
					recordDataUnit.actionName = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_NAME));
					recordDataUnit.iGroupCount = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_GROUP_NUM));
					recordDataUnit.iCount = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_COUNT));
					recordDataUnit.iTotalKcal = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_KCAL));
					recordDataUnit.iConsumeTime = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_TIME));
					
					String tempCountString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
					String finishCountString = tempCountString.substring(1, tempCountString.length() - 1).replace(" ", "");
					recordDataUnit.finishCountList = Arrays.asList( finishCountString.split(",") );
					
					String tempToolWeightString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
					String finishToolWeightString = tempToolWeightString.substring(1, tempToolWeightString.length() - 1).replace(" ", "");
					recordDataUnit.finishToolWeightList = Arrays.asList( finishToolWeightString.split(",") );
					
					String tempBurningString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
					String finishBurningString = tempBurningString.substring(1, tempBurningString.length() - 1).replace(" ", "");
					recordDataUnit.finishBurningList = Arrays.asList( finishBurningString.split(",") );
					
					recordDataUnit.actualDate = c.getString(c.getColumnIndex(Const.DB_KEY_ACTUAL_DATE));
					
					baseRecordDataList.add(recordDataUnit);
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
		return baseRecordDataList;
	}
	
	/**
	  * @Method: getFinishInfoDistinctDateDependsMonth
	  * @Description: 根据月份，获取该月记录数据的日期，并去重排序
	  * @param context
	  * @param uid
	  * @param strYearMonth 格式：2016-08
	  * @return	
	  * 返回类型：ArrayList<String> [2016-08-03, 2016-08-04, ...]
	  */
	public synchronized ArrayList<String> getFinishInfoDistinctDateDependsMonth(Context context, String uid, String strYearMonth){
		ArrayList<String> recordDistinctDateList = new ArrayList<String>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_FINISH, new String[]{ Const.DB_KEY_DISTINCT + Const.DB_KEY_ACTUAL_DATE }, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_ACTUAL_DATE + " like ?",
					new String[]{ uid, "%" + strYearMonth + "%" }, null, null, Const.DB_KEY_ACTUAL_DATE + " ASC");
			if(null != c && c.getCount() > 0){
				while (c.moveToNext() ) {
					
					String strDate = c.getString(c.getColumnIndex(Const.DB_KEY_ACTUAL_DATE));
					
					recordDistinctDateList.add(strDate);
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
		return recordDistinctDateList;
	}
	
	/**
	  * @Method: getFinishInfoDistinctCourseIdDependsDay
	  * @Description: 取指定某天的完成的课程列表
	  * @param context
	  * @param uid
	  * @param strYearMonthDay
	  * @return	
	  * 返回类型：ArrayList<String> 
	  */
	public synchronized ArrayList<String> getFinishInfoDistinctCourseIdDependsDay(Context context, String uid, String strYearMonthDay){
		ArrayList<String> recordDistinctCourseIdList = new ArrayList<String>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_FINISH, new String[]{ Const.DB_KEY_DISTINCT + Const.DB_KEY_COURSE_ID }, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_ACTUAL_DATE + " like ?",
					new String[]{ uid, "%" + strYearMonthDay + "%" }, null, null, null);
			if(null != c && c.getCount() > 0){
				while (c.moveToNext() ) {
					
					String strCoursId = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					
					recordDistinctCourseIdList.add(strCoursId);
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
		return recordDistinctCourseIdList;
	}
	
	/**
	  * @Method: getFinishInfoDependsMonth
	  * @Description: 取指定月份的数据，如2016-08
	  * @param context
	  * @param uid
	  * @param strDate
	  * @return	
	  * 返回类型：ArrayList<RecordDataUnit> 
	  */
	public synchronized ArrayList<RecordDataUnit> getFinishInfoDependsMonth(Context context, String uid, String strYearMonth){
		ArrayList<RecordDataUnit> recordDataUnitList = new ArrayList<RecordDataUnit>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_FINISH, new String[]{ Const.DB_KEY_DISTINCT + Const.DB_KEY_COURSE_ID }, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_ACTUAL_DATE + " like ?",
					new String[]{ uid, "%" + strYearMonth + "%" }, null, null, null);
			if(null != c && c.getCount() > 0){
				while (c.moveToNext() ) {
					RecordDataUnit recordDataUnit = new RecordDataUnit();
					recordDataUnit.strDate = c.getString(c.getColumnIndex(Const.DB_KEY_PLAN_DATE));
					recordDataUnit.strCoursId = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					recordDataUnit.strCourseName = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
					recordDataUnit.actionId = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
					recordDataUnit.actionName = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_NAME));
					recordDataUnit.iGroupCount = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_GROUP_NUM));
					recordDataUnit.iCount = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_COUNT));
					recordDataUnit.iTotalKcal = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_KCAL));
					recordDataUnit.iConsumeTime = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_TIME));
					
					String tempCountString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
					String finishCountString = tempCountString.substring(1, tempCountString.length() - 1).replace(" ", "");
					recordDataUnit.finishCountList = Arrays.asList( finishCountString.split(",") );
					
					String tempToolWeightString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
					String finishToolWeightString = tempToolWeightString.substring(1, tempToolWeightString.length() - 1).replace(" ", "");
					recordDataUnit.finishToolWeightList = Arrays.asList( finishToolWeightString.split(",") );
					
					String tempBurningString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
					String finishBurningString = tempBurningString.substring(1, tempBurningString.length() - 1).replace(" ", "");
					recordDataUnit.finishBurningList = Arrays.asList( finishBurningString.split(",") );
					
					recordDataUnit.actualDate = c.getString(c.getColumnIndex(Const.DB_KEY_ACTUAL_DATE));
					
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
	
	/**
	  * @Method: getFinishInfoDependsYear
	  * @Description: 取指定年份的数据 2016
	  * @param context
	  * @param uid
	  * @param strYear
	  * @return	
	  * 返回类型：ArrayList<RecordDataUnit> 
	  */
	public synchronized ArrayList<RecordDataUnit> getFinishInfoDependsYear(Context context, String uid, String strYear){
		ArrayList<RecordDataUnit> recordDataUnitList = new ArrayList<RecordDataUnit>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_FINISH, null, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_PLAN_DATE + " =?",
					new String[]{ uid, strYear }, null, null, null);
			if(null != c && c.getCount() > 0){
				while (c.moveToNext() ) {
					RecordDataUnit recordDataUnit = new RecordDataUnit();
					recordDataUnit.strDate = c.getString(c.getColumnIndex(Const.DB_KEY_PLAN_DATE));
					recordDataUnit.strCoursId = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					recordDataUnit.strCourseName = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
					recordDataUnit.actionId = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
					recordDataUnit.actionName = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_NAME));
					recordDataUnit.iGroupCount = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_GROUP_NUM));
					recordDataUnit.iCount = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_COUNT));
					recordDataUnit.iTotalKcal = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_KCAL));
					recordDataUnit.iConsumeTime = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TOTAL_TIME));
					
					String tempCountString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
					String finishCountString = tempCountString.substring(1, tempCountString.length() - 1).replace(" ", "");
					recordDataUnit.finishCountList = Arrays.asList( finishCountString.split(",") );
					
					String tempToolWeightString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
					String finishToolWeightString = tempToolWeightString.substring(1, tempToolWeightString.length() - 1).replace(" ", "");
					recordDataUnit.finishToolWeightList = Arrays.asList( finishToolWeightString.split(",") );
					
					String tempBurningString = c.getString(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT_SET));
					String finishBurningString = tempBurningString.substring(1, tempBurningString.length() - 1).replace(" ", "");
					recordDataUnit.finishBurningList = Arrays.asList( finishBurningString.split(",") );
					
					recordDataUnit.actualDate = c.getString(c.getColumnIndex(Const.DB_KEY_ACTUAL_DATE));
					
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
