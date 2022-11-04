package com.mingtech.application.pool.bank.netbanksys;

import com.mingtech.application.pool.bank.message.ReturnMessage;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: chenwei
 * @日期: Jul 13, 2009 9:57:59 AM
 * @描述: [INetbankHandler]接收网银请求处理接口定义
 */
public interface INetbankHandler {

	/**
	* <p>方法名称: txHandleRequest|描述: </p>
	* @param code 交易码
	* @param request ReturnMessage对象的getHead()方法返回的Map描述主报文，其值都是经过类型转换后的对象，getDetails()方法返回的List的每一个元素是一个Map，第一个Map描述一条明细信息，其值都是经过类型转换后的对象。
	* @return ReturnMessage对象的getHead()方法返回的Map描述主报文，其值都是经过类型转换后的对象，getDetails()方法返回的List的每一个元素可能是一个业务对象或者一个Map。
	 */
	public ReturnMessage txHandleRequest(String code,ReturnMessage request) throws Exception;

}