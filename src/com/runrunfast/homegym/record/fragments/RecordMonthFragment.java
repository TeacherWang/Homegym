package com.runrunfast.homegym.record.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.dao.MyFinishDao;
import com.runrunfast.homegym.record.BaseRecordData;
import com.runrunfast.homegym.record.RecordAdapter;
import com.runrunfast.homegym.record.RecordDataDate;
import com.runrunfast.homegym.record.RecordDataPlan;
import com.runrunfast.homegym.record.RecordDataUnit;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;

public class RecordMonthFragment extends Fragment {
	private final String TAG = "RecordMonthFragment";
	
	private View rootView;
	
	private TextView tvTotalDays;
	
	private PullToRefreshListView pullToRefreshListView;
	
	private ArrayList<BaseRecordData> mBaseRecordDataList;
	
	private RecordAdapter mRecordAdapter;
	
	private UserInfo mUserInfo;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_record_month, container, false);
		
		initView();
		
		initData();
		
		return rootView;
	}
	
	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		String strCurrentDay = DateUtil.getCurrentDate();
		String strCurrentMonth = DateUtil.getDateStrOfYearMonth(strCurrentDay);
		int dayNum = MyFinishDao.getInstance().getFinishDayNumDependMonth(Globle.gApplicationContext, mUserInfo.strAccountId, strCurrentMonth);
		tvTotalDays.setText(String.valueOf(dayNum) + "天");
		
		mBaseRecordDataList = new ArrayList<BaseRecordData>();
		// 获取该天记录不同的课程数量
		ArrayList<String> courseIdList = MyFinishDao.getInstance().getFinishInfoDistinctCourseIdDependsDay(Globle.gApplicationContext, mUserInfo.strAccountId, strCurrentDay);
		
		int courseIdSize = courseIdList.size();
		// 根据不同的课程id，组成界面需要的不同的list
		for(int i=0; i<courseIdSize; i++){
			String courseId = courseIdList.get(i);
			ArrayList<BaseRecordData> baseRecordDataList = MyFinishDao.getInstance().getFinishInfoList(Globle.gApplicationContext, mUserInfo.strAccountId, courseId, strCurrentDay);
			// 取出第一个课程的第一个，组成界面显示的时间课程信息头
			RecordDataUnit firstDataUnit = (RecordDataUnit) baseRecordDataList.get(0);
			if(i == 0){
				RecordDataDate recordDataDate = new RecordDataDate();
				recordDataDate.strDate = strCurrentDay;
				recordDataDate.strCourseName = firstDataUnit.strCourseName;
				
				int dataSize = baseRecordDataList.size();
				int totalConsumeTime = 0;
				for(int j=0; j<dataSize; j++){
					RecordDataUnit recordDataUnit = (RecordDataUnit) baseRecordDataList.get(j);
					totalConsumeTime = totalConsumeTime + recordDataUnit.iConsumeTime;
				}
				recordDataDate.iConsumeTime = totalConsumeTime;
				
				baseRecordDataList.add(0, recordDataDate);
				mBaseRecordDataList.addAll(baseRecordDataList);
			}else{// 取出其他课程的第一个，组成界面显示的课程信息头
				RecordDataPlan recordDataPlan = new RecordDataPlan();
				recordDataPlan.strCoursId = firstDataUnit.strCoursId;
				recordDataPlan.strCourseName = firstDataUnit.strCourseName;
				
				int dataSize = baseRecordDataList.size();
				int totalConsumeTime = 0;
				for(int j=0; j<dataSize; j++){
					RecordDataUnit recordDataUnit = (RecordDataUnit) baseRecordDataList.get(j);
					totalConsumeTime = totalConsumeTime + recordDataUnit.iConsumeTime;
				}
				recordDataPlan.iConsumeTime = totalConsumeTime;
				baseRecordDataList.add(0, recordDataPlan);
				mBaseRecordDataList.addAll(baseRecordDataList);
			}
			
		}
		
		mRecordAdapter = new RecordAdapter(getActivity(), mBaseRecordDataList);
		pullToRefreshListView.setAdapter(mRecordAdapter);
		
		pullToRefreshListView.setMode(Mode.BOTH);
	}

	private void addPlanB() {
		RecordDataPlan recordDataPlan = new RecordDataPlan();
		recordDataPlan.strCoursId = "c2";
		recordDataPlan.strCourseName = "腹肌雕刻计划";
		recordDataPlan.iConsumeTime = 1000;
		mBaseRecordDataList.add(recordDataPlan);
		
		RecordDataUnit recordDataUnit = new RecordDataUnit();
		recordDataUnit.strCoursId = "c2";
		recordDataUnit.strCourseName = "腹肌雕刻计划";
		recordDataUnit.actionId = "a1";
		recordDataUnit.actionName = "平板式推举";
		recordDataUnit.iGroupCount = 5;
		recordDataUnit.iTotalKcal = 29;
		mBaseRecordDataList.add(recordDataUnit);
		
		RecordDataUnit recordDataUnit2 = new RecordDataUnit();
		recordDataUnit2.strCoursId = "c2";
		recordDataUnit2.strCourseName = "腹肌雕刻计划";
		recordDataUnit2.actionId = "a2";
		recordDataUnit2.actionName = "站式卧推举";
		recordDataUnit2.iGroupCount = 6;
		recordDataUnit2.iTotalKcal = 179;
		mBaseRecordDataList.add(recordDataUnit2);
	}

	private void initView() {
		pullToRefreshListView = (PullToRefreshListView)rootView.findViewById(R.id.record_month_pull_refresh_list);
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				Log.i(TAG, "onRefresh");
				
			}
		});
		
		tvTotalDays = (TextView)rootView.findViewById(R.id.record_detail_time_result_text);
	}

	// 不要删除，切换fragment用到
    @Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
}
