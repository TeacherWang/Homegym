package com.runrunfast.homegym.widget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.runrunfast.homegym.R;

public class HorizonDialogActivity extends Activity {
	private final String TAG = "DialogActivity";

	public static final int RSP_CANCEL = 1;
	public static final int RSP_CONFIRM = 2;
	
	public static final String KEY_CONTENT_COLOR = "key_content_color";
	public static final String KEY_CONTENT = "key_content";
	public static final String KEY_CANCEL = "key_cancel";
	public static final String KEY_CONFIRM = "key_confirm";
	
	private TextView tvContent;
	private Button btnCancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		
		setData();
	}

	private void setData() {
		int colorId = getIntent().getIntExtra(KEY_CONTENT_COLOR, -1);
		String content = getIntent().getStringExtra(KEY_CONTENT);
		String cancel = getIntent().getStringExtra(KEY_CANCEL);
		String confirm = getIntent().getStringExtra(KEY_CONFIRM);
		
		btnCancel = (Button)findViewById(R.id.dialog_cancel);
		
		tvContent = (TextView)findViewById(R.id.dialog_content_text);
		tvContent.setText(content);
		
		if(cancel == null){
			btnCancel.setVisibility(View.GONE);
		}else{
			btnCancel.setText(cancel);
		}
		
		if(colorId != -1){
			tvContent.setTextColor(colorId);
		}
		
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
