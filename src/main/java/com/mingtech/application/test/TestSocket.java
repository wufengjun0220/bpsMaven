package com.mingtech.application.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import junit.framework.TestCase;
import com.mingtech.framework.common.util.DateUtils;

/**
* <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
* @作者: huangshiqiang
* @日期: Aug 3, 2009 2:23:10 PM
* @描述: [TestMath]测试计算公式相关
*/
public class TestSocket extends TestCase {

	public void testDecimal(){
		
		String ip_addr = "127.0.0.1";
		int port = 6543;
		
		try {
//			Socket socket = new Socket(ip_addr,port);
//			
//			
//			OutputStream out = socket.getOutputStream();
//			InputStream in  = socket.getInputStream();
//			PrintWriter butw = new PrintWriter(out,true);
//			BufferedReader bufr = new BufferedReader(new InputStreamReader(in));
//			butw.println("00000044CIM001&374412e5-7b3a-4aa2-9088-7f416b31bde5");
//			
//			byte[] head = new byte[8];
//	        in.read(head);
//	        String headStr = new String(head);
//	        System.out.println("接收信息  head==================" + headStr + Integer.valueOf(headStr));
//	        byte[] body = new byte[Integer.valueOf(headStr)];
//	        in.read(body);
//	        String bodyStr = new String(body);
//	        
//			System.out.println("====" + body);
//			System.out.println(URLDecoder.decode(bodyStr, "UTF-8"))
			String str = "操作成功";
			byte[] bytes = str.getBytes("UTF-8");
			for (int i = 0; i < bytes.length; i++) {
				
				System.out.println(bytes[i]);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			logger.error(e.getMessage(),e);
		} 
		
		
		
		
		
	}
	
	
	
	
}
