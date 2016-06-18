package com.runrunfast.homegym.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.runrunfast.homegym.R;

public class StartActivity extends Activity{
	private final String TAG = "StartActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
	}
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.start_register:
			handleClickRegister();
			break;
			
		case R.id.start_login:
			handleClickLogin();
			break;

		default:
			break;
		}
	}

	private void handleClickLogin() {
		Log.d(TAG, "handleClickLogin");
		
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	private void handleClickRegister() {
		Log.d(TAG, "handleClickRegister");
		
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}
}
