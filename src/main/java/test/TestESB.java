package test;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dc.eai.data.CompositeData;
import com.dcfs.esb.client.converter.XmlStaxParse;
import com.mingtech.application.pool.bank.bbsp.client.DefaultEcdsClient;
import com.mingtech.application.pool.bank.bbsp.client.EcdsClient;
import com.mingtech.application.pool.bank.hkb.HKBMessageUtil;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.trust.service.DraftPoolTrustInService;

public class TestESB extends TestCase {
	
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
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testEcdsSend() throws Exception {
		
		if (null == application) {
			application = TestESB.getApplication();
		}

		EcdsClient ecdsClient = (EcdsClient) application.getBean("ecdsClient");
		
		ReturnMessageNew request = new ReturnMessageNew();
		request.getBody().put("billId", "1");
		request.getBody().put("applicantAcctNo", "2");
		request.getBody().put("receiverName", "3");
		request.getBody().put("receiverAcctNo", "4");
		request.getBody().put("receiverBankNo", "5");
		request.getBody().put("signature", "6");
		request.getBody().put("batchNo", "7");
		request.getBody().put("reserve1", "8");
		request.getBody().put("ifInPool", "9");
		request.getBody().put("billNo", "10");
		//EcdsClient ecdsClient = new DefaultEcdsClient("192.168.251.181",6001);
		ReturnMessageNew response = ecdsClient.processECDS("30600071", request);
		System.out.println(response);
	}

	public void testSend() throws Exception {
		/*Message request=new DefaultMessage();
		//request赋值
		Message response=new DefaultMessage();
		TransUtil.send("1100200000104",request,response);*/
		
		ReturnMessageNew response = new ReturnMessageNew();
		response.getAppHead().put("RET_CODE", "ISS0000");
		response.getAppHead().put("RET_MSG", "成功");
		response.getAppHead().put("RET_STATUS", "S");
		
		
		response.getBody().put("CXLX", "01");
		response.getBody().put("PJHM", "1111112222222");
		//CompositeData cd = HKBMessageUtil.encodeClientMessageNew("PJC001", response);
		//byte[] message = PackUtil.pack(cd);
		//System.out.println(new String(message));
		/*String msg = HKBMessageUtil.encodeClientMessageNew("PJC001", response);
		System.out.println(msg);
		
		//CompositeData cd = null;
		//StandardCdToXml st = new StandardCdToXml();
		//st.convert(cd);
		
		XmlStaxParse parse = new XmlStaxParse();
		CompositeData paCD = new CompositeData();
		parse.parse(msg.getBytes(), paCD);
		System.out.println(paCD);*/
	}
}
