package com.runrunfast.homegym.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.AccountMgr.IGetPersonalInfoListener;
import com.runrunfast.homegym.account.AccountMgr.IPersonalInfoListener;
import com.runrunfast.homegym.account.AccountMgr.IUpdateHeadimgListener;
import com.runrunfast.homegym.account.DataTransferUtil;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.utils.BitmapUtils;
import com.runrunfast.homegym.utils.FileUtils;
import com.runrunfast.homegym.widget.CircleMaskImageView;
import com.runrunfast.homegym.widget.PopupWindows;
import com.runrunfast.homegym.widget.WheelView;
import com.runrunfast.homegym.widget.WheelView.OnWheelViewListener;

import java.io.File;
import java.util.List;

public class PersonalInfoActivity extends Activity implements OnClickListener{
	private final String TAG = "PersonalInfoActivity";
	
	private static final int INPUT_TYPE_SEX = 1;
	private static final int INPUT_TYPE_BIRTH = 2;
	private static final int INPUT_TYPE_WEIGHT = 3;
	private static final int INPUT_TYPE_HEIGHT = 4;
	
	private int inputType;
	
	private Resources mResources;
	
	private TextView tvTitle;
	private Button btnSelectLeft, btnSelectRight;
	
	private RelativeLayout popView;
	private RelativeLayout selectContainer;
	private PopupWindows popWindows;
	private TextView tvPopTitle, tvPopConfirm, tvChangeHeadimg;
	private CircleMaskImageView headimgView;
	
	private View wheelOneLayout;
	private WheelView wheelOneWheelView;
	
	private View wheelTwoLayout;
	private WheelView wheelTwoWheelView1, wheelTwoWheelView2;
	
	private View wheelThreeLayout;
	private WheelView wheelThreeWheelView1, wheelThreeWheelView2, wheelThreeWheelView3;
	
	private TextView tvNick, tvSex, tvBirth, tvCity, tvHeight, tvWeight, tvBmiNum, tvBmiDescip;
	
	private UserInfo mUserInfo;
	
	private String strNickname;
	private String strSex;
	private String strBirthday;
	private String strWeight;
	private String strHeight;
	
	private HandlerThread handleThread;
	private Handler mWorkHandler;
	
	private String mBirthdayYear;
	private String mBirthdayMonth;
	private String mBirthdayDay;
	
	private int mYearPosition;
	private int mMonthPosition;
	private int mDayPosition;
	
	private Bitmap bmFromCamera;
	
	private IGetPersonalInfoListener mIGetPersonalInfoListener;
	private IPersonalInfoListener mIPersionalInfoListener;
	private IUpdateHeadimgListener mIUpdateHeadimgListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_info);
		
		mResources = getResources();
		
		initView();
		
		initData();
		
		initHandler();
		
		initListener();
		
		AccountMgr.getInstance().getPersonalInfo(mUserInfo.strAccountId);
	}

	private void initHandler() {
		handleThread = new HandlerThread(TAG);
		handleThread.start();
		mWorkHandler = new Handler(handleThread.getLooper());
	}

	private void initListener() {
		// 获取信息接口
		mIGetPersonalInfoListener = new IGetPersonalInfoListener() {
			
			@Override
			public void onSuccess() {
				Log.i(TAG, "IGetPersonalInfoListener, onSuccess");
				initData();
			}
			
			@Override
			public void onFail() {
				Log.i(TAG, "IGetPersonalInfoListener, onFail");
			}
		};
		AccountMgr.getInstance().setOnGetPersonalInfoListener(mIGetPersonalInfoListener);
		
		// 上传信息接口
		mIPersionalInfoListener = new IPersonalInfoListener() {
			
			@Override
			public void onSuccess() {
				Toast.makeText(PersonalInfoActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
				String sex = tvSex.getText().toString();
				String nickName = tvNick.getText().toString();
				String birthday = tvBirth.getText().toString();
				String weight = tvWeight.getText().toString();
				String height = tvHeight.getText().toString();
				
				AccountMgr.getInstance().saveAccountInfo(nickName, sex, birthday, weight, height, "");
				AccountMgr.getInstance().loadUserInfo();
				FileUtils.deleteFile(UserInfo.IMAGE_FILE_LOCATION_TEMP);
				finish();
			}
			
			@Override
			public void onFail() {
				Toast.makeText(PersonalInfoActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
			}
		};
		AccountMgr.getInstance().setOnPersonalInfoListener(mIPersionalInfoListener);
		
		// 上传头像接口
		mIUpdateHeadimgListener = new IUpdateHeadimgListener() {
			
			@Override
			public void onSuccess() {
				// 这里应该先上传，成功后再执行下面操作
				if(bmFromCamera != null){
					BitmapUtils.saveBitmapToSDcard(bmFromCamera, UserInfo.IMAGE_FILE_DIR, UserInfo.IMG_FILE_NAME);
				}
				
				String sex = tvSex.getText().toString();
				String nickName = tvNick.getText().toString();
				String birthday = tvBirth.getText().toString();
				String weight = tvWeight.getText().toString();
				String height = tvHeight.getText().toString();
				
				UserInfo userInfo = new UserInfo();
				userInfo.strAccountId = mUserInfo.strAccountId;
				userInfo.strNickName = nickName;
				userInfo.strSex = sex;
				userInfo.strBirthday = birthday;
				userInfo.strWeight = weight;
				userInfo.strHeight = height;
				
				AccountMgr.getInstance().updatePersonalInfo(userInfo);
			}
			
			@Override
			public void onFail() {
				Toast.makeText(PersonalInfoActivity.this, "上传头像失败", Toast.LENGTH_SHORT).show();
			}
		};
		AccountMgr.getInstance().setOnUpdateHeadimgListener(mIUpdateHeadimgListener);
	}

	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		strNickname = mUserInfo.strNickName;
		strSex = mUserInfo.strSex;
		strBirthday = mUserInfo.strBirthday;
		strHeight = mUserInfo.strHeight;
		strWeight = mUserInfo.strWeight;
		
		strBirthday = mUserInfo.strBirthday;
		
		getBirthdayMonthAndDay();
		
		tvNick.setText(mUserInfo.strNickName);
		if(mUserInfo.strSex.equals(UserInfo.SEX_SERVER_MALE)){
			tvSex.setText(UserInfo.SEX_MAN);
		}else{
			tvSex.setText(UserInfo.SEX_WOMAN);
		}
		
		tvBirth.setText(mUserInfo.strBirthday);
		tvHeight.setText(mUserInfo.strHeight);
		tvWeight.setText(mUserInfo.strWeight);
		
		setBmi();
		
		Bitmap bitmap = BitmapFactory.decodeFile(UserInfo.IMAGE_FILE_LOCATION);
		if(bitmap != null){
			headimgView.setImageBitmap(bitmap);
		}
	}

	private void setBmi() {
		int weight = Integer.parseInt(strWeight);
		int height = Integer.parseInt(strHeight);
		
		float bmi = Float.valueOf(DataTransferUtil.getOneDecimalData((weight / (height * 0.01f * height * 0.01f))));
		tvBmiNum.setText( String.valueOf(bmi) );
		if(bmi < 18.5){
			tvBmiDescip.setText("轻体重");
		}else if(bmi >= 18.5 && bmi < 24){
			tvBmiDescip.setText("健康体重");
		}else if(bmi >= 24 && bmi < 28){
			tvBmiDescip.setText("超重");
		}else{
			tvBmiDescip.setText("肥胖");
		}
	}
	
	private void getBirthdayMonthAndDay(){
		mBirthdayYear = DataTransferUtil.getInstance().getBirthYear(strBirthday);
		mBirthdayMonth = DataTransferUtil.getInstance().getBirthMonth(strBirthday);
		mBirthdayDay = DataTransferUtil.getInstance().getBirthDay(strBirthday);
		
		mYearPosition = DataTransferUtil.getInstance().getYearPosition(mBirthdayYear);
		mMonthPosition = DataTransferUtil.getInstance().getMonthPosition(mBirthdayMonth);
		mDayPosition = DataTransferUtil.getInstance().getDayPosition(mBirthdayDay);
	}
	
	private void initView() {
		tvTitle = (TextView)findViewById(R.id.actionbar_title);
		tvTitle.setText(R.string.personal_info);
		
		btnSelectLeft = (Button)findViewById(R.id.actionbar_left_btn);
		btnSelectLeft.setBackgroundResource(R.drawable.nav_back);
		btnSelectLeft.setOnClickListener(this);
		
		btnSelectRight = (Button)findViewById(R.id.actionbar_right_btn);
		btnSelectRight.setText(R.string.save);
		btnSelectRight.setTextColor(mResources.getColor(R.color.feedback_send_text_color));
		btnSelectRight.setOnClickListener(this);
		
		tvNick = (TextView)findViewById(R.id.personal_nick_text);
		
		tvSex = (TextView)findViewById(R.id.personal_sex_text);
		tvSex.setOnClickListener(this);
		
		tvBirth = (TextView)findViewById(R.id.personal_birth_text);
		tvBirth.setOnClickListener(this);
		
		tvCity = (TextView)findViewById(R.id.personal_city_text);
		tvCity.setOnClickListener(this);
		
		tvHeight = (TextView)findViewById(R.id.personal_height_text);
		tvHeight.setOnClickListener(this);
		
		tvWeight = (TextView)findViewById(R.id.personal_weight_text);
		tvWeight.setOnClickListener(this);
		
		tvBmiNum = (TextView)findViewById(R.id.personal_bmi_text);
		tvBmiDescip = (TextView)findViewById(R.id.personal_bmi_context_text);
		
		popView = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.popupwindow_layout, null);
		tvPopTitle = (TextView)popView.findViewById(R.id.popupwindow_menu_title_text);
		tvPopConfirm = (TextView)popView.findViewById(R.id.popupwindow_menu_confirm_text);
		tvPopConfirm.setOnClickListener(this);
		
		selectContainer = (RelativeLayout)popView.findViewById(R.id.popupwindow_content);
		
		tvChangeHeadimg = (TextView)findViewById(R.id.change_headimg_text);
		tvChangeHeadimg.setOnClickListener(this);
		
		headimgView = (CircleMaskImageView)findViewById(R.id.personal_head_img);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.personal_sex_text:
			showSelectSex();
			break;
		case R.id.personal_birth_text:
			showSelectBirth();
			break;
		case R.id.personal_city_text:
			
			break;
		case R.id.personal_height_text:
			showSelectHeight();
			break;
		case R.id.personal_weight_text:
			showSelectWeight();
			break;
			
		case R.id.popupwindow_menu_confirm_text:
			clickPopConfirm();
			break;
			
		case R.id.change_headimg_text:
			changeHeadimg();
			break;
			
		case R.id.actionbar_left_btn:
			finish();
			break;
			
		case R.id.actionbar_right_btn:
			savePersonalInfo();
			break;
	
		default:
			break;
		}
	}
	
	private void savePersonalInfo() {
//		// 这里应该先上传，成功后再执行下面操作
//		if(bmFromCamera != null){
//			BitmapUtils.saveBitmapToSDcard(bmFromCamera, UserInfo.IMAGE_FILE_DIR, UserInfo.IMG_FILE_NAME);
//		}
		
//		if(FileUtils.isFileExist(UserInfo.IMAGE_FILE_LOCATION_TEMP)){
//			AccountMgr.getInstance().updateHeadImg(new File(UserInfo.IMAGE_FILE_LOCATION_TEMP));
//			return;
//		}
		
		String sex = tvSex.getText().toString();
		String nickName = tvNick.getText().toString();
		String birthday = tvBirth.getText().toString();
		String weight = tvWeight.getText().toString();
		String height = tvHeight.getText().toString();
		
		UserInfo userInfo = new UserInfo();
		userInfo.strAccountId = mUserInfo.strAccountId;
		userInfo.strNickName = nickName;
		userInfo.strSex = sex;
		userInfo.strBirthday = birthday;
		userInfo.strWeight = weight;
		userInfo.strHeight = height;
		
		AccountMgr.getInstance().updatePersonalInfo(mUserInfo);
	}

	private void changeHeadimg() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.select_img);
		builder.setPositiveButton(R.string.camera, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface

			dialog, int which) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, UserInfo.CAMERA_IMG_URI);
				startActivityForResult(intent, UserInfo.REQ_CAMERA);
			}
		});
		builder.setNegativeButton(R.string.album, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, UserInfo.REQ_ALBUM);
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
		if(resultCode == 0){
			Log.e(TAG, "onActivityResult, resultCode = 0, ignore");
			return;
		}
		if (requestCode == UserInfo.REQ_CAMERA) {// 拍照
			startPhotoZoom(UserInfo.CAMERA_IMG_URI);
		} else if (requestCode == UserInfo.REQ_ALBUM) {// 相册
			Log.d(TAG, "onActivityResult, data = " + data);
			if (null != data) {
				Log.d(TAG, "onActivityResult, data.getData() = " + data.getData());
				Log.d(TAG, "onActivityResult, data.getExtras(); = " + data.getExtras());
				startPhotoZoom(data.getData());
			}
		} else if (requestCode == UserInfo.REQ_ZOOM) { // 3--保存裁剪的图片
			if (null != data) {
				bmFromCamera = data.getParcelableExtra("data");
				
				BitmapUtils.saveBitmapToSDcard(bmFromCamera, UserInfo.IMAGE_FILE_DIR, UserInfo.IMG_FILE_NAME_TEMP);
				headimgView.setImageBitmap(bmFromCamera);
//				UserInfoSaveAsyncTask task = new UserInfoSaveAsyncTask(
//						bmFromCamera, this, "正在修改用户信息，请耐心等候...");
//				task.execute();
			}
		}
	}
	
	// 对相册或者拍照进行裁剪
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的view可裁剪
		intent.putExtra("crop", "true");
		// aspectX,aspectY是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX,outputY是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, UserInfo.REQ_ZOOM);
	}

	private void clickPopConfirm() {
		if(popWindows == null){
			Log.e(TAG, "clickPopConfirm, popWindows == null");
			return;
		}
		
		popWindows.dismiss();
		
		switch (inputType) {
		case INPUT_TYPE_HEIGHT:
			tvHeight.setText(strHeight);
			setBmi();
			break;
			
		case INPUT_TYPE_WEIGHT:
			tvWeight.setText(strWeight);
			setBmi();
			break;
			
		case INPUT_TYPE_SEX:
			if(strSex.equals(UserInfo.SEX_SERVER_MALE)){
				tvSex.setText(UserInfo.SEX_MAN);
			}else{
				tvSex.setText(UserInfo.SEX_WOMAN);
			}
			break;
			
		case INPUT_TYPE_BIRTH:
			tvBirth.setText(strBirthday);
			break;

		default:
			break;
		}
	}
	
	private void showSelectHeight() {
		inputType = INPUT_TYPE_HEIGHT;
		selectContainer.removeAllViews();
		setSelectContainerWidth();
		wheelOneLayout = (View)LayoutInflater.from(this).inflate(R.layout.wheel_one, null);
		selectContainer.addView(wheelOneLayout);
		tvPopTitle.setText(R.string.select_height);
		
		wheelOneWheelView = (WheelView)wheelOneLayout.findViewById(R.id.select_wheelview);
		wheelOneWheelView.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, item = " + item);
				strHeight = item;
			}
		});
		wheelOneWheelView.setTextSize(24);
		wheelOneWheelView.setOffset(1);
		wheelOneWheelView.setSeletion(DataTransferUtil.getInstance().getHeightPosition(strHeight));
		wheelOneWheelView.setItems(AccountMgr.getInstance().getHeightList());
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}

	private void showSelectWeight() {
		inputType = INPUT_TYPE_WEIGHT;
		selectContainer.removeAllViews();
		setSelectContainerWidth();
		wheelOneLayout = (View)LayoutInflater.from(this).inflate(R.layout.wheel_one, null);
		selectContainer.addView(wheelOneLayout);
		tvPopTitle.setText(R.string.select_weight);
		
		wheelOneWheelView = (WheelView)wheelOneLayout.findViewById(R.id.select_wheelview);
		wheelOneWheelView.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, item = " + item);
				strWeight = item;
			}
		});
		wheelOneWheelView.setTextSize(24);
		wheelOneWheelView.setOffset(1);
		wheelOneWheelView.setSeletion(DataTransferUtil.getInstance().getWeightPosition(strWeight));
		wheelOneWheelView.setItems(AccountMgr.getInstance().getWeightList());
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}

	private void showSelectBirth() {
		getBirthdayMonthAndDay();
		
		inputType = INPUT_TYPE_BIRTH;
		selectContainer.removeAllViews();
		setSelectContainerBigWidth();
		wheelThreeLayout = (View)LayoutInflater.from(this).inflate(R.layout.wheel_three, null);
		selectContainer.addView(wheelThreeLayout);
		tvPopTitle.setText(R.string.select_birth);
		
		wheelThreeWheelView1 = (WheelView)wheelThreeLayout.findViewById(R.id.select_wheelview_one);
		wheelThreeWheelView2 = (WheelView)wheelThreeLayout.findViewById(R.id.select_wheelview_two);
		wheelThreeWheelView3 = (WheelView)wheelThreeLayout.findViewById(R.id.select_wheelview_three);
		
		wheelThreeWheelView1.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, year = " + item);
				mBirthdayYear = item;
				strBirthday = mBirthdayYear + "-" + String.format("%02d", Integer.parseInt(mBirthdayMonth)) + "-" + String.format("%02d", Integer.parseInt(mBirthdayDay));
			}
		});
		wheelThreeWheelView2.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, month = " + item);
				mBirthdayMonth = String.format("%02d", Integer.parseInt(item));
				
				mBirthdayDay = "1";
				mDayPosition = 0;
				
				strBirthday = mBirthdayYear + "-" + String.format("%02d", Integer.parseInt(mBirthdayMonth)) + "-" + String.format("%02d", Integer.parseInt(mBirthdayDay));
				
				Log.d(TAG, "onSelected, day list = " + DataTransferUtil.getInstance().getDayList(strBirthday));
				
				setBirthDayWheel(DataTransferUtil.getInstance().getDayList(strBirthday), 0);
			}
		});
		wheelThreeWheelView3.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, day = " + item);
				mBirthdayDay = String.format("%02d", Integer.parseInt(item));
				strBirthday = mBirthdayYear + "-" + String.format("%02d", Integer.parseInt(mBirthdayMonth)) + "-" + String.format("%02d", Integer.parseInt(mBirthdayDay));
			}
		});
		wheelThreeWheelView1.setTextSize(18);
		wheelThreeWheelView1.setOffset(1);
		wheelThreeWheelView1.setSeletion(mYearPosition);
		wheelThreeWheelView1.setItems(AccountMgr.getInstance().getYearList());
		
		setBirthMonthWheel(AccountMgr.getInstance().getMonthList(), mMonthPosition);
		
		setBirthDayWheel(DataTransferUtil.getInstance().getDayList(strBirthday), mDayPosition);
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}

	private void setBirthDayWheel(List<String> dayList, int position) {
		wheelThreeWheelView3.setTextSize(18);
		wheelThreeWheelView3.setOffset(1);
		wheelThreeWheelView3.setSeletion(position);
		wheelThreeWheelView3.setItems(dayList);
	}

	private void setBirthMonthWheel(List<String> monthList, int position) {
		wheelThreeWheelView2.setTextSize(18);
		wheelThreeWheelView2.setOffset(1);
		wheelThreeWheelView2.setSeletion(position);
		wheelThreeWheelView2.setItems(monthList);
	}

	private void setSelectContainerWidth() {
		LayoutParams params = (LayoutParams) selectContainer.getLayoutParams();
		params.height = LayoutParams.MATCH_PARENT;
		params.width = (int) mResources.getDimension(R.dimen.popwindow_content_width);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		selectContainer.setLayoutParams(params);
	}
	
	private void setSelectContainerBigWidth() {
		LayoutParams params = (LayoutParams) selectContainer.getLayoutParams();
		params.height = LayoutParams.MATCH_PARENT;
		params.width = (int) mResources.getDimension(R.dimen.popwindow_content_width_big);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		selectContainer.setLayoutParams(params);
	}
	
	private void showSelectSex() {
		inputType = INPUT_TYPE_SEX;
		selectContainer.removeAllViews();
		setSelectContainerWidth();
		wheelOneLayout = (View)LayoutInflater.from(this).inflate(R.layout.wheel_one, null);
		selectContainer.addView(wheelOneLayout);
		tvPopTitle.setText(R.string.select_weight);
		
		wheelOneWheelView = (WheelView)wheelOneLayout.findViewById(R.id.select_wheelview);
		wheelOneWheelView.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, item = " + item);
				if(selectedIndex == 1){
					strSex = UserInfo.SEX_SERVER_MALE;
				}else{
					strSex = UserInfo.SEX_SERVER_FEMALE;
				}
			}
		});
		wheelOneWheelView.setTextSize(24);
		wheelOneWheelView.setOffset(1);
		int sexPosition = 0;
		if(strSex.equals(UserInfo.SEX_SERVER_MALE)){
			sexPosition = 0;
		}else{
			sexPosition = 1;
		}
		wheelOneWheelView.setSeletion(sexPosition);
		wheelOneWheelView.setItems(AccountMgr.getInstance().getSexList());
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}
	
	@Override
	protected void onDestroy() {
		AccountMgr.getInstance().setOnGetPersonalInfoListener(null);
		AccountMgr.getInstance().setOnUpdateHeadimgListener(null);
		AccountMgr.getInstance().setOnPersonalInfoListener(null);
		super.onDestroy();
	}
	
}
