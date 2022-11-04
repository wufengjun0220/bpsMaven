package com.mingtech.application.pool.online.acception.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;

/**
 * PedOnlineAcptProtocol entity. @author MyEclipse Persistence Tools
 */

public class PedOnlineAcptProtocol implements java.io.Serializable {

	// Fields

	private String id;
	private String bpsId;                     //票据池协议表ID
	private String bpsNo;                     //票据池协议号
	private String bpsName;                   //票据池名称
	private String custNumber;                //客户号
	private String custOrgcode;               //客户组织机构代码
	private String custName;                  //客户名称
	private String protocolStatus;            //在线协议状态
	private String onlineAcptNo;              //在线银承编号
	private String baseCreditNo;              //基本授信额度编号
	private String ebkCustNo;                 //网银客户号
	private BigDecimal onlineAcptTotal;       //在线银承总额
	private BigDecimal usedAmt;               //已用额度
	private String acceptorBankNo;            //承兑人承兑行行号
	private String acceptorBankName;          //承兑人承兑行名称
	private String depositAcctNo;             //扣收保证金账号
	private String depositBranchNo;           //扣收保证金账户开户行
	private String depositBranchName;         //扣收保证金账户开户行名称
	private String depositAcctName;           //扣收保证金账户名称
	private String depositRateLevel;          //保证金利率档次
	private String depositRateFloatFlag;      //保证金利率浮动标志
	private BigDecimal depositRateFloatValue; //保证金利率浮动值
	private BigDecimal depositRatio;          //保证金比例（%）
	private BigDecimal poolCreditRatio;       //票据池额度占用比例（%）
	private BigDecimal feeRate;               //手续费率（%）
	private String inAcctBranchNo;            //入账机构所号
	private String inAcctBranchName;          //入账机构名称
	private String contractNo;                //担保合同编号
	private String guarantor;                 //担保人名称
	private String guarantorNo;               //担保人核心客户号
	private String appName;                   //经办人名称
	private String appNo;                     //经办人编号
	private String signBranchNo;              //签约机构所号
	private String signBranchName;            //签约机构名称
	private Date openDate;                    //开通日期
	private Date changeDate;                  //变更日期
	private Date dueDate;                     //到期日期
	private Date createTime;                //创建时间
	private Date updateTime;                //最近修改时间
	private String ycFlag;//在线银承开关 0关 1开
	
	//页面展示
	private String protocolStatusDesc;//在线协议状态
	private BigDecimal availableAmt;//可用金额

	private String depositRateLevelDesc;          //保证金利率档次中文
	private String depositRateFloatFlagDesc;      //保证金利率浮动标志中文

	
	
	
	// Constructors

	public String getYcFlag() {
		return ycFlag;
	}


	public void setYcFlag(String ycFlag) {
		this.ycFlag = ycFlag;
	}


	public String getGuarantorNo() {
		return guarantorNo;
	}


	public void setGuarantorNo(String guarantorNo) {
		this.guarantorNo = guarantorNo;
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


	/** default constructor */
	public PedOnlineAcptProtocol() {
	}


	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBpsId() {
		return this.bpsId;
	}

	public void setBpsId(String bpsId) {
		this.bpsId = bpsId;
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

	public String getCustNumber() {
		return this.custNumber;
	}

	public void setCustNumber(String custNumber) {
		this.custNumber = custNumber;
	}

	public String getCustOrgcode() {
		return this.custOrgcode;
	}

	public void setCustOrgcode(String custOrgcode) {
		this.custOrgcode = custOrgcode;
	}

	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
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
	

	public String getDepositBranchNo() {
		return depositBranchNo;
	}


	public void setDepositBranchNo(String depositBranchNo) {
		this.depositBranchNo = depositBranchNo;
	}


	public String getDepositBranchName() {
		return depositBranchName;
	}


	public void setDepositBranchName(String depositBranchName) {
		this.depositBranchName = depositBranchName;
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

	public String getGuarantor() {
		return this.guarantor;
	}

	public void setGuarantor(String guarantor) {
		this.guarantor = guarantor;
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

	public Date getOpenDate() {
		return this.openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
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
		return createTime;
	}


	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}


	public Date getUpdateTime() {
		return updateTime;
	}


	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
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


	public void setProtocolStatusDesc(String protocolStatusDesc) {
		this.protocolStatusDesc = protocolStatusDesc;
	}


	public BigDecimal getAvailableAmt() {
		if(null != this.getOnlineAcptTotal() && null != this.getUsedAmt()){
			return this.getOnlineAcptTotal().subtract(this.getUsedAmt());
		}else{
			return onlineAcptTotal;
		}
	}


	public void setAvailableAmt(BigDecimal availableAmt) {
		this.availableAmt = availableAmt;
	}
	
	



}