package com.runrunfast.homegym.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.runrunfast.homegym.R;

public class RegisterActivity extends Activity implements OnClickListener{
	
	private View actionBar;
	private ImageView ivBack;
	private Button btnRegisterFinish;
	private TextView tvHadAccount, tvGetVerifyCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
	}

	private void initView() {
		actionBar = (View)findViewById(R.id.register_action_bar);
		((TextView)actionBar.findViewById(R.id.login_title_text)).setText(R.string.register);
		
		ivBack = (ImageView)actionBar.findViewById(R.id.login_back_img);
		ivBack.setOnClickListener(this);
		
		btnRegisterFinish = (Button)findViewById(R.id.btn_register_finish);
		btnRegisterFinish.setOnClickListener(this);
		
		tvHadAccount = (TextView)findViewById(R.id.tv_exist_acount);
		tvHadAccount.setOnClickListener(this);
		
		tvGetVerifyCode = (TextView)findViewById(R.id.tv_get_verificationcode);
		tvGetVerifyCode.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_register_finish:
			handleClickRegisterFinish();
			break;

		case R.id.tv_exist_acount:
			jumpToLoginActivity();
			break;
			
		case R.id.tv_get_verificationcode:
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
		
	}

	private void jumpToLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	private void handleClickRegisterFinish() {
		
	}
}
