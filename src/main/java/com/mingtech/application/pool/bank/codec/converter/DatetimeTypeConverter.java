package com.mingtech.application.pool.bank.codec.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mingtech.application.pool.bank.codec.config.MessageElement;
import com.mingtech.framework.common.util.DateUtils;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: gaofubing
 * @日期: Jul 13, 2009 8:09:09 PM
 * @描述: [DatetimeTypeConverter]请在此简要描述类的功能
 */
public class DatetimeTypeConverter extends AbstractTypeConverter{
	

	/* （非 Javadoc）
	* <p>重写方法: toString|描述: </p>
	* @param value
	* @param template
	* @return
	* @see com.mingtech.application.pool.bank.codec.TypeConverter#toString(java.lang.Object, com.mingtech.application.pool.bank.codec.config.MessageElement)
	*/
	public String toString(Object value, MessageElement element) {
		// 获取字段值
		Object fieldValue = getFieldValue(value, element);

		// 类型转换
		String sText = "";
		if (fieldValue != null && !"".equals(fieldValue.toString())) {
			SimpleDateFormat formatter = new SimpleDateFormat(element.getFormat());
			try {
				// sText = formatter.format(formatter.parse(fieldValue.toString()));  
				// YeCheng 解析日期格式会减几个月
				sText = formatter.format(DateUtils.parseDatStr2Date(fieldValue.toString(), DateUtils.ORA_DATES_FORMAT));
			} catch (Exception e) {
				throw new RuntimeException("解析日期字段失败！[value=]" + fieldValue.toString() + "]");
			}

		}
		return sText;
		// return rightPad(sText,element.getLength(),' ');
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
		String sText = value.trim();
		if(sText.length() == 0)
			return null;
		SimpleDateFormat formatter = new SimpleDateFormat(element.getFormat());
		try{
			Date temp = formatter.parse(sText);
			return temp;
		}catch (ParseException e){
			throw new RuntimeException("解析日期字段失败![value=" + value + ",format=" + element.getFormat() + "]");
		}
	}
}
