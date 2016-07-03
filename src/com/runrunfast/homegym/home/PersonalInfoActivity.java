package com.runrunfast.homegym.home;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.widget.PopupWindows;
import com.runrunfast.homegym.widget.WheelView;
import com.runrunfast.homegym.widget.WheelView.OnWheelViewListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PersonalInfoActivity extends Activity implements OnClickListener{
	private final String TAG = "PersonalInfoActivity";
	
	private static final int INPUT_TYPE_SEX = 1;
	private static final int INPUT_TYPE_BIRTH = 2;
	private static final int INPUT_TYPE_WEIGHT = 3;
	private static final int INPUT_TYPE_HEIGHT = 4;
	
	private int inputType;
	
	private RelativeLayout popView;
	private RelativeLayout selectContainer;
	private PopupWindows popWindows;
	private TextView tvPopTitle, tvPopConfirm;
	
	private View wheelOneLayout;
	private WheelView wheelOneWheelView;
	
	private TextView tvNick, tvSex, tvBirth, tvCity, tvHeight, tvWeight, tvBmiNum, tvBmiDescip;
	
	private String strSex;
	private String strBirth;
	private String strWeight;
	private String strHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info);
		initView();
		
		initData();
	}

	private void initData() {
		
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
			tvBirth.setText(strBirth);
			break;

		default:
			break;
		}
	}
	
	private void showSelectHeight() {
		inputType = INPUT_TYPE_HEIGHT;
		selectContainer.removeAllViews();
		wheelOneLayout = (View)LayoutInflater.from(this).inflate(R.layout.wheel_one, null);
		selectContainer.addView(wheelOneLayout);
		tvPopTitle.setText(R.string.select_height);
		
		wheelOneWheelView = (WheelView)wheelOneLayout.findViewById(R.id.select_wheelview);
		wheelOneWheelView.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, item = " + item);
				strWeight = item;
			}
		});
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
		wheelOneWheelView.setOffset(1);
		wheelOneWheelView.setSeletion(20);
		wheelOneWheelView.setItems(AccountMgr.getInstance().getWeightList());
		strWeight = AccountMgr.getInstance().getWeightList().get(20);
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}

	private void showSelectBirth() {
		
	}
	private void showSelectSex() {
		inputType = INPUT_TYPE_SEX;
		selectContainer.removeAllViews();
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
		wheelOneWheelView.setOffset(1);
		wheelOneWheelView.setSeletion(0);
		wheelOneWheelView.setItems(AccountMgr.getInstance().getSexList());
		strSex = AccountMgr.getInstance().getSexList().get(0);
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}
	
}
