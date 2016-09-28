package com.runrunfast.homegym.home;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.AccountMgr;
import com.runrunfast.homegym.account.UserInfo;
import com.runrunfast.homegym.account.AccountMgr.IFeedbackListener;

public class FeedbackActivity extends Activity {
	
	private UserInfo mUserInfo;
	
	private Button btnSend;
	private TextView tvTitle;
	private EditText etAdvice, etContact;
	
	private IFeedbackListener mIFeedbackListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		
		mUserInfo = AccountMgr.getInstance().mUserInfo;
		
		initView();
		
		initListener();
	}
	
	private void initListener() {
		mIFeedbackListener = new IFeedbackListener() {
			
			@Override
			public void onSuccess() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(FeedbackActivity.this, "反馈成功", Toast.LENGTH_SHORT).show();
						finish();
					}
				});
			}
			
			@Override
			public void onFail() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(FeedbackActivity.this, "反馈失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		};
		AccountMgr.getInstance().setIFeedbackListener(mIFeedbackListener);
	}

	private void initView() {
		tvTitle = (TextView)findViewById(R.id.actionbar_title);
		tvTitle.setText(R.string.feedback);
		
		findViewById(R.id.actionbar_left_btn).setBackgroundResource(R.drawable.nav_back);
		btnSend = (Button)findViewById(R.id.actionbar_right_btn);
		btnSend.setText(R.string.feedback_send);
		btnSend.setTextColor(getResources().getColor(R.color.feedback_send_text_color));
		
		etAdvice = (EditText)findViewById(R.id.feedback_content_edit);
		etContact = (EditText)findViewById(R.id.feedback_contact_edit);
	}
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.actionbar_left_btn:
			finish();
			break;
			
		case R.id.actionbar_right_btn:
			prepareToSend();
			break;

		default:
			break;
		}
	}

	private void prepareToSend() {
		String advice = etAdvice.getText().toString();
		String contact = etContact.getText().toString();
		if(TextUtils.isEmpty(advice) || TextUtils.isEmpty(contact)){
			Toast.makeText(this, "输入不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		
		AccountMgr.getInstance().sendFeedback(mUserInfo.strAccountId, contact, "", advice);
	}
	
	@Override
	protected void onDestroy() {
		
		if(mIFeedbackListener != null){
			AccountMgr.getInstance().setIFeedbackListener(null);
		}
		
		super.onDestroy();
	}
	
}
