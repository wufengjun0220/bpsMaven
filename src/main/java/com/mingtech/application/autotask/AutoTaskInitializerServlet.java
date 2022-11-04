package com.mingtech.application.autotask;


import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;
import org.quartz.ee.servlet.QuartzInitializerServlet;

import com.mingtech.application.autotask.core.AutoTaskServiceFactory;
import com.mingtech.application.autotask.core.ScheduleHelper;
import com.mingtech.application.autotask.core.domain.AutoTask;
/**
 * 配置到web.xml里面，在服务启动时  加载所有配置的日终任务使用；
 * 日终任务主类
 * 根据配置文件autoTask表  加载所有的  日终任务到   调度任务中
 *com.mingtech.application.autotask.AutoTaskInitializerServlet
 */
public class AutoTaskInitializerServlet extends QuartzInitializerServlet{
		
	/* (non-Javadoc)
	 * @see org.quartz.ee.servlet.QuartzInitializerServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {		
		super.init(config);
		
		Logger log = Logger.getLogger(AutoTaskInitializerServlet.class);
		log.info("/***loading AutoTasks.************/");		
		log.info("**********加载日终任务开始******");
		//将任务注册到  
		Iterator it = AutoTaskServiceFactory.getAutoTaskService().getAllAutoTasks().iterator();
		ScheduleHelper helper=new ScheduleHelper();
		while (it.hasNext()){
			AutoTask task = (AutoTask)it.next();
			//任务打开  并且 是自动执行任务
			if (AutoTask.STATUS_OPEN.equals(task.getStatus())
					&&AutoTask.EXEC_MODULE_AUTO.equals(task.getExecModule())){
				try {
					log.info("**********加载日终任务******"+task.getName());
					helper.startJob(task);
				} catch (Exception e) {
					log.error(""+task.getName()+" 启动异常"+e.getMessage());
				}
				log.info(""+task.getName()+" is opened.");
			}else{
				log.info("**********当前任务为手工任务或者已关闭任务*****"+task.getName()+"***没有进行自动加载!");
				log.warn(task.getName()+" is closing.");
			}
		}
		log.info("**********加载日终任务结束******");
		log.info("/***AutoTasks have loaded.********/");
		
	}
}
