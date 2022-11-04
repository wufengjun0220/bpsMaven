package com.mingtech.application.pool.bank.common;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;




/**
* @描述: [AbstractRequestHandler]后台处理的基础类
 */
public abstract class AbstractRequestHandler implements RequestHandler{

	@Override
	public ReturnMessageNew txHandleRequest(String code,
			ReturnMessageNew request) throws Exception {
		return null;
	}
	

}
