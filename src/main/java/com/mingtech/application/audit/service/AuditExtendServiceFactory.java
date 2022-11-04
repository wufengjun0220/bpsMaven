package com.mingtech.application.audit.service;

import java.util.Map;


/**
 * <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
 * @作者: ice
 * @日期: Aug 8, 2019 10:03:35 AM
 * @描述: [AuditExtendServiceFactory]票据行内流转数据转换工厂
 */
public class AuditExtendServiceFactory {
	
	protected Map<String,AuditExtendService> auditExtendServiceMap;

	public void setAuditExtendServiceMap(Map<String,AuditExtendService> auditExtendServiceMap) {
		this.auditExtendServiceMap = auditExtendServiceMap;
	}
	
	public AuditExtendService getAuditExtendService(String dataType){
		return auditExtendServiceMap.get(dataType);
	} 
}
