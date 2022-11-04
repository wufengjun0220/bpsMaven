package com.mingtech.application.report.statistics.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.mingtech.application.report.common.ReportUtils;
import com.mingtech.application.report.domain.ReportForm;
import com.mingtech.application.report.statistics.IReportWorkService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.common.jdbcHelper.DBUtil;
import com.mingtech.framework.common.jdbcHelper.ISqlHandler;
import com.mingtech.framework.common.jdbcHelper.SqlHandler;
import com.mingtech.framework.common.util.ConnectionUtils;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

public class ReportWorkService extends GenericServiceImpl implements IReportWorkService{
	
	private static final Logger logger = Logger.getLogger(ReportWorkService.class);
	private IntoProcessService intoProcessService;
	private CreateXMLDateManagerService createXMLDateByDayManagerService;
	//数据库连接
	private Connection conn;

	public Class getEntityClass() {
		return null;
	}

	public String getEntityName() {
		return null;
	}

	/**
	 * 每天2:00轮询一次，每日生成报表
	 */
	public boolean  getReportByDay() throws Exception{
		Date currDate = DateUtils.getWorkDayDate();
		logger.info("轮询时间是：" + DateUtils.toString(currDate, "yyyy年MM月dd日 HH时mm分ss秒"));
		startWorkByDay(currDate);
		return true;
		
	}
	
	public void startWorkByDay(Date currDate) throws Exception{
		
		logger.info("开始生成报表！  开始时间： " + DateUtils.toString(new Date(), "yyyy年MM月dd日 HH时mm分ss秒"));
		Date thisDate = currDate;
		String hql1 = "select sr from StatisticsResult sr where sr.statisticsEndDate = ? and sr.statisticsFrequency = '"+ReportUtils.DAY+"'";
		List paras = new ArrayList();
		paras.add(thisDate);
		List list1 = this.find(hql1,paras);
		if(list1.isEmpty()){//循环执行process 生成分析数据
			intoProcessService.txProcessByDay(thisDate, thisDate);
		}
		String hql2 = "select rf from ReportForm rf where rf.statisticEndDate = ? and rf.formType = '日度'";
		List list2 = this.find(hql2,paras);
		if(list2.isEmpty()){//循环执行日度报表xml生成数据器
			createXMLDateByDayManagerService.txCreateXml(thisDate, thisDate);
		}
		logger.info("成功生成报表！结束时间：" + DateUtils.toString(new Date(), "yyyy年MM月dd日 HH时mm分ss秒"));
	}

	/**
	 * <P>方法名：submitXMLData|描述：生成报表数据</P>
	 * @author bjm
	 * @date 2017-10-19
	 * @param reportForm
	 */
	public void txSubmitXMLData(ReportForm reportForm) throws Exception{
		
		//清理三张统计分析表
		String StatSql = "truncate table R_StatisticsResult";
		String SearSql = "truncate table R_SEARCHRESULT";
		String AnalSql = "truncate table R_ANALYSISRESULT";
		this.updateData(StatSql);
		this.updateData(SearSql);
		this.updateData(AnalSql);
		
		Department dept = new Department();
		dept.setId("flag");
		dept.setName("HBYH");
		logger.info("开始生成报表！  开始时间： " + DateUtils.toString(new Date(), "yyyy年MM月dd日 HH时mm分ss秒"));
		String hql1 = "select sr from StatisticsResult sr where sr.statisticsStartDate = ? and sr.statisticsEndDate = ?";
		List paras = new ArrayList();
		paras.add(reportForm.getStatisticBeginDate());
		paras.add(reportForm.getStatisticEndDate());
		List list1 = new ArrayList();
		if(!"pjywtz01".equals(reportForm.getFormBusType())&&!"pjywtz02".equals(reportForm.getFormBusType())){
			list1 = this.find(hql1,paras);
		}
		if(list1.isEmpty()){//循环执行process 生成分析数据
			intoProcessService.txProcess(reportForm.getStatisticBeginDate(), reportForm.getStatisticEndDate());
		}
		
		String hql2 = "select rf from ReportForm rf where rf.statisticBeginDate = ? and rf.statisticEndDate = ? and rf.formBusType = ?";
		paras.add(reportForm.getFormBusType());
		List list2 = this.find(hql2,paras);
		if(list2.isEmpty()){//循环执行月度报表xml生成数据器
			ReportForm form = new ReportForm();
			if(reportForm.getFormBusType().equals("pj002")){
			}else if(reportForm.getFormBusType().equals("pj004")){
			}else if(reportForm.getFormBusType().equals("pj005")){
			}else if(reportForm.getFormBusType().equals("pj009")){
			}else if(reportForm.getFormBusType().equals("pjywtz01")){
			}else if(reportForm.getFormBusType().equals("pjywtz02")){
			}else if(reportForm.getFormBusType().equals("pj013")){
			}
			this.txStore(form);
		}
		logger.info("成功生成报表！结束时间：" + DateUtils.toString(new Date(), "yyyy年MM月dd日 HH时mm分ss秒"));
	}
	
	/***
	 * 执行传入的sql语句
	 * @param sql
	 * @return
	 */	
	public boolean updateData(String sql)throws Exception{
		Connection conn=null;
		boolean returnValue=false;
		try{
			conn=this.getConn();
			ISqlHandler ish=new SqlHandler(conn);
			returnValue= ish.executeUpdate(sql);
			DBUtil.commitConn(conn);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			DBUtil.rollbackConn(conn);
			throw new Exception(e.getMessage());
		}finally{
			DBUtil.closeConn(conn);
		}
		return returnValue;
	}
	
	
	/*****
	 * 获取数据库连接，利用jdbc的方式
	 * @return
	 * @throws Exception
	 */
	public Connection getConn() throws Exception{
		if(conn==null || this.conn.isClosed()){			
			conn=ConnectionUtils.getConn();			
		}
		return conn;
	}
	


	public IntoProcessService getIntoProcessService() {
		return intoProcessService;
	}

	public void setIntoProcessService(IntoProcessService intoProcessService) {
		this.intoProcessService = intoProcessService;
	}

	public CreateXMLDateManagerService getCreateXMLDateByDayManagerService() {
		return createXMLDateByDayManagerService;
	}

	public void setCreateXMLDateByDayManagerService(
			CreateXMLDateManagerService createXMLDateByDayManagerService) {
		this.createXMLDateByDayManagerService = createXMLDateByDayManagerService;
	}
	
}
