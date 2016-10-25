package com.runrunfast.homegym.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.update.UpdateMgr;
import com.runrunfast.homegym.update.UpdateMgr.ICheckUpdateResultObserver;
import com.runrunfast.homegym.utils.ApplicationUtil;
import com.runrunfast.homegym.widget.PopupWindows;

public class AboutActivity extends Activity {
	private final String TAG = "AboutActivity";
	
	private TextView tvTitle;
	private TextView tvVersion;
	private ICheckUpdateResultObserver mICheckUpdateResultObserver;
	
	private PopupWindows mUpdatePopupWindows;
	private View mUpdateContentView;
	
	private TextView tvNewVersion, tvDescription, tvUpdateNow, tvIgnore;
	private String mUpdateUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_about);
		
		initView();
		
		initObserver();
	}

	private void initObserver() {
		mICheckUpdateResultObserver = new ICheckUpdateResultObserver() {
			
			@Override
			public void onNotNewerVersion() {
				Toast.makeText(AboutActivity.this, "已是最新版本", Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onHaveNewerVersion(String version, String description, String url) {
				handleHaveNewerVersion(version, description, url);
			}
			
			@Override
			public void onFail() {
				Toast.makeText(AboutActivity.this, "获取版本失败", Toast.LENGTH_SHORT).show();
			}
		};
		UpdateMgr.getInstance().addICheckUpdateResultObserver(mICheckUpdateResultObserver);
	}

	private void handleHaveNewerVersion(String version, String description, String url) {
		mUpdateUrl = url;
		showUpdateView(version, description, url);
	}

	private void showUpdateView(String version, String description, String url) {
		if(mUpdatePopupWindows == null){
			mUpdatePopupWindows = new PopupWindows(this, mUpdateContentView);
			mUpdatePopupWindows.setLayout(mUpdateContentView);
		}
		
		tvNewVersion.setText("新版本 V" + version);
		tvDescription.setText(description);
		
		mUpdatePopupWindows.show();
	}
	
	private void initView() {
		tvTitle = (TextView)findViewById(R.id.actionbar_title);
		tvTitle.setText(R.string.about);
		
		findViewById(R.id.actionbar_left_btn).setBackgroundResource(R.drawable.nav_back);
		
		tvVersion = (TextView)findViewById(R.id.about_version_text);
		
		tvVersion.setText(ApplicationUtil.getVersionName(this));
		
		mUpdateContentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_new_version_layout, null);
		tvNewVersion = (TextView)mUpdateContentView.findViewById(R.id.new_version_title_text);
		tvDescription = (TextView)mUpdateContentView.findViewById(R.id.new_version_descript_text);
		tvUpdateNow = (TextView)mUpdateContentView.findViewById(R.id.update_text);
		tvIgnore = (TextView)mUpdateContentView.findViewById(R.id.ignore_text);
		
		tvUpdateNow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				prepareToUpdate();
			}
		});
		
		tvIgnore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mUpdatePopupWindows.dismiss();
			}
		});
	}
	
	private void prepareToUpdate() {
		mUpdatePopupWindows.dismiss();
		// 下载
		Uri uri = Uri.parse(mUpdateUrl);
		Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(downloadIntent);
	}

	public void onClick(View view){
		switch (view.getId()) {
		case R.id.actionbar_left_btn:
			finish();
			break;

		case R.id.check_update_btn:
			UpdateMgr.getInstance().checkUpdate();
			break;
			
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		if(mICheckUpdateResultObserver != null){
			UpdateMgr.getInstance().removeGetCourseFromServerObserver(mICheckUpdateResultObserver);
		}
		super.onDestroy();
	}
	
}
