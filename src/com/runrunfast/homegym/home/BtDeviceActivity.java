package com.runrunfast.homegym.home;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.BtDevice.BtDeviceAdapter;
import com.runrunfast.homegym.BtDevice.BtInfo;
import com.runrunfast.homegym.widget.DialogActivity;

import java.util.ArrayList;

public class BtDeviceActivity extends Activity{
	private final String TAG = "BtDeviceActivity";
	
	private static final int REQ_CODE = 1;
	
	private TextView tvTitle;
	private Button btnBack, btnUnbind;
	private ListView mDeviceListView;
	private LinearLayout llBtAllDevicesLayout;
	
	private BtDeviceAdapter mBtDeviceAdapter;
	private ArrayList<BtInfo> mBtInfoList;
	
	private Resources mResources;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_btdevice);
		
		mResources = getResources();
		
		initView();
		
		initData();
	}

	private void initData() {
		tvTitle.setText(R.string.bluetooth);
		
		mBtInfoList = new ArrayList<BtInfo>();
		
		BtInfo btInfo1 = new BtInfo();
		btInfo1.btName = "设备1";
		btInfo1.btAddress = "aaa";
		mBtInfoList.add(btInfo1);
		
		BtInfo btInfo2 = new BtInfo();
		btInfo2.btName = "设备2";
		btInfo2.btAddress = "bbb";
		mBtInfoList.add(btInfo2);
		
		BtInfo btInfo3 = new BtInfo();
		btInfo3.btName = "设备3";
		btInfo3.btAddress = "ccc";
		mBtInfoList.add(btInfo3);
		
		mBtDeviceAdapter = new BtDeviceAdapter(this, mBtInfoList);
		mDeviceListView.setAdapter(mBtDeviceAdapter);
	}

	private void initView() {
		tvTitle = (TextView)findViewById(R.id.actionbar_title);
		findViewById(R.id.actionbar_left_btn).setBackgroundResource(R.drawable.nav_back);
		
		btnUnbind = (Button)findViewById(R.id.btn_cancel_pair);
		
		mDeviceListView = (ListView)findViewById(R.id.bt_device_listview);
		
		llBtAllDevicesLayout = (LinearLayout)findViewById(R.id.bt_all_devices_layout);
	}
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.actionbar_left_btn:
			handleClickLeft();
			break;
			
		case R.id.btn_cancel_pair:
			showUnbindDialog();
			break;

		default:
			break;
		}
	}

	private void showUnbindDialog() {
		Intent intent = new Intent(this, DialogActivity.class);
		intent.putExtra(DialogActivity.KEY_CONTENT, mResources.getString(R.string.ask_unbind));
		intent.putExtra(DialogActivity.KEY_CANCEL, mResources.getString(R.string.no));
		intent.putExtra(DialogActivity.KEY_CONFIRM, mResources.getString(R.string.yes));
		startActivityForResult(intent, REQ_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode != REQ_CODE){
			Log.e(TAG, "requestCode = " + requestCode + ", not send requestCode");
			return;
		}
		
		Log.d(TAG, "resultCode = " + resultCode);
		
	}

	private void handleClickLeft() {
		finish();
	}
}
