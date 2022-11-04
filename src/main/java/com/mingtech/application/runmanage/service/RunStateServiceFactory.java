package com.mingtech.application.runmanage.service;

import com.mingtech.framework.common.util.SpringContextUtil;
/**
 * 系统状态工厂 
 * @author Administrator
 *
 */
public class RunStateServiceFactory {
	/**
	 * 获取系统状态服务
	 * @return
	 */
	public static RunStateService getRunStateService(){
		return (RunStateService)SpringContextUtil.getBean("runStateService");
	}
}
