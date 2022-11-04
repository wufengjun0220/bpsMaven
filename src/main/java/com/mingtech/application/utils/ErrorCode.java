
package com.mingtech.application.utils;

/**
 * <p> Description:错误码常量定义</p>
 *
 * @author h2 20210418
 */
public class ErrorCode {

	//工具类必须要定义一个私有的构造器-弱扫
	private ErrorCode(){}
	 public static final String SUCC_MSG_CODE="0000";
     public static final String SUCC_MSG_CH = "处理成功";
     public static final String SUCC_MSG_QU = "查询成功";
	 public static final String Err_MSG_CODE="9999";

	 public static final String ERR_MSG_998 = "业务处理异常"; 
	 public static final String ERR_MSG_997 = "查询数据出错";
	 public static final String ERR_MSG_996 = "保存或修改数据表数据出错";
	 public static final String ERR_MSG_995 = "设定线程睡眠出错";
	 public static final String ERR_MSG_994 = "动态生成类出错";
	 public static final String ERR_CALL_REDIS = "访问redis异常";
	 public static final String ERR_GET_SERVER_IP = "获取本机IP出错";
	 public static final String ERR_LOGIN = "登录失败";
	 public static final String ERR_STATUS = "状态不正确";
	 public static final String ERR_TASK_PUBLISH="自动任务发布失败";
	 public static final String ERR_NO_ASSET_REGISTER="未查询到资产登记信息";
	
	 
	
	 
}

