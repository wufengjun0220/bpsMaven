package com.mingtech.application.pool.bank.bbsp.client;

import com.mingtech.application.pool.bank.hkb.DefaultESBClient;

/**
 * 
 * @author Orange
 *
 * @copyright 北京明润华创科技有限责任公司 
 *
 * @description 
 *
 */
public class DefaultEcdsClient extends DefaultESBClient implements EcdsClient {

	public DefaultEcdsClient() {
		super();
	}

	public DefaultEcdsClient(String ip, int port, int timeout) {
		super(ip, port, timeout);
	}

	public DefaultEcdsClient(String ip, int port) {
		super(ip, port);
	}

}
