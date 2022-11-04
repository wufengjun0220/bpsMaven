package com.mingtech.application.report.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mingtech.application.pool.report.service.PoolReportService;
import com.mingtech.application.pool.report.statistics.impl.ReportWorkService;
import com.mingtech.application.report.domain.ReportForm;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.application.sysmanage.domain.User;
import com.mingtech.framework.core.action.BaseAction;
import com.mingtech.framework.core.page.Page;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-25 下午04:19:49
 * @描述: [ReportFormAction]报表Action
 */
public class ReportFormAction extends BaseAction{

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ReportFormAction.class);
	
	private ReportForm reportForm = new ReportForm(); // 报表实体
	private PoolReportService poolReportService;
	private ReportWorkService reportWorkService;
	private String tableHtmlStr;
	private String queryFrom; // 判断查询请求来源于哪
	private List deptList = new ArrayList(); // 报表机构列表
	

	/**
	* <p>方法名称: reportQuery|描述: 转向报表查询页面</p>
	* @return
	*/
	public String reportQuery(){
		queryFrom = "";
		Department dept = this.getCurrentUser().getDepartment();
		try {
			deptList = poolReportService.getDeptList(dept);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(e.getMessage(),e);
		}
		return SUCCESS;
		
	}
	

	/**
	 * <p>方法名称: loadReportFromJson|描述: 查询报表JSON数据</p>
	 */
	public void loadReportFromJson(){
		Page page = this.getPage();
		User user = this.getCurrentUser();
		String json = "";
		try{
			if(StringUtils.equals(queryFrom, "button")){
				json = poolReportService.queryReportJSON(reportForm, page, user);
			}
		}catch (Exception e){
			logger.error(e.getMessage(),e);
			logger.error(e.toString());
		}
		sendJSON(json);
	}

	/**
	 * 创建xml数据
	 */
	public String submitXMLData(){
		try{
			//reportWorkService.startWork(reportForm.getStatisticEndDate());
			//reportWorkService.startWorkByDay(reportForm.getStatisticEndDate());
			reportWorkService.txSubmitXMLData(reportForm);
		}catch (Exception e){
			logger.error(e.getMessage(),e);
		}
		return SUCCESS;
	}

	public String viewReport(){
		try{
			reportForm = (ReportForm) poolReportService.load(reportForm.getId());
		}catch (Exception e){
			logger.error(e.getMessage(),e);
			logger.error(e.toString());
		}
		return SUCCESS;
	}

	/**
	 * <p>方法名称: exportTableToExcel|描述: 将表格导出EXCEL</p>
	 * @return
	 */
	public String exportTableToExcel(){
		if(StringUtils.isNotBlank(this.getTableHtmlStr())){
			try{
				this.getResponse().setContentType("application/msexcel");
				this.getResponse().setHeader("Content-Disposition","attachment; filename=" + URLEncoder.encode(reportForm.getName(),"UTF-8") + ".xls");
			}catch (UnsupportedEncodingException e){
				logger.error(e.getMessage(),e);
				logger.error(e.toString());
			}
		}
		return SUCCESS;
	}

	public ReportForm getReportForm(){
		return reportForm;
	}

	public void setReportForm(ReportForm reportForm){
		this.reportForm = reportForm;
	}


	public String getTableHtmlStr(){
		return tableHtmlStr;
	}

	public void setTableHtmlStr(String tableHtmlStr){
		this.tableHtmlStr = tableHtmlStr;
	}

	public String getQueryFrom(){
		return queryFrom;
	}

	public void setQueryFrom(String queryFrom){
		this.queryFrom = queryFrom;
	}
	
	public ReportWorkService getReportWorkService() {
		return reportWorkService;
	}

	public void setReportWorkService(ReportWorkService reportWorkService) {
		this.reportWorkService = reportWorkService;
	}

	public List getDeptList() {
		return deptList;
	}

	public void setDeptList(List deptList) {
		this.deptList = deptList;
	}


	public PoolReportService getPoolReportService() {
		return poolReportService;
	}

	public void setPoolReportService(PoolReportService poolReportService) {
		this.poolReportService = poolReportService;
	}
	
	
	
	
	
}
