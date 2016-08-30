package com.runrunfast.homegym.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class Action implements Serializable{
	public String action_id; // 训练动作id
	public String action_name;
	public String action_position; // 锻炼的部位
	public String action_descript; // 简单描述
	public int action_difficult; // 难度等级
	public float action_h; // 动作的铁块上升高度
	public float action_b; // 动作的初始阻力
	public String action_img_url; // 图片下载地址
	public String action_img_local; // 图片本地路径
	public int action_left_right; // 动作是否分左右：0是不区分；1是区分
	public ArrayList<String> action_video_url = new ArrayList<String>(); // jsonArray格式，视频下载地址，最后一个“/”后面是视频文件的名称
	public ArrayList<String> action_video_local = new ArrayList<String>(); // jsonArray格式，视频本地地址，最后一个“/”后面是视频文件的名称
	public AudioLocation action_audio_url = new Action.AudioLocation(); // jsonArray格式，音频下载地址
	public AudioLocation action_audio_local = new Action.AudioLocation();
	
	public static class AudioLocation implements Serializable{
		public String rest; // 休息
		public String change_side; // 换边
		public String action_explain; // 动作讲解
	}
	
}
