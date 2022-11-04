package com.mingtech.application.ecds.draftcollection.service;

import com.mingtech.framework.common.util.SpringContextUtil;

/**
 * 提示付款服务 工厂
 * @author Administrator
 *
 */
public class ConsignServiceFactory {
	/**
	 * 自动提示付款服务
	 * @return
	 */
	public static ConsignService getConsignService(){
		return (ConsignService) SpringContextUtil.getBean("consignService");
	}
}
