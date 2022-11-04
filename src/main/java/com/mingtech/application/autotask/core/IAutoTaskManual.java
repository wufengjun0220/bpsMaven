package com.mingtech.application.autotask.core;
/**
 * 手工执行任务借口类
 * @author wbduanyonggang
 *
 */
public interface IAutoTaskManual {
	/**
	 * 手工执行任务 入口
	 * @param taskId
	 */
	public void executeTaskForManual(String taskId)throws Exception;
}
