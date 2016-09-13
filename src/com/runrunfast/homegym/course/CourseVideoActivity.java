package com.runrunfast.homegym.course;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.google.gson.Gson;
import com.runrunfast.homegym.R;
import com.runrunfast.homegym.BtDevice.BtDeviceMgr;
import com.runrunfast.homegym.BtDevice.BtDeviceMgr.BLEServiceListener;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.DataTransferUtil;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.audio.MediaPlayerMgr;
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
import com.runrunfast.homegym.utils.ConstServer;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.FileUtils;
import com.runrunfast.homegym.utils.Globle;
import com.runrunfast.homegym.widget.DialogActivity;
import com.runrunfast.homegym.widget.HorizonDialogActivity;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CourseVideoActivity extends Activity implements OnClickListener{
	private final String TAG = "CourseVideoActivity";
	private Resources mResources;
	
	private static final int MSG_ONE_SECOND = 1;
	private static final int MSG_REST_FINISH = 2; // 休息结束
	private static final int MSG_SHOW_REST_AND_PLAY = 3; // 最后一个动作做完后，等次数报完后，显示休息并播报“休息一下”
	
	private static final int DELAY_SECOND = 1000;
	private static final int REST_TIME = 50 * 1000; // 休息时间 秒
	private static final int REST_TIME_SHOW_INIT = 50; // 显示休息时间
	
	private static final int ACTION_SIDE_LEFT = 0;
	private static final int ACTION_SIDE_RIGHT = 1;
	
	private int mActionSide = ACTION_SIDE_LEFT;
	
	private int mRestTime = REST_TIME_SHOW_INIT;
	private Timer mTimer;
	private VideoTimerTask mVideoTimerTask;
	private int mTimeSecond = 0;
	
	private Button btnFinishOnce;
	private TextView tvCourseName, tvActionName, tvGroupCount, tvGroupNum, tvTime, tvRestTime;
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
//	private boolean needExplainAction = true;
	
	private IUpdateRecordListener mIUpdateRecordListener;
	
	private boolean isPause = false;
	
	// 语音合成
	private SpeechSynthesizer mSpeechSynthesizer; 
	private String mSampleDirPath;
	private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";
    
    private int mReedSwitchCount = 0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		mResources = getResources();
		
		setContentView(R.layout.activity_course_video);
		
		initView();
		
		initTTS();
		
		initData();
		
		initListener();
		
		initCourseServerListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
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
				
				mReedSwitchCount++;
				
				if( (mReedSwitchCount % 2) != 0){
					return;
				}
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						handleFinishOnce();
					}
				});
			}
			
			@Override
			public void onGetDevice(BluetoothDevice btDevice) {}
			
			@Override
			public void onDeviceDisconnected() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ivBluetooth.setBackgroundResource(R.drawable.video_bluetooth_red);
					}
				});
			}
			
			@Override
			public void onDeviceConnected() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						ivBluetooth.setBackgroundResource(R.drawable.video_bluetooth_white);
					}
				});
			}
			
			@Override
			public void onBLEInit() {}

			@Override
			public void onBTOpen(final boolean isOpened) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if( !isOpened ){
							ivBluetooth.setBackgroundResource(R.drawable.video_bluetooth_red);
						}
					}
				});
			}
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
			handleRestFinish();
			return;
		}
		
		// 当前次数小于该组总次数
		if( mActionCurrentGroupCount < mActionCurrentGroupTotalCount ){
			mActionCurrentGroupCount++; // 当前组的次数
			
			speekFinishOnce(mActionCurrentGroupCount);
			
			mFinishedGroupDetail.kcal = CalculateUtil.calculateTotakKcal(mActionCurrentGroupCount, mTargetGroupDetail.weight, mAction.action_h, mAction.action_b); // 该动作消耗的kcal
			mFinishedGroupDetail.count = mFinishedGroupDetail.count + 1;
			mFinishedGroupDetail.weight = mTargetGroupDetail.weight;
			
			mCurrentRecord.finish_count = mCurrentRecord.finish_count + 1; // 该次训练的总次数
			
			updateUi(mActionCurrentGroupCount, mActionCurrentGroupTotalCount, mActionGroupIndex);
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
			prepareNextGroup();
//			showRest();
			mHandler.sendEmptyMessageDelayed(MSG_SHOW_REST_AND_PLAY, DELAY_SECOND);
		}else{ // 当前为最后一组的最后一次
			Toast.makeText(CourseVideoActivity.this, "该动作结束", Toast.LENGTH_SHORT).show();
			// 保存到列表
			mFinishedActionDetailList.add(mFinishedActionDetail);
			mFinishedActionIds.add(mFinishedActionDetail.action_id);
			// 还有下个动作？
			mCurrentActionPosition++;
			if( mCurrentActionPosition < mTotalActionNum ){
				mTargetActionDetail = mActionDetailList.get(mCurrentActionPosition);
				mAction = ActionDao.getInstance().getActionFromDb(Globle.gApplicationContext, mTargetActionDetail.action_id);
				prepareNextAction();
				// 做下个动作之前休息一下
//				showRest();
				mHandler.sendEmptyMessageDelayed(MSG_SHOW_REST_AND_PLAY, DELAY_SECOND);
				
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
	
	private void handleRestFinish() {
		mHandler.removeMessages(MSG_REST_FINISH);
		isRest = false;
		rlHaveRest.setVisibility(View.GONE);
		mMediaController.show(0);
		startVideo(mVideoPath);
		mRestTime = REST_TIME_SHOW_INIT;
		// 下一个动作
		if(mActionGroupIndex == 0 && mActionCurrentGroupCount == 0){
			speekFirstGroup(mAction.action_name, mTargetGroupDetail.count, mTargetGroupDetail.weight);
			
			updateUi(0, mTargetGroupDetail.count, mActionGroupIndex);
		}else if(mActionGroupIndex < mTargetActionDetail.group_num){ // 该动作还有下一组
			if(mAction.action_left_right == Action.ACTION_TWO_SIDE){
				speekTurnRound(mAction.action_name, mTargetGroupDetail.count, mTargetGroupDetail.weight);
			}else{
				speekNextGroup(mAction.action_name, mTargetGroupDetail.count, mTargetGroupDetail.weight);
			}
			
			GroupDetail groupDetail = mTargetActionGroupDetailList.get(mActionGroupIndex);
			int actionCurrentGroupTotalCount = groupDetail.count;
			// 更新ui
			updateUi(0, actionCurrentGroupTotalCount, mActionGroupIndex);
		}
//		else{
//			// 下一个动作
//			speekFirstGroup(mAction.action_name, mTargetGroupDetail.count, mTargetGroupDetail.weight);
//			
//			ActionDetail targetActionDetail = mActionDetailList.get(mCurrentActionPosition);
//			List<GroupDetail> targetActionGroupDetailList = targetActionDetail.group_detail;
//			GroupDetail targetGroupDetail = targetActionGroupDetailList.get(0);
//			int actionCurrentGroupTotalCount = targetGroupDetail.count;
//			// 更新ui
//			updateUi(0, actionCurrentGroupTotalCount, 0);
//		}
	};

	/**
	  * @Method: prepareNextGroup
	  * @Description: 做下一组动作。可能会换边
	  * 返回类型：void 
	  */
	private void prepareNextGroup() {
		if(mAction.action_left_right == Action.ACTION_ONE_SIDE){
			return;
		}
		
		Log.i(TAG, "handleNextGroup, this is two side!");
		
		if(mActionSide == ACTION_SIDE_LEFT){
			mActionSide = ACTION_SIDE_RIGHT;
			mVideoPath = mAction.action_video_local.get(1);
		}else{
			mActionSide = ACTION_SIDE_LEFT;
			mVideoPath = mAction.action_video_local.get(0);
		}
	}

	private void prepareNextAction() {
		Log.i(TAG, "prepareNextAction");
		
//		needExplainAction = true;
		mActionSide = ACTION_SIDE_LEFT;
		mVideoPath = mAction.action_video_local.get(0);
	}

	private void handleRestAudio() {
		Log.i(TAG, "handleRestAudio, play rest audio");
		
		MediaPlayerMgr.getInstance().startPlaying(Globle.gApplicationContext, R.raw.rest);
		
	}
	
	private void showRest(){
		mHandler.sendEmptyMessageDelayed(MSG_REST_FINISH, REST_TIME);
		
		isRest = true;
		mMediaController.hide();
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
		
		updateUi(mActionCurrentGroupCount, mActionCurrentGroupTotalCount, mActionGroupIndex);
		
		mVideoPath = mAction.action_video_local.get(0);
		
		initVideo();
		
		startVideo(mVideoPath);
		
		speekFirstGroup(mAction.action_name, mTargetGroupDetail.count, mTargetGroupDetail.weight);
		
		mTimer = new Timer();
		mVideoTimerTask = new VideoTimerTask();
		mTimer.scheduleAtFixedRate(mVideoTimerTask, DELAY_SECOND, DELAY_SECOND);
		tvTime.setText(DateUtil.secToMinuteSecond(mTimeSecond));
		
		if(BtDeviceMgr.getInstance().isConnected()){
			ivBluetooth.setBackgroundResource(R.drawable.video_bluetooth_white);
		}else{
			ivBluetooth.setBackgroundResource(R.drawable.video_bluetooth_red);
		}
	}
	
	private void speekFinishOnce(int count){
		Log.i(TAG, "speekFinishOnce, count = " + count);
		MediaPlayerMgr.getInstance().stopPlaying();
		
		mSpeechSynthesizer.stop();
		
		mSpeechSynthesizer.speak(count + "次");
	}
	
	private void speekFirstGroup(String actionName, int count, int weight){
		Log.i(TAG, "speekFirstGroup");
		mSpeechSynthesizer.speak("第一组动作：" + actionName + count + "次，" + weight + "公斤");
	}
	
	private void speekNextGroup(String actionName, int count, int weight){
		Log.i(TAG, "speekNextGroup");
		mSpeechSynthesizer.speak("下一组动作：" + actionName + count + "次，" + weight + "公斤");
	}
	
	private void speekTurnRound(String actionName, int count, int weight){
		Log.i(TAG, "speekTurnRound");
		mSpeechSynthesizer.speak("换另一边，继续：" + actionName + count + "次，" + weight + "公斤");
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
				if(isRest && mRestTime > 0){
					mRestTime--;
					tvRestTime.setText(String.valueOf(mRestTime));
				}
				break;
				
			case MSG_SHOW_REST_AND_PLAY:
				showRest();
				handleRestAudio();
				break;
				
			case MSG_REST_FINISH:
				// 休息结束
				if(isRest){
					handleRestFinish();
				}
				break;

			default:
				break;
			}
		}
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

	/**
	  * @Method: updateUi
	  * @Description: 更新UI
	  * @param currentGroupCount 当前组的个数
	  * @param actinoCurrentGroupTotalCount 当前组的总个数
	  * @param actionGroupIndex	当前组的标识
	  * 返回类型：void 
	  */
	private void updateUi(int currentGroupCount, int actinoCurrentGroupTotalCount, int actionGroupIndex) {
		tvGroupCount.setText( String.valueOf(currentGroupCount) + "/" + String.valueOf(actinoCurrentGroupTotalCount) );
		tvGroupNum.setText("第" + DataTransferUtil.getBigNum(actionGroupIndex + 1) + "组");
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
		
		tvRestTime = (TextView)findViewById(R.id.rest_time_text);
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
		if(mCurrentRecord.finish_count <= 0){
			showUnfinishActivity();
			return;
		}
		
		if(mActionCurrentGroupCount == 0){
			mFinishedActionDetail.group_num = mActionGroupIndex;
		}else{
			mFinishedActionDetail.group_num = mActionGroupIndex + 1;
		}
		
		if(mFinishedGroupDetail.count != 0){
			mFinishedActionGroupDetailList.add(mFinishedGroupDetail);
		}
		if(mFinishedActionDetail.group_num != 0){
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
		
		showUnfinishActivity();
	}

	private void showUnfinishActivity() {
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
			MyTrainRecordDao.getInstance().saveRecordToDb(Globle.gApplicationContext, mUserInfo.strAccountId, mCurrentRecord);
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
	
	private void initTTS() {
		initialEnv();
		
		mSpeechSynthesizer = SpeechSynthesizer.getInstance();
		mSpeechSynthesizer.setContext(this);
		// 文本模型文件路径 (离线引擎使用)
		mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/" + TEXT_MODEL_NAME);
		// 声学模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        this.mSpeechSynthesizer.setAppId("8592514");
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        this.mSpeechSynthesizer.setApiKey("G68yo3o3su3yct1DK874NDWl", "5b783c5dc9db7327a52d52f84a224e4e");
        // 授权检测接口(只是通过AuthInfo进行检验授权是否成功。)
        // AuthInfo接口用于测试开发者是否成功申请了在线或者离线授权，如果测试授权成功了，可以删除AuthInfo部分的代码（该接口首次验证时比较耗时），不会影响正常使用（合成使用时SDK内部会自动验证授权）
//        AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);

//        if (authInfo.isSuccess()) {
//            Log.i(TAG, "initTTS, auth success");
//        } else {
//            String errorMsg = authInfo.getTtsError().getDetailMessage();
//            Log.i(TAG, "initTTS, auth failed errorMsg = " + errorMsg);
//        }
        
		mSpeechSynthesizer.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {
			
			@Override
			public void onSynthesizeStart(String arg0) {
//				Log.i(TAG, "onSynthesizeStart, arg0 = " + arg0);
			}
			
			@Override
			public void onSynthesizeFinish(String arg0) {
//				Log.i(TAG, "onSynthesizeFinish, arg0 = " + arg0);
			}
			
			@Override
			public void onSynthesizeDataArrived(String arg0, byte[] arg1, int arg2) {
//				Log.i(TAG, "onSynthesizeDataArrived, arg0 = " + arg0);
			}
			
			@Override
			public void onSpeechStart(String arg0) {
//				Log.i(TAG, "onSpeechStart, arg0 = " + arg0);
			}
			
			@Override
			public void onSpeechProgressChanged(String arg0, int arg1) {
//				Log.i(TAG, "onSpeechProgressChanged, arg0 = " + arg0 + ", arg1 = " + arg1);
			}
			
			@Override
			public void onSpeechFinish(String arg0) {
				Log.i(TAG, "onSpeechFinish, arg0 = " + arg0
						+ ", mActionCurrentGroupCount = " + mActionCurrentGroupCount
						+ ", mActionGroupIndex = " + mActionGroupIndex
						+ ", isRest = " + isRest);
				if( mActionCurrentGroupCount !=0 || mActionGroupIndex !=0 || isRest){
					return;
				}
				
				String audioPath = mAction.action_audio_local;
				Log.i(TAG, "onSpeechFinish, audioPath = " + audioPath);
				if( TextUtils.isEmpty(audioPath) || !FileUtils.isFileExist(audioPath) ){
					Log.e(TAG, "audioPath = " + audioPath + ", or file not exist");
					return;
				}
				MediaPlayerMgr.getInstance().startPlaying(audioPath);
			}
			
			@Override
			public void onError(String arg0, SpeechError arg1) {
//				Log.i(TAG, "onError, arg0 = " + arg0 + ", SpeechError arg1 = " + arg1);
			}
		});
		
		// 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.MIX);
	}
	
	private void initialEnv() {
        if (TextUtils.isEmpty(mSampleDirPath)) {
            mSampleDirPath = ConstServer.SDCARD_HOMEGYM_ROOT + "/" + SAMPLE_DIR_NAME + "/";
        }
        FileUtils.makeDirs(mSampleDirPath);
        if( !FileUtils.isFileExist(mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME) ){
        	copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        }
        
        if( !FileUtils.isFileExist(mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME) ){
        	copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        }
        
        if( !FileUtils.isFileExist(mSampleDirPath + "/" + TEXT_MODEL_NAME) ){
        	copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        }
        
        if( !FileUtils.isFileExist(mSampleDirPath + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME) ){
        	copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        }
        
        if( !FileUtils.isFileExist(mSampleDirPath + "/" + ENGLISH_SPEECH_MALE_MODEL_NAME) ){
        	copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + ENGLISH_SPEECH_MALE_MODEL_NAME);
        }
        
        if( !FileUtils.isFileExist(mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME) ){
        	copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME);
        }
    }
	
	/**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     * 
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
    private BroadcastReceiver mVolumChangedReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equalsIgnoreCase(Const.ACTION_VOLUME_CHANGED)){
			}
		}
	};
    
	@Override
	public void onBackPressed() {
		handleInterupt();
	}
	
	@Override
	protected void onDestroy() {
		if(mIUpdateRecordListener != null){
			CourseServerMgr.getInstance().removeUpdateRecordObserver(mIUpdateRecordListener);
		}
		
		if(mBleServiceListener != null){
			BtDeviceMgr.getInstance().removeBLEServiceObserver(mBleServiceListener);
		}
		
		if(mSpeechSynthesizer != null){
			mSpeechSynthesizer.release();
		}
		
		MediaPlayerMgr.getInstance().stopPlaying();
		
		super.onDestroy();
	}
	
}
