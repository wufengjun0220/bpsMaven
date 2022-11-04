package com.mingtech.application.autotask.taskService;

import org.apache.log4j.Logger;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.pool.common.service.BlackListManageService;
import com.mingtech.application.pool.common.service.PoolCommonServiceFactory;
import com.mingtech.application.pool.draft.service.DraftPoolInService;

/**
 * ****第1步**** 获取已签自动质押业务客户票据信息，并发起质押申请。
 * 
 * @author yangyawei
 *         com.mingtech.application.autotask.taskService.AutoSendCollectionApplyTask
 */

public class AutoPoolInNO1Task extends AbstractAutoTask {
	private static final Logger logger = Logger.getLogger(AutoPoolInNO1Task.class);
	BlackListManageService blackListManageService = PoolCommonServiceFactory.getBlackListManageService();
	DraftPoolInService draftPoolInService = PoolCommonServiceFactory.getDraftPoolInService();
	
	
	
	public BooleanAutoTaskResult run() throws Exception {
			
		logger.info("自动入池自动任务执行开始...");

		/*
		 * 1.自动入池落库任务
		 * 
		 * （1）查询所有签自动质押入池的单户及集团成员的电票签约账号
		 * （2）根据电票签约账号查询BBSP持有票据
		 * （3）进行黑名单及风险校验后落库
		 */
		draftPoolInService.txQueryAllBillFromBbsp(true);
		
		/*
		 * 2.向信贷系统进行额度校验，校验额度不足的，不产生额度
		 
		blackListManageService.txMisCreditCheck(draftPoolInService.queryCheckBills(true));
		
		
		 * 3.自动入池的票据自动入池
		 
		draftPoolInService.txAutoInPool();*/
		
		
		
		
		return new BooleanAutoTaskResult(true);
	}


	
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}
	
}
