package com.runrunfast.homegym.bean;

import java.util.ArrayList;
import java.util.List;

public class ServerActionData {

	public List<BaseActionData> data = new ArrayList<BaseActionData>();
	
	public static class BaseActionData{
		public String action_id; // 训练动作id
		public String action_name;
		public String action_position; // 锻炼的部位
		public String action_descript; // 简单描述
		public int action_difficult; // 难度等级
		public float action_h; // 动作的铁块上升高度
		public int action_b; // 动作的初始阻力
		public String action_img_url; // 图片下载地址
		public int action_left_right; // 动作是否分左右：0是不区分；1是区分
		public ArrayList<String> action_video_url = new ArrayList<String>(); // jsonArray格式，视频下载地址，最后一个“/”后面是视频文件的名称
		public String action_audio_url; // String，音频下载地址
	}
}
