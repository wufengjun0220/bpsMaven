package com.mingtech.application.pool.bank.codec.converter;

import com.mingtech.application.pool.bank.codec.config.MessageElement;




/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: gaofubing
 * @日期: Jul 13, 2009 7:33:12 PM
 * @描述: [TypeConverter]请在此简要描述类的功能
 */
public interface TypeConverter{
	/**
	 * 转换对象类型为字符串
	* <p>方法名称: toString|描述: </p>
	* @param value
	* @param element
	* @return
	 */
	public String toString(Object value,MessageElement element);
	
	/**
	 * 转换字符串为特定类型对象
	* <p>方法名称: value|描述: </p>
	* @param value
	* @param element
	* @return
	 */
	public Object valueOf(String value,MessageElement element);
}
