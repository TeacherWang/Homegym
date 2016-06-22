package com.runrunfast.homegym.start;

import android.app.Activity;
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

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.home.HomeActivity;

public class LoginActivity extends Activity implements OnClickListener, TextWatcher{
	private final String TAG = "LoginActivity";
	
	private View actionBar;
	private ImageView ivBack;
	private Button btnLogin;
	private TextView tvRegister, tvForgotPwd;
	private EditText etPhoneNume, etPwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
	}

	private void initView() {
		actionBar = (View)findViewById(R.id.login_action_bar);
		((TextView)actionBar.findViewById(R.id.login_title_text)).setText(R.string.login);
		
		ivBack = (ImageView)actionBar.findViewById(R.id.login_back_img);
		ivBack.setOnClickListener(this);
		
		btnLogin = (Button)findViewById(R.id.btn_login);
		tvRegister = (TextView)findViewById(R.id.tv_register);
		tvForgotPwd = (TextView)findViewById(R.id.tv_forget_pwd);
		etPhoneNume = (EditText)findViewById(R.id.et_phone_num);
		etPwd = (EditText)findViewById(R.id.et_psw);
		
		btnLogin.setOnClickListener(this);
		tvRegister.setOnClickListener(this);
		tvForgotPwd.setOnClickListener(this);
		etPhoneNume.addTextChangedListener(this);
		etPwd.addTextChangedListener(this);
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
		
	}

	private void jumpToRegister() {
		
	}

	private void handleClickLogin() {
		Log.d(TAG, "handleClickLogin");
		
		if(AccountMgr.getInstance().checkLoginLegal()){
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (TextUtils.isEmpty(etPhoneNume.getText().toString())
				|| TextUtils.isEmpty(etPwd.getText().toString())) {
			btnLogin.setEnabled(false);
		} else {
			btnLogin.setEnabled(true);
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
	
}
