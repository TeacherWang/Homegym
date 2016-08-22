package com.runrunfast.homegym.record;

import com.runrunfast.homegym.bean.Course.ActionDetail;

import java.util.ArrayList;
import java.util.List;

public class TrainRecord {
	public String uid;
	public String course_id; // 课程计划id
	public String course_name; // 课程计划名称
	public String plan_date;
//	public int finish_group_num; // 本次训练所有动作完成的组数
	public int finish_count; // 本次训练所有动作完成的次数
	public float finish_kcal; // 本次训练所有动作完成的kcal
	public int finish_time; // 本次训练的时间
	public List<ActionDetail> action_detail = new ArrayList<ActionDetail>();
	public String actual_date; // 实际完成的日期
	
}
