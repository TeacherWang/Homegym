package com.runrunfast.homegym.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.DataTransferUtil;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.course.ActionInfo;

import java.util.ArrayList;

public class HadFinishAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<Action> mHadFinishInfoList;
	
	public HadFinishAdapter(Context context, ArrayList<Action> hadfinishList){
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
			holder.tvActionName = (TextView)convertView.findViewById(R.id.had_finish_plan_name_text);
			holder.tvAction = (TextView)convertView.findViewById(R.id.had_finish_action_num_text);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		Action hadFinishInfo = mHadFinishInfoList.get(position);
		
//		holder.ivBg.setBackgroundResource(resid)
		holder.tvActionName.setText(hadFinishInfo.action_name);
		holder.tvAction.setText("动作" + DataTransferUtil.numMap.get(position + 1));
		
		return convertView;
	}
	
	class ViewHolder{
		public ImageView ivBg;
		public TextView tvActionName;
		public TextView tvAction;
	}

}
