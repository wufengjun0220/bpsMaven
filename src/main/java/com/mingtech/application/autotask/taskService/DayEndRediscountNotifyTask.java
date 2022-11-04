package com.mingtech.application.autotask.taskService;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;

/**
 * 买入返售 电票  提前指定日期    发消息提醒
 * 0 0 12 * * ?
 * 【原任务是开启的】
 * com.mingtech.application.autotask.taskService.DayEndRediscountNotifyTask
 */
public class DayEndRediscountNotifyTask extends AbstractAutoTask{

	@Override
	public BooleanAutoTaskResult run() throws Exception {
		//DayEndRediscountNotifyServiceFactory.getDayEndRediscountNotifyService().txAction();
		return new BooleanAutoTaskResult(true);
	}

	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}
	
}
