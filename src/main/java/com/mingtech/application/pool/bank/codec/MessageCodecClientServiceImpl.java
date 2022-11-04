package com.mingtech.application.pool.bank.codec;

import org.apache.log4j.Logger;

import com.mingtech.application.pool.bank.message.ReturnMessage;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: chenwei
 * @日期: Jun 16, 2009 11:16:49 AM
 * @描述: [MessageCodec]报文编码解码处理接口实现类
 */
public abstract class MessageCodecClientServiceImpl implements MessageCodecClientService {
	
	private static final Logger logger = Logger.getLogger(MessageCodecClientServiceImpl.class);
	
	/**
	 * 解码消息
	 * @param tranCode 交易码
	 * @param message 报文体，不包括10位报文头
	 */
	public ReturnMessage decodeMessage(String code, String message) throws Exception {
		logger.debug(code + " decode:\n" + message.toString());
		return MessageUtil.decodeClientMessage(code, message);
	}

	/**
	 * 编码消息
	 * @param tranCode 交易码
	 * @pram objs 业务对象列表
	 * @param message 报文体，不包括10位报文头
	 */
	public String encodeMessage(String code,ReturnMessage request) throws Exception {
		String message = MessageUtil.encodeClientMessage(code, request);
		logger.debug(code + " encode:\n" + message.toString());
		return message;

	}

}

