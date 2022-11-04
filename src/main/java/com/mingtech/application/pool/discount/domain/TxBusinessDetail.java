package com.mingtech.application.pool.discount.domain;

import java.math.BigDecimal;

public class TxBusinessDetail {
	public String getIouNo() {
		return iouNo;
	}
	public void setIouNo(String iouNo) {
		this.iouNo = iouNo;
	}
	private String id;
	private String billNo;				//	票据号码/票据包号
	private String iouNo;				//	借据号
	private String childBillNoBegin;	//	子票号(起)
	private String childBillNoEnd;		//	子票号(止)
	private String realChildBillNoEnd;	//	实际子票号(止)
	
	public String getRealChildBillNoEnd() {
		return realChildBillNoEnd;
	}
	public void setRealChildBillNoEnd(String realChildBillNoEnd) {
		this.realChildBillNoEnd = realChildBillNoEnd;
	}
	private BigDecimal billAmt;			//	票据金额
	private String billType;			//	票据种类
	private BigDecimal applyRate;		//	申请利率
	private String issueDate;			//	出票日
	private String dueDate;				//	到期日
	private int delayDays;				//	遇节假日顺眼天数
	private String issuerName;			//	出票人名称
	private String issuerAccountNo;		//	出票人账号
	private String issuerBankNo;		//	出票人开户行号
	private String issuerBankName;		//	出票人开户行行名
	private String acptBankName;		//	承兑行名称
	private String acptBankNo;			//	承兑行行号
	private String payeeAccountNo;		//	收款人账号
	private String payeeBankName;		//	收款人开户行名
	private String payeeBankNo;			//	收款人人开户行号
	private String billStatus;			//	票据状态
	private String errorMsg;
	private String errId;
	private String errorStatus;			//	错误状态
	
	public String getErrorStatus() {
		return errorStatus;
	}
	public void setErrorStatus(String errorStatus) {
		this.errorStatus = errorStatus;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getErrId() {
		return errId;
	}
	public void setErrId(String errId) {
		this.errId = errId;
	}
	public String getBillNo() {
		return billNo;
	}
	public String getChildBillNoBegin() {
		return childBillNoBegin;
	}
	public String getChildBillNoEnd() {
		return childBillNoEnd;
	}
	public BigDecimal getBillAmt() {
		return billAmt;
	}
	public String getBillType() {
		return billType;
	}
	public BigDecimal getApplyRate() {
		return applyRate;
	}
	public String getIssueDate() {
		return issueDate;
	}
	public String getDueDate() {
		return dueDate;
	}
	public int getDelayDays() {
		return delayDays;
	}
	public String getIssuerName() {
		return issuerName;
	}
	public String getIssuerAccountNo() {
		return issuerAccountNo;
	}
	public String getIssuerBankNo() {
		return issuerBankNo;
	}
	public String getIssuerBankName() {
		return issuerBankName;
	}
	public String getAcptBankName() {
		return acptBankName;
	}
	public String getAcptBankNo() {
		return acptBankNo;
	}
	public String getPayeeAccountNo() {
		return payeeAccountNo;
	}
	public String getPayeeBankName() {
		return payeeBankName;
	}
	public String getPayeeBankNo() {
		return payeeBankNo;
	}
	public String getBillStatus() {
		return billStatus;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public void setChildBillNoBegin(String childBillNoBegin) {
		this.childBillNoBegin = childBillNoBegin;
	}
	public void setChildBillNoEnd(String childBillNoEnd) {
		this.childBillNoEnd = childBillNoEnd;
	}
	public void setBillAmt(BigDecimal billAmt) {
		this.billAmt = billAmt;
	}
	public void setBillType(String billType) {
		this.billType = billType;
	}
	public void setApplyRate(BigDecimal applyRate) {
		this.applyRate = applyRate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}
	public void setDelayDays(int delayDays) {
		this.delayDays = delayDays;
	}
	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}
	public void setIssuerAccountNo(String issuerAccountNo) {
		this.issuerAccountNo = issuerAccountNo;
	}
	public void setIssuerBankNo(String issuerBankNo) {
		this.issuerBankNo = issuerBankNo;
	}
	public void setIssuerBankName(String issuerBankName) {
		this.issuerBankName = issuerBankName;
	}
	public void setAcptBankName(String acptBankName) {
		this.acptBankName = acptBankName;
	}
	public void setAcptBankNo(String acptBankNo) {
		this.acptBankNo = acptBankNo;
	}
	public void setPayeeAccountNo(String payeeAccountNo) {
		this.payeeAccountNo = payeeAccountNo;
	}
	public void setPayeeBankName(String payeeBankName) {
		this.payeeBankName = payeeBankName;
	}
	public void setPayeeBankNo(String payeeBankNo) {
		this.payeeBankNo = payeeBankNo;
	}
	public void setBillStatus(String billStatus) {
		this.billStatus = billStatus;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
