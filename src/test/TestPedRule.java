package test;

import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mingtech.application.pool.enginee.core.RuleActionResult;
import com.mingtech.application.pool.rule.domain.PedRule;
import com.mingtech.application.pool.rule.service.PedRuleService;

public class TestPedRule extends TestCase {

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

	public void testWorkInfo() throws Exception {
		if (null == application) {
			application = TestPedRule.getApplication();
		}

		PedRuleService pedRuleService = (PedRuleService) application
				.getBean("pedRuleServiceImpl");
		/**
		 * 占用顺序
		 */
		//HashMap<String,Object> map = new HashMap<String,Object>();
		//map.put("para6", true);
		List<PedRule>resultList = pedRuleService.findResult("ZC_00", "RT_05");
		System.out.println(resultList);
	}

}
