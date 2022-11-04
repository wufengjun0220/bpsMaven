package com.mingtech.application.pool.discount.domain;

import java.math.BigDecimal;

public class TxRateAdjustInfo {
	private String modifyType;
	private String rateAdjustWay;
	private String hisRateAdjustWay;
	private String txTerm;
	private String hisTxTerm;
	private String bankType;
	private String hisBankType;
	private BigDecimal rate;
	private BigDecimal hisRate;
	public String getModifyType() {
		return modifyType;
	}
	public void setModifyType(String modifyType) {
		this.modifyType = modifyType;
	}
	public String getRateAdjustWay() {
		return rateAdjustWay;
	}
	public void setRateAdjustWay(String rateAdjustWay) {
		this.rateAdjustWay = rateAdjustWay;
	}
	public String getHisRateAdjustWay() {
		return hisRateAdjustWay;
	}
	public void setHisRateAdjustWay(String hisRateAdjustWay) {
		this.hisRateAdjustWay = hisRateAdjustWay;
	}
	public String getTxTerm() {
		return txTerm;
	}
	public void setTxTerm(String txTerm) {
		this.txTerm = txTerm;
	}
	public String getHisTxTerm() {
		return hisTxTerm;
	}
	public void setHisTxTerm(String hisTxTerm) {
		this.hisTxTerm = hisTxTerm;
	}
	public String getBankType() {
		return bankType;
	}
	public void setBankType(String bankType) {
		this.bankType = bankType;
	}
	public String getHisBankType() {
		return hisBankType;
	}
	public void setHisBankType(String hisBankType) {
		this.hisBankType = hisBankType;
	}
	public BigDecimal getRate() {
		return rate;
	}
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	public BigDecimal getHisRate() {
		return hisRate;
	}
	public void setHisRate(BigDecimal hisRate) {
		this.hisRate = hisRate;
	}
}
