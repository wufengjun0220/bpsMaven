package com.mingtech.application.autotask.core;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mingtech.application.autotask.core.autotask.IAutoTaskService;
import com.mingtech.application.autotask.core.domain.AutoTask;
import com.mingtech.application.autotask.core.domain.AutoTaskLog;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;

/**
 * 日终自动任务基类
 * 所有需要 日终自动执行的任务都要  继承这个类
 *
 */
public abstract class AbstractAutoTask implements Job{	
	private static final Logger logger = Logger.getLogger(AbstractAutoTask.class);
	
	private String taskId;
	private String taskPara;
	/**
	 * 定时任务 执行 入口 
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException {
		AutoTask autoTask = (AutoTask) context.getJobDetail().getJobDataMap().get("autoTask");
		//如果任务设置为  手工执行，但是 不是手工触发的  就不进行处理
		if(AutoTask.EXEC_MODULE_MAUAL.equals(autoTask.getExecModule())&& !"1".equals(autoTask.getTaskRunType())){
			return ;
		}
		Date workDay = DateUtils.getWorkDayDate();
		AutoTaskLog taskLog = (AutoTaskLog) context.getJobDetail().getJobDataMap().get("autoTaskLog");	
		String runModel = (String)context.getJobDetail().getJobDataMap().get(ScheduleHelper.Var_Key_RunModel);
		IAutoTaskService taskService = AutoTaskServiceFactory.getAutoTaskService();
		//任务监控对象
		AutoTaskCurrentMonitor monitor = new AutoTaskCurrentMonitor();
		//运行前状态检查
		BooleanAutoTaskResult check = excuteBeforeCheck(autoTask,workDay,runModel);
		if(!check.isSuccess()){//检查不通过			
			taskLog.setRunDate(workDay);
			taskLog.setStatus(AutoTaskInstance.STATUS_ERROR);
			taskLog.setErrMessage(check.getInfo());
			//保存 日终任务日志
			taskService.txSaveUpdateTaskLog(taskLog);
			return;
		}
		taskId = autoTask.getId();	
		AutoTaskInstance ati = new AutoTaskInstance();
		Integer waitingTime=autoTask.getWaitingTime();
		
		ati.setTaskId(autoTask.getId());
		ati.setName(autoTask.getName());
		ati.setRunDate(workDay);
		ati.setStartTime(new Date());
		ati.setStatus(AutoTaskInstance.STATUS_RUN);
		ati.setRate(1);
		//将本任务加入监控器中
		monitor.addAutoTaskInstance(ati);
		
	
		
		try {
			if (ScheduleHelper.Run_Model_Run.equals(runModel)){
				int count=0;
				//如果有依赖任务
				while(!isDependFinished(taskId)){
					try {
						Thread.sleep(5000);	
						count++;
						if (count>waitingTime.longValue()*60/5)
							throw new Exception("'"+autoTask.getName()+"'"+"所依赖任务未完成，不能执行");				
					} catch (InterruptedException e) {
						throw new Exception("'"+autoTask.getName()+"'"+"所依赖任务未完成，不能执行");	
					}
				}
				Date nextDate = DateUtils.getNextWorkday();
				//*********执行具体的任务 逻辑**************
				
				
				logger.info("【"+autoTask.getName()+"】自动任务执行开始...");
				Date starTime = new Date();
				
				BooleanAutoTaskResult br = run();
				
				Date endTime = new Date();
				long executeTime = (endTime.getTime() - starTime.getTime());
				logger.info("【"+autoTask.getName()+"】自动任务执行结束，执行时间为 "+executeTime/1000+"秒（"+executeTime+"毫秒)");
				
				
				if (!br.isSuccess()){
					throw new Exception(br.getInfo());
				}
				// 任务执行完成 ---设置为：下一个工作日  
				autoTask.setWorkday(nextDate);
				//设置为下一日任务状态  为当日未执行
				autoTask.setExecStatus(AutoTask.EXEC_STATUS_1);
			}else if (ScheduleHelper.Run_Model_RollBack.equals(runModel)){
				BooleanAutoTaskResult br = rollBack();
				if (!br.isSuccess()){
					throw new Exception(br.getInfo());
				}
				autoTask.setExecStatus(AutoTask.EXEC_STATUS_1);
				autoTask.setWorkday(workDay);//当前工作日
			}else{
				
				logger.info("【"+autoTask.getName()+"】自动任务【重新】执行开始...");
				Date starTime = new Date();
				
				BooleanAutoTaskResult br = reRun();
				
				Date endTime = new Date();
				long executeTime = (endTime.getTime() - starTime.getTime());
				logger.info("【"+autoTask.getName()+"】自动任务【重新】执行结束，执行时间为 "+executeTime/1000+"秒（"+executeTime+"毫秒)");
				
				
				if (!br.isSuccess()){
					throw new Exception(br.getInfo());
				}
				autoTask.setExecStatus(AutoTask.EXEC_STATUS_1);
				autoTask.setWorkday(DateUtils.getNextWorkday());//下一个工作日
			}
			
			ati.setEndTime(new Date());			
			ati.setStatus(AutoTaskInstance.STATUS_FINISH);
			this.setRateOfProgress(100);
		} catch (Throwable e) {	
			ati.setStatus(AutoTaskInstance.STATUS_ERROR);
			taskLog.setStartTime(ati.getStartTime());
			taskLog.setRunDate(ati.getRunDate());
			String logId = (String)taskService.txSaveTaskLog(taskLog);
			taskLog.setId(logId);
			taskLog.setErrMessage(e.getMessage());
			taskLog.setRunDate(ati.getRunDate());
			taskLog.setStartTime(ati.getStartTime());
			taskLog.setStatus(AutoTaskInstance.STATUS_ERROR);
			taskLog.setTaskId(autoTask.getId());
			autoTask.setExecStatus(AutoTask.EXEC_STATUS_4);
			autoTask.setWorkday(workDay);
			logger.error(e.getMessage(),e);
		}
		AutoTaskServiceFactory.getAutoTaskService().txStore(autoTask);
	}
	
	/**
	 * 任务执行前 的状态检查
	 * @param context
	 * @return
	 * @throws JobExecutionException
	 */
	private BooleanAutoTaskResult excuteBeforeCheck(AutoTask autoTask,Date workDay,String runModel) throws JobExecutionException {
		String runStatus = autoTask.getExecStatus();
		BooleanAutoTaskResult result = new BooleanAutoTaskResult(false);
		autoTask = (AutoTask)AutoTaskServiceFactory.getAutoTaskService().getAutoTask(autoTask.getId());
		//表示自动任务已经启动
		if(AutoTask.STATUS_CLOSE.equals(autoTask.getStatus())&&!AutoTask.EXEC_STATUS_5.equals(runStatus)){ 
			String message = "日终任务为关闭状态，必须手动执行！";
			return new BooleanAutoTaskResult(false,message);
		}
		if(AutoTask.EXEC_STATUS_2.equals(autoTask.getExecStatus())){ //表示自动任务已经启动
			String message = "查询时发现自动任务["+autoTask.getName()+"]已经由另一个server启动,程序已返回";
			return new BooleanAutoTaskResult(false,message);
		}
		String execStatus = autoTask.getExecStatus();
		//新增任务的时候这个字段会为空，在这里默认给他们初始值
		if(execStatus == null){
			int ct = AutoTaskServiceFactory.getAutoTaskService().txUpdateTaskStatus(autoTask.getId(),null,"0");
			if(ct != 1){
				String message = "更新任务状态[null -> 0]时发现自动任务["+autoTask.getName()+"]的状态已经改变,可能是另一个server已更新，程序已返回";
				return new BooleanAutoTaskResult(false,message);
			}
			execStatus = "0";
		}
		//1、初始状态  2：当日未执行 、执行一次，并且 当期日期小于任务的当前日期
		if(AutoTask.EXEC_STATUS_0.equals(execStatus)
				||((AutoTask.EXEC_STATUS_1.equals(execStatus) || AutoTask.EXEC_STATUS_4.equals(execStatus))
						&& !workDay.before(autoTask.getWorkday()))){
			int cnt = AutoTaskServiceFactory.getAutoTaskService().txUpdateTaskStatus(autoTask.getId(), "0,1,4", "2"); //把任务状态改为执行中
			if(cnt == 1){
				//当日执行中
				autoTask.setExecStatus(AutoTask.EXEC_STATUS_2);
				//执行日期设置为当期工作日
				autoTask.setWorkday(workDay);
				return new BooleanAutoTaskResult(true);
			}else if(cnt == 0){ //表示自动任务已经启动
				String message = "更新任务状态时发现自动任务["+autoTask.getName()+"]已经由另一个server启动,程序已返回";
				return new BooleanAutoTaskResult(false,message);
			}
		}else if(workDay.before(autoTask.getWorkday())){//当日工作日   小于   任务的执行日期
			//可循环执行
//			if(ScheduleHelper.Run_Model_Run.equals(runModel)){//执行模式
//				String message = "自动任务["+autoTask.getName()+"]下次运行日期大于当前工作日，程序已返回。";
//				return new BooleanAutoTaskResult(false,message);
//			}else{
				//回滚和重做可以
				int cnt = AutoTaskServiceFactory.getAutoTaskService().txUpdateTaskStatus(autoTask.getId(), "0,1", "2"); //把任务状态改为执行中
				if(cnt == 1){
					autoTask.setExecStatus(AutoTask.EXEC_STATUS_2);
					autoTask.setWorkday(workDay);
					return new BooleanAutoTaskResult(true);
				}else if(cnt == 0){ //表示自动任务已经启动
					String message = "更新任务状态时发现自动任务["+autoTask.getName()+"]已经由另一个server启动,程序已返回";
					return new BooleanAutoTaskResult(false,message);
				}
//			}
		}
		return result;
	}
	
	/**
	 * 任务处理
	 * @throws Exception
	 */
	public abstract BooleanAutoTaskResult run() throws Exception;
	
	/**
	 * 任务回滚
	 * @throws Exception
	 */
	public abstract BooleanAutoTaskResult rollBack() throws Exception;
	
	/**
	 * 重新执行
	 * @throws Exception
	 */
	public BooleanAutoTaskResult reRun() throws Exception{
		BooleanAutoTaskResult br = rollBack();
		if (br == null || br.isSuccess()){
			return run();
		}else{
			return br;
		}
		
	}
	
	/**
	 * 任务的进度百分比例
	 * @param rate 范围：0－100
	 * @return
	 */
	public void setRateOfProgress(int rate){
		AutoTaskCurrentMonitor monitor = new AutoTaskCurrentMonitor();
		AutoTaskInstance ati = monitor.getAutoTaskInstance(taskId);
		if (rate > 100)
			rate = 100;
		if (rate < 0)
			rate = 0;
		ati.setRate(rate);
	}
	
	private boolean isDependFinished(String taskId){
		IAutoTaskService autoTaskService = AutoTaskServiceFactory.getAutoTaskService();
		AutoTaskCurrentMonitor monitor = new AutoTaskCurrentMonitor();
		List dependTasks = autoTaskService.getDependAutoTasks(taskId); 
		for (int i = 0; i < dependTasks.size(); i++) {
			AutoTask autoTask = (AutoTask)dependTasks.get(i);
			//通过监控器  查看任务状态
			AutoTaskInstance ati = (AutoTaskInstance)monitor.getAutoTaskInstance(autoTask.getId());
			if (ati==null || !AutoTaskInstance.STATUS_FINISH.equals(ati.getStatus())){
				return false;
			}
		}
		return true;
	}
	/**
	 * @return Returns the taskId.
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @return Returns the taskInfo.
	 */
	public String getTaskPara() {
		return taskPara;
	}
	
	protected String getStringVal(Object obj) throws Exception {
		String value = "";
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = temp;
			}
		}
		return value;
	}

	protected Date getDateVal(Object obj) throws Exception {
		Date value = null;
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = DateUtils.parseDatStr2Date(temp, "yyyyMMdd");
			}
		}
		return value;
	}

	protected BigDecimal getBigDecimalVal(Object obj) throws Exception {
		BigDecimal value = null;
		if (obj != null) {
			String temp = (String) obj;
			if (StringUtil.isNotBlank(temp.trim())) {
				value = new BigDecimal(temp);
			}
		}
		return value;
	}
	
}
