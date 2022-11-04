package com.mingtech.application.autotask.actions;

import java.util.Date;
import java.util.List;



import org.apache.log4j.Logger;
import org.quartz.CronExpression;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.AutoTaskCurrentMonitor;
import com.mingtech.application.autotask.core.AutoTaskInstance;
import com.mingtech.application.autotask.core.AutoTaskServiceFactory;
import com.mingtech.application.autotask.core.ScheduleHelper;
import com.mingtech.application.autotask.core.autotask.IAutoTaskService;
import com.mingtech.application.autotask.core.domain.AutoTask;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.action.BaseAction;
import com.mingtech.framework.core.page.Page;

/**
 * 日终任务服务
 * com.mingtech.application.autotask.actions
 *
 */
public class AutoTaskAction  extends BaseAction{
	private static final Logger logger = Logger
			.getLogger(AutoTaskAction.class);
	private String taskId;
	private Date workDate;
	private AutoTask autoTask;
	/**
	 * 日终任务查询入口
	 * @return
	 */
	public String queryAutoTaskList(){
		User user = this.getCurrentUser();
		workDate = user.getWorkDate();
		return SUCCESS;
	}
	/**
	 * 数据查询
	 */
	public void queryAutoTaskListJoson(){
		try{
			Page page = this.getPage();
			User user = this.getCurrentUser();
			workDate =user.getWorkDate();
			List result = AutoTaskServiceFactory.getAutoTaskService().getAllAutoTasks();
			String json = JsonUtil.buildJson(result, result.size());
			this.sendJSON(StringUtil.isBlank(json) ? this.RESULT_EMPTY_DEFAULT
					: json);
		}catch (Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public void validTaskRun(){
		String json = "";
		User user = this.getCurrentUser();
		//IAutoTaskService taskService = AutoTaskServiceFactory.getAutoTaskService();
		//List result = taskService.getWaitExcuteTasks();
		IAutoTaskService service = AutoTaskServiceFactory.getAutoTaskService();
		autoTask = service.getAutoTask(taskId);
		Date curDate = user.getWorkDate();
		boolean flag =false;
		//for(int i=0;i<result.size();i++){
		if (taskId != null && !taskId.equals("")) {
			AutoTask task = autoTask;
			//taskId = task.getId();
			//String curDateStr = DateUtils.toString(curDate, DateUtils.ORA_DATES_FORMAT);
			//String taskDateStr = DateUtils.toString(task.getWorkday(), DateUtils.ORA_DATES_FORMAT);
			//if(AutoTask.EXEC_MODULE_MAUAL.equals(task.getExecModule())&&taskDateStr.equals(curDateStr)){
				if(AutoTask.EXEC_STATUS_2.equals(task.getExecStatus())){
					json = "{'result':false,'message':'"+task.getName()+"任务在执行中请勿重复执行！"+"'}";
					this.sendJSON(json);
					return;
				}
				flag =true;
			//}
		}
		//}
		if(!flag){
			json = "{'result':false,'message':'当日无可执行任务'}";
		}else{
			json = "{'result':true,'message':''}";
		}
		this.sendJSON(json);
	}
	
	/**
	 * 执行任务
	 */
	public String runTask(){
		User user = this.getCurrentUser();
		IAutoTaskService taskService = AutoTaskServiceFactory.getAutoTaskService();
//		List result = taskService.getWaitExcuteTasks();
//		Date curDate = user.getWorkDate();
//		boolean flag =false;
//		for(int i=0;i<result.size();i++){
//			AutoTask task = (AutoTask) result.get(i);
//			taskId = task.getId();
//			String curDateStr = DateUtils.toString(curDate, DateUtils.ORA_DATES_FORMAT);
//			String taskDateStr = DateUtils.toString(task.getWorkday(), DateUtils.ORA_DATES_FORMAT);
//			if(AutoTask.EXEC_MODULE_MAUAL.equals(task.getExecModule())&&taskDateStr.equals(curDateStr)){
//				//runTask
//			}else{
//				//当天无待执行的任务
//			}
//			
//		}
		try {
			taskService.runTask(taskId);
		} catch (Exception e) {
			this.sendJSON("{isOk:\"false\",mess:\"提交失败\"}");
			logger.error(e.getMessage(),e);
		}
		this.sendJSON("{isOk:\"false\",mess:\"提交完成\"}");
		return SUCCESS;
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
	
	
	/**
	 * 数据查询
	 */
	public void queryTaskExcLogJoson(){
		try{
			Page page = this.getPage();
			List result = AutoTaskServiceFactory.getAutoTaskService().getExcuteTaskLogsById(taskId,page);
			String json = JsonUtil.buildJson(result, result.size());
			this.sendJSON(StringUtil.isBlank(json) ? this.RESULT_EMPTY_DEFAULT
					: json);
		}catch (Exception e){
			logger.error(e.getMessage(),e);
		}
	}
//	private List autoTasks;
//	private Date executeDate;
//	private Long id;
//	private Long[] leftTasks ;
//	private List dependTasks;
//	private Long[] rightTasks ;
//	private List unDependTasks;
//	private AutoTask autoTask;
	
//	public String list() throws ServiceException{		
//		Page pg=this.getPage();
//		//pg.setPageCommand(this.getCommand());
//		//pg.setPageSize(50);
//		AutoTaskSearchBean bean=new AutoTaskSearchBean();
//		autoTasks=AutoTaskServiceFactory.getAutoTaskService().getBySearchbean(pg, bean);
//		if(pg.getTotalPages()<1)
//			pg.setTotalPages(1);
//		return "list";
//	}
//	
	public String toEdit(){
		IAutoTaskService service = AutoTaskServiceFactory.getAutoTaskService();
		autoTask = service.getAutoTask(taskId);
		//dependTasks = service.getDependAutoTasks(id);
		//unDependTasks = service.getUnDependAutoTasks(id);
		return SUCCESS;
	}
	
	public String toAdd() {
		//IAutoTaskService service = AutoTaskServiceFactory.getAutoTaskService();
		//dependTasks = new ArrayList(0);
		//unDependTasks = service.getAutoTasks();
		return SUCCESS;
	}
	
	public void del() {
		if (autoTask == null ) {
			autoTask = new AutoTask();
		}
		IAutoTaskService service = AutoTaskServiceFactory.getAutoTaskService();
		autoTask = service.getAutoTask(taskId);
		
		//删除数据库中的任务
		try {
			
			AutoTaskServiceFactory.getAutoTaskService().txdelAutoTask(autoTask);
			this.sendJSON("{isOk:\"false\",mess:\"删除成功\"}");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.sendJSON("{isOk:\"false\",mess:\"删除失败\"}");
		}
		
	}
	
	public void update() throws Exception{
		String className=autoTask.getClassName();
		User user = this.getCurrentUser();
		//autoTask.setWorkday(user.getWorkDate());
		try {
			Class clazz = Class.forName(className);
			if (!(clazz.newInstance() instanceof AbstractAutoTask)){
				this.addActionError("该执行类未继承自动任务基类");
				return;
			}
			if(!CronExpression.isValidExpression(autoTask.getCronExpr())){
				this.addActionError("Cron表达式格式不正确");
				return;
			}
			AutoTaskServiceFactory.getAutoTaskService().txaddAutoTask(autoTask);
			this.sendJSON("{isOk:\"true\",mess:\"更改成功\"}");
		} catch (Exception e) {
			this.addActionError("该执行类不存在或缺少无参构造器");
			this.sendJSON("{isOk:\"false\",mess:\"更改失败\"}");
		}
		
//		if (leftTasks!=null){
//			String dependTaskStr = "";
//			for (int i = 0; i < leftTasks.length; i++) {
//				dependTaskStr += leftTasks[i]+",";
//			}
//			autoTask.setDependTasks(dependTaskStr);
//		}
		
	}
	
	/**
	 * 增加日终任务
	 */
	public void addAutoTask() throws Exception{
		User user = this.getCurrentUser();
		autoTask.setId(null);
		autoTask.setExecStatus(autoTask.EXEC_STATUS_0);
		//autoTask.setWorkday(user.getWorkDate());
		String className=autoTask.getClassName();
		try {
			Class clazz = Class.forName(className);
			if (!(clazz.newInstance() instanceof AbstractAutoTask)){
				this.addActionError("该执行类未继承自动任务基类");
				return ;
			}
			if(!CronExpression.isValidExpression(autoTask.getCronExpr())){
				this.addActionError("Cron表达式格式不正确");
				return ;
			}
			AutoTaskServiceFactory.getAutoTaskService().txaddAutoTask(autoTask);
			
			this.sendJSON("{isOk:\"true\",mess:\"添加成功\"}");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			this.sendJSON("{isOk:\"false\",mess:\"添加失败\"}");
		}
		
	}
//	/**
//	 * 开启日终任务
//	 * @return
//	 * @throws Exception
//	 */
//	public String openTask() throws Exception{
//		AutoTaskServiceFactory.getAutoTaskService().openAutoTask(taskId);
//		return "listAction";
//	}
//	/**
//	 * 关闭日终任务
//	 * @return
//	 * @throws Exception
//	 */
//	public String closeTask() throws Exception{
//		AutoTaskServiceFactory.getAutoTaskService().closeAutoTask(taskId);
//		return "listAction";
//	}
//	
	public AutoTask getAutoTask() {
		return autoTask;
	}

	public void setAutoTask(AutoTask autoTask) {
		this.autoTask = autoTask;
//		long waiting=this.autoTask.getWaitingTime().longValue()*60;
//		this.autoTask.setWaitingTime(new Long(waiting));
	}
//
//	public List getAutoTasks() {
//		return autoTasks;
//	}
//
//	public void setAutoTasks(List autoTasks) {
//		this.autoTasks = autoTasks;
//	}
//
//	public Date getExecuteDate() {
//		return executeDate;
//	}
//
//	public void setExecuteDate(Date executeDate) {
//		this.executeDate = executeDate;
//	}
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	/**
//	 * @return Returns the dependTasks.
//	 */
//	public List getDependTasks() {
//		return dependTasks;
//	}
//
//	/**
//	 * @return Returns the unDependTasks.
//	 */
//	public List getUnDependTasks() {
//		return unDependTasks;
//	}
//
//	/**
//	 * @return Returns the leftTasks.
//	 */
//	public Long[] getLeftTasks() {
//		return leftTasks;
//	}
//
//	/**
//	 * @param leftTasks The leftTasks to set.
//	 */
//	public void setLeftTasks(Long[] leftTasks) {
//		this.leftTasks = leftTasks;
//	}
//
//	/**
//	 * @return Returns the rightTasks.
//	 */
//	public Long[] getRightTasks() {
//		return rightTasks;
//	}
//
//	/**
//	 * @param rightTasks The rightTasks to set.
//	 */
//	public void setRightTasks(Long[] rightTasks) {
//		this.rightTasks = rightTasks;
//	}
//
//	
//	
//	
	
	public Date getWorkDate() {
		return workDate;
	}
	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}
	
	
}
