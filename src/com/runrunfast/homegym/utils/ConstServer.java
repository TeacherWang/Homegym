package com.runrunfast.homegym.utils;

import android.os.Environment;

import java.io.File;

public class ConstServer {
	public static final String LOGIN_SERVER = "http://bikeme.duapp.com/";
	public static final String LOGIN_SERVER_TEST = "http://bikemetest.duapp.com/";
	
	public static final String URL_LOGIN = LOGIN_SERVER_TEST + "Login";
	public static final String URL_IDENTIFY = LOGIN_SERVER_TEST + "servlet/PhoneRegisterServlet";
	
	// 登录类型
	public static final String KEY_TYPE = "type";
	
	public static final String TYPE_REGISTER = "register";
	public static final String TYPE_LOGIN = "login";
	public static final String TYPE_RESET = "repassword";
	public static final String TYPE_GET_IDENTY_NUM = "getidentynum";
	
	// key
	public static final String KEY_USER_NAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_IDENTIFY = "identify";
	public static final String KEY_PHONE = "phone";
	
	// 返回码说明
	public static final int RET_OK = 1;
	public static final int RET_RESET_PWD_FAIL 			= -1; // 重置密码失败
	public static final int RET_USER_NAME_EMPTY 		= -3; // 用户名为空
	public static final int RET_PWD_EMPTY 				= -4; // 密码为空
	public static final int RET_LOGIN_FAIL 				= -5; // 登录失败，未认证用户
	public static final int RET_USER_NAME_EXIST 		= -6; // 用户名已存在
	public static final int RET_REGISTE_FAIL 			= -7; // 注册失败，重新获取
	public static final int RET_IDENTIFY_TIMEOUT 		= -8; // 验证码时间过长
	public static final int RET_IDENTIFY_CODE_ERR 		= -9; // 验证码错误
	public static final int RET_THIS_PHONE_HAD_BINDED 	= -4; // 该手机已被绑定
	public static final int RET_NET_ERROR 				= -100; // 我定义的，网络异常
	
	public static final String RET = "ret";
	public static final String IFPHONE = "ifphone";
	public static final String STATUS = "status";
	
	// 登录成功的广播
	public static final String ACTION_LOGIN_SUC = "action_login_suc";
	
	/*
	 * 课程的后台接口
	 */
	public static final String COURSE_SERVER = "http://training.duapp.com/";
	
	/**
	  * @Fields URL_GET_COURSE_INFO : 获取课程信息
	  */
	public static final String URL_GET_COURSE_INFO = COURSE_SERVER + "getCourseInfo";
	/**
	  * @Fields URL_GET_ACTION_INFO : 获取动作信息
	  */
	public static final String URL_GET_ACTION_INFO = COURSE_SERVER + "getActionInfo";
	
	
	public static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String SDCARD_HOMEGYM_ROOT = SDCARD_ROOT + File.separator + "runrunfast" + File.separator + "homegym" + File.separator;
	
}
