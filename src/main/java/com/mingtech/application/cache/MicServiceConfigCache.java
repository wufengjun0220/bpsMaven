package com.mingtech.application.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.cache.service.CacheService;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.runmanage.domain.MicserviceConfig;
import com.mingtech.application.runmanage.service.MicserviceConfigService;
import com.mingtech.framework.common.util.SpringContextUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * 微服务配置
 * @author limaosong
 *
 */
public class MicServiceConfigCache extends GenericServiceImpl implements CacheService{
	
	public static final Logger logger = Logger.getLogger(MicServiceConfigCache.class);
	private static boolean startRedis=false;//启用redis
	
	@Autowired
	private MicserviceConfigService micserviceConfigService;
	@Autowired
	private RedisUtils redisrCache;
	
	private static final Map micSrvGrayUserMap = Collections
			.synchronizedMap(new HashMap());//灰度服务使用用户
	private static final Map micSrvGrayUrlMap = Collections
			.synchronizedMap(new HashMap());//灰度服务调用url
	

	/**
	 * 初始化缓存
	 * @return void
	 */
	public void initCache(){
		if(startRedis == true){
			this.logger.info("-从Redis中获取微服务配置缓存.............");
			Map tmpConfigMap = (Map) redisrCache.hmget("micSrvGrayUser");
			if(tmpConfigMap != null && !tmpConfigMap.isEmpty()){
				return;
			}
			this.logger.info("从Redis中未获取到微服务配置缓存,从DB中重新加载.............");
		}else{
			this.logger.info("-开始从db加载微服务配置缓存.............");
		}
		this.initDataFromDb();
		//启用redis管理缓存
		if(startRedis){
			this.setMapToReids();
			logger.info("-将微服务配置更新到redis中成功.............");
			//删除静态变量中缓存
			this.clearAllCache();
		}
	}
	
	 public void initDataFromDb(){
		 List list = micserviceConfigService.queryMicserviceConfigList();
		 for(int i = 0; i < list.size(); i++){
			 MicserviceConfig micserviceConfig = (MicserviceConfig) list.get(i);
			 micSrvGrayUserMap.put(micserviceConfig.getServiceName(), micserviceConfig.getGrayUser());//项目和灰度服务使用用户
			 micSrvGrayUrlMap.put(micserviceConfig.getServiceName(), micserviceConfig.getGrayUrl());//项目和灰度服务调用url
		 }
	 }
	 
	 private void setMapToReids(){
		 redisrCache.hmset("micSrvGrayUserMap",micSrvGrayUserMap);
		 redisrCache.hmset("micSrvGrayUrlMap",micSrvGrayUrlMap);
	 }
	
	/**
	 * 重新加载缓存
	 * @return void
	 */
	public void reloadCache(){
		long threadNm = Thread.currentThread().getId();
		if(startRedis == true){
			this.initDataFromDb();
			this.setMapToReids();
			logger.info(threadNm+"-将微服务路由配置更新到redis中成功.............");
			//删除静态变量中缓存
			this.clearAllCache();
		}else{
			this.clearAllCache();
			this.initCache();
		}
		
	}
	
	public void clearAllCache(){
		micSrvGrayUserMap.clear();
		micSrvGrayUrlMap.clear();
	}

	  
	/**
	  * 重新加载单笔缓存
	  * @param dataMap 数据集合 key=code value=系统参数配置CODE
	  * @return void 
	  */
	public void reloadSignleCache(Map dataMap){

	}
	
	/**
	 * 获取灰度服务使用用户
	 * @param serviceName
	 * @return
	 */
	public static String getMicSrvGrayUserMap(String serviceName) {
		if(startRedis){
			return getDicNameFromRedis("micSrvGrayUserMap",serviceName);
		}
		return micSrvGrayUserMap.containsKey(serviceName) ? (String)micSrvGrayUserMap.get(serviceName) : "";
	}
	
	/**
	 * 灰度服务调用url
	 * @param serviceName
	 * @return
	 */
	public static String getMicSrvGrayUrlMap(String serviceName) {
		if(startRedis){
			return getDicNameFromRedis("micSrvGrayUrlMap",serviceName);
		}
		return micSrvGrayUrlMap.containsKey(serviceName) ? (String)micSrvGrayUrlMap.get(serviceName) : "";
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
