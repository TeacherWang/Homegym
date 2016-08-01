package com.runrunfast.homegym.course;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.BtDevice.BtDeviceMgr;
import com.runrunfast.homegym.BtDevice.BtDeviceMgr.BLEServiceListener;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.CourseDao;
import com.runrunfast.homegym.dao.MyCourseActionDao;
import com.runrunfast.homegym.dao.MyFinishDao;
import com.runrunfast.homegym.home.FinishActivity;
import com.runrunfast.homegym.record.RecordDataUnit;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;
import java.util.List;

public class CourseVideoActivity extends Activity implements OnClickListener{
	private final String TAG = "CourseVideoActivity";
	
	private Button btnFinished, btnFinishOnce;
	private TextView tvCurrentCount, tvTotalCount, tvGroupIndex, tvActionCount;
	
	private UserInfo mUserInfo;
	private String mCourseId;
	private String mStrDate;
	private String[] mActionIds;
	private ArrayList<ActionInfo> myActionInfoList;
	private int mTotalActionNum; // 总共的动作个数
	
	private CourseInfo mCourseInfo;
	
	private int mTotalTime = 0; // 要根据实际的运动来算
	private int mTotalCount = 0; // 实际运动次数
	private int mTotalKcal = 0; // 实际运动的kcal
	
	private ActionInfo mCurrentActionInfo; // 当前正在进行的动作
	private int mCurrentActionNum; // 当前正在进行的第几个动作
	private List<String> mCurrentCountList; // 当前正在进行的动作的每组个数
	private RecordDataUnit mRecordDataUnit; // 当前的记录
	private int mActionGroupIndex; // 该组动作已经开始的组数。比如第一个动作第二组开始，那么为二
	private int mActionGroupCount; // 该动作在该组的个数
	
	private int mActionCurrentGroupCount; // 该动作在当前组的次数
	private int mActionCurrentKcal; // 该动作已经消耗的kcal。每完成一次动作，都要保存到mRecordDataUnit中。
	private int mActionTime; // 该动作的时间
	private ArrayList<RecordDataUnit> mRecordDataUnitList;
	
	private BLEServiceListener mBleServiceListener;
	
	private boolean isFinished = false;
	private ArrayList<String> mFinishedActionIds;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_course_video);
		
		initView();
		
		initData();
		
		initListener();
	}

	private void initListener() {
		mBleServiceListener = new BLEServiceListener() {
			
			@Override
			public void onReedSwitch() {
				handleFinishOnce();
			}
			
			@Override
			public void onGetDevice(BluetoothDevice btDevice) {}
			
			@Override
			public void onDeviceDisconnected() {}
			
			@Override
			public void onDeviceConnected() {}
			
			@Override
			public void onBLEInit() {}
		};
		
		BtDeviceMgr.getInstance().addBLEServiceObserver(mBleServiceListener);
	}

	private void handleFinishOnce() {
		if(mActionGroupIndex > mCurrentActionInfo.defaultGroupNum){
			Log.e(TAG, "handleFinishOnce, mActionGroupIndex > mCurrentActionInfo.iGroupNum" + 
					"mActionGroupIndex = " + mActionGroupIndex + ", mCurrentActionInfo.iGroupNum = " + mCurrentActionInfo.iGroupNum);
			return;
		}
		
		// 当前次数小于该组总次数
		if( mActionCurrentGroupCount < mActionGroupCount ){
			mActionCurrentGroupCount++;
			mActionCurrentKcal = mActionCurrentKcal + 10;
			mTotalCount++;
			mTotalKcal = mTotalKcal + 10;
			mTotalTime = mTotalTime + 10;
			
			mRecordDataUnit.iGroupCount = mActionGroupIndex;
			mRecordDataUnit.iTotalKcal = mActionCurrentKcal;
			updateUi();
		}
		// 次数+1后，该组还未结束
		if(mActionCurrentGroupCount < mActionGroupCount){
			
		}else if(mActionCurrentGroupCount == mActionGroupCount){ // 次数+1后，该组结束
			Toast.makeText(CourseVideoActivity.this, "休息一下", Toast.LENGTH_SHORT).show();
			
			// 该动作还有下一组
			if(mActionGroupIndex < mCurrentActionInfo.defaultGroupNum){
				mActionGroupIndex++;
				mActionGroupCount = Integer.parseInt( mCurrentCountList.get(mActionGroupIndex - 1) );
				mActionCurrentGroupCount = 0;
//				updateUi();
			}else{ // 当前为最后一组的最后一次
				Toast.makeText(CourseVideoActivity.this, "该动作结束", Toast.LENGTH_SHORT).show();
				// 保存到列表
				mRecordDataUnitList.add(mRecordDataUnit);
				mFinishedActionIds.add(mActionIds[mCurrentActionNum - 1]);
				// 还有下个动作
				mCurrentActionNum++;
				if(mCurrentActionNum <= mTotalActionNum){
					mCurrentActionInfo = myActionInfoList.get(mCurrentActionNum - 1);
					mRecordDataUnit = new RecordDataUnit();
					mRecordDataUnit.strCoursId = mCourseId;
					mRecordDataUnit.strCourseName = mCourseInfo.courseName;
					mRecordDataUnit.actionId = mCurrentActionInfo.strActionId;
					mRecordDataUnit.actionName = mCurrentActionInfo.actionName;
					mRecordDataUnit.strDate = mStrDate;
					mActionGroupIndex = 1;
					mActionCurrentKcal = 0;
					mCurrentCountList = mCurrentActionInfo.defaultCountList;
					mActionGroupCount = Integer.parseInt( mCurrentCountList.get(mActionGroupIndex - 1) );
					mActionCurrentGroupCount = 0;
//					updateUi();
				}else{// 最后一个动作了
					Toast.makeText(CourseVideoActivity.this, "所有动作做完，课程结束", Toast.LENGTH_SHORT).show();
					// 保存数据到数据库
					isFinished = true;
					handleCourseFinished();
				}
			}
		}
		
	}

	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		mCourseId = getIntent().getStringExtra(Const.KEY_COURSE_ID);
		mActionIds = getIntent().getStringArrayExtra(Const.KEY_ACTION_IDS);
		mStrDate = getIntent().getStringExtra(Const.KEY_DATE);
		
		mCourseInfo = CourseDao.getInstance().getCourseInfoFromDb(Globle.gApplicationContext, mCourseId);
		
		myActionInfoList = new ArrayList<ActionInfo>();
		mFinishedActionIds = new ArrayList<String>();;
		
		// 获取动作列表
		mTotalActionNum = mActionIds.length;
		for(int i=0; i<mTotalActionNum; i++){
			ActionInfo myActionInfo = MyCourseActionDao.getInstance().getMyCourseActionInfo
					(Globle.gApplicationContext, mUserInfo.strAccountId, mCourseId, mActionIds[i]);
			ActionInfo actionInfo = ActionDao.getInstance().getActionInfoFromDb(Globle.gApplicationContext, mActionIds[i]);
			myActionInfo.actionName = actionInfo.actionName;
			
			myActionInfoList.add(myActionInfo);
		}
		
		// 初始化记录完成的数据。每完成一次动作，就会有个RecordDataUnit来更新数据，当全部组数做完或者要中途退出时，把当前数据加到list中，然后保存到数据库并上传后台
		mRecordDataUnitList = new ArrayList<RecordDataUnit>();
		
		
		mCurrentActionNum = 1; // 第一个动作
		mCurrentActionInfo = myActionInfoList.get(mCurrentActionNum - 1);
		mActionGroupIndex = 1;
		mActionCurrentKcal = 0;
		mCurrentCountList = mCurrentActionInfo.defaultCountList;
		mActionGroupCount = Integer.parseInt( mCurrentCountList.get(mActionGroupIndex - 1) );
		mActionCurrentGroupCount = 0;
		mTotalKcal = 0;
		
		mRecordDataUnit = new RecordDataUnit();
		mRecordDataUnit.strCoursId = mCourseId;
		mRecordDataUnit.strCourseName = mCourseInfo.courseName;
		mRecordDataUnit.actionId = mCurrentActionInfo.strActionId;
		mRecordDataUnit.actionName = mCurrentActionInfo.actionName;
		mRecordDataUnit.strDate = mStrDate;
		
		updateUi();
	}

	private void updateUi() {
		tvTotalCount.setText(String.valueOf(mActionGroupCount));
		tvCurrentCount.setText(String.valueOf(mActionCurrentGroupCount));
		tvGroupIndex.setText("第" + mActionGroupIndex + "组");
		tvActionCount.setText("动作" + mCurrentActionNum);
	}

	private void initView() {
		btnFinished = (Button)findViewById(R.id.btn_finished);
		btnFinishOnce = (Button)findViewById(R.id.btn_finish_once);
		
		btnFinished.setOnClickListener(this);
		btnFinishOnce.setOnClickListener(this);
		
		tvCurrentCount = (TextView)findViewById(R.id.current_count_text);
		tvTotalCount = (TextView)findViewById(R.id.current_total_count_text);
		tvGroupIndex = (TextView)findViewById(R.id.current_gropu_count_text);
		tvActionCount = (TextView)findViewById(R.id.current_action_count_text);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_finished:
			handleCourseFinished();
			break;
			
		case R.id.btn_finish_once:
			// 测试
			mBleServiceListener.onReedSwitch();
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
		mTotalTime = 150;
		mTotalCount = 90;
		mTotalKcal = 118;
		
		Intent intent = new Intent(this, FinishActivity.class);
		intent.putExtra(FinishActivity.KEY_FINISH_OR_UNFINISH, FinishActivity.TYPE_UNFINISH);
		intent.putExtra(Const.KEY_COURSE_TOTAL_TIME, mTotalTime);
		intent.putExtra(Const.KEY_COURSE_TOTAL_COUNT, mTotalCount);
		intent.putExtra(Const.KEY_COURSE_TOTAL_BURNING, mTotalKcal);
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
		if(mActionCurrentGroupCount != 0){
			mRecordDataUnitList.add(mRecordDataUnit);
		}
		int recordSize = mRecordDataUnitList.size();
		for(int i=0; i<recordSize; i++){
			RecordDataUnit recordDataUnit = mRecordDataUnitList.get(i);
			MyFinishDao.getInstance().saveFinishInfoToDb(Globle.gApplicationContext, mUserInfo.strAccountId, recordDataUnit);
		}
		
		Intent intent = new Intent(this, FinishActivity.class);
		if(isFinished){
			intent.putExtra(FinishActivity.KEY_FINISH_OR_UNFINISH, FinishActivity.TYPE_FINISH);
			intent.putExtra(Const.KEY_ACTION_IDS, mActionIds);
		}else{
			intent.putExtra(FinishActivity.KEY_FINISH_OR_UNFINISH, FinishActivity.TYPE_UNFINISH);
			intent.putStringArrayListExtra(Const.KEY_ACTION_IDS, mFinishedActionIds);
		}
		
		intent.putExtra(Const.KEY_COURSE_TOTAL_TIME, mTotalTime);
		intent.putExtra(Const.KEY_COURSE_TOTAL_COUNT, mTotalCount);
		intent.putExtra(Const.KEY_COURSE_TOTAL_BURNING, mTotalKcal);
		intent.putExtra(Const.KEY_COURSE_ID, mCourseId);
		
		startActivity(intent);
		finish();
	}
	
}
