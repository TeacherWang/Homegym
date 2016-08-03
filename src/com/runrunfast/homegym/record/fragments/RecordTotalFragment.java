package com.runrunfast.homegym.record.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.dao.MyFinishDao;
import com.runrunfast.homegym.record.BaseRecordData;
import com.runrunfast.homegym.record.RecordDataUnit;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;

public class RecordTotalFragment extends Fragment {
	private UserInfo mUserInfo;
	
	private View rootView;
	
	private TextView tvTotalKcal, tvTotalTimeHour, tvTotalFinishCount, tvTotalCount, tvTotalDays;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_record_total, container, false);
		
		initView();
		
		initData();
		
		return rootView;
	}
	
	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		ArrayList<BaseRecordData> baseRecordDataList = MyFinishDao.getInstance().getFinishInfoList(Globle.gApplicationContext, mUserInfo.strAccountId);
		int totalKcal = 0;
		int totalTime = 0;
		int totalCount = 0;
		int totalDays =  MyFinishDao.getInstance().getFinishDayNum(Globle.gApplicationContext, mUserInfo.strAccountId);
		
		int dataSize = baseRecordDataList.size();
		for(int i=0; i<dataSize; i++){
			RecordDataUnit unitRecordData = (RecordDataUnit) baseRecordDataList.get(i);
			totalKcal = totalKcal + unitRecordData.iTotalKcal;
			totalTime = totalTime + unitRecordData.iConsumeTime;
			totalCount = totalCount + unitRecordData.iCount;
		}
		
		tvTotalCount.setText(String.valueOf(totalCount));
		tvTotalKcal.setText(String.valueOf(totalKcal));
		tvTotalDays.setText(String.valueOf(totalDays));
		tvTotalTimeHour.setText(DateUtil.secToHour(totalTime));
	}

	private void initView() {
		tvTotalKcal = (TextView)rootView.findViewById(R.id.record_total_consume_number_text);
		tvTotalTimeHour = (TextView)rootView.findViewById(R.id.record_total_finish_time_text);
		tvTotalFinishCount = (TextView)rootView.findViewById(R.id.record_total_finish_count_text); // 完成的次数
		tvTotalCount = (TextView)rootView.findViewById(R.id.record_total_train_total_count); // 不管完成没完成，一共训练的次数
		tvTotalDays = (TextView)rootView.findViewById(R.id.record_total_finish_days_text);
	}

	// 不要删除，切换fragment用到
    @Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
}
