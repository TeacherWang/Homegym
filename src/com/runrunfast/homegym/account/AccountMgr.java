package com.runrunfast.homegym.account;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.utils.ConstServer;
import com.runrunfast.homegym.utils.Globle;
import com.runrunfast.homegym.utils.PrefUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.http.cookie.DbCookieStore;
import org.xutils.http.request.HttpRequest;

import java.io.File;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AccountMgr {
	private final String TAG = "AccountMgr";
	
	private static Object lockObject = new Object();
	private volatile static AccountMgr instance;
	
	private Resources mResources;
	
	public UserInfo mUserInfo;
	
	private IIdentifyCodeListener iIdentifyCodeListener;
	private IRegisterListener iRegisterListener;
	private ILoginListener iLoginListener;
	private IResetPwdListener iResetPwdListener;
	private IPersonalInfoListener iPersonalInfoListener;
	private IUpdateHeadimgListener iUpdateHeadimgListener;
	private IGetPersonalInfoListener iGetPersonalInfoListener;
	
	public interface IGetPersonalInfoListener{
		void onSuccess();
		void onFail();
	}
	
	public interface IUpdateHeadimgListener{
		void onSuccess();
		void onFail();
	}
	
	public interface IPersonalInfoListener{
		void onSuccess();
		void onFail();
	}
	
	public interface IIdentifyCodeListener{
		void onSuccess();
		void onFail(String reason);
	}
	
	public interface IRegisterListener{
		void onSuccess();
		void onFail(String reason);
	}
	
	public interface ILoginListener{
		void onSuccess();
		void onFail(String reason);
	}
	
	public interface IResetPwdListener{
		void onSuccess();
		void onFail(String reason);
	}
	
	public void setOnGetPersonalInfoListener(IGetPersonalInfoListener iGetPersonalInfoListener){
		this.iGetPersonalInfoListener = iGetPersonalInfoListener;
	}
	
	public void setOnUpdateHeadimgListener(IUpdateHeadimgListener iUpdateHeadimgListener){
		this.iUpdateHeadimgListener = iUpdateHeadimgListener;
	}
	
	public void setOnPersonalInfoListener(IPersonalInfoListener iPersionalInfoListener){
		this.iPersonalInfoListener = iPersionalInfoListener;
	}
	
	public void setOnResetPwdListener(IResetPwdListener resetPwdListener){
		this.iResetPwdListener = resetPwdListener;
	}
	
	public void removeResetPwdListener(){
		this.iResetPwdListener = null;
	}
	
	public void setOnLoginListener(ILoginListener loginListener){
		this.iLoginListener = loginListener;
	}
	
	public void removeLoginListener(){
		this.iLoginListener = null;
	}
	
	public void setOnRegisterListener(IRegisterListener registerListener){
		this.iRegisterListener = registerListener;
	}
	
	public void removeRegisterListener(){
		this.iRegisterListener = null;
	}
	
	public void setOnIdentifyCodeListener(IIdentifyCodeListener identifyCodeListener){
		this.iIdentifyCodeListener = identifyCodeListener;
	}
	
	public void removeIdentifyCodeListener(){
		this.iIdentifyCodeListener = null;
	}
	
	public static AccountMgr getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new AccountMgr();
				}
			}
		}
		return instance;
	}
	
	private AccountMgr(){
		mResources = Globle.gApplicationContext.getResources();
		loadUserInfo();
	}
	
	public void loadUserInfo(){
		String accountId = PrefUtils.getAccount(Globle.gApplicationContext);
		if(TextUtils.isEmpty(accountId)){
			Log.d(TAG, "loadUserInfo, accountId is empty");
			mUserInfo = null;
			return;
		}
		
		if(mUserInfo == null){
			mUserInfo = new UserInfo();
		}
		
		String strNickName = PrefUtils.getNickname(Globle.gApplicationContext);
		String strSex = PrefUtils.getSex(Globle.gApplicationContext);
		String strBirthday = PrefUtils.getBirthday(Globle.gApplicationContext);
		String strWeight = PrefUtils.getWeight(Globle.gApplicationContext);
		String strHeight = PrefUtils.getHeight(Globle.gApplicationContext);
		
		Log.i(TAG, "loadUserInfo, nickname = " + strNickName + ", sex = " + strSex + 
				   ", birthday = " + strBirthday + ", weight = " + strWeight + ", height = " + strHeight);
		
		mUserInfo.strAccountId = accountId;
		mUserInfo.strNickName = strNickName;
		mUserInfo.strSex = strSex;
		mUserInfo.strBirthday = strBirthday;
		mUserInfo.strWeight = strWeight;
		mUserInfo.strHeight = strHeight;
	}
	
	public List<String> getHeightList(){
		return Arrays.asList(mResources.getStringArray(R.array.height));
	}
	
	public List<String> getWeightList(){
		return Arrays.asList(mResources.getStringArray(R.array.weight));
	}
	
	public List<String> getSexList(){
		return Arrays.asList(mResources.getStringArray(R.array.sex));
	}
	
	public List<String> getYearList(){
		List<String> yearList = new ArrayList<String>();
		
		int startYear = Calendar.getInstance().get(Calendar.YEAR) - 70;
		int endYear = Calendar.getInstance().get(Calendar.YEAR);
		
		for(int i=0; i<71; i++){
			yearList.add(String.valueOf(startYear + i));
		}
		
		return yearList;
	}
	
	public List<String> getCountList(){
		List<String> countList = new ArrayList<String>();
		
		int count = 1;
		for(int i=0; i<99; i++){
			countList.add(String.valueOf(count));
			count++;
		}
		
		return countList;
	}
	
	public List<String> getToolWeightList(){
		List<String> toolWeightList = new ArrayList<String>();
		
		int toolWeight = 5;
		
		while(toolWeight <= 300){
			toolWeightList.add(String.valueOf(toolWeight));
			
			toolWeight = toolWeight + 5;
		}
		
		return toolWeightList;
	}
	
	public List<String> getMonthList(){
		return Arrays.asList(mResources.getStringArray(R.array.month));
	}
	
	public List<String> getDay31List(){
		return Arrays.asList(mResources.getStringArray(R.array.day_31));
	}
	
	public List<String> getDay30List(){
		return Arrays.asList(mResources.getStringArray(R.array.day_30));
	}
	
	public List<String> getDay29List(){
		return Arrays.asList(mResources.getStringArray(R.array.day_29));
	}
	
	public List<String> getDay28List(){
		return Arrays.asList(mResources.getStringArray(R.array.day_28));
	}
	
	/**
	  * @Method: getVerifyCode
	  * @Description: 获取验证码
	  * @param phoneNum	
	  * 返回类型：void 
	  */
	public void getVerifyCode(String phoneNum){
		if(TextUtils.isEmpty(phoneNum)){
			Log.e(TAG, "getVerifyCode, phoneNum is empty");
			return;
		}
		
		RequestParams params = new RequestParams(ConstServer.URL_IDENTIFY);
		params.addQueryStringParameter(ConstServer.KEY_TYPE, ConstServer.TYPE_GET_IDENTY_NUM);
		params.addQueryStringParameter(ConstServer.KEY_PHONE, phoneNum);
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				Log.i(TAG, "getVerifyCode onSuccess, result = " + result + ", threadId = " + Thread.currentThread().getId());
				handleGetIdentifyCodeSuccess(result);
			}

			@Override
			public void onError(Throwable throwable, boolean isOnCallback) {
				Log.e(TAG, "getVerifyCode onError, reason is : " + throwable);
				
				notifyGetIdentifyCodeFail(mResources.getString(R.string.get_verify_code_fail));
			}
			
			@Override
			public void onCancelled(CancelledException cex) { }
			@Override
			public void onFinished() { }
		});
	}
	
	private void handleGetIdentifyCodeSuccess(String result) {
		JSONObject object;
		try {
			object = new JSONObject(result);
			String ret = object.optString(ConstServer.RET);
			boolean ifPhone = object.optBoolean(ConstServer.IFPHONE);
			boolean status = object.optBoolean(ConstServer.STATUS);
			
			if( Integer.parseInt(ret) != ConstServer.RET_OK || !ifPhone || !status ){
				notifyGetIdentifyCodeFail(mResources.getString(R.string.get_verify_code_fail));
				return;
			}
			notifyGetIdentifyCodeSuc();
		} catch (JSONException e) {
			e.printStackTrace();
			notifyGetIdentifyCodeFail(mResources.getString(R.string.get_verify_code_fail));
		}
	}
	
	private void notifyGetIdentifyCodeFail(String reason){
		if(iIdentifyCodeListener != null){
			iIdentifyCodeListener.onFail(reason);
		}
	}
	
	private void notifyGetIdentifyCodeSuc(){
		if(iIdentifyCodeListener != null){
			iIdentifyCodeListener.onSuccess();
		}
	}
	
	/**
	  * @Method: finishRegister
	  * @Description: 完成注册
	  * @param phoneNum
	  * @param verifyCode
	  * @param pwd	
	  * 返回类型：void 
	  */
	public void register(String phoneNum, String verifyCode, String pwd){
		if(TextUtils.isEmpty(phoneNum) || TextUtils.isEmpty(verifyCode) || TextUtils.isEmpty(pwd)){
			Log.e(TAG, "finishRegister, info empty");
			return;
		}
		
		RequestParams params = new RequestParams(ConstServer.URL_LOGIN);
		params.addParameter(ConstServer.KEY_TYPE, ConstServer.TYPE_REGISTER);
		params.addParameter(ConstServer.KEY_USER_NAME, phoneNum);
		params.addParameter(ConstServer.KEY_PASSWORD, pwd);
		params.addParameter(ConstServer.KEY_IDENTIFY, verifyCode);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleRegisterOnSuccess(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				Log.e(TAG, "register, onError, throwable is : " + throwable);
				
				notifyRegisterFail(mResources.getString(R.string.net_error));
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}
	
	private void handleRegisterOnSuccess(String result) {
		JSONObject object;
		try {
			object = new JSONObject(result);
			int ret = object.optInt(ConstServer.RET);
			switch (ret) {
			case ConstServer.RET_OK:
				notifyRegisterSuc();
				break;
			case ConstServer.RET_THIS_PHONE_HAD_BINDED:
				notifyRegisterFail(mResources.getString(R.string.this_phone_had_bonded));
				break;
				
			case ConstServer.RET_USER_NAME_EXIST:
				notifyRegisterFail(mResources.getString(R.string.this_account_had_exist));
				break;
			case ConstServer.RET_REGISTE_FAIL:
				notifyRegisterFail(mResources.getString(R.string.register_fail));
				break;
			case ConstServer.RET_IDENTIFY_TIMEOUT:
				notifyRegisterFail(mResources.getString(R.string.get_verify_code_fail));
				break;
			case ConstServer.RET_IDENTIFY_CODE_ERR:
				notifyRegisterFail(mResources.getString(R.string.verify_code_err));
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			notifyRegisterFail(mResources.getString(R.string.register_fail));
		}
	}

	private void notifyRegisterSuc(){
		if(this.iRegisterListener != null){
			this.iRegisterListener.onSuccess();
		}
	}
	
	private void notifyRegisterFail(String reason){
		if(this.iRegisterListener != null){
			this.iRegisterListener.onFail(reason);
		}
	}
	
	public void login(String userName, String pwd){
		
		RequestParams params = new RequestParams(ConstServer.URL_LOGIN);
		params.addParameter(ConstServer.KEY_TYPE, ConstServer.TYPE_LOGIN);
		params.addParameter(ConstServer.KEY_USER_NAME, userName);
		params.addParameter(ConstServer.KEY_PASSWORD, pwd);
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				saveCookies();
				handleLoginOnSuccess(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				Log.e(TAG, "login, onError, throwable is : " + throwable);
				
				notifyLoginFail(mResources.getString(R.string.net_error));
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}
	
	private void saveCookies() {
		DbCookieStore inStore = DbCookieStore.INSTANCE;
		List<HttpCookie> cookies = inStore.getCookies();
		for(HttpCookie cookie : cookies){
			String name = cookie.getName();
			String value = cookie.getValue();
			Log.d(TAG, "saveCookies, name = " + name + ", value = " + value);
			if("JSESSIONID".equals(name)){
				PrefUtils.setCookie(Globle.gApplicationContext, value);
				break;
			}
		}
	}

	private void handleLoginOnSuccess(String result) {
		JSONObject object;
		try {
			object = new JSONObject(result);
			int ret = object.optInt(ConstServer.RET);
			switch (ret) {
			case ConstServer.RET_OK:
				notifyLoginSuc();
				break;
			case ConstServer.RET_PWD_EMPTY:
				notifyLoginFail(mResources.getString(R.string.username_or_pwd_cant_empty));
				break;
				
			case ConstServer.RET_LOGIN_FAIL:
				notifyLoginFail(mResources.getString(R.string.username_not_identified));
				break;
				
			case ConstServer.RET_USER_NAME_EMPTY:
				notifyLoginFail(mResources.getString(R.string.this_account_not_exist));
				break;
			default:
				notifyLoginFail(mResources.getString(R.string.login_fail));
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			notifyRegisterFail(mResources.getString(R.string.login_fail));
		}
	}

	private void notifyLoginFail(String reason) {
		if(iLoginListener != null){
			iLoginListener.onFail(reason);
		}
	}
	
	private void notifyLoginSuc(){
		if(iLoginListener != null){
			iLoginListener.onSuccess();
		}
	}

	public void resetPwd(String userName, String verifyCode, String pwd){
		RequestParams params = new RequestParams(ConstServer.URL_LOGIN);
		params.addParameter(ConstServer.KEY_TYPE, ConstServer.TYPE_RESET);
		params.addParameter(ConstServer.KEY_USER_NAME, userName);
		params.addParameter(ConstServer.KEY_PASSWORD, pwd);
		params.addParameter(ConstServer.KEY_IDENTIFY, verifyCode);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleResetSuccess(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				notifyResetFail(mResources.getString(R.string.net_error));
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}
	
	private void handleResetSuccess(String result) {
		JSONObject object;
		try {
			object = new JSONObject(result);
			int ret = object.optInt(ConstServer.RET);
			switch (ret) {
			case ConstServer.RET_OK:
				notifyResetSuc();
				break;
			case ConstServer.RET_RESET_PWD_FAIL:
				notifyRegisterFail(mResources.getString(R.string.username_or_pwd_cant_empty));
				break;
				
			case ConstServer.RET_IDENTIFY_TIMEOUT:
				notifyRegisterFail(mResources.getString(R.string.verify_code_timeout));
				break;
				
			case ConstServer.RET_IDENTIFY_CODE_ERR:
				notifyRegisterFail(mResources.getString(R.string.verify_code_err));
				break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			notifyRegisterFail(mResources.getString(R.string.reset_pwd_fail));
		}
	}
	
	private void notifyResetSuc(){
		if(iResetPwdListener != null){
			iResetPwdListener.onSuccess();
		}
	}

	private void notifyResetFail(String reason) {
		if(iResetPwdListener != null){
			iResetPwdListener.onFail(reason);
		}
	}

	public void updatePersonalInfo(UserInfo userInfo){
		RequestParams params = new RequestParams(ConstServer.URL_UPDATE_PERSONAL_INFO);
		String cookie = PrefUtils.getCookie(Globle.gApplicationContext);
		if(cookie != null){
			params.addHeader("Cookie", "JSESSIONID=" + cookie);
		}
		params.addParameter(ConstServer.PERSONAL_USER_NAME, userInfo.strAccountId);
		params.addParameter(ConstServer.PERSONAL_NICK_NAME, userInfo.strNickName);
		params.addParameter(ConstServer.PERSONAL_SEX, userInfo.strSex);
		params.addParameter(ConstServer.PERSONAL_HEIGHT, Integer.parseInt(userInfo.strHeight));
		params.addParameter(ConstServer.PERSONAL_WEIGHT, Integer.parseInt(userInfo.strWeight));
		params.addParameter(ConstServer.PERSONAL_BIRTHDAY, userInfo.strBirthday);
		params.addParameter(ConstServer.PERSONAL_LEVEL, 0);
		params.addParameter(ConstServer.PERSONAL_MINUTES, 0);
		params.addParameter(ConstServer.PERSONAL_HRMAX, 0.0);
		params.addParameter(ConstServer.PERSONAL_HEADID, 0);
		params.addParameter(ConstServer.PERSONAL_IS_WECHAT, 0);
		params.addParameter(ConstServer.PERSONAL_WECHAT_HEAD_URL, "");
		params.addParameter(ConstServer.PERSONAL_USER_CITY, "");
		params.addParameter(ConstServer.PERSONAL_USER_DECLARATION, "");
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleUpdateUserInfoSuccess(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				notifyUpdateUserInfoFail();
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}
	
	private void handleUpdateUserInfoSuccess(String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			int retCode = (int) jsonObject.get("ret");
			if(retCode == 1){
				notifyUpdateUserInfoSuc();
			}else if(retCode == -1){
				UserInfo userInfo = AccountMgr.getInstance().mUserInfo;
				AccountMgr.getInstance().login(userInfo.strAccountId, PrefUtils.getPwd(Globle.gApplicationContext));
				
				notifyUpdateUserInfoFail();
			}else{
				notifyUpdateUserInfoFail();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void notifyUpdateUserInfoSuc() {
		if(iPersonalInfoListener != null){
			iPersonalInfoListener.onSuccess();
		}
	}
	
	private void notifyUpdateUserInfoFail() {
		if(iPersonalInfoListener != null){
			iPersonalInfoListener.onFail();
		}
	}
	
	public void updateHeadImg(File headImgFile){
		RequestParams params = new RequestParams(ConstServer.URL_UPDATE_HEADIMG);
		String cookie = PrefUtils.getCookie(Globle.gApplicationContext);
		if(cookie != null){
			params.addHeader("Cookie", "JSESSIONID=" + cookie);
		}
		params.setMultipart(true);
		params.addBodyParameter("type", "insertheadpicture");
		params.addBodyParameter(UserInfo.IMAGE_FILE_LOCATION_TEMP.replace("/", ""), headImgFile);
		
		x.http().post(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleUpdateHeadImgSuccess(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				notifyUpdateHeadImgFail();
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}
	
	private void handleUpdateHeadImgSuccess(String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			int retCode = (int) jsonObject.get("ret");
			if(retCode == 1){
				notifyUpdateHeadImgSuc();
			}else if(retCode == -1){
				UserInfo userInfo = AccountMgr.getInstance().mUserInfo;
				AccountMgr.getInstance().login(userInfo.strAccountId, PrefUtils.getPwd(Globle.gApplicationContext));
				
				notifyUpdateHeadImgFail();
			}else{
				notifyUpdateHeadImgFail();
			}
		} catch (JSONException e) {
			notifyUpdateHeadImgFail();
			e.printStackTrace();
		}
	}

	private void notifyUpdateHeadImgSuc() {
		if(iUpdateHeadimgListener != null){
			iUpdateHeadimgListener.onSuccess();
		}
	}

	private void notifyUpdateHeadImgFail() {
		if(iUpdateHeadimgListener != null){
			iUpdateHeadimgListener.onFail();
		}
	}

	public void getPersonalInfo(String uid){
		RequestParams params = new RequestParams(ConstServer.URL_GET_PERSONAL_INFO);
		String cookie = PrefUtils.getCookie(Globle.gApplicationContext);
		if(cookie != null){
			params.addHeader("Cookie", "JSESSIONID=" + cookie);
		}
		params.addParameter(ConstServer.PERSONAL_USER_NAME, uid);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleGetPersonalInfoSuccess(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				notifyGetPersonalInfoFail();
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}
	
	private void handleGetPersonalInfoSuccess(String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			int retCode = (int) jsonObject.get("ret");
			if(retCode == 2){
				handleGetPersonalInfoResult(jsonObject);
			}else if(retCode == -1){
				UserInfo userInfo = AccountMgr.getInstance().mUserInfo;
				AccountMgr.getInstance().login(userInfo.strAccountId, PrefUtils.getPwd(Globle.gApplicationContext));
				
				notifyGetPersonalInfoFail();
			}else{
				notifyGetPersonalInfoFail();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void handleGetPersonalInfoResult(JSONObject jsonObject) {
		try {
			JSONArray jsonArray = jsonObject.getJSONArray("content");
			JSONObject userJsonObject = jsonArray.getJSONObject(0);
			String userBirthday = userJsonObject.getString("userBirthday");
			String userHeight = userJsonObject.getString("userHeight");
			String userWeight = userJsonObject.getString("userWeight");
			String userSex = userJsonObject.getString("userSex");
			String nickName = userJsonObject.getString("userNickName");
			
			saveAccountInfo(nickName, userSex, userBirthday, userWeight, userHeight, "");
			loadUserInfo();
			
			notifyGetPersonalInfoSuc();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void notifyGetPersonalInfoSuc() {
		if(iGetPersonalInfoListener != null){
			iGetPersonalInfoListener.onSuccess();
		}
	}
	
	private void notifyGetPersonalInfoFail() {
		if(iGetPersonalInfoListener != null){
			iGetPersonalInfoListener.onFail();
		}
	}

	public void saveLoginAccount(Context context, String userName){
		PrefUtils.setAccount(context, userName);
		loadUserInfo();
	}
	
	public String getAccount(Context context){
		return PrefUtils.getAccount(context);
	}
	
	public void clearAccount(Context context){
		PrefUtils.clearAccount(context);
	}
	
	public void setLoginSuc(Context context, boolean suc){
		PrefUtils.setLoginSuc(context, suc);
	}
	
	public boolean getLoginSuc(Context context){
		return PrefUtils.getLoginSuc(context);
	}
	
	public void saveAccountInfo(String nickname, String sex, String birthday, String weight, String height, String city){
		PrefUtils.setNickname(Globle.gApplicationContext, nickname);
		PrefUtils.setSex(Globle.gApplicationContext, sex);
		PrefUtils.setBirthday(Globle.gApplicationContext, birthday);
		PrefUtils.setWeight(Globle.gApplicationContext, weight);
		PrefUtils.setHeight(Globle.gApplicationContext, height);
		PrefUtils.setCity(Globle.gApplicationContext, city);
	}
	
	public void sendLoginSucBroadcast(Context context){
		Intent intent = new Intent();
		intent.setAction(ConstServer.ACTION_LOGIN_SUC);
		context.sendBroadcast(intent);
	}
	
	public void logout(Context context){
		clearAccount(context);
		setLoginSuc(context, false);
	}
	
	public boolean checkLoginLegal(){
		
		return true;
	}
	
}
