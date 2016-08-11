package com.runrunfast.homegym.course;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.bean.Course.ActionDetail;
import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.bean.Course.GroupDetail;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.bean.MyCourse.DayProgress;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

import java.io.Serializable;
import java.util.ArrayList;

public class CourseTrainActivity extends Activity implements OnClickListener{
	
	public static int REQ_CODE_ACTION_SET = 1;
	
	private TextView tvTitle;
	private Button btnLeft, btnRight;
	private Button btnStartTrain;
	
	private CourseTrainAdapter mCourseTrainAdapter;
	private ListView mCourseTrainListView;
	private ArrayList<Action> mActionList;
	
	private ArrayList<ActionTotalData> mActionTotalDataList;
	private CourseDetail mCourseDetail; // 该天的课程详细
	private ArrayList<ActionDetail> mActionDetailListOfThatDay;
	private MyCourse mMyCourse;
	
	private int mCurrentDayPosition; // 该天在日期分布的位置
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_course_train);
		
		initView();
		
		initData();
		
		initListener();
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
		mActionList = new ArrayList<Action>();
		mActionTotalDataList = new ArrayList<ActionTotalData>();
		
		mMyCourse = (MyCourse) getIntent().getSerializableExtra(Const.KEY_COURSE);
		tvTitle.setText(mMyCourse.course_name);
		
		showAction();
		
//		CourseTrainInfo courseTrainInfo1 = new CourseTrainInfo();
//		courseTrainInfo1.iCourseId = 1;
//		courseTrainInfo1.strCrouseName = "21天增肌训练";
//		courseTrainInfo1.iTrainId = 1;
//		courseTrainInfo1.strTrainName = "平板卧推举";
//		courseTrainInfo1.strActionNum = "动作一";
//		courseTrainInfo1.strTrainPosition = "背部 胸部";
//		courseTrainInfo1.strTrainDescript = "坚持训练将锻炼到胸大肌和三角肌";
//		courseTrainInfo1.iTime = 15;
//		courseTrainInfo1.iKcal = 187;
//		courseTrainInfo1.iDiffcultLevel = 1;
//		mCourseActionInfoList.add(courseTrainInfo1);
	}
	
	private void showAction() {
		// 参加的课程的日期分布
		String currentDateStr = DateUtil.getCurrentDate();
		ArrayList<DayProgress> dayProgressList = (ArrayList<DayProgress>) mMyCourse.day_progress;
		int dayNum = dayProgressList.size();
		for(int i=0; i<dayNum; i++){
			DayProgress dayProgress = dayProgressList.get(i);
			String dateStr = dayProgress.plan_date;
			
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
			
			ActionTotalData actionTotalData = getTotalTimeOfActionInMyCourse(actionDetail);
			mActionTotalDataList.add(actionTotalData);
		}
		
		mCourseTrainAdapter = new CourseTrainAdapter(this, mActionList, mActionTotalDataList);
		mCourseTrainListView.setAdapter(mCourseTrainAdapter);
	}
	
	/**
	  * @Method: getTotalTimeOfActionInMyCourse
	  * @Description: 获取该天该动作的总时间和总卡路里
	  * @param actionDetail
	  * @return	
	  * 返回类型：ActionTotalData 
	  */
	private ActionTotalData getTotalTimeOfActionInMyCourse(ActionDetail actionDetail){
		ActionTotalData actionTotalData = new ActionTotalData();
		int groupNum = actionDetail.group_num;
		for(int i=0; i<groupNum; i++){
			GroupDetail groupDetail = actionDetail.group_detail.get(i);
			actionTotalData.totalTime = actionTotalData.totalTime + groupDetail.time;
			actionTotalData.totalKcal = actionTotalData.totalKcal + groupDetail.kcal;
		}
		return actionTotalData;
	}

	public static class ActionTotalData implements Serializable{
		int totalTime;
		int totalKcal;
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

		default:
			break;
		}
	}

	private void startTrain() {
		Intent intent = new Intent(this, CourseVideoActivity.class);
		intent.putExtra(Const.KEY_COURSE, mMyCourse);
		intent.putExtra(Const.KEY_DAY_POSITION, mCurrentDayPosition);
//		intent.putExtra(Const.KEY_COURSE_DETAIL, mCourseDetail);
//		intent.putExtra(Const.KEY_COURSE_ID, mMyCourse.course_id);
//		intent.putExtra(Const.KEY_COURSE_NAME, mMyCourse.course_name);
		startActivity(intent);
	}


	private void jumpToTrainDetailActivity() {
		Intent intent = new Intent(this, DetailPlanActivity.class);
		
		intent.putExtra(Const.KEY_COURSE, mMyCourse);
		
		startActivity(intent);
	}
}
