package com.runrunfast.homegym.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.runrunfast.homegym.account.UserInfo;

public class CameraUtils {
	
	public static void selectImageFromCamera(Activity activity) {
		FileUtils.deleteFile(Environment.getExternalStorageDirectory() + UserInfo.CAMERA_IMG_NAME);
		
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, UserInfo.CAMERA_IMG_URI);
		activity.startActivityForResult(intent, UserInfo.REQ_CAMERA);
	}
	
	public static void selectImageFromLocal(Activity activity) {
		FileUtils.deleteFile(Environment.getExternalStorageDirectory() + UserInfo.CAMERA_IMG_NAME);
		
		Intent intent = new Intent(Intent.ACTION_PICK,null);
        //此处调用了图片选择器
        //如果直接写intent.setDataAndType("image/*");
        //调用的是系统图库
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, UserInfo.CAMERA_IMG_URI);
        activity.startActivityForResult(intent, UserInfo.REQ_ALBUM);
	}
	
	public static void startPhotoZoom(Uri uri, Activity activity) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, UserInfo.CAMERA_IMG_URI);
		
        activity.startActivityForResult(intent, UserInfo.REQ_ZOOM);
	}
}
