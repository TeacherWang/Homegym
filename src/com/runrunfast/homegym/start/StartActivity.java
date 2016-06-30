package com.runrunfast.homegym.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runrunfast.homegym.R;

import java.util.ArrayList;

public class StartActivity extends Activity{
	private final String TAG = "StartActivity";
	
	private static final int[] mImageIds = new int[] { R.drawable.defoult_bg1,
		R.drawable.defoult_bg2, R.drawable.defoult_bg3 };
	private ArrayList<RelativeLayout> mRelativelayoutList;
	
	private ViewPager vpGuide;
	
	private RelativeLayout mStartBgRelativeLayout1, mStartBgRelativeLayout2, mStartBgRelativeLayout3, mStartBgLayout1, mStartBgLayout2, mStartBgLayout3;
	private ImageView ivPoint11, ivPoint12, ivPoint13, ivPoint21, ivPoint22, ivPoint23, ivPoint31, ivPoint32, ivPoint33;
	private TextView tv11, tv12, tv21, tv22, tv31, tv32;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		initView();
		
		initData();
	}
	
	private void initData() {
		mRelativelayoutList = new ArrayList<RelativeLayout>(); 
		
		mRelativelayoutList.add(mStartBgRelativeLayout1);
		mRelativelayoutList.add(mStartBgRelativeLayout2);
		mRelativelayoutList.add(mStartBgRelativeLayout3);
		
		vpGuide.setAdapter(new GuideAdapter());
		vpGuide.setOnPageChangeListener(new GuidePageListener());
		
		setIndicator(0);
	}

	private void initView() {
		vpGuide = (ViewPager) findViewById(R.id.start_view_pager);
		
		mStartBgRelativeLayout1 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.start_bg, null);
		mStartBgRelativeLayout2 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.start_bg, null);
		mStartBgRelativeLayout3 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.start_bg, null);
		
		mStartBgLayout1 = (RelativeLayout)mStartBgRelativeLayout1.findViewById(R.id.start_bg);
		mStartBgLayout2 = (RelativeLayout)mStartBgRelativeLayout2.findViewById(R.id.start_bg);
		mStartBgLayout3 = (RelativeLayout)mStartBgRelativeLayout3.findViewById(R.id.start_bg);
		
		ivPoint11 = (ImageView)mStartBgRelativeLayout1.findViewById(R.id.start_indicator1);
		ivPoint12 = (ImageView)mStartBgRelativeLayout1.findViewById(R.id.start_indicator2);
		ivPoint13 = (ImageView)mStartBgRelativeLayout1.findViewById(R.id.start_indicator3);
		
		ivPoint21 = (ImageView)mStartBgRelativeLayout2.findViewById(R.id.start_indicator1);
		ivPoint22 = (ImageView)mStartBgRelativeLayout2.findViewById(R.id.start_indicator2);
		ivPoint23 = (ImageView)mStartBgRelativeLayout2.findViewById(R.id.start_indicator3);
		
		ivPoint31 = (ImageView)mStartBgRelativeLayout3.findViewById(R.id.start_indicator1);
		ivPoint32 = (ImageView)mStartBgRelativeLayout3.findViewById(R.id.start_indicator2);
		ivPoint33 = (ImageView)mStartBgRelativeLayout3.findViewById(R.id.start_indicator3);
		
		tv11 = (TextView)mStartBgRelativeLayout1.findViewById(R.id.start_text1);
		tv12 = (TextView)mStartBgRelativeLayout1.findViewById(R.id.start_text2);
		
		tv21 = (TextView)mStartBgRelativeLayout2.findViewById(R.id.start_text1);
		tv22 = (TextView)mStartBgRelativeLayout2.findViewById(R.id.start_text2);
		
		tv31 = (TextView)mStartBgRelativeLayout3.findViewById(R.id.start_text1);
		tv32 = (TextView)mStartBgRelativeLayout3.findViewById(R.id.start_text2);
		
		setIndicator(0);
		setIndicator(1);
		setIndicator(2);
	}

	/**
	 * 新手引导页viewGroup的适配器
	 * 
	 * */
	class GuideAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImageIds.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mRelativelayoutList.get(position));
			return mRelativelayoutList.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}
	
	/**
	 * viewpager的滑动监听
	 * 
	 * 
	 */
	class GuidePageListener implements OnPageChangeListener {

		// 滑动事件
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
//			int len = (int) (mPointWidth * positionOffset) + position
//					* mPointWidth;
//			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewPointWhite
//					.getLayoutParams();// 获取当前红点的布局参数
//			params.leftMargin = len;// 设置左边距
//			viewPointWhite.setLayoutParams(params);// 重新给小红点设置布局参数
		}

		// 某个页面被选中
		@Override
		public void onPageSelected(int position) {
//			setIndicator(position);
		}

		// 滑动状态发生变化
		@Override
		public void onPageScrollStateChanged(int state) {}
	}
	
	public void setIndicator(int position) {
		if(position == 0){
			mStartBgLayout1.setBackgroundResource(R.drawable.defoult_bg1);
			ivPoint11.setBackgroundResource(R.drawable.start_round_select_bg);
			ivPoint12.setBackgroundResource(R.drawable.start_round_normal_bg);
			ivPoint13.setBackgroundResource(R.drawable.start_round_normal_bg);
			tv11.setText(R.string.start_text1_first);
			tv12.setText(R.string.start_text1_second);
		}else if(position == 1){
			mStartBgLayout2.setBackgroundResource(R.drawable.defoult_bg2);
			ivPoint21.setBackgroundResource(R.drawable.start_round_normal_bg);
			ivPoint22.setBackgroundResource(R.drawable.start_round_select_bg);
			ivPoint23.setBackgroundResource(R.drawable.start_round_normal_bg);
			tv21.setText(R.string.start_text2_first);
			tv22.setText(R.string.start_text2_second);
		}else if(position == 2){
			mStartBgLayout3.setBackgroundResource(R.drawable.defoult_bg3);
			ivPoint31.setBackgroundResource(R.drawable.start_round_normal_bg);
			ivPoint32.setBackgroundResource(R.drawable.start_round_normal_bg);
			ivPoint33.setBackgroundResource(R.drawable.start_round_select_bg);
			tv31.setText(R.string.start_text3_first);
			tv32.setText(R.string.start_text3_second);
		}
	}
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.start_register:
			handleClickRegister();
			break;
			
		case R.id.start_login:
			handleClickLogin();
			break;

		default:
			break;
		}
	}

	private void handleClickLogin() {
		Log.d(TAG, "handleClickLogin");
		
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}

	private void handleClickRegister() {
		Log.d(TAG, "handleClickRegister");
		
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
	}
}
