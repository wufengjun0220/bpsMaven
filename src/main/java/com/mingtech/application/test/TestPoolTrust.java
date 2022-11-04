package com.mingtech.application.test;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mingtech.application.pool.draft.domain.DraftPoolInBatch;
import com.mingtech.application.pool.trust.service.DraftPoolTrustInService;

public class TestPoolTrust extends TestCase {

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

	public void testWorkInfo() {
		if (null == application) {
			application = TestPoolTrust.getApplication();
		}

		DraftPoolTrustInService draftPoolTrustInService = (DraftPoolTrustInService) application
				.getBean("draftPoolTrustInService");
		DraftPoolInBatch batch = draftPoolTrustInService.load("297e4bbf62ffa0910162ffb2b7330014");
		System.out.println(batch);
	}

}
