package com.runrunfast.homegym.download;

import android.util.Log;

import com.golshadi.majid.core.DownloadManagerPro;
import com.golshadi.majid.report.ReportStructure;
import com.golshadi.majid.report.listener.DownloadManagerListener;
import com.runrunfast.homegym.utils.FileUtils;
import com.runrunfast.homegym.utils.Globle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyDownloadMgr {
	private final String TAG = "MyDownloadMgr";
	
	private volatile static MyDownloadMgr instance;
	private static Object lockObject = new Object();
	
	private DownloadManagerPro mDownloadManagerPro;
	
	private ArrayList<Integer> taskIdList;

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
	}

	private void initDownload() {
		taskIdList = new ArrayList<Integer>();
		mDownloadManagerPro = new DownloadManagerPro(Globle.gApplicationContext);
		
		mDownloadManagerPro.init(DownloadConst.SDCARD_FOLDER_ADDRESS, 2, new DownloadManagerListener() {
			
			@Override
			public void onDownloadProcess(long taskId, double percent,
					long downloadedLength) {
				Log.i(TAG, "onDownloadProcess, taskId = " + taskId + ", percent = " + percent + ", downloadedLength = " + downloadedLength);
			}
			
			@Override
			public void connectionLost(long taskId) {
				Log.i(TAG, "connectionLost, taskId = " + taskId);
			}
			
			@Override
			public void OnDownloadStarted(long taskId) {
				Log.i(TAG, "OnDownloadStarted, taskId = " + taskId);
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
			}
			
			@Override
			public void OnDownloadFinished(long taskId) {
				Log.i(TAG, "OnDownloadFinished, taskId = " + taskId);
			}
			
			@Override
			public void OnDownloadCompleted(long taskId) {
				Log.i(TAG, "OnDownloadCompleted, taskId = " + taskId);
			}
		});
//		int taskId = mDownloadManagerPro.addTask("ZX1/" + FileUtils.getFileName("http://trainingfile.gz.bcebos.com/action_video_file/ZX1_V1%20848X476.mp4"), "http://trainingfile.gz.bcebos.com/action_video_file/ZX1_V1%20848X476.mp4", true, true);
//		try {
//			mDownloadManagerPro.startDownload(taskId);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
	
	
	
	public void addDownloadUrlList(String actionId, ArrayList<String> urlList){
		int urlSize = urlList.size();
		for(int i=0; i<urlSize; i++){
			String strUrl = urlList.get(i);
			int taskId = mDownloadManagerPro.addTask(actionId + File.separator + FileUtils.getFileName(strUrl), strUrl, true, true);
			taskIdList.add(taskId);
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
    
    public void pauseDownload(int token) {
    	mDownloadManagerPro.pauseDownload(token);
    }
    
    public void pauseQueueDownload(){
    	mDownloadManagerPro.pauseQueueDownload();
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
