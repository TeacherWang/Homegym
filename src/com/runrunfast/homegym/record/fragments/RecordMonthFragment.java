package com.runrunfast.homegym.record.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.runrunfast.homegym.R;
import com.runrunfast.homegym.record.BaseRecordData;
import com.runrunfast.homegym.record.RecordDataDate;
import com.runrunfast.homegym.utils.DateUtil;

public class RecordMonthFragment extends Fragment {
	private View rootView;
	private PullToRefreshListView pullToRefreshListView;
	
	private ArrayList<BaseRecordData> mBaseRecordDataList;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_record_month, container, false);
		initView();
		
		initData();
		return rootView;
	}
	
	private void initData() {
		mBaseRecordDataList = new ArrayList<BaseRecordData>();
		
		RecordDataDate recordDataDate = new RecordDataDate();
		recordDataDate.strDate = DateUtil.getCurrentDate();
		recordDataDate.coursId = 1;
		recordDataDate.courseName = "21天增肌计划";
		recordDataDate.strConsumeTime = DateUtil.secToTime(10000);
		
	}

	private void initView() {
		pullToRefreshListView = (PullToRefreshListView)rootView.findViewById(R.id.record_month_pull_refresh_list);
	}

	// 不要删除，切换fragment用到
    @Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
}
