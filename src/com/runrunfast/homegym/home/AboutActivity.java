package com.runrunfast.homegym.home;

import com.runrunfast.homegym.R;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends Activity {
	
	private TextView tvTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		
		initView();
	}

	private void initView() {
		tvTitle = (TextView)findViewById(R.id.actionbar_title);
		tvTitle.setText(R.string.about);
		
		findViewById(R.id.actionbar_left_btn).setBackgroundResource(R.drawable.nav_back);
	}
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.actionbar_left_btn:
			finish();
			break;

		default:
			break;
		}
	}
	
}
