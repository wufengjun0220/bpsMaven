package com.mingtech.framework.common.util;

import java.util.Map;
import org.mvel2.MVEL;

/**
 * <p>mvel language expression util</p>
 * @author Albert Li
 * @date 2015年7月9日
 * @version 1.0
 * <p>修改记录</p>
 * Albert Li   新建类    2015年7月9日
 */
public class MvelUtil {

	public static Object eval(String expr, Map dataMap) throws  Exception{
		Object retValue;
		try {
             retValue = MVEL.eval(expr, dataMap);
			 return retValue;
		} catch (Exception e) {
			throw new Exception("MVEL表达式执行异常"+e.getMessage());
		} 
	}
}
