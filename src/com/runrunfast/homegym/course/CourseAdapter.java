package com.runrunfast.homegym.course;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.runrunfast.homegym.R;

import java.util.ArrayList;

public class CourseAdapter extends BaseAdapter {
	private final String TAG = "CourseAdapter";
	
	private static final int LIST_SHOW_COURSE 				= 0; // 显示课程信息
	private static final int LIST_SHOW_RECOMMEND_DESCRIPT 	= 1; // 显示“推荐课程”描述
	
	private static final int VIEW_TYPE_COUNT = 2;
	
	private LayoutInflater mInflater;
	private Context mContext;
	private ArrayList<CourseInfo> mCourseList;
	private ICourseAdapterListener mICourseAdapterListener;
	private boolean mIsMyCourseEmpty;
	
	public interface ICourseAdapterListener{
		void onAddCourseClicked();
	}
	
	public void setOnCourseAdapterListener(ICourseAdapterListener iCourseAdapterListener){
		this.mICourseAdapterListener = iCourseAdapterListener;
	}
	
	public CourseAdapter(Context context, ArrayList<CourseInfo> courseList, boolean isMyCourseEmpty){
		this.mContext = context;
		setData(courseList);
		this.mInflater = LayoutInflater.from(context);
		this.mIsMyCourseEmpty = isMyCourseEmpty;
	}
	
	private void setData(ArrayList<CourseInfo> courseList){
		if(courseList == null || courseList.size() <= 0){
			this.mCourseList = new ArrayList<CourseInfo>();
		}else{
			this.mCourseList = courseList;
		}
	}
	
	public void updateData(ArrayList<CourseInfo> courseList, boolean isMyCourseEmpty){
		this.mIsMyCourseEmpty = isMyCourseEmpty;
		setData(courseList);
		notifyDataSetChanged();
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
	public int getViewTypeCount() {
		return VIEW_TYPE_COUNT;
	}
	
	@Override
	public int getItemViewType(int position) {
		CourseInfo courseInfo = mCourseList.get(position);
		if(courseInfo.isRecommendDescript){
			return LIST_SHOW_RECOMMEND_DESCRIPT;
		}else{
			return LIST_SHOW_COURSE;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		RecommendDescriptViewHolder recommendDescriptViewHolder = null;
		int holdType = getItemViewType(position);
		if (convertView == null) {
			switch (holdType) {
			case LIST_SHOW_COURSE:
				viewHolder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.fragment_my_training_item, null);
				viewHolder.courseImg = (ImageView)convertView.findViewById(R.id.course_img);
				viewHolder.btnAdd = (Button)convertView.findViewById(R.id.btn_add);
				viewHolder.courseNewImg = (ImageView)convertView.findViewById(R.id.course_new_img);
				viewHolder.courseProgressImg = (ImageView)convertView.findViewById(R.id.course_progress_img);
				viewHolder.tvCourseName = (TextView)convertView.findViewById(R.id.course_name_text);
				viewHolder.tvEmptyDescript = (TextView)convertView.findViewById(R.id.course_empty_text);
				viewHolder.tvProgress = (TextView)convertView.findViewById(R.id.course_progress_text);
				viewHolder.tvCourseQuality = (TextView)convertView.findViewById(R.id.excellent_course_text);
				
				convertView.setTag(viewHolder);
				break;
				
			case LIST_SHOW_RECOMMEND_DESCRIPT:
				recommendDescriptViewHolder = new RecommendDescriptViewHolder();
				convertView = mInflater.inflate(R.layout.fragment_my_course_recommend_descript, null);
				convertView.setTag(recommendDescriptViewHolder);
				break;

			default:
				break;
			}
			
		} else {
			switch (holdType) {
			case LIST_SHOW_COURSE:
				viewHolder = (ViewHolder) convertView.getTag();
				break;
				
			case LIST_SHOW_RECOMMEND_DESCRIPT:
				recommendDescriptViewHolder = (RecommendDescriptViewHolder)convertView.getTag();
				break;

			default:
				break;
			}
		}
		
		if(holdType == LIST_SHOW_RECOMMEND_DESCRIPT){
			Log.d(TAG, "getView, this is recommed descript");
			return convertView;
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
		viewHolder.tvEmptyDescript.setVisibility(View.INVISIBLE);
		viewHolder.tvCourseName.setVisibility(View.VISIBLE);
		viewHolder.courseProgressImg.setVisibility(View.INVISIBLE);
		viewHolder.tvProgress.setVisibility(View.INVISIBLE);
		viewHolder.tvCourseName.setText(courseInfo.courseName);
		if(courseInfo.courseQuality == CourseInfo.QUALITY_EXCELLENT){
			viewHolder.tvCourseQuality.setVisibility(View.VISIBLE);
		}else{
			viewHolder.tvCourseQuality.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * @param viewHolder
	 * @param courseInfo
	 */
	private void handleMyCourse(ViewHolder viewHolder, CourseInfo courseInfo) {
		if(mIsMyCourseEmpty){
			Log.d(TAG, "handleMyCourse empty");
			viewHolder.btnAdd.setVisibility(View.VISIBLE);
			
			viewHolder.btnAdd.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(mICourseAdapterListener != null){
						mICourseAdapterListener.onAddCourseClicked();
					}
				}
			});
			
			viewHolder.courseNewImg.setVisibility(View.INVISIBLE);
			viewHolder.courseProgressImg.setVisibility(View.INVISIBLE);
			viewHolder.tvEmptyDescript.setVisibility(View.VISIBLE);
			viewHolder.tvCourseName.setVisibility(View.INVISIBLE);
			viewHolder.tvProgress.setVisibility(View.INVISIBLE);
			viewHolder.tvCourseQuality.setVisibility(View.INVISIBLE);
			return;
		}
		Log.d(TAG, "handleMyCourse");
		
		viewHolder.btnAdd.setVisibility(View.INVISIBLE);
		viewHolder.courseNewImg.setVisibility(View.INVISIBLE);
		viewHolder.tvEmptyDescript.setVisibility(View.INVISIBLE);
		viewHolder.tvCourseName.setVisibility(View.VISIBLE);
		viewHolder.tvCourseName.setText(courseInfo.courseName);
		viewHolder.tvProgress.setVisibility(View.VISIBLE);
		setCourseProgress(viewHolder, courseInfo.courseProgress);
		if(courseInfo.courseQuality == CourseInfo.QUALITY_EXCELLENT){
			viewHolder.tvCourseQuality.setVisibility(View.VISIBLE);
		}else{
			viewHolder.tvCourseQuality.setVisibility(View.INVISIBLE);
		}
	}

	private void setCourseProgress(ViewHolder viewHolder, int progerssType) {
		Log.i(TAG, "setCourseProgress, progerssType = " + progerssType);
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
		public TextView tvEmptyDescript;
		public TextView tvCourseName;
		public TextView tvProgress;
		public TextView tvCourseQuality;
	}
	
	private class RecommendDescriptViewHolder{
		
	}
	
}
