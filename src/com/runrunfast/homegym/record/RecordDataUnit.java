package com.runrunfast.homegym.record;

import java.util.ArrayList;
import java.util.List;

/**
 * 指定日期单次训练的完成情况
 *
 */
public class RecordDataUnit extends RecordDataDate{
	
	public String actionId; // 本次训练动作id
	public String actionName; // 本次训练动作名称
	public int iGroupCount; // 做了几组
	public int iCount; // 总次数
	public int iTotalKcal; // 消耗的总卡路里
	public String actualDate; // 实际完成的日期
	
	public List<String> finishCountList = new ArrayList<String>();
	public List<String> finishToolWeightList = new ArrayList<String>();
	public List<String> finishBurningList = new ArrayList<String>();
	
	public RecordDataUnit() {
		iDataType = BaseRecordData.DATA_TYPE_ONLY_HAVE_TRAIN;
	}
	
}
