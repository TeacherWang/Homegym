package com.runrunfast.homegym.home;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.BtDevice.BtDeviceActivity;
import com.runrunfast.homegym.home.fragments.AllCoursesFragment;
import com.runrunfast.homegym.home.fragments.MeFragment;
import com.runrunfast.homegym.home.fragments.MyTrainingFragment;
import com.runrunfast.homegym.record.RecordActivity;

public class HomeActivity extends FragmentActivity{
	private final String TAG = "HomeActivity";
	
	private static final int PAGE_COUNT = 3;
	private static final int FRAGMENT_MY_TRAINING = 0;
	private static final int FRAGMENT_ALL_COURSES = 1;
	private static final int FRAGMENT_ME		  = 2;
	
	private int mTrainingType = FRAGMENT_MY_TRAINING; // 默认训练界面的我的训练
	
	private Resources mResources;
	private TextView tvTitle;
	private View selectView;
	private Button btnSelectLeft, btnSelectRight;
	private ImageView ivTraining, ivMe;
	private TextView tvTaining, tvMe;
	private FrameLayout mFrameLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		mResources = getResources();
		initView();
		
		switchFragment(FRAGMENT_MY_TRAINING);
	}
	
	private void initView() {
		tvTitle = (TextView)findViewById(R.id.actionbar_title);
		tvTitle.setVisibility(View.INVISIBLE);
		selectView = (View)findViewById(R.id.actionbar_select_layout);
		selectView.setVisibility(View.VISIBLE);
		
		findViewById(R.id.actionbar_left_btn).setBackgroundResource(R.drawable.nav_bluetooth);
		findViewById(R.id.actionbar_right_btn).setBackgroundResource(R.drawable.nav_record);
		
		btnSelectLeft = (Button)selectView.findViewById(R.id.actionbar_select_left_btn);
		btnSelectRight = (Button)selectView.findViewById(R.id.actionbar_select_right_btn);
		
		ivTraining = (ImageView)findViewById(R.id.bottom_training_img);
		ivMe = (ImageView)findViewById(R.id.bottom_me_img);
		tvTaining = (TextView)findViewById(R.id.bottom_training_text);
		tvMe = (TextView)findViewById(R.id.bottom_me_text);
		
		mFrameLayout = (FrameLayout)findViewById(R.id.home_content_layout);
	}

	public void onClick(View view){
		switch (view.getId()) {
		case R.id.actionbar_select_left_btn:
			handleClickMyTraining();
			break;
			
		case R.id.actionbar_select_right_btn:
			handleClickAllCourse();
			break;
			
		case R.id.actionbar_left_btn:
			handleClickBt();
			break;
			
		case R.id.actionbar_right_btn:
			handleClickRecord();
			break;
			
		case R.id.bottom_training_layout:
			handleClickBottomTraining();
			break;
			
		case R.id.bottom_me_layout:
			handleClickBottomMe();
			break;
			
		default:
			break;
		}
	}
	
	private void switchFragment(int tabPosition) {
		Fragment fragment = (Fragment) mFragmentPagerAdapter.instantiateItem(mFrameLayout, tabPosition);
		mFragmentPagerAdapter.setPrimaryItem(mFrameLayout, FRAGMENT_MY_TRAINING, fragment);
		mFragmentPagerAdapter.finishUpdate(mFrameLayout);
	}
	
	private void handleClickBottomTraining() {
		if(mTrainingType == FRAGMENT_MY_TRAINING){
			switchFragment(FRAGMENT_MY_TRAINING);
		}else{
			switchFragment(FRAGMENT_ALL_COURSES);
		}
		
		changeTainingViewEnable();
		
		tvTitle.setVisibility(View.INVISIBLE);
		selectView.setVisibility(View.VISIBLE);
	}
	
	private void handleClickBottomMe() {
		switchFragment(FRAGMENT_ME);
		
		changeMeViewEnable();
		
		tvTitle.setVisibility(View.VISIBLE);
		tvTitle.setText(R.string.me);
		selectView.setVisibility(View.INVISIBLE);
	}

	/**
	 * 选中“我”界面
	 */
	private void changeMeViewEnable() {
		ivTraining.setBackgroundResource(R.drawable.tab_icon_exercise_disable);
		ivMe.setBackgroundResource(R.drawable.tab_icon_me);
		tvTaining.setTextColor(mResources.getColor(R.color.bottom_text_disable_color));
		tvMe.setTextColor(mResources.getColor(R.color.bottom_text_enable_color));
	}

	/**
	 * 选中“训练”界面
	 */
	private void changeTainingViewEnable() {
		ivTraining.setBackgroundResource(R.drawable.tab_icon_exercis);
		ivMe.setBackgroundResource(R.drawable.tab_icon_me_disabale);
		tvTaining.setTextColor(mResources.getColor(R.color.bottom_text_enable_color));
		tvMe.setTextColor(mResources.getColor(R.color.bottom_text_disable_color));
	}

	private void handleClickAllCourse() {
		switchFragment(FRAGMENT_ALL_COURSES);
		mTrainingType = FRAGMENT_ALL_COURSES;
		
		btnSelectRight.setTextColor(mResources.getColor(R.color.actionbar_select_text_color));
		btnSelectRight.setBackgroundResource(R.drawable.nav_select_right);
		
		btnSelectLeft.setTextColor(mResources.getColor(R.color.actionbar_unselect_text_color));
		btnSelectLeft.setBackgroundResource(R.color.transparent);
	}

	private void handleClickMyTraining() {
		switchFragment(FRAGMENT_MY_TRAINING);
		mTrainingType = FRAGMENT_MY_TRAINING;
		
		btnSelectLeft.setTextColor(mResources.getColor(R.color.actionbar_select_text_color));
		btnSelectLeft.setBackgroundResource(R.drawable.nav_select_left);
		
		btnSelectRight.setTextColor(mResources.getColor(R.color.actionbar_unselect_text_color));
		btnSelectRight.setBackgroundResource(R.color.transparent);
	}

	private void handleClickRecord() {
		Log.d(TAG, "handleClickRecord");
		Intent intent = new Intent(this, RecordActivity.class);
		startActivity(intent);
	}

	private void handleClickBt() {
		Log.d(TAG, "handleClickBt");
		Intent intent = new Intent(this, BtDeviceActivity.class);
		startActivity(intent);
	}
	
	FragmentPagerAdapter mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case FRAGMENT_MY_TRAINING:
				return new MyTrainingFragment();

			case FRAGMENT_ALL_COURSES:
				return new AllCoursesFragment();

			case FRAGMENT_ME:
				return new MeFragment();

			default:
				return new MyTrainingFragment();
			}
		}

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}
	};
	
}
