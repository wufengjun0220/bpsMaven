package com.mingtech.framework.sequence;

import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.SpringContextUtil;
import com.mingtech.framework.common.util.StringUtil;



/**
 * 序列生成器
 * @author h2
 */
public class SequenceUtil {
   private static SequenceUtil sequenceUtil;
	/**
	 * 获取项目配置对象实例
	 * @time:May 11, 2009
	 */
	public static SequenceUtil getInstance() {
		if (null != sequenceUtil) {
			return sequenceUtil;
		} else {
			return new SequenceUtil();
		}
	}
	
	/**
	 *从redis中获取系统公共请求流水号
	 */
	public synchronized String getSysCommonSendReqNo(){
		String seqName = SystemConfigCache.SYS_COMM_SEQNO;
		 //每次获取新连接
		RedisUtils redisrCache = (RedisUtils)SpringContextUtil.getBean("redisrCache");
		int seqNo = redisrCache.increment(seqName);
		//获得序列最大值
		int maxNo = Integer.valueOf(SystemConfigCache.getSystemConfigItemByCode(seqName));		
		//如果超过最大值则删除序列，重新开始
		if(seqNo > maxNo){
			redisrCache.remove(seqName);
			seqNo = redisrCache.increment(seqName);
		}
		//如果位数不够，则左补0
		String curDate = DateUtils.toString(DateUtils.getWorkDayDate(), DateUtils.ORA_DATE_FORMAT);
	    String strSeqNo = StringUtil.formateString(seqNo+"", String.valueOf(maxNo).length());
		return "EDS"+curDate+strSeqNo; 
	}
	
	
	/**
	 *从redis中获取报表生成流水号
	 *@param memberCode 会员编码
	 */
	public synchronized String getReportReqNo(String memberCode){
		String seqName = SystemConfigCache.SYS_COMM_SEQNO;
		 //每次获取新连接
		RedisUtils redisrCache = (RedisUtils)SpringContextUtil.getBean("redisrCache");
		int seqNo = redisrCache.increment(seqName);
		//获得序列最大值
		int maxNo = Integer.valueOf(SystemConfigCache.getSystemConfigItemByCode(seqName));		
		//如果超过最大值则删除序列，重新开始
		if(seqNo > maxNo){
			redisrCache.remove(seqName);
			seqNo = redisrCache.increment(seqName);
		}
		//如果位数不够，则左补0
		String curDate = DateUtils.toString(DateUtils.getWorkDayDate(), DateUtils.ORA_DATE_FORMAT);
	    String strSeqNo = StringUtil.formateString(seqNo+"", String.valueOf(maxNo).length());
		return memberCode+curDate+strSeqNo; 
	}
	
}
