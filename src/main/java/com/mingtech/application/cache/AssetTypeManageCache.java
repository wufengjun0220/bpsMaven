package com.mingtech.application.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.cache.service.CacheService;
import com.mingtech.application.pool.assetmanage.domain.AssetTypeManage;
import com.mingtech.application.pool.assetmanage.service.AssetTypeManageService;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.framework.common.util.SpringContextUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
 * @作者: ice
 * @日期: Jun 15, 2009 10:03:35 AM
 * @描述: [AssetTypeManageCache]资产类型管理缓存Cache
 */
public class AssetTypeManageCache extends GenericServiceImpl implements CacheService{
	
	private static final Logger logger = Logger.getLogger(AssetTypeManageCache.class);
	private static boolean startRedis=false;//启用redis
	
	@Autowired
	private AssetTypeManageService assetTypeManageService;
	@Autowired
	private RedisUtils redisrCache;
	
	private static final Map<String,Map<String,String>> assetTypeManageMap = Collections
			.synchronizedMap(new HashMap<String,Map<String,String>>());//资产类型管理信息
	
	/**
	 * 初始化缓存
	 * @return void
	 */
	public void initCache(){
		if(startRedis == true){
			logger.info("-从Redis中获取资产类型管理缓存.............");
			Map assetTypeManageMap = (Map) redisrCache.hmget("assetTypeManageMap");
			if(assetTypeManageMap != null && !assetTypeManageMap.isEmpty()){
				return;
			}else{
				logger.info("-从Redis未获取到资产类型管理缓存，重新从db加载节假日缓存.............");
			}
		}
		logger.info("-开始从db中获取资产类型管理信息.............");
		this.initDataFromDb();
		//启用redis管理缓存
		if(startRedis){
			this.setMapToReids();
			logger.info("-将资产类型管理缓存更新到redis中成功.............");
			//删除静态变量中缓存
			this.clearAllCache();
		}
	}
	
	 public void initDataFromDb(){
		 List list = assetTypeManageService.queryAllAssetTypeManages();
		 for(int i = 0; i < list.size(); i++){
		    AssetTypeManage assetType = (AssetTypeManage) list.get(i);
			Map<String,String> assetTypeMap = new HashMap<String,String>();
			assetTypeMap.put("assetType", assetType.getAssetType());//资产类型
			assetTypeMap.put("riskType", assetType.getRiskType());//风险类型-低风险
			assetTypeMap.put("amountType", assetType.getAmountType());//金额类型
			assetTypeMap.put("duedateType", assetType.getDuedateType());//到日期类型
			assetTypeMap.put("holidayDelayType", assetType.getHolidayDelayType());//节假日顺延类型
			assetTypeMap.put("assignDelayDay", String.valueOf(assetType.getAssignDelayDay()));//设定顺延天数
			assetTypeManageMap.put(assetType.getAssetType(), assetTypeMap);
		 }
	 }
	 
	 private void setMapToReids(){
		 redisrCache.hmset("assetTypeManageMap",assetTypeManageMap);
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
			logger.info(threadNm+"-将资产类型管理缓存更新到redis中成功.............");
			//删除静态变量中缓存
			this.clearAllCache();
		}else{
			this.clearAllCache();
			this.initCache();
		}
		
	}
	
	public void clearAllCache(){
		assetTypeManageMap.clear();
	}

	  
	/**
	  * 重新加载单笔缓存
	  * @param dataMap 数据集合 key=code value=系统参数配置CODE
	  * @return void 
	  */
	public void reloadSignleCache(Map dataMap){
		logger.debug("---");
	}
	
	/**
	 *根据系统参数编码获取系统参数配置信息
	 * @param code 系统参数编码
	 * @return 系统配置参数值
	 */
	public static Map<String,String> getAssetTypeManageMap(String code){
		if(startRedis){
			return getCacheFromRedis("assetTypeManageMap",code);
		}
		return assetTypeManageMap.containsKey(code) ? assetTypeManageMap.get(code) : null;
	}

	public static Map<String,String> getCacheFromRedis(String mapName,String code){
		if(code == null){
			return new HashMap<String,String>();
		}
		RedisUtils redisrCache = (RedisUtils)SpringContextUtil.getBean("redisrCache");
		return (Map<String,String>)redisrCache.hget(mapName,code);
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
