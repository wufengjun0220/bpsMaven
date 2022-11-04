package com.mingtech.application.runmanage.domain;

import java.util.Date;

import com.mingtech.application.ecds.common.DictionaryCache;


/**
 * 系统微服务注册
 * @author meng
 *
 */
public class MicserviceRoutes {
	private String id;
	private String serviceId;//外键微服务ID
	private String reqUrl;//请求地址
	private String forwardUrl;//重定向请求地址
	private String description;//描述
	private String apiType;//接口类型
	private String regOperLog;//是否记录操作日志：1是、0否
	private String status;//状态:1启用、0停用
	private Date createDate;//创建日期
	private Date updateDate;//最后更新日期
	private String openGray;//灰度调用：1是、0否
	
	//以下字段子用于页面显示或参数传递
	private String serviceName;//键微服务名称
	private String statusName;//状态名称:1启用、0停用
	private String apiTypeDesc;//API类型描述
	private String regOperLogDesc;//是否记录操作日志描述
	private String openGrayDesc;//灰度调用：1是、0否
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getServiceId() {
		return serviceId;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public String getReqUrl() {
		return reqUrl;
	}
	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}
	public String getForwardUrl() {
		return forwardUrl;
	}
	public void setForwardUrl(String forwardUrl) {
		this.forwardUrl = forwardUrl;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusName() {
		String str = "";
		if("1".equals(this.getStatus())){
			str = "启用";
		}else if("0".equals(this.getStatus())){
			str = "停用";
		}
		return str;
		
	
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getApiTypeDesc() {
		apiTypeDesc = DictionaryCache.getSysLogTypeMap(apiType);
		return apiTypeDesc;
	}
	public String getApiType() {
		return apiType;
	}
	public void setApiType(String apiType) {
		this.apiType = apiType;
	}
	public String getRegOperLog() {
		return regOperLog;
	}
	public void setRegOperLog(String regOperLog) {
		this.regOperLog = regOperLog;
	}
	public String getRegOperLogDesc() {
		regOperLogDesc = DictionaryCache.getCommonYesOrNo(this.regOperLog);
		return regOperLogDesc;
	}
	public String getOpenGray() {
		return openGray;
	}
	public void setOpenGray(String openGray) {
		this.openGray = openGray;
	}
	public String getOpenGrayDesc() {
		return openGrayDesc = DictionaryCache.getCommonYesOrNo(openGray);
	}
	
}
