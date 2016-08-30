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
import com.runrunfast.homegym.course.CourseAdapter;
import com.runrunfast.homegym.course.CourseServerMgr;
import com.runrunfast.homegym.course.CourseServerMgr.IGetCourseFromServerListener;
import com.runrunfast.homegym.course.DetailPlanActivity;
import com.runrunfast.homegym.dao.CourseDao;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;

public class AllCoursesFragment extends Fragment {
	private final String TAG = "AllCoursesFragment";
	
	private View rootView;
	
	private ListView mAllCoursesListView;
	
	private ArrayList<Course> mAllCoursesList;
	
	private CourseAdapter mAllCoursesAdapter;
	
	private IGetCourseFromServerListener mIGetCourseFromServerListener;

	@Override
	public View onCreateView(LayoutInflater inflater,
		 ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_all_courses, container, false);
		
		initView();
		
		initData();
		
		initListener();
		
		initCourseServerListener();
		
		return rootView;
	}
	
	private void initCourseServerListener() {
		mIGetCourseFromServerListener = new IGetCourseFromServerListener() {
			
			@Override
			public void onGetCourseSucFromServer() {
				Log.i(TAG, "onGetCourseSucFromServer");
				
				getData();
			}
			
			@Override
			public void onGetCoruseFailFromServer() {
				
			}
		};
		CourseServerMgr.getInstance().addGetCourseFromServerObserver(mIGetCourseFromServerListener);
	}

	private void getData() {
		mAllCoursesList = CourseDao.getInstance().getCourseListFromDb(Globle.gApplicationContext);
		mAllCoursesAdapter.updateData(mAllCoursesList);
	}

	private void initListener() {
		mAllCoursesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Course course = mAllCoursesList.get(position);
				Intent intent = new Intent(getActivity(), DetailPlanActivity.class);
				intent.putExtra(Const.KEY_COURSE, course);
				startActivity(intent);
			}
		});
	}

	private void initData() {
		mAllCoursesList = new ArrayList<Course>();
		
		mAllCoursesList = CourseDao.getInstance().getCourseListFromDb(Globle.gApplicationContext);
		
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
