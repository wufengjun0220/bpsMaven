/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 */
package com.mingtech.application.pool.bank.hkb;

import com.mingtech.application.pool.bank.codec.MessageCodecFactory;

/**
 * @author xukai
 * 
 *         北京中关村银行编解码工厂
 * 
 */
public class HKBMessageCodecFactory extends MessageCodecFactory {

	// 电票交易编码客户端
	private static HKBMessageCodecClientService ecdsClient = null;
	// 信贷交易编码服务端
	private static HKBMessageCodecServerService creditServer = null;
	// 核心交易编码客户端
	private static HKBMessageCodecClientService coreClient = null;
	// 网银交易编码服务端
	private static HKBMessageCodecServerService netBankServer = null;

	public static HKBMessageCodecClientService createECDSlient()
			throws Exception {
		if (ecdsClient == null)
			ecdsClient = new HKBMessageCodecClientServiceImpl();
		return ecdsClient;
	}

	public static HKBMessageCodecServerService createCreditServer()
			throws Exception {
		if (creditServer == null)
			creditServer = new HKBMessageCodecServerServiceImpl();
		return creditServer;
	}

	public static HKBMessageCodecClientService createCoreClient()
			throws Exception {
		if (coreClient == null)
			coreClient = new HKBMessageCodecClientServiceImpl();
		return coreClient;
	}

	public static HKBMessageCodecServerService createNetBankServer()
			throws Exception {
		if (netBankServer == null)
			netBankServer = new HKBMessageCodecServerServiceImpl();
		return netBankServer;
	}
}
