package com.mingtech.application.autotask.taskService;

import java.util.Date;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.ScheduleHelper;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.runmanage.service.RunStateServiceFactory;
import com.mingtech.framework.common.util.DateUtils;

/**
 * 计提摊销日终任务处理类；
 * 1、抽取业务计提摊销数据
 * 2、进行计提摊销记账
 * com.mingtech.application.autotask.taskService.ProvisionAutoTask
 */
public class ProvisionAutoTask extends AbstractAutoTask{
	private static final Logger logger = Logger.getLogger(ProvisionAutoTask.class);

	public BooleanAutoTaskResult run() throws Exception {
		BooleanAutoTaskResult rest = new  BooleanAutoTaskResult(true);
		Date todayDate = RunStateServiceFactory.getRunStateService().getSysRunState().getWorkDate(); 
		//
//		Date busiDate = DateUtils.modDay(todayDate, -1);
		try{
			//计提抽取数据
			this.txProvisionManage(todayDate);
			//
			//第二步:计提摊销记账
			//JiTiServiceFactory.getJiTiService().txAccountStart(todayDate);	
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			rest.setSuccess(false);
			rest.setInfo("计提摊销任务失败："+e.getMessage());
		}
		return rest;
	}
	/**
	 * 分场景  进行计提摊销数据  处理
	 * @param busiDate
	 * @throws Exception
	 */
	public void txProvisionManage(Date busiDate) throws Exception {
		String busiDateYYYY_MM_DD = DateUtils.dtuGetDatTimFmt(busiDate);
	}
	public String getJobName() throws Exception {
		return "计提摊销日终任务";
	}
 

	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public BooleanAutoTaskResult rollBack() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
}
