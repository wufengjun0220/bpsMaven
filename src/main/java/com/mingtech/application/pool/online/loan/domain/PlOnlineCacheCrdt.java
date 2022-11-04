package com.mingtech.application.pool.online.loan.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * PlOnlineCrdt entity. @author MyEclipse Persistence Tools
 */

public class PlOnlineCacheCrdt implements java.io.Serializable {

	// Fields

	private String id;
	private String batchNo;               //批次号
	private String onlineCrdtNo;          //在线协议编号
	private String bpsNo;                 //票据池编号
	private String loanNo;                //借据号
	private String custNo;                //客户号
	private String custName;              //客户名称
	private String baseRateType;          //基准利率类型
	private Date applyDate;               //申请日期
	private Date dueDate;                 //到期日期
	private BigDecimal loanAmt;           //借据金额
	private String deduAcctNo;            //扣款账号
	private String rateFloatType;         //正常利率浮动方式
	private BigDecimal rateFloatValue;    //正常利率浮动值
	private BigDecimal interestRate;      //执行利率
	private String overRateFloatType;     //逾期利率浮动方式
	private BigDecimal overRateFloatValue;//逾期利率浮动值
	private String contractNo;            //合同编号
	private String creditAcct;            //放款账号
	private String status;                //状态
	private String fundNo;                //圈存编号
	private String payType;               //支付类型 1自主支付 2 受托支付
	private String devSeqNo;              //核心记账第三方流水号
	private String dealStatus;            //处理状态
	private Date acctDate;                //核心记账日期
	private String acctFlow;              //核心记账流水
	private Date createTime;              //创建时间
	private Date updateTime;              //最近修改时间
	private String branchNo;              //管理机构
	                                        
	private String currency;              //币种  01-人民币
	private String depositAcctNo;         //票据池保证金账号
	private String transAccount;         //贷款账号--记录核心放款成功返回的贷款账号
	
	private String deductionProductNo; //贷款产品代码，该字段不落库，用于核心放款接口中的产品代码字段传递
	
	private BigDecimal poolCreditRatio;       //票据池额度占用比例（%）


	
	public BigDecimal getPoolCreditRatio() {
		return poolCreditRatio;
	}

	public void setPoolCreditRatio(BigDecimal poolCreditRatio) {
		this.poolCreditRatio = poolCreditRatio;
	}

	/** default constructor */
	public PlOnlineCacheCrdt() {
	}

	// Property accessors

	
	public String getId() {
		return this.id;
	}

	public String getDeductionProductNo() {
		return deductionProductNo;
	}

	public void setDeductionProductNo(String deductionProductNo) {
		this.deductionProductNo = deductionProductNo;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getLoanNo() {
		return this.loanNo;
	}

	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}

	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getBranchNo() {
		return this.branchNo;
	}

	public void setBranchNo(String branchNo) {
		this.branchNo = branchNo;
	}

	public String getBaseRateType() {
		return this.baseRateType;
	}

	public void setBaseRateType(String baseRateType) {
		this.baseRateType = baseRateType;
	}

	public String getCurrency() {
		return this.currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getApplyDate() {
		return this.applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public Date getDueDate() {
		return this.dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public BigDecimal getLoanAmt() {
		return this.loanAmt;
	}

	public void setLoanAmt(BigDecimal loanAmt) {
		this.loanAmt = loanAmt;
	}

	public String getDeduAcctNo() {
		return this.deduAcctNo;
	}

	public void setDeduAcctNo(String deduAcctNo) {
		this.deduAcctNo = deduAcctNo;
	}

	public String getRateFloatType() {
		return this.rateFloatType;
	}

	public void setRateFloatType(String rateFloatType) {
		this.rateFloatType = rateFloatType;
	}

	public BigDecimal getRateFloatValue() {
		return this.rateFloatValue;
	}

	public void setRateFloatValue(BigDecimal rateFloatValue) {
		this.rateFloatValue = rateFloatValue;
	}

	public BigDecimal getInterestRate() {
		return this.interestRate;
	}

	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}

	public String getOverRateFloatType() {
		return this.overRateFloatType;
	}

	public void setOverRateFloatType(String overRateFloatType) {
		this.overRateFloatType = overRateFloatType;
	}

	public BigDecimal getOverRateFloatValue() {
		return this.overRateFloatValue;
	}

	public void setOverRateFloatValue(BigDecimal overRateFloatValue) {
		this.overRateFloatValue = overRateFloatValue;
	}

	public String getContractNo() {
		return this.contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getCreditAcct() {
		return this.creditAcct;
	}

	public void setCreditAcct(String creditAcct) {
		this.creditAcct = creditAcct;
	}

	public String getDepositAcctNo() {
		return this.depositAcctNo;
	}

	public void setDepositAcctNo(String depositAcctNo) {
		this.depositAcctNo = depositAcctNo;
	}

	public Date getAcctDate() {
		return this.acctDate;
	}

	public void setAcctDate(Date acctDate) {
		this.acctDate = acctDate;
	}

	public String getAcctFlow() {
		return this.acctFlow;
	}

	public void setAcctFlow(String acctFlow) {
		this.acctFlow = acctFlow;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getFundNo() {
		return fundNo;
	}

	public void setFundNo(String fundNo) {
		this.fundNo = fundNo;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getDevSeqNo() {
		return devSeqNo;
	}

	public void setDevSeqNo(String devSeqNo) {
		this.devSeqNo = devSeqNo;
	}

	public String getDealStatus() {
		return dealStatus;
	}

	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getTransAccount() {
		return transAccount;
	}

	public void setTransAccount(String transAccount) {
		this.transAccount = transAccount;
	}


	
	
	

}