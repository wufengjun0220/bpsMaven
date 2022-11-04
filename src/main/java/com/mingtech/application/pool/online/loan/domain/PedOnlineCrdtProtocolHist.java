package com.mingtech.application.pool.online.loan.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;

/**
 * PedOnlineCrdtProtocolHist entity. @author MyEclipse Persistence Tools
 */

public class PedOnlineCrdtProtocolHist implements java.io.Serializable {

	// Fields

	private String id;
	private String custName;
	private String protocolStatus;
	private String onlineCrdtNo;
	private String baseCreditNo;
	private String ebkCustNo;
	private BigDecimal poolCreditRatio;
	private BigDecimal onlineLoanTotal;
	private String rateFloatType;
	private BigDecimal rateFloatValue;
	private String overRateFloatType;
	private BigDecimal overRateFloatValue;
	private String loanAcctNo;
	private String loanAcctName;
	private String deduAcctNo;
	private String deduAcctName;
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
	private String baseRateType;       //基准利率类型
	private String makeLoanType;       //放款方式
	private String repaymentType;      //还款方式
	private String isAutoDeduct;       //是否自动扣划本息
	private String isDiscInterest;     //是否贴息
	//页面展示
	private String protocolStatusDesc;
	//中文
	private String baseRateTypeDesc;       //基准利率类型
	private String rateFloatTypeDesc;      //利率浮动方式
	private String overRateFloatTypeDesc;  //逾期利率浮动方式
	private String makeLoanTypeDesc;       //放款方式
	private String repaymentTypeDesc;      //还款方式
	private String isAutoDeductDesc;       //是否自动扣划本息
	private String isDiscInterestDesc;     //是否贴息
	private String guarantorNo;         //担保人核心客户号

public String getBaseRateTypeDesc() {
		
		if("0".equals(this.baseRateType)){
			return "LPR-1年期";
		}
		return baseRateType;
	}


	public void setBaseRateTypeDesc(String baseRateTypeDesc) {
		this.baseRateTypeDesc = baseRateTypeDesc;
	}


	public String getGuarantorNo() {
		return guarantorNo;
	}


	public void setGuarantorNo(String guarantorNo) {
		this.guarantorNo = guarantorNo;
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
	// Constructors

	/** default constructor */
	public PedOnlineCrdtProtocolHist() {
	}


	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
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
		return changeDate;
	}


	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}


	public void setProtocolStatusDesc(String protocolStatusDesc) {
		this.protocolStatusDesc = protocolStatusDesc;
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
	
	public String getProtocolStatusDesc() {
		if(PublicStaticDefineTab.STATUS_0.equals(this.protocolStatus)){
			return "失效";
		}else if(PublicStaticDefineTab.STATUS_1.equals(this.protocolStatus)){
			return "生效";
		}else{
			return protocolStatusDesc;
		}
	}


	public String getBaseRateType() {
		return baseRateType;
	}


	public void setBaseRateType(String baseRateType) {
		this.baseRateType = baseRateType;
	}


	public String getMakeLoanType() {
		return makeLoanType;
	}


	public void setMakeLoanType(String makeLoanType) {
		this.makeLoanType = makeLoanType;
	}


	public String getRepaymentType() {
		return repaymentType;
	}


	public void setRepaymentType(String repaymentType) {
		this.repaymentType = repaymentType;
	}


	public String getIsAutoDeduct() {
		return isAutoDeduct;
	}


	public void setIsAutoDeduct(String isAutoDeduct) {
		this.isAutoDeduct = isAutoDeduct;
	}


	public String getIsDiscInterest() {
		return isDiscInterest;
	}


	public void setIsDiscInterest(String isDiscInterest) {
		this.isDiscInterest = isDiscInterest;
	}
	

}