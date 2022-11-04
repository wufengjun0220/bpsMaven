package com.mingtech.application.pool.online.loan.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;

/**
 * PedOnlineCrdtProtocol entity. @author MyEclipse Persistence Tools
 */

public class PedOnlineCrdtProtocol implements java.io.Serializable {

	// Fields

	private String id;
	private String bpsId;              //票据池协议表ID
	private String bpsNo;              //票据池协议号
	private String bpsName;            //票据池名称
	private String custNumber;         //客户号
	private String custOrgcode;        //客户组织机构代码
	private String custName;           //客户名称
	private String protocolStatus;     //在线协议状态
	private String onlineCrdtNo;       //在线流贷编号
	private String baseCreditNo;       //基本授信额度编号
	private String ebkCustNo;          //网银客户号
	private BigDecimal poolCreditRatio;//票据池额度比例（%）
	private BigDecimal onlineLoanTotal;//在线流贷总额
	private BigDecimal usedAmt;        //在线流贷已用总额
	private String baseRateType;       //基准利率类型
	private String rateFloatType;      //利率浮动方式
	private BigDecimal rateFloatValue; //利率浮动值（%）
	private String overRateFloatType;  //逾期利率浮动方式
	private BigDecimal overRateFloatValue; //逾期利率浮动值（%）
	private String makeLoanType;       //放款方式
	private String repaymentType;      //还款方式
	private String isAutoDeduct;       //是否自动扣划本息
	private String isDiscInterest;     //是否贴息
	private String loanAcctNo;         //放款账户账号
	private String loanAcctName;     //放款账户名称
	private String deduAcctNo;         //扣款账户账号
	private String deduAcctName;       //扣款账户名称
	private String inAcctBranchNo;     //入账机构所号
	private String inAcctBranchName;   //入账机构名称
	private String contractNo;         //担保合同编号
	private String guarantorNo;         //担保人核心客户号
	private String guarantor;          //担保人名称
	private String appName;            //经办人名称
	private String appNo;              //经办人编号
	private String signBranchNo;       //签约机构所号
	private String signBranchName;     //签约机构名称
	private Date openDate;             //开通日期
	private Date changeDate;           //变更日期
	private Date dueDate;              //到期日期
	private Date createTime;         //创建时间
	private Date updateTime;         //最近修改时间
	private String ldFlag;//在线流贷开关 0关 1开
	//页面展示
	private String protocolStatusDesc;
	private BigDecimal availableAmt;//可用金额
	
	//中文
	private String baseRateTypeDesc;       //基准利率类型
	private String rateFloatTypeDesc;      //利率浮动方式
	private String overRateFloatTypeDesc;  //逾期利率浮动方式
	private String makeLoanTypeDesc;       //放款方式
	private String repaymentTypeDesc;      //还款方式
	private String isAutoDeductDesc;       //是否自动扣划本息
	private String isDiscInterestDesc;     //是否贴息



	public String getLdFlag() {
		return ldFlag;
	}


	public void setLdFlag(String ldFlag) {
		this.ldFlag = ldFlag;
	}


	public String getGuarantorNo() {
		return guarantorNo;
	}


	public void setGuarantorNo(String guarantorNo) {
		this.guarantorNo = guarantorNo;
	}


	public String getBaseRateTypeDesc() {
		
		if("0".equals(this.baseRateType)){
			return "LPR-1年期";
		}
		return baseRateType;
	}


	public void setBaseRateTypeDesc(String baseRateTypeDesc) {
		this.baseRateTypeDesc = baseRateTypeDesc;
	}


	public String getRateFloatTypeDesc() {
		if("0".equals(this.rateFloatType)){
			return "基准保持一致";
		}else if("1".equals(this.rateFloatType)){
			return "按实点浮动";
		}else if("2".equals(this.rateFloatType)){
			return "按比例浮动";
		}
		return rateFloatType;
	}


	public void setRateFloatTypeDesc(String rateFloatTypeDesc) {
		this.rateFloatTypeDesc = rateFloatTypeDesc;
	}


	public String getOverRateFloatTypeDesc() {
		if("0".equals(this.overRateFloatType)){
			return "基准保持一致";
		}else if("1".equals(this.overRateFloatType)){
			return "按实点浮动";
		}else if("2".equals(this.overRateFloatType)){
			return "按比例浮动";
		}
		return overRateFloatType;
	}


	public void setOverRateFloatTypeDesc(String overRateFloatTypeDesc) {
		this.overRateFloatTypeDesc = overRateFloatTypeDesc;
	}


	public String getMakeLoanTypeDesc() {
		if("0".equals(this.makeLoanType)){
			return "一次放款";
		}
		
		return makeLoanType;
	}


	public void setMakeLoanTypeDesc(String makeLoanTypeDesc) {
		this.makeLoanTypeDesc = makeLoanTypeDesc;
	}


	public String getRepaymentTypeDesc() {
		if("0".equals(this.repaymentType)){
			return "利随本清";
		}
		return repaymentType;
	}


	public void setRepaymentTypeDesc(String repaymentTypeDesc) {
		this.repaymentTypeDesc = repaymentTypeDesc;
	}


	public String getIsAutoDeductDesc() {
		if("1".equals(this.isAutoDeduct)){
			return "是";
		}
		return isAutoDeduct;
	}


	public void setIsAutoDeductDesc(String isAutoDeductDesc) {
		this.isAutoDeductDesc = isAutoDeductDesc;
	}


	public String getIsDiscInterestDesc() {
		if("0".equals(this.isDiscInterest)){
			return "否";
		}
		return isDiscInterest;
	}


	public void setIsDiscInterestDesc(String isDiscInterestDesc) {
		this.isDiscInterestDesc = isDiscInterestDesc;
	}


	/** default constructor */
	public PedOnlineCrdtProtocol() {
	}


	// Property accessors

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

	public String getOnlineCrdtNo() {
		return this.onlineCrdtNo;
	}

	public void setOnlineCrdtNo(String onlineCrdtNo) {
		this.onlineCrdtNo = onlineCrdtNo;
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

	public BigDecimal getPoolCreditRatio() {
		return this.poolCreditRatio;
	}

	public void setPoolCreditRatio(BigDecimal poolCreditRatio) {
		this.poolCreditRatio = poolCreditRatio;
	}

	public BigDecimal getOnlineLoanTotal() {
		return this.onlineLoanTotal;
	}

	public void setOnlineLoanTotal(BigDecimal onlineLoanTotal) {
		this.onlineLoanTotal = onlineLoanTotal;
	}

	public String getBaseRateType() {
		return this.baseRateType;
	}

	public void setBaseRateType(String baseRateType) {
		this.baseRateType = baseRateType;
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

	public String getMakeLoanType() {
		return this.makeLoanType;
	}

	public void setMakeLoanType(String makeLoanType) {
		this.makeLoanType = makeLoanType;
	}

	public String getRepaymentType() {
		return this.repaymentType;
	}

	public void setRepaymentType(String repaymentType) {
		this.repaymentType = repaymentType;
	}

	public String getIsAutoDeduct() {
		return this.isAutoDeduct;
	}

	public void setIsAutoDeduct(String isAutoDeduct) {
		this.isAutoDeduct = isAutoDeduct;
	}

	public String getIsDiscInterest() {
		return this.isDiscInterest;
	}

	public void setIsDiscInterest(String isDiscInterest) {
		this.isDiscInterest = isDiscInterest;
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

	public BigDecimal getUsedAmt() {
		return usedAmt;
	}

	public void setUsedAmt(BigDecimal usedAmt) {
		this.usedAmt = usedAmt;
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
		if(null != this.getOnlineLoanTotal() && null != this.getUsedAmt()){
			return this.getOnlineLoanTotal().subtract(this.getUsedAmt());
		}else{
			return onlineLoanTotal;
		}
	}

	public void setAvailableAmt(BigDecimal availableAmt) {
		this.availableAmt = availableAmt;
	}


	
	
	
	

}