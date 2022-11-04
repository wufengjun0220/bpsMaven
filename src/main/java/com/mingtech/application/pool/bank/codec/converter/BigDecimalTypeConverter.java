package com.mingtech.application.pool.bank.codec.converter;

import java.math.BigDecimal;

import com.mingtech.application.pool.bank.codec.config.MessageElement;
import com.mingtech.application.pool.bank.message.Constants;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: gaofubing
 * @日期: Jul 13, 2009 7:50:18 PM
 * @描述: [BigDecimalConverter]请在此简要描述类的功能
 */
public class BigDecimalTypeConverter extends AbstractTypeConverter{

	/* （非 Javadoc）
	* <p>重写方法: toString|描述: </p>
	* @param value
	* @param template
	* @return
	* @see com.mingtech.application.pool.bank.codec.TypeConverter#toString(java.lang.Object, com.mingtech.application.pool.bank.codec.config.MessageElement)
	*/
	public String toString(Object value, MessageElement element){
		//获取字段值
		Object fieldValue = getFieldValue(value, element);
		//类型转换
		BigDecimal dFieldValue = null;
		if(fieldValue == null){
			dFieldValue = new BigDecimal(0.0);
		}
		
		if(fieldValue != null)
			dFieldValue = ((BigDecimal)fieldValue).setScale(Integer.parseInt(element.getFormat()), BigDecimal.ROUND_HALF_UP);

		if(!Constants.MAIN_BODY.equals(element.getType())){
			//明细数据
			//String sText = (dFieldValue == null)?"":dFieldValue.toString().replaceAll("\\.", "");
			return dFieldValue.toString();
			//return leftPad(sText,element.getLength(),'0');
		}else
			return dFieldValue.toString();
			//return leftPad(dFieldValue.toString(),element.getLength(),'0');
	}

	/* （非 Javadoc）
	* <p>重写方法: value|描述: </p>
	* @param value
	* @param element
	* @return
	* @see com.mingtech.application.pool.bank.codec.TypeConverter#value(java.lang.String, com.mingtech.application.pool.bank.codec.config.MessageElement)
	*/
	public Object valueOf(String value, MessageElement element){
		if(null == value || "".equals(value))
			return null;		
		String sText = value;
		BigDecimal temp = new BigDecimal(value);
		temp = temp.setScale(2, BigDecimal.ROUND_HALF_UP);
		return temp;
	}
}
