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
	public List<ActionDetail> course_actions = new ArrayList<ActionDetail>();
	public List<CourseDateDistribution> course_date_distribution = new ArrayList<CourseDateDistribution>();
	
	public static class ActionId{
		public String action_id;
		
		public ActionId(String action_id){
			this.action_id = action_id;
		}
	}
	
	public static class CourseDateDistribution{
		public int day_num;
		public List<String> action_ids = new ArrayList<String>();
		
		public CourseDateDistribution(int dayNum, ArrayList<String> action_ids){
			this.day_num = dayNum;
			this.action_ids = action_ids;
		}
		
		public CourseDateDistribution(int dayNum){
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
