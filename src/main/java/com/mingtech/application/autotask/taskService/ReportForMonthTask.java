package com.mingtech.application.autotask.taskService;

import com.mingtech.application.autotask.core.AbstractAutoTask;
import com.mingtech.application.autotask.core.domain.BooleanAutoTaskResult;
import com.mingtech.application.report.statistics.ReportWorkServiceFactory;

/**
 * 月报表生成服务，每月的 1号开始执行
 * 0 0 1 1 * ? *
 * 【原任务开启】
 * com.mingtech.application.autotask.taskService.ReportForMonthTask
 */
public class ReportForMonthTask  extends AbstractAutoTask{

	@Override
	public BooleanAutoTaskResult run() throws Exception {
		ReportWorkServiceFactory.getReportWorkService().getReportByDay();
		return new BooleanAutoTaskResult(true);
	}

	@Override
	public BooleanAutoTaskResult rollBack() throws Exception {
		return null;
	}
	
}
