package com.mingtech.application.pool.discount.domain;

import java.math.BigDecimal;
import java.util.List;

public class TxBusinessInfo {
	private String id;
	private String custNo;			//	核心客户号
	private String custName;		//	客户名称
	private String onLineNo;		//	在线贴现协议编号
	private String businessType;	//	业务类型
	private String businessNo;		//	在线业务合同号
	private BigDecimal businessAmt;	//	业务金额
	private String applyDate;		//	申请日期
	private String applyType;		//	阶段			01-经办提交 02-复核提交
	private String errorMsg;		//	报错原因		
	private String status;			//	流程状态		00-失败 01-部分成功 11-成功
	private String errId;
	private String errorStatus;		//	错误状态 	
	
	
	public String getErrorStatus() {
		return errorStatus;
	}
	public void setErrorStatus(String errorStatus) {
		this.errorStatus = errorStatus;
	}
	public String getErrId() {
		return errId;
	}
	public void setErrId(String errId) {
		this.errId = errId;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	private List<TxBusinessDetail> txBusinessDetails;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<TxBusinessDetail> getTxBusinessDetails() {
		return txBusinessDetails;
	}
	public void setTxBusinessDetails(List<TxBusinessDetail> txBusinessDetails) {
		this.txBusinessDetails = txBusinessDetails;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getOnLineNo() {
		return onLineNo;
	}
	public void setOnLineNo(String onLineNo) {
		this.onLineNo = onLineNo;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getBusinessNo() {
		return businessNo;
	}
	public void setBusinessNo(String businessNo) {
		this.businessNo = businessNo;
	}
	public BigDecimal getBusinessAmt() {
		return businessAmt;
	}
	public void setBusinessAmt(BigDecimal businessAmt) {
		this.businessAmt = businessAmt;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getApplyType() {
		return applyType;
	}
	public void setApplyType(String applyType) {
		this.applyType = applyType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
