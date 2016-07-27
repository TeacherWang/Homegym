package com.runrunfast.homegym.utils;

import android.R.integer;

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
