package com.mingtech.application.pool.bank.creditsys;

import com.mingtech.application.pool.bank.message.ReturnMessage;


/**
 * Copyright (C) 北京中软融鑫计算机系统与工程有限公司.
 * 
 * @author YeCheng 
 * 
 * @描述：接收网银请求处理接口定义
 */
public interface ICreditHandler {

	/**
	 * <p>
	 * 方法名称: txHandleRequest|描述:
	 * </p>
	 * 
	 * @param code
	 *            交易码
	 * @param request
	 *            ReturnMessage对象的getHead()方法返回的Map描述主报文，其值都是经过类型转换后的对象，getDetails()方法返回的List的每一个元素是一个Map，第一个Map描述一条明细信息，其值都是经过类型转换后的对象。
	 * @return ReturnMessage对象的getHead()方法返回的Map描述主报文，其值都是经过类型转换后的对象，getDetails()方法返回的List的每一个元素可能是一个业务对象或者一个Map。
	 */
	public ReturnMessage txHandleRequest(String code, ReturnMessage request)
			throws Exception;

}
