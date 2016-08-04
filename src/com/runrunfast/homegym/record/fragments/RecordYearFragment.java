package com.runrunfast.homegym.record.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
import com.runrunfast.homegym.record.RecordUtil;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

public class RecordYearFragment extends Fragment {
	private final String TAG = "RecordYearFragment";
	
	private View rootView;
	
	private PullToRefreshListView pullToRefreshListView;
	
	private ArrayList<BaseRecordData> mBaseRecordDataList;
	
	private RecordAdapter mRecordAdapter;
	
	private UserInfo mUserInfo;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_record_year, container, false);
		
		initView();
		
		initData();
		
		return rootView;
	}
	
	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		mBaseRecordDataList = new ArrayList<BaseRecordData>();
		String currentYearMonth = DateUtil.getDateStrOfYearMonth(DateUtil.getCurrentDate());
		
		ArrayList<String> dateListOfMonth = MyFinishDao.getInstance().getFinishInfoDistinctDateDependsMonth(Globle.gApplicationContext, mUserInfo.strAccountId, currentYearMonth);
		int dateSize = dateListOfMonth.size();
		
		for(int i=0; i<dateSize; i++){
			String strDate = dateListOfMonth.get(i);
			
			ArrayList<BaseRecordData> baseRecordDataOfDayList = RecordUtil.getRecordDataOfDay(strDate, mUserInfo.strAccountId);
			mBaseRecordDataList.addAll(baseRecordDataOfDayList);
		}
		
		mRecordAdapter = new RecordAdapter(getActivity(), mBaseRecordDataList);
		pullToRefreshListView.setAdapter(mRecordAdapter);
		pullToRefreshListView.setMode(Mode.PULL_FROM_END);
	}

	private void addDate2PlanB() {
		RecordDataPlan recordDataPlan = new RecordDataPlan();
		recordDataPlan.strCoursId = "c4";
		recordDataPlan.strCourseName = "腹肌雕刻计划2";
		recordDataPlan.iConsumeTime = 2300;
		mBaseRecordDataList.add(recordDataPlan);
		
		RecordDataUnit recordDataUnit = new RecordDataUnit();
		recordDataUnit.strCoursId = "c4";
		recordDataUnit.strCourseName = "腹肌雕刻计划2";
		recordDataUnit.actionId = "a1";
		recordDataUnit.actionName = "平板式推举";
		recordDataUnit.iGroupCount = 5;
		recordDataUnit.iTotalKcal = 29;
		mBaseRecordDataList.add(recordDataUnit);
		
		RecordDataUnit recordDataUnit2 = new RecordDataUnit();
		recordDataUnit2.strCoursId = "c4";
		recordDataUnit2.strCourseName = "腹肌雕刻计划2";
		recordDataUnit2.actionId = "a2";
		recordDataUnit2.actionName = "站式卧推举";
		recordDataUnit2.iGroupCount = 6;
		recordDataUnit2.iTotalKcal = 179;
		mBaseRecordDataList.add(recordDataUnit2);
	}

	private void addDate2PlanA() {
		RecordDataDate recordDataDate = new RecordDataDate();
		recordDataDate.strDate = DateUtil.getCurrentDate();
		recordDataDate.strCoursId = "c3";
		recordDataDate.strCourseName = "21天增肌计划2";
		recordDataDate.iConsumeTime = 2800;
		mBaseRecordDataList.add(recordDataDate);
		
		RecordDataUnit recordDataUnit = new RecordDataUnit();
		recordDataUnit.strCoursId = "c3";
		recordDataUnit.strCourseName = "21天增肌计划2";
		recordDataUnit.actionId = "a1";
		recordDataUnit.actionName = "平板式推举";
		recordDataUnit.iGroupCount = 8;
		recordDataUnit.iTotalKcal = 434;
		mBaseRecordDataList.add(recordDataUnit);
		
		RecordDataUnit recordDataUnit2 = new RecordDataUnit();
		recordDataUnit2.strCoursId = "c3";
		recordDataUnit2.strCourseName = "21天增肌计划2";
		recordDataUnit2.actionId = "a1";
		recordDataUnit2.actionName = "站式卧推举";
		recordDataUnit2.iGroupCount = 3;
		recordDataUnit2.iTotalKcal = 479;
		mBaseRecordDataList.add(recordDataUnit2);
	}

	private void addPlanA() {
		RecordDataDate recordDataDate = new RecordDataDate();
		recordDataDate.strDate = DateUtil.getCurrentDate();
		recordDataDate.strCoursId = "c1";
		recordDataDate.strCourseName = "21天增肌计划";
		recordDataDate.iConsumeTime = 1000;
		mBaseRecordDataList.add(recordDataDate);
		
		RecordDataUnit recordDataUnit = new RecordDataUnit();
		recordDataUnit.strCoursId = "c1";
		recordDataUnit.strCourseName = "21天增肌计划";
		recordDataUnit.actionId = "a1";
		recordDataUnit.actionName = "平板式推举";
		recordDataUnit.iGroupCount = 2;
		recordDataUnit.iTotalKcal = 49;
		mBaseRecordDataList.add(recordDataUnit);
		
		RecordDataUnit recordDataUnit2 = new RecordDataUnit();
		recordDataUnit2.strCoursId = "c2";
		recordDataUnit2.strCourseName = "21天增肌计划";
		recordDataUnit2.actionId = "a1";
		recordDataUnit2.actionName = "站式卧推举";
		recordDataUnit2.iGroupCount = 3;
		recordDataUnit2.iTotalKcal = 79;
		mBaseRecordDataList.add(recordDataUnit2);
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
	}
	
	// 不要删除，切换fragment用到
    @Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
}
