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
	public static final String IMG_FILE_NAME_TEMP = "tempheadimg.png";
	public static final String IMAGE_FILE_LOCATION = IMAGE_FILE_DIR + IMG_FILE_NAME;
	public static final String IMAGE_FILE_LOCATION_TEMP = IMAGE_FILE_DIR + IMG_FILE_NAME_TEMP; // 用来临时表示更换头像还未上传成功前的路径
	
	private static final String IMAGE_FILE_LOCATION_CAMERA_TEMP = "file:///sdcard/temp.jpg";
	public static final Uri CAMERA_IMG_URI = Uri.parse(IMAGE_FILE_LOCATION_CAMERA_TEMP);// The Uri to store
	
	public static final int REQ_CAMERA 	= 0;
	public static final int REQ_ALBUM	= 1;
	public static final int REQ_ZOOM	= 2;
	
	public static final String SEX_MAN = "男";
	public static final String SEX_WOMAN = "女";
	
	public String strAccountId;
	public String strNickName;
	public String strSex;
	public String strBirthday;
	public String strWeight;
	public String strHeight;
	public String strCity;
}
