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
import android.widget.Toast;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.bean.Course.ActionDetail;
import com.runrunfast.homegym.bean.Course.ActionId;
import com.runrunfast.homegym.bean.Course.CourseDateDistribution;
import com.runrunfast.homegym.bean.Course.GroupDetail;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.MyCourseActionDao;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;
import java.util.List;

public class CourseTrainActivity extends Activity implements OnClickListener{
	
	private TextView tvTitle;
	private Button btnLeft, btnRight;
	private Button btnStartTrain;
	
	private CourseTrainAdapter mCourseTrainAdapter;
	private ListView mCourseTrainListView;
	private ArrayList<Action> mActionList;
	
	private String mCourseId;
	private List<ActionId> mActionIdList;
	private ArrayList<ActionTotalData> mActionTotalDataList;
	private MyCourse mMyCourse;
	private UserInfo mUserInfo;
	
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
		Action actionInfo = mActionList.get(position);
		String actionId = actionInfo.strActionId;
		String actionName = actionInfo.actionName;
		int actionNum = position + 1;
		String actionDecript = actionInfo.strTrainDescript;
		
		Intent intent = new Intent(CourseTrainActivity.this, ActionSetActivity.class);
		intent.putExtra(Const.KEY_COURSE_ID, mCourseId);
		intent.putExtra(Const.KEY_ACTION_ID, actionId);
		intent.putExtra(Const.KEY_ACTION_NAME, actionName);
		intent.putExtra(Const.KEY_ACTION_DESCRIPT, actionDecript);
		intent.putExtra(Const.KEY_ACTION_NUM, actionNum);
		CourseTrainActivity.this.startActivity(intent);
	}

	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		mActionList = new ArrayList<Action>();
		mActionTotalDataList = new ArrayList<ActionTotalData>();
		
		mMyCourse = (MyCourse) getIntent().getSerializableExtra(Const.KEY_COURSE_INFO);
		
		mCourseId = mMyCourse.course_id;
		tvTitle.setText(mMyCourse.course_name);
		
		mActionIdList = mMyCourse.action_ids;
		int actionIdSize = mActionIdList.size();
		for(int i=0; i<actionIdSize; i++){
			String actionId = mActionIdList.get(i).action_id;
			Action action = ActionDao.getInstance().getActionFromDb(Globle.gApplicationContext, actionId);
			mActionList.add(action);
			
			// 计算该动作在该课程中的总时间和总kcal
			ActionTotalData actionTotalData = getTotalTimeOfActionInMyCourse(actionId);
			mActionTotalDataList.add(actionTotalData);
		}
		
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
		
		mCourseTrainAdapter = new CourseTrainAdapter(this, mActionList, mActionTotalDataList);
		mCourseTrainListView.setAdapter(mCourseTrainAdapter);
	}
	
	private ActionTotalData getTotalTimeOfActionInMyCourse(String actionId){
		ActionTotalData actionTotalData = new ActionTotalData();
		int time = 0;
		int courseDetailNum = mMyCourse.course_detail.size();
		for(int i=0; i<courseDetailNum; i++){
			CourseDateDistribution courseDetail = mMyCourse.course_detail.get(i);
			int actionDetailNum = courseDetail.action_detail.size();
			for(int j=0; j<actionDetailNum; j++){
				ActionDetail actionDetail = courseDetail.action_detail.get(j);
				if( !actionDetail.action_id.equals(actionId) ){
					continue;
				}
				
				int groupNum = actionDetail.group_detail.size();
				for(int k=0; k<groupNum; k++){
					GroupDetail groupDetail = actionDetail.group_detail.get(k);
					actionTotalData.totalTime = actionTotalData.totalTime + groupDetail.time;
					actionTotalData.totalKcal = actionTotalData.totalKcal + groupDetail.kcal;
				}
			}
		}
		
		return actionTotalData;
	}

	public static class ActionTotalData{
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
		ArrayList<String> courseDateList = CourseUtil.getCourseDateList(mMyCourseInfo.startDate, mMyCourseInfo.dateNumList);
		if( !courseDateList.contains(DateUtil.getCurrentDate()) ){
			Toast.makeText(CourseTrainActivity.this, R.string.today_is_rest_day, Toast.LENGTH_SHORT).show();
			return;
		}
		// 今天有训练，需要知道今天训练的动作列表
		int dayIndex = courseDateList.indexOf(DateUtil.getCurrentDate());
		String[] actionIds = mMyCourseInfo.dateActionIdList.get(dayIndex).split(";");
		String strDate = DateUtil.getCurrentDate();
		
		Intent intent = new Intent(this, CourseVideoActivity.class);
		intent.putExtra(Const.KEY_ACTION_IDS, actionIds);
		intent.putExtra(Const.KEY_COURSE_ID, mMyCourseInfo.courseId);
		intent.putExtra(Const.KEY_DATE, strDate);
		startActivity(intent);
	}


	private void jumpToTrainDetailActivity() {
		Intent intent = new Intent(this, DetailPlanActivity.class);
		
		intent.putExtra(Const.KEY_COURSE_INFO, mMyCourseInfo);
		
		
		startActivity(intent);
	}
}
