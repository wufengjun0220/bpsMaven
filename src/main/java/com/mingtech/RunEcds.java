package com.mingtech;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mingtech.framework.common.util.SpringContextUtil;

public class RunEcds {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(RunEcds.class);
	
	private static SpringContextUtil springContextUtil=new SpringContextUtil();
	public static void main(String[] args) {
		logger.info("ecds独立启动开始 - start");
		try {
			springContextUtil.setApplicationContext(new ClassPathXmlApplicationContext("classpath*:module/spring-service-quartz.xml"));
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		logger.info("ecds独立启动成功 - end");
	}
	public void destroy() {
	}
}
