package com.mingtech.application.autotask.taskService;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;

/**
 * 
 * 票据池结清释放额度日终任务
 * 
 * 0 0 5 ? * *
 * com.mingtech.application.autotask.taskService.PoolReleaseCreditTask
 * 【原任务就是 注释掉的】
 */
public class PoolReleaseCreditTask extends AbstractAutoTask{
	@Override
	public BooleanAutoTaskResult run() throws Exception {
		//PoolCommonServiceFactory.getDayEndPoolService().doAction();
		return new BooleanAutoTaskResult(true);
	}

	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}

}
