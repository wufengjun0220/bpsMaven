package com.mingtech.application.autotask.taskService;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
/**
 * 
 *通过计算当天是否在代保管和票据池费用有效期内，修改费率和最低价
 *【原任务是开启的】
 * 0 0 5 ? * *
 * com.mingtech.application.autotask.taskService.PoolDayEndSynRiskBillServiceTask
 */
public class PoolDayEndSynRiskBillServiceTask extends AbstractAutoTask{

	@Override
	public BooleanAutoTaskResult run() throws Exception {
		//PoolCommonServiceFactory.getDayEndSynRiskBillService().doAction();
		return new BooleanAutoTaskResult(true);
	}

	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
