package com.mingtech.framework.common.util;
/**
 * 配置文件中的参数池，spring自动加载
 *
 */
public final class SystemConfig {
	
	private String initPassword;                     //系统初始化密码
	private int pwdInvalidDay;                       //密码失效天数
	private String filePath;                         //附件文件默认存储路径
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getInitPassword() {
		return initPassword;
	}
	public void setInitPassword(String initPassword) {
		this.initPassword = initPassword;
	}
	public int getPwdInvalidDay() {
		return pwdInvalidDay;
	}
	public void setPwdInvalidDay(int pwdInvalidDay) {
		this.pwdInvalidDay = pwdInvalidDay;
	}
	
}
