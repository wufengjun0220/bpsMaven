package test;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestWorkInfo extends TestCase{
	
	private static ApplicationContext application = null;
	public static ApplicationContext getApplication() {
		if (application == null) {
			application = new ClassPathXmlApplicationContext(new String[] { 
					"classpath:module/spring-base-info.xml",
					"classpath:module/spring-service-info.xml", 
					"classpath:module/spring-service-msg.xml" });
		}
		return application;
	}
	
	public void testWorkInfo(){
		if(null == application){
			application = TestWorkInfo.getApplication();
		}
		
//		WorkInfoService workInfoService = (WorkInfoService) application.getBean("workInfoService");
//		WorkInfo wi = new WorkInfo();
//		wi.setTitle("测试信息信息");
//		wi.setIdNb("2222222222222");
//		wi.setIsseAmt(new BigDecimal(2130.99));
//		wi.setTradeTypeName("承兑");
//		wi.setTradeTypeCode("002");
//		wi.setCurTradeName("承兑签收");
//		wi.setSummary("测试承兑签收");
//		wi.setProcessId("test_process");
//		wi.setCaseId(77704);
//		wi.setUpdateTime(new Date());
//		wi.setDistinctId("abc1");
//		wi.setStatus(PublicStaticDefineTab.ACTIVE);
//		List temp = new ArrayList();
//		WorkInfoReader wif1 = new WorkInfoReader();
//		wif1.setUserId("8a2826e01eba6799011eba6d75e60001");
//		wif1.setUserName("root");
//		wif1.setDeptId("1");
//		wif1.setDeptName("北京银行");
//		wif1.setReadType(PublicStaticDefineTab.ALL);
//		wif1.setWorkInfo(wi);
//		temp.add(wif1);
//		wi.setReaderList(temp);
//		workInfoService.txSaveWorkInfo(wi,wi.getCaseId());
//		workInfoService.txSaveWorkInfo(wi);
//		workInfoService.txSaveWorkInfo(wi,null,"00817a6c23c1ad2a0123c2151f9e0058");
	}
	

}
