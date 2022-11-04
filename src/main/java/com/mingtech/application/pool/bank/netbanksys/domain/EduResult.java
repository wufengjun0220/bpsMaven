package com.mingtech.application.pool.bank.netbanksys.domain;

import java.math.BigDecimal;

public class EduResult {
	private BigDecimal totalBillAmount = new BigDecimal("0");//票据池票据总额度
	private BigDecimal highRiskAmount = new BigDecimal("0");//高风险额度
	private BigDecimal lowRiskAmount = new BigDecimal("0");//低风险额度
	private BigDecimal usedHighRiskAmount = new BigDecimal("0");//已用高风险额度
	private BigDecimal usedLowRiskAmount = new BigDecimal("0");//已用低风险额度
	private BigDecimal freeHighRiskAmount = new BigDecimal("0");//未用高风险额度
	private BigDecimal freeLowRiskAmount = new BigDecimal("0");//未用低风险额度
	private BigDecimal zeroEduAmount = new BigDecimal("0");//未产生额度票据总金额
	private BigDecimal bailAmountTotail = new BigDecimal("0");//保证金总金额
	private BigDecimal bailAmount = new BigDecimal("0");//保证金可用
	private BigDecimal bailAmountUsed = new BigDecimal("0");//保证金已用
	
	
	public BigDecimal getBailAmountUsed() {
		return bailAmountUsed;
	}
	public void setBailAmountUsed(BigDecimal bailAmountUsed) {
		this.bailAmountUsed = bailAmountUsed;
	}
	public BigDecimal getBailAmountTotail() {
		return bailAmountTotail;
	}
	public void setBailAmountTotail(BigDecimal bailAmountTotail) {
		this.bailAmountTotail = bailAmountTotail;
	}
	public BigDecimal getTotalBillAmount() {
		return totalBillAmount;
	}
	public void setTotalBillAmount(BigDecimal totalBillAmount) {
		this.totalBillAmount = totalBillAmount;
	}
	public BigDecimal getHighRiskAmount() {
		return highRiskAmount;
	}
	public void setHighRiskAmount(BigDecimal highRiskAmount) {
		this.highRiskAmount = highRiskAmount;
	}
	public BigDecimal getLowRiskAmount() {
		return lowRiskAmount;
	}
	public void setLowRiskAmount(BigDecimal lowRiskAmount) {
		this.lowRiskAmount = lowRiskAmount;
	}
	public BigDecimal getUsedHighRiskAmount() {
		return usedHighRiskAmount;
	}
	public void setUsedHighRiskAmount(BigDecimal usedHighRiskAmount) {
		this.usedHighRiskAmount = usedHighRiskAmount;
	}
	public BigDecimal getUsedLowRiskAmount() {
		return usedLowRiskAmount;
	}
	public void setUsedLowRiskAmount(BigDecimal usedLowRiskAmount) {
		this.usedLowRiskAmount = usedLowRiskAmount;
	}
	public BigDecimal getFreeHighRiskAmount() {
		return freeHighRiskAmount;
	}
	public void setFreeHighRiskAmount(BigDecimal freeHighRiskAmount) {
		this.freeHighRiskAmount = freeHighRiskAmount;
	}
	public BigDecimal getFreeLowRiskAmount() {
		return freeLowRiskAmount;
	}
	public void setFreeLowRiskAmount(BigDecimal freeLowRiskAmount) {
		this.freeLowRiskAmount = freeLowRiskAmount;
	}
	public BigDecimal getZeroEduAmount() {
		return zeroEduAmount;
	}
	public void setZeroEduAmount(BigDecimal zeroEduAmount) {
		this.zeroEduAmount = zeroEduAmount;
	}
	public BigDecimal getBailAmount() {
		return bailAmount;
	}
	public void setBailAmount(BigDecimal bailAmount) {
		this.bailAmount = bailAmount;
	}
	
	
}
