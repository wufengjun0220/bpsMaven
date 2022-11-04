package com.mingtech.application.pool.common.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 集团成员信息表
 * @Description TODO
 * @author Ju Nana
 * @version v1.0
 * @date 2019-5-27
 */

public class PedProtocolList implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 1428052471077782381L;
	private String id;
	private String bpsNo;//票据池编号
	private String bpsName;//票据池名称
	private String custNo;//核心客户号
	private String custName;//客户名
	private String orgCoge;//组织机构代码
	private String socialCode;//社会统一代码号
	private String elecDraftAccount;//电票签约账号
	private String elecDraftAccountName;//电票签约账号名称
	private String status;//状态    00：未签约  01：已签约    02：已解约
	private String financingStatus;//融资人生效标志	SF_01 生效   SF_00 失效
	private Date editTime;//修改时间
	private String role;//角色   01：主户     02：分户
	private String custIdentity;//客户身份  KHLX_01:出质人  KHLX_02:融资人   KHLX_03:出质人+融资人  KHLX_04:签约成员   KHLX_05:融资人解约   KHLX_06：签约成员解约  
	private BigDecimal maxFinancLimit;//最高融资限额 
	private BigDecimal financLimit;//融资限额
	
	//liuxiaodong add
	private String zyFlag;//是否自动入池  01   不自动 00


	// Constructors

	/** default constructor */
	public PedProtocolList() {
	}

	/** full constructor */
	public PedProtocolList(String bpsNo, String bpsName, String custNo,
			String custName, String orgCoge, String socialCode,
			String elecDraftAccount, String elecDraftAccountName,
			String status, Date editTime, String role, String custIdentity,
			BigDecimal maxFinancLimit, BigDecimal financLimit ,String zyFlag,String checkResult) {
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

	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	
	
	


	public String getFinancingStatus() {
		return financingStatus;
	}

	public void setFinancingStatus(String financingStatus) {
		this.financingStatus = financingStatus;
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
	public String getZyFlag() {
		return zyFlag;
	}

	public void setZyFlag(String zyFlag) {
		this.zyFlag = zyFlag;
	}

	public BigDecimal getFinancLimit() {
		return this.financLimit;
	}

	public void setFinancLimit(BigDecimal financLimit) {
		this.financLimit = financLimit;
	}

	
	
}