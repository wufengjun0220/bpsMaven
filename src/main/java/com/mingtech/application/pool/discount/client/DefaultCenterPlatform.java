package com.mingtech.application.pool.discount.client;

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
public class DefaultCenterPlatform extends DefaultESBClient implements CenterPlatformClient {

	public DefaultCenterPlatform() {
		super();
	}

	public DefaultCenterPlatform(String ip, int port, int timeout) {
		super(ip, port, timeout);
	}

	public DefaultCenterPlatform(String ip, int port) {
		super(ip, port);
	}

}
