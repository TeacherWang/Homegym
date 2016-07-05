package com.runrunfast.homegym.start;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.DataTransferUtil;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.utils.BitmapUtils;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.widget.PopupWindows;
import com.runrunfast.homegym.widget.WheelView;
import com.runrunfast.homegym.widget.WheelView.OnWheelViewListener;

import java.util.List;

public class ImprovePersonalInfoActivity extends Activity implements OnClickListener{
	private final String TAG = "ImprovePersonalInfoActivity";
	
	private static final int INPUT_TYPE_SEX = 1;
	private static final int INPUT_TYPE_BIRTH = 2;
	private static final int INPUT_TYPE_WEIGHT = 3;
	private static final int INPUT_TYPE_HEIGHT = 4;
	
	private int inputType;
	
	private Resources mResources;
	private View actionBar;
	private TextView tvAddHeadimg;
	private Button btnFinish;
	
	private View headImg;
	private EditText etNick;
	private TextView tvSex, tvBirth, tvWeight, tvHeight;
	
	private RelativeLayout popView;
	private RelativeLayout selectContainer;
	private PopupWindows popWindows;
	private TextView tvPopTitle, tvPopConfirm;
	
	private View wheelOneLayout;
	private WheelView wheelOneWheelView;
	
	private View wheelThreeLayout;
	private WheelView wheelThreeWheelView1, wheelThreeWheelView2, wheelThreeWheelView3;
	
	private String mBirthdayYear;
	private String mBirthdayMonth;
	private String mBirthdayDay;
	
	private int mYearPosition;
	private int mMonthPosition;
	private int mDayPosition;
	
	private String strSex;
	private String mBirthday;
	private String strWeight;
	private String strHeight;
	
	private Bitmap bmFromCamera;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_improve_personal_info);
		
		mResources = getResources();
		
		initView();
		
		initData();
	}

	private void initData() {
		mBirthday = DateUtil.getCurrentDate();
		strWeight = "65";
		strHeight = "165";
	}

	private void initView() {
		actionBar = (View)findViewById(R.id.impr_personal_info_action_bar);
		((TextView)actionBar.findViewById(R.id.login_title_text)).setText(R.string.improve_personal_info);
		
		actionBar.findViewById(R.id.login_back_img).setVisibility(View.INVISIBLE);
		
		tvAddHeadimg = (TextView)findViewById(R.id.add_headimg_text);
		tvAddHeadimg.setOnClickListener(this);
		
		btnFinish = (Button)findViewById(R.id.btn_impr_finish);
		btnFinish.setOnClickListener(this);
		
		headImg = (View)findViewById(R.id.impr_personal_head_img);
		headImg.setOnClickListener(this);
		
		etNick = (EditText)findViewById(R.id.impr_nick_edit);
		etNick.setOnClickListener(this);
		
		tvSex = (TextView)findViewById(R.id.impr_sex_text);
		tvSex.setOnClickListener(this);
		
		tvBirth = (TextView)findViewById(R.id.impr_birth_text);
		tvBirth.setOnClickListener(this);
		
		tvWeight = (TextView)findViewById(R.id.impr_weight_text);
		tvWeight.setOnClickListener(this);
		
		tvHeight = (TextView)findViewById(R.id.impr_height_text);
		tvHeight.setOnClickListener(this);
		
		popView = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.popupwindow_layout, null);
		tvPopTitle = (TextView)popView.findViewById(R.id.popupwindow_menu_title_text);
		tvPopConfirm = (TextView)popView.findViewById(R.id.popupwindow_menu_confirm_text);
		tvPopConfirm.setOnClickListener(this);
		
		selectContainer = (RelativeLayout)popView.findViewById(R.id.popupwindow_content);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_headimg_text:
			getHeadImg();
			break;
			
		case R.id.btn_impr_finish:
			infoInputFinish();
			break;
			
		case R.id.impr_nick_edit:
			
			break;
			
		case R.id.impr_sex_text:
			showSelectSex();
			break;
			
		case R.id.impr_birth_text:
			showSelectBirth();
			break;
			
		case R.id.impr_weight_text:
			showSelectWeight();
			break;
			
		case R.id.impr_height_text:
			showSelectHeight();
			break;
		
		case R.id.popupwindow_menu_confirm_text:
			clickPopConfirm();
			break;
			
		case R.id.impr_personal_head_img:
			getImgFromCamera();
			break;

		default:
			break;
		}
	}

	private void getImgFromCamera() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(R.string.select_img);
		builder.setPositiveButton(R.string.camera, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface

			dialog, int which) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, UserInfo.IMAGE_URI);
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
			startPhotoZoom(UserInfo.IMAGE_URI);
		} else if (requestCode == UserInfo.REQ_ALBUM) {// 相册
			if (null != data) {
				startPhotoZoom(data.getData());
			}
		} else if (requestCode == UserInfo.REQ_ZOOM) { // 3--保存裁剪的图片
			if (null != data) {
				bmFromCamera = data.getParcelableExtra("data");
				
				setUserInfo();
				BitmapUtils.saveBitmapToSDcard(bmFromCamera);
				UserInfoSaveAsyncTask task = new UserInfoSaveAsyncTask(
						bmFromCamera, this, "正在修改用户信息，请耐心等候...");
				task.execute();
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
			break;
			
		case INPUT_TYPE_WEIGHT:
			tvWeight.setText(strWeight);
			break;
			
		case INPUT_TYPE_SEX:
			tvSex.setText(strSex);
			break;
			
		case INPUT_TYPE_BIRTH:
			tvBirth.setText(mBirthday);
			break;

		default:
			break;
		}
	}

	private void showSelectHeight() {
		inputType = INPUT_TYPE_HEIGHT;
		selectContainer.removeAllViews();
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
		wheelOneWheelView.setOffset(1);
		wheelOneWheelView.setSeletion(DataTransferUtil.getInstance().getWeightPosition(strWeight)); // 45为0位置，20为65位置
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
				mBirthday = mBirthdayYear + "-" + String.format("%02d", Integer.parseInt(mBirthdayMonth)) + "-" + String.format("%02d", Integer.parseInt(mBirthdayDay));
			}
		});
		wheelThreeWheelView2.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, month = " + item);
				mBirthdayMonth = String.format("%02d", Integer.parseInt(item));
				
				mBirthdayDay = "1";
				mDayPosition = 0;
				
				mBirthday = mBirthdayYear + "-" + String.format("%02d", Integer.parseInt(mBirthdayMonth)) + "-" + String.format("%02d", Integer.parseInt(mBirthdayDay));
				
				Log.d(TAG, "onSelected, day list = " + DataTransferUtil.getInstance().getDayList(mBirthday));
				
				setBirthDayWheel(DataTransferUtil.getInstance().getDayList(mBirthday), 0);
			}
		});
		wheelThreeWheelView3.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, day = " + item);
				mBirthdayDay = String.format("%02d", Integer.parseInt(item));
				mBirthday = mBirthdayYear + "-" + String.format("%02d", Integer.parseInt(mBirthdayMonth)) + "-" + String.format("%02d", Integer.parseInt(mBirthdayDay));
			}
		});
		wheelThreeWheelView1.setTextSize(18);
		wheelThreeWheelView1.setOffset(1);
		wheelThreeWheelView1.setSeletion(mYearPosition);
		wheelThreeWheelView1.setItems(AccountMgr.getInstance().getYearList());
		
		setBirthMonthWheel(AccountMgr.getInstance().getMonthList(), mMonthPosition);
		
		setBirthDayWheel(DataTransferUtil.getInstance().getDayList(mBirthday), mDayPosition);
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}
	
	private void setBirthMonthWheel(List<String> monthList, int position) {
		wheelThreeWheelView2.setTextSize(18);
		wheelThreeWheelView2.setOffset(1);
		wheelThreeWheelView2.setSeletion(position);
		wheelThreeWheelView2.setItems(monthList);
	}
	
	private void setBirthDayWheel(List<String> dayList, int position) {
		wheelThreeWheelView3.setTextSize(18);
		wheelThreeWheelView3.setOffset(1);
		wheelThreeWheelView3.setSeletion(position);
		wheelThreeWheelView3.setItems(dayList);
	}
	
	private void getBirthdayMonthAndDay(){
		mBirthdayYear = DataTransferUtil.getInstance().getBirthYear(mBirthday);
		mBirthdayMonth = DataTransferUtil.getInstance().getBirthMonth(mBirthday);
		mBirthdayDay = DataTransferUtil.getInstance().getBirthDay(mBirthday);
		
		mYearPosition = DataTransferUtil.getInstance().getYearPosition(mBirthdayYear);
		mMonthPosition = DataTransferUtil.getInstance().getMonthPosition(mBirthdayMonth);
		mDayPosition = DataTransferUtil.getInstance().getDayPosition(mBirthdayDay);
	}
	
	private void setSelectContainerBigWidth() {
		LayoutParams params = (LayoutParams) selectContainer.getLayoutParams();
		params.height = LayoutParams.MATCH_PARENT;
		params.width = (int) mResources.getDimension(R.dimen.popwindow_content_width_big);
		selectContainer.setLayoutParams(params);
	}
	
	private void showSelectSex() {
		inputType = INPUT_TYPE_SEX;
		selectContainer.removeAllViews();
		wheelOneLayout = (View)LayoutInflater.from(this).inflate(R.layout.wheel_one, null);
		selectContainer.addView(wheelOneLayout);
		tvPopTitle.setText(R.string.select_weight);
		
		wheelOneWheelView = (WheelView)wheelOneLayout.findViewById(R.id.select_wheelview);
		wheelOneWheelView.setOnWheelViewListener(new OnWheelViewListener(){
			@Override
			public void onSelected(int selectedIndex, String item) {
				Log.d(TAG, "onSelected, item = " + item);
				strSex = item;
			}
		});
		wheelOneWheelView.setOffset(1);
		wheelOneWheelView.setSeletion(0);
		wheelOneWheelView.setItems(AccountMgr.getInstance().getSexList());
		strSex = AccountMgr.getInstance().getSexList().get(0);
		
		popWindows = new PopupWindows(this, selectContainer);
		popWindows.setLayout(popView);
		popWindows.show();
	}

	private void infoInputFinish() {
		
	}

	private void getHeadImg() {
		
	}
}
