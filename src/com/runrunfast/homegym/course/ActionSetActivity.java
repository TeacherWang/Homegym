package com.runrunfast.homegym.course;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.DataTransferUtil;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.bean.Course.ActionDetail;
import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.bean.Course.GroupDetail;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.course.ActionSetAdapter.ITrainActionItemListener;
import com.runrunfast.homegym.course.CourseServerMgr.IUpdateTrainPlanListener;
import com.runrunfast.homegym.course.CourseTrainActivity.ActionTotalData;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.MyCourseDao;
import com.runrunfast.homegym.utils.CalculateUtil;
import com.runrunfast.homegym.utils.ClickUtil;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.FileUtils;
import com.runrunfast.homegym.utils.Globle;
import com.runrunfast.homegym.widget.DialogActivity;
import com.runrunfast.homegym.widget.PopupWindows;
import com.runrunfast.homegym.widget.WheelView;
import com.runrunfast.homegym.widget.WheelView.OnWheelViewListener;

import java.util.ArrayList;
import java.util.List;

public class ActionSetActivity extends Activity implements OnClickListener{
	private final String TAG = "TrainActionSetActivity";
	
	private static final int INPUT_TYPE_COUNT = 1;
	private static final int INPUT_TYPE_TOOL_WEIGHT = 2;
	
	public static final int REQ_CODE_CONFIRM = 3;
	
	private int inputType;
	
	private Resources mResources;
	private ImageView ivHeadBg;
	private View backView;
	private TextView tvSave;
	private TextView tvActionNum, tvTrainName, tvTrainDescript, tvJoinInTeach, tvGroupNum, tvBurning;
//	private TextView tvTimeConsume;
	private Button btnAdd, btnMinus;
	
	private ListView mListView;
	
//	private int mConsumeSecond = 500; // 消耗时间
//	private int mTotalBurning; // 总燃脂
	
	private ArrayList<GroupDetail> mGroupDetailList;
	private ActionSetAdapter mTrainActionSetAdapter;
	private ITrainActionItemListener mITrainActionItemListener;
	
	private RelativeLayout popView;
	private RelativeLayout selectContainer;
	private TextView tvPopTitle, tvPopConfirm;
	private View wheelOneLayout;
	private WheelView wheelOneWheelView;
	private PopupWindows popWindows;
	
	private View mActionDescriptLayout;
	private View mActionDescriptLayoutId;
	private PopupWindows mActionDesciptsPopupWindows;
	private TextView tvActionDescript;
	
	private GroupDetail mGroupDetail;
	private int mCount;
	private int mToolWeight;
	
	private UserInfo mUserInfo;
	
	private Action mAction;
	private MyCourse mMyCourse;
	private ActionDetail mActionDetail;
	private int mActionPosition;
	private int mCurrentDayPosition;
	private ActionTotalData mActionTotalData;
	
	private IUpdateTrainPlanListener mIUpdateTrainPlanListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_action_set);
		
		mResources = getResources();
		
		initView();
		
		initData();
		
		initItemListener();
		
		initServerListener();
	}

	private void initItemListener() {
		mITrainActionItemListener = new ITrainActionItemListener() {
			
			@Override
			public void onCountClicked(int position) {
				if(ClickUtil.isFastClick()){
					Log.d(TAG, "onCountClicked, is fast click");
					return;
				}
				showCountSelectView(position);
			}

			@Override
			public void onToolWeightClicked(int position) {
				if(ClickUtil.isFastClick()){
					Log.d(TAG, "onToolWeightClicked, is fast click");
					return;
				}
				showToolWeightSelectView(position);
			}

		};
		mTrainActionSetAdapter.setOnITrainActionItemListener(mITrainActionItemListener);
	}
	
	private void showCountSelectView(int position) {
		inputType = INPUT_TYPE_COUNT;
		
		mGroupDetail = mGroupDetailList.get(position);
		mCount = mGroupDetail.count;
		
		selectContainer.removeAllViews();
//		setSelectContainerWidth();
		wheelOneLayout = (View)LayoutInflater.from(this).inflate(R.layout.wheel_one, null);
		selectContainer.addView(wheelOneLayout);
		tvPopTitle.setText(R.string.select_count);
		
		wheelOneWheelView = (WheelView)wheelOneLayout.findViewById(R.id.select_wheelview);
		wheelOneWheelView.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, item = " + item);
				mCount = Integer.valueOf(item);
			}
		});
		wheelOneWheelView.setTextSize(24);
		wheelOneWheelView.setOffset(1);
		wheelOneWheelView.setSeletion(DataTransferUtil.getInstance().getCountPostion(mCount));
		wheelOneWheelView.setItems(AccountMgr.getInstance().getCountList());
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}
	
	private void showToolWeightSelectView(int position) {
		inputType = INPUT_TYPE_TOOL_WEIGHT;
		
		mGroupDetail = mGroupDetailList.get(position);
		mToolWeight = mGroupDetail.weight;
		
		if(mToolWeight == 0){
			return;
		}
		
		selectContainer.removeAllViews();
//		setSelectContainerWidth();
		wheelOneLayout = (View)LayoutInflater.from(this).inflate(R.layout.wheel_one, null);
		selectContainer.addView(wheelOneLayout);
		tvPopTitle.setText(R.string.select_tool_weight);
		
		wheelOneWheelView = (WheelView)wheelOneLayout.findViewById(R.id.select_wheelview);
		wheelOneWheelView.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, item = " + item);
				mToolWeight = Integer.valueOf(item);
			}
		});
		wheelOneWheelView.setTextSize(24);
		wheelOneWheelView.setOffset(1);
		wheelOneWheelView.setSeletion(DataTransferUtil.getInstance().getToolWeightPostion(mToolWeight));
		wheelOneWheelView.setItems(AccountMgr.getInstance().getToolWeightList());
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}

	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		mGroupDetailList = new ArrayList<GroupDetail>();
		
		Intent intent = getIntent();
		mAction = (Action) intent.getSerializableExtra(Const.KEY_ACTION);
		int actionPosition = intent.getIntExtra(Const.KEY_ACTION_POSITION, 0);
		mMyCourse = (MyCourse) intent.getSerializableExtra(Const.KEY_COURSE);
		mActionDetail = (ActionDetail) intent.getSerializableExtra(Const.KEY_ACTION_DETAIL);
		mCurrentDayPosition = intent.getIntExtra(Const.KEY_DAY_POSITION, 0);
		mActionPosition = intent.getIntExtra(Const.KEY_ACTION_POSITION, 0);
		mActionTotalData = (ActionTotalData) intent.getSerializableExtra(Const.KEY_ACTION_TOTAL_DATA);
		
		tvActionNum.setText("动作" + DataTransferUtil.numMap.get(actionPosition + 1));
		tvTrainName.setText(mAction.action_name);
		tvTrainDescript.setText(R.string.action_descript);
		
//		tvTrainDescript.setText("坚持训练坚持训练坚持训练坚持训练坚持训练坚持训练坚持训练坚持训练坚持训练坚持训练坚持训练坚持训练坚持训练坚持训练坚持训练");
		
		mGroupDetailList = (ArrayList<GroupDetail>) mActionDetail.group_detail;
		
		mTrainActionSetAdapter = new ActionSetAdapter(this, mGroupDetailList);
		mListView.setAdapter(mTrainActionSetAdapter);
		
		tvGroupNum.setText(String.valueOf(mGroupDetailList.size()));
//		tvTimeConsume.setText(DateUtil.secToTime(mActionTotalData.totalTime));
//		mTotalBurning = mActionTotalData.totalKcal;
		tvBurning.setText( DataTransferUtil.getInstance().getTwoDecimalData(mActionTotalData.totalKcal) );
		
		String strImgLocal = ActionDao.getInstance().getActionImgLocalFromDb(Globle.gApplicationContext, mAction.action_id);
		
		if( !TextUtils.isEmpty(strImgLocal) && FileUtils.isFileExist(strImgLocal) ){
			Bitmap actionBitmap = BitmapFactory.decodeFile(strImgLocal);
			ivHeadBg.setImageBitmap(actionBitmap);
		}else{
			ivHeadBg.setBackgroundResource(R.drawable.action1_bg);
		}
		
	}

	private void initView() {
		backView = (View)findViewById(R.id.train_action_back_layout);
		backView.setOnClickListener(this);
		
		ivHeadBg = (ImageView)findViewById(R.id.train_action_set_bg_img);
		
		tvSave = (TextView)findViewById(R.id.train_action_set_save_text);
		tvSave.setOnClickListener(this);
		
		btnAdd = (Button)findViewById(R.id.btn_group_num_add);
		btnAdd.setOnClickListener(this);
		
		btnMinus = (Button)findViewById(R.id.btn_group_num_minus);
		btnMinus.setOnClickListener(this);
		
		tvActionNum = (TextView)findViewById(R.id.train_action_num_text);
		tvTrainName = (TextView)findViewById(R.id.train_action_name_text);
		tvTrainDescript = (TextView)findViewById(R.id.train_action_descript_text);
		tvTrainDescript.setOnClickListener(this);
		tvJoinInTeach = (TextView)findViewById(R.id.train_action_join_in_text);
		tvJoinInTeach.setOnClickListener(this);
//		tvTimeConsume = (TextView)findViewById(R.id.train_action_time_num_text);
		tvBurning = (TextView)findViewById(R.id.train_action_burning_num_text);
		
		mListView = (ListView)findViewById(R.id.train_action_list);
		
		tvGroupNum = (TextView)findViewById(R.id.tv_group_num);
		
		popView = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.popupwindow_layout, null);
		tvPopTitle = (TextView)popView.findViewById(R.id.popupwindow_menu_title_text);
		tvPopConfirm = (TextView)popView.findViewById(R.id.popupwindow_menu_confirm_text);
		tvPopConfirm.setOnClickListener(this);
		selectContainer = (RelativeLayout)popView.findViewById(R.id.popupwindow_content);
		
		mActionDescriptLayout = LayoutInflater.from(this).inflate(R.layout.popupwindow_action_descript_layout, null);
		mActionDescriptLayoutId = mActionDescriptLayout.findViewById(R.id.action_descript_layout);
		mActionDescriptLayoutId.setOnClickListener(this);
		
		tvActionDescript = (TextView)mActionDescriptLayout.findViewById(R.id.action_descript_text);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.train_action_back_layout:
			setResult(Activity.RESULT_CANCELED);
			finish();
			break;
			
		case R.id.btn_group_num_add:
			addGroupNum();
			break;
			
		case R.id.btn_group_num_minus:
			minusGroupNum();
			break;
			
		case R.id.train_action_join_in_text:
			jumpToVideoDemo();
			break;
			
		case R.id.popupwindow_menu_confirm_text:
			clickPopConfirm();
			break;
			
		case R.id.train_action_set_save_text:
			saveNewData();
			break;
			
		case R.id.train_action_descript_text:
			showActionDescript();
			break;
			
		case R.id.action_descript_layout:
			if(mActionDesciptsPopupWindows != null){
				mActionDesciptsPopupWindows.dismiss();
				mActionDesciptsPopupWindows = null;
			}
			break;
			
		default:
			break;
		}
	}

	private void showActionDescript() {
		tvActionDescript.setText(mAction.action_descript);
		if(mActionDesciptsPopupWindows == null){
			mActionDesciptsPopupWindows = new PopupWindows(this, mActionDescriptLayout);
			mActionDesciptsPopupWindows.setLayout(mActionDescriptLayout);
		}
		
		mActionDesciptsPopupWindows.show();
	}

	private void clickPopConfirm() {
		if(popWindows == null){
			Log.e(TAG, "clickPopConfirm, popWindows == null");
			return;
		}
		
		popWindows.dismiss();
		
		ActionTotalData actionTotalData = null;
		switch (inputType) {
		case INPUT_TYPE_COUNT:
			mGroupDetail.count = mCount;
			// 要计算次数跟重量对应的燃脂，公式？
			if(mGroupDetail.weight == 0){
				mGroupDetail.kcal = CalculateUtil.calculateTotakKcal(mGroupDetail.count, CalculateUtil.DEFAULT_WEIGHT_VALUE_IF_ZERO, mAction.action_h, mAction.action_b);
			}else{
				mGroupDetail.kcal = CalculateUtil.calculateTotakKcal(mGroupDetail.count, mGroupDetail.weight, mAction.action_h, mAction.action_b);
			}
			
			actionTotalData = getTotalTimeOfActionInMyCourse(mActionDetail);
			
			tvBurning.setText( DataTransferUtil.getInstance().getTwoDecimalData(actionTotalData.totalKcal) );
			
			mTrainActionSetAdapter.updateData(mGroupDetailList);
			break;
			
		case INPUT_TYPE_TOOL_WEIGHT:
			mGroupDetail.weight = mToolWeight;
			// 要计算次数跟重量对应的燃脂，公式？还有时间
			if(mGroupDetail.weight == 0){
				mGroupDetail.kcal = CalculateUtil.calculateTotakKcal(mGroupDetail.count, CalculateUtil.DEFAULT_WEIGHT_VALUE_IF_ZERO, mAction.action_h, mAction.action_b);
			}else{
				mGroupDetail.kcal = CalculateUtil.calculateTotakKcal(mGroupDetail.count, mGroupDetail.weight, mAction.action_h, mAction.action_b);
			}
			
			actionTotalData = getTotalTimeOfActionInMyCourse(mActionDetail);
			
			tvBurning.setText( DataTransferUtil.getInstance().getTwoDecimalData(actionTotalData.totalKcal) );
//			mTrainActionInfo.iBurning = 公式？
//			mTotalBurning = mTotalBurning + trainActionInfo.iBurning;
//			tvTimeConsume.setText(DateUtil.secToTime(mConsumeSecond));
//			tvBurning.setText(String.valueOf(mTotalBurning));
			
			mTrainActionSetAdapter.updateData(mGroupDetailList);
			break;

		default:
			break;
		}
	}

	private ActionTotalData getTotalTimeOfActionInMyCourse(ActionDetail actionDetail){
		ActionTotalData actionTotalData = new ActionTotalData();
		int groupNum = actionDetail.group_num;
		for(int i=0; i<groupNum; i++){
			GroupDetail groupDetail = actionDetail.group_detail.get(i);
			actionTotalData.totalKcal = actionTotalData.totalKcal + groupDetail.kcal;
		}
		return actionTotalData;
	}
	
	/**
	  * @Method: saveNewData
	  * @Description: 保存设置的数据
	  * 返回类型：void 
	  */
	private void saveNewData() {
		List<CourseDetail> courseDetails = mMyCourse.course_detail;
		CourseDetail currentDayCourseDetail = courseDetails.get(mCurrentDayPosition);
		mMyCourse.course_detail.get(mCurrentDayPosition).action_detail.remove(mActionPosition);
		mMyCourse.course_detail.get(mCurrentDayPosition).action_detail.add(mActionPosition, mActionDetail);
		Log.i(TAG, "modify group_num = " + mMyCourse.course_detail.get(mCurrentDayPosition).action_detail.get(mActionPosition).group_num);
		
		showSaveDialog();
	}
	
	private void showSaveDialog() {
		Intent intent = new Intent(this, DialogActivity.class);
		intent.putExtra(DialogActivity.KEY_CONTENT, mResources.getString(R.string.ask_save));
		intent.putExtra(DialogActivity.KEY_CANCEL, mResources.getString(R.string.no));
		intent.putExtra(DialogActivity.KEY_CONFIRM, mResources.getString(R.string.yes));
		startActivityForResult(intent, Const.DIALOG_REQ_CODE_SAVE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode != Const.DIALOG_REQ_CODE_SAVE){
			return;
		}
		if(resultCode == DialogActivity.RSP_CONFIRM){
			Gson gson = new Gson();
			
			String courseDetailStr = gson.toJson(mMyCourse.course_detail);
			String dayProgress = gson.toJson(mMyCourse.day_progress);
			
			CourseServerMgr.getInstance().uploadTrainPlan(mUserInfo.strAccountId, mMyCourse.course_id, courseDetailStr, mMyCourse.progress, dayProgress);
		}
	}
	
	private void initServerListener() {
		mIUpdateTrainPlanListener = new IUpdateTrainPlanListener() {
			
			@Override
			public void onUpdateTrainPlanSuc() {
				MyCourseDao.getInstance().saveMyCourseToDb(Globle.gApplicationContext, mUserInfo.strAccountId, mMyCourse);
				
				Toast.makeText(ActionSetActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent();
				intent.putExtra(Const.KEY_COURSE, mMyCourse);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
			
			@Override
			public void onUpdateTrainPlanFail() {
				Toast.makeText(ActionSetActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
			}
		};
		CourseServerMgr.getInstance().addUpdateTrainPlanObserver(mIUpdateTrainPlanListener);
	}
	
	private void jumpToVideoDemo() {
		Intent intent = new Intent(this, ActionDemoActivity.class);
		intent.putExtra(Const.KEY_ACTION, mAction);
		startActivity(intent);
	}

	private void minusGroupNum() {
		if(mGroupDetailList.size() == 1){
			Log.e(TAG, "addGroupNum, size == 1, return");
			Toast.makeText(this, R.string.group_num_is_smaller_than_1, Toast.LENGTH_SHORT).show();
			return;
		}
		
		GroupDetail removeGroupDetail = mGroupDetailList.get(mGroupDetailList.size() - 1);
		
		mGroupDetailList.remove(mGroupDetailList.size() - 1);
		mActionDetail.group_num = mGroupDetailList.size();
		mActionDetail.group_detail = mGroupDetailList;
		
		mTrainActionSetAdapter.notifyDataSetChanged();
		
		tvGroupNum.setText(String.valueOf(mGroupDetailList.size()));
		
		ActionTotalData actionTotalData = getTotalTimeOfActionInMyCourse(mActionDetail);
		
		tvBurning.setText(DataTransferUtil.getInstance().getTwoDecimalData(actionTotalData.totalKcal));
	}

	private void addGroupNum() {
		if(mGroupDetailList.size() == 9){
			Log.e(TAG, "addGroupNum, size == 9, return");
			Toast.makeText(this, R.string.group_num_is_bigger_than_9, Toast.LENGTH_SHORT).show();
			return;
		}
		// 修改动作重量为0时的特殊处理
		if(mGroupDetail.weight == 0){
			GroupDetail groupDetail = new GroupDetail(8, 10, CalculateUtil.calculateTotakKcal(8, CalculateUtil.DEFAULT_WEIGHT_VALUE_IF_ZERO, mAction.action_h, mAction.action_b));
			mGroupDetailList.add(groupDetail);
		}else{
			GroupDetail groupDetail = new GroupDetail(8, 10, CalculateUtil.calculateTotakKcal(8, 10, mAction.action_h, mAction.action_b));
			mGroupDetailList.add(groupDetail);
		}
		
//		GroupDetail groupDetail = new GroupDetail(8, 10, CalculateUtil.calculateTotakKcal(8, 10, mAction.action_h, mAction.action_b));
//		mGroupDetailList.add(groupDetail);
		
		mActionDetail.group_num = mGroupDetailList.size();
		mActionDetail.group_detail = mGroupDetailList;
		
		mTrainActionSetAdapter.notifyDataSetChanged();
		
		tvGroupNum.setText(String.valueOf(mActionDetail.group_num));
		
		ActionTotalData actionTotalData = getTotalTimeOfActionInMyCourse(mActionDetail);
		
		tvBurning.setText(DataTransferUtil.getInstance().getTwoDecimalData(actionTotalData.totalKcal));
	}
	
	@Override
	public void onBackPressed() {
		Log.i(TAG, "onBackPressed");
		
		Intent intent = new Intent();
		setResult(Activity.RESULT_CANCELED, intent);
		finish();
	}
	
	@Override
	protected void onDestroy() {
		CourseServerMgr.getInstance().removeUpdateTrainPlanObserver(mIUpdateTrainPlanListener);
		super.onDestroy();
	}
}
