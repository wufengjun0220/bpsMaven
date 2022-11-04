package com.mingtech.application.pool.bank.codec;

import com.mingtech.application.pool.bank.message.ReturnMessage;


/**
 * @描述: [MessageCodec]报文编码解码处理接口
 */
public interface MessageCodecClientService {

	/**
	 * 编码消息
	 * 
	 * @param code
	 *            交易码
	 * @pram request 业务请求
	 * @param message
	 *            报文体，不包括10位报文头
	 */
	public String encodeMessage(String code, ReturnMessage request)
			throws Exception;

	/**
	 * 解码消息
	 * 
	 * @param code
	 *            交易码
	 * @param message
	 *            报文体，去除10位报文头
	 */
	public ReturnMessage decodeMessage(String code, String message)
			throws Exception;

	/**
	 * 编码信贷消息
	 * 
	 * @param code
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String encodeCreditMessage(String code, ReturnMessage request)
			throws Exception;
	/**
	 * 解码信贷消息
	 * 
	 * @param code
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public ReturnMessage decodeCreditMessage(String code, String message)
			throws Exception;

	/**
	 * 方法名称: getRequestMsgService|描述: 获得联盟核心报文主题部分
	 * </p>
	 * 2012-12-24 xingyu add 根据配置文件获取部分参数
	 */
	public String getRequestMsgService(String code, ReturnMessage request)
			throws Exception;
	/**
	 *方法名称：getRequestMsgServices|描述：获得二代转账报文主题部分
	 */
	public String getRequestMsgServices(String code, ReturnMessage request)throws Exception;
}
