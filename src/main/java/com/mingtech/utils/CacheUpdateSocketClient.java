package com.mingtech.utils;
 
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.net.SocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CacheUpdateSocketClient系统缓存更新socket客户端
 */
public class CacheUpdateSocketClient {
    private static final Log logger = LogFactory.getLog(CacheUpdateSocketClient.class);
    private Socket socket = null;   

   
   public Socket getSocketClient(String ip,int port) {
        try {
            socket= SocketFactory.getDefault().createSocket(ip, port);
        } catch (IOException e) {   
            logger.error("业务处理异常",e);
            socket = null;
        }  
        return socket;
    }   
   
    /**
     * 发送请求并接收应答   
     * @param reqMsg
     * @return
     * @throws IOException
     */
    public String sendMsg(String reqMsg) throws IOException{
        String len = fillCharLeft(reqMsg.getBytes("UTF-8").length+"",8,"0");
        OutputStream out = null;
        InputStream in = null;
        String bodyStr = "";
        logger.info("获取ecds返回结果：" + bodyStr);
		try {
		    out = socket.getOutputStream();;
	    	out.write((len+reqMsg).getBytes("UTF-8"));
	        out.flush();   
	        StringBuffer sb = new StringBuffer();
	        byte[] temp = new byte[1024];
	        int lenTmp = 0;
			StringBuffer strBuf = new StringBuffer("");
	        in = socket.getInputStream();
	        while((lenTmp = in.read(temp)) != -1) {
				byte[] tmp = new byte[lenTmp];
				System.arraycopy(tmp, 0, tmp, 0, lenTmp);
				strBuf.append(tmp);
			}
	        bodyStr = new String(strBuf.toString().getBytes(),"UTF-8");
	        if(bodyStr.length() > 8) {
	        	bodyStr  = bodyStr.substring(8,bodyStr.length()) ;
	        }
	        return bodyStr;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}finally{
			if(out != null){
				out.close();
			}
			if(in != null){
				in.close();
			}
		}
        return bodyStr;
    }
    public static String fillCharLeft(String oldStr,int toLength, String fillChar){
        int length = oldStr.getBytes().length;
        if (length == toLength) {
            return oldStr;
        } else if (length < toLength) {
            for (int i = length; i < toLength; i++) {
                oldStr = fillChar + oldStr;
            }
            return oldStr;
        } else {
            return oldStr.substring(length - toLength);
        }
    }
      
} 