package com.mingtech.application.autotask.taskService;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;

/**
 *
 * 票据池到期还款日终任务
 * 0 0 18 ? * *
 *【原任务是 开启的】
 *com.mingtech.application.autotask.taskService.PoolDayEndDueDateRePayServiceTask
 */
public class PoolDayEndDueDateRePayServiceTask extends AbstractAutoTask{
	private Logger logger = Logger.getLogger(PoolDayEndDueDateRePayServiceTask.class);

	@Override
	public BooleanAutoTaskResult run() throws Exception {
		//PoolCommonServiceFactory.getDayEndDueDateRePayService().doAction();
		return new BooleanAutoTaskResult(true);
	}

	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}

}
