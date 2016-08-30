package com.runrunfast.homegym.record.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
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
import com.runrunfast.homegym.course.CourseServerMgr;
import com.runrunfast.homegym.course.CourseServerMgr.IRequestDetailDataListener;
import com.runrunfast.homegym.dao.MyTrainRecordDao;
import com.runrunfast.homegym.record.BaseRecordData;
import com.runrunfast.homegym.record.RecordAdapter;
import com.runrunfast.homegym.record.RecordUtil;
import com.runrunfast.homegym.record.StatisticalData;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.DensityUtil;
import com.runrunfast.homegym.utils.Globle;
import com.runrunfast.homegym.widget.HistogramView;
import com.runrunfast.homegym.widget.HistogramView.Bar;
import com.runrunfast.homegym.widget.HistogramView.OnClickCountListener;

import java.util.ArrayList;
import java.util.Calendar;

public class RecordMonthFragment extends Fragment implements OnClickListener{
	private final String TAG = "RecordMonthFragment";
	
	private Resources mResources;
	
	private View rootView;
	
	private TextView tvTotalDays, tvSelectMonth, tvMonth;
	private Button btnAddMonth, btnReduceMonth;
	
	private PullToRefreshListView pullToRefreshListView;
	
	private ArrayList<BaseRecordData> mBaseRecordDataList;
	
	private RecordAdapter mRecordAdapter;
	
	private ArrayList<StatisticalData> mStatisticalDataList;
	
	private UserInfo mUserInfo;
	
	private int mThisYear;
	
	private int mSelectMonth; // 1-12月
	
	private int mThisMonth; // 本月
	
	private String mStrSelectYearMonth;
	
	// 柱状图
	private HistogramView mHistogramView;
	
	private int screenWidth;
	
	private OnClickCountListener mOnClickCountListener;
	
	// 柱状图
//	private ColumnChartView chart;
//	private ColumnChartData data;
//	private boolean hasAxes = true;
//    private boolean hasAxesNames = true;
//    private boolean hasLabels = false;
//    private boolean hasLabelForSelected = false;
	
	private IRequestDetailDataListener mIRequestDetailDataListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
		 ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_record_month, container, false);
		
		mResources = getResources();
		
		initView();
		
		initData();
		
		initCourseServerListener();
		
		return rootView;
	}
	
	private void initCourseServerListener() {
		mIRequestDetailDataListener = new IRequestDetailDataListener() {
			
			@Override
			public void onRequestDetailDataSuc(String strStartDate) {
				handleServerData(strStartDate);
			}
			
			@Override
			public void onRequestDetailDataFail() {
				
			}
		};
		CourseServerMgr.getInstance().addRequestDetailDataObserver(mIRequestDetailDataListener);
	}

	private void handleServerData(String strStartDate) {
		String serverDateYearMonth = DateUtil.getDateStrOfYearMonth(strStartDate);
		Log.i(TAG, "handleServerData, server startDate = " + strStartDate + ", mStrSelectYearMonth = " + mStrSelectYearMonth);
		if(serverDateYearMonth.equals(mStrSelectYearMonth)){
			updateData();
		}
	}

	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		mSelectMonth = DateUtil.getThisMonth();
		mThisMonth = mSelectMonth;
		mThisYear = Calendar.getInstance().get(Calendar.YEAR);
		
		tvSelectMonth.setText(mSelectMonth + "月");
		tvMonth.setText(DataTransferUtil.numMap.get(mSelectMonth) + "月份");
		
		String strCurrentDay = DateUtil.getCurrentDate();
		mStrSelectYearMonth = DateUtil.getDateStrOfYearMonth(strCurrentDay);
		
		int dayNum = MyTrainRecordDao.getInstance().getTrainDayNumDependMonth(Globle.gApplicationContext, mUserInfo.strAccountId, mStrSelectYearMonth);
		tvTotalDays.setText(String.valueOf(dayNum) + "天");
		
		mBaseRecordDataList = new ArrayList<BaseRecordData>();
		
		mBaseRecordDataList = RecordUtil.getBaseRecordDataList(mStrSelectYearMonth, mUserInfo.strAccountId);
		
		mRecordAdapter = new RecordAdapter(getActivity(), mBaseRecordDataList);
		pullToRefreshListView.setAdapter(mRecordAdapter);
		
		pullToRefreshListView.setMode(Mode.DISABLED);
		
		initChart(mStrSelectYearMonth);
		
		CourseServerMgr.getInstance().requestDetailData(mUserInfo.strAccountId, mStrSelectYearMonth + "-01", mStrSelectYearMonth + "-31");
	}

	private void initChart(String strCurrentYearMonth) {
		mStatisticalDataList = MyTrainRecordDao.getInstance().getDayStatisticalDataDependYearMonth(Globle.gApplicationContext, mUserInfo.strAccountId, strCurrentYearMonth);
		generateHistogramBar(mStatisticalDataList);
	}

	private void generateHistogramBar(ArrayList<StatisticalData> statisticalDataList) {
		ArrayList<Bar> barList = new ArrayList<HistogramView.Bar>();
		float maxValue = 0;
		int dataSize = statisticalDataList.size();
		for(int i=0; i<dataSize; i++){
			StatisticalData statisticalData = statisticalDataList.get(i);
			if(statisticalData.totalKcal > maxValue){
				maxValue = statisticalData.totalKcal;
			}
		}
		
		for(int i=0; i<dataSize; i++){
			StatisticalData statisticalData = statisticalDataList.get(i);
			int month = DateUtil.getMonth(statisticalData.strDate);
			int dayOfMonth = DateUtil.getDayOfMonth(statisticalData.strDate);
			String bootomText = month + "/" + dayOfMonth;
			float ratio = (float)statisticalData.totalKcal / (float)maxValue;
			
			Log.i("RecordMonthFragment", "generateHistogramBar, ratio = " + ratio);
			int color = mResources.getColor(R.color.chart_color_normal);
//			if(i == 0){
//				color = mResources.getColor(R.color.chart_color_normal);
//			}else{
//				color = mResources.getColor(R.color.chart_color_select);
//			}
			
			Bar bar = mHistogramView.new Bar(i+1, ratio, color, bootomText, DataTransferUtil.getInstance().getTwoDecimalData(statisticalData.totalKcal));
			barList.add(bar);
		}
		
//		for(int i=0; i<31; i++){
//			int color = mResources.getColor(R.color.chart_color_normal);
//			
//			Bar bar = mHistogramView.new Bar(i+1, 0.5f, color, (i+1) + "", String.valueOf(100));
//			barList.add(bar);
//		}
		
		LayoutParams params = (LayoutParams) mHistogramView.getLayoutParams();
		int padding = (int) mResources.getDimension(R.dimen.chat_margin_left_right);
		int layoutWidth = screenWidth - padding * 2;
		int contentWidth = (int) (dataSize * 2 * mResources.getDimension(R.dimen.chat_column_width) + mResources.getDimension(R.dimen.chat_column_width));
		if(contentWidth < layoutWidth){
			params.width = layoutWidth;
		}else{
			params.width = contentWidth;
		}
		mHistogramView.setLayoutParams(params);
		
		mHistogramView.setBarLists(barList);
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
		
		mHistogramView = (HistogramView)rootView.findViewById(R.id.record_chart);
		mHistogramView.setCanClickable(false);
		initHistogramViewClickListener();
		
		screenWidth = DensityUtil.getScreenWidth(getActivity());
	}
	
	private void initHistogramViewClickListener() {
		mOnClickCountListener = new OnClickCountListener() {
			
			@Override
			public void onSingleClick(int position) {
				StatisticalData statisticalData = mStatisticalDataList.get(position);
				Log.i(TAG, "onSingleClick, select date = " + statisticalData.strDate);
				
//				String selectDay = mThisYear + "-" + String.format("%02d", mSelectMonth) + "-" + String.format("%02d", columnIndex + 1);
//	            handleDaySelected(selectDay);
			}
		};
		mHistogramView.setOnClickCountListener(mOnClickCountListener);
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

	private void handleDaySelected(String strDateSelectDay) {
		String strCurrentYearMonth = DateUtil.getDateStrOfYearMonth(strDateSelectDay);
		updateDataDependOnDay(strCurrentYearMonth);
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
		
		mStrSelectYearMonth = DateUtil.getDateStrOfYearMonth(strCurrentDay);
		
		int dayNum = MyTrainRecordDao.getInstance().getTrainDayNumDependMonth(Globle.gApplicationContext, mUserInfo.strAccountId, mStrSelectYearMonth);
		tvTotalDays.setText(String.valueOf(dayNum) + "天");
		
		CourseServerMgr.getInstance().requestDetailData(mUserInfo.strAccountId, mStrSelectYearMonth + "-01", mStrSelectYearMonth + "-31");
		
		updateData();
	}

	private void updateData() {
		updateDataDependOnDay(mStrSelectYearMonth);
		
		initChart(mStrSelectYearMonth);
	}

	private void nextMonthData() {
		if(mSelectMonth == 12){
			return;
		}
		
		mSelectMonth++;
		
		tvSelectMonth.setText(mSelectMonth + "月");
		tvMonth.setText(DataTransferUtil.numMap.get(mSelectMonth) + "月份");
		
		String strSelectDay;
		
		if( mSelectMonth == mThisMonth ){
			strSelectDay = DateUtil.getCurrentDate();
		}else{
			strSelectDay = DateUtil.getStrDateFirstDayDependsMonth(mSelectMonth);
		}
		
		mStrSelectYearMonth = DateUtil.getDateStrOfYearMonth(strSelectDay);
		
		int dayNum = MyTrainRecordDao.getInstance().getTrainDayNumDependMonth(Globle.gApplicationContext, mUserInfo.strAccountId, mStrSelectYearMonth);
		tvTotalDays.setText(String.valueOf(dayNum) + "天");
		
		CourseServerMgr.getInstance().requestDetailData(mUserInfo.strAccountId, mStrSelectYearMonth + "-01", mStrSelectYearMonth + "-31");
		
		updateData();
	}
	
	private void updateDataDependOnDay(String strCurrentYearMonth) {
		
		mBaseRecordDataList = RecordUtil.getBaseRecordDataList(strCurrentYearMonth, mUserInfo.strAccountId);
		
		mRecordAdapter.updateData(mBaseRecordDataList);
	}
	
	@Override
	public void onDestroyView() {
		if(mIRequestDetailDataListener != null){
			CourseServerMgr.getInstance().removeRequestDetailDataObserver(mIRequestDetailDataListener);
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
