package com.mingtech.application.redis.listener;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mingtech.application.redis.subscribe.AutoTaskMsgSubscribe;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;

/**
 * <p>自动任务Redis队列监听器</p>
 * @author h2
 * @date 2020年01月02日
 * @version 1.0
 * <p>修改记录</p>
 * h2   新建类   2020年01月02日
 */
public class AutoTaskQueueListener {
	private static final Logger logger = Logger.getLogger(AutoTaskQueueListener.class);
	private int poolSize=1;//线程池大小
	private long readTimeout=120;//消息读取超时时间-单位S
	private String queueName="POOL.AUTO.COMM";
	
	private List<AutoTaskMsgSubscribe> msgSubscribeThreadList = new ArrayList<AutoTaskMsgSubscribe>();
	public void start(){
		AutoTaskMsgSubscribe ecdsQutoQueueMonitor = new AutoTaskMsgSubscribe(queueName,readTimeout,poolSize);
		ecdsQutoQueueMonitor.start();
		msgSubscribeThreadList.add(ecdsQutoQueueMonitor);
		logger.info("自动任务Redis队列监听运行开始......");
		logger.info("......队列名称="+queueName);
		logger.info("......线程池大小="+poolSize);
		
	}
	
	
	public void stop() throws Exception{
		logger.info("自动任务Redis队列["+queueName+"]监听开始停止......");
		for(AutoTaskMsgSubscribe msgSubscribe : msgSubscribeThreadList){
			try{
				msgSubscribe.stopSubscribe();//停止消息订阅
				msgSubscribe.getExecutorServicePool().shutdown();//线程池停止
//				while (true){
					logger.info("...队列名称="+msgSubscribe.getQueueName()+ConstantFields.VAR_JOIN_COMMA+ConstantFields.VAR_RUNNING+ConstantFields.VAR_JOIN_EQUAL+msgSubscribe.isRunnig()+ConstantFields.VAR_JOIN_COMMA+ConstantFields.VAR_TERMINATED+ConstantFields.VAR_JOIN_EQUAL+msgSubscribe.getExecutorServicePool().isTerminated());
					Thread.sleep(500);
					if(!msgSubscribe.isRunnig() && msgSubscribe.getExecutorServicePool().isTerminated()){
						logger.info("....Redis队列监听已停止，队列名称="+msgSubscribe.getQueueName()+ConstantFields.VAR_JOIN_COMMA+ConstantFields.VAR_RUNNING+ConstantFields.VAR_JOIN_EQUAL+msgSubscribe.isRunnig()+ConstantFields.VAR_JOIN_COMMA+ConstantFields.VAR_TERMINATED+ConstantFields.VAR_JOIN_EQUAL+msgSubscribe.getExecutorServicePool().isTerminated());
						break;
//					}
				}
			}catch(Exception e){
				logger.error(ErrorCode.ERR_MSG_998,e);
				logger.info("停止消息订阅线程失败，队列名称="+queueName);
			}
		}
		logger.info("自动任务Redis队列["+queueName+"]监听停止运行......");
		
	}
	
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public void setReadTimeout(long readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}
	
}
