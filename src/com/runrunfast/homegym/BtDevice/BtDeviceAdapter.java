package com.runrunfast.homegym.BtDevice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.runrunfast.homegym.R;

import java.util.ArrayList;

public class BtDeviceAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<BtInfo> mBtList;
	
	public BtDeviceAdapter(Context context, ArrayList<BtInfo> btList) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mBtList = btList;
	}

	@Override
	public int getCount() {
		return mBtList.size();
	}

	@Override
	public Object getItem(int position) {
		return mBtList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.bt_device_item, null);
			viewHolder.tvBtName = (TextView)convertView.findViewById(R.id.bt_device_item_name_text);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		BtInfo btInfo = mBtList.get(position);
		viewHolder.tvBtName.setText(btInfo.btName);
		
		return convertView;
	}
	
	private class ViewHolder {
		public TextView tvBtName;
	}

}
