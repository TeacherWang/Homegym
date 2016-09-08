package com.runrunfast.homegym.utils;

import android.os.Environment;

import java.io.File;

public class ConstServer {
	public static final String LOGIN_SERVER = "http://bikeme.duapp.com/";
	public static final String LOGIN_SERVER_TEST = "http://bikemetest.duapp.com/";
	
	public static final String URL_LOGIN = LOGIN_SERVER_TEST + "Login";
	public static final String URL_IDENTIFY = LOGIN_SERVER_TEST + "servlet/PhoneRegisterServlet";
	
	public static final String URL_UPDATE_PERSONAL_INFO = LOGIN_SERVER_TEST + "PersonalInfo";
	public static final String URL_GET_PERSONAL_INFO = LOGIN_SERVER_TEST + "PersonalInfoCheckOut";
	public static final String URL_UPDATE_HEADIMG = LOGIN_SERVER_TEST + "servlet/HeadPictureServlet";
	
	// 用户信息
	public static final String PERSONAL_USER_NAME = "username";
	public static final String PERSONAL_NICK_NAME = "nick_name";
	public static final String PERSONAL_SEX = "sex";
	public static final String PERSONAL_HEIGHT = "height";
	public static final String PERSONAL_WEIGHT = "weight";
	public static final String PERSONAL_BIRTHDAY = "birthday";
	public static final String PERSONAL_LEVEL = "level";
	public static final String PERSONAL_MINUTES = "minutes";
	public static final String PERSONAL_HRMAX = "hrmax";
	public static final String PERSONAL_HEADID = "headId";
	public static final String PERSONAL_IS_WECHAT = "isWeChat";
	public static final String PERSONAL_WECHAT_HEAD_URL = "weChatHeadUrl";
	public static final String PERSONAL_USER_CITY = "user_city";
	public static final String PERSONAL_PLAN = "plan";
	public static final String PERSONAL_USER_DECLARATION = "user_declaration";
	
	public static final String PERSONAL_TYPE = "type";
	
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
	
	/**
	  * @Fields URL_JOIN_COURSE : 参加课程
	  */
	public static final String URL_JOIN_COURSE = COURSE_SERVER + "joinCourse";
	
	/**
	  * @Fields URL_DELETE_COURSE : 退出课程
	  */
	public static final String URL_DELETE_COURSE = COURSE_SERVER + "deleteCourse";
	
	/**
	  * @Fields URL_UPLOAD_TRAIN_PLAN : 上传定制计划
	  */
	public static final String URL_UPLOAD_TRAIN_PLAN = COURSE_SERVER + "uploadTrainPlan";
	
	/**
	  * @Fields URL_DOWNLOAD_TRAIN_PLAN : 同步定制计划
	  */
	public static final String URL_DOWNLOAD_TRAIN_PLAN = COURSE_SERVER + "downloadTrainPlan";
	
	/**
	  * @Fields URL_RECORD_DATA : 上传本次训练数据
	  */
	public static final String URL_RECORD_DATA = COURSE_SERVER + "record_data";
	
	/**
	  * @Fields URL_REQUEST_TOTAL_DATA : 请求总的完成情况
	  */
	public static final String URL_REQUEST_TOTAL_DATA = COURSE_SERVER + "requestTotalData";
	
	/**
	  * @Fields URL_REQUEST_DETAIL_DATA : 请求详细完成情况
	  */
	public static final String URL_REQUEST_DETAIL_DATA = COURSE_SERVER + "requestDetailData";
	
	
	public static final String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String SDCARD_HOMEGYM_ROOT = SDCARD_ROOT + File.separator + "runrunfast" + File.separator + "homegym" + File.separator;
	
}
