package com.runrunfast.homegym.course;

import android.os.Handler;
import android.os.HandlerThread;

public class DataIniter {
	private static final String TAG = "DataInit";
	
	private volatile static DataIniter instance;
	private static Object lockObject = new Object();
	
	private HandlerThread mHandlerThread;
	private Handler mWorkHandler;
	
	public static DataIniter getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new DataIniter();
				}
			}
		}
		return instance;
	}
	
	private DataIniter(){
		mHandlerThread = new HandlerThread(TAG);
		mHandlerThread.start();
		mWorkHandler = new Handler(mHandlerThread.getLooper());
	}
}
