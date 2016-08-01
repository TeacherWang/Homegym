package com.runrunfast.homegym.course;

import java.util.ArrayList;
import java.util.List;

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
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.MyCourseActionDao;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

public class CourseTrainActivity extends Activity implements OnClickListener{
	
	private TextView tvTitle;
	private Button btnLeft, btnRight;
	private Button btnStartTrain;
	
	private CourseTrainAdapter mCourseTrainAdapter;
	private ListView mCourseTrainListView;
	private ArrayList<ActionInfo> mCourseActionInfoList;
	
	private String mCourseId;
	private String mCourseName;
	private List<String> mActionIdList;
	private CourseInfo mMyCourseInfo;
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
		ActionInfo actionInfo = mCourseActionInfoList.get(position);
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
		
		mCourseActionInfoList = new ArrayList<ActionInfo>();
		
		mMyCourseInfo = (CourseInfo) getIntent().getSerializableExtra(Const.KEY_COURSE_INFO);
		
		mCourseId = mMyCourseInfo.courseId;
		tvTitle.setText(mMyCourseInfo.courseName);
		
		mActionIdList = mMyCourseInfo.actionIds;
		int actionIdSize = mActionIdList.size();
		for(int i=0; i<actionIdSize; i++){
			ActionInfo actionInfo = MyCourseActionDao.getInstance().getMyCourseActionInfo(Globle.gApplicationContext, mUserInfo.strAccountId, mCourseId, mActionIdList.get(i).trim());
			ActionInfo baseActionInfo = ActionDao.getInstance().getActionInfoFromDb(Globle.gApplicationContext, mActionIdList.get(i).trim());
			actionInfo.actionName = baseActionInfo.actionName;
			actionInfo.strTrainPosition = baseActionInfo.strTrainPosition;
			actionInfo.strTrainDescript = baseActionInfo.strTrainDescript;
			actionInfo.iDiffcultLevel = baseActionInfo.iDiffcultLevel;
			mCourseActionInfoList.add(actionInfo);
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
		
		mCourseTrainAdapter = new CourseTrainAdapter(this, mCourseActionInfoList);
		mCourseTrainListView.setAdapter(mCourseTrainAdapter);
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
