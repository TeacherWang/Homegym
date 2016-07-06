package com.runrunfast.homegym.course;


public class CourseInfo {
	public static final int QUALITY_NORMAL = 0;		// 一般课程
	public static final int QUALITY_EXCELLENT = 1;	// 精品课程
	
	public static final int PROGRESS_ING = 1; // 进行中
	public static final int PROGRESS_REST = 2; // 休息日
	
	public boolean isMyCourse = false; 		// 我参加的课程
	public boolean isMyCourseEmpty = true; 	// 是否参加过课程
	public int courseQuality;				// 课程质量
	public boolean isCommend = false; 		// 推荐课程
	public boolean isNew = false;			// 新增课程
	public int courseId;					// 课程id
	public String courseName;				// 课程名
	public int courseProgress;				// 课程进度（我参加的课程）
}
