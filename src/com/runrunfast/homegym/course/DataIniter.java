package com.runrunfast.homegym.course;

import android.os.Handler;
import android.os.HandlerThread;

import com.runrunfast.homegym.bean.Action;
import com.runrunfast.homegym.bean.Course;
import com.runrunfast.homegym.bean.Course.ActionDetail;
import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.bean.Course.GroupDetail;
import com.runrunfast.homegym.dao.ActionDao;
import com.runrunfast.homegym.dao.CourseDao;
import com.runrunfast.homegym.utils.Globle;

import java.util.ArrayList;

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
		// 测试数据，初始化
		ArrayList<Course> courseList = getInitCourse();
		int courseSize = courseList.size();
		for(int i=0; i<courseSize; i++){
			Course course = courseList.get(i);
			CourseDao.getInstance().saveCourseToDb(Globle.gApplicationContext, course);
		}
		
		ArrayList<Action> actionList = getInitActionList();
		int actionSize = actionList.size();
		for(int i=0; i<actionSize; i++){
			Action action = actionList.get(i);
			ActionDao.getInstance().saveActionToDb(Globle.gApplicationContext, action);
		}
	}
	
	private ArrayList<Course> getInitCourse(){
		ArrayList<Course> courseList = new ArrayList<Course>();
		
		courseList.add(getCourse1());
		courseList.add(getCourse2());
		courseList.add(getCourse3());
		courseList.add(getCourse4());
		
		return courseList;
	}

	private Course getCourse1() {
		Course course1 = new Course();
		course1.course_id = "c1";
		course1.course_name = "塑性训练";
		course1.course_recommend = Course.RECOMMED_COURSE;
		course1.course_quality = Course.QUALITY_EXCELLENT;
		course1.course_new = Course.NEW_COURSE;
		
//		course1.action_ids.add(new ActionId("a1"));
//		course1.action_ids.add(new ActionId("a2"));
//		course1.action_ids.add(new ActionId("a3"));
		
		// 日期分布
		CourseDetail courseDetail1 = new CourseDetail(1);
		courseDetail1.action_detail.add(getActionStengthHigh("a1"));
		courseDetail1.action_detail.add(getActionStengthHigh("a2"));
		courseDetail1.action_detail.add(getActionStengthHigh("a3"));
		
		CourseDetail courseDetail2 = new CourseDetail(2);
		courseDetail2.action_detail.add(getActionStengthNormal("a1"));
		courseDetail2.action_detail.add(getActionStengthHigh("a2"));
		
		CourseDetail courseDetail3 = new CourseDetail(4);
		courseDetail3.action_detail.add(getActionStengthNormal("a1"));
		courseDetail3.action_detail.add(getActionStengthLow("a3"));
		
		CourseDetail courseDetail4 = new CourseDetail(5);
		courseDetail4.action_detail.add(getActionStengthHigh("a2"));
		courseDetail4.action_detail.add(getActionStengthHigh("a3"));
		
		course1.course_detail.add(courseDetail1);
		course1.course_detail.add(courseDetail2);
		course1.course_detail.add(courseDetail3);
		course1.course_detail.add(courseDetail4);
		return course1;
	}
	
	private Course getCourse2() {
		Course course1 = new Course();
		course1.course_id = "c2";
		course1.course_name = "21天腹肌雕刻";
		course1.course_recommend = Course.RECOMMED_COURSE;
		course1.course_quality = Course.QUALITY_NORMAL;
		course1.course_new = Course.NORMAL_COURSE;
		
//		course1.action_ids.add(new ActionId("a1"));
//		course1.action_ids.add(new ActionId("a2"));
		
		// 日期分布
		CourseDetail courseDetail1 = new CourseDetail(1);
		courseDetail1.action_detail.add(getActionStengthHigh("a1"));
		courseDetail1.action_detail.add(getActionStengthHigh("a2"));
		
		CourseDetail courseDetail2 = new CourseDetail(2);
		courseDetail2.action_detail.add(getActionStengthNormal("a1"));
		
		CourseDetail courseDetail3 = new CourseDetail(3);
		courseDetail3.action_detail.add(getActionStengthNormal("a2"));
		
		course1.course_detail.add(courseDetail1);
		course1.course_detail.add(courseDetail2);
		course1.course_detail.add(courseDetail3);
		return course1;
	}
	
	private Course getCourse3() {
		Course course1 = new Course();
		course1.course_id = "c3";
		course1.course_name = "S型身材速成";
		course1.course_recommend = Course.NORMAL_COURSE;
		course1.course_quality = Course.QUALITY_EXCELLENT;
		course1.course_new = Course.NEW_COURSE;
		
//		course1.action_ids.add(new ActionId("a1"));
//		course1.action_ids.add(new ActionId("a2"));
//		course1.action_ids.add(new ActionId("a4"));
		// 日期分布
		CourseDetail courseDetail1 = new CourseDetail(1);
		courseDetail1.action_detail.add(getActionStengthHigh("a1"));
		courseDetail1.action_detail.add(getActionStengthHigh("a2"));
		courseDetail1.action_detail.add(getActionStengthHigh("a4"));
		
		CourseDetail courseDetail2 = new CourseDetail(2);
		courseDetail2.action_detail.add(getActionStengthNormal("a1"));
		courseDetail2.action_detail.add(getActionStengthHigh("a2"));
		
		CourseDetail courseDetail3 = new CourseDetail(3);
		courseDetail3.action_detail.add(getActionStengthNormal("a1"));
		courseDetail3.action_detail.add(getActionStengthLow("a4"));
		
		CourseDetail courseDetail4 = new CourseDetail(4);
		courseDetail4.action_detail.add(getActionStengthHigh("a2"));
		courseDetail4.action_detail.add(getActionStengthHigh("a4"));
		
		course1.course_detail.add(courseDetail1);
		course1.course_detail.add(courseDetail2);
		course1.course_detail.add(courseDetail3);
		course1.course_detail.add(courseDetail4);
		return course1;
	}
	
	private Course getCourse4() {
		Course course1 = new Course();
		course1.course_id = "c4";
		course1.course_name = "人鱼线训练";
		course1.course_recommend = Course.NORMAL_COURSE;
		course1.course_quality = Course.QUALITY_EXCELLENT;
		course1.course_new = Course.NEW_COURSE;
		
//		course1.action_ids.add(new ActionId("a1"));
//		course1.action_ids.add(new ActionId("a2"));
//		course1.action_ids.add(new ActionId("a3"));
//		course1.action_ids.add(new ActionId("a4"));
//		course1.action_ids.add(new ActionId("a5"));
		
		// 日期分布
		CourseDetail courseDetail1 = new CourseDetail(1);
		courseDetail1.action_detail.add(getActionStengthHigh("a1"));
		courseDetail1.action_detail.add(getActionStengthHigh("a2"));
		courseDetail1.action_detail.add(getActionStengthHigh("a4"));
		courseDetail1.action_detail.add(getActionStengthLow("a5"));
		
		CourseDetail courseDetail2 = new CourseDetail(2);
		courseDetail2.action_detail.add(getActionStengthNormal("a1"));
		courseDetail2.action_detail.add(getActionStengthLow("a2"));
		courseDetail2.action_detail.add(getActionStengthLow("a3"));
		courseDetail2.action_detail.add(getActionStengthHigh("a4"));
		
		CourseDetail courseDetail3 = new CourseDetail(4);
		courseDetail3.action_detail.add(getActionStengthHigh("a1"));
		courseDetail3.action_detail.add(getActionStengthNormal("a4"));
		courseDetail3.action_detail.add(getActionStengthNormal("a5"));
		
		CourseDetail courseDetail4 = new CourseDetail(5);
		courseDetail4.action_detail.add(getActionStengthLow("a2"));
		courseDetail4.action_detail.add(getActionStengthNormal("a4"));
		courseDetail4.action_detail.add(getActionStengthHigh("a5"));
		
		CourseDetail courseDetail5 = new CourseDetail(7);
		courseDetail5.action_detail.add(getActionStengthHigh("a1"));
		courseDetail5.action_detail.add(getActionStengthNormal("a2"));
		courseDetail5.action_detail.add(getActionStengthLow("a3"));
		
		CourseDetail courseDetail6 = new CourseDetail(8);
		courseDetail6.action_detail.add(getActionStengthHigh("a2"));
		courseDetail6.action_detail.add(getActionStengthNormal("a3"));
		courseDetail6.action_detail.add(getActionStengthLow("a4"));
		
		CourseDetail courseDetail7 = new CourseDetail(9);
		courseDetail7.action_detail.add(getActionStengthNormal("a1"));
		courseDetail7.action_detail.add(getActionStengthNormal("a2"));
		courseDetail7.action_detail.add(getActionStengthLow("a3"));
		courseDetail7.action_detail.add(getActionStengthLow("a5"));
		
		course1.course_detail.add(courseDetail1);
		course1.course_detail.add(courseDetail2);
		course1.course_detail.add(courseDetail3);
		course1.course_detail.add(courseDetail4);
		course1.course_detail.add(courseDetail5);
		course1.course_detail.add(courseDetail6);
		course1.course_detail.add(courseDetail7);
		return course1;
	}
	
	private ActionDetail getActionStengthHigh(String actionName) {
		ActionDetail actionDetail1 = new ActionDetail(actionName, 4);
		actionDetail1.group_detail.add(new GroupDetail(12, 10, 100, 60));
		actionDetail1.group_detail.add(new GroupDetail(10, 15, 150, 50));
		actionDetail1.group_detail.add(new GroupDetail(10, 15, 200, 50));
		actionDetail1.group_detail.add(new GroupDetail(8, 20, 250, 40));
		return actionDetail1;
	}
	
	private ActionDetail getActionStengthNormal(String actionName) {
		ActionDetail actionDetail1 = new ActionDetail(actionName, 4);
		actionDetail1.group_detail.add(new GroupDetail(10, 10, 80, 50));
		actionDetail1.group_detail.add(new GroupDetail(10, 10, 120, 50));
		actionDetail1.group_detail.add(new GroupDetail(10, 10, 150, 50));
		actionDetail1.group_detail.add(new GroupDetail(8, 15, 200, 40));
		return actionDetail1;
	}
	
	private ActionDetail getActionStengthLow(String actionName) {
		ActionDetail actionDetail1 = new ActionDetail(actionName, 3);
		actionDetail1.group_detail.add(new GroupDetail(10, 5, 70, 50));
		actionDetail1.group_detail.add(new GroupDetail(8, 10, 130, 40));
		actionDetail1.group_detail.add(new GroupDetail(8, 15, 180, 40));
		return actionDetail1;
	}
	
	private ArrayList<CourseInfo> getInitCourseInfo(){
		ArrayList<CourseInfo> courseInfoList = new ArrayList<CourseInfo>();
		// course1
		CourseInfo courseInfo1 = new CourseInfo();
		courseInfo1.courseId = "c1";
		courseInfo1.isNew = false;
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
		courseInfo2.isNew = false;
		courseInfo2.courseName = "21天腹肌雕刻";
		
		courseInfo2.actionIds.add("a1");
		courseInfo2.actionIds.add("a2");
		
		courseInfo2.dateNumList.add("1");
		courseInfo2.dateNumList.add("2");
		courseInfo2.dateNumList.add("3");
		
		courseInfo2.dateActionIdList.add("a1;a2");
		courseInfo2.dateActionIdList.add("a1");
		courseInfo2.dateActionIdList.add("a2");
		
		courseInfo2.isRecommend = true;
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
		
		// course4
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
	
	private ArrayList<Action> getInitActionList(){
		ArrayList<Action> actionInfoList = new ArrayList<Action>();
		
		// action1
		Action actionInfo1 = new Action();
		actionInfo1.action_id = "a1";
		actionInfo1.action_name = "坐姿推举";
		actionInfo1.action_position = "背部 腰部";
		actionInfo1.action_descript = "坚持训练将锻炼到胸大肌和三角肌";
		actionInfo1.action_difficult = 1;
		
		actionInfoList.add(actionInfo1);
		
		// action2
		Action actionInfo2 = new Action();
		actionInfo2.action_id = "a2";
		actionInfo2.action_name = "高位绳索划船";
		actionInfo2.action_position = "背部 手臂";
		actionInfo2.action_descript = "坚持训练将锻炼到肱二头肌和三角肌";
		actionInfo2.action_difficult = 2;
		
		actionInfoList.add(actionInfo2);
		
		// action3
		Action actionInfo3 = new Action();
		actionInfo3.action_id = "a3";
		actionInfo3.action_name = "仰卧起坐";
		actionInfo3.action_position = "背部 腰部";
		actionInfo3.action_descript = "坚持训练将锻炼到腹肌";
		actionInfo3.action_difficult = 3;
		
		actionInfoList.add(actionInfo3);
		
		// action1
		Action actionInfo4 = new Action();
		actionInfo4.action_id = "a4";
		actionInfo4.action_name = "坐姿推举";
		actionInfo4.action_position = "背部 腰部";
		actionInfo4.action_descript = "坚持训练将锻炼到胸大肌和三角肌";
		actionInfo4.action_difficult = 1;
		
		actionInfoList.add(actionInfo4);
		
		// action5
		Action actionInfo5 = new Action();
		actionInfo5.action_id = "a5";
		actionInfo5.action_name = "卧姿推举";
		actionInfo5.action_position = "背部 腰部";
		actionInfo5.action_descript = "坚持训练将锻炼到胸大肌和三角肌";
		actionInfo5.action_difficult = 2;
		
		actionInfoList.add(actionInfo5);
		
		return actionInfoList;
	}
	
}
