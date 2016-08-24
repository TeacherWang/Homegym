package com.runrunfast.homegym.bean;

import com.runrunfast.homegym.bean.Course.CourseDetail;

import java.util.ArrayList;
import java.util.List;

public class ServerCourseData {

	public List<BaseCourseData> data = new ArrayList<ServerCourseData.BaseCourseData>();
	
	public static class BaseCourseData {
		public String course_id;
		public String course_name;
		public int course_quality;
		public int course_new;
		public int course_recommend; // 1为推荐课程,0为普通课程
		public String course_img_url;
		public List<CourseDetail> course_detail = new ArrayList<CourseDetail>();
	}
}
