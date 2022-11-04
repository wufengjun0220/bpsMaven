package com.mingtech.application.sysmanage.domain;

import java.math.BigDecimal;
import java.util.Date;
/**
 *　@描述：前台数据数据传输使用 queryBean
  * 　  
 *
 */
public class BusiAcctFlowQueryBean {
	private String id;
	private String billId;//清单ID
	private String billInfoId;//大票表id
	private String prodId;//产品号
	private String batchId;// 批次ID 
	private String brachId;//机构ID
	private Date acctDateStart;//记账日期
	private Date acctDateEnd;//记账日期
	private String acctType;//记账类型(00记账 01撤销记账)
	private String acctUserId;//记账柜员ID
	private String acctUserNo;//记账柜员号
	private String preFlowNo;//前台流水号(票据系统生成)
	private String endFlowNo;//  后台流水号(核心或者其他系统返回的流水号)
	private String status;//  记账状态；(00初始状态；01成功 ；02系统记账异常 03核心记账异常)
	private String errorMsg;//记账返回的错误消息(300);
	private String sourceId;//原记账流水ID（撤销记账时存原记账流水ID）
	private Date busiDate;
	
	private String billNo;//票号
	private BigDecimal billAmt;//票面金额
	private String valid;//是否有效  00有效 01无效
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBillId() {
		return billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}
	public String getBillInfoId() {
		return billInfoId;
	}
	public void setBillInfoId(String billInfoId) {
		this.billInfoId = billInfoId;
	}
	public String getProdId() {
		return prodId;
	}
	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getBrachId() {
		return brachId;
	}
	public void setBrachId(String brachId) {
		this.brachId = brachId;
	}
	public Date getAcctDateStart() {
		return acctDateStart;
	}
	public void setAcctDateStart(Date acctDateStart) {
		this.acctDateStart = acctDateStart;
	}
	public Date getAcctDateEnd() {
		return acctDateEnd;
	}
	public void setAcctDateEnd(Date acctDateEnd) {
		this.acctDateEnd = acctDateEnd;
	}
	public String getAcctType() {
		return acctType;
	}
	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}
	public String getAcctUserId() {
		return acctUserId;
	}
	public void setAcctUserId(String acctUserId) {
		this.acctUserId = acctUserId;
	}
	public String getAcctUserNo() {
		return acctUserNo;
	}
	public void setAcctUserNo(String acctUserNo) {
		this.acctUserNo = acctUserNo;
	}
	public String getPreFlowNo() {
		return preFlowNo;
	}
	public void setPreFlowNo(String preFlowNo) {
		this.preFlowNo = preFlowNo;
	}
	public String getEndFlowNo() {
		return endFlowNo;
	}
	public void setEndFlowNo(String endFlowNo) {
		this.endFlowNo = endFlowNo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}
	public Date getBusiDate() {
		return busiDate;
	}
	public void setBusiDate(Date busiDate) {
		this.busiDate = busiDate;
	}
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	public BigDecimal getBillAmt() {
		return billAmt;
	}
	public void setBillAmt(BigDecimal billAmt) {
		this.billAmt = billAmt;
	}
	public String getValid() {
		return valid;
	}
	public void setValid(String valid) {
		this.valid = valid;
	}
	
}
