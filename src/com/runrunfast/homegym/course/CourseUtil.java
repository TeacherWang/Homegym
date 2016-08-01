package com.runrunfast.homegym.course;

import java.util.ArrayList;
import java.util.List;

import com.runrunfast.homegym.utils.DateUtil;

public class CourseUtil {
	/**
	 * @descript 根据开始日期和天数分布，返回天数对应的日期集合。比如开始日期是2016-07-20，天数集合为{1，2，4}，
	 * 那么返回{2016-07-20，2016-07-21，2016-07-23}
	 * @param strStartDate
	 * @param dayNumList
	 * @return
	 */
	public static ArrayList<String> getCourseDateList(String strStartDate, List<String> dayNumList){
		ArrayList<String> courseDateList = new ArrayList<String>();
		int dayListSize = dayNumList.size();
		for(int i=0; i<dayListSize; i++){
			String dateStr = DateUtil.getDateStrOfDayNumFromStartDate(Integer.parseInt(dayNumList.get(i)), strStartDate);
			courseDateList.add(dateStr);
		}
		
		return courseDateList;
	}
}
