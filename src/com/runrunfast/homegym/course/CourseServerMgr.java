package com.runrunfast.homegym.course;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.bean.Course;
import com.runrunfast.homegym.bean.ServerActionData;
import com.runrunfast.homegym.bean.Action.AudioLocation;
import com.runrunfast.homegym.bean.ServerActionData.BaseActionData;
import com.runrunfast.homegym.bean.ServerCourseData;
import com.runrunfast.homegym.bean.ServerCourseData.BaseCourseData;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.CourseDao;
import com.runrunfast.homegym.utils.ConstServer;
import com.runrunfast.homegym.utils.Globle;

import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CourseServerMgr {
	private final String TAG = "CourseServerMgr";
	
	private static Object lockObject = new Object();
	private volatile static CourseServerMgr instance;
	
	public static CourseServerMgr getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new CourseServerMgr();
				}
			}
		}
		return instance;
	}
	
	/**
	  * @Method: getCourseInfoFromServer
	  * @Description: 从服务器获取课程列表	
	  * 返回类型：void 
	  */
	public void getCourseInfoFromServer(){
		RequestParams params = new RequestParams(ConstServer.URL_GET_COURSE_INFO);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleGetCourseInfoSuc(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				Log.e(TAG, "getCourseInfoFromServer, onError, throwable is : " + throwable);
				
				notifyGetCourseInfoFail();
			}
			@Override
			public void onCancelled(CancelledException arg0) {}
			@Override
			public void onFinished() {}
		});
	}

	private void handleGetCourseInfoSuc(String result) {
		Gson gson = new Gson();
		Type typeServerCourseData = new TypeToken<ServerCourseData>(){}.getType();
		ServerCourseData serverCourseData = gson.fromJson(result, typeServerCourseData);
		
		if(serverCourseData == null){
			Log.e(TAG, "handleGetCourseInfoSuc, serverCourseData is null");
			return;
		}
		
		int dataSize = serverCourseData.data.size();
		for(int i=0; i<dataSize; i++){
			BaseCourseData baseCourseData = serverCourseData.data.get(i);
			Course course = createCourseFromBase(baseCourseData);
			CourseDao.getInstance().saveCourseToDb(Globle.gApplicationContext, course);
		}
	}
	
	private Course createCourseFromBase(BaseCourseData baseCourseData) {
		if(baseCourseData == null){
			Log.e(TAG, "createCourseFromBase, baseCourseData is null");
			return null;
		}
		
		Course course = new Course();
		course.course_id = baseCourseData.course_id;
		course.course_name = baseCourseData.course_name;
		course.course_recommend = baseCourseData.course_recommend;
		course.course_quality = baseCourseData.course_quality;
		course.course_new = baseCourseData.course_new;
		course.course_img_url = baseCourseData.course_img_url;
		course.course_img_local = "";
		course.course_detail = baseCourseData.course_detail;
		
		return course;
	}

	private void notifyGetCourseInfoFail() {
		
	}
	
	/**
	  * @Method: getActionInfoFromServer
	  * @Description: 从服务器获取动作列表	
	  * 返回类型：void 
	  */
	public void getActionInfoFromServer(){
		RequestParams params = new RequestParams(ConstServer.URL_GET_ACTION_INFO);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleGetActionInfoSuc(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				Log.e(TAG, "getActionInfoFromServer, onError, throwable is : " + throwable);
				
				notifyGetCourseInfoFail();
			}
			@Override
			public void onCancelled(CancelledException arg0) {}
			@Override
			public void onFinished() {}
		});
	}

	private void handleGetActionInfoSuc(String result) {
		Gson gson = new Gson();
		Type typeServerActionData = new TypeToken<ServerActionData>(){}.getType();
		ServerActionData serverActionData = gson.fromJson(result, typeServerActionData);
		
		if(serverActionData == null){
			Log.e(TAG, "handleGetActionInfoSuc, serverActionData is null");
			return;
		}
		
		int dataSize = serverActionData.data.size();
		for(int i=0; i<dataSize; i++){
			BaseActionData baseCourseData = serverActionData.data.get(i);
			Action action = createActionFromBase(baseCourseData);
			ActionDao.getInstance().saveActionToDb(Globle.gApplicationContext, action);
		}
	}
	
	private Action createActionFromBase(BaseActionData baseCourseData) {
		if(baseCourseData == null){
			Log.e(TAG, "createActionFromBase, baseCourseData is null");
			return null;
		}
		
		Action action = new Action();
		
		action.action_id = baseCourseData.action_id;
		action.action_name = baseCourseData.action_name;
		action.action_position = baseCourseData.action_position;
		action.action_descript = baseCourseData.action_descript;
		action.action_difficult = baseCourseData.action_difficult;
		action.action_h = baseCourseData.action_h;
		action.action_b = baseCourseData.action_b;
		action.action_img_url = baseCourseData.action_img_url;
		action.action_left_right = baseCourseData.action_left_right;
		action.action_video_url = baseCourseData.action_video_url;
		action.action_audio_url = baseCourseData.action_audio_url;
		
		action.action_img_local = "";
		
		return action;
	}
	
}
