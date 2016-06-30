package com.runrunfast.homegym.start;

public class ConstLogin {
	public static final String SERVER = "http://bikeme.duapp.com/";
	public static final String SERVER_TEST = "http://bikemetest.duapp.com/";
	
	public static final String URL_LOGIN = SERVER_TEST + "Login?";
	public static final String URL_IDENTIFY = SERVER_TEST + "servlet/PhoneRegisterServlet?";
	
	// 登录类型
	public static final String KEY_TYPE = "type";
	
	public static final String TYPE_REGISTER = "register";
	public static final String TYPE_LOGIN = "login";
	public static final String TYPE_RESET = "repassword";
	public static final String TYPE_GET_IDENTY_NUM = "getidentynum";
	
	// key
	public static final String KEY_USER_NAME = "username";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_IDENTIFY = "Identify";
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
}
