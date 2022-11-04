package com.mingtech.application.pool.bank.codec.converter;

import java.lang.reflect.Method;
import java.util.Map;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.pool.bank.codec.config.MessageElement;
import com.mingtech.application.pool.bank.message.Constants;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司 </p>
 * @作者: gaofubing
 * @日期: Jul 13, 2009 7:36:50 PM
 * @描述: [AbstractTypeConverter]请在此简要描述类的功能
 */
public abstract class AbstractTypeConverter implements TypeConverter{

	private static final Logger logger = Logger
			.getLogger(AbstractTypeConverter.class);
	
	/**
	 * 解码时验证域的必须性
	* <p>方法名称: verifyMandatory|描述: </p>
	* @param value 消息串
	* @param element 域配置
	* @return 如果验证成功返回true,否则返回false
	 */
	protected boolean verifyMandatory(String value,MessageElement element)
	{
		if(element.getMandatory().equals(Constants.M) && StringUtils.isEmpty(value))
			return false;
		return true;
	}
	
	/**
	 * <p>方法名称: getPropertyValue|描述:
	 * 根据属性名获取对象属性值（拼get方法实现，boolean类型的get方法可能有问题）</p>
	 * @param obj 对象
	 * @param propertyName 属性名
	 * @return Object 属性值
	 */
	protected Object getPropertyValue(Object obj, String fieldName){
		Class clazz = obj.getClass();
		String firstLetter = fieldName.substring(0, 1).toUpperCase();
		String getMethodName = "get" + firstLetter + fieldName.substring(1);
		Method getMethod = null;
		try{
			getMethod = clazz.getMethod(getMethodName, new Class[] {});
			return getMethod.invoke(obj, new Object[] {});
		}catch (Exception e){			
			return null;
		}
	}

	/**
	 * 根据模板从传递过来的对象中解析出字段值 <p>方法名称: getFieldValue|描述: </p>
	 * @param value 根据模板属性不同，传递进来的值有可能是一个业务对象，有可能是一个MAP
	 * @param element
	 * @return
	 */
	protected Object getFieldValue(Object value, MessageElement element){
		Object fieldValue = null;
		if(element.getDataSource().trim().equals(Constants.XML))
			fieldValue = element.getValue();
		else{
			String[] names = element.getName().split(",");
			for(int i = 0; i < names.length; i++){
				if(element.getDataSource().trim().equals(Constants.MAP)){
					fieldValue = ((Map) value).get(names[i]);
						if(fieldValue == null){
							try{
								fieldValue = Ognl.getValue(names[i], value);
							}catch (OgnlException e){
								logger.warn("获取类" + value.getClass().getName() + "的" + names[i] + "属性值失败!");
							}			
						}
				}

				
				else if(element.getDataSource().trim().equals(Constants.BEAN))
					try{
						fieldValue = Ognl.getValue(names[i], value);
					}catch (OgnlException e){
						if(i == names.length -1)
							logger.warn("获取类" + value.getClass().getName() + "的" + names[i] + "属性值失败!");
					}
				if(fieldValue != null)
					break;
			}
		}
		return fieldValue;
	}

	/**
	 * 按照字节对指定字符串进行右补位 <p>方法名称: rightPad|描述: </p>
	 * @param oldStr 需要补位的字符串
	 * @param toLength 总字节长度
	 * @param fillChar 需要补位的字符
	 * @return
	 */
	protected String rightPad(String oldStr, int toLength, char fillChar){
		int length = oldStr.getBytes().length;
		if(length == toLength){
			return oldStr;
		}else if(length < toLength){
			for(int i = length; i < toLength; i++){
				oldStr = oldStr + fillChar;
			}
			return oldStr;
		}else
			throw new RuntimeException("字符串" + oldStr + "长度超出指定长度" + toLength);
	}

	/**
	 * 按照字节对指定字符串进行左补位 <p>方法名称: leftPad|描述: </p>
	 * @param oldStr 需要补位的字符串
	 * @param toLength 总字节长度
	 * @param fillChar 需要补位的字符
	 * @return
	 */
	protected static String leftPad(String oldStr, int toLength, char fillChar){
		int length = oldStr.getBytes().length;
		if(length == toLength){
			return oldStr;
		}else if(length < toLength){
			for(int i = length; i < toLength; i++){
				oldStr = fillChar + oldStr;
			}
			return oldStr;
		}else
			throw new RuntimeException("字符串" + oldStr + "长度超出指定长度" + toLength);
	}
}
