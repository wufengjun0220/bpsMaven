package com.mingtech.application.pool.bank.common;

import com.mingtech.application.pool.bank.hkb.ReturnMessageNew;
import com.mingtech.application.pool.bank.message.ReturnMessage;

/**
* @描述: [RequestHandler]业务处理接口
 */
public interface RequestHandler {
	
	/**
	* <p>方法名称: txHandleRequest|描述: 处理服务器发过来的请求信息</p>
	* @param code
	* @param request
	* @return
	* @throws Exception
	 */
	public ReturnMessage txHandleRequest(String code,ReturnMessage request) throws Exception;
	
	public ReturnMessageNew txHandleRequest(String code, ReturnMessageNew request)
			throws Exception;

}
