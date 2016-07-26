package com.runrunfast.homegym.course;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.DataTransferUtil;

public class CourseTrainAdapter extends BaseAdapter {
	
	private ArrayList<ActionInfo> mActinoInfoList;
	private LayoutInflater mInflater;
	private Context mContext;
	
	public CourseTrainAdapter(Context context, ArrayList<ActionInfo> actinoInfoList){
		this.mContext = context;
		this.mActinoInfoList = actinoInfoList;
		this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mActinoInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return mActinoInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.course_training_item, null);
			holder = new ViewHolder();
			
			holder.tvTrainName = (TextView)convertView.findViewById(R.id.course_train_name_text);
			holder.tvTrainActionNum = (TextView)convertView.findViewById(R.id.course_train_action_num_text);
			holder.tvTrainPosition = (TextView)convertView.findViewById(R.id.course_train_position_text);
			holder.tvDiffcult = (TextView)convertView.findViewById(R.id.course_train_diffcult_text);
			holder.tvTime = (TextView)convertView.findViewById(R.id.course_train_time_text);
			holder.ivDiffcultLevel1 = (ImageView)convertView.findViewById(R.id.course_train_diffcult_level1_img);
			holder.ivDiffcultLevel2 = (ImageView)convertView.findViewById(R.id.course_train_diffcult_level2_img);
			holder.ivDiffcultLevel3 = (ImageView)convertView.findViewById(R.id.course_train_diffcult_level3_img);
			holder.ivDiffcultLevel4 = (ImageView)convertView.findViewById(R.id.course_train_diffcult_level4_img);
			holder.ivDiffcultLevel5 = (ImageView)convertView.findViewById(R.id.course_train_diffcult_level5_img);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		ActionInfo actionInfo = mActinoInfoList.get(position);
		holder.tvTrainName.setText(actionInfo.actionName);
		holder.tvTrainPosition.setText(actionInfo.strTrainPosition);
		holder.tvTrainActionNum.setText("动作" + DataTransferUtil.numMap.get(position + 1));
		holder.tvTime.setText(String.valueOf(actionInfo.iTime) + mContext.getResources().getString(R.string.minute)
				+ " " + String.valueOf(actionInfo.iDefaultTotalKcal) + mContext.getResources().getString(R.string.kcal));
		holder.tvDiffcult.setText(R.string.difficult);
		if(actionInfo.iDiffcultLevel == 1){
			holder.ivDiffcultLevel1.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel2.setVisibility(View.INVISIBLE);
			holder.ivDiffcultLevel3.setVisibility(View.INVISIBLE);
			holder.ivDiffcultLevel4.setVisibility(View.INVISIBLE);
			holder.ivDiffcultLevel5.setVisibility(View.INVISIBLE);
		}else if(actionInfo.iDiffcultLevel == 2){
			holder.ivDiffcultLevel1.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel2.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel3.setVisibility(View.INVISIBLE);
			holder.ivDiffcultLevel4.setVisibility(View.INVISIBLE);
			holder.ivDiffcultLevel5.setVisibility(View.INVISIBLE);
		}else if(actionInfo.iDiffcultLevel == 3){
			holder.ivDiffcultLevel1.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel2.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel3.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel4.setVisibility(View.INVISIBLE);
			holder.ivDiffcultLevel5.setVisibility(View.INVISIBLE);
		}else if(actionInfo.iDiffcultLevel == 4){
			holder.ivDiffcultLevel1.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel2.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel3.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel4.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel5.setVisibility(View.INVISIBLE);
		}else if(actionInfo.iDiffcultLevel == 5){
			holder.ivDiffcultLevel1.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel2.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel3.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel4.setVisibility(View.VISIBLE);
			holder.ivDiffcultLevel5.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}

	class ViewHolder{
		public TextView tvTrainName;
		public TextView tvTrainActionNum;
		public TextView tvTrainPosition;
		public TextView tvDiffcult;
		public TextView tvTime;
		public ImageView ivDiffcultLevel1, ivDiffcultLevel2, ivDiffcultLevel3, ivDiffcultLevel4, ivDiffcultLevel5;
	}
	
}
