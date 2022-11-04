package com.mingtech.application.autotask.taskdispatch;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;

public  class TestAutoTaskDispatch extends  BaseAutoTaskDispatch{
	private static final Logger logger = Logger.getLogger(TestAutoTaskDispatch.class);

	/**
	 *<p>自动任务调度执行前调用的事件<p/>
	 *@param reqParam 任务调度请求参数
	 *@param autoTaskExe 任务执行流水
	 *@param taskDispatchCfg 任务调度配置
	 *@return 执行结果级（respCode执行结果编码、 respDesc 执行结果描述。）
	 */
	@Override
	public  Map<String,String> execute(Map<String,String> reqParams,AutoTaskExe autoTaskExe,TaskDispatchConfig  taskDispatchCfg){
		logger.info("自动任务调度始执行...");
		logger.info(".......Id="+autoTaskExe.getId());
		logger.info(".......taskId="+taskDispatchCfg.getId()+"，taskDesc="+taskDispatchCfg.getTaskDesc());
		logger.info(".......busiId="+autoTaskExe.getBusiId());
		Map<String,String> resultMap = new HashMap<String,String>();
		resultMap.put(ConstantFields.VAR_RESP_CODE, ErrorCode.SUCC_MSG_CODE);
		resultMap.put(ConstantFields.VAR_RESP_DESC, ErrorCode.SUCC_MSG_CH);
		logger.info("自动任务调度执行完毕...");
		return resultMap;
	}
}
