package com.runrunfast.homegym.record;

/**
 * 单个动作的完成情况
 *
 */
public class RecordDataUnit extends RecordDataDate{
	
	public String actionId; // 本次训练动作id
	public String trainName; // 本次训练动作名称
	public int iGroupCount; // 做了几组
	public int iTotalKcal; // 消耗的总卡路里
//	public ArrayList<TrainData> trainDataList; // 每组的详细数据
	
	public RecordDataUnit() {
		iDataType = BaseRecordData.DATA_TYPE_ONLY_HAVE_TRAIN;
	}
	
}
