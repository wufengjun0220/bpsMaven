package com.mingtech.application.pool.bank.lprsys.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.pool.bank.bbsp.client.EcdsClient;
import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.lprsys.service.PoolLprService;
import com.mingtech.application.pool.bank.message.Constants;

/**
 * 
 * @author wss
 *
 * @copyright 北京明润华创科技有限责任公司 
 *
 * @description 票据池交易 lpr系统接口服务实现类
 *
 */
@Service("PoolLprService")
public class PoolLprServiceImpl implements PoolLprService {
	
	@Autowired
	EcdsClient ecdsClient;
	
	public ReturnMessageNew txhandlerLPS001(String string) throws Exception {
		
		ReturnMessageNew request = new ReturnMessageNew();
		ReturnMessageNew response = ecdsClient.processLPR("LPS001", request);
		String responseCode = response.getRet().getRET_CODE();
		if (responseCode.equals(Constants.TX_SUCCESS_CODE)) {
			return response;
		}
		throw new Exception(responseCode);
	}
	

}
