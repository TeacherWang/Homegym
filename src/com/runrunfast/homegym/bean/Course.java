package com.runrunfast.homegym.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable{
	public static final int QUALITY_NORMAL = 0;		// 一般课程
	public static final int QUALITY_EXCELLENT = 1;	// 精品课程
	
	public static final int NORMAL_COURSE = 0;
	public static final int RECOMMED_COURSE = 1;
	
	public static final int NEW_COURSE = 1;
	
	public String course_id;
	public String course_name;
	public int course_quality;
	public int course_new;
	public int course_recommend; // 1为推荐课程,0为普通课程
	public List<ActionId> action_ids = new ArrayList<ActionId>();
	public List<CourseDetail> course_detail = new ArrayList<Course.CourseDetail>();
	
	public static class ActionId{
		public String action_id;
		
		public ActionId(String action_id){
			this.action_id = action_id;
		}
	}
	
	public static class CourseDetail{
		public int day_num;
		public List<ActionDetail> action_detail = new ArrayList<Course.ActionDetail>();
		
		public CourseDetail(int dayNum, ArrayList<ActionDetail> action_detail){
			this.day_num = dayNum;
			this.action_detail = action_detail;
		}
		
		public CourseDetail(int dayNum){
			this.day_num = dayNum;
		}
	}
	
	public static class ActionDetail{
		public String action_id;
		public int group_num;
		public List<GroupDetail> group_detail = new ArrayList<Course.GroupDetail>();
		
		public ActionDetail(String action_id, int group_num, ArrayList<Course.GroupDetail> group_detail){
			this.action_id = action_id;
			this.group_num = group_num;
			this.group_detail = group_detail;
		}
		
		public ActionDetail(String action_id, int group_num){
			this.action_id = action_id;
			this.group_num = group_num;
		}
	}
	
	public static class GroupDetail{
		public int count;
		public int weight;
		public int kcal;
		public int time;
		
		public GroupDetail(int count, int weight, int kcal, int time){
			this.count = count;
			this.weight = weight;
			this.kcal = kcal;
			this.time = time;
		}
	}
}
