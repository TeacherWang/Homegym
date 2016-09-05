package com.runrunfast.homegym.utils;

import android.media.AudioManager;
import android.util.Log;

public class MusicVolumeController {

    private final String TAG = getClass().getSimpleName();
    private final String TAG_MARK = "MUSIC-VOLUME-TEST: ";
    private final int VOLUME_TOTAL_LEVEL = 10;
	public static MusicVolumeController mController;
    private AudioManager mAudioManager;
    private double mMaxVolume;
    private double mVolumeSection;

	
	public static MusicVolumeController getInstance() {
		if (null == mController) {
			mController = new MusicVolumeController();
		}
		return mController;
	}
	
	private MusicVolumeController() {
        init();
	}

    public void init() {
        mAudioManager = (AudioManager) Globle.gApplicationContext.getSystemService(Globle.gApplicationContext.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolumeSection = mMaxVolume / VOLUME_TOTAL_LEVEL;
        log("MusicVolumeController, mMaxVolume=" + mMaxVolume);
    }

    public void increaseVolume() {
        double curVolume = getCurVolume();
        curVolume += mVolumeSection;
        if (curVolume > mMaxVolume) {
            curVolume = mMaxVolume;
        }
        setCurVolume((int) curVolume);
    }

    public void decreaseVolume() {
        double curVolume = getCurVolume();
        curVolume -= mVolumeSection;
        if (curVolume < 0) {
            curVolume = 0;
        }
        setCurVolume((int) curVolume);
    }

    /**
     * 0 ~ 10
     * @param level
     */
    public void setLevelVolume(int level) {
        int curVolume = (int) (level * mVolumeSection);
        log("setLevelVolume, level=" + level + ", curVolume=" + curVolume);
        setCurVolume(curVolume);
    }

    public int getLevelVolume() {
        int curVolume = getCurVolume();
        int level = (int) (VOLUME_TOTAL_LEVEL * (curVolume / mMaxVolume));
//        log("getLevelVolume, level=" + level + ", curVolume=" + curVolume);
        log("getLevelVolume, level=" + level + ", curVolume=" + curVolume + ", maxVolume=" + getMaxVolume());
        return level;
    }

    public int getCurVolume() {
        int curVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        log("getCurVolume, curVolume=" + curVolume);
        return curVolume;
    }

    public void setCurVolume(int volume) {
        log("setCurVolume, curVolume=" + volume);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);
    }

    public int getMaxVolume() {
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        log("getMaxVolume, maxVolume=" + maxVolume);
        return maxVolume;
    }

    private void log(String text) {
        Log.d(TAG, TAG_MARK + text);
    }
}
