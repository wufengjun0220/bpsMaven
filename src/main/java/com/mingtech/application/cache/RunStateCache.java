package com.mingtech.application.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.mingtech.application.cache.service.CacheService;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.runmanage.domain.RunState;
import com.mingtech.application.runmanage.service.RunStateService;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.SpringContextUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>版权所有:(C)2013-2025 北京明润华创科技有限责任公司</p>
 * @作者: ice
 * @日期: Jun 15, 2009 10:03:35 AM
 * @描述: [RunStateCache]系统运行状态Cache
 */
public class RunStateCache extends GenericServiceImpl implements CacheService{
	private static final Logger logger = Logger.getLogger(RunStateCache.class);
	@Autowired
	private RunStateService runStateService;
	@Autowired
	private RedisUtils redisrCache;
	
	private static boolean startRedis=false;//启用redis
	
	private static final Map runstateCacheMap=Collections
			.synchronizedMap(new HashMap());//系统运行状态缓存
	/**
	 * 初始化缓存
	 * @return void
	 */
	public void initCache(){
		if(startRedis == true){
			logger.info("从Redis中获取系统运行状态缓存Runstate.............");
			Map runStateMap = (Map) redisrCache.hmget("runState");
			if(runStateMap == null || runStateMap.isEmpty()){
				logger.info("从Redis中未获取到缓存Runstate,从DB中重新加载.............");
				RunState rs = runStateService.getSysRunStateFromDb();
				if(rs != null){
					runStateMap = new HashMap();
					runStateMap.put("ID",rs.getId());
					runStateMap.put("RS_BANKNAME",rs.getBankName());
					runStateMap.put("RS_APID",rs.getApId());
					runStateMap.put("RS_APNAME",rs.getApName());
					runStateMap.put("RS_LOGONSTATE",rs.getLogonState());
					runStateMap.put("RS_OLDPWD",rs.getOldPwd());
					runStateMap.put("RS_NEWPWD",rs.getNewPwd());
					runStateMap.put("RS_ORGNLSYSDT",DateUtils.toString(rs.getOrgnlSysDt(),DateUtils.ORA_DATES_FORMAT));
					runStateMap.put("RS_ORGNLSYSSTS",rs.getOrgnlSysSts());
					runStateMap.put("RS_CURDATE",DateUtils.toString(rs.getCurDate(),DateUtils.ORA_DATES_FORMAT));
					runStateMap.put("RS_SYSSTATE",rs.getSysState());
					runStateMap.put("RS_NEXTDATE",DateUtils.toString(rs.getNextDate(),DateUtils.ORA_DATES_FORMAT));
					runStateMap.put("RS_BIZREFTM",rs.getBizRefTm());
					runStateMap.put("RS_RMRK",rs.getRmrk());
					//runStateMap.put("RS_CRLFILE",rs.getApName());
					//runStateMap.put("RS_CRLLASTUPDATETIME",rs.getCrlLastUpdateTime());
					runStateMap.put("RS_PRCMSG",rs.getPrcMsg());
					runStateMap.put("RS_PRCCD",rs.getPrcCd());
					runStateMap.put("RS_BANKCODE",rs.getBankCode());
					runStateMap.put("RS_PFXPATH",rs.getPfxPath());
					runStateMap.put("RS_PFXPASSWORD",rs.getPfxPassword());
					runStateMap.put("RS_CRLPATH",rs.getCrlPath());
					runStateMap.put("RS_STTLMONLINEMRK",rs.getSttlmOnlineMrk());
					runStateMap.put("RS_STTLMONLINERMRK",rs.getSttlmOnlineRmrk());
					runStateMap.put("RS_HVPSNXTSYSDT",DateUtils.toString(rs.getHvpsNxtSysDt(),DateUtils.ORA_DATES_FORMAT));
					runStateMap.put("RS_NPCCODE",rs.getNpcCode());
					runStateMap.put("RS_PFXFILENAME",rs.getPfxFileName());
					runStateMap.put("RS_UNIFYKEY",rs.getUnifyKey());
					runStateMap.put("RS_NPCSTATUS",rs.getNpcStatus());
					runStateMap.put("RS_CCPCCODE",rs.getCcpcCode());
					runStateMap.put("RS_CCPCSTATUS",rs.getCcpcStatus());
					runStateMap.put("RS_SYSPARTNER",rs.getSysPartner());
					runStateMap.put("RS_OPERSWITCH",rs.getOperSwitch());
					runStateMap.put("RS_SWITCHTYPE",rs.getSwithType());
					runStateMap.put("CD_REGION",rs.getRegion());
					runStateMap.put("CD_BANKCODETYPE",rs.getBankcodetype());
					runStateMap.put("WORK_DATE",DateUtils.toString(rs.getWorkDate(),DateUtils.ORA_DATES_FORMAT));
					runStateMap.put("SYSTEM_SWITCH",rs.getSystemSwitch());
					redisrCache.hmset("runState",runStateMap);
					logger.info("-将系统运行状态保存到redis中成功.............");
				}
			}
		}else{
			logger.info("开始从db加载系统运行状态缓存.............");
			// 获取所有数据字典项
			RunState rs = runStateService.getSysRunStateFromDb();
			runstateCacheMap.put("RunState", rs);
			logger.info("-runstate cache load succes. ");
		}
		
	}
	
	/**
	 * 重新加载缓存
	 * @return void
	 */
	public void reloadCache(){
		long threadNm = Thread.currentThread().getId();
		if(startRedis == true){
			RunState rs = runStateService.getSysRunStateFromDb();
			if(rs != null){
				Map<String,String> runStateMap = new HashMap();
				runStateMap.put("ID",rs.getId());
				runStateMap.put("RS_BANKNAME",rs.getBankName());
				runStateMap.put("RS_APID",rs.getApId());
				runStateMap.put("RS_APNAME",rs.getApName());
				runStateMap.put("RS_LOGONSTATE",rs.getLogonState());
				runStateMap.put("RS_OLDPWD",rs.getOldPwd());
				runStateMap.put("RS_NEWPWD",rs.getNewPwd());
				runStateMap.put("RS_ORGNLSYSDT",DateUtils.toString(rs.getOrgnlSysDt(),DateUtils.ORA_DATES_FORMAT));
				runStateMap.put("RS_ORGNLSYSSTS",rs.getOrgnlSysSts());
				runStateMap.put("RS_CURDATE",DateUtils.toString(rs.getCurDate(),DateUtils.ORA_DATES_FORMAT));
				runStateMap.put("RS_SYSSTATE",rs.getSysState());
				runStateMap.put("RS_NEXTDATE",DateUtils.toString(rs.getNextDate(),DateUtils.ORA_DATES_FORMAT));
				runStateMap.put("RS_BIZREFTM",rs.getBizRefTm());
				runStateMap.put("RS_RMRK",rs.getRmrk());
				//runStateMap.put("RS_CRLFILE",rs.getApName());
				//runStateMap.put("RS_CRLLASTUPDATETIME",rs.getCrlLastUpdateTime());
				runStateMap.put("RS_PRCMSG",rs.getPrcMsg());
				runStateMap.put("RS_PRCCD",rs.getPrcCd());
				runStateMap.put("RS_BANKCODE",rs.getBankCode());
				runStateMap.put("RS_PFXPATH",rs.getPfxPath());
				runStateMap.put("RS_PFXPASSWORD",rs.getPfxPassword());
				runStateMap.put("RS_CRLPATH",rs.getCrlPath());
				runStateMap.put("RS_STTLMONLINEMRK",rs.getSttlmOnlineMrk());
				runStateMap.put("RS_STTLMONLINERMRK",rs.getSttlmOnlineRmrk());
				runStateMap.put("RS_HVPSNXTSYSDT",DateUtils.toString(rs.getHvpsNxtSysDt(),DateUtils.ORA_DATES_FORMAT));
				runStateMap.put("RS_NPCCODE",rs.getNpcCode());
				runStateMap.put("RS_PFXFILENAME",rs.getPfxFileName());
				runStateMap.put("RS_UNIFYKEY",rs.getUnifyKey());
				runStateMap.put("RS_NPCSTATUS",rs.getNpcStatus());
				runStateMap.put("RS_CCPCCODE",rs.getCcpcCode());
				runStateMap.put("RS_CCPCSTATUS",rs.getCcpcStatus());
				runStateMap.put("RS_SYSPARTNER",rs.getSysPartner());
				runStateMap.put("RS_OPERSWITCH",rs.getOperSwitch());
				runStateMap.put("RS_SWITCHTYPE",rs.getSwithType());
				runStateMap.put("CD_REGION",rs.getRegion());
				runStateMap.put("CD_BANKCODETYPE",rs.getBankcodetype());
				runStateMap.put("WORK_DATE",DateUtils.toString(rs.getWorkDate(),DateUtils.ORA_DATES_FORMAT));
				runStateMap.put("SYSTEM_SWITCH",rs.getSystemSwitch());
				redisrCache.hmset("runState",runStateMap);
			}
			
		}else{
			runstateCacheMap.clear();
			this.initCache();
		}
	}
	
	/**
	* 清理缓存
	* @return void
	*/
	public void clearAllCache(){
		runstateCacheMap.clear();
	}
  
	  
	/**
	  * 重新加载单笔缓存
	  * @param dataMap 数据集合 key=code value=系统参数配置CODE
	  * @return void 
	  */
	public void reloadSignleCache(Map dataMap){
		
	}
	
	/**
	 * <p>方法名称: getRunstateCache|描述: 获取系统运行状态缓存</p>
	 * @param getRunstateCache
	 * @return
	 */
	public static RunState getRunstateCache(){
		if(startRedis == true){ 
			RedisUtils redisrCache = (RedisUtils)SpringContextUtil.getBean("redisrCache");
			Map runStateMap = (Map) redisrCache.hmget("runState");
			RunState rs = new RunState();
			if(runStateMap == null){
				logger.info("-从redis中获取缓存为空，调整为从DB中重启读取...........  ");
				RunStateService runStateService = (RunStateService)SpringContextUtil.getBean("runStateService");
				rs = runStateService.getSysRunStateFromDb();
			}else{
				rs.setId((String)runStateMap.get("ID"));//主键ID
				rs.setBankName((String)runStateMap.get("RS_BANKNAME"));//接入商行名称
				rs.setBankCode((String)runStateMap.get("RS_BANKCODE"));//接入商行行号
				rs.setApId((String)runStateMap.get("RS_APID"));//接入点ID
				rs.setApName((String)runStateMap.get("RS_APNAME")); //接入点名称
				rs.setNpcCode((String)runStateMap.get("RS_NPCCODE"));//接入NPC号码
				rs.setLogonState((String)runStateMap.get("RS_LOGONSTATE")); //登录状态（正在登录/已登录/正在退出/已退出，正常按上述顺序循环切换状态，除强制退出）
				rs.setOldPwd((String)runStateMap.get("RS_OLDPWD"));//原识别信息
				rs.setNewPwd((String)runStateMap.get("RS_NEWPWD"));//新识别信息
				rs.setOrgnlSysDt(DateUtils.StringToDate((String)runStateMap.get("RS_ORGNLSYSDT"),DateUtils.ORA_DATES_FORMAT));//原系统日期
				rs.setOrgnlSysSts((String)runStateMap.get("RS_ORGNLSYSSTS"));//原系统状态
				rs.setCurDate(DateUtils.StringToDate((String)runStateMap.get("RS_CURDATE"),DateUtils.ORA_DATES_FORMAT));//当前系统日期
				rs.setSysState((String)runStateMap.get("RS_SYSSTATE"));//当前系统状态
				rs.setNextDate(DateUtils.StringToDate((String)runStateMap.get("RS_NEXTDATE"),DateUtils.ORA_DATES_FORMAT));//下一系统工作日期
				rs.setBizRefTm((String)runStateMap.get("RS_BIZREFTM"));//营业参考时间
				rs.setRmrk((String)runStateMap.get("RS_RMRK"));//附言
				//rs.setCrlLastUpdateTime((String)runStateMap.get("RS_CRLLASTUPDATETIME"));//证书更新时间
				rs.setPrcCd((String)runStateMap.get("RS_PRCCD"));//登录处理结果码
				rs.setPrcMsg((String)runStateMap.get("RS_PRCMSG"));//登录处理内容结果
				rs.setPfxPath((String)runStateMap.get("RS_PFXPATH"));//私钥路径
				rs.setPfxFileName((String)runStateMap.get("RS_PFXFILENAME"));//私钥文件名称 
				rs.setUnifyKey((String)runStateMap.get("RS_UNIFYKEY"));//是否使用统一证书
				rs.setPfxPassword((String)runStateMap.get("RS_PFXPASSWORD"));//私钥密码
				rs.setCrlPath((String)runStateMap.get("RS_CRLPATH"));//CRL吊销列表路径
				rs.setSttlmOnlineMrk((String)runStateMap.get("RS_STTLMONLINEMRK"));//线上清算标识
				rs.setSttlmOnlineRmrk((String)runStateMap.get("RS_STTLMONLINERMRK"));//线上清算附言
				//rs.setHvpsNxtSysDt(DateUtils.StringToDate((String)runStateMap.get("RS_HVPSNXTSYSDT"),DateUtils.ORA_DATES_FORMAT));//大额下一系统工作日    
				rs.setNpcStatus((String)runStateMap.get("RS_NPCSTATUS"));// NPC 状态
				rs.setCcpcCode((String)runStateMap.get("RS_CCPCCODE"));//ccpc码
				rs.setCcpcStatus((String)runStateMap.get("RS_CCPCSTATUS"));//ccpc状态
				rs.setSysPartner((String)runStateMap.get("RS_SYSPARTNER"));// 系统参与者类别
				rs.setOperSwitch((String)runStateMap.get("RS_OPERSWITCH"));//营业前准备批处理开关SO01：开 SO02 关
				rs.setSwithType((String)runStateMap.get("RS_SWITCHTYPE"));//开关种类ST01：营业前准备批处理开关
				rs.setRegion((String)runStateMap.get("CD_REGION"));//省别区域代码
				rs.setBankcodetype((String)runStateMap.get("CD_BANKCODETYPE"));//银行机构代码
				rs.setWorkDate(DateUtils.StringToDate((String)runStateMap.get("WORK_DATE"),DateUtils.ORA_DATES_FORMAT));//201612 增加系统工作日概念
				rs.setSystemSwitch((String)runStateMap.get("SYSTEM_SWITCH"));//系统状态概念    1正常营业  0日终状态 ，不可以做业务；
			}
			return rs;
		}else{
			RunState rs = runstateCacheMap.containsKey("RunState")?(RunState)runstateCacheMap.get("RunState"):null;
			return rs;
		}
		
	}
	
	public Class getEntityClass(){
		return null;
	}

	public String getEntityName(){
		return null;
	}
	
	public void setStartRedis(boolean startRedis) {
		this.startRedis = startRedis;
	}

	public boolean isStartRedis() {
		return startRedis;
	}
}
