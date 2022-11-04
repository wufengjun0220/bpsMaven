package com.mingtech.application.pool.bank.codec.converter;

import com.mingtech.application.pool.bank.codec.config.MessageElement;



/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: gaofubing
 * @日期: Jul 13, 2009 7:36:28 PM
 * @描述: [IntTypeConverter]请在此简要描述类的功能
 */
public class IntTypeConverter extends AbstractTypeConverter{

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
		return sText;
		//return leftPad(sText,element.getLength(),'0');
	}

	/* （非 Javadoc）
	* <p>重写方法: value|描述: </p>
	* @param value
	* @param template
	* @return
	* @see com.mingtech.application.pool.bank.codec.TypeConverter#value(java.lang.String, com.mingtech.application.pool.bank.codec.config.MessageElement)
	*/
	public Object valueOf(String value, MessageElement element){
		if(value == null)
			return null;
		String sText = value.replaceAll("^0+", "");
		if(sText.length() == 0)
			return new Integer(0);
		return Integer.valueOf(sText);
	}

}
