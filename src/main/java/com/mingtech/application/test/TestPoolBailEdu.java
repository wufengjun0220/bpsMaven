package com.mingtech.application.test;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mingtech.application.pool.edu.domain.BailDetail;
import com.mingtech.application.pool.edu.service.PoolBailEduService;

public class TestPoolBailEdu extends TestCase {

	private static ApplicationContext application = null;

	public static ApplicationContext getApplication() {
		if (application == null) {
			application = new ClassPathXmlApplicationContext(new String[] {
					"classpath:pjc/spring-base-info.xml",
					"classpath:pjc/spring-service-info.xml",
					"classpath:pjc/spring-service-poolbankinterface.xml" });
		}
		return application;
	}

	public void testBailEduQuery() throws Exception {
		if (null == application) {
			application = TestPoolBailEdu.getApplication();
		}

		PoolBailEduService poolBailEduService = (PoolBailEduService) application
				.getBean("poolBailEduServiceImpl");
//		BailDetail bd = poolBailEduService.queryBailEduQuery("22");
//		System.out.println(bd);
	}

}
