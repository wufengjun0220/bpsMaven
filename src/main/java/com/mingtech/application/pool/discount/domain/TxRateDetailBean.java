package com.mingtech.application.pool.discount.domain;

import java.math.BigDecimal;

public class TxRateDetailBean {
	//  利率详情字段
	private String id;
	private String bankType;			//	行别
	private String status;				//	状态
	private String effTime;				//	生效时间
	private String rateType;			//	利率类型
	private String batchNo;				//	批次号
	private BigDecimal rate;			//	利率值
	private int term;				//	期限
	
	private TxRateMaintainInfo txRateMaintainInfo;
	
	public TxRateMaintainInfo getTxRateMaintainInfo() {
		return txRateMaintainInfo;
	}
	public void setTxRateMaintainInfo(TxRateMaintainInfo txRateMaintainInfo) {
		this.txRateMaintainInfo = txRateMaintainInfo;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBankType() {
		return bankType;
	}
	public void setBankType(String bankType) {
		this.bankType = bankType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getEffTime() {
		return effTime;
	}
	public void setEffTime(String effTime) {
		this.effTime = effTime;
	}
	public String getRateType() {
		return rateType;
	}
	public void setRateType(String rateType) {
		this.rateType = rateType;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public int getTerm() {
		return term;
	}
	public void setTerm(int term) {
		this.term = term;
	}
	
}
