package com.runrunfast.homegym.course;

import java.util.ArrayList;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.CourseDao;
import com.runrunfast.homegym.utils.Globle;

public class DataIniter {
	private static final String TAG = "DataInit";
	
	private volatile static DataIniter instance;
	private static Object lockObject = new Object();
	
	private HandlerThread mHandlerThread;
	private Handler mWorkHandler;
	
	public static DataIniter getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new DataIniter();
				}
			}
		}
		return instance;
	}
	
	private DataIniter(){
		mHandlerThread = new HandlerThread(TAG);
		mHandlerThread.start();
		mWorkHandler = new Handler(mHandlerThread.getLooper());
	}
	
	public void initData(){
		mWorkHandler.post(new Runnable() {
			
			@Override
			public void run() {
				initDbData();
			}
		});
	}

	private void initDbData() {
		ArrayList<CourseInfo> courseInfoList = CourseDao.getInstance().getCourseInfoListFromDb(Globle.gApplicationContext);
		ArrayList<ActionInfo> actionInfoList = ActionDao.getInstance().getActionInfoListFromDb(Globle.gApplicationContext);
		if(courseInfoList != null && courseInfoList.size() > 0){
			Log.d(TAG, "initData, db had data, ignore");
			return;
		}
		// 初始化课程信息，保存数据库
		courseInfoList = getInitCourseInfo();
		int courseInfoSize = courseInfoList.size();
		for(int i=0; i<courseInfoSize; i++){
			CourseInfo courseInfo = courseInfoList.get(i);
			CourseDao.getInstance().saveCourseInfoToDb(Globle.gApplicationContext, courseInfo);
		}
		// 初始化动作信息，保存数据库
		actionInfoList = getInitActionInfo();
		int actionInfoSize = actionInfoList.size();
		for(int i=0; i<actionInfoSize; i++){
			ActionInfo actionInfo = actionInfoList.get(i);
			ActionDao.getInstance().saveActionInfoToDb(Globle.gApplicationContext, actionInfo);
		}
	}
	
	private ArrayList<CourseInfo> getInitCourseInfo(){
		ArrayList<CourseInfo> courseInfoList = new ArrayList<CourseInfo>();
		// course1
		CourseInfo courseInfo1 = new CourseInfo();
		courseInfo1.courseId = "c1";
		courseInfo1.isNew = true;
		courseInfo1.courseName = "塑性训练";
		
		courseInfo1.actionIds.add("a1");
		courseInfo1.actionIds.add("a2");
		courseInfo1.actionIds.add("a3");
		
		courseInfo1.dateNumList.add("1");
		courseInfo1.dateNumList.add("2");
		courseInfo1.dateNumList.add("4");
		courseInfo1.dateNumList.add("5");
		
		courseInfo1.dateActionIdList.add("a1;a2;a3");
		courseInfo1.dateActionIdList.add("a1;a2");
		courseInfo1.dateActionIdList.add("a1;a3");
		courseInfo1.dateActionIdList.add("a2;a3");
		
		courseInfo1.isRecommend = true;
		courseInfo1.courseQuality = CourseInfo.QUALITY_EXCELLENT;
		courseInfoList.add(courseInfo1);
		
		// course2
		CourseInfo courseInfo2 = new CourseInfo();
		courseInfo2.courseId = "c2";
		courseInfo2.isNew = true;
		courseInfo2.courseName = "21天腹肌雕刻";
		
		courseInfo2.actionIds.add("a1");
		courseInfo2.actionIds.add("a2");
		
		courseInfo2.dateNumList.add("1");
		courseInfo2.dateNumList.add("2");
		courseInfo2.dateNumList.add("3");
		
		courseInfo2.dateActionIdList.add("a1;a2");
		courseInfo2.dateActionIdList.add("a1");
		courseInfo2.dateActionIdList.add("a2");
		
		courseInfo2.isRecommend = false;
		courseInfo2.courseQuality = CourseInfo.QUALITY_NORMAL;
		courseInfoList.add(courseInfo2);
		
		// course3
		CourseInfo courseInfo3 = new CourseInfo();
		courseInfo3.courseId = "c3";
		courseInfo3.isNew = true;
		courseInfo3.courseName = "S型身材速成";
		
		courseInfo3.actionIds.add("a1");
		courseInfo3.actionIds.add("a2");
		courseInfo3.actionIds.add("a4");
		
		courseInfo3.dateNumList.add("1");
		courseInfo3.dateNumList.add("2");
		courseInfo3.dateNumList.add("3");
		
		courseInfo3.dateActionIdList.add("a1;a2;a4");
		courseInfo3.dateActionIdList.add("a1;a2");
		courseInfo3.dateActionIdList.add("a2;a4");
		
		courseInfo3.isRecommend = false;
		courseInfo3.courseQuality = CourseInfo.QUALITY_EXCELLENT;
		courseInfoList.add(courseInfo3);
		
		// course3
		CourseInfo courseInfo4 = new CourseInfo();
		courseInfo4.courseId = "c4";
		courseInfo4.isNew = true;
		courseInfo4.courseName = "人鱼线训练";
		
		courseInfo4.actionIds.add("a1");
		courseInfo4.actionIds.add("a2");
		courseInfo4.actionIds.add("a3");
		courseInfo4.actionIds.add("a4");
		
		courseInfo4.dateNumList.add("1");
		courseInfo4.dateNumList.add("2");
		courseInfo4.dateNumList.add("3");
		
		courseInfo4.dateActionIdList.add("a1;a2;a3;a4");
		courseInfo4.dateActionIdList.add("a1;a2;a3");
		courseInfo4.dateActionIdList.add("a2;a3;a4");
		
		courseInfo4.isRecommend = false;
		courseInfo4.courseQuality = CourseInfo.QUALITY_EXCELLENT;
		courseInfoList.add(courseInfo4);
		
		return courseInfoList;
	}
	
	private ArrayList<ActionInfo> getInitActionInfo(){
		ArrayList<ActionInfo> actionInfoList = new ArrayList<ActionInfo>();
		
		// action1
		ActionInfo actionInfo1 = new ActionInfo();
		actionInfo1.strActionId = "a1";
		actionInfo1.actionName = "坐姿推举";
		actionInfo1.strTrainPosition = "背部 腰部";
		actionInfo1.strTrainDescript = "坚持训练将锻炼到胸大肌和三角肌";
		actionInfo1.iTime = 1000;
		actionInfo1.defaultGroupNum = 4;
		
		actionInfo1.defaultCountList.add("11");
		actionInfo1.defaultCountList.add("10");
		actionInfo1.defaultCountList.add("10");
		actionInfo1.defaultCountList.add("8");
		
		actionInfo1.defaultToolWeightList.add("15");
		actionInfo1.defaultToolWeightList.add("20");
		actionInfo1.defaultToolWeightList.add("20");
		actionInfo1.defaultToolWeightList.add("30");
		
		actionInfo1.defaultBurningList.add("76");
		actionInfo1.defaultBurningList.add("108");
		actionInfo1.defaultBurningList.add("102");
		actionInfo1.defaultBurningList.add("88");
		actionInfoList.add(actionInfo1);
		
		// action2
		ActionInfo actionInfo2 = new ActionInfo();
		actionInfo2.strActionId = "a2";
		actionInfo2.actionName = "高位绳索划船";
		actionInfo2.strTrainPosition = "背部 手臂";
		actionInfo2.strTrainDescript = "坚持训练将锻炼到肱二头肌和三角肌";
		actionInfo2.iTime = 2000;
		actionInfo2.defaultGroupNum = 4;
		
		actionInfo2.defaultCountList.add("12");
		actionInfo2.defaultCountList.add("9");
		actionInfo2.defaultCountList.add("20");
		actionInfo2.defaultCountList.add("8");
		
		actionInfo2.defaultToolWeightList.add("15");
		actionInfo2.defaultToolWeightList.add("20");
		actionInfo2.defaultToolWeightList.add("20");
		actionInfo2.defaultToolWeightList.add("30");
		
		actionInfo2.defaultBurningList.add("76");
		actionInfo2.defaultBurningList.add("208");
		actionInfo2.defaultBurningList.add("202");
		actionInfo2.defaultBurningList.add("88");
		actionInfoList.add(actionInfo2);
		
		// action3
		ActionInfo actionInfo3 = new ActionInfo();
		actionInfo3.strActionId = "a3";
		actionInfo3.actionName = "仰卧起坐";
		actionInfo3.strTrainPosition = "背部 腰部";
		actionInfo3.strTrainDescript = "坚持训练将锻炼到腹肌";
		actionInfo3.iTime = 3000;
		actionInfo3.defaultGroupNum = 4;
		
		actionInfo3.defaultCountList.add("32");
		actionInfo3.defaultCountList.add("30");
		actionInfo3.defaultCountList.add("30");
		actionInfo3.defaultCountList.add("8");
		
		actionInfo3.defaultToolWeightList.add("35");
		actionInfo3.defaultToolWeightList.add("20");
		actionInfo3.defaultToolWeightList.add("20");
		actionInfo3.defaultToolWeightList.add("30");
		
		actionInfo3.defaultBurningList.add("76");
		actionInfo3.defaultBurningList.add("308");
		actionInfo3.defaultBurningList.add("302");
		actionInfo3.defaultBurningList.add("88");
		actionInfoList.add(actionInfo3);
		
		// action1
		ActionInfo actionInfo4 = new ActionInfo();
		actionInfo4.strActionId = "a4";
		actionInfo4.actionName = "坐姿推举";
		actionInfo4.strTrainPosition = "背部 腰部";
		actionInfo4.strTrainDescript = "坚持训练将锻炼到胸大肌和三角肌";
		actionInfo4.iTime = 4000;
		actionInfo4.defaultGroupNum = 4;
		
		actionInfo4.defaultCountList.add("42");
		actionInfo4.defaultCountList.add("40");
		actionInfo4.defaultCountList.add("40");
		actionInfo4.defaultCountList.add("8");
		
		actionInfo4.defaultToolWeightList.add("45");
		actionInfo4.defaultToolWeightList.add("20");
		actionInfo4.defaultToolWeightList.add("20");
		actionInfo4.defaultToolWeightList.add("30");
		
		actionInfo4.defaultBurningList.add("76");
		actionInfo4.defaultBurningList.add("408");
		actionInfo4.defaultBurningList.add("402");
		actionInfo4.defaultBurningList.add("88");
		actionInfoList.add(actionInfo4);
		
		// action5
		ActionInfo actionInfo5 = new ActionInfo();
		actionInfo5.strActionId = "a5";
		actionInfo5.actionName = "卧姿推举";
		actionInfo5.strTrainPosition = "背部 腰部";
		actionInfo5.strTrainDescript = "坚持训练将锻炼到胸大肌和三角肌";
		actionInfo5.iTime = 5000;
		actionInfo5.defaultGroupNum = 4;
		
		actionInfo5.defaultCountList.add("52");
		actionInfo5.defaultCountList.add("50");
		actionInfo5.defaultCountList.add("50");
		actionInfo5.defaultCountList.add("8");
		
		actionInfo5.defaultToolWeightList.add("55");
		actionInfo5.defaultToolWeightList.add("20");
		actionInfo5.defaultToolWeightList.add("20");
		actionInfo5.defaultToolWeightList.add("30");
		
		actionInfo5.defaultBurningList.add("76");
		actionInfo5.defaultBurningList.add("508");
		actionInfo5.defaultBurningList.add("502");
		actionInfo5.defaultBurningList.add("88");
		actionInfoList.add(actionInfo5);
		
		return actionInfoList;
	}
	
}
