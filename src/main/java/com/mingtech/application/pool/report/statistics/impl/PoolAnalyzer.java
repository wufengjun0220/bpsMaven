package com.mingtech.application.pool.report.statistics.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.report.common.ReportUtils;
import com.mingtech.application.report.domain.AnalysisResult;
import com.mingtech.application.report.domain.PropertyObject;
import com.mingtech.application.report.domain.SearchResult;
import com.mingtech.application.report.domain.StatisticsResult;
import com.mingtech.application.report.statistics.IAnalyzer;
import com.mingtech.application.sysmanage.domain.Department;
import com.mingtech.framework.core.service.impl.GenericServiceImpl;


/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者: pengdaochang
 * @日期: 2010-8-19 下午06:20:01
 * @描述: [AcceptionBalanceAnalyzer]承兑分析Service实现
 */
@Service("iAnalyzer")
public class PoolAnalyzer extends GenericServiceImpl implements IAnalyzer{

	
	public void analysis(StatisticsResult statisticsResult, Department dept){
		List list = statisticsResult.getSearchResultList();
		String deptId = null;
		if(dept != null){
			deptId = dept.getId(); // 机构ID
		}
		if(!list.isEmpty()){
			int size = list.size();
			Map analyzerMap = statisticsResult.getAnalysisResultMap();
			for(int i = 0; i < size; i++){
				SearchResult sr = (SearchResult) list.get(i);
				Map expandingProperty = sr.getExpandingProperty();
				if(!expandingProperty.isEmpty()){
					String billType = ((PropertyObject) expandingProperty.get(ReportUtils.BILL_TYPE_CODE)).getPropertyValue();
					if(StringUtils.equals(billType, PublicStaticDefineTab.BILL_TYPE_BANK)){ // 银承
						this.addAccptrNm(expandingProperty, analyzerMap, deptId); // 存储出票人开户行名称
						
						BigDecimal isseAmt = this.addIsseAmt(sr, analyzerMap, expandingProperty, deptId); // 同一个开户行的银承当期余额
						this.addTotalIsseAmt(isseAmt,analyzerMap,deptId); // 所有开户行的当期余额合计
						
						BigDecimal cashDepositIsseAmt = this.addCashDepositIsseAmt(sr, analyzerMap, expandingProperty, deptId); // 同一个开户行的保证金余额
						this.addTotalCashDepositIsseAmt(cashDepositIsseAmt,analyzerMap, deptId); // 所有开户行的保证金余额合计
						
						BigDecimal depositReceiptIsseAmt = this.addDepositReceiptIsseAmt(sr, analyzerMap, expandingProperty, deptId); // 同一个开户行的存单质押余额
						this.addTotalDepositReceiptIsseAmt(depositReceiptIsseAmt,analyzerMap, deptId); // 所有开户行的存单质押余额合计
						
						BigDecimal otherIsseAmt = this.addOtherIsseAmt(sr, analyzerMap, expandingProperty, deptId); // 同一个开户行的其他权利余额
						this.addTotalOtherIsseAmt(otherIsseAmt,analyzerMap, deptId); // 所有开户行的其他权利余额合计
						
						BigDecimal lineOfCreditIsseAmt = this.addLineOfCreditIsseAmt(sr, analyzerMap, expandingProperty, deptId); // 同一个开户行的敞口余额
						this.addTotalLineOfCreditIsseAmt(lineOfCreditIsseAmt,analyzerMap, deptId); // 所有开户行的敞口余额合计
					}
				}
			}
			statisticsResult.setAnalysisResultMap(analyzerMap);
			
		}
		
	}

	
	/**
	* <p>方法名称: addAccptrNm|描述: 存储出票人开户行名称</p>
	* @param expandingProperty
	* @param map
	* @param deptId
	*/
	private void addAccptrNm(Map expandingProperty, Map map, String deptId){
		String name = ((PropertyObject)expandingProperty.get(ReportUtils.ISSUERBANKNAMEKEY)).getPropertyValue();
		AnalysisResult bankName = null;
		String key = ReportUtils.CASHDEPOSIT + "-" + ReportUtils.BANKNAMEKEY + "-" + name;
		
		if(!map.containsKey(key)){
			bankName = new AnalysisResult();
			bankName.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+name);
			bankName.setStatisticsNum(1);
			bankName.setOtherValue(name);
			bankName.setValueType(ReportUtils.STRING);
			bankName.setDeptId(deptId);
		}else{
			bankName = (AnalysisResult) map.get(key);
			int i = bankName.getStatisticsNum();
			bankName.setStatisticsNum(++i);
		}
		map.put(key, bankName);
		
	}
	
	/**
	* <p>方法名称: addIsseAmt|描述: 计算同一个开户行下的当期余额</p>
	* @param sr
	* @param map
	* @return
	*/
	private BigDecimal addIsseAmt(SearchResult sr, Map map, Map expandingProperty, String deptId){
		String name = ((PropertyObject)expandingProperty.get(ReportUtils.ISSUERBANKNAMEKEY)).getPropertyValue();
		AnalysisResult bankNameIsseAmt = null;
		String key = ReportUtils.CASHDEPOSIT+"-" + ReportUtils.ISSEAMTKEY+"-"+name;
		if(!map.containsKey(key)){
			bankNameIsseAmt = new AnalysisResult();
			bankNameIsseAmt.setStatisticsClassification(ReportUtils.ISSEAMTKEY);
			bankNameIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+name);
			bankNameIsseAmt.setStatisticsAmount(sr.getIsseAmt());
			bankNameIsseAmt.setStatisticsNum(1);
			bankNameIsseAmt.setDeptId(deptId);
		}else{
			bankNameIsseAmt = (AnalysisResult) map.get(key);
			BigDecimal isseAmt = bankNameIsseAmt.getStatisticsAmount();
			bankNameIsseAmt.setStatisticsAmount(isseAmt.add(sr.getIsseAmt()));
			int i = bankNameIsseAmt.getStatisticsNum();
			bankNameIsseAmt.setStatisticsNum(++i);
		}
		map.put(key, bankNameIsseAmt);
		return sr.getIsseAmt(); 
		
	}
	
	/**
	* <p>方法名称: addTotalIsseAmt|描述: 计算当期余额的合计</p>
	* @param bd
	* @param map
	* @param deptId
	*/
	private void addTotalIsseAmt(BigDecimal bd,Map map, String deptId){
		AnalysisResult totalIsseAmt = null;
		String key = ReportUtils.CASHDEPOSIT+"-" + ReportUtils.TOTAL+"-"+ReportUtils.ISSEAMTKEY;
		if(!map.containsKey(key)){
			totalIsseAmt = new AnalysisResult();
			totalIsseAmt.setStatisticsClassification(ReportUtils.ISSEAMTKEY);
			totalIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+ReportUtils.TOTAL);
			totalIsseAmt.setStatisticsAmount(bd);
			totalIsseAmt.setStatisticsNum(1);
			totalIsseAmt.setDeptId(deptId);
		}else{
			totalIsseAmt = (AnalysisResult) map.get(key);
			BigDecimal isseAmt = totalIsseAmt.getStatisticsAmount();
			totalIsseAmt.setStatisticsAmount(isseAmt.add(bd));
			int i = totalIsseAmt.getStatisticsNum();
			totalIsseAmt.setStatisticsNum(++i);
		}
		map.put(key, totalIsseAmt);
		
	}

	/**
	* <p>方法名称: addCashDepositIsseAmt|描述: 计算同一个开户行下的保证金余额</p>
	* @param sr
	* @param map
	* @return
	*/
	private BigDecimal addCashDepositIsseAmt(SearchResult sr, Map map, Map expandingProperty, String deptId){
		String name = ((PropertyObject)expandingProperty.get(ReportUtils.ISSUERBANKNAMEKEY)).getPropertyValue();
		AnalysisResult cashDepositIsseAmt = null;
		String key = ReportUtils.CASHDEPOSIT+"-" + ReportUtils.CASHDEPOSITISSEAMTKEY+"-"+name;
		Map propertyValueMap = sr.getExpandingProperty();
		PropertyObject cashDeposit = (PropertyObject) propertyValueMap.get(ReportUtils.CASHDEPOSITISSEAMTKEY);
		if(cashDeposit != null){
			BigDecimal tmp = new BigDecimal(0);
			if(StringUtils.isNotBlank(cashDeposit.getPropertyValue())){
				tmp = new BigDecimal(cashDeposit.getPropertyValue());
			}
			if(!map.containsKey(key)){
				cashDepositIsseAmt = new AnalysisResult();
				cashDepositIsseAmt.setStatisticsClassification(ReportUtils.CASHDEPOSITISSEAMTKEY);
				cashDepositIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+name);
				cashDepositIsseAmt.setStatisticsAmount(tmp);
				cashDepositIsseAmt.setStatisticsNum(1);
				cashDepositIsseAmt.setDeptId(deptId);
			}else{
				cashDepositIsseAmt = (AnalysisResult) map.get(key);
				BigDecimal isseAmt = cashDepositIsseAmt.getStatisticsAmount();
				cashDepositIsseAmt.setStatisticsAmount(isseAmt.add(tmp));
				int i = cashDepositIsseAmt.getStatisticsNum();
				cashDepositIsseAmt.setStatisticsNum(++i);
			}
			map.put(key, cashDepositIsseAmt);
			return tmp; 
		}else{
			cashDepositIsseAmt = new AnalysisResult();
			cashDepositIsseAmt.setStatisticsClassification(ReportUtils.CASHDEPOSITISSEAMTKEY);
			cashDepositIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+name);
			cashDepositIsseAmt.setStatisticsAmount(new BigDecimal(0));
			cashDepositIsseAmt.setStatisticsNum(0);
			cashDepositIsseAmt.setDeptId(deptId);
			map.put(key, cashDepositIsseAmt);
			return new BigDecimal(0);
		}

	}
	

	/**
	* <p>方法名称: addTotalCashDepositIsseAmt|描述: 计算保证金余额的合计</p>
	* @param bd
	* @param map
	*/
	private void addTotalCashDepositIsseAmt(BigDecimal bd,Map map, String deptId){
		AnalysisResult totalIsseAmt = null;
		String key = ReportUtils.CASHDEPOSIT+"-" + ReportUtils.TOTAL+"-"+ReportUtils.CASHDEPOSITISSEAMTKEY;
		if(!map.containsKey(key)){
			totalIsseAmt = new AnalysisResult();
			totalIsseAmt.setStatisticsClassification(ReportUtils.CASHDEPOSITISSEAMTKEY);
			totalIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+ReportUtils.TOTAL);
			totalIsseAmt.setStatisticsAmount(bd);
			totalIsseAmt.setStatisticsNum(1);
			totalIsseAmt.setDeptId(deptId);
		}else{
			totalIsseAmt = (AnalysisResult) map.get(key);
			BigDecimal isseAmt = totalIsseAmt.getStatisticsAmount();
			totalIsseAmt.setStatisticsAmount(isseAmt.add(bd));
			int i = totalIsseAmt.getStatisticsNum();
			totalIsseAmt.setStatisticsNum(++i);
		}
		map.put(key, totalIsseAmt);
		
	}
	
	
	/**
	* <p>方法名称: addDepositReceiptIsseAmt|描述: 计算同一个开户行下的存单质押余额</p>
	* @param sr
	* @param map
	* @return
	*/
	private BigDecimal addDepositReceiptIsseAmt(SearchResult sr, Map map, Map expandingProperty, String deptId){
		String name = ((PropertyObject)expandingProperty.get(ReportUtils.ISSUERBANKNAMEKEY)).getPropertyValue();
		AnalysisResult depositReceiptIsseAmt = null;
		String key = ReportUtils.CASHDEPOSIT+"-" + ReportUtils.DEPOSITRECEIPTISSEAMTKEY+"-"+name;
		Map propertyValueMap = sr.getExpandingProperty();
		PropertyObject cashDeposit = (PropertyObject) propertyValueMap.get(ReportUtils.DEPOSITRECEIPTISSEAMTKEY);
		if(cashDeposit != null){
			BigDecimal tmp = new BigDecimal(0);
			if(StringUtils.isNotBlank(cashDeposit.getPropertyValue())){
				tmp = new BigDecimal(cashDeposit.getPropertyValue());
			}
			if(!map.containsKey(key)){
				depositReceiptIsseAmt = new AnalysisResult();
				depositReceiptIsseAmt.setStatisticsClassification(ReportUtils.DEPOSITRECEIPTISSEAMTKEY);
				depositReceiptIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+name);
				depositReceiptIsseAmt.setStatisticsAmount(tmp);
				depositReceiptIsseAmt.setStatisticsNum(1);
				depositReceiptIsseAmt.setDeptId(deptId);
			}else{
				depositReceiptIsseAmt = (AnalysisResult) map.get(key);
				BigDecimal isseAmt = depositReceiptIsseAmt.getStatisticsAmount();
				depositReceiptIsseAmt.setStatisticsAmount(isseAmt.add(tmp));
				int i = depositReceiptIsseAmt.getStatisticsNum();
				depositReceiptIsseAmt.setStatisticsNum(++i);
			}
			map.put(key, depositReceiptIsseAmt);
			return tmp;
		}else{
			depositReceiptIsseAmt = new AnalysisResult();
			depositReceiptIsseAmt.setStatisticsClassification(ReportUtils.DEPOSITRECEIPTISSEAMTKEY);
			depositReceiptIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+name);
			depositReceiptIsseAmt.setStatisticsAmount(new BigDecimal(0));
			depositReceiptIsseAmt.setStatisticsNum(0);
			depositReceiptIsseAmt.setDeptId(deptId);
			map.put(key, depositReceiptIsseAmt);
			return new BigDecimal(0);
		}
		
	}
	
	/**
	* <p>方法名称: addTotalDepositReceiptIsseAmt|描述: 计算存单质押余额的合计</p>
	* @param bd
	* @param map
	*/
	private void addTotalDepositReceiptIsseAmt(BigDecimal bd,Map map, String deptId){
		AnalysisResult totalIsseAmt = null;
		String key = ReportUtils.CASHDEPOSIT+"-" + ReportUtils.TOTAL+"-"+ReportUtils.DEPOSITRECEIPTISSEAMTKEY;
		if(!map.containsKey(key)){
			totalIsseAmt = new AnalysisResult();
			totalIsseAmt.setStatisticsClassification(ReportUtils.DEPOSITRECEIPTISSEAMTKEY);
			totalIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+ReportUtils.TOTAL);
			totalIsseAmt.setStatisticsAmount(bd);
			totalIsseAmt.setStatisticsNum(1);
			totalIsseAmt.setDeptId(deptId);
		}else{
			totalIsseAmt = (AnalysisResult) map.get(key);
			BigDecimal isseAmt = totalIsseAmt.getStatisticsAmount();
			totalIsseAmt.setStatisticsAmount(isseAmt.add(bd));
			int i = totalIsseAmt.getStatisticsNum();
			totalIsseAmt.setStatisticsNum(++i);
		}
		map.put(key, totalIsseAmt);
		
	}
	
	/**
	* <p>方法名称: addOtherIsseAmt|描述: 计算同一个开户行下的其他权利余额</p>
	* @param sr
	* @param map
	* @return
	*/
	private BigDecimal addOtherIsseAmt(SearchResult sr, Map map ,Map expandingProperty, String deptId){
		String name = ((PropertyObject)expandingProperty.get(ReportUtils.ISSUERBANKNAMEKEY)).getPropertyValue();
		AnalysisResult otherIsseAmt = null;
		String key = ReportUtils.CASHDEPOSIT+"-" + ReportUtils.OTHERISSEAMTKEY+"-"+name;
		Map propertyValueMap = sr.getExpandingProperty();
		PropertyObject cashDeposit = (PropertyObject) propertyValueMap.get(ReportUtils.OTHERISSEAMTKEY);
		if(cashDeposit != null){
			BigDecimal tmp = new BigDecimal(0);
			if(StringUtils.isNotBlank(cashDeposit.getPropertyValue())){
				tmp = new BigDecimal(cashDeposit.getPropertyValue());
			}
			if(!map.containsKey(key)){
				otherIsseAmt = new AnalysisResult();
				otherIsseAmt.setStatisticsClassification(ReportUtils.OTHERISSEAMTKEY);
				otherIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+name);
				otherIsseAmt.setStatisticsAmount(tmp);
				otherIsseAmt.setStatisticsNum(1);
				otherIsseAmt.setDeptId(deptId);
			}else{
				otherIsseAmt = (AnalysisResult) map.get(key);
				BigDecimal isseAmt = otherIsseAmt.getStatisticsAmount();
				otherIsseAmt.setStatisticsAmount(isseAmt.add(tmp));
				int i = otherIsseAmt.getStatisticsNum();
				otherIsseAmt.setStatisticsNum(++i);
			}
			map.put(key, otherIsseAmt);
			return tmp;
		}else{
			otherIsseAmt = new AnalysisResult();
			otherIsseAmt.setStatisticsClassification(ReportUtils.OTHERISSEAMTKEY);
			otherIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+name);
			otherIsseAmt.setStatisticsAmount(new BigDecimal(0));
			otherIsseAmt.setStatisticsNum(0);
			otherIsseAmt.setDeptId(deptId);
			map.put(key, otherIsseAmt);
			return new BigDecimal(0);
		}
		
	}
	
	/**
	* <p>方法名称: addTotalOtherIsseAmt|描述: 计算其他权利余额的合计</p>
	* @param bd
	* @param map
	*/
	private void addTotalOtherIsseAmt(BigDecimal bd,Map map, String deptId){
		AnalysisResult totalIsseAmt = null;
		String key = ReportUtils.CASHDEPOSIT+"-" +ReportUtils.TOTAL+"-"+ReportUtils.OTHERISSEAMTKEY;
		if(!map.containsKey(key)){
			totalIsseAmt = new AnalysisResult();
			totalIsseAmt.setStatisticsClassification(ReportUtils.OTHERISSEAMTKEY);
			totalIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+ReportUtils.TOTAL);
			totalIsseAmt.setStatisticsAmount(bd);
			totalIsseAmt.setStatisticsNum(1);
			totalIsseAmt.setDeptId(deptId);
		}else{
			totalIsseAmt = (AnalysisResult) map.get(key);
			BigDecimal isseAmt = totalIsseAmt.getStatisticsAmount();
			totalIsseAmt.setStatisticsAmount(isseAmt.add(bd));
			int i = totalIsseAmt.getStatisticsNum();
			totalIsseAmt.setStatisticsNum(++i);
		}
		map.put(key, totalIsseAmt);
		
	}
	
	/**
	* <p>方法名称: addLineOfCreditIsseAmt|描述: 计算同一个开户行下的敞口余额</p>
	* @param sr
	* @param map
	* @return
	*/
	private BigDecimal addLineOfCreditIsseAmt(SearchResult sr, Map map, Map expandingProperty, String deptId){
		String name = ((PropertyObject)expandingProperty.get(ReportUtils.ISSUERBANKNAMEKEY)).getPropertyValue();
		AnalysisResult otherIsseAmt = null;
		String key = ReportUtils.CASHDEPOSIT+"-" + ReportUtils.LINEOFCREDITISSEAMTKEY+"-"+name;
		Map propertyValueMap = sr.getExpandingProperty();
		PropertyObject cashDeposit = (PropertyObject) propertyValueMap.get(ReportUtils.LINEOFCREDITISSEAMTKEY);
		if(cashDeposit != null){
			BigDecimal tmp = new BigDecimal(0);
			if(StringUtils.isNotBlank(cashDeposit.getPropertyValue())){
				tmp = new BigDecimal(cashDeposit.getPropertyValue());
			}
			if(!map.containsKey(key)){
				otherIsseAmt = new AnalysisResult();
				otherIsseAmt.setStatisticsClassification(ReportUtils.LINEOFCREDITISSEAMTKEY);
				otherIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+name);
				otherIsseAmt.setStatisticsAmount(tmp);
				otherIsseAmt.setStatisticsNum(1);
				otherIsseAmt.setDeptId(deptId);
			}else{
				otherIsseAmt = (AnalysisResult) map.get(key);
				BigDecimal isseAmt = otherIsseAmt.getStatisticsAmount();
				otherIsseAmt.setStatisticsAmount(isseAmt.add(tmp));
				int i = otherIsseAmt.getStatisticsNum();
				otherIsseAmt.setStatisticsNum(++i);
			}
			map.put(key, otherIsseAmt);
			return tmp;
		}else{
			otherIsseAmt = new AnalysisResult();
			otherIsseAmt.setStatisticsClassification(ReportUtils.LINEOFCREDITISSEAMTKEY);
			otherIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+name);
			otherIsseAmt.setStatisticsAmount(new BigDecimal(0));
			otherIsseAmt.setStatisticsNum(0);
			otherIsseAmt.setDeptId(deptId);
			map.put(key, otherIsseAmt);
			return new BigDecimal(0);
		}
		
		
	}
	
	/**
	* <p>方法名称: addTotalLineOfCreditIsseAmt|描述: 计算敞口余额的合计</p>
	* @param bd
	* @param map
	*/
	private void addTotalLineOfCreditIsseAmt(BigDecimal bd,Map map, String deptId){
		AnalysisResult totalIsseAmt = null;
		String key = ReportUtils.CASHDEPOSIT+"-" + ReportUtils.TOTAL+"-"+ReportUtils.LINEOFCREDITISSEAMTKEY;
		if(!map.containsKey(key)){
			totalIsseAmt = new AnalysisResult();
			totalIsseAmt.setStatisticsClassification(ReportUtils.LINEOFCREDITISSEAMTKEY);
			totalIsseAmt.setStatisticsRange(ReportUtils.CASHDEPOSIT+"-"+ReportUtils.TOTAL);
			totalIsseAmt.setStatisticsAmount(bd);
			totalIsseAmt.setStatisticsNum(1);
			totalIsseAmt.setDeptId(deptId);
		}else{
			totalIsseAmt = (AnalysisResult) map.get(key);
			BigDecimal isseAmt = totalIsseAmt.getStatisticsAmount();
			totalIsseAmt.setStatisticsAmount(isseAmt.add(bd));
			int i = totalIsseAmt.getStatisticsNum();
			totalIsseAmt.setStatisticsNum(++i);
		}
		map.put(key, totalIsseAmt);
		
	}
	
	
	public Class getEntityClass(){
		return null;
	}

	public String getEntityName(){
		return null;
	}
	
}
