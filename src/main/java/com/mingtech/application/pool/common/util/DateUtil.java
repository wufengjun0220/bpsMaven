package com.mingtech.application.pool.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.mingtech.application.pool.bank.translog.domain.TransLog;

/**
 * 常用的Date操作类
 * @author maojianwei
 *
 */
public class DateUtil {
	private static final Logger logger = Logger.getLogger(DateUtil.class);

	
	/**
	 * 格式化日期为指定格式
	 * @param date     日期
	 * @param formator 格式，如：yyyy-MM-dd
	 * @return
	 */
	public static String formatDate(Date date,String formator){
		String sdate = null;
		SimpleDateFormat format = new SimpleDateFormat(formator);
		try{
			sdate = format.format(date);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return sdate;
	}

}
