package com.runrunfast.homegym.course;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.widget.KCalendar;
import com.runrunfast.homegym.widget.KCalendar.OnCalendarDateChangedListener;

public class DetailPlanActivity extends Activity implements OnClickListener{
	
	private Resources mResources;
	
	private TextView tvTitle;
	private Button btnLeft, btnRight;
	private KCalendar kCalendar;
	private TextView tvCalendarDate;
	private ImageView ivLastMonth, ivNextMonth;
	
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
		
	}
}
