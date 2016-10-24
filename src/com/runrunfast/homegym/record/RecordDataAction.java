package com.runrunfast.homegym.record;

import com.runrunfast.homegym.bean.Course.GroupDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * 指定日期单次训练的完成情况
 *
 */
public class RecordDataAction extends BaseRecordData{
	
	public String actionId; // 本次训练动作id
	public String actionName; // 本次训练动作名称
	public int groupCount; // 该动作做了几组
	public int totalKcal; // 该动作消耗的总卡路里
	public String actualDate; // 实际完成的日期
	
	public List<GroupDetail> groupDetailList = new ArrayList<GroupDetail>();
	
}
