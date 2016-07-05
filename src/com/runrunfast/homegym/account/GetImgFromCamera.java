package com.runrunfast.homegym.account;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class GetImgFromCamera {

	public GetImgFromCamera() {
		
	}
	
	private void choosePic() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("选择照片");
		builder.setPositiveButton("相机", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface

			dialog, int which) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent, 0);

			}
		});
		builder.setNegativeButton("相册", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, 1);

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ContentResolver resolver = getContentResolver();
		/**
		 * 如果不拍照 或者不选择图片返回 不执行任何操作
		 */
		/**
		 * 因为两种方式都用到了startActivityForResult方法，这个方法执行完后都会执行onActivityResult方法 ，
		 * 所以为了区别到底选择了那个方式获取图片要进行判断
		 * ，这里的requestCode跟startActivityForResult里面第二个参数对应 1== 相册 0 ==相机
		 */

		/**
		 * 因为两种方式都用到了startActivityForResult方法，这个方法执行完后都会执行onActivityResult方法 ，
		 * 所以为了区别到底选择了那个方式获取图片要进行判断
		 * ，这里的requestCode跟startActivityForResult里面第二个参数对应 1== 相册 0 ==相机
		 */
		if (resultCode != 0) {
			if (requestCode == 0) {// 拍照
				startPhotoZoom(imageUri);
			} else if (requestCode == 1) {// 相册
				if (null != data) {
					startPhotoZoom(data.getData());
				}
			} else if (requestCode == 3) { // 3--保存裁剪的图片

				if (null != data) {
					bmFromCamera = data.getParcelableExtra("data");
					// headImage.setImageBitmap(bmFromCamera);

					tempUrl = UserInfo.SDCARD_USER_IMAGE_PATH_ROOT
							+ "tempHeadImg.jpg";
					setUserInfo();
					BitmapUtils.saveBitmapToSDcard(bmFromCamera);
					UserInfoSaveAsyncTask task = new UserInfoSaveAsyncTask(
							bmFromCamera, this, "正在修改用户信息，请耐心等候...");
					task.execute();

				}

			}
		}

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
