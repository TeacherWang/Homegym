package com.runrunfast.homegym.course;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CourseInfo implements Serializable{
	public static final int QUALITY_NORMAL = 0;		// 一般课程
	public static final int QUALITY_EXCELLENT = 1;	// 精品课程
	
	public static final int PROGRESS_ING = 1; // 进行中
	public static final int PROGRESS_REST = 2; // 休息日
	public static final int PROGRESS_FINISH = 3; // 已完成
	public static final int PROGRESS_EXPIRED = 4; // 已过期
	
	public boolean isRecommendDescript = false; // 是要显示“推荐课程”文字说明栏的
	public boolean isMyCourse = false;				// 区分是list中的我的课程
	public int courseQuality;				// 课程质量
	public boolean isRecommend = false; 		// 推荐课程
	public boolean isNew = false;			// 新增课程
	public String courseId;					// 课程id
	public String courseName;				// 课程名
	public int courseProgress;				// 课程进度（我参加的课程）
	public List<String> actionIds = new ArrayList<String>();				// 课程动作类型id
	public List<String> dateNumList = new ArrayList<String>();		// 根据date分布
	public List<String> dateActionIdList = new ArrayList<String>(); // 根据date分配的动作id
	
	public String startDate; // 对于我参加的课程，标示参加的第一天日期
}
