package com.mingtech.application.pool.online.acception.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * PlOnlineAcptBatch entity. @author MyEclipse Persistence Tools
 */

public class PlOnlineAcptCacheBatch implements java.io.Serializable {

	// Fields

	private String id;                        //主键ID
	private String batchNo;                   //批次号
	private String bpsNo;                     //票据池编号
	private String contractNo;                //在线银承合同号
	private String onlineAcptNo;              //在线银承协议编号
	private BigDecimal totalAmt;              //票据总面额
	private String applyBankNo;               //申请人开户行行号
	private String applyBankName;             //申请人开户行行名
	private String applyName;                 //申请人名称
	private String applyAcct;                 //申请人账号
	private String custNo;                    //申请人核心客户号
	private Date applyDate;                   //申请日期
	private BigDecimal feeRate;               //手续费率（%）
	private BigDecimal poolCreditRatio;       //票据池额度占用比例（%）
	private BigDecimal depositRatio;          //保证金比例（%）
	private String depositRateLevel;          //保证金计息方式
	private String depositAcctNo;             //扣收保证金账号
	private String depositAcctName;           //扣收保证金账户名称
	private String depositRateFloatFlag;      //保证金利率浮动标志
	private BigDecimal depositRateFloatValue; //保证金利率浮动值
	private String inAcctBranchNo;            //入账机构所号
	private String inAcctBranchName;          //入账机构名称
	private String elctrncSign;               //电子签名
	private String manageerNo;                //客户经理编号
	private String manageerName;              //客户经理名称
	private String status;                    //状态
	private Date createTime;                  //创建时间
	private Date updateTime;                  //最近修改时间
	private String dealStatus;    //业务处理状态   DS_001未处理  DS_002处理中  DS_003处理完成  DS_004已撤销  DS_005失败

	private String applyOrgcode;              //申请人组织机构代码
	private String branchNo;                  //机构id
	private String isCredit;                  //信贷额度是否占用  0否  1是
	private String operationType;             //操作类型
	private String openFlag;                  //敞口标志
	

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBatchNo() {
		return this.batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getContractNo() {
		return this.contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getOnlineAcptNo() {
		return this.onlineAcptNo;
	}

	public void setOnlineAcptNo(String onlineAcptNo) {
		this.onlineAcptNo = onlineAcptNo;
	}

	public String getBranchNo() {
		return this.branchNo;
	}

	public void setBranchNo(String branchNo) {
		this.branchNo = branchNo;
	}

	public BigDecimal getTotalAmt() {
		return this.totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	public String getApplyBankNo() {
		return this.applyBankNo;
	}

	public void setApplyBankNo(String applyBankNo) {
		this.applyBankNo = applyBankNo;
	}

	public String getApplyBankName() {
		return this.applyBankName;
	}

	public void setApplyBankName(String applyBankName) {
		this.applyBankName = applyBankName;
	}

	public String getApplyName() {
		return this.applyName;
	}

	public void setApplyName(String applyName) {
		this.applyName = applyName;
	}

	public String getApplyAcct() {
		return this.applyAcct;
	}

	public void setApplyAcct(String applyAcct) {
		this.applyAcct = applyAcct;
	}

	public String getApplyOrgcode() {
		return this.applyOrgcode;
	}

	public void setApplyOrgcode(String applyOrgcode) {
		this.applyOrgcode = applyOrgcode;
	}

	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public Date getApplyDate() {
		return this.applyDate;
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public BigDecimal getFeeRate() {
		return this.feeRate;
	}

	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
	}

	public BigDecimal getPoolCreditRatio() {
		return this.poolCreditRatio;
	}

	public void setPoolCreditRatio(BigDecimal poolCreditRatio) {
		this.poolCreditRatio = poolCreditRatio;
	}

	public BigDecimal getDepositRatio() {
		return this.depositRatio;
	}

	public void setDepositRatio(BigDecimal depositRatio) {
		this.depositRatio = depositRatio;
	}

	public String getDepositRateLevel() {
		return this.depositRateLevel;
	}

	public void setDepositRateLevel(String depositRateLevel) {
		this.depositRateLevel = depositRateLevel;
	}

	public String getDepositAcctNo() {
		return this.depositAcctNo;
	}

	public void setDepositAcctNo(String depositAcctNo) {
		this.depositAcctNo = depositAcctNo;
	}

	public String getDepositAcctName() {
		return this.depositAcctName;
	}

	public void setDepositAcctName(String depositAcctName) {
		this.depositAcctName = depositAcctName;
	}

	public String getDepositRateFloatFlag() {
		return this.depositRateFloatFlag;
	}

	public void setDepositRateFloatFlag(String depositRateFloatFlag) {
		this.depositRateFloatFlag = depositRateFloatFlag;
	}

	public BigDecimal getDepositRateFloatValue() {
		return this.depositRateFloatValue;
	}

	public void setDepositRateFloatValue(BigDecimal depositRateFloatValue) {
		this.depositRateFloatValue = depositRateFloatValue;
	}

	public String getInAcctBranchNo() {
		return this.inAcctBranchNo;
	}

	public void setInAcctBranchNo(String inAcctBranchNo) {
		this.inAcctBranchNo = inAcctBranchNo;
	}

	public String getInAcctBranchName() {
		return this.inAcctBranchName;
	}

	public void setInAcctBranchName(String inAcctBranchName) {
		this.inAcctBranchName = inAcctBranchName;
	}

	public String getOperationType() {
		return this.operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getOpenFlag() {
		return this.openFlag;
	}

	public void setOpenFlag(String openFlag) {
		this.openFlag = openFlag;
	}

	public String getElctrncSign() {
		return this.elctrncSign;
	}

	public void setElctrncSign(String elctrncSign) {
		this.elctrncSign = elctrncSign;
	}

	public String getManageerNo() {
		return this.manageerNo;
	}

	public void setManageerNo(String manageerNo) {
		this.manageerNo = manageerNo;
	}

	public String getManageerName() {
		return this.manageerName;
	}

	public void setManageerName(String manageerName) {
		this.manageerName = manageerName;
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

	public String getIsCredit() {
		return isCredit;
	}

	public void setIsCredit(String isCredit) {
		this.isCredit = isCredit;
	}

	public String getDealStatus() {
		return dealStatus;
	}

	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}

	public String getBpsNo() {
		return bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}
	
	

}