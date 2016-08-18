package com.runrunfast.homegym.bean;

import java.io.Serializable;

public class Action implements Serializable{
	public String action_id; // 训练动作id
	public String action_name;
	public String action_position; // 锻炼的部位
	public String action_descript; // 简单描述
	public int action_difficult; // 难度等级
	public String action_url; // 视频下载地址
	public String action_local_file; // 视频本地路径
}
