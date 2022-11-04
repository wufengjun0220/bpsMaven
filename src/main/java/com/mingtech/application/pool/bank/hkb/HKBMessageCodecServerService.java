/**
 * 
 */
package com.mingtech.application.pool.bank.hkb;

import com.mingtech.application.pool.bank.codec.MessageCodecServerService;

/**
 * @author xukai
 * 
 *         北京中关村分行服务端编码解码服务
 * 
 */
public interface HKBMessageCodecServerService extends MessageCodecServerService {

	/**
	 * 解码消息
	 * @param code 报文交易码
	 * @param message 报文体
	 */
	public ReturnMessageNew decodeNetBankMessage(String code, String message) throws Exception;
	
	/**
	 * 解析报文文件
	 * @param code 报文交易码
	 * @param fileName 报文文件
	 */
	public ReturnMessageNew decodeMessageFile(String code, String splictCode, String fileName) throws Exception;

	/**
	 * 编码消息
	 * @param code 报文交易码
	 * @pram request 请求对象
	 */
	public byte[] encodeNetBankMessage(String code,ReturnMessageNew request) throws Exception;
	
	/**
	 * 解码消息
	 * @param code 报文交易码
	 * @param message 报文体
	 */
	public ReturnMessageNew decodeCreditMessage(String code, String message) throws Exception;

	/**
	 * 编码消息
	 * @param code 报文交易码
	 * @pram request 请求对象
	 */
	public byte[] encodeCreditMessage(String code, ReturnMessageNew request) throws Exception;
	
	/**
	 * 编码报文文件
	 * @param code 报文交易码
	 * @pram request 请求对象
	 */
	public String encodeMessageFile(String code, ReturnMessageNew request) throws Exception;

	/**
	 * 编码报文文件   客户端
	 * @param code 报文交易码
	 * @pram request 请求对象
	 */
	public String encodeMessageFileClien(String code, ReturnMessageNew request) throws Exception;
	
}
