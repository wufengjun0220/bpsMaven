package com.mingtech.application.cache.service;

public interface CacheUpdateService{
	/**
	 * 缓存更新服务
	 * @param cacheType缓存类型:01系统参数、02系统运行状态、03数据字典、04产品类型
	 * @return 0000缓存更新成功、否则返回缓存更新系统的系统IP
	 */
	 public String txCacheUpdate(String cacheType);
}
