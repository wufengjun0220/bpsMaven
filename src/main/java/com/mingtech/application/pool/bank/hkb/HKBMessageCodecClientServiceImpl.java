/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 */
package com.mingtech.application.pool.bank.hkb;

import org.apache.log4j.Logger;

/**
 * @author xukai
 * 
 *         北京中关村银行客户端编解码实现
 */
public class HKBMessageCodecClientServiceImpl implements
		HKBMessageCodecClientService {
	private static final Logger logger = Logger
			.getLogger(HKBMessageCodecClientServiceImpl.class);

	/* (non-Javadoc)
	 * @see com.mingtech.application.pool.bank.hkb.HKBMessageCodecClientService#encodeNetBankMessage(java.lang.String, com.mingtech.application.pool.bank.hkb.ReturnMessageNew)
	 */
	public byte[] encodeNetBankMessage(String code, ReturnMessageNew request)
			throws Exception {
		byte[] message = HKBMessageUtil.encodeClientMessageNew(code, request);
		logger.debug(code + " encode:\n" + message);
		return message;// 只返回报文体
	}

	/* (non-Javadoc)
	 * @see com.mingtech.application.pool.bank.hkb.HKBMessageCodecClientService#decodeNetBankMessage(java.lang.String, java.lang.String)
	 */
	public ReturnMessageNew decodeNetBankMessage(String code, String message)
			throws Exception {
		logger.debug(code + " decode:\n" + message);
		ReturnMessageNew returnMessage = HKBMessageUtil.decodeClientMessageNew(
				code, message);
		return returnMessage;
	}

	/* (non-Javadoc)
	 * @see com.mingtech.application.pool.bank.hkb.HKBMessageCodecClientService#encodeCreditMessage(java.lang.String, com.mingtech.application.pool.bank.hkb.ReturnMessageNew)
	 */
	public byte[] encodeCreditMessage(String code, ReturnMessageNew request)
			throws Exception {
		byte[] message = HKBMessageUtil.encodeClientMessageNew(code, request);
		logger.debug(code + " encode:\n" + message);
		return message;// 只返回报文体
	}

	/* (non-Javadoc)
	 * @see com.mingtech.application.pool.bank.hkb.HKBMessageCodecClientService#decodeCreditMessage(java.lang.String, java.lang.String)
	 */
	public ReturnMessageNew decodeCreditMessage(String code, String message)
			throws Exception {
		logger.debug(code + " decode:\n" + message);
		ReturnMessageNew returnMessage = HKBMessageUtil.decodeClientMessageNew(
				code, message);
		return returnMessage;
	}

	/* (non-Javadoc)
	 * @see com.mingtech.application.pool.bank.hkb.HKBMessageCodecClientService#encodeCoreMessage(java.lang.String, com.mingtech.application.pool.bank.hkb.ReturnMessageNew)
	 */
	public byte[] encodeCoreMessage(String code, ReturnMessageNew request)
			throws Exception {
		byte[] message = HKBMessageUtil.encodeClientMessageNew(code, request);
		logger.debug(code + " encode:\n" + message);
		return message;// 只返回报文体
	}

	/* (non-Javadoc)
	 * @see com.mingtech.application.pool.bank.hkb.HKBMessageCodecClientService#decodeCoreMessage(java.lang.String, java.lang.String)
	 */
	public ReturnMessageNew decodeCoreMessage(String code, String message)
			throws Exception {
		logger.debug(code + " decode:\n" + message);
		ReturnMessageNew returnMessage = HKBMessageUtil.decodeClientMessageNew(
				code, message);
		return returnMessage;
	}

	/* (non-Javadoc)
	 * @see com.mingtech.application.pool.bank.hkb.HKBMessageCodecClientService#encodeEcdsMessage(java.lang.String, com.mingtech.application.pool.bank.hkb.ReturnMessageNew)
	 */
	public byte[] encodeEcdsMessage(String code, ReturnMessageNew request)
			throws Exception {
		byte[] message = HKBMessageUtil.encodeClientMessageNew(code, request);
		logger.debug(code + " encode:\n" + message);
		return message;
	}

	/* (non-Javadoc)
	 * @see com.mingtech.application.pool.bank.hkb.HKBMessageCodecClientService#decodeEcdsMessage(java.lang.String, java.lang.String)
	 */
	public ReturnMessageNew decodeEcdsMessage(String code, String message)
			throws Exception {
		logger.debug(code + " decode:\n" + message);
		ReturnMessageNew returnMessage = HKBMessageUtil.decodeClientMessageNew(
				code, message);
		return returnMessage;
	}

	@Override
	public ReturnMessageNew decodeMessageFile(String code, String splictCode, String fileName)
			throws Exception {
		return HKBMessageUtil.decodeMessageFile(code, splictCode, fileName, true);
	}
}
