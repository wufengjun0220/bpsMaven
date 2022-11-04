package com.mingtech.framework.common.util;

import java.lang.reflect.Method;
import org.apache.log4j.Logger;
import ognl.Ognl;

/**
 * 处理javabean的工具类
 * @author Administrator
 *
 */
public class BeanUtil {
	
	private static final Logger logger = Logger.getLogger(BeanUtil.class);
	/**
	 * 完成两个类的值对拷
	 * @param source
	 * @param target
	 */
	public static void copyValue(Object source,Object target) throws Exception{
		
		Method ms[] = source.getClass().getMethods();
		
		for(int i = 0;i<ms.length;i++){
			Method m = ms[i];
			String mn = m.getName();
			String propertyName = "";
			
			if(mn.startsWith("set")){
				propertyName = mn.substring(3);
				
				try{
					Object value = Ognl.getValue(propertyName, source);
					Ognl.setValue(propertyName, target, value);
				}catch(Exception e){
					////logger.error(e.getMessage(),e);
            		logger.error(e.getMessage());
					continue;
				}
				
			}
		}
		
	}
	
	/**
	 * 从一个对象中获得数值
	 * @param property
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public static Object getValue(String property,Object bean) throws Exception{
		Object value = Ognl.getValue(property, bean);
		return value;
	}
	
	
}
