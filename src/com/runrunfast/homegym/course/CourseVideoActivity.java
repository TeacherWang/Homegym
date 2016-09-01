package com.runrunfast.homegym.course;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.runrunfast.homegym.R;
import com.runrunfast.homegym.BtDevice.BtDeviceMgr;
import com.runrunfast.homegym.BtDevice.BtDeviceMgr.BLEServiceListener;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.DataTransferUtil;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.bean.Course.ActionDetail;
import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.bean.Course.GroupDetail;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.bean.MyCourse.DayProgress;
import com.runrunfast.homegym.course.CourseServerMgr.IUpdateRecordListener;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.MyCourseDao;
import com.runrunfast.homegym.dao.MyTrainRecordDao;
import com.runrunfast.homegym.home.FinishActivity;
import com.runrunfast.homegym.record.TrainRecord;
import com.runrunfast.homegym.utils.CalculateUtil;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;
import com.runrunfast.homegym.widget.DialogActivity;
import com.runrunfast.homegym.widget.HorizonDialogActivity;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CourseVideoActivity extends Activity implements OnClickListener{
	private final String TAG = "CourseVideoActivity";
	private Resources mResources;
	
	private static final int MSG_ONE_SECOND = 1;
	private static final int MSG_REST_FINISH = 2; // 休息结束
	
	private static final int DELAY_SECOND = 1000;
	private static final int REST_TIME = 10 * 1000; // 休息时间 秒
	
	private static final int ACTION_SIDE_LEFT = 0;
	private static final int ACTION_SIDE_RIGHT = 1;
	
	private int mActionSide = ACTION_SIDE_LEFT;
	
	private Timer mTimer;
	private VideoTimerTask mVideoTimerTask;
	private int mTimeSecond = 0;
	
	private Button btnFinishOnce;
	private TextView tvCourseName, tvActionName, tvGroupCount, tvGroupNum, tvTime;
	private RelativeLayout rlHaveRest;
	
	private VideoView mVideoView;
	private String mVideoPath;
	private MediaController mMediaController;
	
	private ImageView ivExit, ivBluetooth;
	
	private UserInfo mUserInfo;
	private MyCourse mMyCourse;
	private int mDayPosition;
	private List<DayProgress> mDayProgresseList;
	private DayProgress mDayProgress;
	private ArrayList<ActionDetail> mActionDetailList;
	
	private String mStrPlanDate;
	private int mTotalActionNum; // 总共的动作个数
	
	private int mActionCurrentGroupCount; // 该动作在当前组的次数
	
	private Action mAction; // 当前动作的基本信息
	private ActionDetail mTargetActionDetail; // 当前正在进行的目标动作
	private ActionDetail mFinishedActionDetail; // 当前已经完成的动作
	private int mCurrentActionPosition; // 当前正在进行的动作位置，从0开始
	private GroupDetail mTargetGroupDetail; // 当前动作该组的目标数据
	private GroupDetail mFinishedGroupDetail; // 当前动作该组的已经完成的数据
	private List<GroupDetail> mTargetActionGroupDetailList; // 当前正在进行的目标动作的每组数据集合
	private List<GroupDetail> mFinishedActionGroupDetailList; // 当前已经完成的指定动作的每组数据集合
	private TrainRecord mCurrentRecord; // 当前的记录
	private int mActionGroupIndex; // 该组动作已经开始的组数。比如第一个动作第一组开始，那么为0，第二组开始，那么为二
	private int mActionCurrentGroupTotalCount; // 该动作在该组的总次数
	
	private List<ActionDetail> mFinishedActionDetailList;
	
	private BLEServiceListener mBleServiceListener;
	
	private ArrayList<String> mFinishedActionIds;
	
	private boolean isRest = false;
	private boolean isPrepareNextAction = false; // 该动作完成，正在准备下一个动作
	
	private IUpdateRecordListener mIUpdateRecordListener;
	
	private boolean isPause = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mResources = getResources();
		
		setContentView(R.layout.activity_course_video);
		
		initView();
		
		initData();
		
		initListener();
		
		initCourseServerListener();
	}

	private void initCourseServerListener() {
		mIUpdateRecordListener = new IUpdateRecordListener() {
			
			@Override
			public void onUpdateRecordSuc() {
				Toast.makeText(CourseVideoActivity.this, "上传数据成功", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onUpdateRecordFail() {
				Toast.makeText(CourseVideoActivity.this, "上传数据失败", Toast.LENGTH_SHORT).show();
			}
		};
		CourseServerMgr.getInstance().addUpdateRecordObserver(mIUpdateRecordListener);
	}

	private void initListener() {
		mBleServiceListener = new BLEServiceListener() {
			
			@Override
			public void onReedSwitch() {
				if(isPause){
					Log.d(TAG, "onReedSwitch, isShowDialog, dont handle");
					return;
				}
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
		
		if(isRest){
			mHandler.removeMessages(MSG_REST_FINISH);
			isRest = false;
			rlHaveRest.setVisibility(View.GONE);
			startVideo(mVideoPath);
		}
		
		// 当前次数小于该组总次数
		if( mActionCurrentGroupCount < mActionCurrentGroupTotalCount ){
			mActionCurrentGroupCount++; // 当前组的次数
			
			mFinishedGroupDetail.kcal = CalculateUtil.calculateTotakKcal(mActionCurrentGroupCount, mTargetGroupDetail.weight, mAction.action_h, mAction.action_b); // 该动作消耗的kcal
			mFinishedGroupDetail.count = mFinishedGroupDetail.count + 1;
			mFinishedGroupDetail.weight = mTargetGroupDetail.weight;
			
			mCurrentRecord.finish_count = mCurrentRecord.finish_count + 1; // 该次训练的总次数
			
			updateUi();
		}
		// 次数+1后，该组还未结束
		if(mActionCurrentGroupCount < mActionCurrentGroupTotalCount){
			return;
		}
		// 次数+1后，该组结束
		mFinishedActionDetail.group_num = mActionGroupIndex + 1;
		mFinishedActionGroupDetailList.add(mFinishedGroupDetail);
		
		// 该动作还有下一组
		mActionGroupIndex++;
		if(mActionGroupIndex < mTargetActionDetail.group_num ){
			mFinishedGroupDetail = new GroupDetail();
			mTargetGroupDetail = mTargetActionGroupDetailList.get(mActionGroupIndex);
			mActionCurrentGroupTotalCount = mTargetGroupDetail.count;
			mActionCurrentGroupCount = 0;
			// 做完一组休息一下
			handleNextGroup();
			handleRest();
		}else{ // 当前为最后一组的最后一次
			Toast.makeText(CourseVideoActivity.this, "该动作结束", Toast.LENGTH_SHORT).show();
			// 保存到列表
			mFinishedActionDetailList.add(mFinishedActionDetail);
			mFinishedActionIds.add(mFinishedActionDetail.action_id);
			// 还有下个动作？
			mCurrentActionPosition++;
			if( mCurrentActionPosition < mTotalActionNum ){
				isPrepareNextAction = true;
				mAction = ActionDao.getInstance().getActionFromDb(Globle.gApplicationContext, mTargetActionDetail.action_id);
				handleNextAction();
				// 做下个动作之前休息一下
				handleRest();
				
				mTargetActionDetail = mActionDetailList.get(mCurrentActionPosition);
				tvActionName.setText( (mCurrentActionPosition + 1) + "." + mAction.action_name);
				
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
				prepareCourseFinished();
			}
		}
	}

	/**
	  * @Method: handleNextGroup
	  * @Description: 做下一组动作。可能会换边
	  * 返回类型：void 
	  */
	private void handleNextGroup() {
		if(mAction.action_left_right == Action.ACTION_TWO_SIDE){
			if(mActionSide == ACTION_SIDE_LEFT){
				mActionSide = ACTION_SIDE_RIGHT;
				mVideoPath = mAction.action_video_local.get(1);
			}else{
				mActionSide = ACTION_SIDE_LEFT;
				mVideoPath = mAction.action_video_local.get(0);
			}
		}
	}

	private void handleNextAction() {
		mActionSide = ACTION_SIDE_LEFT;
		mVideoPath = mAction.action_video_local.get(0);
	}

	private void handleRest() {
		Toast.makeText(CourseVideoActivity.this, "休息一下", Toast.LENGTH_SHORT).show();
		
		mHandler.sendEmptyMessageDelayed(MSG_REST_FINISH, REST_TIME);
		
		isRest = true;
		rlHaveRest.setVisibility(View.VISIBLE);
		mVideoView.stopPlayback();
	}

	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		mFinishedActionGroupDetailList = new ArrayList<GroupDetail>();
		mFinishedGroupDetail = new GroupDetail();
		
		mMyCourse = (MyCourse) getIntent().getSerializableExtra(Const.KEY_COURSE);
		mDayPosition = getIntent().getIntExtra(Const.KEY_DAY_POSITION, 0);
		
		tvCourseName.setText(mMyCourse.course_name);
		
		CourseDetail courseDetail = mMyCourse.course_detail.get(mDayPosition);
		
		mActionDetailList = (ArrayList<ActionDetail>) courseDetail.action_detail;
		
		mDayProgresseList = mMyCourse.day_progress;
		mDayProgress = mDayProgresseList.get(mDayPosition);
		mStrPlanDate = DateUtil.getDateStrOfDayNumFromStartDate(mDayProgress.day_num, mMyCourse.start_date);
		
		mFinishedActionIds = new ArrayList<String>();;
		
		// 获取动作列表
		mTotalActionNum = mActionDetailList.size();
		
		mCurrentActionPosition = 0; // 第一个动作
		mTargetActionDetail = mActionDetailList.get(mCurrentActionPosition);
		mAction = ActionDao.getInstance().getActionFromDb(Globle.gApplicationContext, mTargetActionDetail.action_id);
		tvActionName.setText( (mCurrentActionPosition + 1) + "." + mAction.action_name);
		
		mFinishedActionDetail = new ActionDetail();
		mFinishedActionDetail.action_id = mTargetActionDetail.action_id;
		mFinishedActionDetail.group_num = 1;
		mFinishedActionGroupDetailList = mFinishedActionDetail.group_detail;
		
		mActionGroupIndex = 0;
		mTargetActionGroupDetailList = mTargetActionDetail.group_detail;
		mTargetGroupDetail = mTargetActionGroupDetailList.get(mActionGroupIndex);
		mActionCurrentGroupTotalCount = mTargetGroupDetail.count;
		mActionCurrentGroupCount = 0;
		
		mCurrentRecord = new TrainRecord();
		mCurrentRecord.course_id = mMyCourse.course_id;
		mCurrentRecord.course_name = mMyCourse.course_name;
		mCurrentRecord.plan_date = mStrPlanDate;
		mFinishedActionDetailList = mCurrentRecord.action_detail;
		
		updateUi();
		
//		mVideoPath = Environment.getExternalStorageDirectory()+"/video.mp4";
		mVideoPath = mAction.action_video_local.get(0);
		
		initVideo();
		
		startVideo(mVideoPath);
		
		mTimer = new Timer();
		mVideoTimerTask = new VideoTimerTask();
		mTimer.scheduleAtFixedRate(mVideoTimerTask, DELAY_SECOND, DELAY_SECOND);
		tvTime.setText(DateUtil.secToMinuteSecond(mTimeSecond));
	}

	private void initVideo() {
		mMediaController = new MediaController(this);
		mVideoView.requestFocus();
		mVideoView.setMediaController(mMediaController);
		
		mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				// optional need Vitamio 4.0
				mediaPlayer.setPlaybackSpeed(1.0f);
				mediaPlayer.setLooping(true);
			}
		});
	}

	private class VideoTimerTask extends TimerTask{

		@Override
		public void run() {
			Message msg = new Message();
			msg.what = MSG_ONE_SECOND;
			mHandler.sendMessage(msg);
		}
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_ONE_SECOND:
				mTimeSecond++;
				tvTime.setText(DateUtil.secToMinuteSecond(mTimeSecond));
				break;
				
			case MSG_REST_FINISH:
				// 休息结束
				if(isRest){
					mHandler.removeMessages(MSG_REST_FINISH);
					isRest = false;
					rlHaveRest.setVisibility(View.GONE);
					startVideo(mVideoPath);
					// 该动作还有下一组
					if((mActionGroupIndex + 1) < mTargetActionDetail.group_num){
						GroupDetail groupDetail = mTargetActionGroupDetailList.get(mActionGroupIndex + 1);
						int actionCurrentGroupTotalCount = groupDetail.count;
						// 更新ui
						tvGroupCount.setText( 0 + "/" + String.valueOf(actionCurrentGroupTotalCount) );
						tvGroupNum.setText("第" + DataTransferUtil.numMap.get(mActionGroupIndex + 1) + "组");
					}else{
						ActionDetail targetActionDetail = mActionDetailList.get(mCurrentActionPosition + 1);
						List<GroupDetail> targetActionGroupDetailList = targetActionDetail.group_detail;
						GroupDetail targetGroupDetail = targetActionGroupDetailList.get(0);
						int actionCurrentGroupTotalCount = targetGroupDetail.count;
						// 更新ui
						tvGroupCount.setText( 0 + "/" + String.valueOf(actionCurrentGroupTotalCount) );
						tvGroupNum.setText("第一组");
					}
				}
				break;

			default:
				break;
			}
		};
	};
	
	private void startVideo(String videoPath) {
		/*
		 * Alternatively,for streaming media you can use
		 * mVideoView.setVideoURI(Uri.parse(URLstring));
		 */
		mVideoView.setVideoPath(videoPath);
		mVideoView.start();
	}
	
	private void setVideoPath(String videoPath) {
		mVideoView.setVideoPath(videoPath);
	}
	
	private void startVideo(){
		mVideoView.start();
	}

	private void updateUi() {
		tvGroupCount.setText( String.valueOf(mActionCurrentGroupCount) + "/" + String.valueOf(mActionCurrentGroupTotalCount) );
		tvGroupNum.setText("第" + DataTransferUtil.numMap.get(mActionGroupIndex + 1) + "组");
	}

	private void initView() {
		btnFinishOnce = (Button)findViewById(R.id.btn_finish_once);
		
		btnFinishOnce.setOnClickListener(this);
		
		mVideoView = (VideoView)findViewById(R.id.surface_view);
		tvCourseName = (TextView)findViewById(R.id.train_course_name_text);
		tvActionName = (TextView)findViewById(R.id.train_action_name_text);
		tvGroupCount = (TextView)findViewById(R.id.course_video_count_text);
		tvGroupNum = (TextView)findViewById(R.id.course_video_group_index_text);
		tvTime = (TextView)findViewById(R.id.course_video_time_text);
		
		rlHaveRest = (RelativeLayout)findViewById(R.id.have_rest_layout);
		rlHaveRest.setVisibility(View.GONE);
		
		ivExit = (ImageView)findViewById(R.id.video_exit_img);
		ivExit.setOnClickListener(this);
		ivBluetooth = (ImageView)findViewById(R.id.video_bluetooth_img);
		ivBluetooth.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_finish_once:
			// 测试
			mBleServiceListener.onReedSwitch();
			break;
			
		case R.id.video_exit_img:
			handleInterupt();
			break;
			
		case R.id.video_bluetooth_img:
			
			break;

		default:
			break;
		}
	}

	private void handleInterupt() {
		isPause = true;
		
		mVideoTimerTask.cancel();
		mVideoView.pause();
		showExitDialog();
	}
	
	private void showExitDialog() {
		Intent intent = new Intent(this, HorizonDialogActivity.class);
		intent.putExtra(DialogActivity.KEY_CONTENT_COLOR, mResources.getColor(R.color.bt_device_connected_color));
		intent.putExtra(DialogActivity.KEY_CONTENT, mResources.getString(R.string.unfinish_train));
		intent.putExtra(DialogActivity.KEY_CANCEL, mResources.getString(R.string.exit));
		intent.putExtra(DialogActivity.KEY_CONFIRM, mResources.getString(R.string.continue_train));
		startActivityForResult(intent, Const.DIALOG_REQ_CODE_EXIT_TRAIN);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Const.DIALOG_REQ_CODE_EXIT_TRAIN){
			if(resultCode == DialogActivity.RSP_CONFIRM){
				isPause = false;
				
				mVideoView.start();
				mVideoTimerTask = new VideoTimerTask();
				mTimer.scheduleAtFixedRate(mVideoTimerTask, DELAY_SECOND, DELAY_SECOND);
			}else{
				mVideoView.stopPlayback();
				mVideoView.suspend();
				mVideoTimerTask.cancel();
				releaseTimer();
				handleCourseUnfinish();
			}
		}else if(requestCode == Const.DIALOG_REQ_CODE_FINISH_TRAIN){
			if(resultCode == DialogActivity.RSP_CONFIRM){
				handleCourseFinished();
			}
		}
	}

	private void handleCourseUnfinish() {
		if(mFinishedGroupDetail.count > 0){
			mFinishedActionDetail.group_num = mActionGroupIndex + 1;
			mFinishedActionGroupDetailList.add(mFinishedGroupDetail);
			mFinishedActionDetailList.add(mFinishedActionDetail);
		}
		
		float totalKcal = 0;
		int finishedActionSize = mFinishedActionDetailList.size();
		for(int i=0; i<finishedActionSize; i++){
			ActionDetail actionDetail = mFinishedActionDetailList.get(i);
			for(int j=0; j<actionDetail.group_num; j++){
				GroupDetail groupDetail = actionDetail.group_detail.get(j);
				totalKcal = totalKcal + groupDetail.kcal;
			}
		}
		mCurrentRecord.finish_kcal = totalKcal;
		
		mCurrentRecord.finish_time = mTimeSecond;
		mCurrentRecord.actual_date = DateUtil.getCurrentDate();
		mCurrentRecord.unique_flag = System.currentTimeMillis();
		
		if(mCurrentRecord.finish_count > 0){
			MyTrainRecordDao.getInstance().saveRecordToDb(Globle.gApplicationContext, mUserInfo.strAccountId, mCurrentRecord);
			uploadRecord();
		}
		
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
	private void prepareCourseFinished() {
		isPause = true;
		
		mVideoTimerTask.cancel();
		mVideoView.stopPlayback();
		mVideoView.suspend();
		releaseTimer();
		
		showFinishedDialog();
	}
	
	private void showFinishedDialog() {
		Intent intent = new Intent(this, HorizonDialogActivity.class);
		intent.putExtra(DialogActivity.KEY_CONTENT_COLOR, mResources.getColor(R.color.bt_device_connected_color));
		intent.putExtra(DialogActivity.KEY_CONTENT, mResources.getString(R.string.finish_train));
		intent.putExtra(DialogActivity.KEY_CONFIRM, mResources.getString(R.string.confirm));
		startActivityForResult(intent, Const.DIALOG_REQ_CODE_FINISH_TRAIN);
	}

	private void handleCourseFinished() {
		float totalKcal = 0;
		int finishedActionSize = mFinishedActionDetailList.size();
		for(int i=0; i<finishedActionSize; i++){
			ActionDetail actionDetail = mFinishedActionDetailList.get(i);
			for(int j=0; j<actionDetail.group_num; j++){
				GroupDetail groupDetail = actionDetail.group_detail.get(j);
				totalKcal = totalKcal + groupDetail.kcal;
			}
		}
		mCurrentRecord.finish_kcal = totalKcal;
		
		mCurrentRecord.finish_time = mTimeSecond;
		mCurrentRecord.actual_date = DateUtil.getCurrentDate();
		mCurrentRecord.unique_flag = System.currentTimeMillis();
		MyTrainRecordDao.getInstance().saveRecordToDb(Globle.gApplicationContext, mUserInfo.strAccountId, mCurrentRecord);
		
		mDayProgress.progress = MyCourse.DAY_PROGRESS_FINISH;
		MyCourseDao.getInstance().saveMyCourseDayProgress(Globle.gApplicationContext, mUserInfo.strAccountId, mMyCourse);
		
		int courseProgress = MyCourse.COURSE_PROGRESS_FINISH;
		int daySize = mDayProgresseList.size();
		for(int i=0; i<daySize; i++){
			DayProgress dayProgress = mDayProgresseList.get(i);
			if(dayProgress.progress == MyCourse.DAY_PROGRESS_UNFINISH){
				courseProgress = MyCourse.COURSE_PROGRESS_ING;
				break;
			}
		}
		if(mCurrentRecord.finish_count > 0){
			uploadRecord();
		}
		
		if(courseProgress == MyCourse.COURSE_PROGRESS_FINISH){
			mMyCourse.progress = courseProgress;
			
			uploadTrainPlan();
			
			MyCourseDao.getInstance().saveMyCourseProgress(Globle.gApplicationContext, mUserInfo.strAccountId, mMyCourse.course_id, courseProgress);
		}
		
		showFinishActivity();
	}

	private void showFinishActivity() {
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

	private void uploadTrainPlan() {
		Gson gson = new Gson();
		
		String courseDetailStr = gson.toJson(mMyCourse.course_detail);
		String dayProgress = gson.toJson(mMyCourse.day_progress);
		
		CourseServerMgr.getInstance().uploadTrainPlan(mUserInfo.strAccountId, mMyCourse.course_id, courseDetailStr, mMyCourse.progress, dayProgress);
	}
	
	private void uploadRecord(){
		Gson gson = new Gson();
		CourseServerMgr.getInstance().updateRecord(mUserInfo.strAccountId, gson.toJson(mCurrentRecord));
	}
	
	private void releaseTimer(){
		if(mTimer != null){
			mTimer.cancel();
			mTimer.purge();
			mTimer = null;
		}
	}
	
	@Override
	public void onBackPressed() {
		handleInterupt();
	}
	
	@Override
	protected void onDestroy() {
		CourseServerMgr.getInstance().removeUpdateRecordObserver(mIUpdateRecordListener);
		super.onDestroy();
	}
	
}
