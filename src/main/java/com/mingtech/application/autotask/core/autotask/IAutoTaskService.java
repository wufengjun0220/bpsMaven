package com.mingtech.application.autotask.core.autotask;



import java.util.Date;
import java.util.List;

import com.mingtech.application.autotask.core.domain.AutoTask;
import com.mingtech.application.autotask.core.domain.AutoTaskLog;
import com.mingtech.framework.core.dao.DAOException;
import com.mingtech.framework.core.page.Page;
import com.mingtech.framework.core.service.GenericService;

public interface IAutoTaskService extends GenericService{
	/**
	  * 执行任务
	 * 立即运行任务
	 * 提供给手工调用任务使用【可以通过页面调用次方法  执行任务】
	 * @param taskId 任务ID
	 * @throws Exception
	 */
	public void runTask(String taskId)throws Exception;
	/**
	 * 保存 任务日志记录
	 * 独立事物
	 * @param log
	 */
	public void txSaveUpdateTaskLog(AutoTaskLog log);
	/**
	 * 保存   任务日志并返回   对象ID
	 * 独立事物
	 * @param log
	 */
	public Object txSaveTaskLog(AutoTaskLog log);
	/**
	 * 新增日终任务配置
	 * @param autoTask
	 * @throws Exception
	 */
	public void txaddAutoTask(AutoTask autoTask) throws Exception;

	/**
	 * 更新AutoTask
	 * @param autoTask
	 * @throws Exception
	 */
	public void updateAutoTask(AutoTask autoTask) throws Exception;
	/**
	 * 通过ID获取AutoTask
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public AutoTask getAutoTask(String taskId);
	/**
	 * 获取打开的任务
	 * @return
	 * @throws Exception
	 */
	public List getOpenAutoTasks() throws Exception;
	/**
	 * 获取关闭的任务
	 * @return
	 * @throws Exception
	 */
	public List getCloseAutoTasks() throws Exception;
	
	/**
	 * 获取依赖的任务
	 * @return
	 * @throws Exception
	 */
	public List getDependAutoTasks(String taskId);
	/**
	 * 获取未依赖的任务
	 * @return
	 * @throws Exception
	 */
	public List getUnDependAutoTasks(String taskId) throws Exception;
	/**
	 *
	 *关闭日终任务
	 * @param autoTask
	 * @param id
	 * @throws Exception
	 */
	public void closeAutoTask(String taskId) throws Exception;
	/**
	 * 
	 * 开启日终任务
	 * @param autoTask
	 * @param id
	 * @throws Exception
	 */
	public void openAutoTask(String taskId) throws Exception;
	
	/**
	 * 根据任务id与原状态 更新新状态
	 * @param id 任务id
	 * @param oldSts 原状态
	 * @param newSts 新状态
	 * @return
	 * @throws DAOException
	 */
	public int txUpdateTaskStatus(String id, String oldSts, String newSts) ;	
	
	public List getAllAutoTasks() ;
	
	public List getWaitExcuteTasks() ;
	
	public boolean checkTaskSuccess(Date workDate);
	
	public List getExcuteTaskLogsById(String taskId, Page page) ;
	
	
	/**
	 * 删除日终任务配置
	 * @param autoTask
	 * @throws Exception
	 */
	public void txdelAutoTask(AutoTask autoTask) throws Exception;
}
