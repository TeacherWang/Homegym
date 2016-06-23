package com.runrunfast.homegym.widget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runrunfast.homegym.R;

public class DialogActivity extends Activity {
	private final String TAG = "DialogActivity";

	public static final int RSP_CANCEL = 1;
	public static final int RSP_CONFIRM = 2;
	
	public static final String KEY_CONTENT = "key_content";
	public static final String KEY_CANCEL = "key_cancel";
	public static final String KEY_CONFIRM = "key_confirm";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		
		setData();
	}

	private void setData() {
		String content = getIntent().getStringExtra(KEY_CONTENT);
		String cancel = getIntent().getStringExtra(KEY_CANCEL);
		String confirm = getIntent().getStringExtra(KEY_CONFIRM);
		
		Log.d(TAG, "onNewIntent, content = " + content + ", cancel = " + cancel + ", confirm = " + confirm);
		
		((TextView)findViewById(R.id.dialog_content_text)).setText(content);
		((Button)findViewById(R.id.dialog_cancel)).setText(cancel);
		((Button)findViewById(R.id.dialog_confirm)).setText(confirm);
	}
	
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.dialog_cancel:
			rspCancel();
			break;
			
		case R.id.dialog_confirm:
			rspConfirm();
			break;

		default:
			break;
		}
	}
	
	private void rspConfirm() {
		setResult(RSP_CONFIRM);
		finish();
	}

	private void rspCancel() {
		setResult(RSP_CANCEL);
		finish();
	}
}
