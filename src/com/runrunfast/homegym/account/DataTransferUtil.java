package com.runrunfast.homegym.account;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;
import android.util.Log;

/**
 * @author TeacherWang
 *
 */
public class DataTransferUtil {
	private final static String TAG = "DataTransferUtil";
	private static volatile DataTransferUtil instance;
	private static Object lockObject = new Object();
	
	private static int START_YEAR, END_YEAR;// 起始年 结束年
	private static String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
	private static String[] months_little = { "4", "6", "9", "11" };
	private static final List<String> list_big = Arrays.asList(months_big);
	private static final List<String> list_little = Arrays.asList(months_little);
	
	public static HashMap numMap;
	
	static{
		numMap = new HashMap();
		numMap.put(1,"一");
		numMap.put(2,"二");
		numMap.put(3,"三");
		numMap.put(4,"四");
		numMap.put(5,"五");
		numMap.put(6,"六");
		numMap.put(7,"七");
		numMap.put(8,"八");
		numMap.put(9,"九");
	}
	
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
	  * @Method: getSexPosition
	  * @Description: 获取性别选择器的位置
	  * @param strSex
	  * @return	
	  * 返回类型：int 
	  */
	public int getSexPosition(String strSex){
		if(strSex.equals(UserInfo.SEX_MAN)){
			return 0;
		}else{
			return 1;
		}
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
	
	/**
	  * @Method: getCountPostion
	  * @Description: 获取次数选择器的位置
	  * @param count
	  * @return	
	  * 返回类型：int 
	  */
	public int getCountPostion(int count){
		return (count - 1);
	}
	
	/**
	  * @Method: getToolWeightPostion
	  * @Description: 获取重量选择器的位置
	  * @param toolWeight
	  * @return	
	  * 返回类型：int 
	  */
	public int getToolWeightPostion(int toolWeight){
		return (toolWeight / 5 - 1);
	}
	
	public static final float BigDecimals(float f, int scale, int roune) {
		try {
			if (f != 0) {
				BigDecimal bd = new BigDecimal((double) f);
				bd = bd.setScale(scale, roune);
				return bd.floatValue();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}
	
	public static int getAgeByBirtyday(String birthday){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(birthday);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return getAgeByBirthday(date);
	}
	
	/**
	 * 根据用户生日计算年龄
	 */
	public static int getAgeByBirthday(Date birthday) {
		Calendar cal = Calendar.getInstance();

		if(birthday == null){
			Log.e(TAG, "getAgeByBirthday, birthday is null");
			return 0;
		}
		
		if (cal.before(birthday)) {
			throw new IllegalArgumentException(
					"The birthDay is before Now.It's unbelievable!");
		}

		int yearNow = cal.get(Calendar.YEAR);
		int monthNow = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

		cal.setTime(birthday);
		int yearBirth = cal.get(Calendar.YEAR);
		int monthBirth = cal.get(Calendar.MONTH) + 1;
		int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);

		int age = yearNow - yearBirth;

		if (monthNow <= monthBirth) {
			if (monthNow == monthBirth) {
				// monthNow==monthBirth 
				if (dayOfMonthNow < dayOfMonthBirth) {
					age--;
				}
			} else {
				// monthNow>monthBirth 
				age--;
			}
		}
		return age;
	}
	
	public String getBigNum(int num){
		return (String) numMap.get(num);
	}
	
}
