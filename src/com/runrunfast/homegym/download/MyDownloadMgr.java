package com.runrunfast.homegym.download;

import android.util.Log;

import com.golshadi.majid.core.DownloadManagerPro;
import com.golshadi.majid.report.ReportStructure;
import com.golshadi.majid.report.listener.DownloadManagerListener;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.utils.ConstServer;
import com.runrunfast.homegym.utils.FileUtils;
import com.runrunfast.homegym.utils.Globle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MyDownloadMgr {
	private final String TAG = "MyDownloadMgr";
	
	public static final int DOWNLOAD_STATE_INIT = 1;
	public static final int DOWNLOAD_STATE_DOWNLOADING = 2;
	public int state = DOWNLOAD_STATE_INIT;
	
	private volatile static MyDownloadMgr instance;
	private static Object lockObject = new Object();
	
	private DownloadManagerPro mDownloadManagerPro;
	
	private ArrayList<HashMap<String, ArrayList<String>>> mActionUrlHashMapList;
	private ArrayList<String> mCurrentActionUrlList;
	private int mRemainTaskSize; // 剩余任务数
	private int mTotalTaskSize; // 总任务数
	private int mFinishedTaskNum;
	private int mTotalPercent;
	private int mCurrentTaskId;
	
	private int mPreProgress;
	
	private HashSet<IDownloadListener> mSetOfIDownloadObserver;
	private String mCourseId;
	private String mCurrentActionId;

	public interface IDownloadListener{
		void onDownloadStart(String courseId, int finishNum, int totalNum);
		void onDownloadProgress(String courseId, int percent);
		void onDownloadFinished(String courseId, int finishNum, int totalNum);
		void onDownloadComplete(String courseId);
		void onDownloadErr(String courseId);
		void onConnectionLost();
	}
	
	public void addOnIDownloadObserver(IDownloadListener iDownloadListener){
		synchronized (mSetOfIDownloadObserver) {
			if( mSetOfIDownloadObserver.contains(iDownloadListener) == false )
				mSetOfIDownloadObserver.add(iDownloadListener);
		}
	}
	
	public void removeIDownloadObserver(IDownloadListener iDownloadListener){
		synchronized (mSetOfIDownloadObserver) {
			mSetOfIDownloadObserver.remove(iDownloadListener);
		}
	}
	
	public synchronized int getState(){
		return state;
	}
	
	public String getCurrentCourseId(){
		return mCourseId;
	}
	
	public static MyDownloadMgr getInstance() {
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new MyDownloadMgr();
				}
			}
		}
		return instance;
	}
	
	private MyDownloadMgr(){
		initDownload();
		
		mSetOfIDownloadObserver = new HashSet<MyDownloadMgr.IDownloadListener>();
	}

	private void initDownload() {
		mActionUrlHashMapList = new ArrayList<HashMap<String,ArrayList<String>>>();
		mCurrentActionUrlList = new ArrayList<String>();
		mDownloadManagerPro = new DownloadManagerPro(Globle.gApplicationContext);
		
		mDownloadManagerPro.init(DownloadConst.SDCARD_FOLDER_ADDRESS, 2, new DownloadManagerListener() {
			
			@Override
			public void onDownloadProcess(long taskId, double percent,
					long downloadedLength) {
//				int currentProgress = (int) (percent / mTaskSize);
//				
//				if(currentProgress == mPreProgress){
//					return;
//				}
//				
//				mPreProgress = currentProgress;
//				
//				mTotalPercent = mTotalPercent + currentProgress;
//				Log.i(TAG, "onDownloadProcess, taskId = " + taskId + ", percent = " + percent + ", downloadedLength = " + downloadedLength + ", progress = " + currentProgress + ", mTotalPercent = " + mTotalPercent);
				
				int progress = (int) percent;
				
				notifyDownloadProgress(mCourseId, progress);
			}
			
			@Override
			public void connectionLost(long taskId) {
				Log.i(TAG, "connectionLost, taskId = " + taskId);
				
				synchronized (lockObject) {
					state = DOWNLOAD_STATE_INIT;
				}
				
				notifyConnectionLost();
			}
			
			@Override
			public void OnDownloadStarted(long taskId) {
				Log.i(TAG, "OnDownloadStarted, taskId = " + taskId);
				
				synchronized (lockObject) {
					state = DOWNLOAD_STATE_DOWNLOADING;
				}
				
				notifyDownloadStart(mCourseId, mFinishedTaskNum, mTotalTaskSize);
			}
			
			@Override
			public void OnDownloadRebuildStart(long taskId) {
				Log.i(TAG, "OnDownloadRebuildStart, taskId = " + taskId);
			}
			
			@Override
			public void OnDownloadRebuildFinished(long taskId) {
				Log.i(TAG, "OnDownloadRebuildFinished, taskId = " + taskId);
			}
			
			@Override
			public void OnDownloadPaused(long taskId) {
				Log.i(TAG, "OnDownloadPaused, taskId = " + taskId);
				
				synchronized (lockObject) {
					state = DOWNLOAD_STATE_INIT;
				}
			}
			
			@Override
			public void OnDownloadFinished(long taskId) {
				Log.i(TAG, "OnDownloadFinished, taskId = " + taskId);
				
				notifyDownloadFinished(mCourseId, mFinishedTaskNum, mTotalTaskSize);
			}
			
			@Override
			public void OnDownloadCompleted(long taskId) {
				Log.i(TAG, "OnDownloadCompleted, taskId = " + taskId);
				if( checkComplete() ){
					synchronized (lockObject) {
						state = DOWNLOAD_STATE_INIT;
					}
					notifyDownloadComplete(mCourseId);
					clearUrlList();
					return;
				}
				
				continueDownload();
			}
		});
	}
	
	private boolean checkComplete() {
		mRemainTaskSize--;
		mFinishedTaskNum++;
		if(mRemainTaskSize <= 0){
			return true;
		}
		
		return false;
	}
	
	private void notifyDownloadStart(String courseId, int finishNum, int totalNum) {
		synchronized (mSetOfIDownloadObserver) {
			Iterator<IDownloadListener> it = mSetOfIDownloadObserver.iterator();
			while( it.hasNext() ){
				IDownloadListener observer = it.next();
				observer.onDownloadStart(courseId, finishNum, totalNum);
			}	
		}
	}
	
	private void notifyDownloadProgress(String courseId, int percent) {
		synchronized (mSetOfIDownloadObserver) {
			Iterator<IDownloadListener> it = mSetOfIDownloadObserver.iterator();
			while( it.hasNext() ){
				IDownloadListener observer = it.next();
				observer.onDownloadProgress(courseId, percent);
			}
		}
	}
	
	private void notifyDownloadFinished(String courseId, int finishNum, int totalNum) {
		synchronized (mSetOfIDownloadObserver) {
			Iterator<IDownloadListener> it = mSetOfIDownloadObserver.iterator();
			while( it.hasNext() ){
				IDownloadListener observer = it.next();
				observer.onDownloadFinished(courseId, finishNum, totalNum);
			}
		}
	}
	
	private void notifyDownloadComplete(String courseId) {
		synchronized (mSetOfIDownloadObserver) {
			Iterator<IDownloadListener> it = mSetOfIDownloadObserver.iterator();
			while( it.hasNext() ){
				IDownloadListener observer = it.next();
				observer.onDownloadComplete(courseId);
			}
		}
	}
	
	private void notifyDownloadStart(String courseId) {
		synchronized (mSetOfIDownloadObserver) {
			Iterator<IDownloadListener> it = mSetOfIDownloadObserver.iterator();
			while( it.hasNext() ){
				IDownloadListener observer = it.next();
				observer.onDownloadErr(courseId);
			}	
		}
	}
	
	private void notifyConnectionLost() {
		synchronized (mSetOfIDownloadObserver) {
			Iterator<IDownloadListener> it = mSetOfIDownloadObserver.iterator();
			while( it.hasNext() ){
				IDownloadListener observer = it.next();
				observer.onConnectionLost();
			}	
		}
	}
	
	public void addDownloadUrlList(ArrayList<HashMap<String, ArrayList<String>>> actionUrlHashMapList, ArrayList<String> urlList){
		mActionUrlHashMapList = actionUrlHashMapList;
		
		mTotalTaskSize = urlList.size();
		mRemainTaskSize = mTotalTaskSize;
	}
	
	public void clearUrlList(){
		mActionUrlHashMapList.clear();
		mCurrentActionUrlList.clear();
		mTotalTaskSize = 0;
		mRemainTaskSize = 0;
		mFinishedTaskNum = 0;
		mTotalPercent = 0;
	}

	public void startDownload(String courseId){
		if(mRemainTaskSize <= 0){
			Log.e(TAG, "startDownload, mTaskSize <= 0");
			return;
		}
		
		mCourseId = courseId;
		
		HashMap<String, ArrayList<String>> currentActionUrlMap = mActionUrlHashMapList.get(0);
		mCurrentActionId = currentActionUrlMap.keySet().iterator().next();
		mCurrentActionUrlList = currentActionUrlMap.get(mCurrentActionId);
		
		String strUrl = mCurrentActionUrlList.get(0);
		Log.i(TAG, "startDownload, mTotalTaskSize " + mTotalTaskSize + " mCurrentActionId = " + mCurrentActionId
				+ ", strUrl = " + strUrl
				+ ", mCurrentActionUrlList size = " + mCurrentActionUrlList.size());
		
		try {
			String saveName = FileUtils.getFileName(strUrl);
			String saveNameWithoutExtension = FileUtils.getFileNameWithoutExtension(strUrl);
			String localAddress = ConstServer.SDCARD_HOMEGYM_ROOT + saveName;
//			ActionDao.getInstance().saveActionAudioLocalToDb(Globle.gApplicationContext, mCurrentActionId, localAddress);
			
			mCurrentTaskId = mDownloadManagerPro.addTask(saveNameWithoutExtension, strUrl, true, true);
			mDownloadManagerPro.startDownload(mCurrentTaskId);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "startDownload, exception, set state to init : " + e);
			synchronized (lockObject) {
				state = DOWNLOAD_STATE_INIT;
			}
		}
	}
	
	public void continueDownload(){
		String strUrl = "";
		mCurrentActionUrlList.remove(0);
		if(mCurrentActionUrlList.size() <= 0){
			mActionUrlHashMapList.remove(0);
			
			HashMap<String, ArrayList<String>> currentActionUrlMap = mActionUrlHashMapList.get(0);
			mCurrentActionId = currentActionUrlMap.keySet().iterator().next();
			mCurrentActionUrlList = currentActionUrlMap.get(mCurrentActionId);
			
			strUrl = mCurrentActionUrlList.get(0);
		}else{
			strUrl = mCurrentActionUrlList.get(0);
		}
		
		Log.i(TAG, "continueDownload, mCurrentActionId = " + mCurrentActionId
				+ ", strUrl = " + strUrl
				+ ", mCurrentActionUrlList size = " + mCurrentActionUrlList.size());
		
		try {
			String saveName = FileUtils.getFileName(strUrl);
			String saveNameWithoutExtension = FileUtils.getFileNameWithoutExtension(strUrl);
			String localAddress = ConstServer.SDCARD_HOMEGYM_ROOT + saveName;
//			ActionDao.getInstance().saveActionAudioLocalToDb(Globle.gApplicationContext, mCurrentActionId, localAddress);
			
			mCurrentTaskId = mDownloadManagerPro.addTask(saveNameWithoutExtension, strUrl, true, true);
			mDownloadManagerPro.startDownload(mCurrentTaskId);
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "continueDownload, exception, set state to init : " + e);
			synchronized (lockObject) {
				state = DOWNLOAD_STATE_INIT;
			}
		}
	}
	
	public int addTask(String saveName, String url, int chunk,
            String sdCardFolderAddress, boolean overwrite,
            boolean priority){
		return mDownloadManagerPro.addTask(saveName, url, chunk, sdCardFolderAddress, overwrite, priority);
	}
	
	public int addTask(String saveName, String url, int chunk, boolean overwrite, boolean priority){
        return mDownloadManagerPro.addTask(saveName, url, chunk, overwrite, priority);
    }

    public int addTask(String saveName, String url, boolean overwrite, boolean priority) {
        return mDownloadManagerPro.addTask(saveName, url, overwrite, priority);
    }
    
    public void startDownload(int token) throws IOException {
    	mDownloadManagerPro.startDownload(token);
    }
    
    public void pauseDownload() {
    	pauseDownload(mCurrentTaskId);
    }
    
    public void pauseDownload(int token) {
    	if(state == DOWNLOAD_STATE_DOWNLOADING){
    		mDownloadManagerPro.pauseDownload(token);
    	}
    }
    
    public void pauseQueueDownload(){
    	if(state == DOWNLOAD_STATE_DOWNLOADING){
    		mDownloadManagerPro.pauseQueueDownload();
    	}
    }
    
    public boolean delete(int token, boolean deleteTaskFile){
    	return mDownloadManagerPro.delete(token, deleteTaskFile);
    }
    
    /**
     * return list of last completed Download tasks in "ReportStructure" style
     * you can use it as notifier
     *
     * @return
     */
    public List<ReportStructure> lastCompletedDownloads(){
    	return mDownloadManagerPro.lastCompletedDownloads();
    }
    
}
