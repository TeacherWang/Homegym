package com.runrunfast.homegym.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.runrunfast.homegym.R;

public class BtDeviceActivity extends Activity {
	private final String TAG = "BtDeviceActivity";
	
	private TextView tvTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_btdevice);
		
		initView();
		
		initData();
	}

	private void initData() {
		tvTitle.setText(R.string.gym);
	}

	private void initView() {
		tvTitle = (TextView)findViewById(R.id.actionbar_title);
	}
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.actionbar_left_btn:
			handleClickLeft();
			break;

		default:
			break;
		}
	}

	private void handleClickLeft() {
		finish();
	}
}
