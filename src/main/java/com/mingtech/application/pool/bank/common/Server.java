package com.mingtech.application.pool.bank.common;

import com.mingtech.application.pool.bank.codec.MessageCodecServerService;

/**
* @描述: [Server]服务器接口，具体实际的服务器需要实现本接口
 */
public interface Server {
	
	/**
	* <p>方法名称: start|描述: 启动服务器</p>
	* @throws Exception
	 */
	public void start() throws Exception;
	
	/**
	* <p>方法名称: stop|描述: 关闭服务器</p>
	* @throws Exception
	 */
	public void stop() throws Exception;
	
	/**
	* <p>方法名称: messageReceived|描述: 接收报文并处理报文</p>
	* @param message
	* @throws Exception
	 */
	public String messageReceived(String message) throws Exception;
	
	/**
	* <p>方法名称: setMessageCodecServerService|描述: 设置服务器的编解码服务类</p>
	* @param messageCodecServerService
	 */
	public void setMessageCodecServerService(MessageCodecServerService messageCodecServerService);
	
	/**
	* <p>方法名称: setMessageCodecServerService|描述: 获得服务器的编解码服务类</p>
	* @return
	 */
	public MessageCodecServerService getMessageCodecServerService();
	
	/**
	* <p>方法名称: setServerName|描述: 设置服务器名称</p>
	* @param serverName
	 */
	public void setServerName(String serverName);
	
	/** 
	* <p>方法名称: getServerName|描述: 获得服务器名称</p>
	* @return
	 */
	public String getServerName();


}
