package com.mingtech.application.autotask.core.autotask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AutoTaskCurrentMonitor;
import com.mingtech.application.autotask.core.AutoTaskInstance;
import com.mingtech.application.autotask.core.ScheduleHelper;
import com.mingtech.application.autotask.core.domain.AutoTask;
import com.mingtech.application.autotask.core.domain.AutoTaskLog;
import com.mingtech.application.runmanage.web.AutoTaskController;
import com.mingtech.framework.core.dao.DAOException;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;
import com.sun.jmx.snmp.tasks.Task;
/**
 * 日终任务服务类
 * @author Administrator
 * com.mingtech.application.autotask.core.autotask.AutoTaskServiceImpl
 */
public class AutoTaskServiceImpl extends GenericServiceImpl  implements IAutoTaskService{
	private ScheduleHelper helper=new ScheduleHelper();
	private static final Logger logger = Logger.getLogger(AutoTaskServiceImpl.class);
	/**
	 * 执行任务
	 * 立即运行任务
	 * 提供给手工调用任务使用【可以通过页面调用次方法  执行任务】
	 * @param taskId 任务ID
	 * @throws Exception
	 */
	public void runTask(String taskId)throws Exception{
		AutoTask autoTask = (AutoTask)this.load(taskId, AutoTask.class);
		helper.immediateJob(autoTask, ScheduleHelper.Run_Model_Run);
	}
	/**
	 * 关闭日终任务
	 */
	public void closeAutoTask(String taskId) throws Exception {
		AutoTask autoTask=this.getAutoTask(taskId);
		helper.deleteJob(autoTask);
		autoTask.setStatus(AutoTask.STATUS_CLOSE);
		txStore(autoTask);		
	}
	/**
	 * 开启日终任务
	 */
	public void openAutoTask(String taskId) throws Exception {
		AutoTaskCurrentMonitor monitor = new AutoTaskCurrentMonitor();
		AutoTask autoTask=this.getAutoTask(taskId);		
		autoTask.setStatus(AutoTask.STATUS_OPEN);
		helper.startJob(autoTask);
		txStore(autoTask);
		
		AutoTaskInstance ati = new AutoTaskInstance();		
		ati.setTaskId(autoTask.getId());
		ati.setName(autoTask.getName());
		ati.setRunDate(new Date());
		ati.setStartTime(new Date());
		ati.setStatus(AutoTaskInstance.STATUS_UNUSED);
		monitor.addAutoTaskInstance(ati);		
	}
	/**
	 * 新增/编辑日终任务配置 
	 */
	public void txaddAutoTask(AutoTask autoTask) throws Exception {
		try {
			//autoTask.setStatus(AutoTask.STATUS_CLOSE);
			this.txStore(autoTask);	
			if (autoTask.getExecModule().endsWith(autoTask.EXEC_MODULE_AUTO)&&autoTask.getStatus().equals(autoTask.STATUS_OPEN)) {
				ScheduleHelper helper=new ScheduleHelper();
				helper.startJob(autoTask);
			}
		} catch (DAOException e) {
			logger.error(e.getMessage(),e);
			throw new Exception(e);
		}
	}
	/**
	 * 删除日终任务配置
	 * @param autoTask
	 * @throws Exception
	 */
	public void txdelAutoTask(AutoTask autoTask) throws Exception {
		try {
			dao.delete(autoTask);
			if (autoTask.getExecModule().endsWith(autoTask.EXEC_MODULE_AUTO)&&autoTask.getStatus().equals(autoTask.STATUS_OPEN)) {
				ScheduleHelper helper=new ScheduleHelper();
				helper.deleteJob(autoTask);
			}
		} catch (DAOException e) {
			logger.error(e.getMessage(),e);
			throw new Exception(e);
		}
	}
	/**
	 * 保存 任务日志记录
	 * 独立事物
	 * @param log
	 */
	public void txSaveUpdateTaskLog(AutoTaskLog log){
		this.txStore(log);
	}
	/**
	 * 保存   任务日志并返回   对象ID
	 * 独立事物
	 * @param log
	 */
	public Object txSaveTaskLog(AutoTaskLog log){
		return this.txSaveEntity(log);
	}
	/**
	 * 通过任务ID 更新任务状态 
	 * 独立事务
	 * 1成功
	 * 0失败
	 */
	public int txUpdateTaskStatus(String id, String oldStr, String newSts){
		AutoTask task = (AutoTask)this.load(id, AutoTask.class);
		if(oldStr == null){
			task.setExecStatus(newSts);
			this.txStore(task);
			return 1;
		}
		if(task.getExecStatus()!=null && oldStr.indexOf(task.getExecStatus())>-1){
			task.setExecStatus(newSts);
			this.txStore(task);
			return 1;
		}
		return 0;
	}
	/**
	 * 查询所有的任务
	 */
	public List getAllAutoTasks() {
		List<AutoTask> list=null;
		try {
			String hql = " FROM AutoTask   ORDER BY orderNum ASC";
			list = this.find(hql);
		} catch (DAOException e) {
			logger.error(e.getMessage(),e);
		}
		
		return list;
	}
	/**
	 * 根据任务ID  查询 依赖的任务
	 */
	public List getDependAutoTasks(String taskId){
		AutoTask curTask = (AutoTask)this.load(taskId, AutoTask.class);
		String dependTasks = curTask.getDependTasks();
		if(dependTasks==null || StringUtils.isEmpty(dependTasks)){
			return  new ArrayList();
		}
		String[] deps = dependTasks.split(",");
		String hql = "FROM AutoTask WHERE dependTasks in:(dependTasks)  ORDER BY orderNum ASC";
		Object[] paramValues = new Object[] {deps};
		String[] paramNames = new String[] {"dependTasks"};
		List<AutoTask> list = this.find(hql, paramNames, paramValues);
		
		return list;
	}

	public AutoTask getAutoTask(String taskId){
		return (AutoTask)dao.load(AutoTask.class,taskId);
	}
	
	

	public void updateAutoTask(AutoTask autoTask) throws Exception {
			this.txStore(autoTask);
	}

	public ScheduleHelper getHelper() {
		return helper;
	}

	/**
	 * 查询所有的任务
	 */
	public List getWaitExcuteTasks() {
		List<AutoTask> list=null;
		try {
			String hql = " FROM AutoTask where status='1'  ORDER BY orderNum ASC";
			list = this.find(hql);
		} catch (DAOException e) {
			logger.error(e.getMessage(),e);
		}
		return list;
	}
	public List getExcuteTaskLogsById(String taskId, Page page) {
		List<AutoTaskLog> list=null;
		try {
			String hql = " FROM AutoTaskLog where 1=1 ";
			//Object[] paramValues = new Object[] {taskId};
			//String[] paramNames = new String[] {"TaskID"};
			List paramNames = new ArrayList();
			List paramValues = new ArrayList();
			if (taskId != null && !taskId.equals("") ) {
				hql += " and taskId=:TaskID ";
				paramNames.add("TaskID");
				paramValues.add(taskId);
			}
			
			hql+=" ORDER BY startTime desc ";
			
			String [] nameForSetVar = (String [])paramNames.toArray(new String[paramNames.size()]);//查询条件名
			Object[] parameters = paramValues.toArray(); //查询条件值
			
			list = this.find(hql, nameForSetVar, parameters,page);
		} catch (DAOException e) {
			logger.error(e.getMessage(),e);
		}
		return list;
	}
	
	public boolean checkTaskSuccess(Date workDate){
		List<AutoTask> list=null;
		try {
			String hql = " FROM AutoTask where status='1' and workday= :WorkDate ORDER BY orderNum ASC";
			Object[] paramValues = new Object[] {workDate};
			String[] paramNames = new String[] {"WorkDate"};
			list = this.find(hql, paramNames, paramValues);
		} catch (DAOException e) {
			logger.error(e.getMessage(),e);
			return false;
		}
		if(null!=list && list.size()>0){
			return false;
		}
		return true;
	}

	public String getEntityName() {
		// TODO Auto-generated method stub
		return "日终服务999999999999999999999999";
	}

	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}


	public List getUnDependAutoTasks(String taskId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List getOpenAutoTasks() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public List getCloseAutoTasks() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	

}
