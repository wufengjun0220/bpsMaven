package com.mingtech.application.autotask.taskdispatch;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.SpringContextUtil;

public abstract class BaseAutoTaskDispatch extends  AutoTaskDispatchAbstract{
	private static final Logger logger = Logger.getLogger(BaseAutoTaskDispatch.class);
	/**
	 *<p>自动任务调度执行前调用的事件<p/>
	 *@param reqParam 任务调度请求参数
	 *@param autoTaskExe 任务执行流水
	 *@param taskDispatchCfg 任务调度配置
	 *@return 执行结果级（respCode执行结果编码、 respDesc 执行结果描述。以及其他应答参数，调度会将其他应答参数连同本次请求参数一起放入下一个任务请求参数 reqParams）
	 */
	@Override
	public  Map<String,String> predHandle(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){
		logger.info("自动任务调度通用前置事件开始执行...");
		logger.info(".......Id="+autoTaskExe.getId() +"taskId="+taskDispatchCfg.getId()+"，taskDesc="+taskDispatchCfg.getTaskDesc()+"，busiId="+autoTaskExe.getBusiId());
		//更新开始执行时间
		AutoTaskExeService autoTaskExeService = (AutoTaskExeService)SpringContextUtil.getBean("autoTaskExeService");
		autoTaskExeService.txUpdateAutoTaskExeStartTime(autoTaskExe.getId());
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		logger.info("自动任务调度通用前置事件执行完毕...");
		return resultMap;
	}
	
	/**
	 *<p>自动任务调度执行前调用的事件<p/>
	 *@param reqParam 任务调度请求参数
	 *@param autoTaskExe 任务执行流水
	 *@param taskDispatchCfg 任务调度配置
	 *@return 执行结果级（respCode执行结果编码、 respDesc 执行结果描述。）
	 */
	@Override
	public  Map<String,String> afterHandle(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){
		logger.info("自动任务调度通用后置事件开始执行...");
		logger.info("........Id="+autoTaskExe.getId() +"  taskId="+taskDispatchCfg.getId()+"  taskDesc="+taskDispatchCfg.getTaskDesc()+"  busiId="+autoTaskExe.getBusiId());
		//更新执行结束时间
		AutoTaskExeService autoTaskExeService = (AutoTaskExeService)SpringContextUtil.getBean("autoTaskExeService");
		autoTaskExeService.txUpdateAutoTaskExeEndTime(autoTaskExe.getId());
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		logger.info("自动任务调度通用后置事件执行完毕...");
		return resultMap;
	}
}
