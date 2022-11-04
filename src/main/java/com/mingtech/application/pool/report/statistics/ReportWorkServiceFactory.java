package com.mingtech.application.pool.report.statistics;

import com.mingtech.framework.common.util.SpringContextUtil;
/**
 * 报表服务工厂
 * @author Administrator
 *
 */
public class ReportWorkServiceFactory {
	/**
	 * 报表服务
	 * @return
	 */
	public static IReportWorkService getReportWorkService(){
		return (IReportWorkService)SpringContextUtil.getBean("reportWorkService");
	}
}
