package com.runrunfast.homegym.record;

import com.runrunfast.homegym.dao.MyFinishDao;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;

public class RecordUtil {

	public static ArrayList<BaseRecordData> getRecordDataOfDay(String strDay, String uid) {
		ArrayList<BaseRecordData> baseRecordDataForUiList = new ArrayList<BaseRecordData>();
		// 获取该天记录不同的课程数量
		ArrayList<String> courseIdList = MyFinishDao.getInstance().getFinishInfoDistinctCourseIdDependsDay(Globle.gApplicationContext, uid, strDay);
		
		int courseIdSize = courseIdList.size();
		// 根据不同的课程id，组成界面需要的不同的list
		for(int i=0; i<courseIdSize; i++){
			String courseId = courseIdList.get(i);
			ArrayList<BaseRecordData> baseRecordDataList = MyFinishDao.getInstance().getFinishInfoList(Globle.gApplicationContext,uid, courseId, strDay);
			// 取出第一个课程的第一个，组成界面显示的时间课程信息头
			RecordDataUnit firstDataUnit = (RecordDataUnit) baseRecordDataList.get(0);
			if(i == 0){
				RecordDataDate recordDataDate = new RecordDataDate();
				recordDataDate.strDate = strDay;
				recordDataDate.strCourseName = firstDataUnit.strCourseName;
				
				int dataSize = baseRecordDataList.size();
				int totalConsumeTime = 0;
				for(int j=0; j<dataSize; j++){
					RecordDataUnit recordDataUnit = (RecordDataUnit) baseRecordDataList.get(j);
					totalConsumeTime = totalConsumeTime + recordDataUnit.iConsumeTime;
				}
				recordDataDate.iConsumeTime = totalConsumeTime;
				
				baseRecordDataList.add(0, recordDataDate);
//				mBaseRecordDataList.addAll(baseRecordDataList);
				baseRecordDataForUiList.addAll(baseRecordDataList);
			}else{// 取出其他课程的第一个，组成界面显示的课程信息头
				RecordDataPlan recordDataPlan = new RecordDataPlan();
				recordDataPlan.strCoursId = firstDataUnit.strCoursId;
				recordDataPlan.strCourseName = firstDataUnit.strCourseName;
				
				int dataSize = baseRecordDataList.size();
				int totalConsumeTime = 0;
				for(int j=0; j<dataSize; j++){
					RecordDataUnit recordDataUnit = (RecordDataUnit) baseRecordDataList.get(j);
					totalConsumeTime = totalConsumeTime + recordDataUnit.iConsumeTime;
				}
				recordDataPlan.iConsumeTime = totalConsumeTime;
				baseRecordDataList.add(0, recordDataPlan);
//				mBaseRecordDataList.addAll(baseRecordDataList);
				baseRecordDataForUiList.addAll(baseRecordDataList);
			}
		}
		
		return baseRecordDataForUiList;
	}
	
}
