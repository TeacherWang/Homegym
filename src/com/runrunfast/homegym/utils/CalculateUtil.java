package com.runrunfast.homegym.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class CalculateUtil {
	/**
	  * @Method: calculateKcal
	  * @Description: 根据数量和重量计算kcal。公式a((m+b)*h)/133.89，m为(用户选择的重量-5)；a为count；h为铁块高度；b为初始阻力
	  * @param count
	  * @param weight
	  * @return	
	  * 返回类型：float 
	  */
	public static float calculateTotakKcal(int count, int weight, float actionH, float actionB){
		float kcal = count * ((weight - 5) + actionB) * actionH / 133.89f;
		BigDecimal bg = new BigDecimal(kcal).setScale(2, RoundingMode.UP);
		
		return bg.floatValue();
	}
}
