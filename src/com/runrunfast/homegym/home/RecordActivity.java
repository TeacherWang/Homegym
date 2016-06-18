package com.runrunfast.homegym.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.runrunfast.homegym.R;

public class RecordActivity extends Activity {
	private Button btnBack, btnShare;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		
		initView();
	}
	
	private void initView() {
//		btnBack = (Button)findViewById(R.id.actionbar_left_btn).setBackgroundResource(R.drawable.);
//		btnShare = (Button)findViewById(R.id.actionbar_right_btn).setBackgroundResource(R.drawable.);
	}

	public void onClick(View view){
		
	}
	
}
