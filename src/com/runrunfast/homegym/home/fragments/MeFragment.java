package com.runrunfast.homegym.home.fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.DataTransferUtil;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.dao.MyTotalRecordDao;
import com.runrunfast.homegym.home.AboutActivity;
import com.runrunfast.homegym.home.FeedbackActivity;
import com.runrunfast.homegym.home.PersonalInfoActivity;
import com.runrunfast.homegym.record.TotalRecord;
import com.runrunfast.homegym.start.StartActivity;
import com.runrunfast.homegym.utils.Const;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.FileUtils;
import com.runrunfast.homegym.utils.Globle;
import com.runrunfast.homegym.widget.CircleMaskImageView;
import com.runrunfast.homegym.widget.DialogActivity;

public class MeFragment extends Fragment implements OnClickListener{
	private final String TAG = "MeFragment";
	
	private View rootView, personalInfoView, aboutView, feedbackView;
	
	private Resources mResources;
	
	private Button btnExit;
	
	private UserInfo mUserInfo;
	
	private CircleMaskImageView headimgView;
	private TextView tvNickname;
	private ImageView ivSex;
	private TextView tvAge;
	
	private TextView tvTime, tvDays;

	@Override
	public View onCreateView(LayoutInflater inflater,
		 ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_me, container, false);
		
		mResources = getResources();
		
		initView();
		
		initData();
		
		return rootView;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		tvAge.setText(String.valueOf(DataTransferUtil.getAgeByBirtyday(mUserInfo.strBirthday)) + mResources.getString(R.string.age));
	}
	
	private void initData() {
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		Bitmap bitmap = null;
		if(FileUtils.isFileExist(UserInfo.IMAGE_FILE_LOCATION)){
			bitmap = BitmapFactory.decodeFile(UserInfo.IMAGE_FILE_LOCATION);
		}
		
		if(bitmap != null){
			headimgView.setImageBitmap(bitmap);
		}
		
		tvNickname.setText(mUserInfo.strNickName);
		if(mUserInfo.strSex.equals(UserInfo.SEX_MAN)){
			ivSex.setBackgroundResource(R.drawable.sex_men);
		}else{
			ivSex.setBackgroundResource(R.drawable.sex_women);
		}
		
		TotalRecord totalRecord = MyTotalRecordDao.getInstance().getMyTotalRecordFromDb(Globle.gApplicationContext, mUserInfo.strAccountId);
		tvTime.setText(DateUtil.secToHour(totalRecord.total_time));
		tvDays.setText(String.valueOf(totalRecord.total_days));
	}

	private void initView() {
		btnExit = (Button)rootView.findViewById(R.id.btn_exit);
		btnExit.setOnClickListener(this);
		
		aboutView = (View)rootView.findViewById(R.id.account_about_layout);
		aboutView.setOnClickListener(this);
		
		feedbackView = (View)rootView.findViewById(R.id.account_feedback_layout);
		feedbackView.setOnClickListener(this);
		
		personalInfoView = (View)rootView.findViewById(R.id.me_personal_info_layout);
		personalInfoView.setOnClickListener(this);
		
		headimgView = (CircleMaskImageView)rootView.findViewById(R.id.account_head_img);
		tvNickname = (TextView)rootView.findViewById(R.id.account_name_text);
		
		ivSex = (ImageView)rootView.findViewById(R.id.account_sex_img);
		tvAge = (TextView)rootView.findViewById(R.id.account_old_text);
		
		tvTime = (TextView)rootView.findViewById(R.id.account_finish_time_text);
		tvDays = (TextView)rootView.findViewById(R.id.account_finish_days_text);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_exit:
			exitAccount();
			break;
			
		case R.id.account_about_layout:
			startActivity(new Intent(getActivity(), AboutActivity.class));
			break;
			
		case R.id.account_feedback_layout:
			startActivity(new Intent(getActivity(), FeedbackActivity.class));
			break;
			
		case R.id.me_personal_info_layout:
//			startActivity(new Intent(getActivity(), ImprovePersonalInfoActivity.class));
			startActivity(new Intent(getActivity(), PersonalInfoActivity.class));
			break;

		default:
			break;
		}
	}
	
	private void exitAccount() {
		Intent intent = new Intent(getActivity(), DialogActivity.class);
		intent.putExtra(DialogActivity.KEY_CONTENT, mResources.getString(R.string.account_exit_or_not));
		intent.putExtra(DialogActivity.KEY_CANCEL, mResources.getString(R.string.cancel));
		intent.putExtra(DialogActivity.KEY_CONFIRM, mResources.getString(R.string.confirm));
		startActivityForResult(intent, Const.DIALOG_REQ_CODE_EXIT_ACCOUNT);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode != Const.DIALOG_REQ_CODE_EXIT_ACCOUNT){
			Log.e(TAG, "onActivityResult, requestCode = " + requestCode);
			return;
		}
		
		if(resultCode == DialogActivity.RSP_CONFIRM){
			Log.d(TAG, "onActivityResult, confirm resultCode = " + resultCode);
			// TODO 退出帐号的操作
			AccountMgr.getInstance().logout(getActivity());
			startActivity(new Intent(getActivity(), StartActivity.class));
			getActivity().finish();
		}
		
	}

	// 不要删除，切换fragment用到
    @Override
	public void setMenuVisibility(boolean menuVisible) {
		super.setMenuVisibility(menuVisible);
		if (this.getView() != null)
			this.getView().setVisibility(menuVisible ? View.VISIBLE : View.GONE);
	}
}
