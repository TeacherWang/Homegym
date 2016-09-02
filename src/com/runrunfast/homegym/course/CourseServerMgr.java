package com.runrunfast.homegym.course;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.bean.Course;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.bean.ServerActionData;
import com.runrunfast.homegym.bean.ServerActionData.BaseActionData;
import com.runrunfast.homegym.bean.ServerCourseData;
import com.runrunfast.homegym.bean.ServerCourseData.BaseCourseData;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.CourseDao;
import com.runrunfast.homegym.dao.MyCourseDao;
import com.runrunfast.homegym.dao.MyTotalRecordDao;
import com.runrunfast.homegym.dao.MyTrainRecordDao;
import com.runrunfast.homegym.record.TotalRecord;
import com.runrunfast.homegym.record.TrainRecord;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.ConstServer;
import com.runrunfast.homegym.utils.Globle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;

public class CourseServerMgr {
	private final String TAG = "CourseServerMgr";
	
	private static Object lockObject = new Object();
	private volatile static CourseServerMgr instance;
	
	private HashSet<IGetCourseFromServerListener> mSetOfGetCourseFromServerObserver;
	private HashSet<IJoinCourseToServerListener> mSetOfJoinCourseToServerObserver;
	private HashSet<IDeleteCourseToServerListener> mSetOfDeleteCourseToServerObserver;
	private HashSet<IUpdateTrainPlanListener> mSetOfUpdateTrainPlanObserver;
	private HashSet<IDownloadTrainPlanLister> mSetOfDownloadTrainPlanObserver;
	private HashSet<IUpdateRecordListener> mSetOfUpdateRecordServer;
	private HashSet<IRequestTotalDataListener> mSetOfRequestTotalDataObserver;
	private HashSet<IRequestDetailDataListener> mSetOfRequestDetailDataObserver;
	
	public interface IGetCourseFromServerListener{
		void onGetCourseSucFromServer();
		void onGetCoruseFailFromServer();
	}
	
	public interface IJoinCourseToServerListener{
		void onJoinCourseToServerSuc();
		void onJoinCourseToServerFail();
	}
	
	public interface IDeleteCourseToServerListener{
		void onDeleteCourseToServerSuc();
		void onDeleteCourseToServerFail();
	}
	
	public interface IUpdateTrainPlanListener{
		void onUpdateTrainPlanSuc();
		void onUpdateTrainPlanFail();
	}
	
	public interface IDownloadTrainPlanLister{
		void onDownloadTrainPlanSuc();
		void onDownloadTrainPlanFail();
	}
	
	public interface IUpdateRecordListener{
		void onUpdateRecordSuc();
		void onUpdateRecordFail();
	}
	
	public interface IRequestTotalDataListener{
		void onRequestTotalDataSuc(TotalRecord totalRecord);
		void onRequestTotalDataFail();
	}
	
	public interface IRequestDetailDataListener{
		void onRequestDetailDataSuc(String strStartDate);
		void onRequestDetailDataFail();
	}
	
	public void addGetCourseFromServerObserver( IGetCourseFromServerListener liserner ) {
		synchronized (mSetOfGetCourseFromServerObserver) {
			if( mSetOfGetCourseFromServerObserver.contains(liserner) == false )
				mSetOfGetCourseFromServerObserver.add(liserner);
		}
	}
	
	public void removeGetCourseFromServerObserver(IGetCourseFromServerListener liserner){
		mSetOfGetCourseFromServerObserver.remove(liserner);
	}
	
	public void addJoinCourseToServerObserver( IJoinCourseToServerListener liserner ) {
		synchronized (mSetOfJoinCourseToServerObserver) {
			if( mSetOfJoinCourseToServerObserver.contains(liserner) == false )
				mSetOfJoinCourseToServerObserver.add(liserner);
		}
	}
	
	public void removeJoinCourseToServerObserver(IJoinCourseToServerListener liserner){
		mSetOfJoinCourseToServerObserver.remove(liserner);
	}
	
	public void addDeleteCourseToServerObserver( IDeleteCourseToServerListener liserner ) {
		synchronized (mSetOfDeleteCourseToServerObserver) {
			if( mSetOfDeleteCourseToServerObserver.contains(liserner) == false )
				mSetOfDeleteCourseToServerObserver.add(liserner);
		}
	}
	
	public void removeDeleteCourseToServerObserver(IDeleteCourseToServerListener liserner){
		mSetOfDeleteCourseToServerObserver.remove(liserner);
	}
	
	public void addUpdateTrainPlanObserver( IUpdateTrainPlanListener liserner ) {
		synchronized (mSetOfUpdateTrainPlanObserver) {
			if( mSetOfUpdateTrainPlanObserver.contains(liserner) == false )
				mSetOfUpdateTrainPlanObserver.add(liserner);
		}
	}
	
	public void removeUpdateTrainPlanObserver(IUpdateTrainPlanListener liserner){
		mSetOfUpdateTrainPlanObserver.remove(liserner);
	}
	
	public void addDownloadTrainPlanObserver( IDownloadTrainPlanLister liserner ) {
		synchronized (mSetOfDownloadTrainPlanObserver) {
			if( mSetOfDownloadTrainPlanObserver.contains(liserner) == false )
				mSetOfDownloadTrainPlanObserver.add(liserner);
		}
	}
	
	public void removeDownloadTrainPlanObserver(IDownloadTrainPlanLister liserner){
		mSetOfDownloadTrainPlanObserver.remove(liserner);
	}
	
	public void addUpdateRecordObserver( IUpdateRecordListener liserner ) {
		synchronized (mSetOfUpdateRecordServer) {
			if( mSetOfUpdateRecordServer.contains(liserner) == false )
				mSetOfUpdateRecordServer.add(liserner);
		}
	}
	
	public void removeUpdateRecordObserver(IUpdateRecordListener liserner){
		mSetOfUpdateRecordServer.remove(liserner);
	}
	
	public void addRequestTotalDataObserver( IRequestTotalDataListener liserner ) {
		synchronized (mSetOfRequestTotalDataObserver) {
			if( mSetOfRequestTotalDataObserver.contains(liserner) == false )
				mSetOfRequestTotalDataObserver.add(liserner);
		}
	}
	
	public void removeRequestTotalDataObserver(IRequestTotalDataListener liserner){
		mSetOfRequestTotalDataObserver.remove(liserner);
	}
	
	public void addRequestDetailDataObserver( IRequestDetailDataListener liserner ) {
		synchronized (mSetOfRequestDetailDataObserver) {
			if( mSetOfRequestDetailDataObserver.contains(liserner) == false )
				mSetOfRequestDetailDataObserver.add(liserner);
		}
	}
	
	public void removeRequestDetailDataObserver(IRequestDetailDataListener liserner){
		mSetOfRequestDetailDataObserver.remove(liserner);
	}
	
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
	
	private CourseServerMgr(){
		mSetOfGetCourseFromServerObserver = new HashSet<IGetCourseFromServerListener>();
		mSetOfJoinCourseToServerObserver = new HashSet<CourseServerMgr.IJoinCourseToServerListener>();
		mSetOfDeleteCourseToServerObserver = new HashSet<CourseServerMgr.IDeleteCourseToServerListener>();
		mSetOfUpdateTrainPlanObserver = new HashSet<CourseServerMgr.IUpdateTrainPlanListener>();
		mSetOfDownloadTrainPlanObserver = new HashSet<IDownloadTrainPlanLister>();
		mSetOfUpdateRecordServer = new HashSet<CourseServerMgr.IUpdateRecordListener>();
		mSetOfRequestTotalDataObserver = new HashSet<CourseServerMgr.IRequestTotalDataListener>();
		mSetOfRequestDetailDataObserver = new HashSet<CourseServerMgr.IRequestDetailDataListener>();
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
				handleGetCourseInfoResult(result);
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

	private void handleGetCourseInfoResult(String result) {
		Gson gson = new Gson();
		Type typeServerCourseData = new TypeToken<ServerCourseData>(){}.getType();
		ServerCourseData serverCourseData = gson.fromJson(result, typeServerCourseData);
		
		if(serverCourseData == null){
			Log.e(TAG, "handleGetCourseInfoResult, serverCourseData is null");
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
				handleGetActionInfoResult(result);
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

	private void handleGetActionInfoResult(String result) {
		Gson gson = new Gson();
		Type typeServerActionData = new TypeToken<ServerActionData>(){}.getType();
		ServerActionData serverActionData = gson.fromJson(result, typeServerActionData);
		
		if(serverActionData == null){
			Log.e(TAG, "handleGetActionInfoResult, serverActionData is null");
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
	
	/**
	  * @Method: joinCourseToServer
	  * @Description: 参加课程
	  * @param uid
	  * @param courseId
	  * @param startDate	
	  * 返回类型：void 
	  */
	public void joinCourseToServer(String uid, String courseId, String startDate){
		RequestParams params = new RequestParams(ConstServer.URL_JOIN_COURSE);
		params.addParameter(Const.DB_KEY_UID, uid);
		params.addParameter(Const.DB_KEY_COURSE_ID, courseId);
		params.addParameter(Const.DB_KEY_START_DATE, startDate);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleJoinCourseResult(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				Log.e(TAG, "joinCourseToServer, onError, throwable is : " + throwable);
				
				notifyJoinCourseFail();
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}

	private void handleJoinCourseResult(String result) {
		JSONObject resultJsonObject = null;
		try {
			resultJsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(resultJsonObject == null){
			Log.e(TAG, "handleJoinCourseResult, resultJsonObject is null");
			
			notifyJoinCourseFail();
			return;
		}
		int resultCode = resultJsonObject.optInt("result_code");
		Log.i(TAG, "handleJoinCourseResult, resultCode = " + resultCode);
		if(resultCode == 0){
			notifyJoinCourseSuc();
		}else{
			notifyJoinCourseFail();
		}
		
	}
	
	private void notifyJoinCourseSuc(){
		synchronized (mSetOfJoinCourseToServerObserver) {
			Iterator<IJoinCourseToServerListener> it = mSetOfJoinCourseToServerObserver.iterator();
			while( it.hasNext() ){
				IJoinCourseToServerListener observer = it.next();
				observer.onJoinCourseToServerSuc();
			}
		}
	}
	
	private void notifyJoinCourseFail() {
		synchronized (mSetOfJoinCourseToServerObserver) {
			Iterator<IJoinCourseToServerListener> it = mSetOfJoinCourseToServerObserver.iterator();
			while( it.hasNext() ){
				IJoinCourseToServerListener observer = it.next();
				observer.onJoinCourseToServerFail();
			}
		}
	}
	
	public void deleteCourseToServer(String uid, String courseId, String startDate){
		RequestParams params = new RequestParams(ConstServer.URL_DELETE_COURSE);
		params.addParameter(Const.DB_KEY_UID, uid);
		params.addParameter(Const.DB_KEY_COURSE_ID, courseId);
		params.addParameter(Const.DB_KEY_START_DATE, startDate);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleDeleteCourseResult(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				Log.e(TAG, "deleteCourseToServer, onError, throwable is : " + throwable);
				
				notifyDeleteCourseFail();
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}

	protected void handleDeleteCourseResult(String result) {
		JSONObject resultJsonObject = null;
		try {
			resultJsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(resultJsonObject == null){
			Log.e(TAG, "handleDeleteCourseResult, resultJsonObject is null");
			
			notifyDeleteCourseFail();
			return;
		}
		
		int resultCode = resultJsonObject.optInt("result_code");
		Log.i(TAG, "handleDeleteCourseResult, resultCode = " + resultCode);
		if(resultCode == 0){
			notifyDeleteCourseSuc();
		}else{
			notifyDeleteCourseFail();
		}
	}
	
	private void notifyDeleteCourseSuc(){
		synchronized (mSetOfDeleteCourseToServerObserver) {
			Iterator<IDeleteCourseToServerListener> it = mSetOfDeleteCourseToServerObserver.iterator();
			while( it.hasNext() ){
				IDeleteCourseToServerListener observer = it.next();
				observer.onDeleteCourseToServerSuc();
			}
		}
	}
	
	private void notifyDeleteCourseFail() {
		synchronized (mSetOfDeleteCourseToServerObserver) {
			Iterator<IDeleteCourseToServerListener> it = mSetOfDeleteCourseToServerObserver.iterator();
			while( it.hasNext() ){
				IDeleteCourseToServerListener observer = it.next();
				observer.onDeleteCourseToServerFail();
			}
		}
	}
	
	/**
	  * @Method: uploadTrainPlan
	  * @Description: 上传定制计划
	  * 返回类型：void 
	  */
	public void uploadTrainPlan(String uid, String courseId, String course_detail, int progress, String day_progress){
		RequestParams params = new RequestParams(ConstServer.URL_UPLOAD_TRAIN_PLAN);
		params.addParameter(Const.DB_KEY_UID, uid);
		params.addParameter(Const.DB_KEY_COURSE_ID, courseId);
		params.addParameter(Const.DB_KEY_COURSE_DETAIL, course_detail);
		params.addParameter(Const.DB_KEY_PROGRESS, progress);
		params.addParameter(Const.DB_KEY_DAY_PROGRESS, day_progress);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleUploadTrainPlanResult(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				Log.e(TAG, "uploadTrainPlan, onError, throwable is : " + throwable);
				
				notifyUploadTrainPlanFail();
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}

	protected void handleUploadTrainPlanResult(String result) {
		JSONObject resultJsonObject = null;
		try {
			resultJsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(resultJsonObject == null){
			Log.e(TAG, "handleUploadTrainPlanResult, resultJsonObject is null");
			
			notifyUploadTrainPlanFail();
			return;
		}
		
		int resultCode = resultJsonObject.optInt("result_code");
		Log.i(TAG, "handleUploadTrainPlanResult, resultCode = " + resultCode);
		if(resultCode == 0){
			notifyUploadTrainPlanSuc();
		}else{
			notifyUploadTrainPlanFail();
		}
	}
	
	private void notifyUploadTrainPlanSuc(){
		synchronized (mSetOfUpdateTrainPlanObserver) {
			Iterator<IUpdateTrainPlanListener> it = mSetOfUpdateTrainPlanObserver.iterator();
			while( it.hasNext() ){
				IUpdateTrainPlanListener observer = it.next();
				observer.onUpdateTrainPlanSuc();
			}
		}
	}
	
	private void notifyUploadTrainPlanFail() {
		synchronized (mSetOfUpdateTrainPlanObserver) {
			Iterator<IUpdateTrainPlanListener> it = mSetOfUpdateTrainPlanObserver.iterator();
			while( it.hasNext() ){
				IUpdateTrainPlanListener observer = it.next();
				observer.onUpdateTrainPlanFail();
			}
		}
	}
	
	/**
	  * @Method: downloadTrainPlan
	  * @Description: 同步训练计划
	  * @param uid
	  * 返回类型：void
	  */
	public void downloadTrainPlan(final String uid){
		RequestParams params = new RequestParams(ConstServer.URL_DOWNLOAD_TRAIN_PLAN);
		params.addParameter(Const.DB_KEY_UID, uid);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleDownloadTrainPlanResult(result, uid);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				Log.e(TAG, "downloadTrainPlan, onError, throwable is : " + throwable);
				
				notifyDownloadTrainPlanFail();
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}

	private void handleDownloadTrainPlanResult(String result, String uid) {
		JSONObject resultJsonObject = null;
		try {
			resultJsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(resultJsonObject == null){
			Log.e(TAG, "handleDownloadTrainPlanResult, resultJsonObject is null");
			
			notifyDownloadTrainPlanFail();
			return;
		}
		
		int resultCode = resultJsonObject.optInt("result_code");
		Log.i(TAG, "handleDownloadTrainPlanResult, resultCode = " + resultCode);
		if(resultCode == 0){
			JSONArray courseJsonArray;
			try {
				courseJsonArray = resultJsonObject.getJSONArray("data");
				if(courseJsonArray != null){
					handleSaveTrainPlanData(courseJsonArray, uid);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}else{
			notifyDownloadTrainPlanFail();
		}
	}

	private void handleSaveTrainPlanData(JSONArray courseJsonArray, String uid) {
		Gson gson = new Gson();
		Type typeMyCourse = new TypeToken<MyCourse>(){}.getType();
		try {
			int courseSize = courseJsonArray.length();
			for(int i=0; i<courseSize; i++){
				String jsonCourseMyCourse = courseJsonArray.get(i).toString();
				MyCourse myCourse = gson.fromJson(jsonCourseMyCourse, typeMyCourse);
				
				MyCourseDao.getInstance().saveMyCourseToDb(Globle.gApplicationContext, uid, myCourse);
			}
			
			notifyDownloadTrainPlanSuc();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void notifyDownloadTrainPlanSuc(){
		synchronized (mSetOfDownloadTrainPlanObserver) {
			Iterator<IDownloadTrainPlanLister> it = mSetOfDownloadTrainPlanObserver.iterator();
			while( it.hasNext() ){
				IDownloadTrainPlanLister observer = it.next();
				observer.onDownloadTrainPlanSuc();
			}
		}
	}
	
	private void notifyDownloadTrainPlanFail() {
		synchronized (mSetOfDownloadTrainPlanObserver) {
			Iterator<IDownloadTrainPlanLister> it = mSetOfDownloadTrainPlanObserver.iterator();
			while( it.hasNext() ){
				IDownloadTrainPlanLister observer = it.next();
				observer.onDownloadTrainPlanFail();
			}
		}
	}
	
	/**
	  * @Method: updateRecord
	  * @Description: 上传本次训练数据
	  * @param uid
	  * @param recordData	
	  * 返回类型：void 
	  */
	public void updateRecord( String uid, String recordData ){
		RequestParams params = new RequestParams(ConstServer.URL_RECORD_DATA);
		params.addParameter(Const.DB_KEY_UID, uid);
		params.addParameter(Const.DB_KEY_TRAIN_DATA, recordData);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleUpdateRecordResult(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				Log.e(TAG, "uploadTrainPlan, onError, throwable is : " + throwable);
				
				notifyUpdateRecordFail();
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}

	protected void handleUpdateRecordResult(String result) {
		JSONObject resultJsonObject = null;
		try {
			resultJsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(resultJsonObject == null){
			Log.e(TAG, "handleUpdateRecordResult, resultJsonObject is null");
			
			notifyUpdateRecordFail();
			return;
		}
		
		int resultCode = resultJsonObject.optInt("result_code");
		Log.i(TAG, "handleUpdateRecordResult, resultCode = " + resultCode);
		if(resultCode == 0){
			notifyUpdateRecordSuc();
		}else{
			notifyUpdateRecordFail();
		}
	}
	
	private void notifyUpdateRecordSuc(){
		synchronized (mSetOfUpdateRecordServer) {
			Iterator<IUpdateRecordListener> it = mSetOfUpdateRecordServer.iterator();
			while( it.hasNext() ){
				IUpdateRecordListener observer = it.next();
				observer.onUpdateRecordSuc();
			}
		}
	}
	
	private void notifyUpdateRecordFail(){
		synchronized (mSetOfUpdateRecordServer) {
			Iterator<IUpdateRecordListener> it = mSetOfUpdateRecordServer.iterator();
			while( it.hasNext() ){
				IUpdateRecordListener observer = it.next();
				observer.onUpdateRecordSuc();
			}
		}
	}
	
	/**
	  * @Method: requestTotalData
	  * @Description: 请求总数据
	  * @param uid	
	  * 返回类型：void 
	  */
	public void requestTotalData( final String uid ){
		RequestParams params = new RequestParams(ConstServer.URL_REQUEST_TOTAL_DATA);
		params.addParameter(Const.DB_KEY_UID, uid);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleRequestTotalDataResult(result, uid);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				Log.e(TAG, "uploadTrainPlan, onError, throwable is : " + throwable);
				
				notifyRequestTotalDataFail();
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}

	private void handleRequestTotalDataResult(String result, String uid) {
		JSONObject resultJsonObject = null;
		try {
			resultJsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(resultJsonObject == null){
			Log.e(TAG, "handleRequestTotalDataResult, resultJsonObject is null");
			
			notifyRequestTotalDataFail();
			return;
		}
		
		int resultCode = resultJsonObject.optInt("result_code");
		Log.i(TAG, "handleRequestTotalDataResult, resultCode = " + resultCode);
		if(resultCode == 0){
			JSONObject totalDataObject;
			try {
				totalDataObject = resultJsonObject.getJSONObject("data");
				if(totalDataObject != null){
					handleSaveTotalData(totalDataObject, uid);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}else{
			notifyRequestTotalDataFail();
		}
	}

	private void handleSaveTotalData(JSONObject totalDataObject, String uid) {
		Gson gson = new Gson();
		Type typeTotalRecord = new TypeToken<TotalRecord>(){}.getType();
		String strTotalRecord = totalDataObject.toString();
		TotalRecord totalRecord = gson.fromJson(strTotalRecord, typeTotalRecord);
		
		MyTotalRecordDao.getInstance().saveMyTotalRecordToDb(Globle.gApplicationContext, totalRecord, uid);
		
		notifyRequestTotalDataSuc(totalRecord);
	}

	private void notifyRequestTotalDataSuc(TotalRecord totalRecord) {
		synchronized (mSetOfRequestTotalDataObserver) {
			Iterator<IRequestTotalDataListener> it = mSetOfRequestTotalDataObserver.iterator();
			while( it.hasNext() ){
				IRequestTotalDataListener observer = it.next();
				observer.onRequestTotalDataSuc(totalRecord);
			}
		}
	}
	
	private void notifyRequestTotalDataFail() {
		synchronized (mSetOfRequestTotalDataObserver) {
			Iterator<IRequestTotalDataListener> it = mSetOfRequestTotalDataObserver.iterator();
			while( it.hasNext() ){
				IRequestTotalDataListener observer = it.next();
				observer.onRequestTotalDataFail();
			}
		}
	}
	
	/**
	  * @Method: requestDetailData
	  * @Description: 请求详细数据
	  * @param uid	
	  * 返回类型：void 
	  */
	public void requestDetailData( final String uid, final String strStartDate, String strEndDate ){
		RequestParams params = new RequestParams(ConstServer.URL_REQUEST_DETAIL_DATA);
		params.addParameter(Const.DB_KEY_UID, uid);
		params.addParameter(Const.DB_KEY_START_DATE, strStartDate);
		params.addParameter(Const.DB_KEY_END_DATE, strEndDate);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleRequestDetailDataResult(result, uid, strStartDate);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				Log.e(TAG, "uploadTrainPlan, onError, throwable is : " + throwable);
				
				notifyRequestDetailDataFail();
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}

	private void handleRequestDetailDataResult(String result, String uid, String strStartDate) {
		JSONObject resultJsonObject = null;
		try {
			resultJsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(resultJsonObject == null){
			Log.e(TAG, "handleRequestDetailDataResult, resultJsonObject is null");
			
			notifyRequestTotalDataFail();
			return;
		}
		
		int resultCode = resultJsonObject.optInt("result_code");
		Log.i(TAG, "handleRequestDetailDataResult, resultCode = " + resultCode);
		if(resultCode == 0){
			JSONArray detailDataArray;
			try {
				detailDataArray = resultJsonObject.getJSONArray("data");
				if(detailDataArray != null){
					handleSaveDetailData(detailDataArray, uid, strStartDate);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}else{
			notifyRequestTotalDataFail();
		}
	}

	private void handleSaveDetailData(JSONArray detailDataArray, String uid, String strStartDate) {
		Gson gson = new Gson();
		Type typeTrainRecord = new TypeToken<TrainRecord>(){}.getType();
		try {
			int dataSize = detailDataArray.length();
			for(int i=0; i<dataSize; i++){
				String jsonTrainRecord = detailDataArray.get(i).toString();
				TrainRecord trainRecord = gson.fromJson(jsonTrainRecord, typeTrainRecord);
				
				MyTrainRecordDao.getInstance().saveRecordToDb(Globle.gApplicationContext, uid, trainRecord);
			}
			
			notifyRequestDetailDataSuc(strStartDate);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void notifyRequestDetailDataSuc(String strStartDate) {
		synchronized (mSetOfRequestDetailDataObserver) {
			Iterator<IRequestDetailDataListener> it = mSetOfRequestDetailDataObserver.iterator();
			while( it.hasNext() ){
				IRequestDetailDataListener observer = it.next();
				observer.onRequestDetailDataSuc(strStartDate);
			}
		}
	}
	
	private void notifyRequestDetailDataFail() {
		synchronized (mSetOfRequestDetailDataObserver) {
			Iterator<IRequestDetailDataListener> it = mSetOfRequestDetailDataObserver.iterator();
			while( it.hasNext() ){
				IRequestDetailDataListener observer = it.next();
				observer.onRequestDetailDataFail();
			}
		}
	}
	
}
