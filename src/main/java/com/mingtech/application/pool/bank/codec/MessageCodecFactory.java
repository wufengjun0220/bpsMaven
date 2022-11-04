package com.mingtech.application.pool.bank.codec;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: chenwei
 * @日期: Jun 16, 2009 11:16:49 AM
 * @描述: [MessageCodec]报文编码解码工厂
 */
public class MessageCodecFactory {
	
	private static MessageCodecClientService client = null;
	private static MessageCodecServerService server = null;

	public static MessageCodecClientService createClient() throws Exception{
		//if(client==null)
			//client = new MessageCodecClientServiceImpl();
		return client;
	}
	
	public static MessageCodecServerService createServer() throws Exception{
		if(server==null)
			server = new MessageCodecServerServiceImpl();
		return server;
	}

}
