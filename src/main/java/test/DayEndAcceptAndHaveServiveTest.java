package test;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mingtech.framework.common.util.SpringContextUtil;

public class DayEndAcceptAndHaveServiveTest extends TestCase {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(DayEndAcceptAndHaveServiveTest.class);

	private static SpringContextUtil springContextUtil = new SpringContextUtil();

	protected void setUp() throws Exception {
		springContextUtil
				.setApplicationContext(new ClassPathXmlApplicationContext(new String[] {
						"classpath*:module/spring-service-quartz.xml",
						"classpath:module/spring-service-msgnotify.xml"}));
	}

	public void testdayEndAcceptAndHaveService() {}

	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}

	public static void main(String[] args) {
		logger.info("aa:" + 10 % 2);
		logger.info("result:" + (11 % 2 == 0 ? 10 / 2 : 10 / 2 + 1));
	}

}
