package com.runrunfast.homegym.course;

import android.text.TextUtils;

import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.bean.Course.ActionDetail;
import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.bean.MyCourse;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.utils.ConstServer;
import com.runrunfast.homegym.utils.DateUtil;
import com.runrunfast.homegym.utils.FileUtils;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;
import java.util.List;

public class CourseUtil {
	/**
	 * @descript 根据开始日期和天数分布，返回天数对应的日期集合。比如开始日期是2016-07-20，天数集合为{1，2，4}，
	 * 那么返回{2016-07-20，2016-07-21，2016-07-23}
	 * @param strStartDate
	 * @param dayNumList
	 * @return
	 */
	public static ArrayList<String> getCourseDateList(String strStartDate, List<CourseDetail> courseDetails){
		ArrayList<String> courseDateList = new ArrayList<String>();
		int dayListSize = courseDetails.size();
		for(int i=0; i<dayListSize; i++){
			CourseDetail courseDetail = courseDetails.get(i);
			String dateStr = DateUtil.getDateStrOfDayNumFromStartDate(courseDetail.day_num, strStartDate);
			courseDateList.add(dateStr);
		}
		
		return courseDateList;
	}
	
	public static boolean needDownload(MyCourse myCourse){
		boolean needDownload = false;
		ArrayList<Action> allActions = getAllActions(myCourse);
		
		int actionSize = allActions.size();
		for(int i=0; i<actionSize; i++){
			Action action = allActions.get(i);
			ArrayList<String> videoLocalList = new ArrayList<String>();
			// 处理视频video
			if( action.action_video_local == null || action.action_video_local.isEmpty()){
				needDownload = true;
				return needDownload;
			}else{
				int videoSize = action.action_video_local.size();
				for(int k=0; k<videoSize; k++){
					String strUrl = action.action_video_url.get(k);
					
					String saveName = FileUtils.getFileName(strUrl);
					String localAddress = ConstServer.SDCARD_HOMEGYM_ROOT + saveName;
					videoLocalList.add(localAddress);
					if( !FileUtils.isFileExist(action.action_video_local.get(k)) ){
						needDownload = true;
						return needDownload;
					}
				}
			}
			// 处理音频audio
			String audioLocalLocation = action.action_audio_local;
			String audioUrlLocation = action.action_audio_url;
			if( TextUtils.isEmpty(audioLocalLocation) || !FileUtils.isFileExist(audioLocalLocation) ){
				if( !TextUtils.isEmpty(audioUrlLocation) ){
					needDownload = true;
					return needDownload;
				}
			}
		}
		return needDownload;
	}
	
	private static ArrayList<Action> getAllActions(MyCourse myCourse) {
		List<CourseDetail> courseDetailList = myCourse.course_detail;
		ArrayList<String> mAllActionIdList = new ArrayList<String>();
		ArrayList<Action> mAllActionList = new ArrayList<Action>();
		
		int dayDistributionSize = courseDetailList.size();
		for(int i=0; i<dayDistributionSize; i++){
			CourseDetail courseDetail = courseDetailList.get(i);
			int actionSize = courseDetail.action_detail.size();
			for(int k=0; k<actionSize; k++){
				ActionDetail actionDetail = courseDetail.action_detail.get(k);
				if( !mAllActionIdList.contains(actionDetail.action_id) ){
					mAllActionIdList.add(actionDetail.action_id);
					Action action = ActionDao.getInstance().getActionFromDb(Globle.gApplicationContext, actionDetail.action_id);
					mAllActionList.add(action);
				}
			}
		}
		return mAllActionList;
	}
	
}
