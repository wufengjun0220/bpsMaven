package com.mingtech.application.pool.online.loan.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * PlCrdtPayPlanHist entity. @author MyEclipse Persistence Tools
 */

public class PlCrdtPayPlanHist implements java.io.Serializable {

	private static final long serialVersionUID = -3241936320143629300L;
	private String id;
	private String planId;
	private String crdtId;
	private String serialNo;
	private String contractNo;
	private String loanNo;
	private String onlineCrdtNo;
	private String bpsNo;
	private String loanAcctNo;
	private String loanAcctName;
	private String deduAcctNo;
	private String deduAcctName;
	private String deduBankCode;
	private String deduBankName;
	private String isLocal;
	private BigDecimal totalAmt;
	private BigDecimal usedAmt;
	private BigDecimal repayAmt;
	private String status;
	private String operatorType;
	private String appNo;
	private Date createDate;
	private BigDecimal poolCreditRatio;       //票据池额度占用比例（%）
	
	
	

	public BigDecimal getPoolCreditRatio() {
		return poolCreditRatio;
	}

	public void setPoolCreditRatio(BigDecimal poolCreditRatio) {
		this.poolCreditRatio = poolCreditRatio;
	}

	public PlCrdtPayPlanHist() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlanId() {
		return this.planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getCrdtId() {
		return this.crdtId;
	}

	public void setCrdtId(String crdtId) {
		this.crdtId = crdtId;
	}

	public String getSerialNo() {
		return this.serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getContractNo() {
		return this.contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getLoanNo() {
		return this.loanNo;
	}

	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}

	public String getOnlineCrdtNo() {
		return this.onlineCrdtNo;
	}

	public void setOnlineCrdtNo(String onlineCrdtNo) {
		this.onlineCrdtNo = onlineCrdtNo;
	}

	public String getBpsNo() {
		return this.bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getLoanAcctNo() {
		return this.loanAcctNo;
	}

	public void setLoanAcctNo(String loanAcctNo) {
		this.loanAcctNo = loanAcctNo;
	}

	public String getLoanAcctName() {
		return this.loanAcctName;
	}

	public void setLoanAcctName(String loanAcctName) {
		this.loanAcctName = loanAcctName;
	}

	public String getDeduAcctNo() {
		return this.deduAcctNo;
	}

	public void setDeduAcctNo(String deduAcctNo) {
		this.deduAcctNo = deduAcctNo;
	}

	public String getDeduAcctName() {
		return this.deduAcctName;
	}

	public void setDeduAcctName(String deduAcctName) {
		this.deduAcctName = deduAcctName;
	}

	public String getDeduBankCode() {
		return this.deduBankCode;
	}

	public void setDeduBankCode(String deduBankCode) {
		this.deduBankCode = deduBankCode;
	}

	public String getDeduBankName() {
		return this.deduBankName;
	}

	public void setDeduBankName(String deduBankName) {
		this.deduBankName = deduBankName;
	}

	public String getIsLocal() {
		return this.isLocal;
	}

	public void setIsLocal(String isLocal) {
		this.isLocal = isLocal;
	}

	public BigDecimal getTotalAmt() {
		return this.totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	public BigDecimal getUsedAmt() {
		return this.usedAmt;
	}

	public void setUsedAmt(BigDecimal usedAmt) {
		this.usedAmt = usedAmt;
	}

	public BigDecimal getRepayAmt() {
		return this.repayAmt;
	}

	public void setRepayAmt(BigDecimal repayAmt) {
		this.repayAmt = repayAmt;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOperatorType() {
		return this.operatorType;
	}

	public void setOperatorType(String operatorType) {
		this.operatorType = operatorType;
	}

	public String getAppNo() {
		return this.appNo;
	}

	public void setAppNo(String appNo) {
		this.appNo = appNo;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

}