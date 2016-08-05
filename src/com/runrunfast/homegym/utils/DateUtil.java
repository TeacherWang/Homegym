package com.runrunfast.homegym.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	/**
	  * @Method: getCurrentDate
	  * @Description: 获取当天的年月日
	  * @return	
	  * 返回类型：String yyyy-MM-dd
	  */
	public static String getCurrentDate() {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		Date date = new Date();
		String dateStr = dateFormat.format(date);
		return dateStr;
	}
	
	/**
	  * @Method: getStrDate
	  * @Description: 根据Date，获取日期字符串
	  * @param date Date
	  * @return	
	  * 返回类型：String yyyy-MM-dd
	  */
	public static String getStrDate(Date date){
		String strDate = "";
		
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		
		strDate = dateFormat.format(date);
		
		return strDate;
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

	public static String getStrDateFirstDayDependsYear(int year){
		Calendar calendar = Calendar.getInstance();
		
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		
		Date date = calendar.getTime();
		
		String strDate = getStrDate(date);
		
		return strDate;
	}
	
	/**
	  * @Method: getDaysByYearMonth
	  * @Description: 根据 年 月 获取对应的月份 天数
	  * @param year
	  * @param month
	  * @return	
	  * 返回类型：int 
	  */
	public static int getDaysByYearMonth(int year, int month){
		Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);  
        a.set(Calendar.MONTH, month - 1);  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
	}
	
	/**
	  * @Method: getDayIndexOfMonth
	  * @Description: 获取日期对应在该月的天数
	  * @param strDate 格式yyyy-MM-dd
	  * @return	
	  * 返回类型：int 
	  */
	public static int getDayIndexOfMonth(String strDate){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			calendar.setTime(sdf.parse(strDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
        int dayIndex = calendar.get(Calendar.DAY_OF_MONTH);
        return dayIndex;
	}
	
	/**
	  * @Method: getStrDateFirstDayDependsMonth
	  * @Description: 根据月份，获取该月第一天日期
	  * @param month 1-12
	  * @return	
	  * 返回类型：String yyyy-MM-dd
	  */
	public static String getStrDateFirstDayDependsMonth(int month){
		String strDate = "";
		
		Calendar calendar = Calendar.getInstance();
		
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		Date date = calendar.getTime();
		
		strDate = getStrDate(date);
		
		return strDate;
	}
	
	/**
	  * @Method: getThisYear
	  * @Description: 获取今年
	  * @return	
	  * 返回类型：int 
	  */
	public static int getThisYear(){
		Calendar calendar = Calendar.getInstance();
		
		return calendar.get(Calendar.YEAR);
	}
	
	/**
	  * @Method: getMonth
	  * @Description: 获取本月
	  * @return	
	  * 返回类型：int 月份：1-12
	  */
	public static int getThisMonth(){
		Calendar calendar = Calendar.getInstance();
		
		return calendar.get(Calendar.MONTH) + 1;
	}
	
	/**
	  * @Method: getMonth
	  * @Description: 根据日期获取月份
	  * @param strDate 格式yyyy-MM-dd
	  * @return	
	  * 返回类型：int 月份：1-12
	  */
	public static int getMonth(String strDate){
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			calendar.setTime(sdf.parse(strDate));
			return ( calendar.get(Calendar.MONTH) + 1 ) ;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
	/**
	  * @Method: getDateStrOfYearMonth
	  * @Description: 获取年月，比如2016-08-03，获取为2016-08
	  * @param strDate 日期，比如2016-08-03
	  * @return	
	  * 返回类型：String 
	  */
	public static String getDateStrOfYearMonth(String strDate){
		String strDateYearMonth = "";
		
		strDateYearMonth = strDate.substring(0, 7);
		
		return strDateYearMonth;
	}
	
	public static String getDateStrOfYear(String strDate){
		String strDateYear = "";
		
		strDateYear = strDate.substring(0, 4);
		
		return strDateYear;
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
    
    public static String secToHour(int second){
    	return String.valueOf( second / (60 * 60) );
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
