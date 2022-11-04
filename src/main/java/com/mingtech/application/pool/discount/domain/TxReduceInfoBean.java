package com.mingtech.application.pool.discount.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 审价调减信息
 * */
public class TxReduceInfoBean {
	private String id;
	private String txType;
	private String batchNo;
	private String bankType;
	private String billNo;
	public String getBillNo() {
		return billNo;
	}
	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}
	private BigDecimal applyTxRate;
	private String applyTerm;
	private BigDecimal approveAmt;
	private BigDecimal currentAmt;
	private BigDecimal usedAmt;
	private BigDecimal aviableAmt;
	private BigDecimal reduceAmt;
	private BigDecimal totalReduceAmt;	//	累计调减额度
	private String workerNo;
	private String workerName;
	
	private Date lastUpdateTime;
	private Date firstInsertTime;
	
	public String getApplyTerm() {
		return applyTerm;
	}
	public void setApplyTerm(String applyTerm) {
		this.applyTerm = applyTerm;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTxType() {
		return txType;
	}
	public void setTxType(String txType) {
		this.txType = txType;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getBankType() {
		return bankType;
	}
	public void setBankType(String bankType) {
		this.bankType = bankType;
	}
	public BigDecimal getApplyTxRate() {
		return applyTxRate;
	}
	public void setApplyTxRate(BigDecimal applyTxRate) {
		this.applyTxRate = applyTxRate;
	}
	public BigDecimal getApproveAmt() {
		return approveAmt;
	}
	public void setApproveAmt(BigDecimal approveAmt) {
		this.approveAmt = approveAmt;
	}
	public BigDecimal getCurrentAmt() {
		return currentAmt;
	}
	public void setCurrentAmt(BigDecimal currentAmt) {
		this.currentAmt = currentAmt;
	}
	public BigDecimal getUsedAmt() {
		return usedAmt;
	}
	public void setUsedAmt(BigDecimal usedAmt) {
		this.usedAmt = usedAmt;
	}
	public BigDecimal getAviableAmt() {
		return aviableAmt;
	}
	public void setAviableAmt(BigDecimal aviableAmt) {
		this.aviableAmt = aviableAmt;
	}
	public BigDecimal getReduceAmt() {
		return reduceAmt;
	}
	public void setReduceAmt(BigDecimal reduceAmt) {
		this.reduceAmt = reduceAmt;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Date getFirstInsertTime() {
		return firstInsertTime;
	}
	public void setFirstInsertTime(Date firstInsertTime) {
		this.firstInsertTime = firstInsertTime;
	}
	
	public BigDecimal getTotalReduceAmt() {
		return totalReduceAmt;
	}
	public void setTotalReduceAmt(BigDecimal totalReduceAmt) {
		this.totalReduceAmt = totalReduceAmt;
	}
	public String getWorkerNo() {
		return workerNo;
	}
	public void setWorkerNo(String workerNo) {
		this.workerNo = workerNo;
	}
	public String getWorkerName() {
		return workerName;
	}
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
	
}
