package com.runrunfast.homegym.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.course.ActionInfo;
import com.runrunfast.homegym.utils.Const;

import java.util.ArrayList;
import java.util.Arrays;

public class ActionDao {
	private volatile static ActionDao instance;
	private static Object lockObject = new Object();
	
	public static ActionDao getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new ActionDao();
				}
			}
		}
		return instance;
	}
	
	public static String getActionTableSql(){
		String sql = "create table if not exists " + Const.TABLE_ACTION
				+ " (" + Const.DB_KEY_ID + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_ACTION_ID + " TEXT,"
				+ Const.DB_KEY_ACTION_NAME + " TEXT,"
				+ Const.DB_KEY_ACTION_POSITION + " TEXT,"
				+ Const.DB_KEY_ACTION_DESCRIPT + " TEXT,"
				+ Const.DB_KEY_ACTION_DIFFICULT + " INTEGER,"
				+ Const.DB_KEY_ACTION_DEFAULT_TOTAL_KCAL + " INTEGER,"
				+ Const.DB_KEY_DEFAULT_TIME + " TEXT,"
				+ Const.DB_KEY_DEFAULT_GROUP_NUM + " TEXT,"
				+ Const.DB_KEY_DEFAULT_COUNT + " TEXT,"
				+ Const.DB_KEY_DEFAULT_TOOL_WEIGHT + " TEXT,"
				+ Const.DB_KEY_DEFAULT_BURNING + " TEXT"
				+ ");";
		return sql;
	}
	
	public synchronized void saveActionInfoToDb(Context context, ActionInfo actionInfo){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_ACTION_ID, actionInfo.strActionId);
			values.put(Const.DB_KEY_ACTION_NAME, actionInfo.actionName);
			values.put(Const.DB_KEY_ACTION_POSITION, actionInfo.strTrainPosition);
			values.put(Const.DB_KEY_ACTION_DESCRIPT, actionInfo.strTrainDescript);
			values.put(Const.DB_KEY_DEFAULT_TIME, actionInfo.iTime);
			values.put(Const.DB_KEY_ACTION_DIFFICULT, actionInfo.iDiffcultLevel);
			values.put(Const.DB_KEY_ACTION_DEFAULT_TOTAL_KCAL, actionInfo.iDefaultTotalKcal);
			values.put(Const.DB_KEY_DEFAULT_GROUP_NUM, actionInfo.defaultGroupNum);
			values.put(Const.DB_KEY_DEFAULT_COUNT, actionInfo.defaultCountList.toString());
			values.put(Const.DB_KEY_DEFAULT_TOOL_WEIGHT, actionInfo.defaultToolWeightList.toString());
			values.put(Const.DB_KEY_DEFAULT_BURNING, actionInfo.defaultBurningList.toString());
			
			c = db.query(Const.TABLE_ACTION, null, Const.DB_KEY_ACTION_ID + " = ?",
					new String[] { String.valueOf(actionInfo.strActionId) }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_ACTION, values, Const.DB_KEY_ACTION_ID + " = ?",
						new String[] { String.valueOf(actionInfo.strActionId) });
			}else{
				db.insert(Const.TABLE_ACTION, null, values);
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
	
	public synchronized ActionInfo getActionInfoFromDb(Context context, String actionId){
		ActionInfo actionInfo = null;
		SQLiteDatabase db = null;
		Cursor c = null;
		
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_ACTION, null, Const.DB_KEY_ACTION_ID + "=?", new String[]{ actionId }, null, null, null);
			if(null != c && c.getCount() > 0){
				c.moveToNext();
				
				actionInfo = new ActionInfo();
				
				actionInfo.strActionId = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
				actionInfo.actionName = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_NAME));
				actionInfo.strTrainPosition = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_POSITION));
				actionInfo.strTrainDescript = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_DESCRIPT));
				actionInfo.iTime = c.getInt(c.getColumnIndex(Const.DB_KEY_DEFAULT_TIME));
				actionInfo.iDiffcultLevel = c.getInt(c.getColumnIndex(Const.DB_KEY_ACTION_DIFFICULT));
				actionInfo.iDefaultTotalKcal = c.getInt(c.getColumnIndex(Const.DB_KEY_ACTION_DEFAULT_TOTAL_KCAL));
				actionInfo.defaultGroupNum = c.getInt(c.getColumnIndex(Const.DB_KEY_DEFAULT_GROUP_NUM));
				
				String tempDefaultCountString = c.getString(c.getColumnIndex(Const.DB_KEY_DEFAULT_COUNT));
				String defaultCountString = tempDefaultCountString.substring(1, tempDefaultCountString.length() - 1).replace(" ", "");
				actionInfo.defaultCountList = Arrays.asList( defaultCountString.split(",") );
				
				String tempDefaultToolWeightString = c.getString(c.getColumnIndex(Const.DB_KEY_DEFAULT_TOOL_WEIGHT));
				String defaultToolWeightString = tempDefaultToolWeightString.substring(1, tempDefaultToolWeightString.length() - 1).replace(" ", "");
				actionInfo.defaultToolWeightList = Arrays.asList( defaultToolWeightString.split(",") );
				
				String tempDefaultBurningString = c.getString(c.getColumnIndex(Const.DB_KEY_DEFAULT_BURNING));
				String defaultBurningString = tempDefaultBurningString.substring(1, tempDefaultBurningString.length() - 1).replace(" ", "");
				actionInfo.defaultBurningList = Arrays.asList( defaultBurningString.split(",") );
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
	
	public synchronized ArrayList<ActionInfo> getActionInfoListFromDb(Context context) {
		ArrayList<ActionInfo> actionInfoList = new ArrayList<ActionInfo>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_ACTION, null, null, null, null, null, null);
			if(null != c && c.getCount() > 0){
				while (c.moveToNext()) {
					ActionInfo actionInfo = new ActionInfo();
					
					actionInfo.strActionId = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
					actionInfo.actionName = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_NAME));
					actionInfo.strTrainPosition = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_POSITION));
					actionInfo.strTrainDescript = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_DESCRIPT));
					actionInfo.iTime = c.getInt(c.getColumnIndex(Const.DB_KEY_DEFAULT_TIME));
					actionInfo.iDiffcultLevel = c.getInt(c.getColumnIndex(Const.DB_KEY_ACTION_DIFFICULT));
					actionInfo.iDefaultTotalKcal = c.getInt(c.getColumnIndex(Const.DB_KEY_ACTION_DEFAULT_TOTAL_KCAL));
					actionInfo.defaultGroupNum = c.getInt(c.getColumnIndex(Const.DB_KEY_DEFAULT_GROUP_NUM));
					
					String tempDefaultCountString = c.getString(c.getColumnIndex(Const.DB_KEY_DEFAULT_COUNT));
					String defaultCountString = tempDefaultCountString.substring(1, tempDefaultCountString.length() - 1).replace(" ", "");
					actionInfo.defaultCountList = Arrays.asList( defaultCountString.split(",") );
					
					String tempDefaultToolWeightString = c.getString(c.getColumnIndex(Const.DB_KEY_DEFAULT_TOOL_WEIGHT));
					String defaultToolWeightString = tempDefaultToolWeightString.substring(1, tempDefaultToolWeightString.length() - 1).replace(" ", "");
					actionInfo.defaultToolWeightList = Arrays.asList( defaultToolWeightString.split(",") );
					
					String tempDefaultBurningString = c.getString(c.getColumnIndex(Const.DB_KEY_DEFAULT_BURNING));
					String defaultBurningString = tempDefaultBurningString.substring(1, tempDefaultBurningString.length() - 1).replace(" ", "");
					actionInfo.defaultBurningList = Arrays.asList( defaultBurningString.split(",") );
					
					actionInfoList.add(actionInfo);
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
		return actionInfoList;
	}
	
	// 
	public String getActionTableSqlStr(){
		String sql = "create table if not exists " + Const.TABLE_ACTION
				+ " (" + Const.DB_KEY_ID + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_ACTION_ID + " TEXT,"
				+ Const.DB_KEY_ACTION_NAME + " TEXT,"
				+ Const.DB_KEY_ACTION_POSITION + " TEXT,"
				+ Const.DB_KEY_ACTION_DESCRIPT + " TEXT,"
				+ Const.DB_KEY_ACTION_DIFFICULT + " INTEGER"
				+ ");";
		return sql;
	}
	
	public synchronized void saveActionToDb(Context context, Action action){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_ACTION_ID, action.action_id);
			values.put(Const.DB_KEY_ACTION_NAME, action.action_name);
			values.put(Const.DB_KEY_ACTION_POSITION, action.action_position);
			values.put(Const.DB_KEY_ACTION_DESCRIPT, action.action_descript);
			values.put(Const.DB_KEY_ACTION_DIFFICULT, action.action_difficult);
			
			c = db.query(Const.TABLE_ACTION, null, Const.DB_KEY_ACTION_ID + " = ?",
					new String[] { action.action_id }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_ACTION, values, Const.DB_KEY_ACTION_ID + " = ?",
						new String[] { action.action_id });
			}else{
				db.insert(Const.TABLE_ACTION, null, values);
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
	
	public synchronized Action getActionFromDb(Context context, String actionId){
		Action action = null;
		SQLiteDatabase db = null;
		Cursor c = null;
		
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_ACTION, null, Const.DB_KEY_ACTION_ID + "=?", new String[]{ actionId }, null, null, null);
			if(null != c && c.getCount() > 0){
				c.moveToNext();
				
				action = new Action();
				
				action.action_id = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
				action.action_name = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_NAME));
				action.action_position = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_POSITION));
				action.action_descript = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_DESCRIPT));
				action.action_difficult = c.getInt(c.getColumnIndex(Const.DB_KEY_ACTION_DIFFICULT));
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
		
		return action;
	}
	
	public synchronized ArrayList<Action> getActionListFromDb(Context context) {
		ArrayList<Action> actionList = new ArrayList<Action>();
		SQLiteDatabase db = null;
		Cursor c = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_ACTION, null, null, null, null, null, null);
			if(null != c && c.getCount() > 0){
				while (c.moveToNext()) {
					Action action = new Action();
					
					action.action_id = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
					action.action_name = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_NAME));
					action.action_position = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_POSITION));
					action.action_descript = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_DESCRIPT));
					action.action_difficult = c.getInt(c.getColumnIndex(Const.DB_KEY_ACTION_DIFFICULT));
					
					actionList.add(action);
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
		return actionList;
	}
	
}
