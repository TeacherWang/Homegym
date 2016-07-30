package com.runrunfast.homegym.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	//获取当天的年月日
	public static String getCurrentDate() {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		Date date = new Date();
		String dateStr = dateFormat.format(date);
		return dateStr;
	}
	
	/**
	  * @Method: parseCurrentDateToDays
	  * @Description: 把当天的日期转换成距参数yyyyMMdd的天数
	  * @param startDate 开始日期为yyyy-MM-dd格式
	  * @return	
	  * 返回类型：int 
	  */
	public static int getDaysNumBetweenCurrentDayAndStartDay(String startDate){
		int day = 1;
		try {
			long currentMil = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String initDateStr = startDate;
			Date initDate = sdf.parse(initDateStr);
			long initMil = initDate.getTime();

			day = (int)((currentMil - initMil)/1000/60/60/24 + 1);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return day;
	}
	
	/**
	  * @Method: parseDaysToDate
	  * @Description: 把获取到的距startDate的天数转换成日期
	  * @param dayNum
	  * @param startDate 开始日期为yyyy-MM-dd格式
	  * @return	
	  * 返回类型：String 
	  */
	public static String getDateStrOfDayNumFromStartDate(int dayNum, String startDate) {
		String dateStr = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String initDateStr = startDate;
			Date initDate = sdf.parse(initDateStr);
			long dayMil = (dayNum - 1)*24*60*60*1000l;
			initDate.setTime(initDate.getTime() + dayMil);
			dateStr = sdf.format(initDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	// a integer to xx:xx:xx
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }
    
    /**
      * @Method: secToMinute
      * @Description: 秒转成分钟数
      * @param second
      * @return	
      * 返回类型：String 
      */
    public static String secToMinute(int second){
    	return String.valueOf( second / 60);
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }
}
