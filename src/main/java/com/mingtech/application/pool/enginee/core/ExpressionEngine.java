package com.mingtech.application.pool.enginee.core;

/**
 * <p>表达式执行引擎</p>
 * @author Albert Li
 * @date 2017年11月3日
 * @version 1.0
 * <p>修改记录</p>
 * Albert Li   新建类    2017年11月3日
 */
public interface ExpressionEngine {
	/**
	 * 进行预编译以提高性能
	 * @return 
	 * */
	public Object compile(String expr, Context ctx) throws Exception;
	/**
	 * 执行结果
	 * @param ctx 上下文环境
	 * */
	public Object eval(String expr, Context ctx) throws Exception;
}
