package com.runrunfast.homegym.course;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.utils.Const;

public class CourseTrainActivity extends Activity implements OnClickListener{
	
	private TextView tvTitle;
	private Button btnLeft, btnRight;
	private Button btnStartTrain;
	
	private CourseTrainAdapter mCourseTrainAdapter;
	private ListView mCourseTrainListView;
	private ArrayList<CourseTrainInfo> mCourseTrainInfoList;
	
	private int mCourseId;
	private String mCourseName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_course_train);
		
		initView();
		
		initData();
		
		initListener();
	}


	private void initListener() {
		mCourseTrainListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				jumpToTrainActionSetActivity(position);
			}
		});
	}
	
	private void jumpToTrainActionSetActivity(int position) {
		CourseTrainInfo courseTrainInfo = mCourseTrainInfoList.get(position);
		int trainId = courseTrainInfo.iTrainId;
		String trainName = courseTrainInfo.strTrainName;
		String actionNum = courseTrainInfo.strActionNum;
		String trainDecript = courseTrainInfo.strTrainDescript;
		
		Intent intent = new Intent(CourseTrainActivity.this, ActionSetActivity.class);
		intent.putExtra(Const.KEY_COURSE_ID, mCourseId);
		intent.putExtra(Const.KEY_TRAIN_ID, trainId);
		intent.putExtra(Const.KEY_TRAIN_NAME, trainName);
		intent.putExtra(Const.KEY_TRAIN_DESCRIPT, trainDecript);
		intent.putExtra(Const.KEY_ACTION_NUM, actionNum);
		CourseTrainActivity.this.startActivity(intent);
	}

	private void initData() {
		mCourseTrainInfoList = new ArrayList<CourseTrainInfo>();
		
		mCourseId = getIntent().getIntExtra(Const.KEY_COURSE_ID, -1);
		mCourseName = getIntent().getStringExtra(Const.KEY_COURSE_NAME);
		
		tvTitle.setText(mCourseName);
		
		CourseTrainInfo courseTrainInfo1 = new CourseTrainInfo();
		courseTrainInfo1.iCourseId = 1;
		courseTrainInfo1.strCrouseName = "21天增肌训练";
		courseTrainInfo1.iTrainId = 1;
		courseTrainInfo1.strTrainName = "平板卧推举";
		courseTrainInfo1.strActionNum = "动作一";
		courseTrainInfo1.strTrainPosition = "背部 胸部";
		courseTrainInfo1.strTrainDescript = "坚持训练将锻炼到胸大肌和三角肌";
		courseTrainInfo1.iTime = 15;
		courseTrainInfo1.iKcal = 187;
		courseTrainInfo1.iDiffcultLevel = 1;
		mCourseTrainInfoList.add(courseTrainInfo1);
		
		CourseTrainInfo courseTrainInfo2 = new CourseTrainInfo();
		courseTrainInfo2.iCourseId = 1;
		courseTrainInfo2.strCrouseName = "21天增肌训练";
		courseTrainInfo2.iTrainId = 2;
		courseTrainInfo2.strTrainName = "平板卧推举";
		courseTrainInfo2.strActionNum = "动作二";
		courseTrainInfo2.strTrainPosition = "背部 胸部";
		courseTrainInfo2.strTrainDescript = "坚持训练将锻炼到胸大肌和三角肌";
		courseTrainInfo2.iTime = 15;
		courseTrainInfo2.iKcal = 287;
		courseTrainInfo2.iDiffcultLevel = 2;
		mCourseTrainInfoList.add(courseTrainInfo2);
		
		CourseTrainInfo courseTrainInfo3 = new CourseTrainInfo();
		courseTrainInfo3.iCourseId = 1;
		courseTrainInfo3.strCrouseName = "21天增肌训练";
		courseTrainInfo3.iTrainId = 3;
		courseTrainInfo3.strTrainName = "平板卧推举";
		courseTrainInfo3.strActionNum = "动作三";
		courseTrainInfo3.strTrainPosition = "背部 胸部";
		courseTrainInfo3.strTrainDescript = "坚持训练将锻炼到胸大肌和三角肌";
		courseTrainInfo3.iTime = 15;
		courseTrainInfo3.iKcal = 387;
		courseTrainInfo3.iDiffcultLevel = 3;
		mCourseTrainInfoList.add(courseTrainInfo3);
		
		CourseTrainInfo courseTrainInfo4 = new CourseTrainInfo();
		courseTrainInfo4.iCourseId = 1;
		courseTrainInfo4.strCrouseName = "21天增肌训练";
		courseTrainInfo4.iTrainId = 4;
		courseTrainInfo4.strTrainName = "平板卧推举";
		courseTrainInfo4.strActionNum = "动作四";
		courseTrainInfo4.strTrainPosition = "背部 胸部";
		courseTrainInfo4.strTrainDescript = "坚持训练将锻炼到胸大肌和三角肌";
		courseTrainInfo4.iTime = 15;
		courseTrainInfo4.iKcal = 487;
		courseTrainInfo4.iDiffcultLevel = 4;
		mCourseTrainInfoList.add(courseTrainInfo4);
		
		mCourseTrainAdapter = new CourseTrainAdapter(this, mCourseTrainInfoList);
		mCourseTrainListView.setAdapter(mCourseTrainAdapter);
	}


	private void initView() {
		tvTitle = (TextView)findViewById(R.id.actionbar_title);
		btnLeft = (Button)findViewById(R.id.actionbar_left_btn);
		btnLeft.setBackgroundResource(R.drawable.nav_back);
		
		btnRight = (Button)findViewById(R.id.actionbar_right_btn);
		btnRight.setText(R.string.train_detail);
		btnRight.setOnClickListener(this);
		btnRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		btnRight.setTextColor(getResources().getColor(R.color.record_detail_text_color));
		
		mCourseTrainListView = (ListView)findViewById(R.id.course_train_list);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left_btn:
			finish();
			break;
			
		case R.id.actionbar_right_btn:
			jumpToTrainDetailActivity();
			break;

		default:
			break;
		}
	}


	private void jumpToTrainDetailActivity() {
		Intent intent = new Intent(this, DetailPlanActivity.class);
		
		intent.putExtra(Const.KEY_COURSE_ID, 1);
		
		startActivity(intent);
	}
}
