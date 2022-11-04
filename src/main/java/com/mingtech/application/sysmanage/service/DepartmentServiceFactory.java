package com.mingtech.application.sysmanage.service;

import com.mingtech.framework.common.util.SpringContextUtil;
/**
 * 机构  服务工厂
 * @author Administrator
 *
 */
public class DepartmentServiceFactory {
	/**
	 * 获取 机构服务 
	 * @return
	 */
	public static DepartmentService getDepartmentService(){
		return (DepartmentService) SpringContextUtil.getBean("departmentService");
	}
}
