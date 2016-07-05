package com.runrunfast.homegym.start;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.AccountMgr.ILoginListener;
import com.runrunfast.homegym.home.HomeActivity;

public class LoginActivity extends Activity implements OnClickListener, TextWatcher{
	private final String TAG = "LoginActivity";
	
	public static final int REQ_CODE_RESET_PWD = 1;
	public static final int RSP_CODE_RESET_PWD = 2;
	public static final String KEY_USERNAME = "key_username";
	
	private View actionBar;
	private ImageView ivBack;
	private Button btnLogin;
	private TextView tvRegister, tvForgotPwd;
	private EditText etPhoneNume, etPwd;
	
	private ProgressDialog dialog;
	
	private ILoginListener iLoginListener;
	
	private String mUsername;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
		initListener();
	}
	
	private void initListener() {
		iLoginListener = new ILoginListener() {

			@Override
			public void onSuccess() {
				handleLoginSuc();
			}

			@Override
			public void onFail(String reason) {
				dismissDialog();
				Toast.makeText(LoginActivity.this, reason, Toast.LENGTH_SHORT).show();
			}
			
		};
		AccountMgr.getInstance().setOnLoginListener(iLoginListener);
	}

	private void handleLoginSuc() {
		AccountMgr.getInstance().saveLoginAccount(this, mUsername);
		AccountMgr.getInstance().setLoginSuc(this, true);
		dismissDialog();
		jumpToHomeActivity();
		AccountMgr.getInstance().sendLoginSucBroadcast(this);
		finish();
	}
	
	private void initView() {
		actionBar = (View)findViewById(R.id.login_action_bar);
		((TextView)actionBar.findViewById(R.id.login_title_text)).setText(R.string.login);
		
		ivBack = (ImageView)actionBar.findViewById(R.id.login_back_img);
		ivBack.setOnClickListener(this);
		
		btnLogin = (Button)findViewById(R.id.btn_login);
		tvRegister = (TextView)findViewById(R.id.tv_register);
		tvForgotPwd = (TextView)findViewById(R.id.tv_forget_pwd);
		etPhoneNume = (EditText)findViewById(R.id.login_num_edit);
		etPwd = (EditText)findViewById(R.id.login_pwd_edit);
		
		btnLogin.setOnClickListener(this);
		tvRegister.setOnClickListener(this);
		tvForgotPwd.setOnClickListener(this);
		etPhoneNume.addTextChangedListener(this);
		etPwd.addTextChangedListener(this);
		
		setButtonEnable();
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_login:
			handleClickLogin();
			break;
			
		case R.id.tv_register:
			jumpToRegister();
			break;
			
		case R.id.tv_forget_pwd:
			jumpToResetPwd();
			break;
			
		case R.id.login_back_img:
			finish();
			break;

		default:
			break;
		}
	}

	private void jumpToResetPwd() {
		startActivityForResult(new Intent(this, ResetPwdActivity.class), REQ_CODE_RESET_PWD);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode != REQ_CODE_RESET_PWD || resultCode != RSP_CODE_RESET_PWD){
			Log.e(TAG, "onActivityResult, requestCode and resultCode is : " + requestCode + " and " + resultCode);
			return;
		}
		
		String username = data.getStringExtra(KEY_USERNAME);
		if(TextUtils.isEmpty(username)){
			Log.d(TAG, "onActivityResult, username is empty");
			return;
		}
		
		etPhoneNume.setText(username);
	}
	
	private void jumpToRegister() {
		startActivity(new Intent(this, RegisterActivity.class));
	}

	private void handleClickLogin() {
		Log.d(TAG, "handleClickLogin");
		// TODO 测试
//		handleLoginSuc();
		// TODO 测试
		
		mUsername = etPhoneNume.getText().toString();
		String pwd = etPwd.getText().toString();
		if(TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(pwd)){
			Toast.makeText(this, R.string.input_have_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		
		AccountMgr.getInstance().login(mUsername, pwd);
		
		showDialog();
	}

	private void jumpToHomeActivity() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
	}

	@Override
	public void afterTextChanged(Editable s) {
		setButtonEnable();
	}

	private void setButtonEnable() {
		if (TextUtils.isEmpty(etPhoneNume.getText().toString())
				|| TextUtils.isEmpty(etPwd.getText().toString())) {
			btnLogin.setEnabled(false);
			btnLogin.setBackgroundResource(R.drawable.bt_login_disable_round_corner_rect);
		} else {
			btnLogin.setEnabled(true);
			btnLogin.setBackgroundResource(R.drawable.bt_login_round_corner_rect);
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
	
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
		AccountMgr.getInstance().removeLoginListener();
	}
	
}
