package com.runrunfast.homegym.course;

import java.util.ArrayList;

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
import com.runrunfast.homegym.course.ActionSetAdapter.ITrainActionItemListener;
import com.runrunfast.homegym.utils.ClickUtil;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.widget.PopupWindows;
import com.runrunfast.homegym.widget.WheelView;
import com.runrunfast.homegym.widget.WheelView.OnWheelViewListener;

public class ActionSetActivity extends Activity implements OnClickListener{
	private final String TAG = "TrainActionSetActivity";
	
	private static final int INPUT_TYPE_COUNT = 1;
	private static final int INPUT_TYPE_TOOL_WEIGHT = 2;
	
	private int inputType;
	
	private View backView;
	private TextView tvActionNum, tvTrainName, tvTrainDescript, tvJoinInTeach, tvGroupNum, tvTimeConsume, tvBurning;
	private Button btnAdd, btnMinus;
	
	private String mCourseId;
	private String mTrainId;
	private String mTrainName;
	private String mTrainDescript;
	private String mActionNum;
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
		mTrainActionInfoList = new ArrayList<ActionInfo>();
		
		Intent intent = getIntent();
		mCourseId = intent.getStringExtra(Const.KEY_COURSE_ID);
		mTrainId = intent.getStringExtra(Const.KEY_TRAIN_ID);
		mTrainName = intent.getStringExtra(Const.KEY_TRAIN_NAME);
		mTrainDescript = intent.getStringExtra(Const.KEY_TRAIN_DESCRIPT);
		mActionNum = intent.getStringExtra(Const.KEY_ACTION_NUM);
		
		tvActionNum.setText(mActionNum);
		tvTrainName.setText(mTrainName);
		tvTrainDescript.setText(mTrainDescript);
		
		ActionInfo trainActionInfo1 = new ActionInfo();
		trainActionInfo1.strCourseId = mCourseId;
		trainActionInfo1.strActionId = mTrainId;
		trainActionInfo1.strGroupNum = "第一组";
		trainActionInfo1.iCount = 10;
		trainActionInfo1.iToolWeight = 5;
		trainActionInfo1.iBurning = 36;
		mTrainActionInfoList.add(trainActionInfo1);
		
		ActionInfo trainActionInfo2 = new ActionInfo();
		trainActionInfo2.strCourseId = mCourseId;
		trainActionInfo2.strActionId = mTrainId;
		trainActionInfo2.strGroupNum = "第二组";
		trainActionInfo2.iCount = 10;
		trainActionInfo2.iToolWeight = 10;
		trainActionInfo2.iBurning = 45;
		mTrainActionInfoList.add(trainActionInfo2);
		
		ActionInfo trainActionInfo3 = new ActionInfo();
		trainActionInfo3.strCourseId = mCourseId;
		trainActionInfo3.strActionId = mTrainId;
		trainActionInfo3.strGroupNum = "第三组";
		trainActionInfo3.iCount = 12;
		trainActionInfo3.iToolWeight = 15;
		trainActionInfo3.iBurning = 50;
		mTrainActionInfoList.add(trainActionInfo3);
		
		ActionInfo trainActionInfo4 = new ActionInfo();
		trainActionInfo4.strCourseId = mCourseId;
		trainActionInfo4.strActionId = mTrainId;
		trainActionInfo4.strGroupNum = "第四组";
		trainActionInfo4.iCount = 8;
		trainActionInfo4.iToolWeight = 10;
		trainActionInfo4.iBurning = 60;
		mTrainActionInfoList.add(trainActionInfo4);
		
		mTrainActionSetAdapter = new ActionSetAdapter(this, mTrainActionInfoList);
		mListView.setAdapter(mTrainActionSetAdapter);
		
		tvGroupNum.setText(String.valueOf(mTrainActionInfoList.size()));
		tvTimeConsume.setText(DateUtil.secToTime(mConsumeSecond));
		tvBurning.setText(String.valueOf(mBurning));
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
		trainActionInfo.strActionId = mTrainId;
		trainActionInfo.strGroupNum = "第" + DataTransferUtil.getInstance().getBigNum(mTrainActionInfoList.size() + 1) + "组";
		trainActionInfo.iCount = 8;
		trainActionInfo.iToolWeight = 10;
		trainActionInfo.iBurning = 60;
		mTrainActionInfoList.add(trainActionInfo);
		
		mTrainActionSetAdapter.notifyDataSetChanged();
		
		tvGroupNum.setText(String.valueOf(mTrainActionInfoList.size()));
		
		mConsumeSecond = mConsumeSecond + 10;
		mBurning = mBurning + 20;
		tvTimeConsume.setText(DateUtil.secToTime(mConsumeSecond));
		tvBurning.setText(String.valueOf(mBurning));
	}
}
