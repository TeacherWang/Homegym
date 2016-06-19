package com.runrunfast.homegym.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runrunfast.homegym.R;

public class RecordActivity extends Activity {
	private Button btnShare;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		
		initView();
	}
	
	private void initView() {
		findViewById(R.id.actionbar_left_btn).setBackgroundResource(R.drawable.nav_back);
//		btnShare = (Button)findViewById(R.id.actionbar_right_btn).setBackgroundResource(R.drawable.);
		((TextView)findViewById(R.id.actionbar_title)).setText(R.string.trainnig_record);
	}

	public void onClick(View view){
		switch (view.getId()) {
		case R.id.actionbar_left_btn:
			finish();
			break;
			
		case R.id.actionbar_right_btn:
			showShare();
			break;

		default:
			break;
		}
	}

	private void showShare() {
		
	}
	
}
