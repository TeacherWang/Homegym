package com.runrunfast.homegym.bean;

import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.bean.MyCourse.DayProgress;
import java.util.ArrayList;
import java.util.List;

public class ServerMyCourse {

	public String course_id;
	public String course_name;
	public String start_date;
	public String course_img_url;
	public int progress;
	public List<CourseDetail> course_detail = new ArrayList<Course.CourseDetail>();
	public List<DayProgress> day_progress = new ArrayList<DayProgress>();
}
