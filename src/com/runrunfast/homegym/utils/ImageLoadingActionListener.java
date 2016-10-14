package com.runrunfast.homegym.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.runrunfast.homegym.dao.ActionDao;

public class ImageLoadingActionListener extends SimpleImageLoadingListener {
	private final String TAG = "ImageLoadingActionListener";
	
	private String mActionId;
	private String mFilePath;
	private Handler mWorkHandler;
	private HandlerThread mHandlerThread;
	
	
	public ImageLoadingActionListener(String filePath, String actionId){
		this.mActionId = actionId;
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
			saveBitmap(loadedImage, mFilePath, mActionId);
		}
	}

	private void saveBitmap(final Bitmap loadedImage, final String filePath, final String actionId) {
		mWorkHandler.post(new Runnable() {
			
			@Override
			public void run() {
				BitmapUtils.saveBitmapToSDcard(loadedImage, filePath);
				ActionDao.getInstance().saveActionImgLocalToDb(Globle.gApplicationContext, actionId, filePath);
			}
		});
	}
}
