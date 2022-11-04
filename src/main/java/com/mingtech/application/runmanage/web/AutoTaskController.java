package com.mingtech.application.runmanage.web;

import java.util.List;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.AutoTaskServiceFactory;
import com.mingtech.application.autotask.core.autotask.IAutoTaskService;
import com.mingtech.application.autotask.core.domain.AutoTask;
import com.mingtech.application.pool.base.web.BaseController;
import com.mingtech.application.runmanage.service.RunStateService;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.common.util.JsonUtil;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.page.Page;

/**
 * 日终任务服务
 * com.mingtech.application.autotask.actions
 *
 */
@Controller
public class AutoTaskController  extends BaseController{
	@Autowired
	private RunStateService runStateService;
	private static final Logger logger = Logger.getLogger(AutoTaskController.class);
	
	/**
	 * 数据查询
	 */
	@RequestMapping(value="/queryAutoTaskListJoson",method = RequestMethod.POST)
	public void queryAutoTaskListJoson(){
		try{
			User user = this.getCurrentUser();
			List result = AutoTaskServiceFactory.getAutoTaskService().getAllAutoTasks();
			String json = JsonUtil.buildJson(result, result.size());
			this.sendJSON(StringUtil.isBlank(json) ? this.RESULT_EMPTY_DEFAULT
					: json);
		}catch (Exception e){
			logger.error("查询配置信息失败"+ e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询配置信息失败"+ e.getMessage());
		}
	}
	
	/**
	 * 查询任务运行日志
	 */
	@RequestMapping(value="/queryTaskExcLogJoson",method = RequestMethod.POST)
	public void queryTaskExcLogJoson(String taskId){
		try{
			Page page = this.getPage();
			List result = AutoTaskServiceFactory.getAutoTaskService().getExcuteTaskLogsById(taskId,page);
			String json = JsonUtil.buildJson(result, result.size());
			this.sendJSON(StringUtil.isBlank(json) ? this.RESULT_EMPTY_DEFAULT
					: json);
		}catch (Exception e){
			logger.error("查询任务运行日志失败"+ e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("查询任务运行日志失败"+ e.getMessage());
		}
	}
	/**
	 * 删除自动任务
	 */
	@RequestMapping(value="/deleteTask",method = RequestMethod.POST)
	public void deleteTask(String taskId) {
		//删除数据库中的任务
		try {
			IAutoTaskService service = AutoTaskServiceFactory.getAutoTaskService();
			AutoTask autoTask = service.getAutoTask(taskId);		
			AutoTaskServiceFactory.getAutoTaskService().txdelAutoTask(autoTask);
			this.sendJSON("{isOk:\"false\",mess:\"删除成功\"}");
		} catch (Exception e) {
			logger.error("删除失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("删除失败"+ e.getMessage());
		}
		
	}
	
	/**
	 * 执行任务
	 */
	@RequestMapping(value="/runTask",method = RequestMethod.POST)
	public void runTask(String taskId){
		User user = this.getCurrentUser();
		IAutoTaskService taskService = AutoTaskServiceFactory.getAutoTaskService();
		try {
			taskService.runTask(taskId);
			this.sendJSON("提交完成");
		} catch (Exception e) {
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("提交失败");
			logger.error("提交失败"+ e.getMessage(),e);
		}
	}
	
	
	/**
	 * 增加日终任务
	 */
	@RequestMapping(value="/addTask",method = RequestMethod.POST)
	public void addTask(AutoTask autoTask) throws Exception{
		User user = this.getCurrentUser();
		autoTask.setId(null);
		autoTask.setExecStatus(autoTask.EXEC_STATUS_0);
		String className=autoTask.getClassName();
		try {
			Class clazz = Class.forName(className);
			if (!(clazz.newInstance() instanceof AbstractAutoTask)){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("执行类不正确");				
				return ;
			}
			if(!CronExpression.isValidExpression(autoTask.getCronExpr())){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("时间表达式格式不正确");
				return ;
			}
			AutoTaskServiceFactory.getAutoTaskService().txaddAutoTask(autoTask);
			
			this.sendJSON("添加成功");	
		} catch (Exception e) {
			logger.error("该执行类未继承自动任务基类"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("该执行类未继承自动任务基类"+e.getMessage());
		}
		
	}
	
	/**
	 * 更新日终任务
	 */
	@RequestMapping(value="/updateTask",method = RequestMethod.POST)
	public void updateTask(AutoTask autoTask) throws Exception{
		String className=autoTask.getClassName();
		User user = this.getCurrentUser();
		try {
			Class clazz = Class.forName(className);
			if (!(clazz.newInstance() instanceof AbstractAutoTask)){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("执行类不正确");
				return;
			}
			if(!CronExpression.isValidExpression(autoTask.getCronExpr())){
				this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
				this.sendJSON("时间表达式格式不正确");
				return;
			}
			AutoTaskServiceFactory.getAutoTaskService().txaddAutoTask(autoTask);
			this.sendJSON("更改成功");
		} catch (Exception e) {
			logger.error("更改失败"+e.getMessage(),e);
			this.getResponse().setStatus(HttpStatus.SC_BAD_REQUEST);
			this.sendJSON("更改失败"+e.getMessage());
		}

		
	}
	
}
	
