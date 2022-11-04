package com.mingtech.application.autotask.taskService;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;

/**
 * 系统业务余额按月汇总
 *com.mingtech.application.autotask.taskService.BusiBalanceSumByMonthTask
 */
public class BusiBalanceSumByMonthTask extends AbstractAutoTask{
	private Logger logger = Logger.getLogger(PoolDayEndDueDateRePayServiceTask.class);

	@Override
	public BooleanAutoTaskResult run() throws Exception {
		try {
			BalanceOlapDataInphase dataInphase = BalanceOlapDataInphase.getInstance();
			dataInphase.isolateBalanceSumByMonth();     //余额按月汇总
		} catch (Exception e1) {
			logger.error("余额按月汇总异常："+e1.getMessage(),e1);
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

