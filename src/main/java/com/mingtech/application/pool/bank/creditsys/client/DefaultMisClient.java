package com.mingtech.application.pool.bank.creditsys.client;

import com.mingtech.application.pool.bank.hkb.DefaultESBClient;

/**
 * MIS客户端
 * @author Ju Nana
 * @version v1.0
 * @date 2019-10-30
 */
public class DefaultMisClient extends DefaultESBClient implements MisClient {

	public DefaultMisClient() {
		super();
	}

	public DefaultMisClient(String ip, int port, int timeout) {
		super(ip, port, timeout);
	}

	public DefaultMisClient(String ip, int port) {
		super(ip, port);
	}

}
