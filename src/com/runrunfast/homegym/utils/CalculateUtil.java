package com.runrunfast.homegym.utils;

import android.R.integer;

public class CalculateUtil {

	/**
	  * @Method: calculateKcal
	  * @Description: 根据数量和重量计算kcal
	  * @param count
	  * @param weight
	  * @return	
	  * 返回类型：int 
	  */
	public static int calculateTotakKcal(int count, int weight){
		int kcal = weight * 10 * count;
		return kcal;
	}
	
	/**
	  * @Method: calculateTime
	  * @Description: 根据数量计算时间
	  * @param count
	  * @return	
	  * 返回类型：int 
	  */
	public static int calculateTotalTime(int count){
		int time = count * 5;
		return time;
	}
	
	public static int calculateSingleKcal(int weight){
		return weight * 10;
	}
	
	public static int calculateSingleTime(){
		return 5;
	}
	
}
