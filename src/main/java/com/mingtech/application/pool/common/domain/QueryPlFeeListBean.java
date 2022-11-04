package com.mingtech.application.pool.common.domain;

import java.math.BigDecimal;

public class QueryPlFeeListBean  {
	
	private String bpsNo;//票据池编号
	private String bpsName;//票据池名称
	private String custName;//成员单位
	private String SBillNo;//票号
	private String SBillType;//票据类型
	private BigDecimal  FBillAmount;//票面金额
	public String getBpsNo() {
		return bpsNo;
	}
	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}
	public String getBpsName() {
		return bpsName;
	}
	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getSBillNo() {
		return SBillNo;
	}
	public void setSBillNo(String sBillNo) {
		SBillNo = sBillNo;
	}
	public String getSBillType() {
		return SBillType;
	}
	public void setSBillType(String sBillType) {
		SBillType = sBillType;
	}
	public BigDecimal getFBillAmount() {
		return FBillAmount;
	}
	public void setFBillAmount(BigDecimal fBillAmount) {
		FBillAmount = fBillAmount;
	}
	
	
}
