package com.runrunfast.homegym.course;

import java.util.ArrayList;
import java.util.List;

public class ActionInfo {
	public String strCourseId; // 课程id
	public String strActionId; // 训练动作id
	public String actionName;
	public String strTrainPosition; // 锻炼的部位
	public String strTrainDescript; // 简单描述
	public int iDiffcultLevel;
	public int iTime; // 秒
	public int defaultGroupNum;
	public int iDefaultTotalKcal; // 千卡
	public List<String> defaultCountList = new ArrayList<String>();
	public List<String> defaultToolWeightList = new ArrayList<String>();
	public List<String> defaultBurningList = new ArrayList<String>();
	
	public String strGroupNum; // 第几组
	public int iCount; // 次数
	public int iToolWeight; // 重量
	public int iBurning; // 燃脂
}
