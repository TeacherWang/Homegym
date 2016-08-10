package com.runrunfast.homegym.home.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.bean.Course;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.course.CourseAdapter;
import com.runrunfast.homegym.course.CourseAdapter.ICourseAdapterListener;
import com.runrunfast.homegym.course.CourseInfo;
import com.runrunfast.homegym.course.CourseTrainActivity;
import com.runrunfast.homegym.course.DetailPlanActivity;
import com.runrunfast.homegym.dao.CourseDao;
import com.runrunfast.homegym.dao.MyCourseDao;
import com.runrunfast.homegym.home.HomeActivity;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;

public class MyTrainingFragment extends Fragment{
	private final String TAG = "MyTrainingFragment";
	
	private View rootView;
	private ListView mMyCourseListView;
	
	private CourseAdapter mMyCourseAdapter;
	
	private ArrayList<Course> mMyCourseList;
	private ArrayList<Course> mRecommedList;
	
	private ICourseAdapterListener mICourseAdapterListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_my_training, container, false);
		
		initView();
		
		initData();
		
		initListener();
		
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		Log.i(TAG, "onResume");
		
		mMyCourseList = MyCourseDao.getInstance().getMyCourseListFromDb(Globle.gApplicationContext);
		if(mMyCourseList.size() > 0){
			mMyCourseList.add(new InvalidCourse(InvalidCourse.COURSE_TYPE_SHOW_RECOMMED_TEXT));
			
			mMyCourseList.addAll(mRecommedList);
			mMyCourseAdapter.updateData(mMyCourseList);
		}else{
			mMyCourseList.add(new InvalidCourse(InvalidCourse.COURSE_TYPE_EMPTY));
			
			mMyCourseList.add(new InvalidCourse(InvalidCourse.COURSE_TYPE_SHOW_RECOMMED_TEXT));
			
			mMyCourseList.addAll(mRecommedList);
			mMyCourseAdapter.updateData(mMyCourseList);
		}
	}

	private void initListener() {
		mMyCourseListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Course course = mMyCourseList.get(position);
				if(course instanceof InvalidCourse){
					return;
				}
				
				Intent intent = null;
				
				if(course instanceof MyCourse){
					intent = new Intent(getActivity(), CourseTrainActivity.class);
				}else{
					intent = new Intent(getActivity(), DetailPlanActivity.class);
				}
				intent.putExtra(Const.KEY_COURSE, course);
				startActivity(intent);
			}
		});
		
		mICourseAdapterListener = new ICourseAdapterListener() {
			
			@Override
			public void onAddCourseClicked() {
				((HomeActivity)getActivity()).handleClickAllCourse();
			}
		};
		
		mMyCourseAdapter.setOnCourseAdapterListener(mICourseAdapterListener);
	}

	private void initData() {
		mMyCourseList = MyCourseDao.getInstance().getMyCourseListFromDb(Globle.gApplicationContext);
		
		if(mMyCourseList.size() == 0){
			mMyCourseList.add(new InvalidCourse(InvalidCourse.COURSE_TYPE_EMPTY));
			mMyCourseAdapter = new CourseAdapter(getActivity(), mMyCourseList);
			mMyCourseListView.setAdapter(mMyCourseAdapter);
		}else{
			mMyCourseAdapter = new CourseAdapter(getActivity(), mMyCourseList);
			mMyCourseListView.setAdapter(mMyCourseAdapter);
		}
		
		mRecommedList = CourseDao.getInstance().getRecommedCourseListFromDb(Globle.gApplicationContext);
	}

	private void initView() {
		mMyCourseListView = (ListView)rootView.findViewById(R.id.my_course_listview);
//		mRecommedCourseListView = (ListView)rootView.findViewById(R.id.course_recommed_listview);
	}
	
	// 不要删除，切换fragment用到
    @Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
    
}
