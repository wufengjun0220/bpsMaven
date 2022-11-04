package com.mingtech.application.report.statistics.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import com.mingtech.application.report.common.ReportUtils;
import com.mingtech.application.report.domain.StatisticsResult;
import com.mingtech.application.report.statistics.IStatisticsProcessor;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.ProjectConfig;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

public class IntoProcessService extends GenericServiceImpl{

	private static final Logger logger = Logger.getLogger(IntoProcessService.class);
	private List processList;
	private List processList1;
	
	public List getProcessList() {
		return processList;
	}

	public void setProcessList(List processList) {
		this.processList = processList;
	}
	
	/**
	 * 轮询process程序
	 * @param startDate
	 * @param endDate
	 * @throws Exception
	 */
	public void txProcess(Date startDate,Date endDate) throws Exception{
		
		if(processList1 != null){
			String bankLevel = ProjectConfig.getInstance().getBankLevel(); // 报表级别
			int size = processList1.size();
			String hql = "select dept from Department dept where dept.level = :level ";
			List keyList = new ArrayList(); // 要查询的字段列表
			List valueList = new ArrayList(); // 要查询的值列表
			List deptList = new ArrayList();
			// 总行级别
			logger.info("开始查询、分析月报表！  开始时间： " + DateUtils.toString(new Date(), "yyyy年MM月dd日 HH时mm分ss秒"));
			for(int i = 0; i < size; i++){
				IStatisticsProcessor process = (IStatisticsProcessor)processList1.get(i);
				process.statistics("", startDate, endDate, null, bankLevel);
			}
			StatisticsResult statisticsResult = new StatisticsResult();
			statisticsResult.setStatisticsType(ReportUtils.getYearAndMonth(endDate)); // 统计业务类型
			statisticsResult.setStatisticsStartDate(startDate);
			statisticsResult.setStatisticsEndDate(endDate);
			this.txStore(statisticsResult);
			
		}
		
	}
	
	/**
	 * 轮询process程序
	 * @param startDate
	 * @param endDate
	 * @throws Exception
	 */
	public void txProcessByDay(Date startDate,Date endDate) throws Exception{
		
		if(processList != null){
			String bankLevel = ProjectConfig.getInstance().getBankLevel(); // 报表级别
			int size = processList.size();
			String hql = "select dept from Department dept where dept.level = :level ";
			List keyList = new ArrayList(); // 要查询的字段列表
			List valueList = new ArrayList(); // 要查询的值列表
			List deptList = new ArrayList();
			String curdate = DateUtils.toString(startDate, "yyyyMMdd");
			for(int i = 0; i < size; i++){
				IStatisticsProcessor process = (IStatisticsProcessor)processList.get(i);
				process.statistics("", startDate, endDate, null, bankLevel);
			}
			StatisticsResult statisticsResult = new StatisticsResult();
			statisticsResult.setStatisticsType(ReportUtils.getYearAndMonth(endDate)); // 统计业务类型
			statisticsResult.setStatisticsStartDate(startDate);
			statisticsResult.setStatisticsEndDate(endDate);
			this.txStore(statisticsResult);
			
		}
		
	}
	
	/**
	* <p>方法名称: statisticsByDept|描述: 查询、分析数据</p>
	* @param startDate 开始日期
	* @param endDate 结束日期
	* @param deptList 机构对象列表
	* @param bankLevel 报表级别
	* @throws Exception
	*/
	private void statisticsByDept(Date startDate,Date endDate, List deptList, String bankLevel) throws Exception{
		int deptSize = deptList.size();
		Department dept = null;
		int size = processList.size();
		for(int i = 0; i < deptSize; i++){
			dept = (Department) deptList.get(i);
			for(int j = 0; j < size; j++){
				IStatisticsProcessor process = (IStatisticsProcessor)processList.get(j);
				process.statistics("", startDate, endDate, dept, bankLevel);
			}
		}
	}

	public Class getEntityClass() {
		return null;
	}

	public String getEntityName() {
		return null;
	}

	public List getProcessList1() {
		return processList1;
	}

	public void setProcessList1(List processList1) {
		this.processList1 = processList1;
	}

}
