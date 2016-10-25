package com.runrunfast.homegym.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.utils.Const;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

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

	// 
	public String getActionTableSqlStr(){
		String sql = "create table if not exists " + Const.TABLE_ACTION
				+ " (" + Const.DB_KEY_ID + " INTEGER PRIMARY KEY,"
				+ Const.DB_KEY_ACTION_ID + " TEXT,"
				+ Const.DB_KEY_ACTION_NAME + " TEXT,"
				+ Const.DB_KEY_ACTION_POSITION + " TEXT,"
				+ Const.DB_KEY_ACTION_DESCRIPT + " TEXT,"
				+ Const.DB_KEY_ACTION_DIFFICULT + " INTEGER,"
				+ Const.DB_KEY_ACTION_H + " FLOAT,"
				+ Const.DB_KEY_ACTION_B + " INTEGER,"
				+ Const.DB_KEY_ACTION_IMG_URL + " TEXT,"
				+ Const.DB_KEY_ACTION_IMG_LOCAL + " TEXT,"
				+ Const.DB_KEY_ACTION_LEFT_RIGHT + " TEXT,"
				+ Const.DB_KEY_ACTION_VIDEO_URL + " TEXT,"
				+ Const.DB_KEY_ACTION_VIDEO_LOCAL + " TEXT,"
				+ Const.DB_KEY_ACTION_AUDIO_URL + " TEXT,"
				+ Const.DB_KEY_ACTION_AUDIO_LOCAL + " TEXT"
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
			Gson gson = new Gson();
			
			values.put(Const.DB_KEY_ACTION_ID, action.action_id);
			values.put(Const.DB_KEY_ACTION_NAME, action.action_name);
			values.put(Const.DB_KEY_ACTION_POSITION, action.action_position);
			values.put(Const.DB_KEY_ACTION_DESCRIPT, action.action_descript);
			values.put(Const.DB_KEY_ACTION_DIFFICULT, action.action_difficult);
			values.put(Const.DB_KEY_ACTION_H, action.action_h);
			values.put(Const.DB_KEY_ACTION_B, action.action_b);
			values.put(Const.DB_KEY_ACTION_IMG_URL, action.action_img_url);
			values.put(Const.DB_KEY_ACTION_LEFT_RIGHT, action.action_left_right);
			
			String jsonVideoUrl = gson.toJson(action.action_video_url);
			values.put(Const.DB_KEY_ACTION_VIDEO_URL, jsonVideoUrl);
			
			String jsonVideoLocal = gson.toJson(action.action_video_local);
			values.put(Const.DB_KEY_ACTION_VIDEO_LOCAL, jsonVideoLocal);
			
			values.put(Const.DB_KEY_ACTION_AUDIO_URL, action.action_audio_url);
			values.put(Const.DB_KEY_ACTION_AUDIO_LOCAL, action.action_audio_local);
			
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
	
	public synchronized void saveActionImgLocalToDb(Context context, String actionId, String imgLocal){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_ACTION_ID, actionId);
			values.put(Const.DB_KEY_ACTION_IMG_LOCAL, imgLocal);
			
			c = db.query(Const.TABLE_ACTION, null, Const.DB_KEY_ACTION_ID + " = ?",
					new String[] { actionId }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_ACTION, values, Const.DB_KEY_ACTION_ID + " = ?",
						new String[] { actionId });
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
	
	public synchronized void saveActionAudioLocalToDb(Context context, String actionId, String audioLocal){
		Cursor c = null;
		SQLiteDatabase db = null;
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_ACTION_ID, actionId);
			values.put(Const.DB_KEY_ACTION_AUDIO_LOCAL, audioLocal);
			
			c = db.query(Const.TABLE_ACTION, null, Const.DB_KEY_ACTION_ID + " = ?",
					new String[] { actionId }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_ACTION, values, Const.DB_KEY_ACTION_ID + " = ?",
						new String[] { actionId });
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
	
	public synchronized void saveActionVideoLocalToDb(Context context, String actionId, ArrayList<String> videoLocalList){
		Cursor c = null;
		SQLiteDatabase db = null;
		Gson gson = new Gson();
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			
			values.put(Const.DB_KEY_ACTION_ID, actionId);
			
			String jsonVideoLocal = gson.toJson(videoLocalList);
			values.put(Const.DB_KEY_ACTION_VIDEO_LOCAL, jsonVideoLocal);
			
			c = db.query(Const.TABLE_ACTION, null, Const.DB_KEY_ACTION_ID + " = ?",
					new String[] { actionId }, null, null, null);
			if (c.getCount() > 0) {// 查询到数据库有该数据，就更新该行数据
				db.update(Const.TABLE_ACTION, values, Const.DB_KEY_ACTION_ID + " = ?",
						new String[] { actionId });
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
				Gson gson = new Gson();
				Type typeVideoUrl = new TypeToken<Collection<String>>(){}.getType();
				Type typeVideoLocal = new TypeToken<Collection<String>>(){}.getType();
				action = new Action();
				
				action.action_id = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
				action.action_name = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_NAME));
				action.action_position = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_POSITION));
				action.action_descript = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_DESCRIPT));
				action.action_difficult = c.getInt(c.getColumnIndex(Const.DB_KEY_ACTION_DIFFICULT));
				action.action_h = c.getFloat(c.getColumnIndex(Const.DB_KEY_ACTION_H));
				action.action_b = c.getInt(c.getColumnIndex(Const.DB_KEY_ACTION_B));
				action.action_img_url = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_IMG_URL));
				action.action_img_local = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_IMG_LOCAL));
				action.action_left_right = c.getInt(c.getColumnIndex(Const.DB_KEY_ACTION_LEFT_RIGHT));
				
				String jsonVideoUrl = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_VIDEO_URL));
				action.action_video_url = gson.fromJson(jsonVideoUrl, typeVideoUrl);
				
				String jsonVideoLocal = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_VIDEO_LOCAL));
				action.action_video_local = gson.fromJson(jsonVideoLocal, typeVideoLocal);
				
				action.action_audio_url = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_AUDIO_URL));
				action.action_audio_local = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_AUDIO_LOCAL));
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
	
	public synchronized String getActionImgLocalFromDb(Context context, String actionId){
		SQLiteDatabase db = null;
		Cursor c = null;
		String strImgLocal = "";
		try {
			DBOpenHelper dbHelper = new DBOpenHelper(context);
			db = dbHelper.getWritableDatabase();
			
			c = db.query(Const.TABLE_ACTION, null, Const.DB_KEY_ACTION_ID + "=?", new String[]{ actionId }, null, null, null);
			if(null != c && c.getCount() > 0){
				c.moveToNext();
				
				strImgLocal = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_IMG_LOCAL));
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
		
		return strImgLocal;
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
				Gson gson = new Gson();
				Type typeVideoUrl = new TypeToken<Collection<String>>(){}.getType();
				Type typeVideoLocal = new TypeToken<Collection<String>>(){}.getType();
				while (c.moveToNext()) {
					Action action = new Action();
					
					action.action_id = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_ID));
					action.action_name = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_NAME));
					action.action_position = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_POSITION));
					action.action_descript = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_DESCRIPT));
					action.action_difficult = c.getInt(c.getColumnIndex(Const.DB_KEY_ACTION_DIFFICULT));
					action.action_h = c.getFloat(c.getColumnIndex(Const.DB_KEY_ACTION_H));
					action.action_b = c.getInt(c.getColumnIndex(Const.DB_KEY_ACTION_B));
					action.action_img_url = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_IMG_URL));
					action.action_img_local = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_IMG_LOCAL));
					action.action_left_right = c.getInt(c.getColumnIndex(Const.DB_KEY_ACTION_LEFT_RIGHT));
					
					String jsonVideoUrl = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_VIDEO_URL));
					action.action_video_url = gson.fromJson(jsonVideoUrl, typeVideoUrl);
					
					String jsonVideoLocal = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_VIDEO_LOCAL));
					action.action_video_local = gson.fromJson(jsonVideoLocal, typeVideoLocal);
					
					action.action_audio_url = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_AUDIO_URL));
					action.action_audio_local = c.getString(c.getColumnIndex(Const.DB_KEY_ACTION_AUDIO_LOCAL));
					
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
