package com.runrunfast.homegym.course;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.runrunfast.homegym.R;
import com.runrunfast.homegym.bean.Course;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.home.fragments.InvalidCourse;
import com.runrunfast.homegym.utils.BitmapUtils;
import com.runrunfast.homegym.utils.ConstServer;
import com.runrunfast.homegym.utils.FileUtils;
import com.runrunfast.homegym.utils.ImageLoadingCourseListener;
import com.runrunfast.homegym.utils.ImageWorker;

import java.io.File;
import java.util.ArrayList;

public class CourseAdapter extends BaseAdapter {
	private static final int LIST_SHOW_COURSE 				= 0; // 显示课程信息
	private static final int LIST_SHOW_RECOMMEND_DESCRIPT 	= 1; // 显示“推荐课程”描述
	
	private static final int VIEW_TYPE_COUNT = 2;
	
	private Resources mResources;
	
	private LayoutInflater mInflater;
	private ArrayList<Course> mCourseList;
	private ICourseAdapterListener mICourseAdapterListener;
	private ImageWorker mImageWorker;
	
	public interface ICourseAdapterListener{
		void onAddCourseClicked();
	}
	
	public void setOnCourseAdapterListener(ICourseAdapterListener iCourseAdapterListener){
		this.mICourseAdapterListener = iCourseAdapterListener;
	}
	
	public CourseAdapter(Context context, ArrayList<Course> courseList){
		mResources = context.getResources();
		setData(courseList);
		this.mInflater = LayoutInflater.from(context);
		
		mImageWorker = new ImageWorker(context);
		mImageWorker.enableImageCache();
        mImageWorker.setImageFadeIn(false);
	}
	
	private void setData(ArrayList<Course> courseList){
		if(courseList == null || courseList.size() <= 0){
			this.mCourseList = new ArrayList<Course>();
		}else{
			this.mCourseList = courseList;
		}
	}
	
	public void updateData(ArrayList<Course> courseList){
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
		Course course = mCourseList.get(position);
		if(course instanceof InvalidCourse){
			InvalidCourse invalidCourse = (InvalidCourse) course;
			if(invalidCourse.courseType == InvalidCourse.COURSE_TYPE_SHOW_RECOMMED_TEXT){
				return LIST_SHOW_RECOMMEND_DESCRIPT;
			}else{
				return LIST_SHOW_COURSE;
			}
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
			return convertView;
		}
		
		Course course = mCourseList.get(position);
		if(course instanceof MyCourse){
			MyCourse myCourse = (MyCourse) course;
			handleMyCourse(viewHolder, myCourse);
		}else{
			handleCourse(viewHolder, course);
		}
		
		return convertView;
	}

	private void handleCourse(ViewHolder viewHolder, Course course) {
		if(course.course_new == Course.NEW_COURSE){
			viewHolder.courseNewImg.setVisibility(View.VISIBLE);
		}else{
			viewHolder.courseNewImg.setVisibility(View.INVISIBLE);
		}
		viewHolder.tvEmptyDescript.setVisibility(View.INVISIBLE);
		viewHolder.tvCourseName.setVisibility(View.VISIBLE);
		viewHolder.courseProgressImg.setVisibility(View.INVISIBLE);
		viewHolder.tvProgress.setVisibility(View.INVISIBLE);
		viewHolder.tvCourseName.setText(course.course_name);
		if(course.course_quality == Course.QUALITY_EXCELLENT){
			viewHolder.tvCourseQuality.setVisibility(View.VISIBLE);
		}else{
			viewHolder.tvCourseQuality.setVisibility(View.INVISIBLE);
		}
		
		if( !TextUtils.isEmpty(course.course_img_local) && FileUtils.isFileExist(course.course_img_local) ){
			mImageWorker.loadImage(course.course_img_local, viewHolder.courseImg);
			return;
		}
		
		String filePath = ConstServer.SDCARD_HOMEGYM_ROOT + course.course_id + File.separator + FileUtils.getFileName(course.course_img_url);
		
		ImageLoader.getInstance().displayImage(course.course_img_url,
				viewHolder.courseImg, BitmapUtils.initCourseImageLoader(),
				new ImageLoadingCourseListener(filePath, course.course_id));
	}
	
	/**
	 * @param viewHolder
	 * @param courseInfo
	 */
	private void handleMyCourse(ViewHolder viewHolder, MyCourse myCourse) {
		if(myCourse instanceof InvalidCourse){
			InvalidCourse invalidCourse = (InvalidCourse) myCourse;
			if(invalidCourse.courseType == InvalidCourse.COURSE_TYPE_EMPTY){
				viewHolder.btnAdd.setVisibility(View.VISIBLE);
				
				viewHolder.btnAdd.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if(mICourseAdapterListener != null){
							mICourseAdapterListener.onAddCourseClicked();
						}
					}
				});
				
				viewHolder.courseImg.setBackgroundResource(R.drawable.home_add);
				viewHolder.courseNewImg.setVisibility(View.INVISIBLE);
				viewHolder.courseProgressImg.setVisibility(View.INVISIBLE);
				viewHolder.tvEmptyDescript.setVisibility(View.VISIBLE);
				viewHolder.tvCourseName.setVisibility(View.INVISIBLE);
				viewHolder.tvProgress.setVisibility(View.INVISIBLE);
				viewHolder.tvCourseQuality.setVisibility(View.INVISIBLE);
				return;
			}
		}
		
		viewHolder.btnAdd.setVisibility(View.INVISIBLE);
		viewHolder.courseNewImg.setVisibility(View.INVISIBLE);
		viewHolder.tvEmptyDescript.setVisibility(View.INVISIBLE);
		viewHolder.tvCourseName.setVisibility(View.VISIBLE);
		viewHolder.tvCourseName.setText(myCourse.course_name);
		viewHolder.tvProgress.setVisibility(View.VISIBLE);
		setCourseProgress(viewHolder, myCourse.progress);
		if(myCourse.course_quality == Course.QUALITY_EXCELLENT){
			viewHolder.tvCourseQuality.setVisibility(View.VISIBLE);
		}else{
			viewHolder.tvCourseQuality.setVisibility(View.INVISIBLE);
		}
		
		if( !TextUtils.isEmpty(myCourse.course_img_local) && FileUtils.isFileExist(myCourse.course_img_local) ){
			mImageWorker.loadImage(myCourse.course_img_local, viewHolder.courseImg);
			return;
		}
		
		String filePath = ConstServer.SDCARD_HOMEGYM_ROOT + myCourse.course_id + File.separator + FileUtils.getFileName(myCourse.course_img_url);
		
		ImageLoader.getInstance().displayImage(myCourse.course_img_url,
				viewHolder.courseImg, BitmapUtils.initCourseImageLoader(),
				new ImageLoadingCourseListener(filePath, myCourse.course_id));
	}

	private void setCourseProgress(ViewHolder viewHolder, int progerssType) {
		viewHolder.courseProgressImg.setVisibility(View.VISIBLE);
		switch (progerssType) {
		case MyCourse.COURSE_PROGRESS_ING:
			viewHolder.courseProgressImg.setBackgroundResource(R.drawable.home_state_going);
			viewHolder.tvProgress.setText(R.string.course_progress_ing);
			viewHolder.tvProgress.setTextColor(mResources.getColor(R.color.white));
			break;
			
		case MyCourse.COURSE_PROGRESS_REST:
			viewHolder.courseProgressImg.setBackgroundResource(R.drawable.home_state_going);
			viewHolder.tvProgress.setText(R.string.course_rest_day);
			viewHolder.tvProgress.setTextColor(mResources.getColor(R.color.white));
			break;
			
		case MyCourse.COURSE_PROGRESS_EXPIRED:
			viewHolder.courseProgressImg.setBackgroundResource(R.drawable.home_state_going);
			viewHolder.tvProgress.setText(R.string.course_expired);
			viewHolder.tvProgress.setTextColor(mResources.getColor(R.color.course_expire_text));
			break;
			
		case MyCourse.COURSE_PROGRESS_FINISH:
			viewHolder.courseProgressImg.setBackgroundResource(R.drawable.home_state_finish);
			viewHolder.tvProgress.setText(R.string.course_finished);
			viewHolder.tvProgress.setTextColor(mResources.getColor(R.color.white));
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
