package com.mingtech.application.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.mingtech.application.cache.service.CacheService;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.runmanage.domain.MicserviceRoutes;
import com.mingtech.application.runmanage.service.MicserviceRoutesService;
import com.mingtech.framework.common.util.SpringContextUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
 * @作者: ice
 * @日期: Jun 15, 2009 10:03:35 AM
 * @描述: [MicServiceRouteCache]微服务路由配置Cache
 */
public class MicServiceRouteCache extends GenericServiceImpl implements CacheService{
	private static final Logger logger = Logger.getLogger(MicServiceRouteCache.class);
	private static boolean startRedis=false;//启用redis
	
	@Autowired
	private MicserviceRoutesService micserviceRoutesService;
	@Autowired
	private RedisUtils redisrCache;
	
	private static final Map routesMap = Collections.synchronizedMap(new HashMap());//微服务路由配置缓存
	private static final Map routesDescMap = Collections.synchronizedMap(new HashMap());//微服务路由配置描述
	private static final Map apiOpenGrayConf = Collections.synchronizedMap(new HashMap());//微服务配置缓存
	
	/**
	 * 初始化缓存
	 * @return void
	 */
	public void initCache(){
		/*
		 * 启动必重新加载
		 */
		this.initDataFromDb();
		this.setMapToReids();
		logger.info("-将路由信息保存到redis中成功.............");
		//删除静态变量中缓存
		this.clearAllCache();
	}
	
	 public void initDataFromDb(){
		 List list = micserviceRoutesService.queryAllMicserviceRoutes();
		 for(int i = 0; i < list.size(); i++){
			 MicserviceRoutes route = (MicserviceRoutes) list.get(i);
			 routesMap.put(route.getReqUrl(), route.getForwardUrl());
			 Map tmpMap = new HashMap();
			 tmpMap.put("apiType", route.getApiType());
			 tmpMap.put("description", route.getDescription());
			 tmpMap.put("regOperLog", route.getRegOperLog());//是否记录操作日志
			 routesDescMap.put(route.getReqUrl(), JSON.toJSONString(tmpMap));
			 apiOpenGrayConf.put(route.getReqUrl(),route.getOpenGray());
		 }
	 }
	 
	 private void setMapToReids(){
		 redisrCache.hmset("routes",routesMap);
		 redisrCache.hmset("routesDesc",routesDescMap);
		 redisrCache.hmset("apiOpenGrayConf",apiOpenGrayConf);
	 }
	
	/**
	 * 重新加载缓存
	 * @return void
	 */
	public void reloadCache(){
		if(startRedis == true){
			this.initDataFromDb();
			this.setMapToReids();
			logger.info("-将微服务路由配置更新到redis中成功.............");
			//删除静态变量中缓存
			this.clearAllCache();
		}else{
			this.clearAllCache();
			this.initCache();
		}
		
	}
	
	public void clearAllCache(){
		routesMap.clear();
		routesDescMap.clear();
		apiOpenGrayConf.clear();
	}

	  
	/**
	  * 重新加载单笔缓存
	  * @param dataMap 数据集合 key=code value=系统参数配置CODE
	  * @return void 
	  */
	public void reloadSignleCache(Map dataMap){

	}
	
	
	public static String getRoutesDescMap(String reqUrl) {
		if(startRedis){
			return getDicNameFromRedis("routesDesc",reqUrl);
		}
		return routesDescMap.containsKey(reqUrl) ? (String)routesDescMap.get(reqUrl) : "";
	}
	
	/**
	   *  获取灰度调用
	 * @param reqUrl
	 * @return
	 */
	public static String getAPIOpenGrayConf(String reqUrl) {
		if(startRedis) {
			return getDicNameFromRedis("apiOpenGrayConf",reqUrl);
		}
		return apiOpenGrayConf.containsKey(reqUrl) ? (String)apiOpenGrayConf.get(reqUrl):"";
	}
	
	public static String getDicNameFromRedis(String mapName,String code){
		RedisUtils redisrCache = (RedisUtils)SpringContextUtil.getBean("redisrCache");
		String name = (String)redisrCache.hget(mapName,code);
		return StringUtils.isNotBlank(name)?name:"";
	}


	public Class getEntityClass(){
		return null;
	}

	public String getEntityName(){
		return null;
	}

	public boolean isStartRedis() {
		return startRedis;
	}

	public void setStartRedis(boolean startRedis) {
		this.startRedis = startRedis;
	}
}
