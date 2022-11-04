package com.mingtech.application.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.cache.service.CacheService;
import com.mingtech.application.ecds.common.domain.HolidayDto;
import com.mingtech.application.ecds.common.service.HolidayService;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.SpringContextUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
 * @作者: yufei
 * @日期: Jun 19, 2009 10:59:10 AM
 * @描述: [HolidayCache]缓存节假日时间，用来计算利息，减少多次与数据库交互。
 */
public class HolidayCache extends GenericServiceImpl implements CacheService{
	private static final Logger logger = Logger.getLogger(HolidayCache.class);
	private static boolean startRedis=false;//启用redis
	@Autowired
	private RedisUtils redisrCache;
	@Autowired
	private HolidayService holidayService;//节假日数据接口

	public static final List holidayList = Collections
			.synchronizedList(new ArrayList());
	
	public void initCache(){
		if(startRedis == true){
			logger.info("-从Redis中获取节假日缓存.............");
			Map holidaListMap = (Map) redisrCache.hmget("holidayCache");
			if(holidaListMap != null && !holidaListMap.isEmpty()){
				return;
			}else{
				logger.info("-从Redis未获取到节假日，重新从db加载节假日缓存.............");
			}
		}else{
			logger.info("-开始从db加载节假日缓存.............");
		}
		this.initDataFromDb();
		//启用redis管理缓存
		if(startRedis){
			this.setMapToReids();
			logger.info("-将节假日保存到redis中成功.............");
			//删除静态变量中缓存
			this.clearAllCache();
		}
		
	}
	 public void initDataFromDb(){
		 try{
				List list = holidayService.getAllHolidayList();
				for(int i = 0; i < list.size(); i++){
					HolidayDto h = (HolidayDto) list.get(i);
					holidayList.add(DateUtils.toString(h.getDDate(), "yyyy-MM-dd"));
				}
			}catch (Exception e){
				logger.error(ErrorCode.ERR_MSG_998,e);
			}
	 }
	 
	 private void setMapToReids(){
		 Map holidayMap = new HashMap();
		 holidayMap.put("holidayList", holidayList);
		 redisrCache.hmset("holidayCache",holidayMap);
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
			logger.info(threadNm+"-将节假日更新到redis中成功.............");
			//删除静态变量中缓存
			this.clearAllCache();
		}else{
			this.clearAllCache();
			this.initCache();
		}
	}
	
	
	public static List getHolidayList(){
		if(startRedis){
			RedisUtils redisrCache = (RedisUtils)SpringContextUtil.getBean("redisrCache");
			List list = (List)redisrCache.hget("holidayCache","holidayList");
			return list != null ? list : new ArrayList();
		}
		return holidayList;
	}
	
	public void clearAllCache(){
		holidayList.clear();
	}


	public Class getEntityClass(){
		return null;
	}

	public String getEntityName(){
		return null;
	}

	@Override
	public void reloadSignleCache(Map dataMap) {
		logger.debug("---");
		
	}

	public boolean isStartRedis() {
		return startRedis;
	}

	public void setStartRedis(boolean startRedis) {
		this.startRedis = startRedis;
	}
	
}
