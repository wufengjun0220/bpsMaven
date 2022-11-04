package com.mingtech.application.autotask.taskService;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;

/**
 * 系统业务余额 按日汇总任务
 *com.mingtech.application.autotask.taskService.BusiBalanceSumByDayTask
 */
public class BusiBalanceSumByDayTask extends AbstractAutoTask{
	private Logger logger = Logger.getLogger(PoolDayEndDueDateRePayServiceTask.class);

	@Override
	public BooleanAutoTaskResult run() throws Exception {
		try {
			BalanceOlapDataInphase dataInphase = BalanceOlapDataInphase.getInstance();
			dataInphase.isolateBalanceSumByDay();     //余额按日汇总
		} catch (Exception e1) {
			logger.error("余额按日汇总异常："+e1.getMessage(),e1);
			e1.printStackTrace();
			throw new Exception(e1.getMessage());
		}
		return new BooleanAutoTaskResult(true);
	}

	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}
	
	
}

