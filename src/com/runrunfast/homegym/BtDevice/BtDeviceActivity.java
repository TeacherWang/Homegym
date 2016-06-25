package com.runrunfast.homegym.BtDevice;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.BtDevice.BtDeviceMgr.BLEServiceListener;
import com.runrunfast.homegym.widget.DialogActivity;

public class BtDeviceActivity extends Activity{
	private final String TAG = "BtDeviceActivity";
	
	private static final int REQ_CODE_OPEN_BT = 1;
	private static final int REQ_CODE_UNBIND_BT = 2;
	
	private TextView tvTitle, tvPairedDevice;
	private Button btnBack, btnUnbind;
	private ListView mDeviceListView;
	private LinearLayout llBtAllDevicesLayout;
	
	private BtDeviceAdapter mBtDeviceAdapter;
	private ArrayList<BtInfo> mBtInfoList;
	
	private Resources mResources;
	
	private BLEServiceListener mBLEServiceListener;
	
	private BtInfo mLastBtInfo;
	
//	private BluetoothDevice mLastConnecteDevice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_btdevice);
		
		mResources = getResources();
		
		initView();
		
		initData();
		
		initListener();
	}

	private void initListener() {
		mDeviceListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectDevice(position);
			}
		});
		
		mBLEServiceListener = new BLEServiceListener() {
			
			@Override
			public void onReedSwitch() {
				
			}
			
			@Override
			public void onGetDevice(BluetoothDevice btDevice) {
				handleGetBTDevices(btDevice);
			}
			
			@Override
			public void onBLEInit() {
				checkBtOpen();
			}

			@Override
			public void onDeviceConnected() {
				
			}

			@Override
			public void onDeviceDisconnected() {
				
			}
		};
		BtDeviceMgr.getInstance().setBLEServiceListener(mBLEServiceListener);
	}
	
	private void selectDevice(int position) {
		BtInfo btInfo = mBtInfoList.get(position);
		BtDeviceMgr.getInstance().setLastBtInfo(this, btInfo.btName, btInfo.btAddress);
		mLastBtInfo = btInfo;
		
		llBtAllDevicesLayout.setVisibility(View.INVISIBLE);
		btnUnbind.setVisibility(View.VISIBLE);
		tvPairedDevice.setText(btInfo.btName);
	}

	private void handleGetBTDevices(BluetoothDevice btDevice) {
		if(hasTheBtDevice(btDevice)){
			Log.d(TAG, "handleGetBTDevices, has the btDevice");
			return;
		}
		
//		if(mLastConnecteDevice != null){
//			Log.d(TAG, "handleGetBTDevices, mLastConnecteDevice != null");
//			BtDeviceMgr.getInstance().connectBLE(mLastConnecteDevice);
//			return;
//		}
		
		if(mLastBtInfo != null){
			Log.d(TAG, "handleGetBTDevices, mLastBtInfo != null, do not change list");
			return;
		}
		
		BtInfo btInfo = new BtInfo();
		btInfo.btAddress = btDevice.getAddress();
		btInfo.btName = btDevice.getName();
		
		mBtInfoList.add(btInfo);
		mBtDeviceAdapter.notifyDataSetChanged();
	}
	
	private boolean hasTheBtDevice(BluetoothDevice btDevice){
		String deviceAddress = btDevice.getAddress();
		
		int listSize = mBtInfoList.size();
		
		for(int i=0; i<listSize; i++){
			BtInfo btInfo = mBtInfoList.get(i);
			if(btInfo.btAddress.equalsIgnoreCase(deviceAddress)){
//				mLastConnecteDevice = btDevice;
				return true;
			}
		}
		
		return false;
	}

	private void checkBtOpen() {
		boolean isOpen = BtDeviceMgr.getInstance().checkBTOpen();
		if(!isOpen){
			showOpenBtDialog();
		}
	}
	
	private void showOpenBtDialog() {
		Intent intent = new Intent(this, DialogActivity.class);
		intent.putExtra(DialogActivity.KEY_CONTENT, mResources.getString(R.string.ask_open_bt));
		intent.putExtra(DialogActivity.KEY_CANCEL, mResources.getString(R.string.no));
		intent.putExtra(DialogActivity.KEY_CONFIRM, mResources.getString(R.string.yes));
		startActivityForResult(intent, REQ_CODE_OPEN_BT);
	}

	private void initData() {
		tvTitle.setText(R.string.bluetooth);
		
		mLastBtInfo = BtDeviceMgr.getInstance().getLastBtInfo(this);
		
		if(mLastBtInfo != null){
			tvPairedDevice.setText(mLastBtInfo.btName);
			llBtAllDevicesLayout.setVisibility(View.INVISIBLE);
			btnUnbind.setVisibility(View.VISIBLE);
		}else{
			tvPairedDevice.setText(R.string.bt_last_connected_no);
			llBtAllDevicesLayout.setVisibility(View.VISIBLE);
			btnUnbind.setVisibility(View.INVISIBLE);
		}
		
		mBtInfoList = new ArrayList<BtInfo>();
		
		mBtDeviceAdapter = new BtDeviceAdapter(this, mBtInfoList);
		mDeviceListView.setAdapter(mBtDeviceAdapter);
		
		BtDeviceMgr.getInstance().bindBLEService();
	}

	private void initView() {
		tvTitle = (TextView)findViewById(R.id.actionbar_title);
		findViewById(R.id.actionbar_left_btn).setBackgroundResource(R.drawable.nav_back);
		
		btnUnbind = (Button)findViewById(R.id.btn_cancel_pair);
		
		mDeviceListView = (ListView)findViewById(R.id.bt_device_listview);
		
		llBtAllDevicesLayout = (LinearLayout)findViewById(R.id.bt_all_devices_layout);
		
		tvPairedDevice = (TextView)findViewById(R.id.bt_paired_device);
		
//		tvConnected = (TextView)findViewById(R.id.bt_paired_device_connect);
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
		startActivityForResult(intent, REQ_CODE_UNBIND_BT);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "resultCode = " + resultCode);
		if(requestCode == REQ_CODE_UNBIND_BT){
			handleUnbindDialogResult(resultCode);
		}else if(requestCode == REQ_CODE_OPEN_BT){
			handleOpenBtDialogResult(resultCode);
		}
	}

	private void handleOpenBtDialogResult(int resultCode) {
		if(resultCode == DialogActivity.RSP_CONFIRM){
			BtDeviceMgr.getInstance().openBT();
		}
	}

	private void handleUnbindDialogResult(int resultCode) {
		if(resultCode == DialogActivity.RSP_CONFIRM){
			unbindDevice();
		}
	}

	private void unbindDevice() {
//		BtDeviceMgr.getInstance().disconnect();
		BtDeviceMgr.getInstance().removeLastBtInfo(this);
		mLastBtInfo = null;
		checkBtOpen();
		
		llBtAllDevicesLayout.setVisibility(View.VISIBLE);
		tvPairedDevice.setText(R.string.bt_last_connected_no);
		btnUnbind.setVisibility(View.INVISIBLE);
//		tvConnected.setVisibility(View.INVISIBLE);
	}

	private void handleClickLeft() {
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
		
		BtDeviceMgr.getInstance().removeBLEServiceListener();
		
		BtDeviceMgr.getInstance().unBindBLEService();
	}
}
