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
import com.runrunfast.homegym.bean.Course.ActionDetail;
import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.bean.Course.GroupDetail;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.bean.MyCourse.DayProgress;
import com.runrunfast.homegym.dao.MyCourseDao;
import com.runrunfast.homegym.dao.MyFinishDao;
import com.runrunfast.homegym.home.FinishActivity;
import com.runrunfast.homegym.record.Record;
import com.runrunfast.homegym.utils.CalculateUtil;
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
	private MyCourse mMyCourse;
	private int mDayPosition;
	private DayProgress mDayProgress;
	private ArrayList<ActionDetail> mActionDetailList;
	
	private String mStrPlanDate;
	private int mTotalActionNum; // 总共的动作个数
	
	private int mActionCurrentGroupCount; // 该动作在当前组的次数
	
	private ActionDetail mTargetActionDetail; // 当前正在进行的目标动作
	private ActionDetail mFinishedActionDetail; // 当前已经完成的动作
	private int mCurrentActionPosition; // 当前正在进行的动作位置，从0开始
	private GroupDetail mTargetGroupDetail; // 当前动作该组的目标数据
	private GroupDetail mFinishedGroupDetail; // 当前动作该组的已经完成的数据
	private List<GroupDetail> mTargetActionGroupDetailList; // 当前正在进行的目标动作的每组数据集合
	private List<GroupDetail> mFinishedActionGroupDetailList; // 当前已经完成的指定动作的每组数据集合
	private Record mCurrentRecord; // 当前的记录
	private int mActionGroupIndex; // 该组动作已经开始的组数。比如第一个动作第一组开始，那么为0，第二组开始，那么为二
	private int mActionCurrentGroupTotalCount; // 该动作在该组的总次数
	
	private List<ActionDetail> mFinishedActionDetailList;
//	private ArrayList<Record> mRecordList;
	
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
		if(mActionGroupIndex > mTargetActionDetail.group_num){
			Log.e(TAG, "handleFinishOnce, mActionGroupIndex > mCurrentActionDetail.group_num" + 
					"mActionGroupIndex = " + mActionGroupIndex + ", mCurrentActionDetail.group_num = " + mTargetActionDetail.group_num);
			return;
		}
		// 当前次数小于该组总次数
		if( mActionCurrentGroupCount < mActionCurrentGroupTotalCount ){
			mActionCurrentGroupCount++; // 当前组的次数
			
			int kcal = CalculateUtil.calculateSingleKcal(mTargetGroupDetail.weight); // 该动作消耗的kcal
			
			mFinishedGroupDetail.kcal = mFinishedGroupDetail.kcal + kcal; // 该动作消耗的kcal
			mFinishedGroupDetail.count = mFinishedGroupDetail.count + 1;
			mFinishedGroupDetail.weight = mTargetGroupDetail.weight;
			
			mCurrentRecord.finish_count = mCurrentRecord.finish_count + 1; // 该次训练的总次数
			mCurrentRecord.finish_kcal = mCurrentRecord.finish_kcal + kcal; // 该次训练的总kcal
			mCurrentRecord.finish_time = mCurrentRecord.finish_time + CalculateUtil.calculateSingleTime(); // 该次训练的总耗时
			
			updateUi();
		}
		// 次数+1后，该组还未结束
		if(mActionCurrentGroupCount < mActionCurrentGroupTotalCount){
			
		}else if(mActionCurrentGroupCount == mActionCurrentGroupTotalCount){ // 次数+1后，该组结束
			Toast.makeText(CourseVideoActivity.this, "休息一下", Toast.LENGTH_SHORT).show();
			
			mFinishedActionDetail.group_num = mActionGroupIndex + 1;
			mFinishedActionGroupDetailList.add(mFinishedGroupDetail);
			
			// 该动作还有下一组
			mActionGroupIndex++;
			if(mActionGroupIndex < mTargetActionDetail.group_num ){
				mFinishedGroupDetail = new GroupDetail();
				mTargetGroupDetail = mTargetActionGroupDetailList.get(mActionGroupIndex);
				mActionCurrentGroupTotalCount = mTargetGroupDetail.count;
				mActionCurrentGroupCount = 0;
			}else{ // 当前为最后一组的最后一次
				Toast.makeText(CourseVideoActivity.this, "该动作结束", Toast.LENGTH_SHORT).show();
				// 保存到列表
				mFinishedActionDetailList.add(mFinishedActionDetail);
				mFinishedActionIds.add(mFinishedActionDetail.action_id);
				// 还有下个动作
				mCurrentActionPosition++;
				if( mCurrentActionPosition < mTotalActionNum ){
					mTargetActionDetail = mActionDetailList.get(mCurrentActionPosition);
					
					mFinishedGroupDetail = new GroupDetail();
					mFinishedActionDetail = new ActionDetail();
					mFinishedActionDetail.action_id = mTargetActionDetail.action_id;
					mFinishedActionDetail.group_num = 1;
					mFinishedActionGroupDetailList = mFinishedActionDetail.group_detail;
					
					mActionGroupIndex = 0;
					mTargetActionGroupDetailList = mTargetActionDetail.group_detail;
					mTargetGroupDetail = mTargetActionGroupDetailList.get(mActionGroupIndex);
					mActionCurrentGroupTotalCount = mTargetGroupDetail.count;
					mActionCurrentGroupCount = 0;
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
		
		mFinishedActionGroupDetailList = new ArrayList<GroupDetail>();
		mFinishedGroupDetail = new GroupDetail();
		
		mMyCourse = (MyCourse) getIntent().getSerializableExtra(Const.KEY_COURSE);
		mDayPosition = getIntent().getIntExtra(Const.KEY_DAY_POSITION, 0);
		
		CourseDetail courseDetail = mMyCourse.course_detail.get(mDayPosition);
		
		mActionDetailList = (ArrayList<ActionDetail>) courseDetail.action_detail;
		
		List<DayProgress> day_progress = mMyCourse.day_progress;
		mDayProgress = day_progress.get(mDayPosition);
		mStrPlanDate = mDayProgress.plan_date;
		
		mFinishedActionIds = new ArrayList<String>();;
		
		// 获取动作列表
		mTotalActionNum = mActionDetailList.size();
		
		mCurrentActionPosition = 0; // 第一个动作
		mTargetActionDetail = mActionDetailList.get(mCurrentActionPosition);
		
		mFinishedActionDetail = new ActionDetail();
		mFinishedActionDetail.action_id = mTargetActionDetail.action_id;
		mFinishedActionDetail.group_num = 1;
		mFinishedActionGroupDetailList = mFinishedActionDetail.group_detail;
		
		mActionGroupIndex = 0;
		mTargetActionGroupDetailList = mTargetActionDetail.group_detail;
		mTargetGroupDetail = mTargetActionGroupDetailList.get(mActionGroupIndex);
		mActionCurrentGroupTotalCount = mTargetGroupDetail.count;
		mActionCurrentGroupCount = 0;
		
		mCurrentRecord = new Record();
		mCurrentRecord.course_id = mMyCourse.course_id;
		mCurrentRecord.course_name = mMyCourse.course_name;
		mCurrentRecord.plan_date = mStrPlanDate;
		mCurrentRecord.finish_group_num = mActionGroupIndex + 1; // 完成的组数
		mFinishedActionDetailList = mCurrentRecord.action_detail;
		
		updateUi();
	}

	private void updateUi() {
		tvTotalCount.setText(String.valueOf(mActionCurrentGroupTotalCount));
		tvCurrentCount.setText(String.valueOf(mActionCurrentGroupCount));
		tvGroupIndex.setText("第" + (mActionGroupIndex + 1) + "组");
		tvActionCount.setText("动作" + (mCurrentActionPosition + 1));
		tvTotalGroup.setText("共" + mTargetActionDetail.group_num + "组");
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
			handleInterupt();
			break;
			
		case R.id.btn_finish_once:
			// 测试
			mBleServiceListener.onReedSwitch();
			break;

		default:
			break;
		}
	}

	private void handleInterupt() {
		if(mFinishedGroupDetail.count > 0){
			mFinishedActionDetail.group_num = mActionGroupIndex + 1;
			mFinishedActionGroupDetailList.add(mFinishedGroupDetail);
			mFinishedActionDetailList.add(mFinishedActionDetail);
		}
		mCurrentRecord.actual_date = DateUtil.getCurrentDate();
		MyFinishDao.getInstance().saveRecordToDb(Globle.gApplicationContext, mUserInfo.strAccountId, mCurrentRecord);
		
		Intent intent = new Intent(this, FinishActivity.class);
		intent.putExtra(FinishActivity.KEY_FINISH_OR_UNFINISH, FinishActivity.TYPE_UNFINISH);
		intent.putStringArrayListExtra(Const.KEY_ACTION_IDS, mFinishedActionIds);
		
		intent.putExtra(Const.KEY_COURSE_TOTAL_TIME, mCurrentRecord.finish_time);
		intent.putExtra(Const.KEY_COURSE_TOTAL_COUNT, mCurrentRecord.finish_count);
		intent.putExtra(Const.KEY_COURSE_TOTAL_BURNING, mCurrentRecord.finish_kcal);
		intent.putExtra(Const.KEY_COURSE_ID, mMyCourse.course_id);
		
		startActivity(intent);
		finish();
	}

	/**
	  * @Method: handleCourseFinished
	  * @Description: 完成训练	
	  * 返回类型：void 
	  */
	private void handleCourseFinished() {
		mCurrentRecord.actual_date = DateUtil.getCurrentDate();
		MyFinishDao.getInstance().saveRecordToDb(Globle.gApplicationContext, mUserInfo.strAccountId, mCurrentRecord);
		
		mDayProgress.progress = MyCourse.DAY_PROGRESS_FINISH;
		MyCourseDao.getInstance().saveMyCourseDayProgress(Globle.gApplicationContext, mUserInfo.strAccountId, mMyCourse);
		
		Intent intent = new Intent(this, FinishActivity.class);
		intent.putExtra(FinishActivity.KEY_FINISH_OR_UNFINISH, FinishActivity.TYPE_FINISH);
		intent.putExtra(Const.KEY_ACTION_IDS, mFinishedActionIds);
		
		intent.putExtra(Const.KEY_COURSE_TOTAL_TIME, mCurrentRecord.finish_time);
		intent.putExtra(Const.KEY_COURSE_TOTAL_COUNT, mCurrentRecord.finish_count);
		intent.putExtra(Const.KEY_COURSE_TOTAL_BURNING, mCurrentRecord.finish_kcal);
		intent.putExtra(Const.KEY_COURSE_ID, mMyCourse.course_id);
		
		startActivity(intent);
		finish();
	}
	
}
