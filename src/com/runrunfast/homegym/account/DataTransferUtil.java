package com.runrunfast.homegym.account;

import android.R.integer;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DataTransferUtil {
	private final String TAG = "DataTransferUtil";
	private static volatile DataTransferUtil instance;
	private static Object lockObject = new Object();
	
	private static int START_YEAR, END_YEAR;// 起始年 结束年
	private static String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	private static String[] months_little = { "4", "6", "9", "11" };
	private static final List<String> list_big = Arrays.asList(months_big);
	private static final List<String> list_little = Arrays.asList(months_little);
	
	public static DataTransferUtil getInstance(){
		if(instance == null){
			synchronized (lockObject) {
				if(instance == null){
					instance = new DataTransferUtil();
				}
			}
		}
		return instance;
	}
	
	public int getYearPosition(String strYear){
		int year = Integer.parseInt(strYear);
		return 70 - Calendar.getInstance().get(Calendar.YEAR) + year;
	}
	
	public int getMonthPosition(String strMonth){
		int month = Integer.parseInt(strMonth);
		return month - 1;
	}
	
	public int getDayPosition(String strDay){
		int day = Integer.parseInt(strDay);
		return day - 1;
	}
	
	public String getBirthYear(String birthday){
		if(TextUtils.isEmpty(birthday)){
			Log.e(TAG, "getBirthYear, birthday is null");
			return null;
		}
		return String.valueOf(Integer.parseInt(birthday.substring(0, birthday.indexOf("-"))));
	}
	
	public String getBirthMonth(String birthday){
		if(TextUtils.isEmpty(birthday)){
			Log.e(TAG, "getBirthYear, birthday is null");
			return null;
		}
		return String.valueOf(Integer.parseInt(birthday.substring(birthday.indexOf("-") + 1, birthday.lastIndexOf("-"))));
	}
	
	public String getBirthDay(String birthday){
		if(TextUtils.isEmpty(birthday)){
			Log.e(TAG, "getBirthYear, birthday is null");
			return null;
		}
		return String.valueOf(Integer.parseInt(birthday.substring(birthday.lastIndexOf("-") + 1, birthday.length())));
	}
	
	/**
	 * 选择日期
	 */
	public List<String> getDayList(String date) {
		START_YEAR = Calendar.getInstance().get(Calendar.YEAR) - 70;
		END_YEAR = Calendar.getInstance().get(Calendar.YEAR);

		int year = 0;
		int month = 0;
		int day = 0;
		if (null == date || date.equals("")) {
			year = START_YEAR;
			month = 1;
			day = 1;
		} else {
			year = Integer.parseInt(date.substring(0, date.indexOf("-")));
			month = Integer.parseInt(date.substring(date.indexOf("-") + 1,
					date.lastIndexOf("-")));
			day = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1,
					date.length()));
		}
		
		Log.i(TAG, "getDayList, year = " + year + ", month = " + month + ", day = " + day);
		
		if (list_big.contains(String.valueOf(month))) {
			return AccountMgr.getInstance().getDay31List();
		} else if (list_little.contains(String.valueOf(month))) {
			return AccountMgr.getInstance().getDay30List();
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0){
				return AccountMgr.getInstance().getDay29List();
			}else{
				return AccountMgr.getInstance().getDay28List();
			}
		}
	}
	
	/**
	  * @Method: getWeightPosition
	  * @Description: 获取体重选择器的位置
	  * @param strWeight
	  * @return	
	  * 返回类型：int 
	  */
	public int getWeightPosition(String strWeight){
		return (Integer.parseInt(strWeight) - 45);
	}
	
	/**
	  * @Method: getHeightPosition
	  * @Description: 获取身高选择器的位置
	  * @param strHeight
	  * @return	
	  * 返回类型：int 
	  */
	public int getHeightPosition(String strHeight){
		return (Integer.parseInt(strHeight) - 150);
	}
	
}
