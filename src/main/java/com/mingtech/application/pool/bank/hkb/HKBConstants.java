/**
 * 
 */
package com.mingtech.application.pool.bank.hkb;

import java.io.File;

import com.mingtech.application.pool.bank.message.Constants;

/**
 * @author xukai
 *
 */
public class HKBConstants extends Constants {
	
	public static final String SUCCESS_RET_STATUS = "S";//成功
	public static final String FAIL_RET_STATUS = "F";//失败
	
	/**信贷交易返回码**/
	public static final String XD_SUCCESS_RET_CODE = "000000";
	public static final String XD_SUCCESS_RET_MSG = "交易成功";
	
	/**响应代码常量**/
	public static final String RESPONSE_SUCCESS_RET_CODE = "000000";
	public static final String RESPONSE_SUCCESS_RET_MSG = "交易成功";
	
	public static final String RESPONSE_FAIL_RET_CODE = "9999";
	public static final String RESPONSE_FAIL_RET_MSG = "调用接口失败";
	
	public static final String RESPONSE_FAIL_RET_CODE_PJC001 = "PJC001"; 
	public static final String RESPONSE_FAIL_RET_MSG_PJC001 = "交易处理失败";
	
	public static final String RESPONSE_FAIL_RET_CODE_PJC002 = "PJC002";    
	public static final String RESPONSE_FAIL_RET_MSG_PJC002 = "请求报文格式有误";
	
	public static final String RESPONSE_FAIL_RET_CODE_PJC003 = "PJC003";	   
	public static final String RESPONSE_FAIL_RET_MSG_PJC003 = "不支持的交易类型";
	
	public static final String RESPONSE_FAIL_RET_CODE_PJC004 = "PJC004";		
	public static final String RESPONSE_FAIL_RET_MSG_PJC004 = "根据网点号获取大额支付行号失败";
	
	public static final String RESPONSE_FAIL_RET_CODE_PJC005 = "PJC005";		
	public static final String RESPONSE_FAIL_RET_MSG_PJC005 = "根据帐号获取组织机构代码失败";
	
	public static final String RESPONSE_FAIL_RET_CODE_PJC006 = "PJC006";		
	public static final String RESPONSE_FAIL_RET_MSG_PJC006 = "企业帐号不存在指定票号的票据";
	
	public static final String RESPONSE_FAIL_RET_CODE_PJC007 = "PJC007";		
	public static final String RESPONSE_FAIL_RET_MSG_PJC007 = "（回购式贴现）贴现申请日期小于贴现赎回开放日";
	
	public static final String RESPONSE_FAIL_RET_CODE_PJC008 = "PJC008";		
	public static final String RESPONSE_FAIL_RET_MSG_PJC008 = "重复提交的交易";
	
	public static final String RESPONSE_FAIL_RET_CODE_PJC009 = "PJC009";		
	public static final String RESPONSE_FAIL_RET_MSG_PJC009 = "本行帐号校验失败";
	
	public static final String RESPONSE_FAIL_RET_CODE_PJC021 = "PJC021";		
	public static final String RESPONSE_FAIL_RET_MSG_PJC021 = "文件下载出错";
	
	public static final String SYSTEM_ID_CORE = "CB";//核心系统号
	public static final String SYSTEM_ID_CREDIT = "CMS";//信贷系统号
	public static final String SYSTEM_ID_NETBANK = "IBS";//网银系统号
	public static final String SYSTEM_ID_ECDS = "ECDS";//票据系统号
	
	public static final String ESB_MSG_SYS = "SYS_HEAD";	//ESB报文SYS_HEAD
	public static final String ESB_MSG_APP = "APP_HEAD";    //ESB报文APP_HEAD
	public static final String ESB_MSG_LOCAL = "LOCAL_HEAD";//ESB报文LOCAL_HEAD
	public static final String ESB_MSG_FILE = "FILE_HEAD";  //ESB报文FILE_HEAD
	public static final String ESB_MSG_BODY = "BODY";       //ESB报文BODY
	
	public static final String ESB_MSG_RET_STATUS = "RET_STATUS"; //报文返回状态
	public static final String ESB_MSG_RET_CODE = "RET.RET_CODE"; //报文返回码
	public static final String ESB_MSG_RET_MSG = "RET.RET_MSG";   //报文返回信息
	
	/****************************以下为FTP文件传输使用的常量***************************************************/
//	public static final String FTP_CLIENT_CONFIG = File.separator+"was"+File.separator+"IBM"+File.separator+"WebSphere"+File.separator+"AppServer"+File.separator+"profiles"+File.separator+"AppSrv01"+File.separator+"installedApps"+File.separator+"localhostNode01Cell"+File.separator+"HKB_war.ear"+File.separator+"HKB.war"+File.separator+"WEB-INF"+File.separator+"classes"+File.separator+"config"+File.separator+"FtpClientConfig.properties" ;//ftp客户端配置文件	
//	public static final String FTP_CLIENT_CONFIG = "/config/FtpClientConfig.properties";//ftp客户端配置文件
//	public static final String FTP_CLIENT_CONFIG ="/was/IBM/WebSphere/AppServer/profiles/AppSrv01/installedApps/localhostNode01Cell/HKB_war.ear/HKB.war/WEB-INF/classes/config/FtpClientConfig.properties";
	public static final String FTP_CLIENT_CONFIG = "FtpClientConfig.properties";//ftp客户端配置文件
	public static final String FTP_FILE_FLAG = "FILE_FLAG";
	
	public static final String FILE_FLAG_0 = "0"; //0-无文件;
	public static final String FILE_FLAG_1 = "1"; //1-有文件，上传，使用FTP或其他文件传输服务进行获取;
	public static final String FILE_FLAG_2 = "2"; //2-有文件，下载，使用FTP或其他文件传输服务进行获取;
	public static final String FILE_FLAG_A = "A"; //A-有文件，文件使用指定的文件传输服务;
	public static final String FILE_FLAG_B = "B"; //B-有文件，文件内容在报文体的FILE_VIEW字段中
	
	public static final String FTP_FILE_PATH = "FILE_PATH";//ftp文件路径
	public static final String FTP_DELIMITOR = "DELIMITOR";//ftp文件分割符
	public static final String FTP_SPILCT_CODE = "|";//ftp文件分割符
	
}
