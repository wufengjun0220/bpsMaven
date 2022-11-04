/**
 * 
 */
package com.mingtech.application.pool.bank.hkb;


/**
 * @author xukai
 * 
 */
public interface HKBMessageCodecClientService {

	/**
	 * 组装网银报文
	 * @param code 信贷服务码(外部的)
	 * @param request 数据
	 * @return
	 * @throws Exception
	 */
	public byte[] encodeNetBankMessage(String code, ReturnMessageNew request)
			throws Exception;
	
	/** 解析网银报文
	 * @param code 交易码(内部)
	 * @param message 接收到的报文内容JSON格式
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew decodeNetBankMessage(String code, String message)
			throws Exception;
	
	/**
	 * 组装信贷报文
	 * @param code 信贷服务码(外部的)
	 * @param request 数据
	 * @return
	 * @throws Exception
	 */
	public byte[] encodeCreditMessage(String code, ReturnMessageNew request)
			throws Exception;
	
	/** 解析信贷报文
	 * @param code 交易码(内部)
	 * @param message 接收到的报文内容JSON格式
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew decodeCreditMessage(String code, String message)
			throws Exception;
	
	/**
	 * 组装核心报文
	 * @param code 核心服务码(外部的)
	 * @param request 数据
	 * @return
	 * @throws Exception
	 */
	public byte[] encodeCoreMessage(String code, ReturnMessageNew request)
			throws Exception;
	
	/** 解析核心报文
	 * @param code 交易码(内部)
	 * @param message 接收到的报文内容JSON格式
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew decodeCoreMessage(String code, String message)
			throws Exception;
	
	/**
	 * 组装电票系统报文
	 * @param code 核心服务码(外部的)
	 * @param request 数据
	 * @return
	 * @throws Exception
	 */
	public byte[] encodeEcdsMessage(String code, ReturnMessageNew request)
			throws Exception;
	
	/** 解析电票系统报文
	 * @param code 交易码(内部)
	 * @param message 接收到的报文内容JSON格式
	 * @return
	 * @throws Exception
	 */
	public ReturnMessageNew decodeEcdsMessage(String code, String message)
			throws Exception;
	
	/**
	 * 解析报文文件
	 * @param code 报文交易码
	 * @param fileName 报文文件
	 */
	public ReturnMessageNew decodeMessageFile(String code, String splictCode,
			String fileName) throws Exception;
}
