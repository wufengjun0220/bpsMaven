package com.mingtech.application.cache;

import org.apache.commons.lang.StringUtils;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.framework.common.util.SpringContextUtil;

/**
 * <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
 * @作者: ice
 * @日期: Jun 15, 2019 10:03:35 AM
 * @描述: [ProductTypeCache]产品类型定义Cache
 */
public class ProductTypeCache{
	
	//根据产品ID获取产品名称
	public static String getProductName(String prodId){
		return getDicNameFromRedis("productTypeMap",prodId);
	}
	//根据产品ID获取该产品对应的代办任务名称
	public static String getProductTaskNm(String prodId){
		return getDicNameFromRedis("productTaskNmMap",prodId);
	}
	
	public static String getDicNameFromRedis(String mapName,String code){
		if(code == null){
			return null;
		}
		RedisUtils redisrCache = (RedisUtils)SpringContextUtil.getBean("redisrCache");
		String name = (String)redisrCache.hget(mapName,code);
		return StringUtils.isNotBlank(name)?name:code;
	}
}
