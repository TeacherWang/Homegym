package com.runrunfast.homegym.record.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.DataTransferUtil;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.dao.MyFinishDao;
import com.runrunfast.homegym.record.BaseRecordData;
import com.runrunfast.homegym.record.RecordAdapter;
import com.runrunfast.homegym.record.RecordDataDate;
import com.runrunfast.homegym.record.RecordDataPlan;
import com.runrunfast.homegym.record.RecordDataUnit;
import com.runrunfast.homegym.record.RecordUtil;
import com.runrunfast.homegym.record.StatisticalData;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;

public class RecordMonthFragment extends Fragment implements OnClickListener{
	private final String TAG = "RecordMonthFragment";
	
	private View rootView;
	
	private TextView tvTotalDays, tvSelectMonth, tvMonth;
	private Button btnAddMonth, btnReduceMonth;
	
	private PullToRefreshListView pullToRefreshListView;
	
	private ArrayList<BaseRecordData> mBaseRecordDataList;
	
	private RecordAdapter mRecordAdapter;
	
	private UserInfo mUserInfo;
	
	private int mSelectMonth; // 1-12月
	
	private int mThisMonth; // 本月
	
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
		
		mSelectMonth = DateUtil.getThisMonth();
		mThisMonth = mSelectMonth;
		
		tvSelectMonth.setText(mSelectMonth + "月");
		tvMonth.setText(DataTransferUtil.numMap.get(mSelectMonth) + "月份");
		
		String strCurrentDay = DateUtil.getCurrentDate();
		String strCurrentYearMonth = DateUtil.getDateStrOfYearMonth(strCurrentDay);
		
		ArrayList<StatisticalData> statisticalDataList = MyFinishDao.getInstance().getDayStatisticalDataDependYearMonth(Globle.gApplicationContext, mUserInfo.strAccountId, strCurrentYearMonth);
		
		int dayNum = MyFinishDao.getInstance().getTrainDayNumDependMonth(Globle.gApplicationContext, mUserInfo.strAccountId, strCurrentYearMonth);
		tvTotalDays.setText(String.valueOf(dayNum) + "天");
		
		mBaseRecordDataList = new ArrayList<BaseRecordData>();
		
		mBaseRecordDataList = RecordUtil.getRecordDataOfDay(strCurrentDay, mUserInfo.strAccountId);
		
		mRecordAdapter = new RecordAdapter(getActivity(), mBaseRecordDataList);
		pullToRefreshListView.setAdapter(mRecordAdapter);
		
		pullToRefreshListView.setMode(Mode.BOTH);
	}

	private ArrayList<BaseRecordData> getRecordDataOfDay(String strDay, String uid) {
		ArrayList<BaseRecordData> baseRecordDataForUiList = new ArrayList<BaseRecordData>();
		ArrayList<String> courseIdList = MyFinishDao.getInstance().getFinishInfoDistinctCourseIdDependsDay(Globle.gApplicationContext, uid, strDay);
		
		int courseIdSize = courseIdList.size();
		// 根据不同的课程id，组成界面需要的不同的list
		for(int i=0; i<courseIdSize; i++){
			String courseId = courseIdList.get(i);
			ArrayList<BaseRecordData> baseRecordDataList = MyFinishDao.getInstance().getFinishInfoList(Globle.gApplicationContext, uid, courseId, strDay);
			// 取出第一个课程的第一个，组成界面显示的时间课程信息头
			RecordDataUnit firstDataUnit = (RecordDataUnit) baseRecordDataList.get(0);
			if(i == 0){
				RecordDataDate recordDataDate = new RecordDataDate();
				recordDataDate.strDate = strDay;
				recordDataDate.strCourseName = firstDataUnit.strCourseName;
				
				int dataSize = baseRecordDataList.size();
				int totalConsumeTime = 0;
				for(int j=0; j<dataSize; j++){
					RecordDataUnit recordDataUnit = (RecordDataUnit) baseRecordDataList.get(j);
					totalConsumeTime = totalConsumeTime + recordDataUnit.iConsumeTime;
				}
				recordDataDate.iConsumeTime = totalConsumeTime;
				
				baseRecordDataList.add(0, recordDataDate);
//				mBaseRecordDataList.addAll(baseRecordDataList);
				baseRecordDataForUiList.addAll(baseRecordDataList);
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
//				mBaseRecordDataList.addAll(baseRecordDataList);
				baseRecordDataForUiList.addAll(baseRecordDataList);
			}
		}
		
		return baseRecordDataForUiList;
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
		btnAddMonth = (Button)rootView.findViewById(R.id.btn_record_add);
		btnReduceMonth = (Button)rootView.findViewById(R.id.btn_record_reduce);
		
		btnAddMonth.setOnClickListener(this);
		btnReduceMonth.setOnClickListener(this);
		
		tvSelectMonth = (TextView)rootView.findViewById(R.id.tv_record_month_year);
		tvMonth = (TextView)rootView.findViewById(R.id.record_detail_time_text);
	}

	// 不要删除，切换fragment用到
    @Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_record_add:
			nextMonthData();
			break;
			
		case R.id.btn_record_reduce:
			lastMonthData();
			break;

		default:
			break;
		}
	}

	private void lastMonthData() {
		if(mSelectMonth == 1){
			
			return;
		}
		
		mSelectMonth--;
		
		tvSelectMonth.setText(mSelectMonth + "月");
		tvMonth.setText(DataTransferUtil.numMap.get(mSelectMonth) + "月份");
		
		String strCurrentDay;
		
		if( mSelectMonth == mThisMonth ){
			strCurrentDay = DateUtil.getCurrentDate();
		}else{
			strCurrentDay = DateUtil.getStrDateFirstDayDependsMonth(mSelectMonth);
		}
		
		String strCurrentYearMonth = DateUtil.getDateStrOfYearMonth(strCurrentDay);
		
		updateDataDependOnDay(strCurrentDay, strCurrentYearMonth);
	}

	private void nextMonthData() {
		if(mSelectMonth == 12){
			return;
		}
		
		mSelectMonth++;
		
		tvSelectMonth.setText(mSelectMonth + "月");
		tvMonth.setText(DataTransferUtil.numMap.get(mSelectMonth) + "月份");
		
		String strCurrentDay;
		
		if( mSelectMonth == mThisMonth ){
			strCurrentDay = DateUtil.getCurrentDate();
		}else{
			strCurrentDay = DateUtil.getStrDateFirstDayDependsMonth(mSelectMonth);
		}
		
		String strCurrentYearMonth = DateUtil.getDateStrOfYearMonth(strCurrentDay);
		
		updateDataDependOnDay(strCurrentDay, strCurrentYearMonth);
	}
	
	private void updateDataDependOnDay(String strCurrentDay, String strCurrentMonth) {
		int dayNum = MyFinishDao.getInstance().getTrainDayNumDependMonth(Globle.gApplicationContext, mUserInfo.strAccountId, strCurrentMonth);
		tvTotalDays.setText(String.valueOf(dayNum) + "天");
		
		mBaseRecordDataList.clear();
		mBaseRecordDataList = RecordUtil.getRecordDataOfDay(strCurrentDay, mUserInfo.strAccountId);
		mRecordAdapter.updateData(mBaseRecordDataList);
	}
}
