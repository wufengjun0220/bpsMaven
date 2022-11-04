package com.mingtech.application.pool.common.domain;

import java.util.List;


/**
 * 集团子户表查询Bean
 * @Description TODO
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-21
 */
public class ProListQueryBean implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String bpsNo;//票据池编号
	private String bpsName;//票据池名称
	private String custNo;//核心客户号
	private String custName;//客户名
	private String orgCoge;//组织机构代码
	private String status;//状态    00：未签约  01：已签约    02：已解约
	private String financingStatus;//融资人生效标志	FS_01 生效   FS_00 失效
	private String role;//角色   JS_01：主户     JS_02：分户
	private String custIdentity;//客户身份  KHLX_01:出质人  KHLX_02:融资人   KHLX_03:出质人+融资人  KHLX_04:签约成员 KHLX_05:融资人解约   KHLX_06：签约成员解约  
	
	private String zyflag;//是否自动入池  01   不自动 00
	
	private List<String> stautsList;//状态List
	
	private List<String> custIdentityList;//客户身份
	
	private String eleAccount;//电票签约账号
	
	private List<String> custNos;//客户号list
	
	
	public List<String> getCustNos() {
		return custNos;
	}

	public void setCustNos(List<String> custNos) {
		this.custNos = custNos;
	}

	public String getEleAccount() {
		return eleAccount;
	}

	public void setEleAccount(String eleAccount) {
		this.eleAccount = eleAccount;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	

	public List<String> getStautsList() {
		return stautsList;
	}

	public void setStautsList(List<String> stautsList) {
		this.stautsList = stautsList;
	}

	public String getBpsNo() {
		return bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getBpsName() {
		return bpsName;
	}

	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
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

	public String getOrgCoge() {
		return orgCoge;
	}

	public void setOrgCoge(String orgCoge) {
		this.orgCoge = orgCoge;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFinancingStatus() {
		return financingStatus;
	}

	public void setFinancingStatus(String financingStatus) {
		this.financingStatus = financingStatus;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getCustIdentity() {
		return custIdentity;
	}

	public void setCustIdentity(String custIdentity) {
		this.custIdentity = custIdentity;
	}

	public String getZyflag() {
		return zyflag;
	}

	public void setZyflag(String zyflag) {
		this.zyflag = zyflag;
	}

	public List<String> getCustIdentityList() {
		return custIdentityList;
	}

	public void setCustIdentityList(List<String> custIdentityList) {
		this.custIdentityList = custIdentityList;
	}

	
	
	
	
}
