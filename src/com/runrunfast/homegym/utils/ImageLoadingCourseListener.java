package com.runrunfast.homegym.utils;

import android.accounts.Account;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.dao.CourseDao;
import com.runrunfast.homegym.dao.MyCourseDao;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImageLoadingCourseListener extends SimpleImageLoadingListener {
	private final String TAG = "ImageLoadingCourseListener";
	
	private String mCourseId;
	private String mFilePath;
	private Handler mWorkHandler;
	private HandlerThread mHandlerThread;
	
	
	public ImageLoadingCourseListener(String filePath, String courseId){
		this.mCourseId = courseId;
		mFilePath = filePath;
		
		mHandlerThread = new HandlerThread(TAG);
		mHandlerThread.start();
		mWorkHandler = new Handler(mHandlerThread.getLooper());
	}
	
	public static final List<String> displayedImages = Collections
			.synchronizedList(new LinkedList<String>());

	@Override
	public void onLoadingFailed(String imageUri, View view,
			FailReason failReason) {

		ImageLoader.getInstance().cancelDisplayTask((ImageView) view);
		super.onLoadingFailed(imageUri, view, failReason);
	}

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		if (loadedImage != null) {
			ImageView imageView = (ImageView) view;
			boolean firstDisplay = !displayedImages.contains(imageUri);
			if (firstDisplay) {
				FadeInBitmapDisplayer.animate(imageView, 0);
				displayedImages.add(imageUri);
			}
			saveBitmap(loadedImage, mFilePath, mCourseId);
		}
	}

	private void saveBitmap(final Bitmap loadedImage, final String filePath, final String courseId) {
		mWorkHandler.post(new Runnable() {
			
			@Override
			public void run() {
				BitmapUtils.saveBitmapToSDcard(loadedImage, filePath);
				CourseDao.getInstance().saveCourseImgLocalToDb(Globle.gApplicationContext, courseId, filePath);
				MyCourseDao.getInstance().saveMyCourseImgLocalToDb(Globle.gApplicationContext, AccountMgr.getInstance().mUserInfo.strAccountId, courseId, filePath);
			}
		});
	}
}
