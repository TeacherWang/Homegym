package com.runrunfast.homegym.home;

import com.runrunfast.homegym.R;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FeedbackActivity extends Activity {
	
	private Button btnSend;
	private TextView tvTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		initView();
		
	}
	
	private void initView() {
		tvTitle = (TextView)findViewById(R.id.actionbar_title);
		tvTitle.setText(R.string.feedback);
		
		findViewById(R.id.actionbar_left_btn).setBackgroundResource(R.drawable.nav_back);
		btnSend = (Button)findViewById(R.id.actionbar_right_btn);
		btnSend.setText(R.string.feedback_send);
		btnSend.setTextColor(getResources().getColor(R.color.feedback_send_text_color));
	}
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.actionbar_left_btn:
			finish();
			break;
			
		case R.id.actionbar_right_btn:
			prepareToSend();
			break;

		default:
			break;
		}
	}

	private void prepareToSend() {
		
	}
	
}
