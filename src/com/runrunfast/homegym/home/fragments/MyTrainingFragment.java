package com.runrunfast.homegym.home.fragments;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.course.CourseAdapter;
import com.runrunfast.homegym.course.CourseAdapter.ICourseAdapterListener;
import com.runrunfast.homegym.course.CourseInfo;
import com.runrunfast.homegym.course.CourseTrainActivity;
import com.runrunfast.homegym.home.HomeActivity;
import com.runrunfast.homegym.utils.Const;

public class MyTrainingFragment extends Fragment{
	private View rootView;
	private ListView mMyCourseListView;
	private ListView mRecommedCourseListView;
	
	private CourseAdapter mMyCourseAdapter;
	private CourseAdapter mRecommedAdapter;
	
	private ArrayList<CourseInfo> mMyCourseList;
	private ArrayList<CourseInfo> mRecommedList;
	
	private ICourseAdapterListener mICourseAdapterListener;
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_my_training, container, false);
		
		initView();
		
		initData();
		
		initListener();
		
		return rootView;
	}

	private void initListener() {
		mRecommedCourseListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CourseInfo courseInfo = mRecommedList.get(position);
				int courseId = courseInfo.courseId;
				String courseName = courseInfo.courseName;
				Intent intent = new Intent(getActivity(), CourseTrainActivity.class);
				intent.putExtra(Const.KEY_COURSE_ID, courseId);
				intent.putExtra(Const.KEY_COURSE_NAME, courseName);
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
		mMyCourseList = new ArrayList<CourseInfo>();
		mRecommedList = new ArrayList<CourseInfo>();
		
		CourseInfo emptyCourseInfo = new CourseInfo();
		emptyCourseInfo.isMyCourse = true;
		emptyCourseInfo.isMyCourseEmpty = true;
		mMyCourseList.add(emptyCourseInfo);
		mMyCourseAdapter = new CourseAdapter(getActivity(), mMyCourseList);
		mMyCourseListView.setAdapter(mMyCourseAdapter);
		
		CourseInfo courseInfo1 = new CourseInfo();
		courseInfo1.isMyCourse = false;
		courseInfo1.isNew = true;
		courseInfo1.courseName = "21天增肌训练";
		mRecommedList.add(courseInfo1);
		
		CourseInfo courseInfo2 = new CourseInfo();
		courseInfo2.isMyCourse = false;
		courseInfo2.isNew = false;
		courseInfo2.courseName = "腹肌雕刻训练";
		mRecommedList.add(courseInfo2);
		
		mRecommedAdapter = new CourseAdapter(getActivity(), mRecommedList);
		mRecommedCourseListView.setAdapter(mRecommedAdapter);
	}

	private void initView() {
		mMyCourseListView = (ListView)rootView.findViewById(R.id.my_course_listview);
		mRecommedCourseListView = (ListView)rootView.findViewById(R.id.course_recommed_listview);
	}
	
	// 不要删除，切换fragment用到
    @Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
    
}
