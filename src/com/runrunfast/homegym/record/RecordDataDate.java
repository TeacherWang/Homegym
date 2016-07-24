package com.runrunfast.homegym.record;

public class RecordDataDate extends BaseRecordData{
	
	public String strDate; // 日期，取出long型后转换为2016-06-26形式
	public int iConsumeTime; // 耗时，秒值
	
	public RecordDataDate(){
		iDataType = BaseRecordData.DATA_TYPE_HAVE_DATE;
	}
}
