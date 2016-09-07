package com.runrunfast.homegym.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

import com.runrunfast.homegym.BtDevice.BtInfo;

public class PrefUtils {
	private final static String TAG = "SharedPreferenceUtils";
	// 保存最近连接过的蓝牙设备
	private static final String SP_LAST_CONNECTED_BT 	= "sp_last_connected_bt";
	private static final String KEY_BT_NAME 			= "key_bt_name";
	private static final String KEY_BT_ADDRESS 			= "key_bt_address";

	public static void setLastConnectedBt(Context context, String btName, String btAddress) {
		SharedPreferences preferences = context.getSharedPreferences(SP_LAST_CONNECTED_BT, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(KEY_BT_NAME, btName);
		editor.putString(KEY_BT_ADDRESS, btAddress);
		editor.commit();
	}

	public static BtInfo getLastConnectedBt(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(SP_LAST_CONNECTED_BT, Context.MODE_PRIVATE);
		String btName = preferences.getString(KEY_BT_NAME, "");
		String btAddress = preferences.getString(KEY_BT_ADDRESS, "");
		
		if(TextUtils.isEmpty(btName) || TextUtils.isEmpty(btAddress)){
			Log.d(TAG, "getLastConnectedBt, btName or btAdrress is empty, return null");
			return null;
		}
		
		BtInfo btInfo = new BtInfo();
		btInfo.btName = btName;
		btInfo.btAddress = btAddress;
		
		return btInfo;
	}
	
	public static void removeLastConnectedBt(Context context){
		SharedPreferences preferences = context.getSharedPreferences(SP_LAST_CONNECTED_BT, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
	
	private static final String SP_ACCOUNT 		= "sp_account";
	private static final String KEY_USER_NAME	= "key_user_name";
	private static final String KEY_LOGIN_SUC	= "key_login_suc";
	private static final String KEY_NICKNAME 	= "key_nuckname";
	private static final String KEY_SEX			= "key_sex";
	private static final String KEY_BIRTHDAY	= "key_birthday";
	private static final String KEY_WEIGHT		= "key_weight";
	private static final String KEY_HEIGHT		= "key_height";
	private static final String KEY_CITY		= "key_city";
	
	private static final String KEY_PWD	= "key_pwd";
	// 保存注册成功的账号
	public static void setAccount(Context context, String userName){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(KEY_USER_NAME, userName);
		editor.commit();
	}
	
	public static String getAccount(Context context){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		return preferences.getString(KEY_USER_NAME, "");
	}
	
	public static void setPwd(Context context, String pwd){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(KEY_PWD, pwd);
		editor.commit();
	}
	
	public static String getPwd(Context context){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		return preferences.getString(KEY_PWD, "");
	}
	
	public static void clearAccount(Context context){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.clear();
		editor.commit();
	}
	
	public static void setLoginSuc(Context context, boolean suc){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(KEY_LOGIN_SUC, suc);
		editor.commit();
	}
	
	public static boolean getLoginSuc(Context context){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		return preferences.getBoolean(KEY_LOGIN_SUC, false);
	}
	
	public static void setNickname(Context context, String nickname){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(KEY_NICKNAME, nickname);
		editor.commit();
	}
	
	public static String getNickname(Context context){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		return preferences.getString(KEY_NICKNAME, "");
	}
	
	public static void setSex(Context context, String sex){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(KEY_SEX, sex);
		editor.commit();
	}
	
	public static String getSex(Context context){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		return preferences.getString(KEY_SEX, "");
	}
	
	public static void setBirthday(Context context, String birthday){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(KEY_BIRTHDAY, birthday);
		editor.commit();
	}
	
	public static String getBirthday(Context context){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		return preferences.getString(KEY_BIRTHDAY, "");
	}
	
	public static void setWeight(Context context, String weight){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(KEY_WEIGHT, weight);
		editor.commit();
	}
	
	public static String getWeight(Context context){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		return preferences.getString(KEY_WEIGHT, "");
	}
	
	public static void setHeight(Context context, String height){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(KEY_HEIGHT, height);
		editor.commit();
	}
	
	public static String getHeight(Context context){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		return preferences.getString(KEY_HEIGHT, "");
	}
	
	public static void setCity(Context context, String city){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(KEY_CITY, city);
		editor.commit();
	}
	
	public static String getCity(Context context){
		SharedPreferences preferences = context.getSharedPreferences(SP_ACCOUNT, Context.MODE_PRIVATE);
		return preferences.getString(KEY_CITY, "");
	}
	
	
	
	private static final String SP_COOKIE 		= "sp_cookie";
	private static final String KEY_COOKIE	= "key_cookie";
	
	public static void setCookie(Context context, String cookie){
		SharedPreferences preferences = context.getSharedPreferences(SP_COOKIE, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(KEY_COOKIE, cookie);
		editor.commit();
	}
	
	public static String getCookie(Context context){
		SharedPreferences preferences = context.getSharedPreferences(SP_COOKIE, Context.MODE_PRIVATE);
		return preferences.getString(KEY_COOKIE, "");
	}
}
