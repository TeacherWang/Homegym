package com.runrunfast.homegym.start;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.AccountMgr.IIdentifyCodeListener;
import com.runrunfast.homegym.account.AccountMgr.IRegisterListener;
import com.runrunfast.homegym.utils.Const;

public class RegisterActivity extends Activity implements OnClickListener, TextWatcher{
	
	private static final int MSG_COUNTDOWN = 1;
	private static final int MSG_COUNTDOWN_OVER = 2;
	private static final int COUNTDOWN_LENGTH = 60; // 倒计时
	private static final int COUNTDOWN_INTERVAL = 1000; // 倒计时间隔1s
	
	private View actionBar;
	private ImageView ivBack;
	private Button btnRegisterFinish, btnGetVerifyCode;
	private TextView tvHadAccount;
	private EditText etNum, etVerifyCode, etPwd;
	
	private IRegisterListener iRegisterListener;
	private IIdentifyCodeListener identifyCodeListener;
	
	private ProgressDialog dialog;
	
	private int time = COUNTDOWN_LENGTH;
	
	private String mUsername;
	private String mPwd;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
		initListener();
	}
	
	private Handler mMainHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_COUNTDOWN:
				mMainHandler.postDelayed(runnable, COUNTDOWN_INTERVAL);
				btnGetVerifyCode.setEnabled(false);
				btnGetVerifyCode.setBackgroundResource(R.drawable.bt_get_verify_code_disable_round_corner_rect);
				btnGetVerifyCode.setText(time + "秒后重发");
				break;
				
			case MSG_COUNTDOWN_OVER:
				time = COUNTDOWN_LENGTH;
				btnGetVerifyCode.setEnabled(true);
				btnGetVerifyCode.setBackgroundResource(R.drawable.bt_get_verify_code_round_corner_rect);
				btnGetVerifyCode.setText(R.string.get_verificationcode);
				break;

			default:
				break;
			}
		};
	};
	
	private Runnable runnable = new Runnable() {

		@Override
		public void run() {
			if (time != 0) {
				time--;
				mMainHandler.sendEmptyMessage(MSG_COUNTDOWN);
			} else {
				mMainHandler.sendEmptyMessage(MSG_COUNTDOWN_OVER);
				mMainHandler.removeCallbacks(runnable);
			}
		}
	};
	
	private void initListener() {
		iRegisterListener = new IRegisterListener() {
			
			@Override
			public void onSuccess() {
				handleRegisterSuc();
			}

			@Override
			public void onFail(String reason) {
				dismissDialog();
				Toast.makeText(RegisterActivity.this, reason, Toast.LENGTH_SHORT).show();
			}
		};
		AccountMgr.getInstance().setOnRegisterListener(iRegisterListener);
		
		identifyCodeListener = new IIdentifyCodeListener() {
			
			@Override
			public void onSuccess() {
				mMainHandler.sendEmptyMessage(MSG_COUNTDOWN);
			}
			
			@Override
			public void onFail(String reason) {
				dismissDialog();
				btnGetVerifyCode.setEnabled(true);
				Toast.makeText(RegisterActivity.this, reason, Toast.LENGTH_SHORT).show();
			}
		};
		AccountMgr.getInstance().setOnIdentifyCodeListener(identifyCodeListener);
	}
	
	private void handleRegisterSuc() {
		AccountMgr.getInstance().saveLoginAccount(this, mUsername, mPwd);
		AccountMgr.getInstance().setLoginSuc(this, true);
		dismissDialog();
		jumpToImprovePersonalInfoActivity();
		AccountMgr.getInstance().sendLoginSucBroadcast(this);
		finish();
	}

	private void initView() {
		actionBar = (View)findViewById(R.id.register_action_bar);
		((TextView)actionBar.findViewById(R.id.login_title_text)).setText(R.string.register);
		
		ivBack = (ImageView)actionBar.findViewById(R.id.login_back_img);
		ivBack.setOnClickListener(this);
		
		btnRegisterFinish = (Button)findViewById(R.id.btn_register_finish);
		btnRegisterFinish.setOnClickListener(this);
		
		tvHadAccount = (TextView)findViewById(R.id.register_exist_acount_text);
		tvHadAccount.setOnClickListener(this);
		
		btnGetVerifyCode = (Button)findViewById(R.id.btn_register_get_verfy_code);
		btnGetVerifyCode.setOnClickListener(this);
		
		etNum = (EditText)findViewById(R.id.register_num_edit);
		etVerifyCode = (EditText)findViewById(R.id.register_vevify_code_edit);
		etPwd = (EditText)findViewById(R.id.register_pwd_edit);
		
		etNum.addTextChangedListener(this);
		etVerifyCode.addTextChangedListener(this);
		etPwd.addTextChangedListener(this);
		
		setBtnRegisterEnable();
		setBtnGetVetifyCodeEnable();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_register_finish:
			handleClickRegisterFinish();
			break;

		case R.id.register_exist_acount_text:
			jumpToLoginActivity();
			break;
			
		case R.id.btn_register_get_verfy_code:
			toGetVerifyCode();
			break;
			
		case R.id.login_back_img:
			finish();
			break;
			
		default:
			break;
		}
	}

	private void toGetVerifyCode() {
		String phoneNum = etNum.getText().toString();
		if(TextUtils.isEmpty(phoneNum)){
			Toast.makeText(this, R.string.phone_num_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		btnGetVerifyCode.setEnabled(false);
		AccountMgr.getInstance().getVerifyCode(phoneNum);
	}

	private void jumpToLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	private void handleClickRegisterFinish() {
		mUsername = etNum.getText().toString();
		String strVerifyCode = etVerifyCode.getText().toString();
		mPwd = etPwd.getText().toString();
		if(checkEmpty(mUsername, strVerifyCode, mPwd)){
			Toast.makeText(this, R.string.input_have_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		
		AccountMgr.getInstance().register(mUsername, strVerifyCode, mPwd);
		
		showDialog();
	}

	private void jumpToImprovePersonalInfoActivity() {
		Intent intent = new Intent(this, ImprovePersonalInfoActivity.class);
		intent.putExtra(Const.FROM_LOGIN_REGISTER_TO_HOME_ACTIVITY, Const.FROM_LOGIN_REGISTER_TO_HOME_ACTIVITY_VALUE_CONFIRM);
		startActivity(intent);
		finish();
	}
	
	private boolean checkEmpty(String num, String verifyCode, String pwd){
		if(TextUtils.isEmpty(num) || TextUtils.isEmpty(verifyCode) || TextUtils.isEmpty(pwd)){
			return true;
		}
		return false;
	}

	@Override
	public void afterTextChanged(Editable s) {
		setBtnRegisterEnable();
		setBtnGetVetifyCodeEnable();
	}

	private void setBtnRegisterEnable() {
		if (TextUtils.isEmpty(etNum.getText().toString())
				|| TextUtils.isEmpty(etPwd.getText().toString())
				|| TextUtils.isEmpty(etVerifyCode.getText().toString())) {
			btnRegisterFinish.setEnabled(false);
			btnRegisterFinish.setBackgroundResource(R.drawable.bt_login_disable_round_corner_rect);
		} else {
			btnRegisterFinish.setEnabled(true);
			btnRegisterFinish.setBackgroundResource(R.drawable.bt_login_round_corner_rect);
		}
	}
	
	private void setBtnGetVetifyCodeEnable(){
		if (etNum.getText().toString().length() == 11 && etNum.getText().toString().substring(0, 1).equals("1")) {
			if(time != 60){
				return;
			}
			btnGetVerifyCode.setEnabled(true);
			btnGetVerifyCode.setBackgroundResource(R.drawable.bt_get_verify_code_round_corner_rect);
		} else {
			btnGetVerifyCode.setBackgroundResource(R.drawable.bt_get_verify_code_disable_round_corner_rect);
			btnGetVerifyCode.setEnabled(false);
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) { }
	
	private void showDialog(){
		dialog = new ProgressDialog(this);
		dialog.setMessage(getResources().getString(R.string.please_wait));
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	private void dismissDialog(){
		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AccountMgr.getInstance().removeRegisterListener();
		AccountMgr.getInstance().removeIdentifyCodeListener();
	}

}
