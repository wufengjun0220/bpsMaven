package com.mingtech.application.autotask.core;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.mingtech.application.autotask.core.domain.AutoTask;
import com.mingtech.application.autotask.core.domain.AutoTaskLog;



public class ScheduleHelper {
	private static final Logger logger = Logger.getLogger(ScheduleHelper.class);

	/**变量KEY--运行模式*/
	public final static String Var_Key_RunModel = "runModel";
	/**运行模式--运行*/
	public final static String Run_Model_Run = "1";
	/**运行模式--回滚*/
	public final static String Run_Model_RollBack = "2";
	/**运行模式--重新运行*/
	public final static String Run_Model_ReRun = "3";
	/**
	 * 打开任务
	 * @param autoTask
	 * @return
	 * @throws Exception
	 */
	public void startJob(AutoTask autoTask) throws Exception{
		//如果当前任务已经注册完成，则先把当前任务删除掉；
		this.deleteJob(autoTask);		
		try {	
			Scheduler scd = StdSchedulerFactory.getDefaultScheduler();			
			//将当前  日终任务   放入  job中
			JobDetail jobDetail = this.createJobDetail(autoTask);
			//设置当前job模式为【执行】
			jobDetail.getJobDataMap().put(Var_Key_RunModel, Run_Model_Run);
			//trigger 设置job的定时触发器
			CronTrigger triger=new CronTrigger();
			triger.setName(autoTask.getCode());
			triger.setGroup(autoTask.getId().toString());
			triger.setCronExpression(autoTask.getCronExpr());
			
			//设置 任务的 监听器
			scd.addJobListener(new AutoTaskListener());
			jobDetail.addJobListener("autoTaskListener");
			//设置完成  添加到 spring的调度任务；
			scd.scheduleJob(jobDetail,triger);		
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			logger.error(e.getMessage(),e);
			throw new Exception("日终任务启动失败:"+e.getMessage());
		}
	}
	/**
	 * 
	 * 立即运行任务
	 * 提供给手工调用任务使用【可以通过页面调用次方法  执行任务】
	 * @param autoTask
	 * @return
	 * @throws Exception
	 */
	public void immediateJob(AutoTask autoTask, String runModel) throws Exception{
		try {
			//手工触发执行
			autoTask.setExecStatus(autoTask.EXEC_STATUS_5);
			//调度方式  设置为 手工调度
			autoTask.setTaskRunType("1");
			this.startJob(autoTask);
			
			AutoTaskCurrentMonitor monitor = new AutoTaskCurrentMonitor();
			AutoTaskInstance ati = new AutoTaskInstance();		
			ati.setTaskId(autoTask.getId());
			ati.setName(autoTask.getCode());
			ati.setRunDate(new Date());
			ati.setStartTime(new Date());
			ati.setStatus(AutoTaskInstance.STATUS_UNUSED);
			monitor.addAutoTaskInstance(ati);
			
			Scheduler scd = StdSchedulerFactory.getDefaultScheduler();
			scd.triggerJob(autoTask.getCode(), autoTask.getId().toString());
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new Exception("日终任务启动失败:"+e.getMessage());
		}
	}
	/**
	 * 删除任务
	 * @param autoTask
	 * @return
	 * @throws Exception
	 */
	public void deleteJob(AutoTask autoTask) throws Exception{
		try {			
			Scheduler scd=StdSchedulerFactory.getDefaultScheduler();
			scd.deleteJob(autoTask.getCode(), String.valueOf(autoTask.getId()));
		} catch (SchedulerException e) {
			logger.error(e.getMessage(),e);
			throw new Exception("关闭任务异常"+e.getMessage());
		}
	}
	
	
	
	private JobDetail createJobDetail(AutoTask autoTask) throws ClassNotFoundException {
//		job类全名
		String className=autoTask.getClassName();
		Class obj= Class.forName(className);
//		job
		JobDetail jobDetail = new JobDetail();
		jobDetail.setName(autoTask.getCode());				
		jobDetail.setGroup(autoTask.getId());
		jobDetail.setJobClass(obj);
		//把任务 放入 监听中
		jobDetail.getJobDataMap().put("autoTask", autoTask);
		AutoTaskLog log = new AutoTaskLog();
		log.setTaskId(autoTask.getId());
		//任务对应的  执行日志
		jobDetail.getJobDataMap().put("autoTaskLog", log);
		
		return jobDetail;
	}
	
}
