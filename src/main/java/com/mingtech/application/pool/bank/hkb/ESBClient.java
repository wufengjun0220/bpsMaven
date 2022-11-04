package com.mingtech.application.pool.bank.hkb;

/**
 * 
 * @author Orange
 *
 * @copyright 北京明润华创科技有限责任公司 
 *
 * @description 
 *
 */
public interface ESBClient {
	/***************************************************************************
	 * 向核心服务器发送请求并获取响应(新接口) <p>方法名称: request|描述: </p>
	 * @param code 交易码
	 * @param request 请求对象
	 * @return
	 */
	public abstract ReturnMessageNew process(String code, ReturnMessageNew request) throws Exception;
	
	/***************************************************************************
	 * 向核心服务器发送请求并获取响应<p>方法名称: request|描述: </p>
	 * @param code 交易码
	 * @param request 请求对象
	 * @return
	 */
	public abstract ReturnMessageNew processCore(String code, ReturnMessageNew request) throws Exception;
	
	/***************************************************************************
	 * 向电票系统发送请求并获取响应 <p>方法名称: request|描述: </p>
	 * @param code 交易码
	 * @param request 请求对象
	 * @return
	 */
	public abstract ReturnMessageNew processECDS(String code, ReturnMessageNew request) throws Exception;
	/***************************************************************************
	 * 向短信服务器发送请求并获取响应<p>方法名称: request|描述: </p>
	 * @param code 交易码
	 * @param request 请求对象
	 * @return
	 */
	public abstract ReturnMessageNew processMSS(String code, ReturnMessageNew request) throws Exception;
	/***************************************************************************
	 * 向lpr系统发送请求并获取响应 <p>方法名称: request|描述: </p>
	 * @param code 交易码
	 * @param request 请求对象
	 * @return
	 */
	public abstract ReturnMessageNew processLPR(String code, ReturnMessageNew request) throws Exception;
}
