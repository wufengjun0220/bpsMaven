package com.mingtech.application.cache.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.cache.SystemCacheUpdateFactory;
import com.mingtech.application.cache.service.CacheService;
import com.mingtech.application.cache.service.CacheUpdateService;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
 * @作者: ice
 * @日期: Jun 15, 2009 10:03:35 AM
 * @描述: [RunStateCache]系统运行状态Cache
 */
public class CacheUpdateServiceImpl extends GenericServiceImpl implements CacheUpdateService{
	private static final Logger logger = Logger.getLogger(CacheUpdateServiceImpl.class);
	
	//系统缓存更新工厂
	@Autowired
	private SystemCacheUpdateFactory systemCacheUpdateFactory;
	
	
	/**
	 * 缓存更新服务
	 * @param cacheType缓存类型:01系统参数、02系统运行状态、03数据字典、04产品类型
	 * @return 0000缓存更新成功、否则返回缓存更新系统的系统IP
	 */
	 public String txCacheUpdate(String cacheType){
		
		//根据缓存类型获取缓存更新服务
		CacheService cacheService = systemCacheUpdateFactory.getCacheUpdateService(cacheType);
		//更新当前应用缓存
		cacheService.reloadCache();
		
		return "0000";
		
	 }
	
	public Class getEntityClass(){
		return null;
	}

	public String getEntityName(){
		return null;
	}

}
