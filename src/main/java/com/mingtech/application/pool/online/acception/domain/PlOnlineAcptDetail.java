package com.mingtech.application.pool.online.acception.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.mingtech.application.ecds.common.DictionaryCache;

/**
 * PlOnlineAcptDetail entity. @author MyEclipse Persistence Tools
 */

public class PlOnlineAcptDetail implements java.io.Serializable {

	// Fields

	private String id;            //主键ID
	private String onlineAcptNo;  //在线协议编号
	private String bpsNo;         //票据池编号
	private String billNo;        //票号
	private String acptBatchId;   //批次id
	private BigDecimal billAmt;   //票面金额
	private String loanNo;        //借据编号
	private String issuerName;    //出票人名称
	private String issuerAcct;    //出票人账号
	private String issuerAcctName;    //出票人账号名称
	private String issuerBankCode;//出票人开户行行号
	private String issuerBankName;//出票人开户行名称
	private String issuerOrgcode; //出票人组织机构代码
	private String payeeName;     //收票人名称
	private String payeeAcct;     //收票人账号
	private String payeeAcctName;     //收票人账号名称
	private String payeeBankCode; //收票人开户行行号
	private String payeeBankName; //收票人开户行名称
	private Date isseDate;        //出票日
	private Date dueDate;         //到期日
	private String acptBankName;  //承兑行行名
	private String acptBankCode;  //承兑行行号
	private String limitType;     //期限方式 QXFS_0:到期日期 QXFS_1:期限（月）
	private String endDate;       //到期日/期限（月）
	private String transferFlag;  //是否可转让 0：否 1：是
	private String isAutoCallPyee;//是否联动收票人自动收票  0：否 1：是
	private String status;        //状态  新增成功 登记成功 承兑申请成功  记账成功 承兑签收成功
	private String billId;        //票据id  
	private String billSerialNo;  //序列号
	private Date createTime;      //创建时间
	private Date updateTime;      //最近修改时间
	private String dealStatus;    //业务处理状态   DS_001未处理  DS_002处理中  DS_003处理完成  DS_004已撤销  DS_005失败
	private Date cancelDate;      //未用退回时间
	// Constructors

	private String operationType; //操作类型
	private String statusDesc;        //状态  新增成功 登记成功 承兑申请成功  记账成功 承兑签收成功
	private String transferFlagDesc;
	
	
	private String transAccount;         //业务保证金账号--记录BBSP返回的该笔借据的核心的业务保证金账号
	private Date taskDate;//调度操作时间
	
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	private String hilrId;//持有id
	private String transId;//签收交易ID
	private String sendTransId;//申请交易ID
	/*** 融合改造新增字段  end*/
	
	/** default constructor */
	public PlOnlineAcptDetail() {
	}

	// Property accessors

	
	
	public String getId() {
		return this.id;
	}

	

	public String getSendTransId() {
		return sendTransId;
	}

	public void setSendTransId(String sendTransId) {
		this.sendTransId = sendTransId;
	}

	public String getIssuerAcctName() {
		return issuerAcctName;
	}

	public void setIssuerAcctName(String issuerAcctName) {
		this.issuerAcctName = issuerAcctName;
	}

	public String getPayeeAcctName() {
		return payeeAcctName;
	}

	public void setPayeeAcctName(String payeeAcctName) {
		this.payeeAcctName = payeeAcctName;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getHilrId() {
		return hilrId;
	}

	public void setHilrId(String hilrId) {
		this.hilrId = hilrId;
	}

	public String getBeginRangeNo() {
		return beginRangeNo;
	}

	public void setBeginRangeNo(String beginRangeNo) {
		this.beginRangeNo = beginRangeNo;
	}

	public String getEndRangeNo() {
		return endRangeNo;
	}

	public void setEndRangeNo(String endRangeNo) {
		this.endRangeNo = endRangeNo;
	}

	public BigDecimal getStandardAmt() {
		return standardAmt;
	}

	public void setStandardAmt(BigDecimal standardAmt) {
		this.standardAmt = standardAmt;
	}

	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	public String getDraftSource() {
		return draftSource;
	}

	public void setDraftSource(String draftSource) {
		this.draftSource = draftSource;
	}

	public String getSplitFlag() {
		return splitFlag;
	}

	public void setSplitFlag(String splitFlag) {
		this.splitFlag = splitFlag;
	}

	public Date getTaskDate() {
		return taskDate;
	}

	public void setTaskDate(Date taskDate) {
		this.taskDate = taskDate;
	}

	public String getTransAccount() {
		return transAccount;
	}

	public void setTransAccount(String transAccount) {
		this.transAccount = transAccount;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBillNo() {
		return this.billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getAcptBatchId() {
		return this.acptBatchId;
	}

	public void setAcptBatchId(String acptBatchId) {
		this.acptBatchId = acptBatchId;
	}

	public BigDecimal getBillAmt() {
		return this.billAmt;
	}

	public void setBillAmt(BigDecimal billAmt) {
		this.billAmt = billAmt;
	}

	public String getLoanNo() {
		return this.loanNo;
	}

	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}

	public String getIssuerName() {
		return this.issuerName;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}

	public String getIssuerAcct() {
		return this.issuerAcct;
	}

	public void setIssuerAcct(String issuerAcct) {
		this.issuerAcct = issuerAcct;
	}

	public String getIssuerBankCode() {
		return this.issuerBankCode;
	}

	public void setIssuerBankCode(String issuerBankCode) {
		this.issuerBankCode = issuerBankCode;
	}

	public String getIssuerBankName() {
		return this.issuerBankName;
	}

	public void setIssuerBankName(String issuerBankName) {
		this.issuerBankName = issuerBankName;
	}

	public String getIssuerOrgcode() {
		return this.issuerOrgcode;
	}

	public void setIssuerOrgcode(String issuerOrgcode) {
		this.issuerOrgcode = issuerOrgcode;
	}

	public String getPayeeName() {
		return this.payeeName;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}

	public String getPayeeAcct() {
		return this.payeeAcct;
	}

	public void setPayeeAcct(String payeeAcct) {
		this.payeeAcct = payeeAcct;
	}

	public String getPayeeBankCode() {
		return this.payeeBankCode;
	}

	public void setPayeeBankCode(String payeeBankCode) {
		this.payeeBankCode = payeeBankCode;
	}

	public String getPayeeBankName() {
		return this.payeeBankName;
	}

	public void setPayeeBankName(String payeeBankName) {
		this.payeeBankName = payeeBankName;
	}

	public Date getIsseDate() {
		return this.isseDate;
	}

	public void setIsseDate(Date isseDate) {
		this.isseDate = isseDate;
	}

	public Date getDueDate() {
		return this.dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getAcptBankName() {
		return this.acptBankName;
	}

	public void setAcptBankName(String acptBankName) {
		this.acptBankName = acptBankName;
	}

	public String getAcptBankCode() {
		return this.acptBankCode;
	}

	public void setAcptBankCode(String acptBankCode) {
		this.acptBankCode = acptBankCode;
	}

	public String getLimitType() {
		return this.limitType;
	}

	public void setLimitType(String limitType) {
		this.limitType = limitType;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getTransferFlag() {
		return this.transferFlag;
	}

	public void setTransferFlag(String transferFlag) {
		this.transferFlag = transferFlag;
	}

	public String getIsAutoCallPyee() {
		return this.isAutoCallPyee;
	}

	public void setIsAutoCallPyee(String isAutoCallPyee) {
		this.isAutoCallPyee = isAutoCallPyee;
	}

	public String getOperationType() {
		return this.operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
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

	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
	}

	public String getBillSerialNo() {
		return billSerialNo;
	}

	public void setBillSerialNo(String billSerialNo) {
		this.billSerialNo = billSerialNo;
	}

	public String getDealStatus() {
		return dealStatus;
	}

	public void setDealStatus(String dealStatus) {
		this.dealStatus = dealStatus;
	}

	public String getStatusDesc() {
		String statusDesc=DictionaryCache.getDealstatusmap(this.getStatus());
		if(StringUtils.isBlank(statusDesc)){
			statusDesc=this.getStatus();
		}
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getTransferFlagDesc() {
		if("0".equals(this.getTransferFlag())){
			return "不可转让";
		}else if("1".equals(this.getTransferFlag())){
			return "可转让";
		}else{
			return transferFlagDesc;
		}
	}

	public String getOnlineAcptNo() {
		return onlineAcptNo;
	}

	public void setOnlineAcptNo(String onlineAcptNo) {
		this.onlineAcptNo = onlineAcptNo;
	}

	public String getBpsNo() {
		return bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}
	

	


}