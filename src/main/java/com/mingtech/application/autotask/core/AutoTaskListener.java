package com.mingtech.application.autotask.core;


import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import com.mingtech.application.autotask.core.domain.*;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 日终任务  监听
 * 监听 所有 任务的执行情况，并在 执行前、后 进行日志的处理
 *
 */
public class AutoTaskListener implements JobListener{
	
	public String getName() {
		return "autoTaskListener";
	}
	/**
	 * 在jobdetail 即将被执行，但又被triggerListener否决了时调用这个方法
	 */
	public void jobExecutionVetoed(JobExecutionContext arg0) {		
	}
	/**
	 * 在jobdetail 将要被执行时调用这个方法；
	 */
	public void jobToBeExecuted(JobExecutionContext context) {
		//同步一下  所有任务的状态
		AutoTaskCurrentMonitor monitor = new AutoTaskCurrentMonitor();
		//设置一下  任务中缓存的日志的  状态信息
		AutoTaskLog log = (AutoTaskLog) context.getJobDetail().getJobDataMap().get("autoTaskLog");
		log.setStartTime(context.getFireTime());
		log.setStatus(AutoTaskInstance.STATUS_RUN);
		log.setRunDate(DateUtils.getWorkDayDate());	
		log.setErrMessage("执行成功");
	}
	/**
	 * 在jobdetail 被执行之后调用这个方法；
	 */
	public void jobWasExecuted(JobExecutionContext context, JobExecutionException e) {
		//任务执行完成后   保存日志记录
		AutoTaskLog log = (AutoTaskLog) context.getJobDetail().getJobDataMap().get("autoTaskLog");
		if (log==null )
			return;
		log.setEndTime(new Date());
		AutoTaskServiceFactory.getAutoTaskService().txSaveUpdateTaskLog(log);
	}

}
