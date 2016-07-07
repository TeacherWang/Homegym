package com.runrunfast.homegym.course;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.widget.KCalendar;
import com.runrunfast.homegym.widget.KCalendar.OnCalendarDateChangedListener;

import java.util.ArrayList;

public class DetailPlanActivity extends Activity implements OnClickListener{
	
	private Resources mResources;
	
	private TextView tvTitle;
	private Button btnLeft, btnRight;
	private KCalendar kCalendar;
	private TextView tvCalendarDate;
	private ImageView ivLastMonth, ivNextMonth;
	private ArrayList<CurrentDayTrainContentInfo> mContentInfoList;
	private ListView mCurrentDayListView;
	private CurrentDayTrainAdapter mCurrentDayTrainAdapter;
	
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
		
		tvCalendarDate = (TextView)findViewById(R.id.calendar_date_text);
		ivLastMonth = (ImageView)findViewById(R.id.calendar_left_img);
		ivLastMonth.setOnClickListener(this);
		
		ivNextMonth = (ImageView)findViewById(R.id.calendar_right_img);
		ivNextMonth.setOnClickListener(this);
		
		mCurrentDayListView = (ListView)findViewById(R.id.course_train_list);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left_btn:
			finish();
			break;
			
		case R.id.actionbar_right_btn:
			exitTrain();
			break;
			
		case R.id.calendar_left_img:
			toLastMonth();
			break;
			
		case R.id.calendar_right_img:
			toNextMonth();
			break;

		default:
			break;
		}
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
