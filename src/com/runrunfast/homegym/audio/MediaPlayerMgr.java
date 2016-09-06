package com.runrunfast.homegym.audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;

public class MediaPlayerMgr {
	private final String TAG = "MediaPlayerMgr";
	
	private volatile static MediaPlayerMgr instance;
	private static Object lockObject = new Object();
	
	private MediaPlayer mPlayer = null;
	
	private HashSet<IMediaListener> mSetOfMediaObserver;
	
	public interface IMediaListener{
		void onCompletion();
	}
	
	public static MediaPlayerMgr getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new MediaPlayerMgr();
				}
			}
		}
		return instance;
	}
	
	private MediaPlayerMgr(){
		mSetOfMediaObserver = new HashSet<MediaPlayerMgr.IMediaListener>();
	}
	
	public void addMediaPlayerObserver(IMediaListener iMediaListener){
		synchronized (mSetOfMediaObserver) {
			if( !mSetOfMediaObserver.contains(iMediaListener) ){
				mSetOfMediaObserver.add(iMediaListener);
			}
		}
	}
	
	public void removeMediaPlayerObserver(IMediaListener iMediaListener){
		synchronized (mSetOfMediaObserver) {
			mSetOfMediaObserver.remove(iMediaListener);
		}
	}
	
    /**
      * @Method: startPlaying
      * @Description: 开始播放sdcard语音文件
      * @param fileName	
      * 返回类型：void 
      */
    public void startPlaying(String fileName){
    	if(mPlayer == null){
    		mPlayer = new MediaPlayer();
    	}
    	
    	if(mPlayer.isPlaying()){
    		stopPlaying();
    	}
    	
        try {
            mPlayer.setDataSource(fileName);
            mPlayer.prepare();
            mPlayer.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					Log.i(TAG, "start play audio!");
					mPlayer.start();
				}
			});
            
            mPlayer.setOnCompletionListener(new OnCompletionListener() {
                
                @Override
                public void onCompletion(MediaPlayer mp) {
                	Log.i(TAG, "onCompletion!");
                	
                	notifyMediaPlayCompletion();
                	
                    stopPlaying();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "mPalyer.prepare() failed");
        }
    }
    
    public boolean isPlaying(){
    	if(mPlayer != null && mPlayer.isPlaying()){
    		return true;
    	}
    	return false;
    }
    
    /**
      * @Method: startPlaying
      * @Description: 开始播放raw下的语音文件
      * @param context
      * @param resid	
      * 返回类型：void 
      */
    public void startPlaying(Context context, int resid){
    	if(mPlayer != null && mPlayer.isPlaying()){
    		stopPlaying();
    	}
    	
    	mPlayer = MediaPlayer.create(context, resid);
    	
        try {
        	mPlayer.start();
            mPlayer.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					Log.i(TAG, "start play audio!");
					mPlayer.start();
				}
			});
            
            mPlayer.setOnCompletionListener(new OnCompletionListener() {
                
                @Override
                public void onCompletion(MediaPlayer mp) {
                	Log.i(TAG, "onCompletion!");
                	
                	notifyMediaPlayCompletion();
                	
                    stopPlaying();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "mPalyer.prepare() failed");
        }
    }
    
    public void pause(){
    	if(mPlayer != null && mPlayer.isPlaying()){
    		mPlayer.pause();
    	}
    }
    
    public void resume(){
    	if(mPlayer != null){
    		mPlayer.start();
    	}
    }
    
    /**
      * @Method: stopPlaying
      * @Description: 停止播放	
      * 返回类型：void 
      */
    public void stopPlaying(){
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            Log.i(TAG, "stopPlaying");
        }
        mPlayer = null;
    }
    
    private void notifyMediaPlayCompletion(){
    	synchronized (mSetOfMediaObserver) {
    		Iterator<IMediaListener> it = mSetOfMediaObserver.iterator();
			while( it.hasNext() ){
				IMediaListener observer = it.next();
				observer.onCompletion();
			}
		}
    }
    
}
