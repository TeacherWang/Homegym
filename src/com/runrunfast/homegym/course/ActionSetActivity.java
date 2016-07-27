package com.runrunfast.homegym.course;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.DataTransferUtil;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.course.ActionSetAdapter.ITrainActionItemListener;
import com.runrunfast.homegym.dao.MyCourseActionDao;
import com.runrunfast.homegym.utils.ClickUtil;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.Globle;
import com.runrunfast.homegym.widget.PopupWindows;
import com.runrunfast.homegym.widget.WheelView;
import com.runrunfast.homegym.widget.WheelView.OnWheelViewListener;

import java.util.ArrayList;
import java.util.List;

public class ActionSetActivity extends Activity implements OnClickListener{
	private final String TAG = "TrainActionSetActivity";
	
	private static final int INPUT_TYPE_COUNT = 1;
	private static final int INPUT_TYPE_TOOL_WEIGHT = 2;
	
	private int inputType;
	
	private View backView;
	private TextView tvActionNum, tvTrainName, tvTrainDescript, tvJoinInTeach, tvGroupNum, tvTimeConsume, tvBurning;
	private Button btnAdd, btnMinus;
	
	private String mCourseId;
	private String mActionId;
	private String mActionName;
	private String mActionDescript;
	private int mActionNum; // 动作几
	private ListView mListView;
	
	private int mConsumeSecond = 500; // 消耗时间
	private int mBurning = 178; // 燃脂
	
	private ArrayList<ActionInfo> mTrainActionInfoList;
	private ActionSetAdapter mTrainActionSetAdapter;
	private ITrainActionItemListener mITrainActionItemListener;
	
	private RelativeLayout popView;
	private RelativeLayout selectContainer;
	private TextView tvPopTitle, tvPopConfirm;
	private View wheelOneLayout;
	private WheelView wheelOneWheelView;
	private PopupWindows popWindows;
	
	private ActionInfo mTrainActionInfo;
	private int mCount;
	private int mToolWeight;
	
	private UserInfo mUserInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_action_set);
		
		initView();
		
		initData();
		
		initListener();
	}

	private void initListener() {
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
		
		mTrainActionInfo = mTrainActionInfoList.get(position);
		int defaultCount = mTrainActionInfo.iCount;
		
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
		wheelOneWheelView.setSeletion(DataTransferUtil.getInstance().getCountPostion(defaultCount));
		wheelOneWheelView.setItems(AccountMgr.getInstance().getCountList());
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}
	
	private void showToolWeightSelectView(int position) {
		inputType = INPUT_TYPE_TOOL_WEIGHT;
		
		mTrainActionInfo = mTrainActionInfoList.get(position);
		int defaultToolWeight = mTrainActionInfo.iToolWeight;
		
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
		wheelOneWheelView.setSeletion(DataTransferUtil.getInstance().getToolWeightPostion(defaultToolWeight));
		wheelOneWheelView.setItems(AccountMgr.getInstance().getToolWeightList());
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}

	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		mTrainActionInfoList = new ArrayList<ActionInfo>();
		
		Intent intent = getIntent();
		mCourseId = intent.getStringExtra(Const.KEY_COURSE_ID);
		mActionId = intent.getStringExtra(Const.KEY_ACTION_ID);
		mActionName = intent.getStringExtra(Const.KEY_ACTION_NAME);
		mActionDescript = intent.getStringExtra(Const.KEY_ACTION_DESCRIPT);
		mActionNum = intent.getIntExtra(Const.KEY_ACTION_NUM, 1);
		
		tvActionNum.setText("动作" + DataTransferUtil.numMap.get(mActionNum));
		tvTrainName.setText(mActionName);
		tvTrainDescript.setText(mActionDescript);
		
		ActionInfo myActionInfo = MyCourseActionDao.getInstance().getMyCourseActionInfo(Globle.gApplicationContext, mUserInfo.strAccountId, mCourseId, mActionId);
		int myActionGroupNum = myActionInfo.defaultGroupNum;
		List<String> defaultCountList = myActionInfo.defaultCountList;
		List<String> defaultToolWeightList = myActionInfo.defaultToolWeightList;
		List<String> defaultBurningList = myActionInfo.defaultBurningList;
 		
		for(int i=0; i<myActionGroupNum; i++){
			ActionInfo trainActionInfo = new ActionInfo();
			trainActionInfo.iGroupNum = i + 1;
			trainActionInfo.iCount = Integer.parseInt(defaultCountList.get(i).trim());
			trainActionInfo.iToolWeight = Integer.parseInt(defaultToolWeightList.get(i).trim());
			trainActionInfo.iBurning = Integer.parseInt(defaultBurningList.get(i).trim());
			mTrainActionInfoList.add(trainActionInfo);
		}
		
		mTrainActionSetAdapter = new ActionSetAdapter(this, mTrainActionInfoList);
		mListView.setAdapter(mTrainActionSetAdapter);
		
		tvGroupNum.setText(String.valueOf(mTrainActionInfoList.size()));
		tvTimeConsume.setText(DateUtil.secToTime(myActionInfo.iTime));
		tvBurning.setText(String.valueOf(myActionInfo.iDefaultTotalKcal));
	}

	private void initView() {
		backView = (View)findViewById(R.id.train_action_back_layout);
		backView.setOnClickListener(this);
		
		btnAdd = (Button)findViewById(R.id.btn_group_num_add);
		btnAdd.setOnClickListener(this);
		
		btnMinus = (Button)findViewById(R.id.btn_group_num_minus);
		btnMinus.setOnClickListener(this);
		
		tvActionNum = (TextView)findViewById(R.id.train_action_num_text);
		tvTrainName = (TextView)findViewById(R.id.train_action_name_text);
		tvTrainDescript = (TextView)findViewById(R.id.train_action_descript_text);
		tvJoinInTeach = (TextView)findViewById(R.id.train_action_join_in_text);
		tvJoinInTeach.setOnClickListener(this);
		tvTimeConsume = (TextView)findViewById(R.id.train_action_time_num_text);
		tvBurning = (TextView)findViewById(R.id.train_action_burning_num_text);
		
		mListView = (ListView)findViewById(R.id.train_action_list);
		
		tvGroupNum = (TextView)findViewById(R.id.tv_group_num);
		
		popView = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.popupwindow_layout, null);
		tvPopTitle = (TextView)popView.findViewById(R.id.popupwindow_menu_title_text);
		tvPopConfirm = (TextView)popView.findViewById(R.id.popupwindow_menu_confirm_text);
		tvPopConfirm.setOnClickListener(this);
		selectContainer = (RelativeLayout)popView.findViewById(R.id.popupwindow_content);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.train_action_back_layout:
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
			
		default:
			break;
		}
	}

	private void clickPopConfirm() {
		if(popWindows == null){
			Log.e(TAG, "clickPopConfirm, popWindows == null");
			return;
		}
		
		popWindows.dismiss();
		
		switch (inputType) {
		case INPUT_TYPE_COUNT:
			mTrainActionInfo.iCount = mCount;
			break;
			
		case INPUT_TYPE_TOOL_WEIGHT:
			mTrainActionInfo.iToolWeight = mToolWeight;
			break;

		default:
			break;
		}
	}

	private void jumpToVideoDemo() {
		
	}

	private void minusGroupNum() {
		if(mTrainActionInfoList.size() == 1){
			Log.e(TAG, "addGroupNum, size == 1, return");
			Toast.makeText(this, R.string.group_num_is_smaller_than_1, Toast.LENGTH_SHORT).show();
			return;
		}
		
		mTrainActionInfoList.remove(mTrainActionInfoList.size() - 1);
		mTrainActionSetAdapter.notifyDataSetChanged();
		
		tvGroupNum.setText(String.valueOf(mTrainActionInfoList.size()));
		mConsumeSecond = mConsumeSecond - 10;
		mBurning = mBurning - 20;
		tvTimeConsume.setText(DateUtil.secToTime(mConsumeSecond));
		tvBurning.setText(String.valueOf(mBurning));
	}

	private void addGroupNum() {
		if(mTrainActionInfoList.size() == 9){
			Log.e(TAG, "addGroupNum, size == 9, return");
			Toast.makeText(this, R.string.group_num_is_bigger_than_9, Toast.LENGTH_SHORT).show();
			return;
		}
		
		ActionInfo trainActionInfo = new ActionInfo();
		trainActionInfo.strCourseId = mCourseId;
		trainActionInfo.strActionId = mActionId;
		trainActionInfo.iGroupNum = mTrainActionInfoList.size() + 1;
		trainActionInfo.iCount = 8;
		trainActionInfo.iToolWeight = 10;
		trainActionInfo.iBurning = 60;
		mTrainActionInfoList.add(trainActionInfo);
		
		int groupNum = mTrainActionInfoList.size();
		
		mTrainActionSetAdapter.notifyDataSetChanged();
		
		tvGroupNum.setText(String.valueOf(groupNum));
		
		mConsumeSecond = mConsumeSecond + 10;
		mBurning = mBurning + 20;
		tvTimeConsume.setText(DateUtil.secToTime(mConsumeSecond));
		tvBurning.setText(String.valueOf(mBurning));
		
		ActionInfo myActionInfo = new ActionInfo();
		myActionInfo.strCourseId = mCourseId;
		myActionInfo.strActionId = mActionId;
		myActionInfo.iTime = mConsumeSecond;
		myActionInfo.defaultGroupNum = groupNum;
		myActionInfo.iDefaultTotalKcal = mBurning;
		for(int i=0; i<groupNum; i++){
			ActionInfo actionInfo = mTrainActionInfoList.get(i);
			myActionInfo.defaultCountList.add(String.valueOf(actionInfo.iCount));
			myActionInfo.defaultToolWeightList.add(String.valueOf(actionInfo.iToolWeight));
			myActionInfo.defaultBurningList.add(String.valueOf(actionInfo.iBurning));
		}
		
		MyCourseActionDao.getInstance().saveMyCourseActionInfo(Globle.gApplicationContext, mCourseId, myActionInfo);
	}
}
