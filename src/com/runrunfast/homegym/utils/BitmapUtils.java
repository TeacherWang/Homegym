package com.runrunfast.homegym.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.runrunfast.homegym.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapUtils {
	
	private static final String TAG = "BitmapUtils";
	
	public static final DisplayImageOptions initCourseImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.home_add)
				.showImageOnFail(R.drawable.home_add)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.resetViewBeforeLoading(true).handler(new Handler())
				.displayer(new SimpleBitmapDisplayer()).build();
		return options;
	}
	
	public static final DisplayImageOptions initActionImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.course_action_pic_1)
				.showImageOnFail(R.drawable.course_action_pic_1)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.resetViewBeforeLoading(true).handler(new Handler())
				.displayer(new SimpleBitmapDisplayer()).build();
		return options;
	}
	
	public static final DisplayImageOptions initActionDescriptImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.course_action_pic_1)
				.showImageOnFail(R.drawable.course_action_pic_1)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.resetViewBeforeLoading(true).handler(new Handler())
				.displayer(new SimpleBitmapDisplayer()).build();
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
	
	public static void saveBitmapToSDcard(Bitmap bitmap, String strFilePath) {
		try {
			FileOutputStream fileOutputStream = null;
			String sdcardState = Environment.getExternalStorageState();
			if(Environment.MEDIA_MOUNTED.equals(sdcardState)){
				String folderName = FileUtils.getFolderName(strFilePath);
				
				File fileDir = new File(folderName);
				if(!fileDir.exists()){
					fileDir.mkdirs();
				}
				
				File file = new File(strFilePath);
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
	
	public static final String IPAD_USERAGENT = "Mozilla/5.0 (iPad; U; CPU OS 5_1 like Mac OS X) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B367 Safari/531.21.10";
	
	public static boolean saveImgFileFromUrl(String url, String fileFolder, String fileName)
    {
        boolean saveSuc = false;
        FileOutputStream fileOutputStream = null;
        HttpURLConnection conn = null;
        try
        {
            //          imageFile = File.createTempFile("image", null, mTempDirectory);

            // Connects the http server.
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setReadTimeout(60000);
            conn.setConnectTimeout(60000);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("User-Agent", IPAD_USERAGENT);
            conn.connect();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                // Reads the image data from server.
                InputStream inputStream = conn.getInputStream();
                File dirFile = new File(fileFolder);
                if (!dirFile.exists())
                {
                    dirFile.mkdirs();
                }
                File destFile = new File(fileFolder + fileName);
                if (destFile.exists())
                {
                    destFile.delete();
                }
                destFile.createNewFile();
                saveSuc = FileUtils.writeFile(destFile, inputStream);
            } else
            {
                saveSuc = false;
            }
        } catch (Throwable e)
        {
            Log.e(TAG, "Couldn't from '" + url + "' to load image.", e);
            saveSuc = false;
        } finally
        {
            if (conn != null)
            {
                conn.disconnect();
            }
            if (fileOutputStream != null)
            {
                try
                {
                    fileOutputStream.close();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return saveSuc;
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
