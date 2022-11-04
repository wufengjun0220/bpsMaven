package com.mingtech.application.pool.discount.domain;

public class BankRoleMappingBean {
	private String acptHeadBankName;	//	行名
	private String acptHeadBankNo;		//	承兑行总行行号
	private String pjsBankType;			//	票交所行别
	private String defaultType;			//	默认类型
	private String maintainType;		//	维护类型
	private String actualType;			//	实际类型
	private String maintainTime;		//	维护日期
	private String workerName;			//	经办人
	private String workerNo;			//	经办人工号
	private String branchCode;
	private String branchName;
	public String getAcptHeadBankName() {
		return acptHeadBankName;
	}
	public void setAcptHeadBankName(String acptHeadBankName) {
		this.acptHeadBankName = acptHeadBankName;
	}
	public String getBranchCode() {
		return branchCode;
	}
	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public String getAcptHeadBankNo() {
		return acptHeadBankNo;
	}
	public void setAcptHeadBankNo(String acptHeadBankNo) {
		this.acptHeadBankNo = acptHeadBankNo;
	}
	public String getPjsBankType() {
		return pjsBankType;
	}
	public void setPjsBankType(String pjsBankType) {
		this.pjsBankType = pjsBankType;
	}
	public String getDefaultType() {
		return defaultType;
	}
	public void setDefaultType(String defaultType) {
		this.defaultType = defaultType;
	}
	public String getMaintainType() {
		return maintainType;
	}
	public void setMaintainType(String maintainType) {
		this.maintainType = maintainType;
	}
	public String getActualType() {
		return actualType;
	}
	public void setActualType(String actualType) {
		this.actualType = actualType;
	}
	public String getMaintainTime() {
		return maintainTime;
	}
	public void setMaintainTime(String maintainTime) {
		this.maintainTime = maintainTime;
	}
	public String getWorkerName() {
		return workerName;
	}
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}
	public String getWorkerNo() {
		return workerNo;
	}
	public void setWorkerNo(String workerNo) {
		this.workerNo = workerNo;
	}
}
