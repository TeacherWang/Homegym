package com.runrunfast.homegym.account;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;

public class UserInfo {
	private static final String SDCARD_ROOT = Environment .getExternalStorageDirectory().getPath();
	
	public static final String IMAGE_FILE_DIR = SDCARD_ROOT + File.separator
			+ "homegym" + File.separator + MediaStore.MEDIA_IGNORE_FILENAME + File.separator;
	public static final String IMG_FILE_NAME = "headimg.png";
	public static final String IMAGE_FILE_LOCATION = IMAGE_FILE_DIR + IMG_FILE_NAME;
	
	private static final String IMAGE_FILE_LOCATION_TEMP = SDCARD_ROOT + File.separator
			+ "homegym" + File.separator + MediaStore.MEDIA_IGNORE_FILENAME + File.separator + "temp_headimg.png";
	public static final Uri IMAGE_URI = Uri.parse(IMAGE_FILE_LOCATION_TEMP);// The Uri to store
	
	public static final int REQ_CAMERA 	= 0;
	public static final int REQ_ALBUM	= 1;
	public static final int REQ_ZOOM	= 2;
	
	public String strAccountId;
	public String strNickName;
	public String strSex;
	public String strBirthday;
	public String strWeight;
	public String strHeight;
}
