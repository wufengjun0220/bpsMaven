package com.mingtech.application.pool.common.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 集团子户协议变更历史表
 * @author Ju Nana
 * @version v1.0
 * @date 2019-9-10
 */

public class PedProtocolListHist implements java.io.Serializable {

	// Fields

	private String id;
	private String bpsNo;
	private String bpsName;
	private String custNo;
	private String custName;
	private String orgCoge;
	private String socialCode;
	private String elecDraftAccount;
	private String elecDraftAccountName;
	private String status;
	private Date editTime;
	private String role;
	private String custIdentity;
	private BigDecimal maxFinancLimit;
	private BigDecimal financLimit;
	private String zyFlag;
	private String financingStatus;

	// Constructors

	/** default constructor */
	public PedProtocolListHist() {
	}

	/** full constructor */
	public PedProtocolListHist(String bpsNo, String bpsName, String custNo,
			String custName, String orgCoge, String socialCode,
			String elecDraftAccount, String elecDraftAccountName,
			String status, Date editTime, String role, String custIdentity,
			BigDecimal maxFinancLimit, BigDecimal financLimit, String zyFlag,
			String financingStatus) {
		this.bpsNo = bpsNo;
		this.bpsName = bpsName;
		this.custNo = custNo;
		this.custName = custName;
		this.orgCoge = orgCoge;
		this.socialCode = socialCode;
		this.elecDraftAccount = elecDraftAccount;
		this.elecDraftAccountName = elecDraftAccountName;
		this.status = status;
		this.editTime = editTime;
		this.role = role;
		this.custIdentity = custIdentity;
		this.maxFinancLimit = maxFinancLimit;
		this.financLimit = financLimit;
		this.zyFlag = zyFlag;
		this.financingStatus = financingStatus;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBpsNo() {
		return this.bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getBpsName() {
		return this.bpsName;
	}

	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
	}

	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getOrgCoge() {
		return this.orgCoge;
	}

	public void setOrgCoge(String orgCoge) {
		this.orgCoge = orgCoge;
	}

	public String getSocialCode() {
		return this.socialCode;
	}

	public void setSocialCode(String socialCode) {
		this.socialCode = socialCode;
	}

	public String getElecDraftAccount() {
		return this.elecDraftAccount;
	}

	public void setElecDraftAccount(String elecDraftAccount) {
		this.elecDraftAccount = elecDraftAccount;
	}

	public String getElecDraftAccountName() {
		return this.elecDraftAccountName;
	}

	public void setElecDraftAccountName(String elecDraftAccountName) {
		this.elecDraftAccountName = elecDraftAccountName;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getEditTime() {
		return this.editTime;
	}

	public void setEditTime(Date editTime) {
		this.editTime = editTime;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCustIdentity() {
		return this.custIdentity;
	}

	public void setCustIdentity(String custIdentity) {
		this.custIdentity = custIdentity;
	}

	public BigDecimal getMaxFinancLimit() {
		return this.maxFinancLimit;
	}

	public void setMaxFinancLimit(BigDecimal maxFinancLimit) {
		this.maxFinancLimit = maxFinancLimit;
	}

	public BigDecimal getFinancLimit() {
		return this.financLimit;
	}

	public void setFinancLimit(BigDecimal financLimit) {
		this.financLimit = financLimit;
	}

	public String getZyFlag() {
		return this.zyFlag;
	}

	public void setZyFlag(String zyFlag) {
		this.zyFlag = zyFlag;
	}

	public String getFinancingStatus() {
		return this.financingStatus;
	}

	public void setFinancingStatus(String financingStatus) {
		this.financingStatus = financingStatus;
	}

}