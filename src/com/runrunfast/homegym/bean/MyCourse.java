package com.runrunfast.homegym.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyCourse extends Course {
	// 课程的进度
	public static final int COURSE_PROGRESS_ING = 0; // 进行中
	public static final int COURSE_PROGRESS_REST = 1; // 休息日
	public static final int COURSE_PROGRESS_FINISH = 2; // 已完成
	public static final int COURSE_PROGRESS_EXPIRED = 3; // 已过期
	
	// 每天的完成情况
	public static final int DAY_PROGRESS_UNFINISH = 0; // 未完成
	public static final int DAY_PROGRESS_FINISH = 1; // 已完成
	
	public String uid;
	public int progress;
	public String start_date;
	public List<DayProgress> day_progress = new ArrayList<DayProgress>();
	
	public static class DayProgress implements Serializable{
		public int day_num;
		public int progress;
	}
	
}
