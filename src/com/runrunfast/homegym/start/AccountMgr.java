package com.runrunfast.homegym.start;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.utils.Globle;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

public class AccountMgr {
	private final String TAG = "AccountMgr";
	
	private static Object lockObject = new Object();
	private volatile static AccountMgr instance;
	
	private Resources mResources;
	
	private IIdentifyCodeListener iIdentifyCodeListener;
	private IRegisterListener iRegisterListener;
	private ILoginListener iLoginListener;
	private IResetPwdListener iResetPwdListener;
	
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
		
		RequestParams params = new RequestParams(ConstLogin.URL_IDENTIFY);
		params.addQueryStringParameter(ConstLogin.KEY_TYPE, ConstLogin.TYPE_GET_IDENTY_NUM);
		params.addQueryStringParameter(ConstLogin.KEY_PHONE, phoneNum);
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				Log.i(TAG, "getVerifyCode onSuccess, result = " + result + ", threadId = " + Thread.currentThread().getId());
				handleGetIdentifyCodeSuccess(result);
			}

			@Override
			public void onError(Throwable throwable, boolean isOnCallback) {
				Log.e(TAG, "getVerifyCode onError, reason is : " + throwable.getMessage());
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
			String ret = object.optString(ConstLogin.RET);
			boolean ifPhone = object.optBoolean(ConstLogin.IFPHONE);
			boolean status = object.optBoolean(ConstLogin.STATUS);
			
			if( Integer.parseInt(ret) != ConstLogin.RET_OK || !ifPhone || !status ){
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
		
		RequestParams params = new RequestParams(ConstLogin.URL_LOGIN);
		params.addBodyParameter(ConstLogin.KEY_TYPE, ConstLogin.TYPE_REGISTER);
		params.addBodyParameter(ConstLogin.KEY_USER_NAME, phoneNum);
		params.addBodyParameter(ConstLogin.KEY_PASSWORD, pwd);
		params.addBodyParameter(ConstLogin.KEY_IDENTIFY, verifyCode);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleRegisterOnSuccess(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
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
			int ret = object.optInt(ConstLogin.RET);
			switch (ret) {
			case ConstLogin.RET_OK:
				notifyRegisterSuc();
				break;
			case ConstLogin.RET_THIS_PHONE_HAD_BINDED:
				notifyRegisterFail(mResources.getString(R.string.this_phone_had_bonded));
				break;
				
			case ConstLogin.RET_USER_NAME_EXIST:
				notifyRegisterFail(mResources.getString(R.string.this_account_had_exist));
				break;
			case ConstLogin.RET_REGISTE_FAIL:
				notifyRegisterFail(mResources.getString(R.string.register_fail));
				break;
			case ConstLogin.RET_IDENTIFY_TIMEOUT:
				notifyRegisterFail(mResources.getString(R.string.get_verify_code_fail));
				break;
			case ConstLogin.RET_IDENTIFY_CODE_ERR:
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
		RequestParams params = new RequestParams(ConstLogin.URL_LOGIN);
		params.addBodyParameter(ConstLogin.KEY_TYPE, ConstLogin.TYPE_LOGIN);
		params.addBodyParameter(ConstLogin.KEY_USER_NAME, userName);
		params.addBodyParameter(ConstLogin.KEY_PASSWORD, pwd);
		
		x.http().get(params, new Callback.CommonCallback<String>() {

			@Override
			public void onSuccess(String result) {
				handleLoginOnSuccess(result);
			}
			@Override
			public void onError(Throwable throwable, boolean arg1) {
				notifyLoginFail(mResources.getString(R.string.net_error));
			}
			@Override
			public void onCancelled(CancelledException arg0) { }
			@Override
			public void onFinished() { }
		});
	}
	
	private void handleLoginOnSuccess(String result) {
		JSONObject object;
		try {
			object = new JSONObject(result);
			int ret = object.optInt(ConstLogin.RET);
			switch (ret) {
			case ConstLogin.RET_OK:
				notifyLoginSuc();
				break;
			case ConstLogin.RET_PWD_EMPTY:
				notifyRegisterFail(mResources.getString(R.string.username_or_pwd_cant_empty));
				break;
				
			case ConstLogin.RET_LOGIN_FAIL:
				notifyRegisterFail(mResources.getString(R.string.username_not_identified));
				break;
				
			case ConstLogin.RET_USER_NAME_EMPTY:
				notifyRegisterFail(mResources.getString(R.string.this_account_not_exist));
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
		RequestParams params = new RequestParams(ConstLogin.URL_LOGIN);
		params.addBodyParameter(ConstLogin.KEY_TYPE, ConstLogin.TYPE_RESET);
		params.addBodyParameter(ConstLogin.KEY_USER_NAME, userName);
		params.addBodyParameter(ConstLogin.KEY_PASSWORD, pwd);
		params.addBodyParameter(ConstLogin.KEY_IDENTIFY, verifyCode);
		
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
			int ret = object.optInt(ConstLogin.RET);
			switch (ret) {
			case ConstLogin.RET_OK:
				notifyResetSuc();
				break;
			case ConstLogin.RET_RESET_PWD_FAIL:
				notifyRegisterFail(mResources.getString(R.string.username_or_pwd_cant_empty));
				break;
				
			case ConstLogin.RET_IDENTIFY_TIMEOUT:
				notifyRegisterFail(mResources.getString(R.string.verify_code_timeout));
				break;
				
			case ConstLogin.RET_IDENTIFY_CODE_ERR:
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

	public boolean checkLoginLegal(){
		
		return true;
	}
	
}
