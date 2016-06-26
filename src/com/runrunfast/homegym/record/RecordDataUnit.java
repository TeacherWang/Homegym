package com.runrunfast.homegym.record;

import java.util.ArrayList;

public class RecordDataUnit extends BaseRecordData{
	
	public int trainId; // 本次训练动作id
	public String trainName; // 本次训练动作名称
	public int groupCount; // 做了几组
	public int totalKcal; // 消耗的总卡路里
	public ArrayList<TrainData> trainDataList; // 每组的详细数据
	
	public RecordDataUnit() {
		dataType = BaseRecordData.DATA_TYPE_ONLY_HAVE_TRAIN;
	}
	
}
