package com.runrunfast.homegym.home.fragments;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.runrunfast.homegym.course.DetailPlanActivity;
import com.runrunfast.homegym.dao.CourseDao;
import com.runrunfast.homegym.dao.MyCourseDao;
import com.runrunfast.homegym.home.HomeActivity;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.Globle;

public class MyTrainingFragment extends Fragment{
	private final String TAG = "MyTrainingFragment";
	
	private View rootView;
	private ListView mMyCourseListView;
	
	private CourseAdapter mMyCourseAdapter;
	
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
	
	@Override
	public void onResume() {
		super.onResume();
		
		Log.i(TAG, "onResume");
		
		mMyCourseList = MyCourseDao.getInstance().getMyCourseInfoList(Globle.gApplicationContext);
		if(mMyCourseList.size() > 0){
			CourseInfo recommendDescipt = new CourseInfo();
			recommendDescipt.isRecommendDescript = true;
			mMyCourseList.add(recommendDescipt);
			
			mMyCourseList.addAll(mRecommedList);
			mMyCourseAdapter.updateData(mMyCourseList, false);
		}else{
			CourseInfo emptyCourseInfo = new CourseInfo();
			emptyCourseInfo.isMyCourse = true;
			mMyCourseList.add(emptyCourseInfo);
			
			CourseInfo recommendDescipt = new CourseInfo();
			recommendDescipt.isRecommendDescript = true;
			mMyCourseList.add(recommendDescipt);
			
			mMyCourseList.addAll(mRecommedList);
			mMyCourseAdapter.updateData(mMyCourseList, true);
		}
	}

	private void initListener() {
		mMyCourseListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CourseInfo courseInfo = mMyCourseList.get(position);
				String courseId = courseInfo.courseId;
				String courseName = courseInfo.courseName;
				if(courseInfo.isRecommendDescript){
					return;
				}
				
				Intent intent = null;
				
				if(courseInfo.isMyCourse){
					intent = new Intent(getActivity(), CourseTrainActivity.class);
					intent.putStringArrayListExtra(Const.KEY_ACTION_IDS, (ArrayList<String>)courseInfo.actionIds);
				}else{
					intent = new Intent(getActivity(), DetailPlanActivity.class);
				}
				
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
		mMyCourseList = MyCourseDao.getInstance().getMyCourseInfoList(Globle.gApplicationContext);
		
		if(mMyCourseList.size() == 0){
			CourseInfo emptyCourseInfo = new CourseInfo();
			emptyCourseInfo.isMyCourse = true;
			mMyCourseList.add(emptyCourseInfo);
			mMyCourseAdapter = new CourseAdapter(getActivity(), mMyCourseList, true);
			mMyCourseListView.setAdapter(mMyCourseAdapter);
		}else{
			mMyCourseAdapter = new CourseAdapter(getActivity(), mMyCourseList, false);
			mMyCourseListView.setAdapter(mMyCourseAdapter);
		}
		
		mRecommedList = CourseDao.getInstance().getRecommedCourseInfoListFromDb(Globle.gApplicationContext);
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
