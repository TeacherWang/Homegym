package com.runrunfast.homegym.course;

import java.util.ArrayList;

public class ActionInfo {
	public String strCourseId; // 课程id
	public String strActionId; // 训练动作id
	public String actionName;
	public String strTrainPosition; // 锻炼的部位
	public String strTrainDescript; // 简单描述
	public int iTime; // 分钟
	public int defaultGroupNum;
	public ArrayList<String> defaultCountList;
	public ArrayList<String> defaultToolWeightList;
	public ArrayList<String> defaultBurningList;
	
	public String strGroupNum; // 第几组
	public int iCount; // 次数
	public int iToolWeight; // 重量
	public int iBurning; // 燃脂
}
