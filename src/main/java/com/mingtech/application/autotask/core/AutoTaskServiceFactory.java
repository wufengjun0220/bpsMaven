package com.mingtech.application.autotask.core;

import com.mingtech.application.autotask.core.autotask.IAutoTaskService;
import com.mingtech.framework.common.util.SpringContextUtil;
/**
 * 日终任务   服务 工厂类
 *
 */
public class AutoTaskServiceFactory {
	/**
	 * 日终任务 服务
	 * @return
	 */
	public static IAutoTaskService getAutoTaskService(){
		IAutoTaskService service = (IAutoTaskService)SpringContextUtil.getBean("autoTaskService");
		return service;
	}
}
