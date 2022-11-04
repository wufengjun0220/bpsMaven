package com.mingtech.framework.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 类说明：获取系统配置资源
 *
 * @author liumeng@ May 11, 2009
 */
public class HRBConfig {
	private static HRBConfig config;
	private Properties properties;
	private Logger logger = Logger.getLogger(HRBConfig.class);

	/**
	 * 构造函数 加载配置文件
	 */
	private HRBConfig() {
		try {
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream("config/HRB.properties");
			properties = new Properties();
			properties.load(is);
		} catch (IOException ex) {
			logger.error(ex);
		}
	}

	/**
	 * 获取项目配置对象实例
	 *
	 * @return
	 * @author:liumeng@
	 * @time:May 11, 2009
	 */
	public static HRBConfig getInstance() {
		if (null != config) {
			return config;
		} else {
			return new HRBConfig();
		}
	}
	/**
	 * 
	* <p>方法名称: getHostName|描述:获取FTP 主机IP </p>
	* @return
	 */
	
	public String getHostName(){
		if(null != properties){
			return properties.getProperty("FTP.hostName").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getUserName|描述:获取FTP用户名 </p>
	* @return
	 */
	public String getUserName(){
		if(null != properties){
			return properties.getProperty("FTP.userName").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getPassWord|描述: 获取FTP密码</p>
	* @return
	 */
	public String getPassWord(){
		if(null != properties){
			return properties.getProperty("FTP.passWord").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getHostPort|描述:获取FTP端口号 </p>
	* @return
	 */
	public String getHostPort(){
		if(null != properties){
			return properties.getProperty("FTP.hostPort").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getUserPath|描述:获得用户主目录 </p>
	* @return
	 */
	public String getUserPath(){
		if(null != properties){
			return properties.getProperty("FTP.userPath").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getHostName|描述:获取全流程信贷服务FTP 主机IP </p>
	* @return
	 */
	
	public String getCreditServerHostName(){
		if(null != properties){
			return properties.getProperty("CreditServer.hostName").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getUserName|描述:获取全流程信贷服务FTP用户名 </p>
	* @return
	 */
	public String getCreditServerUserName(){
		if(null != properties){
			return properties.getProperty("CreditServer.userName").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getPassWord|描述: 获取全流程信贷服务FTP密码</p>
	* @return
	 */
	public String getCreditServerPassWord(){
		if(null != properties){
			return properties.getProperty("CreditServer.passWord").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getHostPort|描述:获取全流程信贷服务FTP端口号 </p>
	* @return
	 */
	public String getCreditServerHostPort(){
		if(null != properties){
			return properties.getProperty("CreditServer.hostPort").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getUserPath|描述:获得全流程信贷服务用户主目录 </p>
	* @return
	 */
	public String getCreditServerUserPath(){
		if(null != properties){
			return properties.getProperty("CreditServer.userPath").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getHostName|描述:获取FTP 主机IP </p>
	* @return
	 */
	
	public String getPaperName(){
		if(null != properties){
			return properties.getProperty("PaperServer.hostName").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getUserName|描述:获取FTP用户名 </p>
	* @return
	 */
	public String getPaperUserName(){
		if(null != properties){
			return properties.getProperty("PaperServer.userName").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getPassWord|描述: 获取FTP密码</p>
	* @return
	 */
	public String getPaperPassWord(){
		if(null != properties){
			return properties.getProperty("PaperServer.passWord").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getHostPort|描述:获取FTP端口号 </p>
	* @return
	 */
	public String getPaperHostPort(){
		if(null != properties){
			return properties.getProperty("PaperServer.hostPort").toString();
		}else{
			return "";
		}
	}
	/**
	 * 
	* <p>方法名称: getUserPath|描述:获得用户主目录 </p>
	* @return
	 */
	public String getPaperUserPath(){
		if(null != properties){
			return properties.getProperty("PaperServer.userPath").toString();
		}else{
			return "";
		}
	}
	
	/**
	 * 
	* <p>方法名称: getUserPath|描述:获得用户主目录 </p>
	* @return
	 */
	public String getPaperLocalPath(){
		if(null != properties){
			return properties.getProperty("PaperServer.localPath").toString();
		}else{
			return "";
		}
	}
	
	/**
	 * 
	* <p>方法名称: getPaperBackUpPath|描述:获得用户主目录 </p>
	* @return
	 */
	public String getPaperBackUpPath(){
		if(null != properties){
			return properties.getProperty("PaperServer.backUpPath").toString();
		}else{
			return "";
		}
	}
	
	/**
	 * 
	* <p>方法名称: getEdraftUploadTempPath|描述:获得用户主目录 </p>
	* @return
	 */
	public String getEdraftUploadTempPath(){
		if(null != properties){
			return properties.getProperty("Edraft.uploadTempPath").toString();
		}else{
			return "";
		}
	}
	
	/**
	 * 
	* <p>方法名称: getEcdsChnlNo|描述:获得票据系统渠道号 </p>
	* @return
	 */
	public String getEcdsChnlNo(){
		if(null != properties){
			return properties.getProperty("ChnlNo.ecds").toString();
		}else{
			return "";
		}
	}
	
	/**
	 * 
	* <p>方法名称: getLocalFileInPath|描述:NAS上送文件目录 </p>
	* @return
	 */
	public String getLocalFileInPath(){
		if(null != properties){
			return properties.getProperty("BatchFile.localFileInPath").toString();
		}else{
			return "";
		}
	}
	
	/**
	 * 
	* <p>方法名称: getLocalFileOutPath|描述:NAS返还文件目录 </p>
	* @return
	 */
	public String getLocalFileOutPath(){
		if(null != properties){
			return properties.getProperty("BatchFile.localFileOutPath").toString();
		}else{
			return "";
		}
	}
	
}
