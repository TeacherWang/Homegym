package com.runrunfast.homegym.start;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.runrunfast.homegym.start.AccountMgr.IIdentifyCodeListener;
import com.runrunfast.homegym.start.AccountMgr.ILoginListener;
import com.runrunfast.homegym.start.AccountMgr.IResetPwdListener;

public class ResetPwdActivity extends Activity implements OnClickListener, TextWatcher{
	
	private static final int MSG_COUNTDOWN = 1;
	private static final int MSG_COUNTDOWN_OVER = 2;
	private static final int COUNTDOWN_INTERVAL = 1000; // 倒计时间隔1s
	
	private View resetActionBar;
	private ImageView ivBack;
	private EditText etNum, etVerifyCode, etPwd;
	private Button btnGetVerifyCode, btnResetFinish;
	
	private IResetPwdListener iResetPwdListener;
	private IIdentifyCodeListener identifyCodeListener;
	
	private ProgressDialog dialog;
	private int time = 60;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_pwd);
		
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
		iResetPwdListener = new IResetPwdListener() {
			
			@Override
			public void onSuccess() {
				dismissDialog();
				Toast.makeText(ResetPwdActivity.this, R.string.reset_pwd_suc, Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onFail(String reason) {
				dismissDialog();
				btnGetVerifyCode.setEnabled(true);
				Toast.makeText(ResetPwdActivity.this, reason, Toast.LENGTH_SHORT).show();
			}
		};
		AccountMgr.getInstance().setOnResetPwdListener(iResetPwdListener);
		
		identifyCodeListener = new IIdentifyCodeListener() {
			
			@Override
			public void onSuccess() {
				mMainHandler.sendEmptyMessage(MSG_COUNTDOWN);
			}
			
			@Override
			public void onFail(String reason) {
				dismissDialog();
				btnGetVerifyCode.setEnabled(true);
				Toast.makeText(ResetPwdActivity.this, reason, Toast.LENGTH_SHORT).show();
			}
		};
		AccountMgr.getInstance().setOnIdentifyCodeListener(identifyCodeListener);
	}

	private void initView() {
		resetActionBar = (View)findViewById(R.id.reset_action_bar);
		((TextView)resetActionBar.findViewById(R.id.login_title_text)).setText(R.string.reset_pwd);
		
		ivBack = (ImageView)resetActionBar.findViewById(R.id.login_back_img);
		ivBack.setOnClickListener(this);
		
		etNum = (EditText)findViewById(R.id.rest_num_edit);
		etVerifyCode = (EditText)findViewById(R.id.reset_verfy_edit);
		etPwd = (EditText)findViewById(R.id.reset_pwd_edit);
		
		etNum.addTextChangedListener(this);
		etVerifyCode.addTextChangedListener(this);
		etPwd.addTextChangedListener(this);
		
		btnGetVerifyCode = (Button)findViewById(R.id.btn_reset_get_verfy_code);
		btnResetFinish = (Button)findViewById(R.id.btn_reset_finish);
		
		btnGetVerifyCode.setOnClickListener(this);
		btnResetFinish.setOnClickListener(this);
		
		setBtnResetFinishEnable();
		setBtnGetVetifyCodeEnable();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_back_img:
			finish();
			break;
			
		case R.id.btn_reset_get_verfy_code:
			handleClickGetVerifyCode();
			break;
			
		case R.id.btn_reset_finish:
			handlClickResetFinish();
			break;

		default:
			break;
		}
	}

	private void handlClickResetFinish() {
		String userName = etNum.getText().toString();
		String verifyCode = etVerifyCode.getText().toString();
		String pwd = etPwd.getText().toString();
		
		if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(verifyCode) || TextUtils.isEmpty(pwd)){
			Toast.makeText(this, R.string.input_have_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		
		AccountMgr.getInstance().resetPwd(userName, verifyCode, pwd);
		showDialog();
	}

	private void handleClickGetVerifyCode() {
		String phoneNum = etNum.getText().toString();
		if(TextUtils.isEmpty(phoneNum)){
			Toast.makeText(this, R.string.phone_num_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		btnGetVerifyCode.setEnabled(false);
		AccountMgr.getInstance().getVerifyCode(phoneNum);
	}
	
	private void setBtnGetVetifyCodeEnable(){
		if (etNum.getText().toString().length() == 11
				&& etNum.getText().toString().substring(0, 1).equals("1")) {
			btnGetVerifyCode.setEnabled(true);
			btnGetVerifyCode.setBackgroundResource(R.drawable.bt_get_verify_code_disable_round_corner_rect);
		} else {
			btnGetVerifyCode.setEnabled(false);
			btnGetVerifyCode.setBackgroundResource(R.drawable.bt_get_verify_code_round_corner_rect);
		}
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		setBtnResetFinishEnable();
		setBtnGetVetifyCodeEnable();
	}

	private void setBtnResetFinishEnable() {
		if (TextUtils.isEmpty(etNum.getText().toString())
				|| TextUtils.isEmpty(etPwd.getText().toString())
				|| TextUtils.isEmpty(etVerifyCode.getText().toString())) {
			btnResetFinish.setEnabled(false);
			btnResetFinish.setBackgroundResource(R.drawable.bt_login_disable_round_corner_rect);
		} else {
			btnResetFinish.setEnabled(true);
			btnResetFinish.setBackgroundResource(R.drawable.bt_login_round_corner_rect);
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
		dialog.dismiss();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AccountMgr.getInstance().removeResetPwdListener();
		AccountMgr.getInstance().removeIdentifyCodeListener();
	}
	
}
