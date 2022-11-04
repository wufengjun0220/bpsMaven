package com.mingtech.application.pool.query.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 每日融资业务表：该表日终跑批时候生成数据，记录当日票据池所有客户融资业务数据
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-1
 */

public class PedCrdtDaily implements java.io.Serializable {

	private static final long serialVersionUID = 6172505808489764948L;
	private String id;           //主键ID    
	private String batchId;      //批次ID   
	private String bpsNo;        //票据池编号  
	private String bpsName;      //票据池名称  
	private String custNo;       //客户号    
	private String custName;     //客户名称   
	private Date createDate;     //创建日期   
	private String crdtType;     //融资业务类型    XD_01:银承   XD_02:流贷   XD_03:保函   XD_04:国内信用证
	private String crdtNo;       //合同号    
	private String loanNo;       //借据号    
	private BigDecimal loanAmt;      //业务金额   
	private BigDecimal loanBalance;  //业务余额   
	private String ccupy;        //占用额度比例 
	private String riskLevel;    //占用额度类型/风险类型   FX_01：低风险 FX_02：高风险
	private Date startDate;      //融资业务起始日
	private Date endDate;        //融资业务到期日
	private Date createTime;//创建时间   
    private String isOnline;//是否线上 1 是 0 否


	public String getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBatchId() {
		return this.batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getBpsNo() {
		return this.bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getBpsName() {
		return this.bpsName;
	}

	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
	}

	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCrdtType() {
		return this.crdtType;
	}

	public void setCrdtType(String crdtType) {
		this.crdtType = crdtType;
	}

	public String getCrdtNo() {
		return this.crdtNo;
	}

	public void setCrdtNo(String crdtNo) {
		this.crdtNo = crdtNo;
	}

	public String getLoanNo() {
		return this.loanNo;
	}

	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}

	public BigDecimal getLoanAmt() {
		return loanAmt;
	}

	public void setLoanAmt(BigDecimal loanAmt) {
		this.loanAmt = loanAmt;
	}

	public BigDecimal getLoanBalance() {
		return loanBalance;
	}

	public void setLoanBalance(BigDecimal loanBalance) {
		this.loanBalance = loanBalance;
	}

	public String getCcupy() {
		return ccupy;
	}

	public void setCcupy(String ccupy) {
		this.ccupy = ccupy;
	}


	public String getRiskLevel() {
		return this.riskLevel;
	}

	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}