package com.mingtech.application.pool.bank.message;

import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.UUID;

public class FileName {

	/**
	 * 
	 * <p>
	 * 方法名称: getFileName|描述:汉口银行：通过交易码及时间和UUID得到文件名称
	 * </p>
	 * 
	 * @param code
	 * @return fileName
	 */
	public static String getFileNameClient(String code) {
		return "/BPS/CF" + code + DateUtils.getTimeStamp();
		//return "CF" + code + DateUtils.getTimeStamp() + UUID.randomUUID();
	}

	/**
	 * @return fileName
	 */
	public static String getFileNameServer(String code) {
		return "/BPS/SF" + code + DateUtils.getTimeStamp();
		//return "SF" + code + DateUtils.getTimeStamp() + UUID.randomUUID();
	}

	/**
	 * <p>
	 * 方法名称: main|描述:
	 * </p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
	}
}
