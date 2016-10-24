package com.runrunfast.homegym.update;

import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import android.util.Log;

import com.runrunfast.homegym.utils.ApplicationUtil;
import com.runrunfast.homegym.utils.ConstServer;
import com.runrunfast.homegym.utils.Globle;

public class UpdateMgr {

	private final String TAG = "updateMgr";
	
	private volatile static UpdateMgr instance;
	private static Object lockObject = new Object();
	private HashSet<ICheckUpdateResultObserver> mSetOfICheckUpdateResultObserver = new HashSet<ICheckUpdateResultObserver>();
	
	public static UpdateMgr getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new UpdateMgr();
				}
			}
		}
		return instance;
	}
	
	public interface ICheckUpdateResultObserver{
		void onHaveNewerVersion(String version, String description, String url);
		void onNotNewerVersion();
		void onFail();
	}
	
	public void addICheckUpdateResultObserver( ICheckUpdateResultObserver observer ) {
		synchronized (mSetOfICheckUpdateResultObserver) {
			if( mSetOfICheckUpdateResultObserver.contains(observer) == false )
				mSetOfICheckUpdateResultObserver.add(observer);
		}
	}
	
	public void removeGetCourseFromServerObserver(ICheckUpdateResultObserver observer){
		mSetOfICheckUpdateResultObserver.remove(observer);
	}
	
	public void checkUpdate(){
		RequestParams params = new RequestParams(ConstServer.URL_CHECK_UPDATE);
		params.addParameter(ConstServer.TYPE, "Android");
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleCheckUpdateResult(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				Log.e(TAG, "checkUpdate, onError, throwable is : " + throwable);
				
				notifyCheckUpdateFail();
			}
			@Override
			public void onCancelled(CancelledException arg0) {}
			@Override
			public void onFinished() {}
		});
	}

	private void handleCheckUpdateResult(String result) {
		JSONObject resultJsonObject = null;
		try {
			resultJsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(resultJsonObject == null){
			Log.e(TAG, "handleCheckUpdateResult, resultJsonObject is null");
			
			notifyCheckUpdateFail();
			return;
		}
		int resultCode = resultJsonObject.optInt("code");
		Log.i(TAG, "handleCheckUpdateResult, code = " + resultCode);
		if(resultCode == 1){
			handleGetUpdateInfo(resultJsonObject);
//			notifyCheckUpdateSuc();
		}else{
			notifyCheckUpdateFail();
		}
	}
	
	private void handleGetUpdateInfo(JSONObject resultJsonObject) {
		try {
			JSONObject infoObject = resultJsonObject.getJSONObject("info");
			if(infoObject == null){
				notifyCheckUpdateFail();
				return;
			}
			// 先比较版本
			String serverVersion = infoObject.getString("version");
			String[] serverVersionUnits = serverVersion.split("\\.");
			
			String localVersion = ApplicationUtil.getVersionName(Globle.gApplicationContext);
			String[] localVersionUnits = localVersion.split("\\.");
			
			if(Integer.valueOf(serverVersionUnits[0]) > Integer.valueOf(localVersionUnits[0])
					|| Integer.valueOf(serverVersionUnits[1]) > Integer.valueOf(localVersionUnits[1])
					|| Integer.valueOf(serverVersionUnits[2]) > Integer.valueOf(localVersionUnits[2])){
				
				String description = infoObject.getString("description");
				String url = infoObject.getString("url");
				
				notifyHaveNewerVersion(serverVersion, description, url);
			}else{
				notifyNotHaveNewerVersion();
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	private void notifyHaveNewerVersion(String version, String description, String url){
		synchronized (mSetOfICheckUpdateResultObserver) {
			Iterator<ICheckUpdateResultObserver> it = mSetOfICheckUpdateResultObserver.iterator();
			while( it.hasNext() ){
				ICheckUpdateResultObserver observer = it.next();
				observer.onHaveNewerVersion(version, description, url);
			}
		}
	}
	
	private void notifyNotHaveNewerVersion(){
		synchronized (mSetOfICheckUpdateResultObserver) {
			Iterator<ICheckUpdateResultObserver> it = mSetOfICheckUpdateResultObserver.iterator();
			while( it.hasNext() ){
				ICheckUpdateResultObserver observer = it.next();
				observer.onNotNewerVersion();
			}
		}
	}
	
	private void notifyCheckUpdateFail() {
		synchronized (mSetOfICheckUpdateResultObserver) {
			Iterator<ICheckUpdateResultObserver> it = mSetOfICheckUpdateResultObserver.iterator();
			while( it.hasNext() ){
				ICheckUpdateResultObserver observer = it.next();
				observer.onFail();
			}
		}
	}
	
	public void downloadNewApp(String url){
//		Downl
	}
	
}
