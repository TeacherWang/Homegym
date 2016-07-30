package com.runrunfast.homegym.record;

import java.util.ArrayList;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.utils.DateUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RecordAdapter extends BaseAdapter {
	private static final int LIST_SHOW_DATE 		= 0;
	private static final int LIST_SHOW_ONLY_COURSE 	= 1;
	private static final int LIST_SHOW_ONLY_TRAIN 	= 2;
	
	private static final int VIEW_TYPE_COUNT = 3;
	
	private LayoutInflater mInflater;
	private ArrayList<BaseRecordData> mBaseRecordDataList;
	
	public RecordAdapter(Context context, ArrayList<BaseRecordData> baseRecordDataList){
		this.mInflater = LayoutInflater.from(context);
		this.mBaseRecordDataList = baseRecordDataList;
	}

	@Override
	public int getCount() {
		return mBaseRecordDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mBaseRecordDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}
	
	@Override
	public int getItemViewType(int position) {
		BaseRecordData baseRecordData = mBaseRecordDataList.get(position);
		if(baseRecordData.iDataType == BaseRecordData.DATA_TYPE_HAVE_DATE){
			return LIST_SHOW_DATE;
		}else if(baseRecordData.iDataType == BaseRecordData.DATA_TYPE_ONLY_HAVE_COURSE){
			return LIST_SHOW_ONLY_COURSE;
		}else {
			return LIST_SHOW_ONLY_TRAIN;
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RecordDateViewHolder recordDateViewHolder = null;
		RecordCourseViewHolder recordCourseViewHolder = null;
		RecordTrainViewHolder recordTrainViewHolder = null;
		int holderType = getItemViewType(position);
		if(convertView == null){
			switch (holderType) {
			case LIST_SHOW_DATE:
				convertView = mInflater.inflate(R.layout.record_date, null);
				recordDateViewHolder = new RecordDateViewHolder();
				
				recordDateViewHolder.tvDate = (TextView)convertView.findViewById(R.id.record_date);
				recordDateViewHolder.tvCourseName = (TextView)convertView.findViewById(R.id.record_course_name);
				recordDateViewHolder.tvCourseConsumeTime = (TextView)convertView.findViewById(R.id.record_course_consume_text);
				
				convertView.setTag(recordDateViewHolder);
				break;
				
			case LIST_SHOW_ONLY_COURSE:
				convertView = mInflater.inflate(R.layout.record_course, null);
				recordCourseViewHolder = new RecordCourseViewHolder();
				
				recordCourseViewHolder.tvCourseName = (TextView)convertView.findViewById(R.id.record_course_name);
				recordCourseViewHolder.tvCourseConsumeTime = (TextView)convertView.findViewById(R.id.record_course_consume_text);
				
				convertView.setTag(recordCourseViewHolder);
				break;
				
			case LIST_SHOW_ONLY_TRAIN:
				convertView = mInflater.inflate(R.layout.record_train, null);
				recordTrainViewHolder = new RecordTrainViewHolder();
				
				recordTrainViewHolder.ivTrain = (ImageView)convertView.findViewById(R.id.record_detail_train_img);
				recordTrainViewHolder.tvTrainName = (TextView)convertView.findViewById(R.id.record_detail_train_name);
				recordTrainViewHolder.tvTrainCount = (TextView)convertView.findViewById(R.id.record_detail_train_group_count_text);
				recordTrainViewHolder.tvTrainKcal = (TextView)convertView.findViewById(R.id.record_detail_train_kcal);
				
				convertView.setTag(recordTrainViewHolder);
				break;

			default:
				break;
			}
		}else{
			switch (holderType) {
			case LIST_SHOW_DATE:
				recordDateViewHolder = (RecordDateViewHolder)convertView.getTag();
				break;
				
			case LIST_SHOW_ONLY_COURSE:
				recordCourseViewHolder = (RecordCourseViewHolder)convertView.getTag();
				break;
				
			case LIST_SHOW_ONLY_TRAIN:
				recordTrainViewHolder = (RecordTrainViewHolder)convertView.getTag();
				break;

			default:
				break;
			}
		}
		
		BaseRecordData baseRecordData = mBaseRecordDataList.get(position);
		switch (holderType) {
		case LIST_SHOW_DATE:
			RecordDataDate recordDataDate = (RecordDataDate)baseRecordData;
			recordDateViewHolder.tvDate.setText(recordDataDate.strDate);
			recordDateViewHolder.tvCourseName.setText(recordDataDate.strCourseName);
			recordDateViewHolder.tvCourseConsumeTime.setText(DateUtil.secToTime(recordDataDate.iConsumeTime));
			break;
			
		case LIST_SHOW_ONLY_COURSE:
			RecordDataPlan recordDataPlan = (RecordDataPlan)baseRecordData;
			recordCourseViewHolder.tvCourseName.setText(recordDataPlan.strCourseName);
			recordCourseViewHolder.tvCourseConsumeTime.setText(DateUtil.secToTime(recordDataPlan.iConsumeTime));
			break;
			
		case LIST_SHOW_ONLY_TRAIN:
			RecordDataUnit recordDataUnit = (RecordDataUnit)baseRecordData;
			recordTrainViewHolder.ivTrain.setBackgroundResource(R.drawable.record_action1);
			recordTrainViewHolder.tvTrainName.setText(recordDataUnit.actionName);
			recordTrainViewHolder.tvTrainCount.setText(String.valueOf(recordDataUnit.iGroupCount) + "组");
			recordTrainViewHolder.tvTrainKcal.setText(String.valueOf(recordDataUnit.iTotalKcal) + "千卡");
			break;

		default:
			break;
		}
		
		return convertView;
	}
	
	class RecordDateViewHolder{
		TextView tvDate;
		TextView tvCourseName;
		TextView tvCourseConsumeTime;
	}
	
	class RecordCourseViewHolder{
		TextView tvCourseName;
		TextView tvCourseConsumeTime;
	}
	
	class RecordTrainViewHolder{
		ImageView ivTrain;
		TextView tvTrainName;
		TextView tvTrainCount;
		TextView tvTrainKcal;
	}

}
