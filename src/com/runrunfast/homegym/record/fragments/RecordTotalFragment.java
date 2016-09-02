package com.runrunfast.homegym.record.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.DataTransferUtil;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.course.CourseServerMgr;
import com.runrunfast.homegym.course.CourseServerMgr.IRequestTotalDataListener;
import com.runrunfast.homegym.dao.MyTotalRecordDao;
import com.runrunfast.homegym.record.TotalRecord;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

public class RecordTotalFragment extends Fragment {
	private UserInfo mUserInfo;
	
	private View rootView;
	
	private TextView tvTotalKcal, tvTotalTimeHour, tvTotalDays, tvFood;
	
	private IRequestTotalDataListener mIRequestTotalDataListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
		 ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_record_total, container, false);
		
		initView();
		
		initData();
		
		initCourseServerListener();
		
		return rootView;
	}
	
	private void initCourseServerListener() {
		mIRequestTotalDataListener = new IRequestTotalDataListener() {
			
			@Override
			public void onRequestTotalDataSuc(TotalRecord totalRecord) {
				MyTotalRecordDao.getInstance().saveMyTotalRecordToDb(Globle.gApplicationContext, totalRecord, mUserInfo.strAccountId);
				setUiData(totalRecord);
			}
			
			@Override
			public void onRequestTotalDataFail() {
				
			}
		};
		CourseServerMgr.getInstance().addRequestTotalDataObserver(mIRequestTotalDataListener);
	}

	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		TotalRecord totalRecord = MyTotalRecordDao.getInstance().getMyTotalRecordFromDb(Globle.gApplicationContext, mUserInfo.strAccountId);
		
		setUiData(totalRecord);
		
		CourseServerMgr.getInstance().requestTotalData(mUserInfo.strAccountId);
	}

	private void setUiData(TotalRecord totalRecord) {
		tvTotalKcal.setText(DataTransferUtil.getOneDecimalData((totalRecord.total_kcal)));
		tvTotalDays.setText(String.valueOf(totalRecord.total_days));
		tvTotalTimeHour.setText(DateUtil.secToHour(totalRecord.total_time));
		tvFood.setText(DataTransferUtil.getOneDecimalData(totalRecord.total_food) + "个汉堡包");
	}

	private void initView() {
		tvTotalKcal = (TextView)rootView.findViewById(R.id.record_total_consume_number_text);
		tvTotalTimeHour = (TextView)rootView.findViewById(R.id.record_total_finish_time_text);
		tvTotalDays = (TextView)rootView.findViewById(R.id.record_total_finish_days_text);
		tvFood = (TextView)rootView.findViewById(R.id.record_total_equal_result_text);
	}

	@Override
	public void onDestroyView() {
		if(mIRequestTotalDataListener != null){
			CourseServerMgr.getInstance().removeRequestTotalDataObserver(mIRequestTotalDataListener);
		}
		super.onDestroyView();
	}
	
	// 不要删除，切换fragment用到
    @Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
}
