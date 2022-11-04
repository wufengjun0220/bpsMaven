package com.mingtech.application.pool.manage.domain;

import java.math.BigDecimal;

public class QueryPedListBean {
	private String bpsNo;//票据池编号
	private String custNo;//核心客户号
	private String custName;//客户名
	private String orgCoge;//组织机构代码
	private String custIdentity;//客户身份  KHLX_01:出质人  KHLX_02:融资人   KHLX_03:出质人+融资人  KHLX_04:签约成员
	private BigDecimal totalBillAmount = new BigDecimal("0");//池额度总计
	private BigDecimal highRiskAmount = new BigDecimal("0");//高风险额度
	private BigDecimal lowRiskAmount = new BigDecimal("0");//低风险额度
	private BigDecimal allMaxLimitAmt = new BigDecimal("0");//分配额度限额
	private BigDecimal allLimitAmt = new BigDecimal("0");//分配额度
	private BigDecimal usedLimitAmt = new BigDecimal("0");//已使用额度
	private BigDecimal remainAvailLimitAmt = new BigDecimal("0");//剩余可用额度
	public String getBpsNo() {
		return bpsNo;
	}
	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public BigDecimal getTotalBillAmount() {
		return totalBillAmount;
	}
	public void setTotalBillAmount(BigDecimal totalBillAmount) {
		this.totalBillAmount = totalBillAmount;
	}
	public BigDecimal getHighRiskAmount() {
		return highRiskAmount;
	}
	public void setHighRiskAmount(BigDecimal highRiskAmount) {
		this.highRiskAmount = highRiskAmount;
	}
	public BigDecimal getLowRiskAmount() {
		return lowRiskAmount;
	}
	public void setLowRiskAmount(BigDecimal lowRiskAmount) {
		this.lowRiskAmount = lowRiskAmount;
	}
	public BigDecimal getAllMaxLimitAmt() {
		return allMaxLimitAmt;
	}
	public void setAllMaxLimitAmt(BigDecimal allMaxLimitAmt) {
		this.allMaxLimitAmt = allMaxLimitAmt;
	}
	public BigDecimal getAllLimitAmt() {
		return allLimitAmt;
	}
	public void setAllLimitAmt(BigDecimal allLimitAmt) {
		this.allLimitAmt = allLimitAmt;
	}
	public BigDecimal getUsedLimitAmt() {
		return usedLimitAmt;
	}
	public void setUsedLimitAmt(BigDecimal usedLimitAmt) {
		this.usedLimitAmt = usedLimitAmt;
	}
	public BigDecimal getRemainAvailLimitAmt() {
		return remainAvailLimitAmt;
	}
	public void setRemainAvailLimitAmt(BigDecimal remainAvailLimitAmt) {
		this.remainAvailLimitAmt = remainAvailLimitAmt;
	}
	public String getCustIdentity() {
		return custIdentity;
	}
	public void setCustIdentity(String custIdentity) {
		this.custIdentity = custIdentity;
	}
	public String getOrgCoge() {
		return orgCoge;
	}
	public void setOrgCoge(String orgCoge) {
		this.orgCoge = orgCoge;
	}
		
	
}
