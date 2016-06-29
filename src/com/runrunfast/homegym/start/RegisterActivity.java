package com.runrunfast.homegym.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class RegisterActivity extends Activity implements OnClickListener, TextWatcher{
	
	private View actionBar;
	private ImageView ivBack;
	private Button btnRegisterFinish, btnGetVerifyCode;
	private TextView tvHadAccount;
	private EditText etNum, etVerifyCode, etPwd;
	
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
		
	}

	private void jumpToLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	private void handleClickRegisterFinish() {
		String strNum = etNum.getText().toString();
		String strVerifyCode = etVerifyCode.getText().toString();
		String strPwd = etPwd.getText().toString();
		if(checkEmpty(strNum, strVerifyCode, strPwd)){
			Toast.makeText(this, R.string.input_have_empty, Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent intent = new Intent(this, ImprovePersonalInfoActivity.class);
		startActivity(intent);
		finish();
	}
	
	private boolean checkEmpty(String num, String verifyCode, String pwd){
		if(TextUtils.isEmpty(num) || TextUtils.isEmpty(verifyCode) || TextUtils.isEmpty(pwd)){
			return false;
		}
		return true;
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (TextUtils.isEmpty(etNum.getText().toString())
				|| TextUtils.isEmpty(etPwd.getText().toString())
				|| TextUtils.isEmpty(etVerifyCode.getText().toString())) {
			btnRegisterFinish.setEnabled(false);
		} else {
			btnRegisterFinish.setEnabled(true);
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) { }

}
