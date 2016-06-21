package com.runrunfast.homegym.course;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.runrunfast.homegym.R;

public class CourseAdapter extends BaseAdapter {
	private final String TAG = "CourseAdapter";
	
	private LayoutInflater mInflater;
	private Context mContext;
	private ArrayList<CourseInfo> mCourseList;
	
	public CourseAdapter(Context context, ArrayList<CourseInfo> courseList){
		this.mContext = context;
		this.mCourseList = courseList;
		this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mCourseList.size();
	}

	@Override
	public Object getItem(int position) {
		return mCourseList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.fragment_my_training_item, null);
			viewHolder.courseImg = (ImageView)convertView.findViewById(R.id.course_img);
			viewHolder.btnAdd = (Button)convertView.findViewById(R.id.btn_add);
			viewHolder.courseNewImg = (ImageView)convertView.findViewById(R.id.course_new_img);
			viewHolder.courseProgressImg = (ImageView)convertView.findViewById(R.id.course_progress_img);
			viewHolder.tvProgress = (TextView)convertView.findViewById(R.id.course_progress_text);
			viewHolder.tvCourseQuality = (TextView)convertView.findViewById(R.id.excellent_course_text);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		CourseInfo courseInfo = mCourseList.get(position);
		if(courseInfo.isMyCourse){
			handleMyCourse(viewHolder, courseInfo);
		}else{
			handleCourse(viewHolder, courseInfo);
		}
		
		return convertView;
	}

	private void handleCourse(ViewHolder viewHolder, CourseInfo courseInfo) {
		if(courseInfo.isNew){
			viewHolder.courseNewImg.setVisibility(View.VISIBLE);
		}
		viewHolder.courseProgressImg.setVisibility(View.INVISIBLE);
		viewHolder.tvProgress.setVisibility(View.INVISIBLE);
	}

	/**
	 * @param viewHolder
	 * @param courseInfo
	 */
	private void handleMyCourse(ViewHolder viewHolder, CourseInfo courseInfo) {
		if(courseInfo.isMyCourseEmpty){
			Log.d(TAG, "handleMyCourse empty");
			viewHolder.btnAdd.setVisibility(View.VISIBLE);
			viewHolder.courseNewImg.setVisibility(View.INVISIBLE);
			viewHolder.courseProgressImg.setVisibility(View.INVISIBLE);
			viewHolder.tvProgress.setVisibility(View.INVISIBLE);
			viewHolder.tvCourseQuality.setVisibility(View.INVISIBLE);
			return;
		}
		Log.d(TAG, "handleMyCourse");
		
		viewHolder.btnAdd.setVisibility(View.INVISIBLE);
		viewHolder.courseNewImg.setVisibility(View.INVISIBLE);
		setCourseProgress(viewHolder, courseInfo.courseProgress);
		if(courseInfo.courseQuality == CourseInfo.QUALITY_EXCELLENT){
			viewHolder.tvCourseQuality.setVisibility(View.VISIBLE);
		}else{
			viewHolder.tvCourseQuality.setVisibility(View.INVISIBLE);
		}
	}

	private void setCourseProgress(ViewHolder viewHolder, int progerssType) {
		viewHolder.courseProgressImg.setVisibility(View.VISIBLE);
		switch (progerssType) {
		case CourseInfo.PROGRESS_ING:
			viewHolder.courseProgressImg.setBackgroundResource(R.drawable.home_state_going);
			viewHolder.tvProgress.setText(R.string.course_progress_ing);
			break;
			
		case CourseInfo.PROGRESS_REST:
			viewHolder.courseProgressImg.setBackgroundResource(R.drawable.home_state_going);
			viewHolder.tvProgress.setText(R.string.course_rest_day);
			break;

		default:
			break;
		}
	}

	private class ViewHolder {
		public ImageView courseImg;
		public Button btnAdd;
		public ImageView courseNewImg;
		public ImageView courseProgressImg;
		public TextView tvProgress;
		public TextView tvCourseQuality;
	}
	
}
