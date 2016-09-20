package com.runrunfast.homegym.BtDevice;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.runrunfast.ble.BLEService;
import com.runrunfast.ble.BLEService.LocalBinder;
import com.runrunfast.ble.BLESingleton;
import com.runrunfast.ble.MotionCallback;
import com.runrunfast.homegym.utils.Globle;
import com.runrunfast.homegym.utils.PrefUtils;

import java.util.ArrayList;

public class BtDeviceMgr {
	private final String TAG = "BtDeviceMgr";
	
	private static Object lockObject = new Object();
	private volatile static BtDeviceMgr instance;
	private boolean isConnected = false;
	
	private ArrayList<BLEServiceListener> mBleServiceObserver;
	
	public interface BLEServiceListener{
		void onBLEInit();
		void onReedSwitch();
		void onGetDevice(BluetoothDevice btDevice);
		void onDeviceConnected();
		void onDeviceDisconnected();
		void onBTOpen(boolean isOpened);
	}
	
	public static BtDeviceMgr getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new BtDeviceMgr();
				}
			}
		}
		return instance;
	}
	
	private BtDeviceMgr(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		filter.addAction(BLESingleton.mBLEService.ACTION_STATE_CONNECTED);
		filter.addAction(BLESingleton.mBLEService.ACTION_STATE_DISCONNECTED);
		Globle.gApplicationContext.registerReceiver(mReceiver, filter);
		
		mBleServiceObserver = new ArrayList<BtDeviceMgr.BLEServiceListener>();
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
				handleBtState(intent);
			}else if(intent.getAction().equals(BLESingleton.mBLEService.ACTION_STATE_CONNECTED)){
				Log.i(TAG, "notifyDeviceConnected");
				
				BLESingleton.mBLEService.setConnectting(false);
				isConnected = true;
				notifyDeviceConnected();
			}else if(intent.getAction().equals(BLESingleton.mBLEService.ACTION_STATE_DISCONNECTED)){
				Log.i(TAG, "notifyDeviceDisconnected");
				
				BLESingleton.mBLEService.setMdevice(null);
				isConnected = false;
				notifyDeviceDisconnected();
			}
		}
	};
	
	private void handleBtState(Intent intent) {
		int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
		Log.i(TAG, "handleBtState, state = " + state);
		if(state == BluetoothAdapter.STATE_ON){
//			startScan();
			notifyBTOpen(true);
		}else{
			isConnected = false;
			notifyBTOpen(false);
		}
	}
	
	private void notifyDeviceConnected() {
		synchronized (mBleServiceObserver) {
			if(mBleServiceObserver != null){
				for(BLEServiceListener bleServiceListener : mBleServiceObserver){
					bleServiceListener.onDeviceConnected();
				}
			}
		}
		
	}
	
	private void notifyDeviceDisconnected() {
		synchronized (mBleServiceObserver) {
			if(mBleServiceObserver != null){
				for(BLEServiceListener bleServiceListener : mBleServiceObserver){
					bleServiceListener.onDeviceDisconnected();
				}
			}
		}
	}
	
	private void notifyBTOpen(boolean isOpened) {
		synchronized (mBleServiceObserver) {
			if(mBleServiceObserver != null){
				for(BLEServiceListener bleServiceListener : mBleServiceObserver){
					bleServiceListener.onBTOpen(isOpened);
				}
			}
		}
		
	}
	
	private void startScan() {
		Log.i(TAG, "startScan");
		BLESingleton.mBLEService.scanBle();
	}
	
	public void addBLEServiceObserver(BLEServiceListener bleServiceListener){
		synchronized (mBleServiceObserver) {
			if(mBleServiceObserver.contains(bleServiceListener)){
				Log.d(TAG, "addBLEServiceObserver, bleServiceListener exist + " + bleServiceListener);
				return;
			}
			mBleServiceObserver.add(bleServiceListener);
		}
	}
	
	public void removeBLEServiceObserver(BLEServiceListener bleServiceListener){
		synchronized (mBleServiceObserver) {
			mBleServiceObserver.remove(bleServiceListener);
		}
	}
	
	public void bindBLEService(){
		boolean bindResult = Globle.gApplicationContext.bindService(new Intent(Globle.gApplicationContext, BLEService.class), connectionBlue, Context.BIND_AUTO_CREATE);
		
		Log.i(TAG, "bindBLEService, bindResult = " + bindResult);
	}
	
	public void unBindBLEService(){
		Log.i(TAG, "unBindBLEService");
		
		Globle.gApplicationContext.unbindService(connectionBlue);
	}
	
	public void setLastBtInfo(Context context, String btName, String btAddress){
		PrefUtils.setLastConnectedBt(context, btName, btAddress);
	}
	
	public BtInfo getLastBtInfo(Context context){
		return PrefUtils.getLastConnectedBt(context);
	}
	
	public void removeLastBtInfo(Context context){
		PrefUtils.removeLastConnectedBt(context);
	}
	
	public boolean isConnected(){
		return isConnected;
	}
	
	public void connectBLE(BluetoothDevice bluetoothDevice){
		BLESingleton.mBLEService.setMdevice(bluetoothDevice);
		BLESingleton.mBLEService.setConnectting(true);
	}
	
	public void disconnect(){
		BLESingleton.mBLEService.setConnectting(false);
		BLESingleton.mBLEService.disconnect();
	}
	
	public boolean checkBTOpen(){
		if(!BLESingleton.mBLEService.mBluetoothAdapter.isEnabled()){
			Log.d(TAG, "checkBTOpen, bt is closed");
			return false;
		}
		
//		Log.i(TAG, "checkBTOpen, bt is already open, scanBle");
//		BLESingleton.mBLEService.scanBle();
		
		return true;
	}
	
	public void openBT(){
		BLESingleton.mBLEService.mBluetoothAdapter.enable();
	}
	
	private ServiceConnection connectionBlue = new ServiceConnection() {
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "onServiceConnected");
			
			LocalBinder mBinder = (LocalBinder)service;
			BLESingleton.mBLEService = mBinder.getBLEService();
			BLESingleton.mBLEService.SetCallback(motionCallback);
			BLESingleton.mBLEService.scanBle();
			if(BLESingleton.mBLEService.initBle()){
				notifyBLEServierInit();
			}
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected");
		}
	};
	
	MotionCallback motionCallback = new MotionCallback() {
		
		@Override
		public void ReedSwitch() {
			synchronized (mBleServiceObserver) {
				if(mBleServiceObserver != null){
					for(BLEServiceListener bleServiceListener : mBleServiceObserver){
						bleServiceListener.onReedSwitch();
					}
				}
			}
		}
		
		@Override
		public void Press(int arg0) {
			
		}
		
		@Override
		public void GetDevice(BluetoothDevice btDevice) {
			BtInfo lastConnecteDevice = BtDeviceMgr.getInstance().getLastBtInfo(Globle.gApplicationContext);
			if(lastConnecteDevice != null){
				String lastDeviceAddress = lastConnecteDevice.btAddress;
				
				Log.d(TAG, "GetDevice, lastConnecteDevice != null, address = " + lastDeviceAddress + ", scan device address = " + btDevice.getAddress());
				
				if(lastDeviceAddress.equalsIgnoreCase(btDevice.getAddress())){
					BtDeviceMgr.getInstance().connectBLE(btDevice);
//					return;
				}
			}
			
			Log.d(TAG, "GetDevice, btDevice = " + btDevice.getName());
			
			synchronized (mBleServiceObserver) {
				if(mBleServiceObserver != null){
					for(BLEServiceListener bleServiceListener : mBleServiceObserver){
						bleServiceListener.onGetDevice(btDevice);
					}
				}
			}
		}
	};

	private void notifyBLEServierInit() {
		synchronized (mBleServiceObserver) {
			if(mBleServiceObserver != null){
				for(BLEServiceListener bleServiceListener : mBleServiceObserver){
					bleServiceListener.onBLEInit();
				}
			}
		}
	}
	
}
