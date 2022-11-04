package com.mingtech.application.common.logic;

import com.mingtech.framework.common.util.SpringContextUtil;
/**
 * 字典服务 工厂类
 * @author Administrator
 *
 */
public class DictionaryServiceFactory {
	/**
	 * 获取字典服务
	 * @return
	 */
	public static IDictionaryService getDictionaryService(){
		return (IDictionaryService)SpringContextUtil.getBean("dictionaryService");
	}
}
