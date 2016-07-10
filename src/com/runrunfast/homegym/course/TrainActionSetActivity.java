package com.runrunfast.homegym.course;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.utils.Const;

public class TrainActionSetActivity extends Activity implements OnClickListener{
	
	private View backView;
	private TextView tvActionNum, tvTrainName, tvTrainDescript, tvJoinInTeach, tvGroupNum, tvTimeConsume, tvBurning;
	private Button btnAdd, btnMinus;
	
	private int mCourseId;
	private int mTrainId;
	private String mTrainName;
	private String mTrainDescript;
	private String mActionNum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_action_set);
		
		initView();
		
		initData();
	}

	private void initData() {
		Intent intent = getIntent();
		mCourseId = intent.getIntExtra(Const.KEY_COURSE_ID, -1);
		mTrainId = intent.getIntExtra(Const.KEY_TRAIN_ID, -1);
		mTrainName = intent.getStringExtra(Const.KEY_TRAIN_NAME);
		mTrainDescript = intent.getStringExtra(Const.KEY_TRAIN_DESCRIPT);
		mActionNum = intent.getStringExtra(Const.KEY_ACTION_NUM);
		
		tvActionNum.setText(mActionNum);
		tvTrainName.setText(mTrainName);
		tvTrainDescript.setText(mTrainDescript);
		
	}

	private void initView() {
		backView = (View)findViewById(R.id.train_action_back_layout);
		backView.setOnClickListener(this);
		
		btnAdd = (Button)findViewById(R.id.btn_group_num_add);
		btnAdd.setOnClickListener(this);
		
		btnMinus = (Button)findViewById(R.id.btn_group_num_minus);
		btnMinus.setOnClickListener(this);
		
		tvActionNum = (TextView)findViewById(R.id.train_action_num_text);
		tvTrainName = (TextView)findViewById(R.id.train_action_name_text);
		tvTrainDescript = (TextView)findViewById(R.id.train_action_descript_text);
		tvJoinInTeach = (TextView)findViewById(R.id.train_action_join_in_text);
		tvJoinInTeach.setOnClickListener(this);
		tvTimeConsume = (TextView)findViewById(R.id.train_action_time_num_text);
		tvBurning = (TextView)findViewById(R.id.train_action_burning_num_text);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.train_action_back_layout:
			finish();
			break;
			
		case R.id.btn_group_num_add:
			showAddGroupNum();
			break;
			
		case R.id.btn_group_num_minus:
			showMinusGroupNum();
			break;
			
		case R.id.train_action_join_in_text:
			jumpToVideoDemo();
			break;

		default:
			break;
		}
	}

	private void jumpToVideoDemo() {
		
	}

	private void showMinusGroupNum() {
		
	}

	private void showAddGroupNum() {
		
	}
}
