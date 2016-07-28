package com.runrunfast.homegym.home.fragments;

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
import com.runrunfast.homegym.course.CourseInfo;
import com.runrunfast.homegym.course.CourseTrainActivity;
import com.runrunfast.homegym.course.DetailPlanActivity;
import com.runrunfast.homegym.dao.CourseDao;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.Globle;

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
		
		initListener();
		
		return rootView;
	}

	private void initListener() {
		mAllCoursesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CourseInfo courseInfo = mAllCoursesList.get(position);
				Intent intent = new Intent(getActivity(), DetailPlanActivity.class);
				intent.putExtra(Const.KEY_COURSE_INFO, courseInfo);
				startActivity(intent);
			}
		});
	}

	private void initData() {
		mAllCoursesList = new ArrayList<CourseInfo>();
		
		mAllCoursesList = CourseDao.getInstance().getCourseInfoListFromDb(Globle.gApplicationContext);
		
		mAllCoursesAdapter = new CourseAdapter(getActivity(), mAllCoursesList, false);
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
