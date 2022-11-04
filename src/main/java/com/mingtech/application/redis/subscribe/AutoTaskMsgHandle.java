package com.mingtech.application.redis.subscribe;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.autotask.service.TaskDispatchConfigService;
import com.mingtech.application.autotask.taskdispatch.AutoTaskDispatchAbstract;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.SpringContextUtil;

/**
 * <p>自动任务redis队列消息控制</p>
 * @author h2
 * @date 2019年12月30日
 * @version 1.0
 * <p>修改记录</p>
 * h2   新建类    2019年12月30日
 */
public class AutoTaskMsgHandle implements Runnable {
	private static final Log logger = LogFactory.getLog(AutoTaskMsgHandle.class);
	private String queueName;// 队列名称
	private Map<String,String> msgDataMap;
    private RedisUtils redisQueueCache;


	/**
	 * @param queueName 队列名称
	 * @param  msgDataMap 从redis队列中读取出来的报文信息
	 */
    public AutoTaskMsgHandle(String queueName, Map<String,String> msgDataMap, RedisUtils redisQueueCache) {
        this.queueName = queueName;
        this.msgDataMap = msgDataMap;
		this.redisQueueCache = redisQueueCache;
    }

    public void run() {
    	String taskRegId =  msgDataMap.get(ConstantFields.VAR_TASK_REG_ID);//自动任务登记表ID
    	logger.info("任务调度逻辑开始执行，taskRegId="+ taskRegId);
    	AutoTaskExeService autoTaskExeService = (AutoTaskExeService)SpringContextUtil.getBean("autoTaskExeService");
    	AutoTaskExe autoTaskExe = autoTaskExeService.getAutoTaskExeById(taskRegId);
		if(autoTaskExe == null) {
			logger.info("未查询到任务执行流行信息，taskRegId+" + taskRegId);
			//特殊情况未查到流水，重新放入队列
			Map<String,String> exeMap = new HashMap<String,String>();
			exeMap.put(ConstantFields.VAR_TASK_REG_ID, taskRegId);
			boolean result = redisQueueCache.lSet(queueName, exeMap);
			return;
		}
		//获取任务配置信息
		TaskDispatchConfigService taskDispatchConfigService = (TaskDispatchConfigService)SpringContextUtil.getBean("taskDispatchConfigService");
		TaskDispatchConfig taskDispatchCfg = taskDispatchConfigService.getTaskDispatchConfigById(autoTaskExe.getTaskId());
		if(!PublicStaticDefineTab.TASK_DISPATCH_STATUS_START.equals(taskDispatchCfg.getStatus())) {
			autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR, ErrorCode.ERR_STATUS+"-调度配置状态-"+taskDispatchCfg.getStatusDesc());
            return;
		}
		
		//判断是否已经到最大处理次数
		if(autoTaskExe.getResetCout() >= taskDispatchCfg.getResetCout()) {
			logger.error("该任务已达重复处理次数上限，任务执行调度ID="+autoTaskExe.getId()+" 原业务ID="+autoTaskExe.getBusiId());
			return;
		}
		//更新重新处理次数
		autoTaskExeService.txUpdateAutoTaskExeResetCount(autoTaskExe.getId());
		
		if(StringUtils.isNotBlank(taskDispatchCfg.getSleepTime())){//延迟处理时间
			logger.info("自动任务调度延迟处理["+taskDispatchCfg.getTaskDesc()+"] taskRegId="+taskRegId+" queueName="+queueName+" busiId="+queueName+ " delayTime="+taskDispatchCfg.getSleepTime()+"毫秒");
			try {
				Thread.sleep(Integer.valueOf(taskDispatchCfg.getSleepTime()).longValue());
			} catch (InterruptedException e1) {
				logger.error(ErrorCode.ERR_MSG_995 ,e1);
			}//线程沉睡30秒
		}
		
		//通过反转生成调度类
		AutoTaskDispatchAbstract  autoTaskDispatch = null;
		try {
			  autoTaskDispatch= (AutoTaskDispatchAbstract)  Class.forName(taskDispatchCfg.getClassName()).newInstance();
		} catch (InstantiationException e) {
			logger.error(ErrorCode.ERR_MSG_994 ,e);
		} catch (IllegalAccessException e) {
			logger.error(ErrorCode.ERR_MSG_994 ,e);
		} catch (ClassNotFoundException e) {
			logger.error(ErrorCode.ERR_MSG_994 ,e);
		}
	    if(autoTaskDispatch == null) {
	    	autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR, ErrorCode.ERR_MSG_994+"-调度类-"+taskDispatchCfg.getClassName());
            return;
	    }
	    //更新任务为执行状态为执行中
	    autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_RUNNING,"");
	 	 /** 更新任务流程中状态 20210602 zjt */
		autoTaskExeService.txUpdateProceStatus(autoTaskExe,PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_RUNNING,"");

	    Map<String,String>reqParamsMap = new HashMap<String,String>();
	    //将json请求参数，转换成MAP
	    if(StringUtils.isNotBlank(autoTaskExe.getReqParams())) {
	    	JSONObject jsonObj = JSON.parseObject(autoTaskExe.getReqParams());
	    	reqParamsMap = JSON.toJavaObject(jsonObj, Map.class);
	    }
	    //调用前置方法
	    Map<String,String> resultMap = autoTaskDispatch.predHandle(reqParamsMap, autoTaskExe, taskDispatchCfg);
		
	    if(!ErrorCode.SUCC_MSG_CODE.equals(resultMap.get(ConstantFields.VAR_RESP_CODE))) {
	    	autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR, ErrorCode.ERR_MSG_998+"-前置事件-"+resultMap.get(ConstantFields.VAR_RESP_DESC));
	    	autoTaskExeService.txUpdateAutoTaskExeErrorCount(autoTaskExe.getId());//更新异常次数
			/** 更新任务流程中状态 20210602 zjt */
			autoTaskExeService.txUpdateProceStatus(autoTaskExe,PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR,"");
	    	return;
	    }
		/** 更新任务流程中状态 20210602 zjt */
		autoTaskExeService.txUpdateProceStatus(autoTaskExe,PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC,"");

	    //将前置事件中返回参数除响应码和响应描述外的参数放入reqParams中
	    resultMap.remove(ConstantFields.VAR_RESP_CODE);
	    resultMap.remove(ConstantFields.VAR_RESP_DESC);
	    reqParamsMap.putAll(resultMap);
	    
	    //业务逻辑调用
	    logger.info("【"+taskDispatchCfg.getTaskDesc()+"】调度任务业务逻辑执行开始，处理的AutoTaskExe表原业务ID为【"+autoTaskExe.getBusiId()+"】");
		Date starTime = new Date();
		
	    resultMap  = autoTaskDispatch.execute(reqParamsMap, autoTaskExe, taskDispatchCfg);
	    
	    Date endTime = new Date();
		long executeTime = (endTime.getTime() - starTime.getTime())/1000;
		logger.info("【"+taskDispatchCfg.getTaskDesc()+"】调度任务业务逻辑执行结束，处理的AutoTaskExe表原业务ID为【"+autoTaskExe.getBusiId()+"】，执行时间为 "+executeTime+"秒");
		
	    
	    if(!ErrorCode.SUCC_MSG_CODE.equals(resultMap.get(ConstantFields.VAR_RESP_CODE))) {
	    	autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR, ErrorCode.ERR_MSG_998+"-业务逻辑调度事件-"+resultMap.get(ConstantFields.VAR_RESP_DESC));
	    	autoTaskExeService.txUpdateAutoTaskExeErrorCount(autoTaskExe.getId());//更新异常次数
			/** 更新任务流程中状态 20210602 zjt */
			autoTaskExeService.txUpdateProceStatus(autoTaskExe,PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR,"");
	    	//业务逻辑调用异常，将报文重新放入redis队列
	    	boolean result = redisQueueCache.lSet(taskDispatchCfg.getQueueName(), msgDataMap);
	    	return;
	    }
		/** 更新任务流程中状态 20210602 zjt */
		autoTaskExeService.txUpdateProceStatus(autoTaskExe,PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC,"");
	    //将业务调度事件中返回参数除响应码和响应描述外的参数放入reqParams中
	    resultMap.remove(ConstantFields.VAR_RESP_CODE);
	    resultMap.remove(ConstantFields.VAR_RESP_DESC);
	    reqParamsMap.putAll(resultMap);
	    
	    //调用后置方法
	    resultMap = autoTaskDispatch.afterHandle(reqParamsMap, autoTaskExe, taskDispatchCfg);
		
	    if(!ErrorCode.SUCC_MSG_CODE.equals(resultMap.get(ConstantFields.VAR_RESP_CODE))) {
	    	//autoTaskExeService.txUpdateAutTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR, ErrorCode.ERR_MSG_998+"-后置事件逻辑处理异常");
	    	logger.info(ErrorCode.ERR_MSG_998+"-后置事件-"+resultMap.get(ConstantFields.VAR_RESP_DESC));
			/** 更新任务流程中状态 20210602 zjt */
			autoTaskExeService.txUpdateProceStatus(autoTaskExe,PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR,"");
            return;
	    }else{
	    	//调用后置方法成功后该任务处理成功
			autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExe.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC, ErrorCode.SUCC_MSG_CH);
			/** 更新任务流程中状态 20210602 zjt */
			autoTaskExeService.txUpdateProceStatus(autoTaskExe,PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_SUCC,"");
	    }
	    //将业务调度事件中返回参数除响应码和响应描述外的参数放入reqParams中
	    resultMap.remove(ConstantFields.VAR_RESP_CODE);
	    resultMap.remove(ConstantFields.VAR_RESP_DESC);
	    reqParamsMap.putAll(resultMap);
	    //查找当前任务下级已生效的自动任务
	    List taskDispatchIds = taskDispatchConfigService.queryEffectTaskDispatchConfigIdsByParentId(autoTaskExe.getTaskId(),PublicStaticDefineTab.TASK_DISPATCH_TYPE_AUTO);
	    if(taskDispatchIds.isEmpty()) {//没有可用的下级子任务
	    	return;
	    }
	    //查询下级待运行自动任务，并对请求参数进行更新
	    List childAutoTaskExes = autoTaskExeService.txUpdateNextAutoTaskExes(autoTaskExe.getBusiId(),taskDispatchIds,JSON.toJSON(reqParamsMap).toString());
	    logger.info("下级待运行自动任务的状态和请求参数更新完毕,已更新的任务数="+childAutoTaskExes.size());
	    //将自动任务放入对应的redis队列中
	    int count = childAutoTaskExes.size();
		for(int i=0; i<count; i++) {
			AutoTaskExe  autoTaskExeTmp = (AutoTaskExe)childAutoTaskExes.get(i);
			//查询对应的任务调度配置-获取队列名称
			TaskDispatchConfig taskDispatchCfgTmp = taskDispatchConfigService.getTaskDispatchConfigById(autoTaskExe.getTaskId());
			if(taskDispatchCfgTmp != null) {
				if(!PublicStaticDefineTab.TASK_DISPATCH_STATUS_START.equals(taskDispatchCfgTmp.getStatus())) {
					autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExeTmp.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR, ErrorCode.ERR_STATUS+"-调度配置状态-"+taskDispatchCfg.getStatusDesc());
		            return;
				}
				//设定队列中报文信息
				Map<String,String> childMsgDataMap = new HashMap<String,String>();
				childMsgDataMap.put(ConstantFields.VAR_TASK_REG_ID, autoTaskExeTmp.getId());
				boolean result = redisQueueCache.lSet(taskDispatchCfgTmp.getQueueName(), childMsgDataMap);
				if(!result) {
					logger.error(ErrorCode.ERR_CALL_REDIS+"-将自动任务执行调度消息放入队列失败。任务ID="+autoTaskExeTmp.getId()+" 业务ID="+autoTaskExeTmp.getBusiId()+" 任务配置ID="+autoTaskExeTmp.getTaskId());
					autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExeTmp.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_ERR, ErrorCode.ERR_CALL_REDIS+"-将自动任务执行流水放入队列失败。");
				}else {
					autoTaskExeService.txUpdateAutoTaskExeStatus(autoTaskExeTmp.getId(), PublicStaticDefineTab.AUTO_TASK_EXE_STATUS_QUEUE, "");
				}
			}else {
				logger.error("未找到下级待运行自动的对应的任务调度配置信息，子任务ID="+autoTaskExeTmp.getId()+" 原业务ID="+autoTaskExeTmp.getBusiId());
			}
			
		}
	    
		
    }


}

