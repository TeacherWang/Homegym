package com.runrunfast.homegym.start;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.runrunfast.homegym.R;

public class ImprovePersonalInfoActivity extends Activity implements OnClickListener{
	
	private View actionBar;
	private TextView tvAddHeadimg;
	private Button btnFinish;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_improve_personal_info);
		initView();
	}

	private void initView() {
		actionBar = (View)findViewById(R.id.impr_personal_info_action_bar);
		((TextView)actionBar.findViewById(R.id.login_title_text)).setText(R.string.improve_personal_info);
		
		actionBar.findViewById(R.id.login_back_img).setVisibility(View.INVISIBLE);
		
		tvAddHeadimg = (TextView)findViewById(R.id.add_headimg_text);
		tvAddHeadimg.setOnClickListener(this);
		
		btnFinish = (Button)findViewById(R.id.btn_impr_finish);
		btnFinish.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_headimg_text:
			getHeadImg();
			break;
			
		case R.id.btn_impr_finish:
			infoInputFinish();
			break;

		default:
			break;
		}
	}

	private void infoInputFinish() {
		
	}

	private void getHeadImg() {
		
	}
}
