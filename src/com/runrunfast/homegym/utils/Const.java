package com.runrunfast.homegym.utils;

public class Const {

	public static final int DIALOG_REQ_CODE_OPEN_BT = 1;
	public static final int DIALOG_REQ_CODE_UNBIND_BT = 2;
	public static final int DIALOG_REQ_CODE_EXIT_ACCOUNT = 3;
	public static final int DIALOG_REQ_CODE_EXIT_COURSE = 4;
	
	// 启动CourseTrainActivity所传参数
	public static final String KEY_COURSE = "key_course";
	public static final String KEY_COURSE_ID = "key_course_id";
	public static final String KEY_COURSE_NAME = "key_course_name";
	public static final String KEY_ACTION_ID = "key_action_id";
	public static final String KEY_ACTION_IDS = "key_action_ids";
	public static final String KEY_ACTION_NAME = "key_action_name";
	public static final String KEY_ACTION_DESCRIPT = "key_descript";
	public static final String KEY_ACTION_NUM = "key_action_num"; // 动作几
	public static final String KEY_DATE = "key_date";
	public static final String KEY_COURSE_DETAIL = "key_course_detail";
	public static final String KEY_ACTION = "key_action";
	public static final String KEY_ACTION_TOTAL_DATA = "key_action_total_data";
	public static final String KEY_ACTION_DETAIL = "key_action_detail";
	public static final String KEY_DAY_POSITION = "key_day_position"; // 该天在课程的位置
	public static final String KEY_ACTION_POSITION = "key_action_position"; // 该动作在该天中的位置
	
	public static final String KEY_COURSE_TOTAL_TIME = "key_course_total_time"; // 实际课程完成的数据
	public static final String KEY_COURSE_TOTAL_COUNT = "key_course_total_count";
	public static final String KEY_COURSE_TOTAL_BURNING = "key_course_total_burning";
	
	// 数据库字段名称
	public static final String TABLE_COURSE = "course_table";
	public static final String TABLE_ACTION = "action_table";
	public static final String TABLE_MY_COURSE = "my_course_table";
	public static final String TABLE_MY_COURSE_ACTION = "my_course_action_table";
	public static final String TABLE_FINISH = "finish_table";
	
	public static final String DB_KEY_ID = "_id";
	public static final String DB_KEY_COURSE_ID = "course_id";
	public static final String DB_KEY_COURSE_NAME = "course_name";
	public static final String DB_KEY_COURSE_ACTIONS = "course_actions";
	public static final String DB_KEY_ACTION_IDS = "action_ids";
	public static final String DB_KEY_DATE_NUM = "date_num";
	public static final String DB_KEY_DATE_ACTION_IDS = "date_action_ids";
	public static final String DB_KEY_COURSE_RECOMMEND = "course_recommend";
	public static final String DB_KEY_COURSE_QUALITY = "course_quality";
	public static final String DB_KEY_COURSE_NEW = "course_new";
	public static final String DB_KEY_COURSE_DETAIL = "course_detail";
	
	public static final String DB_KEY_ACTION_ID = "action_id";
	public static final String DB_KEY_ACTION_NAME = "action_name";
	public static final String DB_KEY_ACTION_POSITION = "action_position"; // 动作锻炼的部位
	public static final String DB_KEY_ACTION_DESCRIPT = "action_descript"; // 动作的描述
	public static final String DB_KEY_ACTION_DIFFICULT = "action_difficult"; // 动作的难度
	public static final String DB_KEY_ACTION_DEFAULT_TOTAL_KCAL = "action_default_total_kcal"; // 动作的默认总消耗卡
	public static final String DB_KEY_DEFAULT_TIME = "default_time";
	public static final String DB_KEY_DEFAULT_GROUP_NUM = "default_group_num";
	public static final String DB_KEY_DEFAULT_COUNT = "default_count";
	public static final String DB_KEY_DEFAULT_TOOL_WEIGHT = "default_tool_weight";
	public static final String DB_KEY_DEFAULT_BURNING = "default_burning"; // 每组动作的消耗卡
	public static final String DB_KEY_START_DATE = "start_date";
	public static final String DB_KEY_PROGRESS = "progress"; // 进度--进行中、休息日、已完成
	public static final String DB_KEY_DAY_PROGRESS = "day_progress"; // 每天的完成度 -- 已完成、未完成
	
	public static final String DB_KEY_FINISH_GROUP_NUM = "finish_group_num";
	public static final String DB_KEY_FINISH_TOTAL_COUNT = "finish_total_count";
	public static final String DB_KEY_FINISH_TOTAL_KCAL = "finish_total_kcal";
	public static final String DB_KEY_FINISH_TOTAL_TIME = "finish_total_time";
	public static final String DB_KEY_FINISH_COUNT_SET = "finish_count_set";
	public static final String DB_KEY_FINISH_TOOLWEIGHT_SET = "finish_toolweight_set";
	public static final String DB_KEY_FINISH_BURNING_SET = "finish_burning_set";
	public static final String DB_KEY_PLAN_DATE = "plan_date"; // 计划的时间
	public static final String DB_KEY_ACTUAL_DATE = "actual_date"; // 实际完成的时间
	public static final String DB_KEY_DATE_PROGRESS = "date_progress"; // 指定日期的完成情况：未完成，已完成
	
	public static final String DB_KEY_UID = "uid";
	public static final String DB_KEY_NICK = "nick";
	public static final String DB_KEY_SEX = "sex";
	public static final String DB_KEY_BIRTH = "birth";
	public static final String DB_KEY_WEIGHT = "weight";
	public static final String DB_KEY_HEIGHT = "height";
	public static final String DB_KEY_CITY = "city";
	
	
	public static final String DB_KEY_DISTINCT = "DISTINCT ";
	
}
