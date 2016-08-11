package com.runrunfast.homegym.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;

public class FinishActivity extends Activity {
	
	public static final String KEY_FINISH_OR_UNFINISH = "key_finish_or_unfinish";
	public static final int TYPE_FINISH = 1;
	public static final int TYPE_UNFINISH = 2;
	
	private ListView mHadFinishListView;
	private HadFinishAdapter mHadFinishAdapter;
	private ArrayList<Action> mActionList;
	
	private FrameLayout mFinishDataContainer;
	private View mFinishedView;
	
	private TextView tvFinishCourseName;
	private TextView tvFinishActionTime;
	private TextView tvFinishActionCount;
	private TextView tvFinishActionBurning;
	
	private int mTotalTime;
	private int mTotalCount;
	private int mTotalKcal;
	private String mCourseName;
	private ArrayList<String> mActionIdList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finish);
		
		initView();
		
		initData();
	}

	private void initData() {
		int type = getIntent().getIntExtra(KEY_FINISH_OR_UNFINISH, TYPE_FINISH);
		if(type == TYPE_FINISH){
			mFinishedView = (View)LayoutInflater.from(this).inflate(R.layout.finish_child, null);
			tvFinishCourseName = (TextView)mFinishedView.findViewById(R.id.finish_data_course_name);
			tvFinishActionTime = (TextView)mFinishedView.findViewById(R.id.finish_time_text);
			tvFinishActionCount = (TextView)mFinishedView.findViewById(R.id.finish_count_text);
			tvFinishActionBurning = (TextView)mFinishedView.findViewById(R.id.finish_buring_text);
		}else{
			mFinishedView = (View)LayoutInflater.from(this).inflate(R.layout.finish_unfinish_child, null);
			tvFinishCourseName = (TextView)mFinishedView.findViewById(R.id.finish_data_course_name);
			tvFinishActionTime = (TextView)mFinishedView.findViewById(R.id.finish_time_text);
			tvFinishActionCount = (TextView)mFinishedView.findViewById(R.id.finish_count_text);
			tvFinishActionBurning = (TextView)mFinishedView.findViewById(R.id.finish_buring_text);
		}
		mFinishDataContainer.addView(mFinishedView);
		
		Intent intent = getIntent();
		mTotalTime = intent.getIntExtra(Const.KEY_COURSE_TOTAL_TIME, 0);
		mTotalCount = intent.getIntExtra(Const.KEY_COURSE_TOTAL_COUNT, 0);
		mTotalKcal = intent.getIntExtra(Const.KEY_COURSE_TOTAL_BURNING, 0);
		mCourseName = intent.getStringExtra(Const.KEY_COURSE_NAME);
		mActionIdList = intent.getStringArrayListExtra(Const.KEY_ACTION_IDS);
		
		tvFinishCourseName.setText(mCourseName);
		tvFinishActionTime.setText(DateUtil.secToTime(mTotalTime));
		tvFinishActionCount.setText(String.valueOf(mTotalCount));
		tvFinishActionBurning.setText(String.valueOf(mTotalKcal));
		
		mActionList = new ArrayList<Action>();
		int actionSize = mActionIdList.size();
		for(int i=0; i<actionSize; i++){
			String actionId = mActionIdList.get(i);
			Action action = ActionDao.getInstance().getActionFromDb(Globle.gApplicationContext, actionId);
			mActionList.add(action);
		}
	
		mHadFinishAdapter = new HadFinishAdapter(this, mActionList);
		mHadFinishListView.setAdapter(mHadFinishAdapter);
	}

	private void initView() {
		findViewById(R.id.actionbar_left_btn).setBackgroundResource(R.drawable.nav_back);
		
		mHadFinishListView = (ListView)findViewById(R.id.had_finished_train_list);
		mFinishDataContainer = (FrameLayout)findViewById(R.id.finish_data_container);
	}
	
	public void onClick(View v){
		switch (v.getId()) {
		case R.id.actionbar_left_btn:
			finish();
			break;

		default:
			break;
		}
	}
}
