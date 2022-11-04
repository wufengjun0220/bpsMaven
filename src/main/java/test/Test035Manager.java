package test;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test035Manager extends TestCase{

	private static ApplicationContext application = null;

	public static ApplicationContext getApplication(){
		if(application == null){
			application = new ClassPathXmlApplicationContext(new String[] {
					"module/spring-base-info.xml",
					"module/spring-service-info.xml",
					"module/spring-service-msg.xml"});
		}
		return application;
	}

	public void testNotificate(){
		if(null == application){
			application = getApplication();
		}
		/*Msg011 msg = new Msg011();
		Map dataMap = new HashMap();
		dataMap.put(Msg011.CurMsgCode, msg);
		dataMap.put(NotificationManagerService.REQUISITIONID, "1231321");
		NotificationManagerService msgService = (NotificationManagerService) application
				.getBean("notificationManagerService");
		try{
			boolean result = msgService.txExceptionNotification("011", dataMap);
			System.out.println("处理结果:" + result);
		}catch (Exception ex){
			ex.printStackTrace();
		}*/
	}
	
	public void testFlow(){
		if(null == application){
			application = getApplication();
		}
		/*RediscountSaleService rediscountSaleService = (RediscountSaleService)application.getBean("flowNumberServiceImpl");
		RediscountSellDto sellDto = new RediscountSellDto();
		rediscountSaleService.txSaveBussinessFlow(sellDto);*/
	}

}
