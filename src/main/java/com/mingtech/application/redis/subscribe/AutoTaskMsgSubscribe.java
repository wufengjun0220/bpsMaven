package com.mingtech.application.redis.subscribe;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mingtech.application.redis.RedisUtils;
import com.mingtech.application.utils.ConstantFields;
import com.mingtech.application.utils.ErrorCode;
import com.mingtech.framework.common.util.SpringContextUtil;

/**
 * <p>自动任务redis队列消息订阅</p>
 * @author h2
 * @date 2019年12月30日
 * @version 1.0
 * <p>修改记录</p>
 * h2   新建类    2019年12月30日
 */
public class AutoTaskMsgSubscribe extends Thread {
	private static final Log logger = LogFactory.getLog(AutoTaskMsgSubscribe.class);
	
	private String queueName;//队列名称
	private long readTimeout;
	private boolean loop=true;//循环标记
	private boolean running=true;//标记线程是否循环执行
	private ExecutorService executorServicePool;//来报业务处理线程池
	
	
	public AutoTaskMsgSubscribe(){ }

	/**
	 * <p>自动任务消息订阅构造器</p>
	 * @param queueName 队列名称
	 * @param  readTimeout 读取消息超时时间
	 * @param  poolSize 业务处理线程池大小
	 */
	public AutoTaskMsgSubscribe(String queueName,long readTimeout,int poolSize){
		this.queueName = queueName;
		this.readTimeout = readTimeout;
		executorServicePool = Executors.newFixedThreadPool(poolSize);
	}
	

	@Override
	public void run() {
		logger.info("[RedisBusiAutoQueueMonitor queueName="+queueName+"] start ...");
		RedisUtils redisMultitonQueueCache = (RedisUtils)SpringContextUtil.getBean("redisMultitonQueueCache");//消息订阅使用
		RedisUtils redisQueueCache = (RedisUtils)SpringContextUtil.getBean("redisQueueCache");//消息重新入队使用
		while(loop) {
			try{
				//通过阻塞方式从redis队列中读取消息直到超时
				Map msgDataMap = (Map)redisMultitonQueueCache.lbrPop(queueName, readTimeout);
				if(msgDataMap != null){
					if(!loop){//服务停止，将读取到的消息放回队列
						redisQueueCache.lSet(queueName, msgDataMap);
					}else{
						String taskRegId =  (String)msgDataMap.get(ConstantFields.VAR_TASK_REG_ID);//报文调度表业务ID
						logger.info("[从队列="+queueName+"获取到待处理任务taskId="+taskRegId+"]");
						executorServicePool.execute(new AutoTaskMsgHandle(queueName,msgDataMap,redisQueueCache));
					}
				}else{
					logger.debug("自动任务队列"+queueName+"中无消息");
				}

			}catch(Exception e){
				logger.error("[read msg from [queueName="+queueName+"] is error ",e);
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e1) {
					logger.error(ErrorCode.ERR_MSG_995,e1);
				}
			}
			
		}
		running = false;
	}

	
	public void stopSubscribe() {
		this.loop = false;
	}

	
	public boolean isRunnig(){
		return running;
	}

	public ExecutorService  getExecutorServicePool(){
		return executorServicePool;
	}
	
	public String getQueueName() {
		return queueName;
	}

}
