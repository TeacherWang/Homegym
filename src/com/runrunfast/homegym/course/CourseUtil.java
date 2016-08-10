package com.runrunfast.homegym.course;

import com.runrunfast.homegym.bean.Course.CourseDetail;
import com.runrunfast.homegym.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class CourseUtil {
	/**
	 * @descript 根据开始日期和天数分布，返回天数对应的日期集合。比如开始日期是2016-07-20，天数集合为{1，2，4}，
	 * 那么返回{2016-07-20，2016-07-21，2016-07-23}
	 * @param strStartDate
	 * @param dayNumList
	 * @return
	 */
	public static ArrayList<String> getCourseDateList(String strStartDate, List<CourseDetail> courseDetails){
		ArrayList<String> courseDateList = new ArrayList<String>();
		int dayListSize = courseDetails.size();
		for(int i=0; i<dayListSize; i++){
			CourseDetail courseDetail = courseDetails.get(i);
			String dateStr = DateUtil.getDateStrOfDayNumFromStartDate(courseDetail.day_num, strStartDate);
			courseDateList.add(dateStr);
		}
		
		return courseDateList;
	}
}
