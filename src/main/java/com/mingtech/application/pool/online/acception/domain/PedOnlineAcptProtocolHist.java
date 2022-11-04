package com.mingtech.application.pool.online.acception.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;

/**
 * PedOnlineAcptProtocolHist entity. @author MyEclipse Persistence Tools
 */

public class PedOnlineAcptProtocolHist implements java.io.Serializable {

	// Fields

	private String id;
	private String custName;                  //客户名称
	private String protocolStatus;
	private String onlineAcptNo;
	private String bpsNo;
	private String baseCreditNo;
	private String ebkCustNo;
	private BigDecimal onlineAcptTotal;
	private BigDecimal usedAmt;
	private String acceptorBankNo;
	private String acceptorBankName;
	private String depositAcctNo;
	private String depositAcctName;
	private String depositRateLevel;
	private String depositRateFloatFlag;
	private BigDecimal depositRateFloatValue;
	private BigDecimal depositRatio;
	private BigDecimal poolCreditRatio;
	private BigDecimal feeRate;
	private String inAcctBranchNo;
	private String inAcctBranchName;
	private String contractNo;
	private String appName;
	private String appNo;
	private String signBranchNo;
	private String signBranchName;
	private Date changeDate;
	private Date dueDate;
	private String modeMark;//历史信息的唯一标记  
	private String modeContent;
	private String lastSourceId;//上一次修改的id
	private Date createTime;
	private Date updateTime;
	private String guarantorNo;               //担保人核心客户号

	//页面展示
	private String protocolStatusDesc;
	private String depositRateLevelDesc;          //保证金利率档次中文
	private String depositRateFloatFlagDesc;      //保证金利率浮动标志中文
	// Constructors

	/** default constructor */
	public PedOnlineAcptProtocolHist() {
	}
	public String getDepositRateLevelDesc() {
		if("0".equals(this.depositRateLevel)){
			return "活期";
		}else if("1".equals(this.depositRateLevel)){
			return "定期";
		}
		return depositRateLevel;
	}


	public void setDepositRateLevelDesc(String depositRateLevelDesc) {
		this.depositRateLevelDesc = depositRateLevelDesc;
	}


	public String getGuarantorNo() {
		return guarantorNo;
	}
	public void setGuarantorNo(String guarantorNo) {
		this.guarantorNo = guarantorNo;
	}
	public String getDepositRateFloatFlagDesc() {
		if("0".equals(this.depositRateFloatFlag)){
			return "与基准保持一致";
		}else if("1".equals(this.depositRateFloatFlag)){
			return "按实点浮动";
		}else if("2".equals(this.depositRateFloatFlag)){
			return "按比例浮动";
		}
		
		return depositRateFloatFlag;
	}


	public void setDepositRateFloatFlagDesc(String depositRateFloatFlagDesc) {
		this.depositRateFloatFlagDesc = depositRateFloatFlagDesc;
	}
	

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProtocolStatus() {
		return this.protocolStatus;
	}

	public void setProtocolStatus(String protocolStatus) {
		this.protocolStatus = protocolStatus;
	}

	public String getOnlineAcptNo() {
		return this.onlineAcptNo;
	}

	public void setOnlineAcptNo(String onlineAcptNo) {
		this.onlineAcptNo = onlineAcptNo;
	}

	public String getBpsNo() {
		return this.bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getBaseCreditNo() {
		return this.baseCreditNo;
	}

	public void setBaseCreditNo(String baseCreditNo) {
		this.baseCreditNo = baseCreditNo;
	}

	public String getEbkCustNo() {
		return this.ebkCustNo;
	}

	public void setEbkCustNo(String ebkCustNo) {
		this.ebkCustNo = ebkCustNo;
	}

	public BigDecimal getOnlineAcptTotal() {
		return this.onlineAcptTotal;
	}

	public void setOnlineAcptTotal(BigDecimal onlineAcptTotal) {
		this.onlineAcptTotal = onlineAcptTotal;
	}

	public BigDecimal getUsedAmt() {
		return this.usedAmt;
	}

	public void setUsedAmt(BigDecimal usedAmt) {
		this.usedAmt = usedAmt;
	}

	public String getAcceptorBankNo() {
		return this.acceptorBankNo;
	}

	public void setAcceptorBankNo(String acceptorBankNo) {
		this.acceptorBankNo = acceptorBankNo;
	}

	public String getAcceptorBankName() {
		return this.acceptorBankName;
	}

	public void setAcceptorBankName(String acceptorBankName) {
		this.acceptorBankName = acceptorBankName;
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

	public String getDepositRateLevel() {
		return this.depositRateLevel;
	}

	public void setDepositRateLevel(String depositRateLevel) {
		this.depositRateLevel = depositRateLevel;
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

	public BigDecimal getDepositRatio() {
		return this.depositRatio;
	}

	public void setDepositRatio(BigDecimal depositRatio) {
		this.depositRatio = depositRatio;
	}

	public BigDecimal getPoolCreditRatio() {
		return this.poolCreditRatio;
	}

	public void setPoolCreditRatio(BigDecimal poolCreditRatio) {
		this.poolCreditRatio = poolCreditRatio;
	}

	public BigDecimal getFeeRate() {
		return this.feeRate;
	}

	public void setFeeRate(BigDecimal feeRate) {
		this.feeRate = feeRate;
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

	public String getContractNo() {
		return this.contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getAppName() {
		return this.appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppNo() {
		return this.appNo;
	}

	public void setAppNo(String appNo) {
		this.appNo = appNo;
	}

	public String getSignBranchNo() {
		return this.signBranchNo;
	}

	public void setSignBranchNo(String signBranchNo) {
		this.signBranchNo = signBranchNo;
	}

	public String getSignBranchName() {
		return this.signBranchName;
	}

	public void setSignBranchName(String signBranchName) {
		this.signBranchName = signBranchName;
	}

	public Date getChangeDate() {
		return this.changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

	public Date getDueDate() {
		return this.dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
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



	public String getModeMark() {
		return modeMark;
	}



	public void setModeMark(String modeMark) {
		this.modeMark = modeMark;
	}



	public String getModeContent() {
		return modeContent;
	}



	public void setModeContent(String modeContent) {
		this.modeContent = modeContent;
	}



	public String getLastSourceId() {
		return lastSourceId;
	}



	public void setLastSourceId(String lastSourceId) {
		this.lastSourceId = lastSourceId;
	}



	public String getCustName() {
		return custName;
	}



	public void setCustName(String custName) {
		this.custName = custName;
	}



	public String getProtocolStatusDesc() {
		if(PublicStaticDefineTab.STATUS_0.equals(this.protocolStatus)){
			return "失效-"+this.protocolStatus;
		}else if(PublicStaticDefineTab.STATUS_1.equals(this.protocolStatus)){
			return "生效-"+this.protocolStatus;
		}else{
			return protocolStatusDesc;
		}
	}
	
	
	

}