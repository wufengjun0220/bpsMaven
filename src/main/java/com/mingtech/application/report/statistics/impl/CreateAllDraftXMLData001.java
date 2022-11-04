package com.mingtech.application.report.statistics.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mingtech.application.report.common.ReportUtils;
import com.mingtech.application.report.domain.AnalysisResult;
import com.mingtech.application.report.domain.ReportForm;
import com.mingtech.application.report.statistics.ICreateXML;
import com.mingtech.application.runmanage.domain.RunState;
import com.mingtech.application.runmanage.service.RunStateService;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.common.util.DateUtils;
import com.mingtech.framework.common.util.StringUtil;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: shenyang
 * @日期: 2010-10-09 下午03:54:36
 * @描述: [CreateAllDraftXMLData001]生成银行承兑汇票相关数据统计表2XMLService实现
 */
public class CreateAllDraftXMLData001 extends GenericServiceImpl
implements ICreateXML{

	private RunStateService runStateService;
	
	public RunStateService getRunStateService() {
		return runStateService;
	}

	public void setRunStateService(RunStateService runStateService) {
		this.runStateService = runStateService;
	}

	public ReportForm createXML(Date startDate, Date endDate, Department dept) {
		
		RunState rs = runStateService.getSysRunState();
		String bankName = "";
		String deptId = null;
		if(dept != null){
			bankName = dept.getName();
			deptId = dept.getId(); // 机构ID
		}else{
			bankName = rs.getBankName();
		}
		//取本月年月字符串，如："2010年09月"
		String thisMonth = ReportUtils.getYearAndMonth(startDate);
		String partten = "yyyy年MM月dd日";
		StringBuffer xmlStr = new StringBuffer();
		xmlStr.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xmlStr.append("<table name=\"" + bankName + "\" date=\"" +  DateUtils.toString(endDate, partten)  + "\" unit=\"" + ReportUtils.TENTHOUSAND + "\" >");

		
		String[][] str = {{ReportUtils.ACCEPTIONBUSINESS},{ReportUtils.DISCOUNTBUSINESS,ReportUtils.REDISCOUNTOUTER_BUY_Repurchase,ReportUtils.REDISCOUNTINNER_BUY_Repurchase},{ReportUtils.DISCOUNTBUSINESS},{ReportUtils.REDISCOUNTOUTER_BUY_Repurchase,ReportUtils.REDISCOUNTINNER_BUY_Repurchase},{ReportUtils.REDISCOUNTTOPEOPLEBANK}}; 
		for(int i = 0;i < str.length;i++){
			String[] cells = str[i];
			String[] thisYeKeys = new String[cells.length];//本月余额key
			String[] thisAmountKeys = new String[cells.length];//本月发生额key
			String[] thisBsKeys = new String[cells.length];//本月笔数key
			String[] totalamtByYearKeys = new String[cells.length];//本月年累计发生额key
			for(int j = 0;j < cells.length;j++){
				thisYeKeys[j] = ReportUtils.BANKACCEPTBILLDATASTATIS +  "-" + ReportUtils.PAPERYBILL + "-" + cells[j] + "-" + ReportUtils.THISMONTH + "_" +thisMonth + "-" + ReportUtils.BALANCE; 
				thisAmountKeys[j] = ReportUtils.BANKACCEPTBILLDATASTATIS + "-" + ReportUtils.PAPERYBILL + "-" + cells[j] + "-" + ReportUtils.THISMONTH + "_" +thisMonth + "-" + ReportUtils.AMOUNT;; 
				thisBsKeys[j] = ReportUtils.BANKACCEPTBILLDATASTATIS + "-" + ReportUtils.PAPERYBILL + "-" + cells[j] + "-" + ReportUtils.THISMONTH + "_" +thisMonth + "-" + ReportUtils.BUSINESSSIZE;
				totalamtByYearKeys[j] = ReportUtils.BANKACCEPTBILLDATASTATIS  + "-" + ReportUtils.PAPERYBILL + "-" + cells[j]  + "-" + ReportUtils.THISMONTH + "_" +thisMonth + "-" + ReportUtils.TOTALAMTBYYEAR;
			}
			String[] rowStr = {createCell(1,"Y",thisYeKeys,deptId),createCell(2,"Y",thisAmountKeys,deptId),createCell(3,"N",thisBsKeys,deptId),createCell(4,"Y",totalamtByYearKeys,deptId)};
			xmlStr.append(createRow(i+1,rowStr));	
		}
		xmlStr.append("</table>");
		
		ReportForm form = new ReportForm();
		form.setName(thisMonth+rs.getBankName()+"银行承兑汇票相关数据统计表2");//表名
		form.setFillInDate(new Date());//填表日期
		form.setPrincipal("");//负责人 
		form.setTabulator("");//制表人
		form.setTabulatorPhone("");//制表人电话
		form.setStatus("未生产"); // 报表状态
		form.setStatisticBeginDate(startDate); // 报表统计开始日期
		form.setStatisticEndDate(endDate); // 报表统计结束日期
		form.setFormType(ReportUtils.MONTH); // 报表类型    月度/季度/年度/其他
		form.setFormData(xmlStr.toString()); // 报表数据  XML格式
		form.setFormContent(""); // 报表内容  HTML格式e
		form.setCreateTime(rs.getCurDateTime()); // 报表生成时间
		form.setFormBusType("001") ; //报表业务类型
		form.setDraftStuff(ReportUtils.ALLDRAFTSTUFF);//票据介质类型（纸票、电票）
		form.setDeptId(deptId); // 机构ID
		return form;
	}

	/**
	 * 获取行数据xml字符串
	 * @param rowNumber
	 * @param cells
	 * @return
	 */
	public String createRow(int rowNumber,String[] cells){
		StringBuffer sb = new StringBuffer();
		sb.append("<row rownm=\"" + rowNumber + "\">");
		for(int i = 0;i < cells.length;i++){
			sb.append(cells[i]);
		}
		sb.append("</row>");
		return sb.toString();
	}
	
	/**
	 * 拼取单元格的xml字符串
	 * @param colNumber 列数
	 * @param sfJE 是否金额值 Y,N
	 * @param keys
	 * @return
	 */
	public String createCell(int colNumber,String sfJE,String[] keys,String deptId){
		StringBuffer sb = new StringBuffer();
		sb.append("<cell colnm=\"" + colNumber + "\" sfje=\"" + sfJE + "\">");
		sb.append("<value>");
		BigDecimal amount = new BigDecimal(0);
		for(int i = 0;i < keys.length;i++){
			amount = amount.add(getAllCellData(keys[i],deptId));
		}
		sb.append(amount);
		sb.append("</value>");
		sb.append("</cell>");
		return sb.toString();
	}
	
	/**
	 * 根据纸票key统计纸票和电票的值之和
	 * @param key
	 * @return
	 */
	public BigDecimal getAllCellData(String key,String deptId){
		BigDecimal paperyValue = this.getCellData(key,deptId);
		String electroniclKey = key.replaceAll(ReportUtils.PAPERYBILL + "-", "");
		BigDecimal electroniclValue = this.getCellData(electroniclKey,deptId);
		return paperyValue.add(electroniclValue);
	}
	
	/**
	 * 根据key在分析器中取出相关数据
	 * @param key
	 * @return
	 */
	public BigDecimal getCellData(String key,String deptId){
		String hql = "select ar from AnalysisResult ar where ar.analysisResultMapKey = ? ";
		if(StringUtils.isNotBlank(deptId)){
			hql += "and ar.deptId = ? ";
		}else{
			hql += "and ar.deptId is null ";
		}
		List params = new ArrayList();
		params.add(key);
		if(StringUtils.isNotBlank(deptId)){
			params.add(deptId);
		}
		BigDecimal retValue = new BigDecimal(0);
		List list = this.find(hql,params);
		if(list != null && list.size() > 0){
			AnalysisResult analysisResult = (AnalysisResult) list.get(0);
			String type = analysisResult.getValueType();
			if(StringUtil.isEmpty(type)){
				retValue = analysisResult.getStatisticsAmount();
			}
			else{
				retValue = new BigDecimal(analysisResult.getOtherValue());
			}
		}
		return retValue;
	}
	
	public Class getEntityClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityName() {
		// TODO Auto-generated method stub
		return null;
	}

	public ReportForm createXML(Date currDate, Department dept) {
		// TODO Auto-generated method stub
		return null;
	}

}
