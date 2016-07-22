package com.runrunfast.homegym.course;

import java.util.ArrayList;

public class ActionInfo {
	public int iCourseId; // 课程id
	public int iActionId; // 训练动作id
	public String actionName;
	public int defaultGroupNum;
	public ArrayList<String> defaultCountList;
	public ArrayList<String> defaultToolWeightList;
	public ArrayList<String> defaultBurningList;
	
	public String strGroupNum; // 第几组
	public int iCount; // 次数
	public int iToolWeight; // 重量
	public int iBurning; // 燃脂
}
