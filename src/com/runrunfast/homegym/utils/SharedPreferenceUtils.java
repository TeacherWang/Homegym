package com.runrunfast.homegym.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

import com.runrunfast.homegym.BtDevice.BtInfo;

public class SharedPreferenceUtils {
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
	
}
