package com.runrunfast.homegym.course;

import java.util.ArrayList;

import com.runrunfast.homegym.dao.CourseDao;
import com.runrunfast.homegym.utils.Globle;

import android.os.Handler;
import android.os.HandlerThread;
import android.provider.Settings.Global;
import android.util.Log;

public class DataInit {
	private static final String TAG = "DataInit";
	
	private volatile static DataInit instance;
	private static Object lockObject = new Object();
	
	private HandlerThread mHandlerThread;
	private Handler mWorkHandler;
	
	public static DataInit getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new DataInit();
				}
			}
		}
		return instance;
	}
	
	private DataInit(){
		mHandlerThread = new HandlerThread(TAG);
		mHandlerThread.start();
		mWorkHandler = new Handler(mHandlerThread.getLooper());
	}
	
	public void initData(){
		ArrayList<CourseInfo> courseInfoList = CourseDao.getInstance().getCourseInfoListFromDb(Globle.gApplicationContext);
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
		
	}
	
	private ArrayList<CourseInfo> getInitCourseInfo(){
		ArrayList<CourseInfo> courseInfoList = new ArrayList<CourseInfo>();
		// course1
		CourseInfo courseInfo1 = new CourseInfo();
		courseInfo1.courseId = "c1";
		courseInfo1.isMyCourse = false;
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
		courseInfo2.isMyCourse = false;
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
		courseInfo3.isMyCourse = false;
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
		courseInfo4.isMyCourse = false;
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
	
}
