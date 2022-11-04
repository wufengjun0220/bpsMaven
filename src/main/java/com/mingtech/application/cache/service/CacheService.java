package com.mingtech.application.cache.service;

import java.util.Map;


public interface CacheService{
	/**
	 * 初始化缓存
	 * @return void
	 */
	  public void initCache();
	  
	/**
	 * 重新加载缓存
	 * @return void
	 */
	  public void reloadCache();
	  
	  /**
		* 清理缓存
		* @return void
		*/
	  public void clearAllCache();
	  
	  /**
		* 重新加载单笔缓存
		* @param dataMap 数据集合
		* @return void 
		*/
	  public void reloadSignleCache(Map dataMap);
	  
	  /**
		* 是否启用redis进行缓存处理
		* @return void 
		*/
	  public boolean isStartRedis();
}
