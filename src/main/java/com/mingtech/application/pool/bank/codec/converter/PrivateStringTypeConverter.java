package com.mingtech.application.pool.bank.codec.converter;

import org.apache.log4j.Logger;

import com.mingtech.application.pool.bank.codec.config.MessageElement;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: gaofubing
 * @日期: Jul 13, 2009 8:17:13 PM
 * @描述: [StringTypeConverter]字符串域,解码时不截取空格
 */
public class PrivateStringTypeConverter extends AbstractTypeConverter{
	private static final Logger logger = Logger.getLogger(PrivateStringTypeConverter.class);
	/* （非 Javadoc）
	* <p>重写方法: toString|描述: </p>
	* @param value
	* @param template
	* @return
	* @see com.mingtech.application.pool.bank.codec.TypeConverter#toString(java.lang.Object, com.mingtech.application.pool.bank.codec.config.MessageElement)
	*/
	public String toString(Object value, MessageElement element){
		Object fieldValue = getFieldValue(value, element);
		String sText = fieldValue == null?"":fieldValue.toString();
		return rightPad(sText,element.getLength(),' ');
	}

	/* （非 Javadoc）
	* <p>重写方法: value|描述: </p>
	* @param value
	* @param template
	* @return
	* @see com.mingtech.application.pool.bank.codec.TypeConverter#value(java.lang.String, com.mingtech.application.pool.bank.codec.config.MessageElement)
	*/
	public Object valueOf(String value, MessageElement element){
		//验证域的必须性
		if(!verifyMandatory(value,element))
		{
			String message = element.getDescription() + "域是必须的!";
			logger.error(message);
			throw new RuntimeException(message);
		}
		else if(value == null)
			return null;	
		
		return value;
	}
}
