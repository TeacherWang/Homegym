package com.runrunfast.homegym.course;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.widget.DialogActivity;
import com.runrunfast.homegym.widget.KCalendar;
import com.runrunfast.homegym.widget.KCalendar.OnCalendarDateChangedListener;

import java.util.ArrayList;

public class DetailPlanActivity extends Activity implements OnClickListener{
	private final String TAG = "DetailPlanActivity";
	
	private Resources mResources;
	
	private TextView tvTitle;
	private Button btnLeft, btnRight;
	private Button btnJoin;
	private KCalendar kCalendar;
	private TextView tvCalendarDate;
	private ImageView ivLastMonth, ivNextMonth;
	private ArrayList<CurrentDayTrainContentInfo> mContentInfoList;
	private ListView mCurrentDayListView;
	private CurrentDayTrainAdapter mCurrentDayTrainAdapter;
	
	private boolean isCourseExist = false;
	private int mCourseId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_detail_plan);
		
		mResources = getResources();
		
		initView();
		
		initData();
		
		initCalendarListener();
	}

	private void initCalendarListener() {
		kCalendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
			
			@Override
			public void onCalendarDateChanged(int year, int month) {
				tvCalendarDate.setText(year + mResources.getString(R.string.year) + month + mResources.getString(R.string.month));
			}
		});
	}

	private void initData() {
		mCourseId = getIntent().getIntExtra(Const.KEY_COURSE_ID, -1);
		
		isCourseExist = isCourseExist();
		
		if(isCourseExist){
			btnJoin.setText(R.string.start_train);
		}else{
			btnJoin.setText(R.string.join_train);
		}
		
		tvCalendarDate.setText(kCalendar.getCalendarYear() + mResources.getString(R.string.year)
				+ kCalendar.getCalendarMonth() + mResources.getString(R.string.month));
		
		mContentInfoList = new ArrayList<CurrentDayTrainContentInfo>();
		
		CurrentDayTrainContentInfo currentDayTrainContentInfo1 = new CurrentDayTrainContentInfo();
		currentDayTrainContentInfo1.iCourseId = 1;
		currentDayTrainContentInfo1.iTrainId = 1;
		currentDayTrainContentInfo1.iDifficultLevel = 1;
		currentDayTrainContentInfo1.iGroupNum = 4;
		currentDayTrainContentInfo1.iCount = 30;
		currentDayTrainContentInfo1.strTrainName = "平板卧推举";
		currentDayTrainContentInfo1.strActionNum = "动作一";
		mContentInfoList.add(currentDayTrainContentInfo1);
		
		CurrentDayTrainContentInfo currentDayTrainContentInfo2 = new CurrentDayTrainContentInfo();
		currentDayTrainContentInfo2.iCourseId = 1;
		currentDayTrainContentInfo2.iTrainId = 2;
		currentDayTrainContentInfo2.iDifficultLevel = 2;
		currentDayTrainContentInfo2.iGroupNum = 2;
		currentDayTrainContentInfo2.iCount = 20;
		currentDayTrainContentInfo2.strTrainName = "平板卧推举";
		currentDayTrainContentInfo2.strActionNum = "动作二";
		mContentInfoList.add(currentDayTrainContentInfo2);
		
		CurrentDayTrainContentInfo currentDayTrainContentInfo3 = new CurrentDayTrainContentInfo();
		currentDayTrainContentInfo3.iCourseId = 1;
		currentDayTrainContentInfo3.iTrainId = 3;
		currentDayTrainContentInfo3.iDifficultLevel = 3;
		currentDayTrainContentInfo3.iGroupNum = 5;
		currentDayTrainContentInfo3.iCount = 40;
		currentDayTrainContentInfo3.strTrainName = "平板卧推举";
		currentDayTrainContentInfo3.strActionNum = "动作三";
		mContentInfoList.add(currentDayTrainContentInfo3);
		
		mCurrentDayTrainAdapter = new CurrentDayTrainAdapter(this, mContentInfoList);
		mCurrentDayListView.setAdapter(mCurrentDayTrainAdapter);
	}

	private void initView() {
		kCalendar = (KCalendar)findViewById(R.id.calendar_view);
		
		tvTitle = (TextView)findViewById(R.id.actionbar_title);
		tvTitle.setText(R.string.train_detail);
		btnLeft = (Button)findViewById(R.id.actionbar_left_btn);
		btnLeft.setBackgroundResource(R.drawable.nav_back);
		
		btnRight = (Button)findViewById(R.id.actionbar_right_btn);
		btnRight.setText(R.string.exit_train);
		btnRight.setOnClickListener(this);
		btnRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		btnRight.setTextColor(getResources().getColor(R.color.about_email_content_text_color));
		btnRight.setVisibility(View.INVISIBLE);
		
		tvCalendarDate = (TextView)findViewById(R.id.calendar_date_text);
		ivLastMonth = (ImageView)findViewById(R.id.calendar_left_img);
		ivLastMonth.setOnClickListener(this);
		
		ivNextMonth = (ImageView)findViewById(R.id.calendar_right_img);
		ivNextMonth.setOnClickListener(this);
		
		mCurrentDayListView = (ListView)findViewById(R.id.course_train_list);
		
		btnJoin = (Button)findViewById(R.id.btn_join_in_train);
		btnJoin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left_btn:
			finish();
			break;
			
		case R.id.actionbar_right_btn:
			showExitDialog();
			break;
			
		case R.id.calendar_left_img:
			toLastMonth();
			break;
			
		case R.id.calendar_right_img:
			toNextMonth();
			break;
			
		case R.id.btn_join_in_train:
			prepareJoinCourse();
			break;

		default:
			break;
		}
	}

	private void showExitDialog() {
		Intent intent = new Intent(this, DialogActivity.class);
		intent.putExtra(DialogActivity.KEY_CONTENT, mResources.getString(R.string.exit_train_or_not));
		intent.putExtra(DialogActivity.KEY_CANCEL, mResources.getString(R.string.no));
		intent.putExtra(DialogActivity.KEY_CONFIRM, mResources.getString(R.string.yes));
		startActivityForResult(intent, Const.DIALOG_REQ_CODE_EXIT_COURSE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "resultCode = " + resultCode);
		if(requestCode == Const.DIALOG_REQ_CODE_EXIT_COURSE && resultCode == DialogActivity.RSP_CONFIRM){
			// 删除本地数据
			
			// 退出界面
			exitTrain();
		}
	}

	private void prepareJoinCourse() {
		// 本地存在此课程视频等信息
		if(isCourseExist){
			btnRight.setVisibility(View.VISIBLE);
		}
		// 本地不存在此课程视频等信息
		else{
			// 先下载
			
		}
		
	}

	/**
	  * @Method: isCourseExist
	  * @Description: 判断本课程本地是否有下载
	  * @return	
	  * 返回类型：boolean 
	  */
	private boolean isCourseExist(){
		
		return true;
	}
	
	private void toNextMonth() {
		kCalendar.nextMonth();
	}

	private void toLastMonth() {
		kCalendar.lastMonth();
	}

	private void exitTrain() {
		finish();
	}
}
