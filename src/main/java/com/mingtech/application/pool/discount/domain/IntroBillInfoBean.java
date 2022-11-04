package com.mingtech.application.pool.discount.domain;

import java.math.BigDecimal;

import com.mingtech.application.ecds.common.DictionaryCache;


/**
 * 贴现票据审价   票据引入信息
 * */
public class IntroBillInfoBean {
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String batchNo;				//	batchNo
	private String billNo;				//	bill_no
	private String childBillNoBegin;	//	子票号(起)
	private String childBillNoEnd;		//	子票号(止)
	private String issueDate;			//	issue_date 出票日
	private String issuerName;			//	出票人名称
	private String issuerBankName;		//	出票人开户行名
	private String issuerBankNo;		//	出票人开户行号
	private String payeeName;			//	收款人名称
	private String payeeBankName;		//	收款人开户行名
	private String payeeBankNo;			//	收款人人开户行号
	private String status;
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	private BigDecimal billAmt;				//	bill_amt	票据金额
	private String billType;			//	bill_type	票据类型
	private String billTypeName;			//	bill_type	票据类型
	private String applyTxDate;			//	apply_tx_date	申请贴现日期
	private String dueDate;				//	due_date		到期日
	private String limitDays;			//	limit_days		期限
	private String acptBankName;		//	acpt_bank_name 	承兑行名称
	private String acptBankNo;			//	acpt_bank_no	承兑行行号
	
	private BigDecimal guidanceRate;	//	guidance_rate	指导利率
	private BigDecimal favorRate;		//	favor_rate		优惠利率
	private BigDecimal bestFavorRate;	//	best_favor_rate	最优惠利率
	private String dataSource;			//	data_source		数据来源		01-录票 02-引票
	private TxReviewPriceInfo txReviewPriceInfo;
	
	private String acptBankType;		//	acpt_bank_type	承兑行别
	private BigDecimal applyTxRate;		//	apply_tx_rate	申请贴现利率
	private String adjustType;			//	调整方式
	public String getAdjustType() {
		return adjustType;
	}
	public void setAdjustType(String adjustType) {
		this.adjustType = adjustType;
	}
	private BigDecimal applyAmt;		//	申请额度
	private String txTerm;				//	贴现期限
	private String approveBranchNo;		//	审批机构号
	private String approveBranchName;	//	审批机构名称
	private String approveDate;			//	审批时间
	
	public String getApproveBranchNo() {
		return approveBranchNo;
	}
	public void setApproveBranchNo(String approveBranchNo) {
		this.approveBranchNo = approveBranchNo;
	}
	public String getApproveBranchName() {
		return approveBranchName;
	}
	public void setApproveBranchName(String approveBranchName) {
		this.approveBranchName = approveBranchName;
	}
	public String getApproveDate() {
		return approveDate;
	}
	public void setApproveDate(String approveDate) {
		this.approveDate = approveDate;
	}
	public String getAcptBankNo() {
		return acptBankNo;
	}
	public void setAcptBankNo(String acptBankNo) {
		this.acptBankNo = acptBankNo;
	}
	
	public String getBillTypeName(){
		billTypeName = (String) DictionaryCache.getBillType(this.getBillType());
		return billTypeName;
	}

	public void setBillTypeName(String billTypeName){
		this.billTypeName = billTypeName;
	}
	
	public String getTxTerm() {
		return txTerm;
	}
	public void setTxTerm(String txTerm) {
		this.txTerm = txTerm;
	}
	
	
	
	public BigDecimal getApplyAmt() {
		return applyAmt;
	}
	public void setApplyAmt(BigDecimal applyAmt) {
		this.applyAmt = applyAmt;
	}
	
	public TxReviewPriceInfo getTxReviewPriceInfo() {
		return txReviewPriceInfo;
	}
	public void setTxReviewPriceInfo(TxReviewPriceInfo txReviewPriceInfo) {
		this.txReviewPriceInfo = txReviewPriceInfo;
	}
	public BigDecimal getApplyTxRate() {
		return applyTxRate;
	}
	public BigDecimal getGuidanceRate() {
		return guidanceRate;
	}
	public void setApplyTxRate(BigDecimal applyTxRate) {
		this.applyTxRate = applyTxRate;
	}
	public void setGuidanceRate(BigDecimal guidanceRate) {
		this.guidanceRate = guidanceRate;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public String getBillNo() {
		return billNo;
	}
	public BigDecimal getBillAmt() {
		return billAmt;
	}
	public String getBillType() {
		return billType;
	}
	public String getApplyTxDate() {
		return applyTxDate;
	}
	public String getDueDate() {
		return dueDate;
	}
	public String getLimitDays() {
		return limitDays;
	}
	public String getAcptBankName() {
		return acptBankName;
	}
	public String getAcptBankType() {
		return acptBankType;
	}
	public BigDecimal getFavorRate() {
		return favorRate;
	}
	public BigDecimal getBestFavorRate() {
		return bestFavorRate;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public void setBillAmt(BigDecimal billAmt) {
		this.billAmt = billAmt;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public void setApplyTxDate(String applyTxDate) {
		this.applyTxDate = applyTxDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public void setLimitDays(String limitDays) {
		this.limitDays = limitDays;
	}
	public void setAcptBankName(String acptBankName) {
		this.acptBankName = acptBankName;
	}
	public void setAcptBankType(String acptBankType) {
		this.acptBankType = acptBankType;
	}
	public void setFavorRate(BigDecimal favorRate) {
		this.favorRate = favorRate;
	}
	public void setBestFavorRate(BigDecimal bestFavorRate) {
		this.bestFavorRate = bestFavorRate;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public String getChildBillNoBegin() {
		return childBillNoBegin;
	}
	public void setChildBillNoBegin(String childBillNoBegin) {
		this.childBillNoBegin = childBillNoBegin;
	}
	public String getChildBillNoEnd() {
		return childBillNoEnd;
	}
	public void setChildBillNoEnd(String childBillNoEnd) {
		this.childBillNoEnd = childBillNoEnd;
	}
	public String getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	public String getIssuerName() {
		return issuerName;
	}
	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}
	public String getIssuerBankName() {
		return issuerBankName;
	}
	public void setIssuerBankName(String issuerBankName) {
		this.issuerBankName = issuerBankName;
	}
	public String getIssuerBankNo() {
		return issuerBankNo;
	}
	public void setIssuerBankNo(String issuerBankNo) {
		this.issuerBankNo = issuerBankNo;
	}
	public String getPayeeName() {
		return payeeName;
	}
	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}
	public String getPayeeBankName() {
		return payeeBankName;
	}
	public void setPayeeBankName(String payeeBankName) {
		this.payeeBankName = payeeBankName;
	}
	public String getPayeeBankNo() {
		return payeeBankNo;
	}
	public void setPayeeBankNo(String payeeBankNo) {
		this.payeeBankNo = payeeBankNo;
	}
	public IntroBillInfoBean(String billNo, String childBillNoBegin,
			String childBillNoEnd, String issueDate, String issuerName,
			String issuerBankName, String issuerBankNo, String payeeName,
			String payeeBankName, String payeeBankNo, BigDecimal billAmt,
			String billType, String applyTxDate, String dueDate,
			String limitDays, String acptBankName, String acptBankNo,
			String acptBankType, BigDecimal applyTxRate, BigDecimal guidanceRate,
			BigDecimal favorRate, BigDecimal bestFavorRate, String dataSource) {
		super();
		this.billNo = billNo;
		this.childBillNoBegin = childBillNoBegin;
		this.childBillNoEnd = childBillNoEnd;
		this.issueDate = issueDate;
		this.issuerName = issuerName;
		this.issuerBankName = issuerBankName;
		this.issuerBankNo = issuerBankNo;
		this.payeeName = payeeName;
		this.payeeBankName = payeeBankName;
		this.payeeBankNo = payeeBankNo;
		this.billAmt = billAmt;
		this.billType = billType;
		this.applyTxDate = applyTxDate;
		this.dueDate = dueDate;
		this.limitDays = limitDays;
		this.acptBankName = acptBankName;
		this.acptBankNo = acptBankNo;
		this.acptBankType = acptBankType;
		this.applyTxRate = applyTxRate;
		this.guidanceRate = guidanceRate;
		this.favorRate = favorRate;
		this.bestFavorRate = bestFavorRate;
		this.dataSource = dataSource;
	}
	public IntroBillInfoBean() {
		super();
	}
}
