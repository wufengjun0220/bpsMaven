package com.mingtech.application.pool.online.manage.domain;

import java.util.Date;

/**
 * PedOnlineHandleLog entity. @author MyEclipse Persistence Tools
 */

public class PedOnlineHandleLog implements java.io.Serializable {

	// Fields

	private String id;
	private String busiName;     //业务名称   
	private String tradeName;    //业务渠道  信贷、电票、lpr、网银、核心、智慧宝、消息中心  
	private String tradeCode;    //报文码  
	private String sendType;     //收发类型 receive、send
	private String tradeResult;  //处理结果
	private String errorType;    //错误类型 提示、禁止
	private String operationType;//岗位     经办、复核
	private String billNo;       //票号、批次号
	private String busiId;       //业务id  
	private String onlineNo;     //在线协议编号
	private Date createTime;     //创建时间
	private String custNumber;   //客户号
	private String bpsNo;        //票据池协议号  

	// Constructors

	/** default constructor */
	public PedOnlineHandleLog() {
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTradeName() {
		return this.tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public String getBusiName() {
		return this.busiName;
	}

	public void setBusiName(String busiName) {
		this.busiName = busiName;
	}

	public String getSendType() {
		return this.sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getTradeCode() {
		return this.tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public String getTradeResult() {
		return this.tradeResult;
	}

	public void setTradeResult(String tradeResult) {
		this.tradeResult = tradeResult;
	}

	public String getErrorType() {
		return this.errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public String getOperationType() {
		return this.operationType;
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getBillNo() {
		return this.billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public String getOnlineNo() {
		return this.onlineNo;
	}

	public void setOnlineNo(String onlineNo) {
		this.onlineNo = onlineNo;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getBusiId() {
		return busiId;
	}

	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}

	public String getCustNumber() {
		return custNumber;
	}

	public void setCustNumber(String custNumber) {
		this.custNumber = custNumber;
	}

	public String getBpsNo() {
		return bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}
	
	

}