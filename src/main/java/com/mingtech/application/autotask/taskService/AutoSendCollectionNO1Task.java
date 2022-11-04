package com.mingtech.application.autotask.taskService;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.ecds.draftcollection.service.ConsignServiceFactory;

/**
 * ****** 第一步，获取到期票据发起提示付款申请。*****************************
 * 
 * @author yangyawei
 * 配置为 每日上午8点自动执行 
 * 0 0 8 ? * *
 * com.mingtech.application.autotask.taskService.AutoSendCollectionApplyTask
 */
public class AutoSendCollectionNO1Task  extends AbstractAutoTask {

	@Override
	public BooleanAutoTaskResult run() throws Exception {
		//调用具体的  提示服务申请服务
		ConsignServiceFactory.getConsignService().autoCollectionSendNO1Task();
		return new BooleanAutoTaskResult(true);
	}

	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}

	
}
