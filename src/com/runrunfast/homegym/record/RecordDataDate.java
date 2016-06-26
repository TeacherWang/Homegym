package com.runrunfast.homegym.record;

public class RecordDataDate extends BaseRecordData{
	
	public String strDate; // 日期，取出long型后转换为2016-06-26形式
	public String strConsumeTime; // 耗时，把取出的秒值，转换成00:30:48的形式
	
	public RecordDataDate(){
		dataType = BaseRecordData.DATA_TYPE_HAVE_DATE;
	}
}
