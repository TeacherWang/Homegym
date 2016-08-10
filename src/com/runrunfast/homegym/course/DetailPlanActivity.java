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
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.bean.Course;
import com.runrunfast.homegym.bean.Course.ActionDetail;
import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.bean.MyCourse.DayProgress;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.MyCourseActionDao;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_detail_plan);
		
		mResources = getResources();
		
		initView();
		
		initData();
		
		initCalendarListener();
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
				Log.i(TAG, "onCalendarClick, row = " + row + ", col = " + col + ", dateFormat = " + dateFormat);
				
				handleCalendarClick(dateFormat);
			}
			
		});
	}
	
	@SuppressLint("ResourceAsColor")
	private void handleCalendarClick(String dateFormat) {
		if( mCourseDateList.contains(dateFormat) ){
			int datePosition = mCourseDateList.indexOf(dateFormat);
			handleCourseActionDaysDistribution(datePosition);
			mCurrentDayTrainAdapter.updateData(mActionDetailListOfThatDay, mActionsOfThatDay);
		}else{
			mActionDetailListOfThatDay.clear();
			mActionsOfThatDay.clear();
			mCurrentDayTrainAdapter.updateData(mActionDetailListOfThatDay, mActionsOfThatDay);
		}
	}

	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		mCourse = (Course) getIntent().getSerializableExtra(Const.KEY_COURSE);
		mCourseId = mCourse.course_id;
		
		mActionsOfThatDay = new ArrayList<Action>();
		mActionDetailListOfThatDay = new ArrayList<ActionDetail>();
		mCourseDateList = new ArrayList<String>();
		
		isCourseExist = isCourseExist();
		MyCourse myCourse = MyCourseDao.getInstance().getMyCourseFromDb(Globle.gApplicationContext, mCourseId);
		if(myCourse != null){
			isMyCourse = true;
			mMyCourse = myCourse;
		}else{
			isMyCourse = false;
		}
		
		if(isMyCourse){
			btnJoin.setText(R.string.start_train);
			btnRight.setVisibility(View.VISIBLE);
			
			handleMyCourseActionDaysDistribution(myCourse.start_date);
//			// 把间隔天数转换为日期集合
//			mCourseDateList = CourseUtil.getCourseDateList(myCourse.start_date, mMyCourse.course_detail);
		}else{
			btnJoin.setText(R.string.join_train);
			btnRight.setVisibility(View.INVISIBLE);
			
			handleCourseActionDaysDistribution(0);
//			//把间隔天数转换为日期集合
//			mCourseDateList = CourseUtil.getCourseDateList(DateUtil.getCurrentDate(), mCourse.course_detail);
		}
		
		tvCalendarDate.setText(kCalendar.getCalendarYear() + mResources.getString(R.string.year)
				+ kCalendar.getCalendarMonth() + mResources.getString(R.string.month));
		
		mCurrentDayTrainAdapter = new CurrentDayTrainAdapter(this, mActionDetailListOfThatDay, mActionsOfThatDay);
		mCurrentDayListView.setAdapter(mCurrentDayTrainAdapter);
		
//		setCalendar();
	}

	@SuppressLint("ResourceAsColor")
	private void setCalendar() {
		kCalendar.setCalendarDaysTextColor(mCourseDateList, mResources.getColor(R.color.calendar_have_course));
		kCalendar.setCalendarDayBgColor(DateUtil.getCurrentDate(), R.color.calendar_day_select);
	}

	/**
	  * @Method: handleCourseActionDaysDistribution
	  * @Description: 一般课程指定日期的动作集合	
	  * 返回类型：void 
	  */
	private void handleCourseActionDaysDistribution(int position) {
		// 课程的日期分布
		String currentDateStr = DateUtil.getCurrentDate();
		int dayNum = mCourse.course_detail.size();
		for(int i=0; i<dayNum; i++){
			CourseDetail courseDetail = mCourse.course_detail.get(i);
			String dateStr = DateUtil.getDateStrOfDayNumFromStartDate(courseDetail.day_num, currentDateStr);
			mCourseDateList.add(dateStr);
			kCalendar.setCalendarDayTextColor(dateStr, mResources.getColor(R.color.calendar_have_course)); // 标注计划的日期
		}
		
		// 当天的动作集合
		mActionDetailListOfThatDay = (ArrayList<ActionDetail>) mCourse.course_detail.get(position).action_detail;
		int actionNum = mActionDetailListOfThatDay.size();
		for(int i=0; i<actionNum; i++){
			ActionDetail actionDetail = mActionDetailListOfThatDay.get(i);
			String actionId = actionDetail.action_id;
			Action action = ActionDao.getInstance().getActionFromDb(Globle.gApplicationContext, actionId);
			mActionsOfThatDay.add(action);
		}
				
				
				
				
		
//		String actionString = mCourse.dateActionIdList.get(position).trim();
//		
//		mActionInfoListOfThatDay.clear();
//		mActionIdsOfThatDay = actionString.split(";");// 获取第position + 1天的动作集合{a1,a2,a3}
//		int actionSize = mActionIdsOfThatDay.length;
//		for(int i=0; i<actionSize; i++){
//			String actionId = mActionIdsOfThatDay[i].trim();
//			ActionInfo actionInfo = ActionDao.getInstance().getActionInfoFromDb(Globle.gApplicationContext, actionId);
//			mActionInfoListOfThatDay.add(actionInfo);
//		}
	}

	/**
	  * @Method: handleMyCourseActionDaysDistribution
	  * @Description: 我参加的课程指定日期的动作集合	
	  * @param myCourseStartDateStr	开始的日期
	  * 返回类型：void 
	  */
	private void handleMyCourseActionDaysDistribution(String myCourseStartDateStr) {
		// 参加的课程的日期分布
		int currentDayPosition = -1;
		String currentDateStr = DateUtil.getCurrentDate();
		ArrayList<DayProgress> dayProgressList = (ArrayList<DayProgress>) mMyCourse.day_progress;
		int dayNum = dayProgressList.size();
		for(int i=0; i<dayNum; i++){
			DayProgress dayProgress = dayProgressList.get(i);
			String dateStr = dayProgress.plan_date;
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
		
		// 当天的动作集合
		mActionDetailListOfThatDay = (ArrayList<ActionDetail>) mMyCourse.course_detail.get(currentDayPosition).action_detail;
		int actionNum = mActionDetailListOfThatDay.size();
		for(int i=0; i<actionNum; i++){
			ActionDetail actionDetail = mActionDetailListOfThatDay.get(i);
			String actionId = actionDetail.action_id;
			Action action = ActionDao.getInstance().getActionFromDb(Globle.gApplicationContext, actionId);
			mActionsOfThatDay.add(action);
		}
		
//		// 获取当天跟开始日期的间隔天数，1为当天，2为第二天，以此类推
//		int currentDayNumBetweenStartDay = DateUtil.getDaysNumBetweenCurrentDayAndStartDay(myCourseStartDateStr);
//		// 如果天数集合不包含当天的，那么界面显示休息日
//		if( !mCourseInfo.dateNumList.contains(String.valueOf(currentDayNumBetweenStartDay)) ){
//			Log.d(TAG, "handleMyCourseActionDaysDistribution, my course dateNumList not have current day, ignore");
//			return;
//		}
		
//		// 天数集合中含当天，那么取出当天在集合中的位置，并根据位置，取出对应的动作集合，如{a1,a2}
//		int dayNumPosition = mCourseInfo.dateNumList.indexOf(String.valueOf(currentDayNumBetweenStartDay));
//		String actionString = mCourseInfo.dateActionIdList.get(dayNumPosition).trim();
//		
//		mActionIdsOfThatDay = actionString.split(";");// 获取指定日期的动作集合，如{a1,a2}
//		int actionSize = mActionIdsOfThatDay.length;
//		for(int i=0; i<actionSize; i++){
//			String actionId = mActionIdsOfThatDay[i].trim();
//			ActionInfo myActionInfo = MyCourseActionDao.getInstance().getMyCourseActionInfo(Globle.gApplicationContext, mUserInfo.strAccountId, mCourseId, actionId);
//			ActionInfo actionInfo = ActionDao.getInstance().getActionInfoFromDb(Globle.gApplicationContext, actionId);
//			myActionInfo.actionName = actionInfo.actionName;
//			mActionInfoListOfThatDay.add(myActionInfo);
//		}
//		// 测试，默认第一天完成
//		kCalendar.addMark(myCourseStartDateStr, 0);
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
			// 删除本地数据
			MyCourseDao.getInstance().deleteMyCourse(Globle.gApplicationContext, mUserInfo.strAccountId, mCourseId);
			MyCourseActionDao.getInstance().deleteMyCourseAction(Globle.gApplicationContext, mUserInfo.strAccountId, mCourseId);
			// 退出界面
			exitTrain();
		}
	}

	private void prepareJoinCourse() {
		// 不是我的课程，点击添加到我的课程
		if( !isMyCourse ){
			prepareToSaveMyCourse();
			btnRight.setVisibility(View.VISIBLE);
			btnJoin.setText(R.string.start_train);
		}
		// 本地不存在此课程视频等信息
		else{
			// 先下载
			
		}
		
	}

	private void prepareToSaveMyCourse() {
		MyCourse myCourse = createMyCourseFromCourse();
		
		MyCourseDao.getInstance().saveMyCourseToDb(Globle.gApplicationContext, AccountMgr.getInstance().mUserInfo.strAccountId, myCourse);
	}
	
	private MyCourse createMyCourseFromCourse(){
		MyCourse myCourse = new MyCourse();
		
		ArrayList<DayProgress> dayProgresseList = new ArrayList<MyCourse.DayProgress>();
		
		String uid = mUserInfo.strAccountId;
		int progress = MyCourse.COURSE_PROGRESS_ING;
		int dateNum = mCourseDateList.size();
		String strStartDate = mCourseDateList.get(0);
		for(int i=0; i<dateNum; i++){
			DayProgress dayProgress = new DayProgress();
			dayProgress.plan_date = mCourseDateList.get(i);
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
}
