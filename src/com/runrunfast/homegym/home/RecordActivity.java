package com.runrunfast.homegym.home;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.runrunfast.homegym.R;

public class RecordActivity extends Activity implements OnClickListener{
	private Button btnShare;
	private RelativeLayout rlSelectionWhichLayout;
	private TextView tvSelectMonth, tvSelectYear, tvSelectTotal;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		
		initView();
	}
	
	private void initView() {
		findViewById(R.id.actionbar_left_btn).setBackgroundResource(R.drawable.nav_back);
		btnShare = (Button)findViewById(R.id.actionbar_right_btn);
		btnShare.setBackgroundResource(R.drawable.record_share);
		((TextView)findViewById(R.id.actionbar_title)).setText(R.string.trainnig_record);
//		ivSelectImg = (ImageView)findViewById(R.id.record_selection_img);
		
		tvSelectMonth = (TextView)findViewById(R.id.record_selection_month);
		tvSelectYear = (TextView)findViewById(R.id.record_selection_year);
		tvSelectTotal = (TextView)findViewById(R.id.record_selection_total);
		tvSelectMonth.setOnClickListener(this);
		tvSelectYear.setOnClickListener(this);
		tvSelectTotal.setOnClickListener(this);
		
		rlSelectionWhichLayout = (RelativeLayout)findViewById(R.id.record_selection_which_layout);
		
		selectMonth();
	}
	@Override
	public void onClick(View view){
		switch (view.getId()) {
		case R.id.record_selection_month:
			selectMonth();
			break;
			
		case R.id.record_selection_year:
			selectYear();
			break;
			
		case R.id.record_selection_total:
			selectTotal();
			break;
			
		case R.id.actionbar_left_btn:
			finish();
			break;
			
		case R.id.actionbar_right_btn:
			showShare();
			break;

		default:
			break;
		}
	}

	private void selectTotal() {
		tvSelectMonth.setTextColor(getResources().getColor(R.color.record_selectbox_time_normal));
		tvSelectYear.setTextColor(getResources().getColor(R.color.record_selectbox_time_normal));
		tvSelectTotal.setTextColor(getResources().getColor(R.color.white));
		
		rlSelectionWhichLayout.removeAllViews();
		ImageView img = new ImageView(this);
		img.setBackgroundResource(R.drawable.record_select_3);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		img.setLayoutParams(params);
		rlSelectionWhichLayout.addView(img);
	}

	private void selectYear() {
		tvSelectMonth.setTextColor(getResources().getColor(R.color.record_selectbox_time_normal));
		tvSelectYear.setTextColor(getResources().getColor(R.color.white));
		tvSelectTotal.setTextColor(getResources().getColor(R.color.record_selectbox_time_normal));
		
		rlSelectionWhichLayout.removeAllViews();
		ImageView img = new ImageView(this);
		img.setBackgroundResource(R.drawable.record_select_2);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		params.width = LayoutParams.WRAP_CONTENT;
//		params.height = LayoutParams.WRAP_CONTENT;
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		img.setLayoutParams(params);
		rlSelectionWhichLayout.addView(img);
	}

	private void selectMonth() {
		tvSelectMonth.setTextColor(getResources().getColor(R.color.white));
		tvSelectYear.setTextColor(getResources().getColor(R.color.record_selectbox_time_normal));
		tvSelectTotal.setTextColor(getResources().getColor(R.color.record_selectbox_time_normal));
		
		rlSelectionWhichLayout.removeAllViews();
		ImageView img = new ImageView(this);
		img.setBackgroundResource(R.drawable.record_select_1);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		img.setLayoutParams(params);
		rlSelectionWhichLayout.addView(img);
	}

	private void showShare() {
		
	}

}
