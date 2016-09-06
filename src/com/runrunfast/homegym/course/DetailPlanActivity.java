package com.runrunfast.homegym.course;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.runrunfast.homegym.download.MyDownloadMgr;
import com.runrunfast.homegym.download.MyDownloadMgr.IDownloadListener;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.ConstServer;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.FileUtils;
import com.runrunfast.homegym.utils.Globle;
import com.runrunfast.homegym.widget.DialogActivity;
import com.runrunfast.homegym.widget.KCalendar;
import com.runrunfast.homegym.widget.KCalendar.OnCalendarClickListener;
import com.runrunfast.homegym.widget.KCalendar.OnCalendarDateChangedListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailPlanActivity extends Activity implements OnClickListener{
	private final String TAG = "DetailPlanActivity";
	
	private Resources mResources;
	
	private RelativeLayout haveActionsLayout, restDayLayout;
	private TextView tvTitle;
	private Button btnLeft, btnRight;
	private Button btnJoin;
	private RelativeLayout rlProgress;
	private ProgressBar mProgressBar;
	private TextView tvFinishNum;
	private KCalendar kCalendar;
	private TextView tvCalendarDate;
	private ImageView ivLastMonth, ivNextMonth;
	private ListView mCurrentDayListView;
	private CurrentDayTrainAdapter mCurrentDayTrainAdapter;
	
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
	
	private ArrayList<String> mAllActionIdList; // 该课程所有的动作Id集合
	private ArrayList<Action> mAllActionList; // 该课程所有的动作信息集合
	
	private int mSelectDayPosition; // 该天在日期分布的位置
	
	private IJoinCourseToServerListener mIJoinCourseToServerListener;
	private IDeleteCourseToServerListener mIDeleteCourseToServerListener;
	
	private IDownloadListener mIDownloadListener;
	
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
		
		initDownloadObserver();
	}

	private void initDownloadObserver() {
		mIDownloadListener = new IDownloadListener() {
			
			@Override
			public void onDownloadProgress(String courseId, final int percent) {
				if( !courseId.equals(mCourseId) ){
					Log.d(TAG, "onDownloadProgress, courseId = " + courseId + ",mCourseId =   + mCourseId");
					return;
				}
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						rlProgress.setVisibility(View.VISIBLE);
						btnJoin.setVisibility(View.INVISIBLE);
						
						Log.i(TAG, "onDownloadProgress, percent = " + percent);
						mProgressBar.setProgress(percent);
					}
				});
			}

			@Override
			public void onDownloadComplete(String courseId) {
				if( !courseId.equals(mCourseId) ){
					Log.d(TAG, "onDownloadComplete, courseId = " + courseId + ",mCourseId =   + mCourseId");
					return;
				}
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// 不是我的课程，自动添加到我的课程
						if( !isMyCourse ){
							CourseServerMgr.getInstance().joinCourseToServer(mUserInfo.strAccountId, mCourseId, DateUtil.getCurrentDate());
							rlProgress.setVisibility(View.INVISIBLE);
							btnJoin.setText("请求中...");
							btnJoin.setVisibility(View.VISIBLE);
						}else{
							rlProgress.setVisibility(View.INVISIBLE);
							btnJoin.setText(R.string.start_train);
							btnJoin.setVisibility(View.VISIBLE);
						}
					}
				});
				
			}

			@Override
			public void onDownloadStart(String courseId, final int finishNum, final int totalNum) {
				if( !courseId.equals(mCourseId) ){
					Log.d(TAG, "onDownloadStart, courseId = " + courseId + ",mCourseId =   + mCourseId");
					return;
				}
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						tvFinishNum.setText(finishNum + "/" + totalNum);
					}
				});
				
			}

			@Override
			public void onDownloadFinished(String courseId, final int finishNum, final int totalNum) {
				if( !courseId.equals(mCourseId) ){
					Log.d(TAG, "onDownloadFinished, courseId = " + courseId + ",mCourseId =   + mCourseId");
					return;
				}
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						tvFinishNum.setText(finishNum + "/" + totalNum);
					}
				});
			}

			@Override
			public void onDownloadErr(String courseId) {
				if( !courseId.equals(mCourseId) ){
					Log.d(TAG, "onDownloadFinished, courseId = " + courseId + ",mCourseId =   + mCourseId");
					return;
				}
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(DetailPlanActivity.this, "下载出错！", Toast.LENGTH_SHORT).show();
						rlProgress.setVisibility(View.INVISIBLE);
						btnJoin.setVisibility(View.VISIBLE);
					}
				});
			}
		};
		MyDownloadMgr.getInstance().addOnIDownloadObserver(mIDownloadListener);
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
		mAllActionIdList = new ArrayList<String>();
		mAllActionList = new ArrayList<Action>();
		mCourseDateList = new ArrayList<String>();
		
		String currentDateStr = DateUtil.getCurrentDate();
		
		if(mCourse instanceof MyCourse){
			mMyCourse = (MyCourse) mCourse;
			isMyCourse = true;
			if(mMyCourse.progress == MyCourse.COURSE_PROGRESS_EXPIRED){
				btnJoin.setVisibility(View.INVISIBLE);
			}
		}else{
			MyCourse myCourse = MyCourseDao.getInstance().getMyCourseFromDb(Globle.gApplicationContext, mCourse.course_id);
			if(myCourse == null){
				isMyCourse = false;
			}else{
				mMyCourse = myCourse;
				isMyCourse = true;
				if(myCourse.progress == MyCourse.COURSE_PROGRESS_EXPIRED){
					btnJoin.setVisibility(View.INVISIBLE);
				}
			}
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
		
		getAllActions();
	}
	
	private void getAllActions() {
		List<CourseDetail> courseDetailList = mCourse.course_detail;
		int dayDistributionSize = courseDetailList.size();
		for(int i=0; i<dayDistributionSize; i++){
			CourseDetail courseDetail = courseDetailList.get(i);
			int actionSize = courseDetail.action_detail.size();
			for(int k=0; k<actionSize; k++){
				ActionDetail actionDetail = courseDetail.action_detail.get(k);
				if( !mAllActionIdList.contains(actionDetail.action_id) ){
					mAllActionIdList.add(actionDetail.action_id);
					Action action = ActionDao.getInstance().getActionFromDb(Globle.gApplicationContext, actionDetail.action_id);
					mAllActionList.add(action);
				}
			}
		}
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
		
		rlProgress = (RelativeLayout)findViewById(R.id.progress_layout);
		rlProgress.setVisibility(View.INVISIBLE);
		
		mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
		mProgressBar.setOnClickListener(this);
		
		tvFinishNum = (TextView)findViewById(R.id.progress_num_text);
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
			
		case R.id.progress_bar:
			
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
				getAllActions();
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
	
	/**
	  * @Method: prepareJoinCourse
	  * @Description: 参加课程：
	  * A.1、如果视频和音频不全，先下载
	  *   2、下载完后，提示用户参加
	  * B.如果视频和音频都有，直接显示参加
	  * 返回类型：void 
	  */
	private void prepareJoinCourse() {
		// 先下载
		if( needDownload() ){
			if(MyDownloadMgr.getInstance().getState() == MyDownloadMgr.DOWNLOAD_STATE_DOWNLOADING 
					&& !mCourseId.equals(MyDownloadMgr.getInstance().getCurrentCourseId())){
				Toast.makeText(this, "正在下载其他课程，请稍后", Toast.LENGTH_SHORT).show();
				return;
			}
			MyDownloadMgr.getInstance().addDownloadUrlList(mActionUrlHashMapList, mUrlStrTotalList);
			MyDownloadMgr.getInstance().startDownload(mCourseId);
			// 显示进度条
			btnJoin.setVisibility(View.INVISIBLE);
			rlProgress.setVisibility(View.VISIBLE);
			return;
		}
		
		// 不是我的课程，点击添加到我的课程
		if( !isMyCourse ){
			CourseServerMgr.getInstance().joinCourseToServer(mUserInfo.strAccountId, mCourseId, DateUtil.getCurrentDate());
		}else{
			startTrain();
		}
	}
	
	private ArrayList<String> mUrlStrTotalList = new ArrayList<String>();
	private ArrayList<HashMap<String, ArrayList<String>>> mActionUrlHashMapList = new ArrayList<HashMap<String,ArrayList<String>>>();
	
	private boolean needDownload(){
		boolean needDownload = false;
		ArrayList<String> taskList = null;
		HashMap<String, ArrayList<String>> hashActionUrl= null;;
		mUrlStrTotalList = new ArrayList<String>();
		
		int actionSize = mAllActionList.size();
		for(int i=0; i<actionSize; i++){
			taskList = new ArrayList<String>();
			
			Action action = mAllActionList.get(i);
			ArrayList<String> videoLocalList = new ArrayList<String>();
			// 处理视频video
			if( action.action_video_local == null || action.action_video_local.isEmpty()){
				needDownload = true;
				int videoSize = action.action_video_url.size();
				for(int k=0; k<videoSize; k++){
					String strUrl = action.action_video_url.get(k);
					
					String saveName = FileUtils.getFileName(strUrl);
					String localAddress = ConstServer.SDCARD_HOMEGYM_ROOT + saveName;
					videoLocalList.add(localAddress);
					taskList.add(strUrl);
				}
				ActionDao.getInstance().saveActionVideoLocalToDb(Globle.gApplicationContext, action.action_id, videoLocalList);
			}else{
				int videoSize = action.action_video_local.size();
				for(int k=0; k<videoSize; k++){
					String strUrl = action.action_video_url.get(k);
					
					String saveName = FileUtils.getFileName(strUrl);
					String localAddress = ConstServer.SDCARD_HOMEGYM_ROOT + saveName;
					videoLocalList.add(localAddress);
					if( !FileUtils.isFileExist(action.action_video_local.get(k)) ){
						needDownload = true;
						taskList.add(action.action_video_url.get(k));
					}
				}
				ActionDao.getInstance().saveActionVideoLocalToDb(Globle.gApplicationContext, action.action_id, videoLocalList);
			}
			// 处理音频audio
			String audioLocalLocation = action.action_audio_local;
			String audioUrlLocation = action.action_audio_url;
			if( TextUtils.isEmpty(audioLocalLocation) || !FileUtils.isFileExist(audioLocalLocation) ){
				if( !TextUtils.isEmpty(audioUrlLocation) ){
					needDownload = true;
					taskList.add(audioUrlLocation);
				}
			}
			
			if(taskList.size() > 0){
				hashActionUrl = new HashMap<String, ArrayList<String>>();
				hashActionUrl.put(action.action_id, taskList);
				mActionUrlHashMapList.add(hashActionUrl);
				mUrlStrTotalList.addAll(taskList);
			}
		}
		
		return needDownload;
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
//		MyDownloadMgr.getInstance().pauseDownload();
//		MyDownloadMgr.getInstance().clearUrlList();
		
		if(mIJoinCourseToServerListener != null){
			CourseServerMgr.getInstance().removeJoinCourseToServerObserver(mIJoinCourseToServerListener);
		}
		
		if(mIDeleteCourseToServerListener != null){
			CourseServerMgr.getInstance().removeDeleteCourseToServerObserver(mIDeleteCourseToServerListener);
		}
		
		if(mIDownloadListener != null){
			MyDownloadMgr.getInstance().removeIDownloadObserver(mIDownloadListener);
		}
		
		super.onDestroy();
	}
}
