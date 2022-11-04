package com.mingtech.application.pool.discount.domain;

import java.math.BigDecimal;

/**
 * 贴现信息详情
 * */
public class TxReviewPriceDetail {
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String batchNo ;			//	贴现定价审批编号batch_no
	private String applyBranch ;		//	申请单位apply_branch
	private String applyDate ;			//	申请日期apply_date
	private String custManager ;		//	客户经理cust_Manager
	private String empNo ;				//	员工编号emp_no
	private String telPhone ;			//	手机telPhone
	private String landLine ;			//	座机landline
	private String custName ;			//	客户名称cust_name
	private String custNo ;				//	核心客户号cust_no
	private String misCustNo ;			//	mis客户号mis_cust_no
	private String onlineNo ;			//	在线协议编号online_no
	private String accountType ;		//	账户情况account_type
	private String isMicro ;			//	是否小微is_micro
	private String isPriBusi;			
	private String isPriFarm;
	private String isGreen;
	private String isTech;
	
	private BigDecimal applyAmountSum ;	//	申请总金额apply_amount_sum
	private String auditType ;			//	审批条线audit_type
	private String applyTxDate ;		//	申请贴现日期apply_tx_date
	private String applyValidDate ;		//	申请有效期apply_valid_date
	private String aveDailyDeposit ;	//	客户日均存款ave_daily_deposit
	private String innerBusiIncome ;	//	客户中间业务收入inner_busi_income
	private String otherBusiIncome ;	//	客户其他收入other_busi_income
	private String applyReason ;		//	申请说明apply_reason
	private String effect ;				//	此次影响收入effect
	private BigDecimal applyTxRate;		//	申请贴现利率   (额度审价二字段)
	private BigDecimal bestFavorRate;	//	最优惠利率
	
	private String effDate;
	
	private String otherRemark ;		//	其他other_remark
	private String lastUpdateTime ;		//	lastUpdateTime
	private String firstInsertTime ;	//	firstInsertTime
	
	public String getIsPriBusi() {
		return isPriBusi;
	}
	public void setIsPriBusi(String isPriBusi) {
		this.isPriBusi = isPriBusi;
	}
	public String getIsPriFarm() {
		return isPriFarm;
	}
	public void setIsPriFarm(String isPriFarm) {
		this.isPriFarm = isPriFarm;
	}
	public String getIsGreen() {
		return isGreen;
	}
	public void setIsGreen(String isGreen) {
		this.isGreen = isGreen;
	}
	public String getIsTech() {
		return isTech;
	}
	public void setIsTech(String isTech) {
		this.isTech = isTech;
	}
	
	public String getEffDate() {
		return effDate;
	}
	public void setEffDate(String effDate) {
		this.effDate = effDate;
	}
	public BigDecimal getApplyTxRate() {
		return applyTxRate;
	}
	public void setApplyTxRate(BigDecimal applyTxRate) {
		this.applyTxRate = applyTxRate;
	}
	public BigDecimal getBestFavorRate() {
		return bestFavorRate;
	}
	public void setBestFavorRate(BigDecimal bestFavorRate) {
		this.bestFavorRate = bestFavorRate;
	}
	
	public String getBatchNo() {
		return batchNo;
	}
	public String getApplyBranch() {
		return applyBranch;
	}
	public String getApplyDate() {
		return applyDate;
	}
	public String getCustManager() {
		return custManager;
	}
	public String getEmpNo() {
		return empNo;
	}
	public String getTelPhone() {
		return telPhone;
	}
	public String getLandLine() {
		return landLine;
	}
	public String getCustName() {
		return custName;
	}
	public String getCustNo() {
		return custNo;
	}
	public String getMisCustNo() {
		return misCustNo;
	}
	public String getOnlineNo() {
		return onlineNo;
	}
	public String getAccountType() {
		return accountType;
	}
	public String getIsMicro() {
		return isMicro;
	}
	public BigDecimal getApplyAmountSum() {
		return applyAmountSum;
	}
	public String getAuditType() {
		return auditType;
	}
	public String getApplyTxDate() {
		return applyTxDate;
	}
	public String getApplyValidDate() {
		return applyValidDate;
	}
	public String getAveDailyDeposit() {
		return aveDailyDeposit;
	}
	public String getInnerBusiIncome() {
		return innerBusiIncome;
	}
	public String getOtherBusiIncome() {
		return otherBusiIncome;
	}
	public String getApplyReason() {
		return applyReason;
	}
	public String getEffect() {
		return effect;
	}
	public String getOtherRemark() {
		return otherRemark;
	}
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	public String getFirstInsertTime() {
		return firstInsertTime;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public void setApplyBranch(String applyBranch) {
		this.applyBranch = applyBranch;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public void setCustManager(String custManager) {
		this.custManager = custManager;
	}
	public void setEmpNo(String empNo) {
		this.empNo = empNo;
	}
	public void setTelPhone(String telPhone) {
		this.telPhone = telPhone;
	}
	public void setLandLine(String landLine) {
		this.landLine = landLine;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	public void setMisCustNo(String misCustNo) {
		this.misCustNo = misCustNo;
	}
	public void setOnlineNo(String onlineNo) {
		this.onlineNo = onlineNo;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public void setIsMicro(String isMicro) {
		this.isMicro = isMicro;
	}
	public void setApplyAmountSum(BigDecimal applyAmountSum) {
		this.applyAmountSum = applyAmountSum;
	}
	public void setAuditType(String auditType) {
		this.auditType = auditType;
	}
	public void setApplyTxDate(String applyTxDate) {
		this.applyTxDate = applyTxDate;
	}
	public void setApplyValidDate(String applyValidDate) {
		this.applyValidDate = applyValidDate;
	}
	public void setAveDailyDeposit(String aveDailyDeposit) {
		this.aveDailyDeposit = aveDailyDeposit;
	}
	public void setInnerBusiIncome(String innerBusiIncome) {
		this.innerBusiIncome = innerBusiIncome;
	}
	public void setOtherBusiIncome(String otherBusiIncome) {
		this.otherBusiIncome = otherBusiIncome;
	}
	public void setApplyReason(String applyReason) {
		this.applyReason = applyReason;
	}
	public void setEffect(String effect) {
		this.effect = effect;
	}
	public void setOtherRemark(String otherRemark) {
		this.otherRemark = otherRemark;
	}
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public void setFirstInsertTime(String firstInsertTime) {
		this.firstInsertTime = firstInsertTime;
	}
}
