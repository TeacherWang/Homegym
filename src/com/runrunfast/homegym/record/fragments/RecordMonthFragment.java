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
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.DataTransferUtil;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.dao.MyTrainRecordDao;
import com.runrunfast.homegym.record.BaseRecordData;
import com.runrunfast.homegym.record.RecordAdapter;
import com.runrunfast.homegym.record.RecordUtil;
import com.runrunfast.homegym.record.StatisticalData;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.model.SelectedValue.SelectedValueType;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecordMonthFragment extends Fragment implements OnClickListener{
	private final String TAG = "RecordMonthFragment";
	
	private Resources mResources;
	
	private View rootView;
	
	private TextView tvTotalDays, tvSelectMonth, tvMonth;
	private Button btnAddMonth, btnReduceMonth;
	
	private PullToRefreshListView pullToRefreshListView;
	
	private ArrayList<BaseRecordData> mBaseRecordDataList;
	
	private RecordAdapter mRecordAdapter;
	
	private UserInfo mUserInfo;
	
	private int mThisYear;
	
	private int mSelectMonth; // 1-12月
	
	private int mThisMonth; // 本月
	
	// 柱状图
	private ColumnChartView chart;
	private ColumnChartData data;
	private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLabels = false;
    private boolean hasLabelForSelected = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater,
		 ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_record_month, container, false);
		
		mResources = getResources();
		
		initView();
		
		initData();
		
		return rootView;
	}
	
	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		mSelectMonth = DateUtil.getThisMonth();
		mThisMonth = mSelectMonth;
		mThisYear = Calendar.getInstance().get(Calendar.YEAR);
		
		tvSelectMonth.setText(mSelectMonth + "月");
		tvMonth.setText(DataTransferUtil.numMap.get(mSelectMonth) + "月份");
		
		String strCurrentDay = DateUtil.getCurrentDate();
		String strCurrentYearMonth = DateUtil.getDateStrOfYearMonth(strCurrentDay);
		
		int dayNum = MyTrainRecordDao.getInstance().getTrainDayNumDependMonth(Globle.gApplicationContext, mUserInfo.strAccountId, strCurrentYearMonth);
		tvTotalDays.setText(String.valueOf(dayNum) + "天");
		
		mBaseRecordDataList = new ArrayList<BaseRecordData>();
		
		mBaseRecordDataList = RecordUtil.getBaseRecordDataList(strCurrentYearMonth, mUserInfo.strAccountId);
		
		mRecordAdapter = new RecordAdapter(getActivity(), mBaseRecordDataList);
		pullToRefreshListView.setAdapter(mRecordAdapter);
		
		pullToRefreshListView.setMode(Mode.BOTH);
		
		initChart(strCurrentDay, strCurrentYearMonth);
	}

	private void initChart(String strCurrentDay, String strCurrentYearMonth) {
		ArrayList<StatisticalData> statisticalDataList = MyTrainRecordDao.getInstance().getDayStatisticalDataDependYearMonth(Globle.gApplicationContext, mUserInfo.strAccountId, strCurrentYearMonth);
		
		int daysOfMonth = DateUtil.getDaysByYearMonth(mThisYear, mSelectMonth);
		int dayIndexOfMonth = DateUtil.getDayIndexOfMonth(strCurrentDay);
		
		int chatHeight = (int) mResources.getDimension(R.dimen.chat_view_height);
		int columnWidth = (int) mResources.getDimension(R.dimen.chat_column_width);
		int spaceWidth = (int) mResources.getDimension(R.dimen.chat_column_space);
		int chatWidth = daysOfMonth * (columnWidth + spaceWidth);
		
		LayoutParams params = (LayoutParams) chart.getLayoutParams();
		params.height = chatHeight;
		params.width = chatWidth;
		chart.setLayoutParams(params);
		
		generateColumnData(daysOfMonth, dayIndexOfMonth - 1, statisticalDataList);
	}

//	private ArrayList<BaseRecordData> getRecordDataOfDay(String strDay, String uid) {
//		ArrayList<BaseRecordData> baseRecordDataForUiList = new ArrayList<BaseRecordData>();
//		ArrayList<String> courseIdList = MyFinishDao.getInstance().getFinishInfoDistinctCourseIdDependsDay(Globle.gApplicationContext, uid, strDay);
//		
//		int courseIdSize = courseIdList.size();
//		// 根据不同的课程id，组成界面需要的不同的list
//		for(int i=0; i<courseIdSize; i++){
//			String courseId = courseIdList.get(i);
//			ArrayList<BaseRecordData> baseRecordDataList = MyFinishDao.getInstance().getFinishInfoList(Globle.gApplicationContext, uid, courseId, strDay);
//			// 取出第一个课程的第一个，组成界面显示的时间课程信息头
//			RecordDataAction firstDataUnit = (RecordDataAction) baseRecordDataList.get(0);
//			if(i == 0){
//				RecordDataDate recordDataDate = new RecordDataDate();
//				recordDataDate.strDate = strDay;
//				recordDataDate.strCourseName = firstDataUnit.strCourseName;
//				
//				int dataSize = baseRecordDataList.size();
//				int totalConsumeTime = 0;
//				for(int j=0; j<dataSize; j++){
//					RecordDataAction recordDataUnit = (RecordDataAction) baseRecordDataList.get(j);
//					totalConsumeTime = totalConsumeTime + recordDataUnit.iConsumeTime;
//				}
//				recordDataDate.iConsumeTime = totalConsumeTime;
//				
//				baseRecordDataList.add(0, recordDataDate);
////				mBaseRecordDataList.addAll(baseRecordDataList);
//				baseRecordDataForUiList.addAll(baseRecordDataList);
//			}else{// 取出其他课程的第一个，组成界面显示的课程信息头
//				RecordDataCourse recordDataPlan = new RecordDataCourse();
//				recordDataPlan.strCoursId = firstDataUnit.strCoursId;
//				recordDataPlan.strCourseName = firstDataUnit.strCourseName;
//				
//				int dataSize = baseRecordDataList.size();
//				int totalConsumeTime = 0;
//				for(int j=0; j<dataSize; j++){
//					RecordDataAction recordDataUnit = (RecordDataAction) baseRecordDataList.get(j);
//					totalConsumeTime = totalConsumeTime + recordDataUnit.iConsumeTime;
//				}
//				recordDataPlan.iConsumeTime = totalConsumeTime;
//				baseRecordDataList.add(0, recordDataPlan);
////				mBaseRecordDataList.addAll(baseRecordDataList);
//				baseRecordDataForUiList.addAll(baseRecordDataList);
//			}
//		}
//		
//		return baseRecordDataForUiList;
//	}

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
		
		chart = (ColumnChartView) rootView.findViewById(R.id.chart);
		
		chart.setOnValueTouchListener(new ValueTouchListener());
		chart.setZoomEnabled(false);
		chart.setValueSelectionEnabled(true);
//        chart.setScrollEnabled(true);
	}
	
	private void generateColumnData(int daysNum, int highlightPosition, ArrayList<StatisticalData> statisticalDataList) {
		int statisticSize = statisticalDataList.size();
		int statisticPosition = 0;
		int dayIndex = 0;
		StatisticalData statisticalData = null;
		if(statisticSize > 0){
			statisticalData = statisticalDataList.get(statisticPosition);
			dayIndex = DateUtil.getDayIndexOfMonth(statisticalData.strDate);
		}
		
        int numColumns = daysNum;
        // Column can have many subcolumns, here by default I use 1 subcolumn in each of 8 columns.
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
        	if(statisticSize > 0 && statisticPosition < statisticSize && (dayIndex - 1) == i){
        		values.add(new SubcolumnValue(statisticalData.totalKcal, ChartUtils.pickNormalColor()));
        		statisticPosition++;
        		if(statisticPosition < statisticSize){
        			statisticalData = statisticalDataList.get(statisticPosition);
        			dayIndex = DateUtil.getDayIndexOfMonth(statisticalData.strDate);
        		}
        	}else{
        		values.add(new SubcolumnValue(0, ChartUtils.pickNormalColor()));
        	}

            Column column = new Column(values);
            column.setHasLabels(hasLabels);
            column.setHasLabelsOnlyForSelected(hasLabelForSelected);
            columns.add(column);
        }

        data = new ColumnChartData(columns);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(false);
            if (hasAxesNames) {
                axisX.setName("日期");
                axisY.setName("燃脂/千卡");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        chart.setColumnChartData(data);
        
        SelectedValue selectedValue = new SelectedValue(highlightPosition, 0, SelectedValueType.COLUMN);
		chart.selectValue(selectedValue);
    }

	private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            Toast.makeText(getActivity(), "Selected: " + value + ", columnIndex = " + columnIndex + ", subcolumnIndex = " + subcolumnIndex, Toast.LENGTH_SHORT).show();
            String selectDay = mThisYear + "-" + String.format("%02d", mSelectMonth) + "-" + String.format("%02d", columnIndex + 1);
            handleDaySelected(selectDay);
        }

        @Override
        public void onValueDeselected() {
        	
        }

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
		updateDataDependOnDay(strDateSelectDay);
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
		
		int dayNum = MyTrainRecordDao.getInstance().getTrainDayNumDependMonth(Globle.gApplicationContext, mUserInfo.strAccountId, strCurrentYearMonth);
		tvTotalDays.setText(String.valueOf(dayNum) + "天");
		
		updateDataDependOnDay(strCurrentDay);
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
		
		int dayNum = MyTrainRecordDao.getInstance().getTrainDayNumDependMonth(Globle.gApplicationContext, mUserInfo.strAccountId, strCurrentYearMonth);
		tvTotalDays.setText(String.valueOf(dayNum) + "天");
		
		updateDataDependOnDay(strCurrentDay);
	}
	
	private void updateDataDependOnDay(String strCurrentDay) {
		
//		mBaseRecordDataList = RecordUtil.getRecordDataOfDay(strCurrentDay, mUserInfo.strAccountId);
//		mRecordAdapter.updateData(mBaseRecordDataList);
	}
	
	// 不要删除，切换fragment用到
    @Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
}
