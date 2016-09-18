package com.runrunfast.homegym.BtDevice;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.runrunfast.homegym.R;

public class BtDeviceAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<BluetoothDevice> mBtDeviceList;
	
	public BtDeviceAdapter(Context context, ArrayList<BluetoothDevice> btList) {
		mContext = context;
		mInflater = LayoutInflater.from(context);
		setData(btList);
	}

	private void setData(ArrayList<BluetoothDevice> btList){
		if(btList != null){
			mBtDeviceList = btList;
		}else{
			mBtDeviceList = new ArrayList<BluetoothDevice>();
		}
	}
	
	public void updateData(ArrayList<BluetoothDevice> btList){
		setData(btList);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mBtDeviceList.size();
	}

	@Override
	public Object getItem(int position) {
		return mBtDeviceList.get(position);
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
		
		BluetoothDevice btDevice = mBtDeviceList.get(position);
		viewHolder.tvBtName.setText(btDevice.getName());
		
		return convertView;
	}
	
	private class ViewHolder {
		public TextView tvBtName;
	}

}
