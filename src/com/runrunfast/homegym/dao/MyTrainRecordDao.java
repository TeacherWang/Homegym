package com.runrunfast.homegym.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runrunfast.homegym.bean.Course.ActionDetail;
import com.runrunfast.homegym.record.StatisticalData;
import com.runrunfast.homegym.record.TrainRecord;
import com.runrunfast.homegym.utils.Const;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class MyTrainRecordDao {
	private volatile static MyTrainRecordDao instance;
	private static Object lockObject = new Object();
	
	public static MyTrainRecordDao getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new MyTrainRecordDao();
				}
			}
		}
		return instance;
	}
	
	public String getFinishTableSqlStr(){
		String sql = "create table if not exists " + Const.TABLE_MY_TRAIN_RECORD
				+ " (" + Const.DB_KEY_ID + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_UID + " TEXT,"
				+ Const.DB_KEY_PLAN_DATE + " TEXT,"
				+ Const.DB_KEY_COURSE_ID + " TEXT,"
				+ Const.DB_KEY_COURSE_NAME + " TEXT,"
				+ Const.DB_KEY_FINISH_COUNT + " INTEGER,"
				+ Const.DB_KEY_FINISH_KCAL + " INTEGER,"
				+ Const.DB_KEY_FINISH_TIME + " INTEGER,"
				+ Const.DB_KEY_ACTION_DETAIL + " TEXT,"
				+ Const.DB_KEY_ACTUAL_DATE + " TEXT,"
				+ Const.DB_KEY_UNIQUE_FLAG + " TEXT"
				+ ");";
		return sql;
	}
	
	public synchronized int getFinishDayNum( Context context, String uid ){
		int dayNum = 0;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_TRAIN_RECORD, new String[]{ Const.DB_KEY_DISTINCT + Const.DB_KEY_ACTUAL_DATE }, Const.DB_KEY_UID + " =? ",
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
	  * @Description: 获取指定月份的训练的天数
	  * @param context
	  * @param uid
	  * @param strActualDateOfYearMonth 指定月份，格式：2016-08
	  * @return	
	  * 返回类型：int 
	  */
	public synchronized int getTrainDayNumDependMonth( Context context, String uid, String strActualDateOfYearMonth ){
		int dayNum = 0;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_TRAIN_RECORD, new String[]{ Const.DB_KEY_DISTINCT + Const.DB_KEY_ACTUAL_DATE }, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_ACTUAL_DATE + " like ? ",
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
	
	/**
	  * @Method: getFinishDayNumDependYear
	  * @Description: 获取指定月份的训练的天数
	  * @param context
	  * @param uid
	  * @param year
	  * @return	
	  * 返回类型：int 
	  */
	public synchronized int getTrainDayNumDependYear( Context context, String uid, int year ){
		int dayNum = 0;
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_TRAIN_RECORD, new String[]{ Const.DB_KEY_DISTINCT + Const.DB_KEY_ACTUAL_DATE }, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_ACTUAL_DATE + " like ? ",
					new String[]{ uid, "%" + year + "%" }, null, null, null);
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
	  * @Method: getDayStatisticalDataDependYearMonth
	  * @Description: 获取指定月份每天的统计数据
	  * @param context
	  * @param uid
	  * @param strActualDateOfYearMonth 指定月份，格式：2016-08
	  * @return	
	  * 返回类型：ArrayList<StatisticalData> 
	  */
	public synchronized ArrayList<StatisticalData> getDayStatisticalDataDependYearMonth( Context context, String uid, String strActualDateOfYearMonth ){
		ArrayList<StatisticalData> statisticalDataList = new ArrayList<StatisticalData>();
		SQLiteDatabase db = null;
		Cursor c = null;
		Cursor cSum = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			// 先找出该月有数据的不同的具体dateDay
			c = db.query(Const.TABLE_MY_TRAIN_RECORD, new String[]{ Const.DB_KEY_DISTINCT + Const.DB_KEY_ACTUAL_DATE }, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_ACTUAL_DATE + " like ? ",
					new String[]{ uid, "%" + strActualDateOfYearMonth + "%" }, null, null, null);
			if(null != c && c.getCount() > 0){
				while (c.moveToNext()) {
					// 再根据每天求和
					String strDate = c.getString(c.getColumnIndex(Const.DB_KEY_ACTUAL_DATE));
					cSum = db.query(Const.TABLE_MY_TRAIN_RECORD, new String[]{ "SUM("+ Const.DB_KEY_FINISH_KCAL +")" }, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_ACTUAL_DATE + " = ? ",
							new String[]{ uid, strDate }, null, null, null);
					if(cSum.moveToNext()){
						StatisticalData statisticalData = new StatisticalData();
						statisticalData.strDate = strDate;
						statisticalData.totalKcal = cSum.getFloat(0);
						statisticalDataList.add(statisticalData);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(c != null){
				c.close();
			}
			if(cSum != null){
				cSum.close();
			}
			if(db != null){
				db.close();
			}
		}
		return statisticalDataList;
	}
	
	public synchronized ArrayList<StatisticalData> getMonthStatisticalDataDependYear( Context context, String uid, int year ){
		ArrayList<StatisticalData> statisticalDataList = new ArrayList<StatisticalData>();
		SQLiteDatabase db = null;
		Cursor c = null;
		Cursor cSum = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			for(int i=0; i<12; i++){
				// 循环找出比如2016-xx月份是否有数据，有则把该月所有数据的kcal求和
				String strYearMonth = year + "-" + String.format("%02d", i+1);
				c = db.query(Const.TABLE_MY_TRAIN_RECORD, null, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_ACTUAL_DATE + " like ? ",
						new String[]{ uid, "%" + strYearMonth + "%" }, null, null, null);
				if(null != c && c.getCount() > 0){
					cSum = db.query(Const.TABLE_MY_TRAIN_RECORD, new String[]{ "SUM("+ Const.DB_KEY_FINISH_KCAL +")" }, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_ACTUAL_DATE + " like ? ",
							new String[]{ uid, "%" + strYearMonth + "%" }, null, null, null);
					if(cSum.moveToNext()){
						StatisticalData statisticalData = new StatisticalData();
						statisticalData.strDate = strYearMonth;
						statisticalData.totalKcal = cSum.getFloat(0);
						statisticalDataList.add(statisticalData);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(c != null){
				c.close();
			}
			if(cSum != null){
				cSum.close();
			}
			if(db != null){
				db.close();
			}
		}
		return statisticalDataList;
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
			
			c = db.query(Const.TABLE_MY_TRAIN_RECORD, new String[]{ Const.DB_KEY_DISTINCT + Const.DB_KEY_ACTUAL_DATE }, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_ACTUAL_DATE + " like ?",
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
	
	public synchronized void saveRecordToDb( Context context, String uid, TrainRecord record ){
		Cursor c = null;
		SQLiteDatabase db = null;
		Gson gson = new Gson();
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_UID, uid);
			values.put(Const.DB_KEY_PLAN_DATE, record.plan_date);
			values.put(Const.DB_KEY_COURSE_ID, record.course_id);
			values.put(Const.DB_KEY_COURSE_NAME, record.course_name);
			values.put(Const.DB_KEY_FINISH_COUNT, record.finish_count);
			values.put(Const.DB_KEY_FINISH_KCAL, record.finish_kcal);
			values.put(Const.DB_KEY_FINISH_TIME, record.finish_time);
			
			String jsonActionDetail = gson.toJson(record.action_detail);
			values.put(Const.DB_KEY_ACTION_DETAIL, jsonActionDetail);
			
			values.put(Const.DB_KEY_ACTUAL_DATE, record.actual_date);
			values.put(Const.DB_KEY_UNIQUE_FLAG, record.unique_flag);
			
			
			c = db.query(Const.TABLE_MY_TRAIN_RECORD, null, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_UNIQUE_FLAG + " =?" ,
					new String[] { uid, String.valueOf(record.unique_flag) }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_MY_COURSE, values, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_UNIQUE_FLAG + " =?",
						new String[] { uid, String.valueOf(record.unique_flag) });
			}else{
				db.insert(Const.TABLE_MY_TRAIN_RECORD, null, values);
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
	
	public synchronized ArrayList<TrainRecord> getRecordListFromDb(Context context, String uid){
		ArrayList<TrainRecord> recordList = new ArrayList<TrainRecord>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_TRAIN_RECORD, null, Const.DB_KEY_UID + " =? ",
					new String[]{ uid }, null, null, null);
			if(null != c && c.getCount() > 0){
				Gson gson = new Gson();
				Type typeActionDetail = new TypeToken<Collection<ActionDetail>>(){}.getType();
				while (c.moveToNext() ) {
					TrainRecord record = new TrainRecord();
					record.course_id = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					record.course_name = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
					record.finish_count = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT));
					record.finish_kcal = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_KCAL));
					record.finish_time = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TIME));
					record.actual_date = c.getString(c.getColumnIndex(Const.DB_KEY_ACTUAL_DATE));
					
					String jsonActionDetail = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_DETAIL));
					record.action_detail = gson.fromJson(jsonActionDetail, typeActionDetail);
					
					record.unique_flag = c.getLong(c.getColumnIndex(Const.DB_KEY_UNIQUE_FLAG));
					
					recordList.add(record);
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
		return recordList;
	}
	
	public synchronized ArrayList<TrainRecord> getRecordListDependDateFromDb(Context context, String uid, String strDate){
		ArrayList<TrainRecord> recordList = new ArrayList<TrainRecord>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_TRAIN_RECORD, null, Const.DB_KEY_UID + " = ? and " + Const.DB_KEY_ACTUAL_DATE + " = ? ",
					new String[]{ uid, strDate }, null, null, null);
			if(null != c && c.getCount() > 0){
				Gson gson = new Gson();
				Type typeActionDetail = new TypeToken<Collection<ActionDetail>>(){}.getType();
				while (c.moveToNext() ) {
					TrainRecord record = new TrainRecord();
					record.course_id = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_ID));
					record.course_name = c.getString(c.getColumnIndex(Const.DB_KEY_COURSE_NAME));
					record.finish_count = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_COUNT));
					record.finish_kcal = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_KCAL));
					record.finish_time = c.getInt(c.getColumnIndex(Const.DB_KEY_FINISH_TIME));
					record.actual_date = c.getString(c.getColumnIndex(Const.DB_KEY_ACTUAL_DATE));
					
					String jsonActionDetail = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_DETAIL));
					record.action_detail = gson.fromJson(jsonActionDetail, typeActionDetail);
					
					record.unique_flag = c.getLong(c.getColumnIndex(Const.DB_KEY_UNIQUE_FLAG));
					
					recordList.add(record);
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
		return recordList;
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
	public synchronized ArrayList<String> getRecordDistinctDateDependsMonth(Context context, String uid, String strYearMonth){
		ArrayList<String> recordDistinctDateList = new ArrayList<String>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_MY_TRAIN_RECORD, new String[]{ Const.DB_KEY_DISTINCT + Const.DB_KEY_ACTUAL_DATE }, Const.DB_KEY_UID + " =? and " + Const.DB_KEY_ACTUAL_DATE + " like ?",
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
	
}
