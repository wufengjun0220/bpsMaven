package com.mingtech.application.cache;

import java.util.Map;

import com.mingtech.application.cache.service.CacheService;
import com.mingtech.application.cache.service.CacheUpdateService;
import com.mingtech.framework.common.util.SpringContextUtil;

/**
 * <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
 * @作者: ice
 * @日期: Aug 8, 2019 10:03:35 AM
 * @描述: [SystemCacheUpdateFactory]系统缓存更新工厂
 */
public class SystemCacheUpdateFactory {
	
	protected Map<String,CacheService> cacheUpdateMap;

	public void setCacheUpdateMap(Map<String,CacheService> cacheUpdateMap) {
		this.cacheUpdateMap = cacheUpdateMap;
	}
	
	public CacheService getCacheUpdateService(String cacheType){
		return cacheUpdateMap.get(cacheType);
	}

	public static CacheUpdateService getCacheUpdateService(){
		return  (CacheUpdateService) SpringContextUtil.getBean("cacheUpdateService");
	}
}
