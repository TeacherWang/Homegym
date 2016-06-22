package com.runrunfast.homegym.start;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.runrunfast.homegym.R;

import java.util.ArrayList;

public class StartActivity extends Activity{
	private final String TAG = "StartActivity";
	
	private static final int[] mImageIds = new int[] { R.drawable.home_state_finish,
		R.drawable.home_state_going, R.drawable.home_state_high_quality };
	private ArrayList<ImageView> mImageViewList;
	
	private ViewPager vpGuide;
	
	private ImageView ivPoint1, ivPoint2, ivPoint3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		initView();
		
		initData();
	}
	
	private void initData() {
		mImageViewList = new ArrayList<ImageView>(); 
		
		for(int i=0; i<3; i++){
			ImageView image = new ImageView(this);
			image.setBackgroundResource(mImageIds[i]);// 设置引导页背景
			mImageViewList.add(image);
		}
		
		vpGuide.setAdapter(new GuideAdapter());
		vpGuide.setOnPageChangeListener(new GuidePageListener());
	}

	private void initView() {
		vpGuide = (ViewPager) findViewById(R.id.start_view_pager);
		ivPoint1 = (ImageView)findViewById(R.id.start_indicator1);
		ivPoint2 = (ImageView)findViewById(R.id.start_indicator2);
		ivPoint3 = (ImageView)findViewById(R.id.start_indicator3);
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
			container.addView(mImageViewList.get(position));
			return mImageViewList.get(position);
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
			setIndicator(position);
		}

		// 滑动状态发生变化
		@Override
		public void onPageScrollStateChanged(int state) {}
	}
	
	public void setIndicator(int position) {
		if(position == 0){
			ivPoint1.setBackgroundResource(R.drawable.start_indicator_big);
			ivPoint2.setBackgroundResource(R.drawable.start_indicator_small);
			ivPoint3.setBackgroundResource(R.drawable.start_indicator_small);
		}else if(position == 1){
			ivPoint1.setBackgroundResource(R.drawable.start_indicator_small);
			ivPoint2.setBackgroundResource(R.drawable.start_indicator_big);
			ivPoint3.setBackgroundResource(R.drawable.start_indicator_small);
		}else if(position == 2){
			ivPoint1.setBackgroundResource(R.drawable.start_indicator_small);
			ivPoint2.setBackgroundResource(R.drawable.start_indicator_small);
			ivPoint3.setBackgroundResource(R.drawable.start_indicator_big);
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
