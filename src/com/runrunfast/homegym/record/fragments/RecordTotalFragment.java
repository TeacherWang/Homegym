package com.runrunfast.homegym.record.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.dao.MyTotalRecordDao;
import com.runrunfast.homegym.record.TotalRecord;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

public class RecordTotalFragment extends Fragment {
	private UserInfo mUserInfo;
	
	private View rootView;
	
	private TextView tvTotalKcal, tvTotalTimeHour, tvTotalDays;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
		 ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_record_total, container, false);
		
		initView();
		
		initData();
		
		return rootView;
	}
	
	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		TotalRecord totalRecord = MyTotalRecordDao.getInstance().getMyTotalRecordFromDb(Globle.gApplicationContext, mUserInfo.strAccountId);
		
		tvTotalKcal.setText(String.valueOf(totalRecord.total_kcal));
		tvTotalDays.setText(String.valueOf(totalRecord.total_days));
		tvTotalTimeHour.setText(DateUtil.secToHour(totalRecord.total_time));
	}

	private void initView() {
		tvTotalKcal = (TextView)rootView.findViewById(R.id.record_total_consume_number_text);
		tvTotalTimeHour = (TextView)rootView.findViewById(R.id.record_total_finish_time_text);
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
