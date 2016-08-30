package com.runrunfast.homegym.course;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.bean.Course;
import com.runrunfast.homegym.bean.Course.ActionDetail;
import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.bean.MyCourse.DayProgress;
import com.runrunfast.homegym.course.CourseServerMgr.IDeleteCourseToServerListener;
import com.runrunfast.homegym.course.CourseServerMgr.IJoinCourseToServerListener;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.MyCourseDao;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;
import com.runrunfast.homegym.widget.DialogActivity;
import com.runrunfast.homegym.widget.KCalendar;
import com.runrunfast.homegym.widget.KCalendar.OnCalendarClickListener;
import com.runrunfast.homegym.widget.KCalendar.OnCalendarDateChangedListener;

import java.util.ArrayList;

public class DetailPlanActivity extends Activity implements OnClickListener{
	private final String TAG = "DetailPlanActivity";
	
	private Resources mResources;
	
	private RelativeLayout haveActionsLayout, restDayLayout;
	private TextView tvTitle;
	private Button btnLeft, btnRight;
	private Button btnJoin;
	private KCalendar kCalendar;
	private TextView tvCalendarDate;
	private ImageView ivLastMonth, ivNextMonth;
	private ListView mCurrentDayListView;
	private CurrentDayTrainAdapter mCurrentDayTrainAdapter;
	
	private boolean isCourseExist = false;
	private boolean isMyCourse = false;
	private String mCourseId;
	private Course mCourse;
	private MyCourse mMyCourse;
	
	private UserInfo mUserInfo;
	
//	private String mMyCourseStartDate; // 在我参加的课程中才会有，第一天的日期
//	private ArrayList<String> mDateBeforeList; // 在我参加的课程中已经过去的日子
	
	private ArrayList<String> mCourseDateList; // 课程的日期集合，如2016-07-29, ....
//	private ArrayList<String> mCourseDateFinishList; // 课程完成的日期列表
	
	private ArrayList<ActionDetail> mActionDetailListOfThatDay; // 指定某天的ActionDetail集合
	private ArrayList<Action> mActionsOfThatDay; // 指定某天的动作信息集合
	
	private int mSelectDayPosition; // 该天在日期分布的位置
	
	private IJoinCourseToServerListener mIJoinCourseToServerListener;
	private IDeleteCourseToServerListener mIDeleteCourseToServerListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_detail_plan);
		
		mResources = getResources();
		
		initView();
		
		initData();
		
		initCalendarListener();
		
		initJoinCourseListener();
		
		initDeleteCourseListener();
	}

	private void initCalendarListener() {
		kCalendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
			
			@Override
			public void onCalendarDateChanged(int year, int month) {
				tvCalendarDate.setText(year + mResources.getString(R.string.year) + month + mResources.getString(R.string.month));
			}
		});
		
		kCalendar.setOnCalendarClickListener(new OnCalendarClickListener() {
			
			@Override
			public void onCalendarClick(int row, int col, String dateFormat) {
				// dateFormat = 2016-07-27
//				Log.i(TAG, "onCalendarClick, row = " + row + ", col = " + col + ", dateFormat = " + dateFormat);
				
				handleCalendarClick(dateFormat);
			}
			
		});
	}
	
	@SuppressLint("ResourceAsColor")
	private void handleCalendarClick(String dateFormat) {
		if( mCourseDateList.contains(dateFormat) ){
			int datePosition = mCourseDateList.indexOf(dateFormat);
			updateActionsDependDayPosition(datePosition, mCourse);
			
			showActions();
			mCurrentDayTrainAdapter.updateData(mActionDetailListOfThatDay, mActionsOfThatDay);
		}else{
			mActionDetailListOfThatDay.clear();
			mActionsOfThatDay.clear();
			
			showRest();
			mCurrentDayTrainAdapter.updateData(mActionDetailListOfThatDay, mActionsOfThatDay);
		}
	}

	@SuppressLint("ResourceAsColor")
	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		mCourse = (Course) getIntent().getSerializableExtra(Const.KEY_COURSE);
		mCourseId = mCourse.course_id;
		
		mActionsOfThatDay = new ArrayList<Action>();
		mActionDetailListOfThatDay = new ArrayList<ActionDetail>();
		mCourseDateList = new ArrayList<String>();
		
		String currentDateStr = DateUtil.getCurrentDate();
		
		isCourseExist = isCourseExist();
		if(mCourse instanceof MyCourse){
			mMyCourse = (MyCourse) mCourse;
			isMyCourse = true;
			if(mMyCourse.progress == MyCourse.COURSE_PROGRESS_EXPIRED){
				btnJoin.setVisibility(View.GONE);
			}
		}else{
			isMyCourse = false;
		}
		
		if(isMyCourse){
			btnJoin.setText(R.string.start_train);
			btnRight.setVisibility(View.VISIBLE);
			
			handleMyCourseActionDaysDistribution(currentDateStr);
		}else{
			btnJoin.setText(R.string.join_train);
			btnRight.setVisibility(View.INVISIBLE);
			
			handleCourseActionDaysDistribution(0, currentDateStr);
		}
		
		kCalendar.setCalendarDayBgColor(currentDateStr, R.color.calendar_day_select);
		
		tvCalendarDate.setText(kCalendar.getCalendarYear() + mResources.getString(R.string.year)
				+ kCalendar.getCalendarMonth() + mResources.getString(R.string.month));
		
		if(mCourseDateList.contains(currentDateStr)){
			showActions();
		}else{
			showRest();
		}
		mCurrentDayTrainAdapter = new CurrentDayTrainAdapter(this, mActionDetailListOfThatDay, mActionsOfThatDay);
		mCurrentDayListView.setAdapter(mCurrentDayTrainAdapter);
	}
	
	/**
	  * @Method: showActions
	  * @Description: 显示动作列表布局
	  * 返回类型：void 
	  */
	private void showActions(){
		haveActionsLayout.setVisibility(View.VISIBLE);
		restDayLayout.setVisibility(View.INVISIBLE);
	}
	
	/**
	  * @Method: showRest
	  * @Description: 显示休息日
	  * 返回类型：void 
	  */
	private void showRest(){
		haveActionsLayout.setVisibility(View.INVISIBLE);
		restDayLayout.setVisibility(View.VISIBLE);
	}

//	@SuppressLint("ResourceAsColor")
//	private void setCalendar() {
//		kCalendar.setCalendarDaysTextColor(mCourseDateList, mResources.getColor(R.color.calendar_have_course));
//		kCalendar.setCalendarDayBgColor(DateUtil.getCurrentDate(), R.color.calendar_day_select);
//	}

	/**
	  * @Method: handleCourseActionDaysDistribution
	  * @Description: 一般课程指定日期的动作集合	
	  * 返回类型：void 
	  */
	private void handleCourseActionDaysDistribution(int position, String currentDateStr) {
		// 课程的日期分布
		int dayNum = mCourse.course_detail.size();
		for(int i=0; i<dayNum; i++){
			CourseDetail courseDetail = mCourse.course_detail.get(i);
			String dateStr = DateUtil.getDateStrOfDayNumFromStartDate(courseDetail.day_num, currentDateStr);
			mCourseDateList.add(dateStr);
			kCalendar.setCalendarDayTextColor(dateStr, mResources.getColor(R.color.calendar_have_course)); // 标注计划的日期
		}
		
		updateActionsDependDayPosition(position, mCourse);
	}

	/**
	  * @Method: updateActionsDependDayPosition
	  * @Description: 根据日期位置重新获取动作集合
	  * @param position	
	  * 返回类型：void 
	  */
	private void updateActionsDependDayPosition(int position, Course course) {
		mSelectDayPosition = position;
		
		mActionDetailListOfThatDay.clear();
		mActionsOfThatDay.clear();
		// 当天的动作集合
		ArrayList<ActionDetail> actionDetails = (ArrayList<ActionDetail>) mCourse.course_detail.get(position).action_detail;
		int actionNum = actionDetails.size();
		
		for(int i=0; i<actionNum; i++){
			ActionDetail actionDetail = actionDetails.get(i);
			mActionDetailListOfThatDay.add(actionDetail);
			
			String actionId = actionDetail.action_id;
			Action action = ActionDao.getInstance().getActionFromDb(Globle.gApplicationContext, actionId);
			mActionsOfThatDay.add(action);
		}
	}

	/**
	  * @Method: handleMyCourseActionDaysDistribution
	  * @Description: 我参加的课程指定日期的动作集合	
	  * @param currentDateStr	当天日期
	  * 返回类型：void 
	  */
	@SuppressLint("ResourceAsColor")
	private void handleMyCourseActionDaysDistribution(String currentDateStr) {
		// 参加的课程的日期分布
		int currentDayPosition = -1;
		ArrayList<DayProgress> dayProgressList = (ArrayList<DayProgress>) mMyCourse.day_progress;
		int dayNum = dayProgressList.size();
		for(int i=0; i<dayNum; i++){
			DayProgress dayProgress = dayProgressList.get(i);
			String dateStr = DateUtil.getDateStrOfDayNumFromStartDate(dayProgress.day_num, mMyCourse.start_date);
			int progress = dayProgress.progress;
			mCourseDateList.add(dateStr);
			
			kCalendar.setCalendarDayTextColor(dateStr, mResources.getColor(R.color.calendar_have_course)); // 标注计划的日期
			if(progress == MyCourse.DAY_PROGRESS_FINISH){
				kCalendar.addMark(dateStr, 0); // 标注完成的日期
			}
			
			if(currentDayPosition != -1){
				continue;
			}
			if(dateStr.equals(currentDateStr)){
				currentDayPosition = i;
			}
		}
		
		if(currentDayPosition == -1){
			// 休息日
			showRest();
		}else{
			// 当天的动作集合
			updateActionsDependDayPosition(currentDayPosition, mCourse);
		}
	}

//	private boolean isMyCourse() {
//		Course course = MyCourseDao.getInstance().getMyCourseFromDb(Globle.gApplicationContext, mCourseId);
//		if(course == null){
//			return false;
//		}else{
//			return true;
//		}
//		
//	}

	private void initView() {
		kCalendar = (KCalendar)findViewById(R.id.calendar_view);
		
		tvTitle = (TextView)findViewById(R.id.actionbar_title);
		tvTitle.setText(R.string.train_detail);
		btnLeft = (Button)findViewById(R.id.actionbar_left_btn);
		btnLeft.setBackgroundResource(R.drawable.nav_back);
		
		btnRight = (Button)findViewById(R.id.actionbar_right_btn);
		btnRight.setText(R.string.exit_train);
		btnRight.setOnClickListener(this);
		btnRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		btnRight.setTextColor(getResources().getColor(R.color.about_email_content_text_color));
		btnRight.setVisibility(View.INVISIBLE);
		
		tvCalendarDate = (TextView)findViewById(R.id.calendar_date_text);
		ivLastMonth = (ImageView)findViewById(R.id.calendar_left_img);
		ivLastMonth.setOnClickListener(this);
		
		ivNextMonth = (ImageView)findViewById(R.id.calendar_right_img);
		ivNextMonth.setOnClickListener(this);
		
		mCurrentDayListView = (ListView)findViewById(R.id.course_train_list);
		
		btnJoin = (Button)findViewById(R.id.btn_join_in_train);
		btnJoin.setOnClickListener(this);
		
		haveActionsLayout = (RelativeLayout)findViewById(R.id.have_actions_layout);
		restDayLayout = (RelativeLayout)findViewById(R.id.rest_day_layout);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left_btn:
			finish();
			break;
			
		case R.id.actionbar_right_btn:
			showExitDialog();
			break;
			
		case R.id.calendar_left_img:
			toLastMonth();
			break;
			
		case R.id.calendar_right_img:
			toNextMonth();
			break;
			
		case R.id.btn_join_in_train:
			prepareJoinCourse();
			break;

		default:
			break;
		}
	}

	private void showExitDialog() {
		Intent intent = new Intent(this, DialogActivity.class);
		intent.putExtra(DialogActivity.KEY_CONTENT, mResources.getString(R.string.exit_train_or_not));
		intent.putExtra(DialogActivity.KEY_CANCEL, mResources.getString(R.string.no));
		intent.putExtra(DialogActivity.KEY_CONFIRM, mResources.getString(R.string.yes));
		startActivityForResult(intent, Const.DIALOG_REQ_CODE_EXIT_COURSE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "resultCode = " + resultCode);
		if(requestCode == Const.DIALOG_REQ_CODE_EXIT_COURSE && resultCode == DialogActivity.RSP_CONFIRM){
			// 删除服务器数据
			CourseServerMgr.getInstance().deleteCourseToServer(mUserInfo.strAccountId, mCourseId, mMyCourse.start_date);
		}
	}

	private void initJoinCourseListener() {
		mIJoinCourseToServerListener = new IJoinCourseToServerListener() {
			
			@Override
			public void onJoinCourseToServerSuc() {
				prepareToSaveMyCourse();
				btnRight.setVisibility(View.VISIBLE);
				btnJoin.setText(R.string.start_train);
				isMyCourse = true;
			}
			
			@Override
			public void onJoinCourseToServerFail() {
				Toast.makeText(DetailPlanActivity.this, "参加失败", Toast.LENGTH_SHORT).show();
			}
		};
		CourseServerMgr.getInstance().addJoinCourseToServerObserver(mIJoinCourseToServerListener);
	}
	
	private void initDeleteCourseListener(){
		mIDeleteCourseToServerListener = new IDeleteCourseToServerListener() {
			
			@Override
			public void onDeleteCourseToServerSuc() {
				// 删除本地数据
				MyCourseDao.getInstance().deleteMyCourseFromDb(Globle.gApplicationContext, mUserInfo.strAccountId, mCourseId, mMyCourse.start_date);
				// 退出界面
				exitTrain();
			}
			
			@Override
			public void onDeleteCourseToServerFail() {
				Toast.makeText(DetailPlanActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
			}
		};
		CourseServerMgr.getInstance().addDeleteCourseToServerObserver(mIDeleteCourseToServerListener);
	}
	
	private void prepareJoinCourse() {
		// 不是我的课程，点击添加到我的课程
		if( !isMyCourse ){
			CourseServerMgr.getInstance().joinCourseToServer(mUserInfo.strAccountId, mCourseId, DateUtil.getCurrentDate());
		}
		// 本地不存在此课程视频等信息
		else{
			startTrain();
		}
		
	}
	
	private void startTrain() {
		Intent intent = new Intent(this, CourseVideoActivity.class);
		intent.putExtra(Const.KEY_COURSE, mMyCourse);
		intent.putExtra(Const.KEY_DAY_POSITION, mSelectDayPosition);
		startActivity(intent);
	}

	private void prepareToSaveMyCourse() {
		mMyCourse = createMyCourseFromCourse();
		
		MyCourseDao.getInstance().saveMyCourseToDb(Globle.gApplicationContext, AccountMgr.getInstance().mUserInfo.strAccountId, mMyCourse);
	}
	
	private MyCourse createMyCourseFromCourse(){
		MyCourse myCourse = new MyCourse();
		
		ArrayList<DayProgress> dayProgresseList = new ArrayList<MyCourse.DayProgress>();
		
		String uid = mUserInfo.strAccountId;
		int progress = MyCourse.COURSE_PROGRESS_ING;
		int dateNum = mCourseDateList.size();
		String strStartDate = mCourseDateList.get(0);
		for(int i=0; i<dateNum; i++){
			CourseDetail courseDetail = mCourse.course_detail.get(i);
			DayProgress dayProgress = new DayProgress();
			dayProgress.day_num = courseDetail.day_num;
			dayProgress.progress = MyCourse.DAY_PROGRESS_UNFINISH;
			dayProgresseList.add(dayProgress);
		}
		
		myCourse.uid = uid;
		myCourse.progress = progress;
		myCourse.start_date = strStartDate;
		myCourse.day_progress = dayProgresseList;
		myCourse.course_id = mCourse.course_id;
		myCourse.course_name = mCourse.course_name;
		myCourse.course_new = mCourse.course_new;
		myCourse.course_detail = mCourse.course_detail;
		myCourse.course_quality = mCourse.course_quality;
		myCourse.course_recommend = mCourse.course_recommend;
		myCourse.course_img_url = mCourse.course_img_url;
		myCourse.course_img_local = mCourse.course_img_local;
		
		return myCourse;
	}

	/**
	  * @Method: isCourseExist
	  * @Description: 判断本课程本地是否有下载
	  * @return	
	  * 返回类型：boolean 
	  */
	private boolean isCourseExist(){
		
		return false;
	}
	
	private void toNextMonth() {
		kCalendar.nextMonth();
	}

	private void toLastMonth() {
		kCalendar.lastMonth();
	}

	private void exitTrain() {
		finish();
	}
	
	@Override
	protected void onDestroy() {
		
		if(mIJoinCourseToServerListener != null){
			CourseServerMgr.getInstance().removeJoinCourseToServerObserver(mIJoinCourseToServerListener);
		}
		
		if(mIDeleteCourseToServerListener != null){
			CourseServerMgr.getInstance().removeDeleteCourseToServerObserver(mIDeleteCourseToServerListener);
		}
		
		super.onDestroy();
	}
}
