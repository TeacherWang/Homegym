package com.runrunfast.homegym.course;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.runrunfast.homegym.R;

public class ActionSetAdapter extends BaseAdapter {

	private ArrayList<ActionInfo> mTrainActionInfoList;
	private LayoutInflater mInflater;
	private Context mContext;
	
	private ITrainActionItemListener mITrainActionItemListener;
	
	public ActionSetAdapter(Context context, ArrayList<ActionInfo> trainActionInfos){
		setData(trainActionInfos);
		this.mInflater = LayoutInflater.from(context);
		this.mContext = context;
	}
	
	private void setData(ArrayList<ActionInfo> trainActionInfos){
		if(trainActionInfos == null){
			mTrainActionInfoList = new ArrayList<ActionInfo>();
		}else{
			mTrainActionInfoList = trainActionInfos;
		}
	}
	
	public void updateData(ArrayList<ActionInfo> trainActionInfos){
		setData(trainActionInfos);
		notifyDataSetChanged();
	}
	
	public interface ITrainActionItemListener{
		void onCountClicked(int position);
		void onToolWeightClicked(int position);
	}
	
	public void setOnITrainActionItemListener(ITrainActionItemListener iTrainActionItemListener){
		this.mITrainActionItemListener = iTrainActionItemListener;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mTrainActionInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mTrainActionInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.train_action_set_item, null);
			holder.tvGroupNum = (TextView)convertView.findViewById(R.id.train_action_set_item_group_num);
			holder.tvCount = (TextView)convertView.findViewById(R.id.train_action_set_item_count_num);
			holder.tvWeight = (TextView)convertView.findViewById(R.id.train_action_set_item_tool_weight);
			holder.tvKcal = (TextView)convertView.findViewById(R.id.train_action_set_item_burning);
			holder.layout = (LinearLayout)convertView.findViewById(R.id.train_action_set_item_layout);
			
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		
		ActionInfo trainActionInfo = mTrainActionInfoList.get(position);
		
		if( (position % 2) == 0 ){
			holder.layout.setBackgroundColor(mContext.getResources().getColor(R.color.my_course_bg));
		}else{
			holder.layout.setBackgroundColor(mContext.getResources().getColor(R.color.train_action_set_item_deep_color));
		}
		holder.tvGroupNum.setText("第" + trainActionInfo.iGroupNum + "组");
		holder.tvCount.setText(String.valueOf(trainActionInfo.iCount));
		holder.tvWeight.setText(String.valueOf(trainActionInfo.iToolWeight));
		holder.tvKcal.setText(String.valueOf(trainActionInfo.iBurning));
		
		holder.tvCount.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mITrainActionItemListener != null){
					mITrainActionItemListener.onCountClicked(position);
				}
			}
		});
		
		holder.tvWeight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mITrainActionItemListener != null){
					mITrainActionItemListener.onToolWeightClicked(position);
				}
			}
		});
		
		return convertView;
	}

	class ViewHolder{
		public TextView tvGroupNum;
		public TextView tvCount;
		public TextView tvWeight;
		public TextView tvKcal;
		public LinearLayout layout;
	}
	
}
