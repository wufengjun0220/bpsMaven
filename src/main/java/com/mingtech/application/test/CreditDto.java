package com.mingtech.application.test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 额度计算处理实体
 * @author Ju Nana
 * @version v1.0
 * @date 2021-5-6
 */
public class CreditDto {
	Date startDate;//起始日
	Date endDate;//截止日
	BigDecimal lowRiskIn = BigDecimal.ZERO;//低风险现金流入
	BigDecimal lowRiskOut = BigDecimal.ZERO;//低风险现金流出
	BigDecimal lowRiskCashFlow = BigDecimal.ZERO;//低风险时点现金流
	BigDecimal lowRiskCredit = BigDecimal.ZERO;//低风险可用额度
	BigDecimal highRiskIn = BigDecimal.ZERO;//高风险现金流入
	BigDecimal highRiskOut = BigDecimal.ZERO;//高风险现金流出
	BigDecimal highRiskCashFlow = BigDecimal.ZERO;//高风险时点现金流
	BigDecimal highRiskCredit= BigDecimal.ZERO;//高风险可用额度
	BigDecimal allCredit= BigDecimal.ZERO;//全部可用额度
	
	List list1 = new ArrayList();//备用list1
	List list2 = new ArrayList();//备用list2
	
	
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public BigDecimal getLowRiskIn() {
		return lowRiskIn;
	}
	public void setLowRiskIn(BigDecimal lowRiskIn) {
		this.lowRiskIn = lowRiskIn;
	}
	public BigDecimal getLowRiskOut() {
		return lowRiskOut;
	}
	public void setLowRiskOut(BigDecimal lowRiskOut) {
		this.lowRiskOut = lowRiskOut;
	}
	public BigDecimal getLowRiskCashFlow() {
		return lowRiskCashFlow;
	}
	public void setLowRiskCashFlow(BigDecimal lowRiskCashFlow) {
		this.lowRiskCashFlow = lowRiskCashFlow;
	}
	public BigDecimal getLowRiskCredit() {
		return lowRiskCredit;
	}
	public void setLowRiskCredit(BigDecimal lowRiskCredit) {
		this.lowRiskCredit = lowRiskCredit;
	}
	public BigDecimal getHighRiskIn() {
		return highRiskIn;
	}
	public void setHighRiskIn(BigDecimal highRiskIn) {
		this.highRiskIn = highRiskIn;
	}
	public BigDecimal getHighRiskOut() {
		return highRiskOut;
	}
	public void setHighRiskOut(BigDecimal highRiskOut) {
		this.highRiskOut = highRiskOut;
	}
	public BigDecimal getHighRiskCashFlow() {
		return highRiskCashFlow;
	}
	public void setHighRiskCashFlow(BigDecimal highRiskCashFlow) {
		this.highRiskCashFlow = highRiskCashFlow;
	}
	public BigDecimal getHighRiskCredit() {
		return highRiskCredit;
	}
	public void setHighRiskCredit(BigDecimal highRiskCredit) {
		this.highRiskCredit = highRiskCredit;
	}
	public BigDecimal getAllCredit() {
		allCredit = this.getLowRiskCredit().add(this.getHighRiskCredit());
		return allCredit;
	}
	public void setAllCredit(BigDecimal allCredit) {
		this.allCredit = allCredit;
	}
	public List getList1() {
		return list1;
	}
	public void setList1(List list1) {
		this.list1 = list1;
	}
	public List getList2() {
		return list2;
	}
	public void setList2(List list2) {
		this.list2 = list2;
	}
	
	
	

}
