package com.mingtech.application.autotask.taskdispatch;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mingtech.application.autotask.domain.AutoTaskExe;
import com.mingtech.application.autotask.domain.TaskDispatchConfig;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.autotask.service.TaskDispatchConfigService;
import com.mingtech.application.pool.draft.domain.PoolQueryBean;
import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;

/**
 *自动任务发布服务-各个业务调用该类进行各业务自动任务发布
 *
 */

@Service("autoTaskPublishService")
public class AutoTaskPublishService {
	private static final Logger logger = Logger.getLogger(AutoTaskPublishService.class);
	
	@Autowired
    private AutoTaskExeService autoTaskExeService;
	@Autowired
	private RedisUtils redisQueueCache;
	@Autowired
    private TaskDispatchConfigService taskDispatchConfigService;
	
	/**
	 *<p>自动任务发布<p/>
	 * @param memberCode 会员编码
	 * @param queueNode 队列分发节点
	 * @param busiId 原业务ID
	 * @param productId业务类型(自定义业务类型，方便页面显示)
	 * @param reqParams 任务调度请求参数
	 * @param batchNo 批次号或票号
	 * @param bpsNo 票据池协议号
	 * @param bpsName 票据池名称
	 * @param depId 操作机构id
	 *@return 执行结果级（respCode执行结果编码、 respDesc 执行结果描述。以及其他应答参数，调度会将其他应答参数连同本次请求参数一起放入下一个任务请求参数 reqParams）
	 */
	public  void publishTask(String memberCode,String queueNode,String busiId,String productId,Map<String,String> reqParams,
							 String batchNo,String bpsNo,String bpsName,String depId){
		try {
			logger.info("任务发布-开始保存自动任务执行调度流水信息,业务ID："+busiId);
			PoolQueryBean bean=new PoolQueryBean();
			bean.setQueueNode(queueNode);
			TaskDispatchConfig  taskDispatchConf = taskDispatchConfigService.queryTaskDispatchConfigByParm(bean);
			AutoTaskExe mainAutoTaskExe = autoTaskExeService.txSaveAutoTaskExe(memberCode, taskDispatchConf.getTaskNo(), busiId, productId, reqParams,batchNo,bpsNo,bpsName,depId);
			//TaskDispatchConfig  taskDispatchConf = taskDispatchConfigService.getTaskDispatchConfigById(mainAutoTaskExe.getTaskId());
			logger.info("任务发布-将主任务执行调度流水信息放入队列进行,业务ID："+busiId);
			//设定队列中报文信息
			Map<String,String> msgDataMap = new HashMap<String,String>();
			msgDataMap.put(ConstantFields.VAR_TASK_REG_ID, mainAutoTaskExe.getId());
			boolean result = redisQueueCache.lSet(taskDispatchConf.getQueueName(), msgDataMap);
			if(!result) {
				logger.info(ErrorCode.ERR_CALL_REDIS+"-将自动任务执行调度消息放入队列失败。任务ID="+mainAutoTaskExe.getId()+" 业务ID="+mainAutoTaskExe.getBusiId()+" 任务配置ID="+mainAutoTaskExe.getTaskId());
			}
			
		}catch(RuntimeException e) {
			logger.info(ErrorCode.ERR_TASK_PUBLISH,e);
		}
		
		
	}
	
	
	/**
	 *<p>唤醒任务发布<p/>
	 * @param memberCode 会员编码
	 * @param queueNode 任务节点
	 * @param busiId 原业务ID
	 * @param productId业务类型(自定义业务类型，方便页面显示)
	 * @param reqParams 任务调度请求参数
	 *@return 
	 */
	public  void publishWaitTask(String memberCode,String queueNode,String busiId,String productId,Map<String,String> reqParams) throws Exception{
		try {
			AutoTaskExe autoTaskExe=autoTaskExeService.txAutoTaskExeInfo(memberCode, queueNode, busiId, productId, reqParams);
			if(autoTaskExe == null){
				logger.info(ErrorCode.ERR_CALL_REDIS+"-将自动任务执行调度消息放入队列失败。需唤醒的任务不存在，业务ID="+busiId);
			}else{
				
				PoolQueryBean query=new PoolQueryBean();
				query.setQueueNode(queueNode);//任务节点
				TaskDispatchConfig  taskDispatchConf = taskDispatchConfigService.queryTaskDispatchConfigByParm(query);
				logger.info("任务发布-将子任务执行调度流水信息放入队列进行+"+busiId);
				//设定队列中报文信息
				Map<String,String> msgDataMap = new HashMap<String,String>();
				msgDataMap.put(ConstantFields.VAR_TASK_REG_ID, autoTaskExe.getId());
				boolean result = redisQueueCache.lSet(taskDispatchConf.getQueueName(), msgDataMap);
				if(!result) {
					logger.info(ErrorCode.ERR_CALL_REDIS+"-将自动任务执行调度消息放入队列失败。任务ID="+autoTaskExe.getId()+" 业务ID="+autoTaskExe.getBusiId()+" 任务配置ID="+autoTaskExe.getTaskId());
				}
			}
			
		}catch(RuntimeException e) {
			logger.info(ErrorCode.ERR_TASK_PUBLISH,e);
			throw new Exception("操作失败！");
		}
    }
	
	/**
	 *<p>唤醒任务发布同时执行query校验内容<p/>
	 * @param memberCode 会员编码
	 * @param queueNode 任务节点
	 * @param busiId 原业务ID
	 * @param productId业务类型(自定义业务类型，方便页面显示)
	 * @param reqParams 任务调度请求参数
	 *@return 
	 */
	public  void publishStartTask(String memberCode,String queueNode,String busiId,String productId,Map<String,String> reqParams) throws Exception{
		try {
			AutoTaskExe autoTaskExe=autoTaskExeService.txAutoTaskExeInfo(memberCode, queueNode, busiId, productId, reqParams);
			autoTaskExeService.txUpdateErrorCount(autoTaskExe);
			PoolQueryBean query=new PoolQueryBean();
			query.setQueueNode(queueNode);//任务节点
			TaskDispatchConfig  taskDispatchConf = taskDispatchConfigService.queryTaskDispatchConfigByParm(query);
			logger.info("任务发布-将子任务执行调度流水信息放入队列进行+"+busiId);
			//设定队列中报文信息
			Map<String,String> msgDataMap = new HashMap<String,String>();
			msgDataMap.put(ConstantFields.VAR_TASK_REG_ID, autoTaskExe.getId());
			boolean result = redisQueueCache.lSet(taskDispatchConf.getQueueName(), msgDataMap);
			if(!result) {
				logger.info(ErrorCode.ERR_CALL_REDIS+"-将自动任务执行调度消息放入队列失败。任务ID="+autoTaskExe.getId()+" 业务ID="+autoTaskExe.getBusiId()+" 任务配置ID="+autoTaskExe.getTaskId());
			}
			
		}catch(RuntimeException e) {
			logger.info(ErrorCode.ERR_TASK_PUBLISH,e);
			throw new Exception("操作失败！");
		}
    }
}
