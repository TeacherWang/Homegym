package com.runrunfast.homegym.course;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.bean.Course.ActionDetail;
import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.bean.Course.GroupDetail;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.bean.MyCourse.DayProgress;
import com.runrunfast.homegym.course.CourseServerMgr.IJoinCourseToServerListener;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.MyCourseDao;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

import java.io.Serializable;
import java.util.ArrayList;

public class CourseTrainActivity extends Activity implements OnClickListener{
	
	public static int REQ_CODE_ACTION_SET = 1;
	
	private UserInfo mUserInfo;
	
	private TextView tvTitle;
	private Button btnLeft, btnRight;
	private Button btnStartTrain, btnRejoinTrain;
	private RelativeLayout mActionLayout, mRestDayLayout;
	private TextView tvRestDay;
	
	private CourseTrainAdapter mCourseTrainAdapter;
	private ListView mCourseTrainListView;
	private ArrayList<Action> mActionList;
	
	private ArrayList<ActionTotalData> mActionTotalDataList;
	private CourseDetail mCourseDetail; // 该天的课程详细
	private ArrayList<ActionDetail> mActionDetailListOfThatDay;
	private MyCourse mMyCourse;
	
	private int mCurrentDayPosition; // 该天在日期分布的位置
	
	private String mCurrentDate;
	
	private ProgressDialog dialog;
	
	private IJoinCourseToServerListener mIJoinCourseToServerListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_course_train);
		
		initView();
		
		initData();
		
		initListener();
		
		initJoinCourseListener();
	}

	private void initListener() {
		mCourseTrainListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				jumpToTrainActionSetActivity(position);
			}
		});
	}
	
	private void initJoinCourseListener() {
		mIJoinCourseToServerListener = new IJoinCourseToServerListener() {
			
			@Override
			public void onJoinCourseToServerSuc() {
				dismissDialog();
				
				prepareToSaveMyCourse();
				
				showAction();
				
				showStartTrain();
			}
			
			@Override
			public void onJoinCourseToServerFail() {
				dismissDialog();
				
				Toast.makeText(CourseTrainActivity.this, "参加失败", Toast.LENGTH_SHORT).show();
			}
		};
		CourseServerMgr.getInstance().addJoinCourseToServerObserver(mIJoinCourseToServerListener);
	}
	
	private void showStartTrain() {
		mRestDayLayout.setVisibility(View.INVISIBLE);
		mActionLayout.setVisibility(View.VISIBLE);
	}

	private void prepareToSaveMyCourse() {
		MyCourseDao.getInstance().deleteMyCourseFromDb(Globle.gApplicationContext, mUserInfo.strAccountId, mMyCourse.course_id);
		
		recreateMyCourse();
		
		MyCourseDao.getInstance().updateProgressOfMyCourseToDb(Globle.gApplicationContext, mUserInfo.strAccountId, mMyCourse);
	}

	private void recreateMyCourse() {
		mMyCourse.progress = MyCourse.COURSE_PROGRESS_ING;
		mMyCourse.start_date = mCurrentDate;
		int dayNum = mMyCourse.day_progress.size();
		for(int i=0; i<dayNum; i++){
			DayProgress dayProgress = mMyCourse.day_progress.get(i);
			dayProgress.progress = MyCourse.DAY_PROGRESS_UNFINISH;
		}
	}

	private void jumpToTrainActionSetActivity(int position) {
		Action action = mActionList.get(position);
		
		ActionDetail actionDetail = mActionDetailListOfThatDay.get(position);
		
		Intent intent = new Intent(CourseTrainActivity.this, ActionSetActivity.class);
		intent.putExtra(Const.KEY_COURSE, mMyCourse);
		intent.putExtra(Const.KEY_ACTION_DETAIL, actionDetail);
		intent.putExtra(Const.KEY_DAY_POSITION, mCurrentDayPosition);
		intent.putExtra(Const.KEY_ACTION_POSITION, position);
		intent.putExtra(Const.KEY_ACTION, action);
		intent.putExtra(Const.KEY_ACTION_TOTAL_DATA, mActionTotalDataList.get(position));
		
		CourseTrainActivity.this.startActivityForResult(intent, REQ_CODE_ACTION_SET);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode != REQ_CODE_ACTION_SET){
			return;
		}
		if(resultCode != Activity.RESULT_OK){
			return;
		}
		
		mMyCourse = (MyCourse) data.getSerializableExtra(Const.KEY_COURSE);
		showAction();
	}

	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		mActionList = new ArrayList<Action>();
		mActionTotalDataList = new ArrayList<ActionTotalData>();
		
		mMyCourse = (MyCourse) getIntent().getSerializableExtra(Const.KEY_COURSE);
		tvTitle.setText(mMyCourse.course_name);
		
		// 判断是否是休息日
		if(mMyCourse.progress == MyCourse.COURSE_PROGRESS_REST){
			mActionLayout.setVisibility(View.GONE);
			mRestDayLayout.setVisibility(View.VISIBLE);
			btnRejoinTrain.setVisibility(View.GONE);
			tvRestDay.setText("今天是休息日！");
		}else if(mMyCourse.progress == MyCourse.COURSE_PROGRESS_EXPIRED){
			mActionLayout.setVisibility(View.GONE);
			mRestDayLayout.setVisibility(View.VISIBLE);
			btnRejoinTrain.setVisibility(View.VISIBLE);
			tvRestDay.setText("课程已过期！");
		}else{
			mActionLayout.setVisibility(View.VISIBLE);
			mRestDayLayout.setVisibility(View.GONE);
			showAction();
		}
	}
	
	private void showAction() {
		// 参加的课程的日期分布
		String currentDateStr = DateUtil.getCurrentDate();
		ArrayList<DayProgress> dayProgressList = (ArrayList<DayProgress>) mMyCourse.day_progress;
		int dayNum = dayProgressList.size();
		for(int i=0; i<dayNum; i++){
			DayProgress dayProgress = dayProgressList.get(i);
			String dateStr = DateUtil.getDateStrOfDayNumFromStartDate(dayProgress.day_num, mMyCourse.start_date);
			
			if(dateStr.equals(currentDateStr)){
				mCurrentDayPosition = i; // 当天在课程日期分布中的位置
				break;
			}
		}
		
		// 当天的动作集合
		mCourseDetail = mMyCourse.course_detail.get(mCurrentDayPosition);
		mActionDetailListOfThatDay = (ArrayList<ActionDetail>) mCourseDetail.action_detail;
		mActionList.clear();
		mActionTotalDataList.clear();
		int actionNum = mActionDetailListOfThatDay.size();
		for(int i=0; i<actionNum; i++){
			ActionDetail actionDetail = mActionDetailListOfThatDay.get(i);
			String actionId = actionDetail.action_id;
			Action action = ActionDao.getInstance().getActionFromDb(Globle.gApplicationContext, actionId);
			mActionList.add(action);
			
			ActionTotalData actionTotalData = getTotalKcalOfActionInMyCourse(actionDetail);
			mActionTotalDataList.add(actionTotalData);
		}
		
		mCourseTrainAdapter = new CourseTrainAdapter(this, mActionList, mActionTotalDataList);
		mCourseTrainListView.setAdapter(mCourseTrainAdapter);
	}
	
	/**
	  * @Method: getTotalKcalOfActionInMyCourse
	  * @Description: 获取该天该动作的总时间和总卡路里
	  * @param actionDetail
	  * @return	
	  * 返回类型：ActionTotalData 
	  */
	private ActionTotalData getTotalKcalOfActionInMyCourse(ActionDetail actionDetail){
		ActionTotalData actionTotalData = new ActionTotalData();
		int groupNum = actionDetail.group_num;
		for(int i=0; i<groupNum; i++){
			GroupDetail groupDetail = actionDetail.group_detail.get(i);
			actionTotalData.totalKcal = actionTotalData.totalKcal + groupDetail.kcal;
		}
		return actionTotalData;
	}

	public static class ActionTotalData implements Serializable{
		float totalKcal;
	}

	private void initView() {
		tvTitle = (TextView)findViewById(R.id.actionbar_title);
		btnLeft = (Button)findViewById(R.id.actionbar_left_btn);
		btnLeft.setBackgroundResource(R.drawable.nav_back);
		
		btnRight = (Button)findViewById(R.id.actionbar_right_btn);
		btnRight.setText(R.string.train_detail);
		btnRight.setOnClickListener(this);
		btnRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		btnRight.setTextColor(getResources().getColor(R.color.record_detail_text_color));
		
		mCourseTrainListView = (ListView)findViewById(R.id.course_train_list);
		
		btnStartTrain = (Button)findViewById(R.id.btn_start_train);
		btnStartTrain.setOnClickListener(this);
		
		btnRejoinTrain = (Button)findViewById(R.id.btn_rejoin);
		btnRejoinTrain.setOnClickListener(this);
		
		mActionLayout = (RelativeLayout)findViewById(R.id.course_train_action_layout);
		mRestDayLayout = (RelativeLayout)findViewById(R.id.course_train_rest_day_layout);
		tvRestDay = (TextView)findViewById(R.id.rest_day_text);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_left_btn:
			finish();
			break;
			
		case R.id.actionbar_right_btn:
			jumpToTrainDetailActivity();
			break;
			
		case R.id.btn_start_train:
			startTrain();
			break;
			
		case R.id.btn_rejoin:
			rejoinTrain();
			break;

		default:
			break;
		}
	}

	private void rejoinTrain() {
		mCurrentDate = DateUtil.getCurrentDate();
		CourseServerMgr.getInstance().joinCourseToServer(mUserInfo.strAccountId, mMyCourse.course_id, mCurrentDate);
		
		showDialog();
	}

	private void startTrain() {
//		CourseDetail courseDetail = mMyCourse.course_detail.get(0);
//		ArrayList<ActionDetail> actionDetailList = (ArrayList<ActionDetail>) courseDetail.action_detail;
//		ActionDetail actionDetail = actionDetailList.get(0);
//		Action action = ActionDao.getInstance().getActionFromDb(Globle.gApplicationContext, actionDetail.action_id);
//		String action_video_path = action.action_audio_local;
		if( CourseUtil.needDownload(mMyCourse) ){
			Toast.makeText(this, "请到详细计划界面下载课程", Toast.LENGTH_LONG).show();
			return;
		}
		
		Intent intent = new Intent(this, CourseVideoActivity.class);
		intent.putExtra(Const.KEY_COURSE, mMyCourse);
		intent.putExtra(Const.KEY_DAY_POSITION, mCurrentDayPosition);
		startActivity(intent);
	}

	private void jumpToTrainDetailActivity() {
		Intent intent = new Intent(this, DetailPlanActivity.class);
		
		intent.putExtra(Const.KEY_COURSE, mMyCourse);
		
		startActivity(intent);
	}
	
	private void showDialog(){
		dialog = new ProgressDialog(this);
		dialog.setMessage(getResources().getString(R.string.please_wait));
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
	
	private void dismissDialog(){
		if(dialog != null && dialog.isShowing()){
			dialog.dismiss();
		}
	}
	
	@Override
	protected void onDestroy() {
		
		CourseServerMgr.getInstance().removeJoinCourseToServerObserver(mIJoinCourseToServerListener);
		
		super.onDestroy();
	}
}
