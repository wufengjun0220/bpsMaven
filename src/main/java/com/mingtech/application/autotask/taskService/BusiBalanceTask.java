package com.mingtech.application.autotask.taskService;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;

/**
 * 系统业务余额 结转任务
 *com.mingtech.application.autotask.taskService.BusiBalanceTask
 */
public class BusiBalanceTask extends AbstractAutoTask{
	private Logger logger = Logger.getLogger(PoolDayEndDueDateRePayServiceTask.class);

	@Override
	public BooleanAutoTaskResult run() throws Exception {
		try {
			BalanceOlapDataInphase dataInphase = BalanceOlapDataInphase.getInstance();
			dataInphase.isolateBalanceListInphase();     //余额明细抽取
		} catch (Exception e1) {
			logger.error("余额结转异常："+e1.getMessage());
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

