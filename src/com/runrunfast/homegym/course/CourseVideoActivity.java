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
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;
import java.util.List;

public class CourseVideoActivity extends Activity implements OnClickListener{
	private final String TAG = "CourseVideoActivity";
	
	private Button btnFinished, btnFinishOnce;
	private TextView tvCurrentCount, tvTotalCount, tvGroupIndex, tvActionCount, tvTotalGroup;
	
	private UserInfo mUserInfo;
	private String mCourseId;
	private String mStrPlanDate;
	private String[] mActionIds;
	private ArrayList<ActionInfo> myActionInfoList;
	private int mTotalActionNum; // 总共的动作个数
	
	private CourseInfo mCourseInfo;
	
	private int mTotalTime = 0; // 总用时，所有动作之和
	private int mTotalCount = 0; // 总次数，所有动作之和
	private int mTotalKcal = 0; // 总kcal，所有动作之和
	
	private int mActionTime = 0; // 该动作用时
	private int mActionCount = 0; // 该动作总次数
	private int mActionKcal = 0; // 该动作总kcal。每完成一次动作，都要保存到mRecordDataUnit中。
	
	private int mActionCurrentGroupCount; // 该动作在当前组的次数
	private int mActionCurrentGroupToolWeight; // 该动作在该组的器械重量
	private int mActionCurrentGroupBurning; // 该动作在当前组的燃脂
	
	private ActionInfo mCurrentActionInfo; // 当前正在进行的动作
	private int mCurrentActionNum; // 当前正在进行的第几个动作
	private List<String> mCurrentActionTotalCountList; // 当前正在进行的动作的每组个数
	private RecordDataUnit mRecordDataUnit; // 当前的记录
	private int mActionGroupIndex; // 该组动作已经开始的组数。比如第一个动作第二组开始，那么为二
	private int mActionGroupTotalCount; // 该动作在该组的总次数
	
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
		if( mActionCurrentGroupCount < mActionGroupTotalCount ){
			mActionCurrentGroupCount++; // 当前组的次数
			mActionCurrentGroupToolWeight = Integer.parseInt( mCurrentActionInfo.defaultToolWeightList.get(mActionGroupIndex - 1) );
			mActionCurrentGroupBurning = mActionCurrentGroupBurning + 10;
			
			mActionKcal = mActionKcal + 10; // 该动作消耗的kcal
			mActionTime = mActionTime + 10; // 该动作总耗时
			mActionCount++; // 该动作总次数
			
			mTotalCount++; // 总次数
			mTotalKcal = mTotalKcal + 10; // 总消耗kcal
			mTotalTime = mTotalTime + 10; // 总耗时
			
			mRecordDataUnit.iConsumeTime = mTotalTime;
			mRecordDataUnit.iCount = mTotalCount;
			mRecordDataUnit.iGroupCount = mActionGroupIndex;
			mRecordDataUnit.iTotalKcal = mActionKcal;
			updateUi();
		}
		// 次数+1后，该组还未结束
		if(mActionCurrentGroupCount < mActionGroupTotalCount){
			
		}else if(mActionCurrentGroupCount == mActionGroupTotalCount){ // 次数+1后，该组结束
			Toast.makeText(CourseVideoActivity.this, "休息一下", Toast.LENGTH_SHORT).show();
			
			mRecordDataUnit.finishCountList.add(String.valueOf(mActionCurrentGroupCount));
			mRecordDataUnit.finishToolWeightList.add(String.valueOf(mActionCurrentGroupToolWeight));
			mRecordDataUnit.finishBurningList.add(String.valueOf(mActionCurrentGroupBurning));
			// 该动作还有下一组
			if(mActionGroupIndex < mCurrentActionInfo.defaultGroupNum){
				mActionGroupIndex++;
				mActionGroupTotalCount = Integer.parseInt( mCurrentActionTotalCountList.get(mActionGroupIndex - 1) );
				mActionCurrentGroupCount = 0;
				mActionCurrentGroupBurning = 0;
//				mActionCurrentGroupToolWeight = 0;
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
					mRecordDataUnit.strDate = mStrPlanDate;
					mActionGroupIndex = 1;
					mActionKcal = 0;
					mActionCount = 0;
					mActionTime = 0;
					mCurrentActionTotalCountList = mCurrentActionInfo.defaultCountList;
					mActionGroupTotalCount = Integer.parseInt( mCurrentActionTotalCountList.get(mActionGroupIndex - 1) );
					mActionCurrentGroupCount = 0;
					mActionCurrentGroupBurning = 0;
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
		mStrPlanDate = getIntent().getStringExtra(Const.KEY_DATE);
		
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
		mActionKcal = 0;
		mCurrentActionTotalCountList = mCurrentActionInfo.defaultCountList;
		mActionGroupTotalCount = Integer.parseInt( mCurrentActionTotalCountList.get(mActionGroupIndex - 1) );
		mActionCurrentGroupCount = 0;
		mTotalKcal = 0;
		
		mRecordDataUnit = new RecordDataUnit();
		mRecordDataUnit.strCoursId = mCourseId;
		mRecordDataUnit.strCourseName = mCourseInfo.courseName;
		mRecordDataUnit.actionId = mCurrentActionInfo.strActionId;
		mRecordDataUnit.actionName = mCurrentActionInfo.actionName;
		mRecordDataUnit.strDate = mStrPlanDate;
		
		updateUi();
	}

	private void updateUi() {
		tvTotalCount.setText(String.valueOf(mActionGroupTotalCount));
		tvCurrentCount.setText(String.valueOf(mActionCurrentGroupCount));
		tvGroupIndex.setText("第" + mActionGroupIndex + "组");
		tvActionCount.setText("动作" + mCurrentActionNum);
		tvTotalGroup.setText("共" + mCurrentActionInfo.defaultGroupNum + "组");
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
		tvTotalGroup = (TextView)findViewById(R.id.current_action_total_group_text);
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
	  * @Method: handleCourseFinished
	  * @Description: 完成训练	
	  * 返回类型：void 
	  */
	private void handleCourseFinished() {
		if(mActionCurrentGroupCount != 0){
			mRecordDataUnit.finishCountList.add(String.valueOf(mActionCurrentGroupCount));
			mRecordDataUnit.finishToolWeightList.add(String.valueOf(mActionCurrentGroupToolWeight));
			mRecordDataUnit.finishBurningList.add(String.valueOf(mActionCurrentGroupBurning));
			
			mRecordDataUnitList.add(mRecordDataUnit);
		}
		int recordSize = mRecordDataUnitList.size();
		for(int i=0; i<recordSize; i++){
			RecordDataUnit recordDataUnit = mRecordDataUnitList.get(i);
			MyFinishDao.getInstance().saveFinishInfoToDb(Globle.gApplicationContext, mUserInfo.strAccountId, recordDataUnit, DateUtil.getCurrentDate());
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
