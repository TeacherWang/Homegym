package com.runrunfast.homegym.start;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.runrunfast.homegym.R;

public class ResetPwdActivity extends Activity implements OnClickListener{
	
	private View resetActionBar;
	private ImageView ivBack;
	
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
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_back_img:
			finish();
			break;

		default:
			break;
		}
	}
}
