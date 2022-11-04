package com.mingtech.application.pool.enginee.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.janino.ExpressionEvaluator;

import com.mingtech.application.pool.enginee.core.Context;
import com.mingtech.application.pool.enginee.core.ExpressionEngine;

/**
 * <p>用janino实现表达式执行引擎</p>
 * @author Albert Li
 * @date 2017年11月3日
 * @version 1.0
 * <p>修改记录</p>
 * Albert Li   新建类    2017年11月3日
 */
public class JaninoExpressionEngine implements ExpressionEngine {
	private static final Log logger = LogFactory.getLog(JaninoExpressionEngine.class);
	
	@Override
	public Object compile(String expr, Context ctx) throws Exception {
		ExpressionEvaluator ee = pool.get(expr);
		if(ee == null) {
			Map<String, Object> params = ctx.toMap();
			String[] paramNames = new String[params.size()];
			Class[] paramTypes = new Class[params.size()];
			int i = 0;
			for(String key : params.keySet()) {
				paramNames[i] = key;
				Object value = params.get(key);
				paramTypes[i] = value.getClass();
				
				i++;
			}
			
			ee = new ExpressionEvaluator();
			ee.setExpressionType(Object.class);
			ee.setParameters(paramNames, paramTypes);
			try {
				ee.cook(expr);
				pool.put(expr, ee);
				return ee;
			} catch(Exception e) {
				logger.error("compile fail.expr:[" + expr + "].error:" + e.getMessage(),e);
				return null;
			}
		} else {
			return ee;
		}
	}
	@Override
	public Object eval(String expr, Context ctx) throws Exception {
		//设置参数类型
		Map<String, Object> params = ctx.toMap();
		Object[] values = new Object[params.size()];
		params.values().toArray(values);
		
		ExpressionEvaluator ee;
		ee = pool.get(expr);
		if(ee == null) {
			ee = (ExpressionEvaluator) compile(expr, ctx);
		}
		//evaluate
		try {
			return ee.evaluate(values);
		} catch(Exception e) {
			throw e;
		}
		
	}

	// IExpressionEvaluator的javadoc中已说明ExpressionEvaluator为线程安全
	private Map<String, ExpressionEvaluator> pool = new java.util.concurrent.ConcurrentHashMap<String, ExpressionEvaluator>();
}
