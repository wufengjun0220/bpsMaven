package com.mingtech.application.runmanage.domain;

/**
 * 系统微服配置
 * @author meng
 *
 */
public class MicserviceConfig {
	private String id;
	private String serviceName;//微服务名称
	private String description;//描述
	private String grayUser;//灰度服务使用用户
	private String grayUrl;//灰度服务HTTP请求地址
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getGrayUser() {
		return grayUser;
	}
	public void setGrayUser(String grayUser) {
		this.grayUser = grayUser;
	}
	public String getGrayUrl() {
		return grayUrl;
	}
	public void setGrayUrl(String grayUrl) {
		this.grayUrl = grayUrl;
	}
	
}
