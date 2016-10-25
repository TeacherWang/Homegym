package com.runrunfast.homegym.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.runrunfast.homegym.account.AccountMgr;


public class CalculateUtil {
	public static final int DEFAULT_WEIGHT_VALUE_IF_ZERO = 5; // 服务器返回重量为0时，为不可编辑重量，默认5
	/**
	  * @Method: calculateKcal
	  * @Description: 根据数量和重量计算kcal。公式a((m+b)*h)/133.89，m为(用户选择的重量-5)；a为count；h为铁块高度；b为初始阻力
	  * @param count
	  * @param weight
	  * @return	
	  * 返回类型：float 
	  */
	public static float calculateTotakKcal(int count, int weight, float actionH, int actionB){
		float kcal = count * ((weight - 5) + actionB) * actionH / 133.89f;
		BigDecimal bg = new BigDecimal(kcal).setScale(2, RoundingMode.UP);
		
		return bg.floatValue();
	}
	
	public static int calculateTotalTrain(int count, int weight, float action_h, int action_b){
		int train = 0;
		
		if(weight == 0){
			train = (int) (count * 60 * action_h);
		}else{
			train = count * weight;
		}
		
		return train;
	}
	
}
