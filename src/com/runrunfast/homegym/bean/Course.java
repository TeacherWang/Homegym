package com.runrunfast.homegym.bean;

import java.util.List;

public class Course {
	public String course_id;
	public String course_name;
	public int course_quality;
	public int course_new;
	public int course_recommend; // 1为推荐课程,0为普通课程
	public List<String> action_ids;
	public List<CourseDetail> course_detail;
	
	
	public static class CourseDetail{
		public int day_num;
		public int progress; // 该天的进度
		public List<ActionDetail> action_detail;
	}
	
	public static class ActionDetail{
		public String action_id;
		public int group_num;
		public List<GroupDetail> group_detail;
	}
	
	public static class GroupDetail{
		public int count;
		public int weight;
		public int kcal;
	}
}
