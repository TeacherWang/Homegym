package com.runrunfast.homegym.home;

import java.util.ArrayList;

import com.runrunfast.homegym.R;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;

public class FinishActivity extends Activity {
	
	public static final String KEY_FINISH_OR_UNFINISH = "key_finish_or_unfinish";
	public static final int TYPE_FINISH = 1;
	public static final int TYPE_UNFINISH = 2;
	
	private ListView mHadFinishListView;
	private HadFinishAdapter mHadFinishAdapter;
	private ArrayList<HadFinishInfo> mHadFinishInfoList;
	
	private FrameLayout mFinishDataContainer;
	private View mFinishedView;
	
	private Button btnBack;
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
		}else{
			mFinishedView = (View)LayoutInflater.from(this).inflate(R.layout.finish_unfinish_child, null);
		}
		
		mFinishDataContainer.addView(mFinishedView);
		
		findViewById(R.id.actionbar_left_btn).setBackgroundResource(R.drawable.nav_back);
		
		mHadFinishInfoList = new ArrayList<HadFinishInfo>();
		
		HadFinishInfo hadFinishInfo1 = new HadFinishInfo();
		hadFinishInfo1.iTrainId = 1;
		hadFinishInfo1.strTrainName = "平板卧推举";
		hadFinishInfo1.strActionNum = "动作一";
		mHadFinishInfoList.add(hadFinishInfo1);
		
		HadFinishInfo hadFinishInfo2 = new HadFinishInfo();
		hadFinishInfo2.iTrainId = 2;
		hadFinishInfo2.strTrainName = "平板卧推举";
		hadFinishInfo2.strActionNum = "动作二";
		mHadFinishInfoList.add(hadFinishInfo2);
	
		mHadFinishAdapter = new HadFinishAdapter(this, mHadFinishInfoList);
		mHadFinishListView.setAdapter(mHadFinishAdapter);
	}

	private void initView() {
		mHadFinishListView = (ListView)findViewById(R.id.had_finished_train_list);
		mFinishDataContainer = (FrameLayout)findViewById(R.id.finish_data_container);
	}
}
