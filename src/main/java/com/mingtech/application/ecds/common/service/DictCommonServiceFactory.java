package com.mingtech.application.ecds.common.service;

import com.mingtech.framework.common.util.SpringContextUtil;
/**
 * 字典服务 工厂
 * @author Administrator
 *
 */
public class DictCommonServiceFactory {
	/**
	 * 字典服务
	 * @return
	 */
	public static DictCommonService getDictCommonService(){
		return (DictCommonService)SpringContextUtil.getBean("dictCommonService");
	}
}
