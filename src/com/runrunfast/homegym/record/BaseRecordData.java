package com.runrunfast.homegym.record;

public class BaseRecordData {
	public static final int DATA_TYPE_HAVE_DATE 		= 1; // 带日期
	public static final int DATA_TYPE_ONLY_HAVE_COURSE 	= 2; // 不带日期，带课程计划
	public static final int DATA_TYPE_ONLY_HAVE_TRAIN 	= 3; // 不带课程计划，只带训练
	
	public int iDataType; // 数据类型
	public String strCoursId; // 课程计划id
	public String strCourseName; // 课程计划名称
}
