package com.mingtech.application.autotask.taskService;

import org.apache.log4j.Logger;
import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.autotask.service.AutoTaskExeService;
import com.mingtech.application.autotask.taskdispatch.AutoTaskPublishService;
import com.mingtech.application.cache.SystemConfigCache;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.SpringContextUtil;


/**
 * 调度异常的任务自动唤醒处理：
 * 	（1）执行设置：19：30执行一次 全天执行失败，未处理的报文
 *  （2）识别调度流水表中状态，将停留超过5分钟的唤醒任务
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-17
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class AutoDispatchNotifyAllTask  extends AbstractAutoTask {
	private static final Logger logger = Logger.getLogger(AutoDispatchNotifyAllTask.class);

	AutoTaskPublishService autoTaskPublishService =  PoolCommonServiceFactory.getAutoTaskPublishService();
	  AutoTaskExeService autoTaskExeService = (AutoTaskExeService)SpringContextUtil.getBean("autoTaskExeService");

	public BooleanAutoTaskResult run() throws Exception {
		logger.info("调度异常的任务自动唤醒处理....");
		String start    = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.OL_OPENTIME_YC    );//在线银承开始时间
		String end    = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.OL_ENDTIME_YC    );//在线银承结束时间
		String hour    = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.OL_LIMIT_ALLTIME    );//在线业务异常轮训处理时间 12小时
		String min    = SystemConfigCache.getSystemConfigItemByCode(SystemConfigCache.OL_LIMIT_MIN_TIME    );//轮训处理五分钟之前数据
		 boolean succ = DateUtils.isBetweenTimes(start,end,"HH:mm");
	     if (succ) {
	 		 //1：自动任务处理异常情况
	    	 autoTaskExeService.txAutoExeFailDispatch(hour,min);
	     
	         //2：自动任务未处理情况( 前置任务成功停留五分钟以上的)
	    	 autoTaskExeService.txAutoExeWaitDispatch(hour,min);
	    	 
	    	 //3: 银承、出入池任务成功最后没有等待到异步等待bbsp返回
	    	 autoTaskExeService.txAutoExeTaskStatus(hour,min,null);

	     }

		return new BooleanAutoTaskResult(true);
	}





	
	public AutoDispatchNotifyAllTask() {
	}
	
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}
	
	
	


}
