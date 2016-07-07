package com.runrunfast.homegym.course;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.runrunfast.homegym.R;

import java.util.ArrayList;

public class CurrentDayTrainAdapter extends BaseAdapter {
	private ArrayList<CurrentDayTrainContentInfo> mCurrentDayTrainContentInfoList;
	private LayoutInflater mInflater;
	private Context mContext;
	
	public CurrentDayTrainAdapter(Context context, ArrayList<CurrentDayTrainContentInfo> currentDayTrainContentInfos){
		this.mContext = context;
		this.mCurrentDayTrainContentInfoList = currentDayTrainContentInfos;
		this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mCurrentDayTrainContentInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return mCurrentDayTrainContentInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.current_day_train_item, null);
			holder = new ViewHolder();
			
			holder.tvTrainName = (TextView)convertView.findViewById(R.id.current_day_train_name);
			holder.tvGroupNum = (TextView)convertView.findViewById(R.id.current_day_group_num);
			holder.tvCountNum = (TextView)convertView.findViewById(R.id.current_day_count_num);
			holder.tvActionNum = (TextView)convertView.findViewById(R.id.current_day_action_num_text);
			holder.ivDifficultLevel1 = (ImageView)convertView.findViewById(R.id.current_day_difficult_level1);
			holder.ivDifficultLevel2 = (ImageView)convertView.findViewById(R.id.current_day_difficult_level2);
			holder.ivDifficultLevel3 = (ImageView)convertView.findViewById(R.id.current_day_difficult_level3);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		CurrentDayTrainContentInfo currentDayTrainContentInfo = mCurrentDayTrainContentInfoList.get(position);
		holder.tvTrainName.setText(currentDayTrainContentInfo.strTrainName);
		holder.tvGroupNum.setText("共" + currentDayTrainContentInfo.iGroupNum + "组");
		holder.tvCountNum.setText(currentDayTrainContentInfo.iCount + mContext.getResources().getString(R.string.count));
		holder.tvActionNum.setText(currentDayTrainContentInfo.strActionNum);
		
		if(currentDayTrainContentInfo.iDifficultLevel == 1){
			holder.ivDifficultLevel1.setBackgroundResource(R.drawable.icon_level_black);
			holder.ivDifficultLevel2.setBackgroundResource(R.drawable.icon_level_while);
			holder.ivDifficultLevel3.setBackgroundResource(R.drawable.icon_level_while);
		}else if(currentDayTrainContentInfo.iDifficultLevel == 2){
			holder.ivDifficultLevel1.setBackgroundResource(R.drawable.icon_level_black);
			holder.ivDifficultLevel2.setBackgroundResource(R.drawable.icon_level_black);
			holder.ivDifficultLevel3.setBackgroundResource(R.drawable.icon_level_while);
		}else if(currentDayTrainContentInfo.iDifficultLevel == 3){
			holder.ivDifficultLevel1.setBackgroundResource(R.drawable.icon_level_black);
			holder.ivDifficultLevel2.setBackgroundResource(R.drawable.icon_level_black);
			holder.ivDifficultLevel3.setBackgroundResource(R.drawable.icon_level_black);
		}
		
		return convertView;
	}

	class ViewHolder{
		public TextView tvTrainName;
		public TextView tvGroupNum;
		public TextView tvCountNum;
		public TextView tvActionNum;
		public ImageView ivDifficultLevel1, ivDifficultLevel2, ivDifficultLevel3;
	}
	
}
