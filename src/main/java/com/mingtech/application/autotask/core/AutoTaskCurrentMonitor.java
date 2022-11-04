package com.mingtech.application.autotask.core;



import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mingtech.application.autotask.core.domain.AutoTask;
import com.mingtech.application.autotask.core.domain.AutoTaskLog;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 
 * 日终自动任务监控器
 * 
 */
public class AutoTaskCurrentMonitor {
	
	private Map taskMap = new HashMap();//key=taskId.toString(), value=AutoTaskInstance;
	/**
	 * 单例构造器
	 */
	public AutoTaskCurrentMonitor(){
		//全部注册为未启动
		List list = AutoTaskServiceFactory.getAutoTaskService().getAllAutoTasks();
		Iterator taskIt = list.iterator();
		//【第一步】以日终任务 数据库状态为准  同步一下“监控器中任务状态”
		while(taskIt.hasNext()){
			AutoTask autoTask = (AutoTask)taskIt.next();
			AutoTaskInstance ati = new AutoTaskInstance();
			if(StringUtils.isEmpty(autoTask.getExecStatus())){
				ati.setStatus(AutoTaskInstance.STATUS_UNUSED);
			}else{
				ati.setStatus(autoTask.getExecStatus());
			}
			ati.setName(autoTask.getName());
			ati.setTaskId(autoTask.getId());
			//
			this.addAutoTaskInstance(ati);
		}
		//【第二步】以日终任务执行日志为准，同步一下 “监控器中任务状态”
		//this.syncTaskLogStatus();		
	}
	/**
	 * 同步日志状态
	 *
	 */
	private void syncTaskLogStatus(){
		List logList = AutoTaskServiceFactory.getAutoTaskService().
				find("from AutoTaskLog where runDate=? order by startTime desc", new Object[]{ DateUtils.getWorkDayDate()});
		if (logList.isEmpty()){
			this.reset();
			return;
		}
		Iterator logIt = logList.iterator();
		while (logIt.hasNext()){
			AutoTaskLog taskLog = (AutoTaskLog)logIt.next();
			AutoTaskInstance ati = this.getAutoTaskInstance(taskLog.getTaskId());
			if (ati != null){
				ati.setEndTime(taskLog.getEndTime());				
				ati.setRunDate(taskLog.getRunDate());
				ati.setStartTime(taskLog.getStartTime());
				ati.setStatus(taskLog.getStatus());
				if (AutoTaskInstance.STATUS_RUN.equals(ati.getStatus()) && ati.getRate()<=1) {
					ati.setRate(55);
				}else if (AutoTaskInstance.STATUS_FINISH.equals(ati.getStatus()) && ati.getRate()<=1) {
					ati.setRate(100);
				}
			}			
		}
	}
	
	/**
	 * 增加任务实例
	 * @param ati
	 */
	public void addAutoTaskInstance(AutoTaskInstance ati){		
		taskMap.put(ati.getTaskId(), ati);
	}
	/**
	 * 删除任务实例
	 * @param taskId
	 */
	public void delAutoTaskInstance(String taskId){		
		taskMap.remove(taskId);
	}
	
	public AutoTaskInstance getAutoTaskInstance(String taskId){
		return (AutoTaskInstance)taskMap.get(taskId);
	}
	/**
	 * 清空
	 */
	public void clear(){
		this.taskMap.clear();
	}
	/**
	 * 还原所有任务状态
	 */
	public void reset(){
		Iterator taskIt = taskMap.values().iterator();
		while (taskIt.hasNext()){
			AutoTaskInstance ati = (AutoTaskInstance)taskIt.next();
			ati.setStatus(AutoTaskInstance.STATUS_UNUSED);
			ati.setEndTime(null);
			ati.setRate(0);
			//ati.setRunDate(null);
			ati.setStartTime(null);
		}
	}

}
