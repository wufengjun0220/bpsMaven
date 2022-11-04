package test;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mingtech.application.sysmanage.service.ResourceService;
import com.mingtech.application.sysmanage.vo.Tree;

public class TestJsonMenu extends TestCase {

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
			application = TestJsonMenu.getApplication();
		}

		ResourceService resourceService = (ResourceService) application
				.getBean("resourceService");
		Tree tree = new Tree();
		tree.setId("-1");
		String json = resourceService.getResources(tree);
		System.out.println(json);
	}

}
