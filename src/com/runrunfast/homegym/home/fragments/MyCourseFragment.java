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
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.bean.Course;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.bean.MyCourse.DayProgress;
import com.runrunfast.homegym.course.CourseAdapter;
import com.runrunfast.homegym.course.CourseAdapter.ICourseAdapterListener;
import com.runrunfast.homegym.course.CourseServerMgr.IDownloadTrainPlanLister;
import com.runrunfast.homegym.course.CourseServerMgr.IGetCourseFromServerListener;
import com.runrunfast.homegym.course.CourseServerMgr;
import com.runrunfast.homegym.course.CourseTrainActivity;
import com.runrunfast.homegym.course.DetailPlanActivity;
import com.runrunfast.homegym.dao.CourseDao;
import com.runrunfast.homegym.dao.MyCourseDao;
import com.runrunfast.homegym.home.HomeActivity;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;
import java.util.List;

public class MyCourseFragment extends Fragment{
	private final String TAG = "MyTrainingFragment";
	
	private View rootView;
	
	private ListView mMyCourseListView;
	
	private CourseAdapter mMyCourseAdapter;
	
	private ArrayList<Course> mMyCourseList;
	private ArrayList<Course> mRecommedList;
	
	private ICourseAdapterListener mICourseAdapterListener;
	
	private UserInfo mUserInfo;
	
	private IGetCourseFromServerListener mIGetCourseFromServerListener;
	private IDownloadTrainPlanLister mIDownloadTrainPlanLister;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_my_training, container, false);
		
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
				Log.i(TAG, "MyCourseFragment, onGetCourseSucFromServer");
				
//				getData();
			}
			
			@Override
			public void onGetCoruseFailFromServer() {
				
			}
		};
		CourseServerMgr.getInstance().addGetCourseFromServerObserver(mIGetCourseFromServerListener);
		
		mIDownloadTrainPlanLister = new IDownloadTrainPlanLister() {
			
			@Override
			public void onDownloadTrainPlanSuc() {
				Log.i(TAG, "onDownloadTrainPlanSuc");
				
				getData();
			}
			
			@Override
			public void onDownloadTrainPlanFail() {
				
			}
		};
		CourseServerMgr.getInstance().addDownloadTrainPlanObserver(mIDownloadTrainPlanLister);
	}

	@Override
	public void onResume() {
		super.onResume();
		
		Log.i(TAG, "onResume");
		
		getData();
		
		initAddCourseListener();
	}

	private void handleMyCourseExpireOrNot() {
		String currentDay = DateUtil.getCurrentDate();
		int myCourseSize = mMyCourseList.size();
		for(int i=0; i<myCourseSize; i++){
			MyCourse myCourse = (MyCourse) mMyCourseList.get(i);
			List<DayProgress> dayProgresseList = myCourse.day_progress;
			// 如果该课程已过期或者已完成，则忽略
			if(myCourse.progress == MyCourse.COURSE_PROGRESS_EXPIRED
					|| myCourse.progress == MyCourse.COURSE_PROGRESS_FINISH){
				continue;
			}
			// 如果当天时间已经超过课程的最后日期，则设置为已过期
			String planDate = DateUtil.getDateStrOfDayNumFromStartDate(myCourse.course_period, myCourse.start_date);
			if(DateUtil.getMillsFromStrDate(currentDay) > DateUtil.getMillsFromStrDate(planDate)){
				myCourse.progress = MyCourse.COURSE_PROGRESS_EXPIRED;
				MyCourseDao.getInstance().saveMyCourseProgress(Globle.gApplicationContext, mUserInfo.strAccountId, myCourse.course_id, myCourse.progress);
				continue;
			}
			// 判断是否是休息日
			myCourse.progress = MyCourse.COURSE_PROGRESS_REST;
			int daySize = dayProgresseList.size();
			for(int j=0; j<daySize; j++){
				DayProgress dayProgress = dayProgresseList.get(j);
				String dayPlanDate = DateUtil.getDateStrOfDayNumFromStartDate(dayProgress.day_num, myCourse.start_date);
				if(dayPlanDate.equals(currentDay)){
					myCourse.progress = MyCourse.COURSE_PROGRESS_ING;
					break;
				}
			}
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
				
				handleCourseItemClick(course);
			}

		});
		
	}

	private void initAddCourseListener() {
		if(mICourseAdapterListener != null){
			mICourseAdapterListener = null;
		}
		
		mICourseAdapterListener = new ICourseAdapterListener() {
			
			@Override
			public void onAddCourseClicked() {
				((HomeActivity)getActivity()).handleClickAllCourse();
			}
		};
		
		mMyCourseAdapter.setOnCourseAdapterListener(mICourseAdapterListener);
	}
	
	private void handleCourseItemClick(Course course) {
		Intent intent = null;
		
		if(course instanceof MyCourse){
//			if(((MyCourse) course).progress == MyCourse.COURSE_PROGRESS_EXPIRED){
//				Toast.makeText(getActivity(), R.string.this_mycourse_is_expired, Toast.LENGTH_SHORT).show();
//				return;
//			}
			intent = new Intent(getActivity(), CourseTrainActivity.class);
		}else{
			intent = new Intent(getActivity(), DetailPlanActivity.class);
		}
		intent.putExtra(Const.KEY_COURSE, course);
		startActivity(intent);
	}

	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
//		mMyCourseList = MyCourseDao.getInstance().getMyCourseListFromDb(Globle.gApplicationContext);
//		
//		if(mMyCourseList.size() == 0){
//			mMyCourseList.add(new InvalidCourse(InvalidCourse.COURSE_TYPE_EMPTY));
//			mMyCourseAdapter = new CourseAdapter(getActivity(), mMyCourseList);
//			AllCoursesFragment.setListViewHeightBasedOnChildren(mMyCourseListView);
//			mMyCourseListView.setAdapter(mMyCourseAdapter);
//		}else{
//			mMyCourseAdapter = new CourseAdapter(getActivity(), mMyCourseList);
//			AllCoursesFragment.setListViewHeightBasedOnChildren(mMyCourseListView);
//			mMyCourseListView.setAdapter(mMyCourseAdapter);
//		}
//		
//		mRecommedList = CourseDao.getInstance().getRecommedCourseListFromDb(Globle.gApplicationContext);
	}
	
	private void getData() {
		mMyCourseList = MyCourseDao.getInstance().getMyCourseListFromDb(Globle.gApplicationContext, mUserInfo.strAccountId);
		mRecommedList = CourseDao.getInstance().getRecommedCourseListFromDb(Globle.gApplicationContext);
		
		if(mMyCourseList.size() == 0){
			mMyCourseList.add(new InvalidCourse(InvalidCourse.COURSE_TYPE_EMPTY));
			mMyCourseList.add(new InvalidCourse(InvalidCourse.COURSE_TYPE_SHOW_RECOMMED_TEXT));
		}else{
			// 根据当天日期保存课程是否过期
			handleMyCourseExpireOrNot();
		}
		
		mMyCourseList.add(new InvalidCourse(InvalidCourse.COURSE_TYPE_SHOW_RECOMMED_TEXT));
		mMyCourseList.addAll(mRecommedList);
		
		mMyCourseAdapter = new CourseAdapter(getActivity(), mMyCourseList);
//		AllCoursesFragment.setListViewHeightBasedOnChildren(mMyCourseListView);
		mMyCourseListView.setAdapter(mMyCourseAdapter);
		
//		mMyCourseList = MyCourseDao.getInstance().getMyCourseListFromDb(Globle.gApplicationContext);
//		if(mMyCourseList.size() > 0){
//			// 根据当天日期保存课程是否过期
//			handleMyCourseExpireOrNot();
//			
//			mMyCourseList.add(new InvalidCourse(InvalidCourse.COURSE_TYPE_SHOW_RECOMMED_TEXT));
//			
//			mMyCourseList.addAll(mRecommedList);
//			mMyCourseAdapter.updateData(mMyCourseList);
//		}else{
//			mMyCourseList.add(new InvalidCourse(InvalidCourse.COURSE_TYPE_EMPTY));
//			
//			mMyCourseList.add(new InvalidCourse(InvalidCourse.COURSE_TYPE_SHOW_RECOMMED_TEXT));
//			
//			mMyCourseList.addAll(mRecommedList);
//			mMyCourseAdapter.updateData(mMyCourseList);
//		}
	}

	private void initView() {
		mMyCourseListView = (ListView)rootView.findViewById(R.id.my_course_listview);
//		mRecommedCourseListView = (ListView)rootView.findViewById(R.id.course_recommed_listview);
	}
	
	@Override
	public void onDestroyView() {
		if(mIGetCourseFromServerListener != null){
			CourseServerMgr.getInstance().removeGetCourseFromServerObserver(mIGetCourseFromServerListener);
		}
		
		if(mIDownloadTrainPlanLister != null){
			CourseServerMgr.getInstance().removeDownloadTrainPlanObserver(mIDownloadTrainPlanLister);
		}
		
		super.onDestroyView();
	}
	
	// 不要删除，切换fragment用到
    @Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
    
}
