package com.runrunfast.homegym.home.fragments;

import com.runrunfast.homegym.bean.Course;

public class InvalidCourse extends Course {

	public static int COURSE_TYPE_SHOW_RECOMMED_TEXT = 1; // 用来显示我的界面“推荐课程”一栏文字的
	public static int COURSE_TYPE_EMPTY = 2; // 没有我的课程
	
	public int courseType;
	
	public InvalidCourse(int courseType){
		this.courseType = courseType;
	}
}
