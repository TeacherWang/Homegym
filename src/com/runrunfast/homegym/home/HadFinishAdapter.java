package com.runrunfast.homegym.home;

import java.util.ArrayList;

import com.runrunfast.homegym.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HadFinishAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<HadFinishInfo> mHadFinishInfoList;
	
	public HadFinishAdapter(Context context, ArrayList<HadFinishInfo> hadfinishList){
		this.mHadFinishInfoList = hadfinishList;
		this.mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return mHadFinishInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return mHadFinishInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.had_finish_item, null);
			holder = new ViewHolder();
			holder.ivBg = (ImageView)convertView.findViewById(R.id.had_finish_img);
			holder.tvPlanName = (TextView)convertView.findViewById(R.id.had_finish_plan_name_text);
			holder.tvAction = (TextView)convertView.findViewById(R.id.had_finish_action_num_text);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		HadFinishInfo hadFinishInfo = mHadFinishInfoList.get(position);
		
//		holder.ivBg.setBackgroundResource(resid)
		holder.tvPlanName.setText(hadFinishInfo.strTrainName);
		holder.tvAction.setText(hadFinishInfo.strActionNum);
		
		return convertView;
	}
	
	class ViewHolder{
		public ImageView ivBg;
		public TextView tvPlanName;
		public TextView tvAction;
	}

}
