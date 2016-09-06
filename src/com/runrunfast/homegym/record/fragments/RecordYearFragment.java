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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

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

public class RecordYearFragment extends Fragment implements OnClickListener{
	private final String TAG = "RecordYearFragment";
	
	private Resources mResources;
	
	private View rootView;
	
	private TextView tvTotalDays, tvSelectYear, tvYear;
	private Button btnAddMonth, btnReduceMonth;
	
	private PullToRefreshListView pullToRefreshListView;
	
	private ArrayList<BaseRecordData> mBaseRecordDataList;
	
	private RecordAdapter mRecordAdapter;
	
	private UserInfo mUserInfo;
	
	private int mSelectYear; // 选中年份
	
	private int mThisYear; // 今年
	
	private String mSelectYearMonth;
	
	// 柱状图
	private HistogramView mHistogramView;
	private OnClickCountListener mOnClickCountListener;
	
	private ArrayList<StatisticalData> mStatisticalDataList;
	
	private int screenWidth;
	
	private IRequestDetailDataListener mIRequestDetailDataListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
		 ViewGroup container, Bundle savedInstanceState) {
		mResources = getResources();
		
		rootView = inflater.inflate(R.layout.fragment_record_year, container, false);
		
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
		int serverYear = DateUtil.getYearOfDate(strStartDate);
		
		if(serverYear == mThisYear){
			mSelectYearMonth = DateUtil.getDateStrOfYearMonth(DateUtil.getCurrentDate());
		}else{
			mSelectYearMonth = DateUtil.getStrDateFirstDayDependsYear(mSelectYear);
		}
		
		updateDataDependOnYearMonth(mSelectYearMonth);
	}
	
	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		mSelectYear = DateUtil.getThisYear();
		mThisYear = mSelectYear;
		
		tvSelectYear.setText(String.valueOf(mSelectYear));
		tvYear.setText(mSelectYear + "年");
		
		int dayNum = MyTrainRecordDao.getInstance().getTrainDayNumDependYear(Globle.gApplicationContext, mUserInfo.strAccountId, mSelectYear);
		tvTotalDays.setText(String.valueOf(dayNum) + "天");
		
		mBaseRecordDataList = new ArrayList<BaseRecordData>();
		
		mSelectYearMonth = DateUtil.getDateStrOfYearMonth(DateUtil.getCurrentDate());
		
		mBaseRecordDataList = RecordUtil.getBaseRecordDataList(mSelectYearMonth, mUserInfo.strAccountId);
		
		mRecordAdapter = new RecordAdapter(getActivity(), mBaseRecordDataList);
		pullToRefreshListView.setAdapter(mRecordAdapter);
		pullToRefreshListView.setMode(Mode.DISABLED);
		
		initChart(mSelectYear);
		
		CourseServerMgr.getInstance().requestDetailData(mUserInfo.strAccountId, mSelectYearMonth + "-01", mSelectYearMonth + "-31");
	}

	private void initChart(int selectYear) {
		mStatisticalDataList = MyTrainRecordDao.getInstance().getMonthStatisticalDataDependYear(Globle.gApplicationContext, mUserInfo.strAccountId, selectYear);
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
			int month = DateUtil.getMonthOfYearMonth(statisticalData.strDate); // 格式：2016-08
			String bootomText = DataTransferUtil.numMap.get(month) + "月";
			float ratio = (float)statisticalData.totalKcal / (float)maxValue;
			
			Log.i("RecordMonthFragment", "generateHistogramBar, ratio = " + ratio);
			int color = mResources.getColor(R.color.chart_color_normal);
			if(i == 0){
				color = mResources.getColor(R.color.chart_color_normal);
			}else{
				color = mResources.getColor(R.color.chart_color_select);
				mHistogramView.setSelectPosition(i);
			}
			
			Bar bar = mHistogramView.new Bar(i+1, ratio, color, bootomText, DataTransferUtil.getInstance().getTwoDecimalData(statisticalData.totalKcal));
			barList.add(bar);
		}
		
//		for(int i=0; i<31; i++){
//			StatisticalData statisticalData = statisticalDataList.get(0);
//			int month = DateUtil.getMonthOfYearMonth(statisticalData.strDate); // 格式：2016-08
//			String bootomText = DataTransferUtil.numMap.get(month) + "月";
//			float ratio = (float)statisticalData.totalKcal / (float)maxValue;
//			
//			Log.i("RecordMonthFragment", "generateHistogramBar, ratio = " + ratio);
//			int color = mResources.getColor(R.color.chart_color_normal);
//			if(i == 0){
//				color = mResources.getColor(R.color.chart_color_normal);
//			}else{
//				color = mResources.getColor(R.color.chart_color_select);
//				mHistogramView.setSelectPosition(i);
//			}
//			
//			Bar bar = mHistogramView.new Bar(i+1, ratio, color, bootomText, String.valueOf(statisticalData.totalKcal));
//			barList.add(bar);
//		}
		
		LayoutParams params = (LayoutParams) mHistogramView.getLayoutParams();
		int padding = (int) mResources.getDimension(R.dimen.chat_margin_left_right);
		int layoutWidth = screenWidth - padding * 2;
		int contentWidth = (int) (barList.size() * 2 * mResources.getDimension(R.dimen.chat_column_width) + mResources.getDimension(R.dimen.chat_column_width));
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
		
		tvSelectYear = (TextView)rootView.findViewById(R.id.tv_record_month_year);
		tvYear = (TextView)rootView.findViewById(R.id.record_detail_time_text);
		tvTotalDays = (TextView)rootView.findViewById(R.id.record_detail_time_result_text);
		
		btnAddMonth = (Button)rootView.findViewById(R.id.btn_record_add);
		btnReduceMonth = (Button)rootView.findViewById(R.id.btn_record_reduce);
		
		btnAddMonth.setOnClickListener(this);
		btnReduceMonth.setOnClickListener(this);
		
		mHistogramView = (HistogramView)rootView.findViewById(R.id.record_chart);
		initHistogramViewClickListener();
		
		screenWidth = DensityUtil.getScreenWidth(getActivity());
	}
	
	private void initHistogramViewClickListener() {
		mOnClickCountListener = new OnClickCountListener() {
			
			@Override
			public void onSingleClick(int position) {
				StatisticalData statisticalData = mStatisticalDataList.get(position);
				Log.i(TAG, "onSingleClick, select date = " + statisticalData.strDate);
				
				mBaseRecordDataList.clear();
				mBaseRecordDataList = RecordUtil.getBaseRecordDataList(statisticalData.strDate, mUserInfo.strAccountId);
				mRecordAdapter.updateData(mBaseRecordDataList);
			}
		};
		mHistogramView.setOnClickCountListener(mOnClickCountListener);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_record_add:
			nextYearData();
			break;
			
		case R.id.btn_record_reduce:
			lastYearData();
			break;

		default:
			break;
		}
	}

	private void lastYearData() {
		if(mSelectYear == 1970){
			return;
		}
		
		mSelectYear--;
		
		tvSelectYear.setText(String.valueOf(mSelectYear));
		tvYear.setText(mSelectYear + "年");
		
		int dayNum = MyTrainRecordDao.getInstance().getTrainDayNumDependYear(Globle.gApplicationContext, mUserInfo.strAccountId, mSelectYear);
		tvTotalDays.setText(String.valueOf(dayNum) + "天");
		
		if(mSelectYear == mThisYear){
			mSelectYearMonth = DateUtil.getDateStrOfYearMonth(DateUtil.getCurrentDate());
		}else{
			mSelectYearMonth = DateUtil.getStrDateFirstDayDependsYear(mSelectYear);
		}
		
		CourseServerMgr.getInstance().requestDetailData(mUserInfo.strAccountId, mSelectYearMonth + "-01", mSelectYearMonth + "-31");
		
		updateDataDependOnYearMonth(mSelectYearMonth);
	}

	private void nextYearData() {
		if(mSelectYear == mThisYear){
			return;
		}
		
		mSelectYear++;
		
		tvSelectYear.setText(String.valueOf(mSelectYear));
		tvYear.setText(mSelectYear + "年");
		
		int dayNum = MyTrainRecordDao.getInstance().getTrainDayNumDependYear(Globle.gApplicationContext, mUserInfo.strAccountId, mSelectYear);
		tvTotalDays.setText(String.valueOf(dayNum) + "天");
		
		if(mSelectYear == mThisYear){
			mSelectYearMonth = DateUtil.getDateStrOfYearMonth(DateUtil.getCurrentDate());
		}else{
			mSelectYearMonth = DateUtil.getStrDateFirstDayDependsYear(mSelectYear);
		}
		
		CourseServerMgr.getInstance().requestDetailData(mUserInfo.strAccountId, mSelectYearMonth + "-01", mSelectYearMonth + "-31");
		
		updateDataDependOnYearMonth(mSelectYearMonth);
	}

	private void updateDataDependOnYearMonth(String currentYearMonth) {
		mBaseRecordDataList.clear();
		mBaseRecordDataList = RecordUtil.getBaseRecordDataList(currentYearMonth, mUserInfo.strAccountId);
		mRecordAdapter.updateData(mBaseRecordDataList);
		
		initChart(mSelectYear);
	}
	
	// 不要删除，切换fragment用到
    @Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
}
