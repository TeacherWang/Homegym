package com.runrunfast.homegym.start;

import android.app.Activity;
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

import com.runrunfast.homegym.R;

public class ResetPwdActivity extends Activity implements OnClickListener, TextWatcher{
	
	private View resetActionBar;
	private ImageView ivBack;
	private EditText etNum, etVerifyCode, etPwd;
	private Button btnGetVerifyCode, btnResetFinish;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_pwd);
		
		initView();
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
		
	}

	private void handleClickGetVerifyCode() {
		
	}
	
	@Override
	public void afterTextChanged(Editable s) {
		if (TextUtils.isEmpty(etNum.getText().toString())
				|| TextUtils.isEmpty(etPwd.getText().toString())
				|| TextUtils.isEmpty(etVerifyCode.getText().toString())) {
			btnResetFinish.setEnabled(false);
		} else {
			btnResetFinish.setEnabled(true);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) { }
	
}
