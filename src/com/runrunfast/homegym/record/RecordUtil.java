package com.runrunfast.homegym.record;

import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.bean.Course.ActionDetail;
import com.runrunfast.homegym.bean.Course.GroupDetail;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.MyTrainRecordDao;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;

public class RecordUtil {

	public static ArrayList<BaseRecordData> getBaseRecordDataList(String strYearMonth, String uid){
		ArrayList<String> dateListOfMonth = MyTrainRecordDao.getInstance().getRecordDistinctDateDependsMonth(Globle.gApplicationContext, uid, strYearMonth);
		
		ArrayList<BaseRecordData> baseRecordDataForUiList = new ArrayList<BaseRecordData>();
		
		int dateSize = dateListOfMonth.size();
		
		for(int i=0; i<dateSize; i++){
			String strDate = dateListOfMonth.get(i);
			ArrayList<TrainRecord> recordList = MyTrainRecordDao.getInstance().getRecordListDependDateFromDb(Globle.gApplicationContext, uid, strDate);
			
			int recordSize = recordList.size();
			for(int j=0; j<recordSize; j++){
				TrainRecord record = recordList.get(j);
				
				if(j == 0){
					RecordDataDate recordDataDate = getRecordDataDate(record);
					baseRecordDataForUiList.add(recordDataDate);
					
					int actionSize = record.action_detail.size();
					for(int k=0; k<actionSize; k++){
						ActionDetail actionDetail = record.action_detail.get(k);
						RecordDataAction recordDataAction = gerRecordDataAction(actionDetail);
						baseRecordDataForUiList.add(recordDataAction);
					}
				}else{
					RecordDataCourse recordDataCourse = new RecordDataCourse();
					recordDataCourse.strCourseName = record.course_name;
					recordDataCourse.iConsumeTime = record.finish_time;
					baseRecordDataForUiList.add(recordDataCourse);
					
					int actionSize = record.action_detail.size();
					for(int k=0; k<actionSize; k++){
						ActionDetail actionDetail = record.action_detail.get(k);
						RecordDataAction recordDataAction = gerRecordDataAction(actionDetail);
						baseRecordDataForUiList.add(recordDataAction);
					}
				}
			}
			
		}
		
		return baseRecordDataForUiList;
	}


	private static RecordDataAction gerRecordDataAction(ActionDetail actionDetail) {
		RecordDataAction recordDataAction = new RecordDataAction();
		
		Action action = ActionDao.getInstance().getActionFromDb(Globle.gApplicationContext, actionDetail.action_id);
		
		recordDataAction.actionId = action.action_id;
		recordDataAction.actionName = action.action_name;
		recordDataAction.groupCount = actionDetail.group_detail.size();
		
		float actionKcal = 0;
		for(int i=0; i<recordDataAction.groupCount; i++){
			GroupDetail groupDetail = actionDetail.group_detail.get(i);
			actionKcal = actionKcal + groupDetail.kcal;
		}
		
		recordDataAction.totalKcal = actionKcal;
		recordDataAction.groupDetailList = actionDetail.group_detail;
		
		return recordDataAction;
	}


	private static RecordDataDate getRecordDataDate(TrainRecord record) {
		RecordDataDate recordDataDate = new RecordDataDate();
		recordDataDate.strDate = record.actual_date;
		recordDataDate.iConsumeTime = record.finish_time;
//		recordDataDate.strCoursId = record.course_id;
		recordDataDate.strCourseName = record.course_name;
		return recordDataDate;
	}
		
	
//	public static ArrayList<BaseRecordData> getRecordDataOfDay(String strDay, String uid) {
//		ArrayList<BaseRecordData> baseRecordDataForUiList = new ArrayList<BaseRecordData>();
//		// 获取该天记录不同的课程数量
//		ArrayList<String> courseIdList = MyFinishDao.getInstance().getFinishInfoDistinctCourseIdDependsDay(Globle.gApplicationContext, uid, strDay);
//		
//		int courseIdSize = courseIdList.size();
//		// 根据不同的课程id，组成界面需要的不同的list
//		for(int i=0; i<courseIdSize; i++){
//			String courseId = courseIdList.get(i);
//			ArrayList<BaseRecordData> baseRecordDataList = MyFinishDao.getInstance().getFinishInfoList(Globle.gApplicationContext,uid, courseId, strDay);
//			// 取出第一个课程的第一个，组成界面显示的时间课程信息头
//			RecordDataUnit firstDataUnit = (RecordDataUnit) baseRecordDataList.get(0);
//			if(i == 0){
//				RecordDataDate recordDataDate = new RecordDataDate();
//				recordDataDate.strDate = strDay;
//				recordDataDate.strCourseName = firstDataUnit.strCourseName;
//				
//				int dataSize = baseRecordDataList.size();
//				int totalConsumeTime = 0;
//				for(int j=0; j<dataSize; j++){
//					RecordDataUnit recordDataUnit = (RecordDataUnit) baseRecordDataList.get(j);
//					totalConsumeTime = totalConsumeTime + recordDataUnit.iConsumeTime;
//				}
//				recordDataDate.iConsumeTime = totalConsumeTime;
//				
//				baseRecordDataList.add(0, recordDataDate);
////				mBaseRecordDataList.addAll(baseRecordDataList);
//				baseRecordDataForUiList.addAll(baseRecordDataList);
//			}else{// 取出其他课程的第一个，组成界面显示的课程信息头
//				RecordDataPlan recordDataPlan = new RecordDataPlan();
//				recordDataPlan.strCoursId = firstDataUnit.strCoursId;
//				recordDataPlan.strCourseName = firstDataUnit.strCourseName;
//				
//				int dataSize = baseRecordDataList.size();
//				int totalConsumeTime = 0;
//				for(int j=0; j<dataSize; j++){
//					RecordDataUnit recordDataUnit = (RecordDataUnit) baseRecordDataList.get(j);
//					totalConsumeTime = totalConsumeTime + recordDataUnit.iConsumeTime;
//				}
//				recordDataPlan.iConsumeTime = totalConsumeTime;
//				baseRecordDataList.add(0, recordDataPlan);
////				mBaseRecordDataList.addAll(baseRecordDataList);
//				baseRecordDataForUiList.addAll(baseRecordDataList);
//			}
//		}
//		
//		return baseRecordDataForUiList;
//	}
	
}
