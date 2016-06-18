package com.runrunfast.homegym.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.home.HomeActivity;

public class LoginActivity extends Activity {
	private final String TAG = "LoginActivity";
	
	private TextView tvLogin;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		initView();
	}

	private void initView() {
		tvLogin = (TextView)findViewById(R.id.login_text);
	}
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.login_text:
			handleClickLogin();
			break;

		default:
			break;
		}
	}

	private void handleClickLogin() {
		Log.d(TAG, "handleClickLogin");
		
		if(AccountMgr.getInstance().checkLoginLegal()){
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
		}
	}
	
}
