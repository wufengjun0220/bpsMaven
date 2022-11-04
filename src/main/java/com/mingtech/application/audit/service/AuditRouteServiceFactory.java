package com.mingtech.application.audit.service;

import com.mingtech.framework.common.util.SpringContextUtil;

public class AuditRouteServiceFactory {
	
	public static AuditRouteService getAuditRouteService(){
		return (AuditRouteService) SpringContextUtil.getBean("auditRouteService");
	}
}
