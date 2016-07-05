package com.runrunfast.homegym.home;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.DataTransferUtil;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.widget.PopupWindows;
import com.runrunfast.homegym.widget.WheelView;
import com.runrunfast.homegym.widget.WheelView.OnWheelViewListener;

import java.util.List;

public class PersonalInfoActivity extends Activity implements OnClickListener{
	private final String TAG = "PersonalInfoActivity";
	
	private static final int INPUT_TYPE_SEX = 1;
	private static final int INPUT_TYPE_BIRTH = 2;
	private static final int INPUT_TYPE_WEIGHT = 3;
	private static final int INPUT_TYPE_HEIGHT = 4;
	
	private int inputType;
	
	private Resources mResources;
	
	private RelativeLayout popView;
	private RelativeLayout selectContainer;
	private PopupWindows popWindows;
	private TextView tvPopTitle, tvPopConfirm;
	
	private View wheelOneLayout;
	private WheelView wheelOneWheelView;
	
	private View wheelTwoLayout;
	private WheelView wheelTwoWheelView1, wheelTwoWheelView2;
	
	private View wheelThreeLayout;
	private WheelView wheelThreeWheelView1, wheelThreeWheelView2, wheelThreeWheelView3;
	
	private TextView tvNick, tvSex, tvBirth, tvCity, tvHeight, tvWeight, tvBmiNum, tvBmiDescip;
	
	private String strSex;
	private String mBirthday;
	private String strWeight;
	private String strHeight;
	
	private HandlerThread handleThread;
	private Handler mWorkHandler;
	
	private String mBirthdayYear;
	private String mBirthdayMonth;
	private String mBirthdayDay;
	
	private int mYearPosition;
	private int mMonthPosition;
	private int mDayPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info);
		
		mResources = getResources();
		
		initView();
		
		initData();
	}

	private void initData() {
		handleThread = new HandlerThread(TAG);
		handleThread.start();
		mWorkHandler = new Handler(handleThread.getLooper());
		
		mBirthday = DateUtil.getCurrentDate();
		
		getBirthdayMonthAndDay();
	}
	
	private void getBirthdayMonthAndDay(){
		mBirthdayYear = DataTransferUtil.getInstance().getBirthYear(mBirthday);
		mBirthdayMonth = DataTransferUtil.getInstance().getBirthMonth(mBirthday);
		mBirthdayDay = DataTransferUtil.getInstance().getBirthDay(mBirthday);
		
		mYearPosition = DataTransferUtil.getInstance().getYearPosition(mBirthdayYear);
		mMonthPosition = DataTransferUtil.getInstance().getMonthPosition(mBirthdayMonth);
		mDayPosition = DataTransferUtil.getInstance().getDayPosition(mBirthdayDay);
	}
	
	private void initView() {
		tvNick = (TextView)findViewById(R.id.personal_nick_text);
		
		tvSex = (TextView)findViewById(R.id.personal_sex_text);
		tvSex.setOnClickListener(this);
		
		tvBirth = (TextView)findViewById(R.id.personal_birth_text);
		tvBirth.setOnClickListener(this);
		
		tvCity = (TextView)findViewById(R.id.personal_city_text);
		tvCity.setOnClickListener(this);
		
		tvHeight = (TextView)findViewById(R.id.personal_height_text);
		tvHeight.setOnClickListener(this);
		
		tvWeight = (TextView)findViewById(R.id.personal_weight_text);
		tvWeight.setOnClickListener(this);
		
		tvBmiNum = (TextView)findViewById(R.id.personal_bmi_text);
		tvBmiDescip = (TextView)findViewById(R.id.personal_bmi_context_text);
		
		popView = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.popupwindow_layout, null);
		tvPopTitle = (TextView)popView.findViewById(R.id.popupwindow_menu_title_text);
		tvPopConfirm = (TextView)popView.findViewById(R.id.popupwindow_menu_confirm_text);
		tvPopConfirm.setOnClickListener(this);
		
		selectContainer = (RelativeLayout)popView.findViewById(R.id.popupwindow_content);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.personal_sex_text:
			showSelectSex();
			break;
		case R.id.personal_birth_text:
			showSelectBirth();
			break;
		case R.id.personal_city_text:
			
			break;
		case R.id.personal_height_text:
			showSelectHeight();
			break;
		case R.id.personal_weight_text:
			showSelectWeight();
			break;
		case R.id.personal_bmi_text:
			
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
		case INPUT_TYPE_HEIGHT:
			tvHeight.setText(strHeight);
			break;
			
		case INPUT_TYPE_WEIGHT:
			tvWeight.setText(strWeight);
			break;
			
		case INPUT_TYPE_SEX:
			tvSex.setText(strSex);
			break;
			
		case INPUT_TYPE_BIRTH:
			tvBirth.setText(mBirthday);
			break;

		default:
			break;
		}
	}
	
	private void showSelectHeight() {
		inputType = INPUT_TYPE_HEIGHT;
		selectContainer.removeAllViews();
		setSelectContainerWidth();
		wheelOneLayout = (View)LayoutInflater.from(this).inflate(R.layout.wheel_one, null);
		selectContainer.addView(wheelOneLayout);
		tvPopTitle.setText(R.string.select_height);
		
		wheelOneWheelView = (WheelView)wheelOneLayout.findViewById(R.id.select_wheelview);
		wheelOneWheelView.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, item = " + item);
				strHeight = item;
			}
		});
		wheelOneWheelView.setTextSize(24);
		wheelOneWheelView.setOffset(1);
		wheelOneWheelView.setSeletion(20);
		wheelOneWheelView.setItems(AccountMgr.getInstance().getHeightList());
		strHeight = AccountMgr.getInstance().getHeightList().get(20);
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}

	private void showSelectWeight() {
		inputType = INPUT_TYPE_WEIGHT;
		selectContainer.removeAllViews();
		setSelectContainerWidth();
		wheelOneLayout = (View)LayoutInflater.from(this).inflate(R.layout.wheel_one, null);
		selectContainer.addView(wheelOneLayout);
		tvPopTitle.setText(R.string.select_weight);
		
		wheelOneWheelView = (WheelView)wheelOneLayout.findViewById(R.id.select_wheelview);
		wheelOneWheelView.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, item = " + item);
				strWeight = item;
			}
		});
		wheelOneWheelView.setTextSize(24);
		wheelOneWheelView.setOffset(1);
		wheelOneWheelView.setSeletion(20);
		wheelOneWheelView.setItems(AccountMgr.getInstance().getWeightList());
		strWeight = AccountMgr.getInstance().getWeightList().get(20);
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}

	private void showSelectBirth() {
		getBirthdayMonthAndDay();
		
		inputType = INPUT_TYPE_BIRTH;
		selectContainer.removeAllViews();
		setSelectContainerBigWidth();
		wheelThreeLayout = (View)LayoutInflater.from(this).inflate(R.layout.wheel_three, null);
		selectContainer.addView(wheelThreeLayout);
		tvPopTitle.setText(R.string.select_birth);
		
		wheelThreeWheelView1 = (WheelView)wheelThreeLayout.findViewById(R.id.select_wheelview_one);
		wheelThreeWheelView2 = (WheelView)wheelThreeLayout.findViewById(R.id.select_wheelview_two);
		wheelThreeWheelView3 = (WheelView)wheelThreeLayout.findViewById(R.id.select_wheelview_three);
		
		wheelThreeWheelView1.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, year = " + item);
				mBirthdayYear = item;
				mBirthday = mBirthdayYear + "-" + mBirthdayMonth + "-" + mBirthdayDay;
			}
		});
		wheelThreeWheelView2.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, month = " + item);
				mBirthdayMonth = String.format("%02d", Integer.parseInt(item));
				
				mBirthdayDay = "1";
				mDayPosition = 0;
				
				mBirthday = mBirthdayYear + "-" + mBirthdayMonth + "-" + String.format("%02d", Integer.parseInt(mBirthdayDay));
				
				Log.d(TAG, "onSelected, day list = " + DataTransferUtil.getInstance().getDayList(mBirthday));
				
				setBirthDayWheel(DataTransferUtil.getInstance().getDayList(mBirthday), 0);
			}
		});
		wheelThreeWheelView3.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, day = " + item);
				mBirthdayDay = String.format("%02d", Integer.parseInt(item));
				mBirthday = mBirthdayYear + "-" + mBirthdayMonth + "-" + mBirthdayDay;
			}
		});
		wheelThreeWheelView1.setTextSize(18);
		wheelThreeWheelView1.setOffset(1);
		wheelThreeWheelView1.setSeletion(mYearPosition);
		wheelThreeWheelView1.setItems(AccountMgr.getInstance().getYearList());
		
		setBirthMonthWheel(AccountMgr.getInstance().getMonthList(), mMonthPosition);
		
		setBirthDayWheel(DataTransferUtil.getInstance().getDayList(mBirthday), mDayPosition);
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}

	private void setBirthDayWheel(List<String> dayList, int position) {
		wheelThreeWheelView3.setTextSize(18);
		wheelThreeWheelView3.setOffset(1);
		wheelThreeWheelView3.setSeletion(position);
		wheelThreeWheelView3.setItems(dayList);
	}

	private void setBirthMonthWheel(List<String> monthList, int position) {
		wheelThreeWheelView2.setTextSize(18);
		wheelThreeWheelView2.setOffset(1);
		wheelThreeWheelView2.setSeletion(position);
		wheelThreeWheelView2.setItems(monthList);
	}

	private void setSelectContainerWidth() {
		LayoutParams params = (LayoutParams) selectContainer.getLayoutParams();
		params.height = LayoutParams.MATCH_PARENT;
		params.width = (int) mResources.getDimension(R.dimen.popwindow_content_width);
		selectContainer.setLayoutParams(params);
	}
	
	private void setSelectContainerBigWidth() {
		LayoutParams params = (LayoutParams) selectContainer.getLayoutParams();
		params.height = LayoutParams.MATCH_PARENT;
		params.width = (int) mResources.getDimension(R.dimen.popwindow_content_width_big);
		selectContainer.setLayoutParams(params);
	}
	
	private void showSelectSex() {
		inputType = INPUT_TYPE_SEX;
		selectContainer.removeAllViews();
		setSelectContainerWidth();
		wheelOneLayout = (View)LayoutInflater.from(this).inflate(R.layout.wheel_one, null);
		selectContainer.addView(wheelOneLayout);
		tvPopTitle.setText(R.string.select_weight);
		
		wheelOneWheelView = (WheelView)wheelOneLayout.findViewById(R.id.select_wheelview);
		wheelOneWheelView.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, item = " + item);
				strHeight = item;
			}
		});
		wheelOneWheelView.setTextSize(24);
		wheelOneWheelView.setOffset(1);
		wheelOneWheelView.setSeletion(0);
		wheelOneWheelView.setItems(AccountMgr.getInstance().getSexList());
		strSex = AccountMgr.getInstance().getSexList().get(0);
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}
	
}
