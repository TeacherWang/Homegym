package com.runrunfast.homegym.course;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CourseTrainAdapter extends BaseAdapter {
	
	private ArrayList<CourseTrainInfo> mCourseTrainInfoList;
	private LayoutInflater mInflater;
	
	public CourseTrainAdapter(Context context, ArrayList<CourseTrainInfo> courseTrainInfos){
		this.mCourseTrainInfoList = courseTrainInfos;
		this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mCourseTrainInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return mCourseTrainInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return convertView;
	}

}
