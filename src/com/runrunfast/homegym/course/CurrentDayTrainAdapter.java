package com.runrunfast.homegym.course;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.runrunfast.homegym.R;
import com.runrunfast.homegym.account.DataTransferUtil;
import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.bean.Course.ActionDetail;
import com.runrunfast.homegym.bean.Course.GroupDetail;
import com.runrunfast.homegym.utils.BitmapUtils;
import com.runrunfast.homegym.utils.ConstServer;
import com.runrunfast.homegym.utils.FileUtils;
import com.runrunfast.homegym.utils.ImageLoadingActionListener;
import com.runrunfast.homegym.utils.ImageWorker;

import java.io.File;
import java.util.ArrayList;

public class CurrentDayTrainAdapter extends BaseAdapter {
	private ArrayList<ActionDetail> mCurrentDayActionDetailList;
	private ArrayList<Action> mActionList;
	private LayoutInflater mInflater;
	private Context mContext;
	private ImageWorker mImageWorker;
	
	public CurrentDayTrainAdapter(Context context, ArrayList<ActionDetail> currentDayActionDetails, ArrayList<Action> actions){
		this.mContext = context;
		setData(currentDayActionDetails, actions);
		this.mInflater = LayoutInflater.from(context);
		
		mImageWorker = new ImageWorker(context);
		mImageWorker.enableImageCache();
        mImageWorker.setImageFadeIn(false);
	}
	
	private void setData(ArrayList<ActionDetail> currentDayActionInfos, ArrayList<Action> actions){
		if(currentDayActionInfos == null){
			this.mCurrentDayActionDetailList = new ArrayList<ActionDetail>();
		}else{
			mCurrentDayActionDetailList = currentDayActionInfos;
		}
		
		if(actions == null){
			mActionList = new ArrayList<Action>();
		}else{
			mActionList = actions;
		}
	}
	
	public void updateData(ArrayList<ActionDetail> currentDayActionInfos, ArrayList<Action> actions){
		setData(currentDayActionInfos, actions);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mCurrentDayActionDetailList.size();
	}

	@Override
	public Object getItem(int position) {
		return mCurrentDayActionDetailList.get(position);
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
			
			holder.ivActionImg = (ImageView)convertView.findViewById(R.id.current_day_img);
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
		
		ActionDetail actionDetail = mCurrentDayActionDetailList.get(position);
		Action action = mActionList.get(position);
		holder.tvTrainName.setText(action.action_name);
		holder.tvGroupNum.setText("共" + actionDetail.group_num + "组");
		int totalCount = 0;
		for(int i=0; i<actionDetail.group_num; i++){
			GroupDetail groupDetail = actionDetail.group_detail.get(i);
			totalCount = totalCount + groupDetail.count;
		}
		holder.tvCountNum.setText(totalCount + mContext.getResources().getString(R.string.count));
		holder.tvActionNum.setText( "动作" + (String)DataTransferUtil.numMap.get(position + 1));
		
		if(action.action_difficult == 1){
			holder.ivDifficultLevel1.setBackgroundResource(R.drawable.icon_level_black);
			holder.ivDifficultLevel2.setBackgroundResource(R.drawable.icon_level_while);
			holder.ivDifficultLevel3.setBackgroundResource(R.drawable.icon_level_while);
		}else if(action.action_difficult == 2){
			holder.ivDifficultLevel1.setBackgroundResource(R.drawable.icon_level_black);
			holder.ivDifficultLevel2.setBackgroundResource(R.drawable.icon_level_black);
			holder.ivDifficultLevel3.setBackgroundResource(R.drawable.icon_level_while);
		}else{
			holder.ivDifficultLevel1.setBackgroundResource(R.drawable.icon_level_black);
			holder.ivDifficultLevel2.setBackgroundResource(R.drawable.icon_level_black);
			holder.ivDifficultLevel3.setBackgroundResource(R.drawable.icon_level_black);
		}
		
		if( !TextUtils.isEmpty(action.action_img_local) && FileUtils.isFileExist(action.action_img_local) ){
			mImageWorker.loadImage(action.action_img_local, holder.ivActionImg);
			return convertView;
		}
		
		String filePath = ConstServer.SDCARD_HOMEGYM_ROOT + action.action_id + File.separator + FileUtils.getFileName(action.action_img_url);
		
		ImageLoader.getInstance().displayImage(action.action_img_url,
				holder.ivActionImg, BitmapUtils.initActionImageLoader(),
				new ImageLoadingActionListener(filePath, action.action_id));
		
		return convertView;
	}

	class ViewHolder{
		public ImageView ivActionImg;
		public TextView tvTrainName;
		public TextView tvGroupNum;
		public TextView tvCountNum;
		public TextView tvActionNum;
		public ImageView ivDifficultLevel1, ivDifficultLevel2, ivDifficultLevel3;
	}
	
}
