package com.mingtech.application.pool.bank.codec;

import com.mingtech.application.pool.bank.message.ReturnMessage;



/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: gaofubing
 * @日期: Jul 13, 2009 11:07:32 AM
 * @描述: [MessageCodecServerService]请在此简要描述类的功能
 */
public interface MessageCodecServerService{
	
	/**
	 * 编码消息
	 * @param code 交易码
	 * @pram request 请求对象
	 * @param message 报文体，不包括10位报文头
	 */
	public String encodeMessage(String code,ReturnMessage request) throws Exception;
	
	/**
	 * 解码消息
	 * @param code 交易码
	 * @param message 报文体，去除10位报文头
	 */
	public ReturnMessage decodeMessage(String code,String message) throws Exception;
	
	/**
	 * 编码消息
	 * @param code 交易码
	 * @pram request 请求对象
	 * @param message 报文体，不包括8位报文头
	 */
	public String encodeNewMessage(String code,ReturnMessage request) throws Exception;
	
	/**
	 * 解码消息
	 * @param code 交易码
	 * @param message 报文体，去除8位报文头
	 */
	public ReturnMessage decodeNewMessage(String code,String message) throws Exception;

}
