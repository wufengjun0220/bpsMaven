package com.mingtech.application.pool.bank.coresys;

import org.springframework.stereotype.Service;

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
@Service
public class DefaultCoreClient extends DefaultESBClient implements CoreClient {

	public DefaultCoreClient() {
		super();
	}

	public DefaultCoreClient(String ip, int port, int timeout) {
		super(ip, port, timeout);
	}

	public DefaultCoreClient(String ip, int port) {
		super(ip, port);
	}

	
}
