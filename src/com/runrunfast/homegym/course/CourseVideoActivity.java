package com.runrunfast.homegym.course;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.MyCourseActionDao;
import com.runrunfast.homegym.home.FinishActivity;
import com.runrunfast.homegym.record.RecordDataUnit;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;

public class CourseVideoActivity extends Activity implements OnClickListener{

	private Button btnFinished, btnUnfinished;
	
	private UserInfo mUserInfo;
	private String mCourseId;
	private String[] mActionIds;
	private ArrayList<ActionInfo> myActionInfoList;
	
	private int totalTime = 0; // 要根据实际的运动来算
	private int totalCount = 0; // 实际运动次数
	private int totalKcal = 0; // 实际运动的kcal
	
	private ArrayList<RecordDataUnit> recordDataUnitList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_video);
		
		initView();
		
		initData();
	}

	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		mCourseId = getIntent().getStringExtra(Const.KEY_COURSE_ID);
		mActionIds = getIntent().getStringArrayExtra(Const.KEY_ACTION_IDS);
		
		myActionInfoList = new ArrayList<ActionInfo>();
		
		// 获取动作列表
		int actionSize = mActionIds.length;
		for(int i=0; i<actionSize; i++){
			ActionInfo myActionInfo = MyCourseActionDao.getInstance().getMyCourseActionInfo
					(Globle.gApplicationContext, mUserInfo.strAccountId, mCourseId, mActionIds[i]);
			ActionInfo actionInfo = ActionDao.getInstance().getActionInfoFromDb(Globle.gApplicationContext, mActionIds[i]);
			myActionInfo.actionName = actionInfo.actionName;
			
			myActionInfoList.add(myActionInfo);
		}
		
		// 初始化记录完成的数据。每完成一次动作，就会有个RecordDataUnit来更新数据，当全部组数做完或者要中途退出时，把当前数据加到list中，然后保存到数据库并上传后台
		recordDataUnitList = new ArrayList<RecordDataUnit>();
	}

	private void initView() {
		btnFinished = (Button)findViewById(R.id.btn_finished);
		btnUnfinished = (Button)findViewById(R.id.btn_unfinished);
		
		btnFinished.setOnClickListener(this);
		btnUnfinished.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_finished:
			handleCourseFinished();
			break;
			
		case R.id.btn_unfinished:
			handleCourseUnfinished();
			break;

		default:
			break;
		}
	}

	/**
	  * @Method: handleCourseUnfinished
	  * @Description: 未完成训练
	  * 返回类型：void 
	  */
	private void handleCourseUnfinished() {
		// 测试
		totalTime = 150;
		totalCount = 90;
		totalKcal = 118;
		
		Intent intent = new Intent(this, FinishActivity.class);
		intent.putExtra(FinishActivity.KEY_FINISH_OR_UNFINISH, FinishActivity.TYPE_UNFINISH);
		intent.putExtra(Const.KEY_COURSE_TOTAL_TIME, totalTime);
		intent.putExtra(Const.KEY_COURSE_TOTAL_COUNT, totalCount);
		intent.putExtra(Const.KEY_COURSE_TOTAL_BURNING, totalKcal);
		intent.putExtra(Const.KEY_COURSE_ID, mCourseId);
		intent.putExtra(Const.KEY_ACTION_IDS, mActionIds);
		startActivity(intent);
		finish();
	}

	/**
	  * @Method: handleCourseFinished
	  * @Description: 完成训练	
	  * 返回类型：void 
	  */
	private void handleCourseFinished() {
		// 测试
		totalTime = 200;
		totalCount = 110;
		totalKcal = 188;
		
		// 
		
		Intent intent = new Intent(this, FinishActivity.class);
		intent.putExtra(FinishActivity.KEY_FINISH_OR_UNFINISH, FinishActivity.TYPE_FINISH);
		intent.putExtra(Const.KEY_COURSE_TOTAL_TIME, totalTime);
		intent.putExtra(Const.KEY_COURSE_TOTAL_COUNT, totalCount);
		intent.putExtra(Const.KEY_COURSE_TOTAL_BURNING, totalKcal);
		intent.putExtra(Const.KEY_COURSE_ID, mCourseId);
		intent.putExtra(Const.KEY_ACTION_IDS, mActionIds);
		startActivity(intent);
		finish();
	}
	
}
