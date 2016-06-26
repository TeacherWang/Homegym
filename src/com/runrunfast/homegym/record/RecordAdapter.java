package com.runrunfast.homegym.record;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
		if(baseRecordData.dataType == BaseRecordData.DATA_TYPE_HAVE_DATE){
			return LIST_SHOW_DATE;
		}else if(baseRecordData.dataType == BaseRecordData.DATA_TYPE_ONLY_HAVE_COURSE){
			return LIST_SHOW_ONLY_COURSE;
		}else {
			return LIST_SHOW_ONLY_TRAIN;
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		return convertView;
	}

}
