package com.runrunfast.homegym.home.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.course.CourseAdapter;
import com.runrunfast.homegym.course.CourseInfo;

import java.util.ArrayList;

public class AllCoursesFragment extends Fragment {
	
	private View rootView;
	
	private ListView mAllCoursesListView;
	
	private ArrayList<CourseInfo> mAllCoursesList;
	
	private CourseAdapter mAllCoursesAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_all_courses, container, false);
		
		initView();
		
		initData();
		
		return rootView;
	}

	private void initData() {
		mAllCoursesList = new ArrayList<CourseInfo>();
		
		CourseInfo courseInfo1 = new CourseInfo();
		courseInfo1.isMyCourse = false;
		courseInfo1.isNew = true;
		courseInfo1.courseName = "塑性训练";
		mAllCoursesList.add(courseInfo1);
		
		CourseInfo courseInfo2 = new CourseInfo();
		courseInfo2.isMyCourse = false;
		courseInfo2.isNew = false;
		courseInfo2.courseName = "21天腹肌雕刻";
		mAllCoursesList.add(courseInfo2);
		
		CourseInfo courseInfo3 = new CourseInfo();
		courseInfo3.isMyCourse = false;
		courseInfo3.isNew = false;
		courseInfo3.courseName = "S型身材速成";
		mAllCoursesList.add(courseInfo3);
		
		CourseInfo courseInfo4 = new CourseInfo();
		courseInfo4.isMyCourse = false;
		courseInfo4.isNew = false;
		courseInfo4.courseName = "人鱼线训练";
		mAllCoursesList.add(courseInfo4);
		
		mAllCoursesAdapter = new CourseAdapter(getActivity(), mAllCoursesList);
		mAllCoursesListView.setAdapter(mAllCoursesAdapter);
	}

	private void initView() {
		mAllCoursesListView = (ListView)rootView.findViewById(R.id.all_courses_listview);
	}
	
	// 不要删除，切换fragment用到
    @Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
	
}
