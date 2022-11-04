package com.mingtech.application.autotask.taskdispatch;

import java.util.Map;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;

/**
 * @deprecated自动任务调度
 * @author h2
 * add 2021-04026
 *
 */
public abstract class AutoTaskDispatchAbstract {	
	
	/**
	 *<p>自动任务调度执行前调用的事件<p/>
	 *@param reqParam 任务调度请求参数
	 *@param autoTaskExe 任务执行流水
	 *@param taskDispatchCfg 任务调度配置
	 *@return 执行结果级（respCode执行结果编码、 respDesc 执行结果描述。以及其他应答参数，调度会将其他应答参数连同本次请求参数一起放入下一个任务请求参数 reqParams）
	 */
	public abstract Map<String,String> predHandle(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg);
	/**
	 *<p> 自动任务调度执行<p/>
	 *@param reqParam 任务调度请求参数
	 *@param autoTaskExe 任务执行流水
	 *@param taskDispatchCfg 任务调度配置
	 *@return 执行结果map集（respCode执行结果编码、 respDesc 执行结果描述。以及其他应答参数，调度会将其他应答参数连同本次请求参数一起放入下一个任务请求参数 reqParams）
	 */
	public abstract Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg);
	/**
	 *<p> 自动任务调度执行完成后调用的事件<p/>
	 *@param reqParam 任务调度请求参数
	*@param autoTaskExe 任务执行流水
	 *@param taskDispatchCfg 任务调度配置
	 *@return 执行结果级（respCode执行结果编码、 respDesc 执行结果描述）
	 */
	public abstract Map<String,String> afterHandle(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg);
}
