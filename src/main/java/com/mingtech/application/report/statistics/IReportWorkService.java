package com.mingtech.application.report.statistics;

import com.mingtech.application.report.domain.ReportForm;

public interface IReportWorkService {
	/**
	 * 生成报表数据
	 */
	public void txSubmitXMLData(ReportForm reportForm) throws Exception;
	
	/**
	 * 每天2:00轮询一次，每日生成报表
	 */
	public boolean getReportByDay() throws Exception;
}
