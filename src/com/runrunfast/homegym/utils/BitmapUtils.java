package com.runrunfast.homegym.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.os.Handler;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.runrunfast.homegym.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class BitmapUtils {
	
	public static final DisplayImageOptions initCourseImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.home_add)
				.showImageOnFail(R.drawable.home_add)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.resetViewBeforeLoading(true).handler(new Handler())
				.displayer(new RoundedBitmapDisplayer(0)).build();
		return options;
	}
	
	public static final DisplayImageOptions initActionImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.course_action_pic_1)
				.showImageOnFail(R.drawable.course_action_pic_1)
				.cacheInMemory(true).cacheOnDisc(true)
				.resetViewBeforeLoading(true).handler(new Handler())
				.displayer(new RoundedBitmapDisplayer(0)).build();
		return options;
	}
	
	public static void saveBitmapToSDcard(Bitmap bitmap, String strFileDir, String strFileName) {
		try {
			FileOutputStream fileOutputStream = null;
			String sdcardState = Environment.getExternalStorageState();
			if(Environment.MEDIA_MOUNTED.equals(sdcardState)){
				//有sd卡，是否有myImage文件夹
				File fileDir = new File(strFileDir);
				if(!fileDir.exists()){
					fileDir.mkdirs();
				}
				//是否有headImg文件
				File file = new File(strFileDir + strFileName);
				if(file.exists()){
					file.delete();
				}
				
				fileOutputStream = new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);// 把数据写入文件
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 从指定路径 按规定收缩比例加载图片
	 * 
	 * @param filePath
	 * @param scale
	 * @return
	 */
	public static Bitmap getBitmap(Resources res, int ResId, int scale) {
		Options opts = new Options();
		opts.inSampleSize = scale;
		return BitmapFactory.decodeResource(res, ResId, opts);
	}
	
	/**
	 * 从指定路径 按指定宽高 保持纵横比收缩加载图片
	 * 
	 * @param filePath
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmap(Resources res, int ResId, int width, int height) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, ResId, opts);
		int x = opts.outWidth / width;
		int y = opts.outHeight / height;
		int scale = x > y ? x : y;
		return getBitmap(res, ResId, scale);
		}
	/**
	 * 从字节数组中收缩加载 指定位图对象
	 * 
	 * @param data
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmap(byte[] data, int width, int height) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, opts);
		int x = opts.outWidth / width;
		int y = opts.outHeight / height;
		int scale = x > y ? x : y;

		opts.inJustDecodeBounds = false;
		opts.inSampleSize = scale;
		return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
	}

	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;

	}

	public static Bitmap getPicFromBytes(byte[] bytes,
			BitmapFactory.Options opts) {
		if (bytes != null)
			if (opts != null)
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
						opts);
			else
				return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return null;
	}
	
}
