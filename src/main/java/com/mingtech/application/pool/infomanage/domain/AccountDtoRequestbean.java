package com.mingtech.application.pool.infomanage.domain;

import com.mingtech.application.pool.common.PoolDictionaryCache;

/**
 * @描述：request结果集对像 （页面数据显示）
 * @author Administrator
 *
 */
public class AccountDtoRequestbean {

	private String pkAccountId;
	private String SAccountType;    // 账户类别
	//账户类别  参考  '0_195'  01：结算账户 02活期保证金账户 03定期保证金账户
	private String SAccountTypeName;// 显示账户类别字中文名
	private String SAccountName;    // 客户账号户名
	private String SAccountNo;      // 客户账号
	private String SCustBankName;   // 开户银行名称
	private String SCustBankCode;	// 开户银行行号
	private String signFlag; // 账号签约标记   01已签约 00未签约  02已解约
	private String signFlagName; // 显示账号签约标记中文名

	
	
	//=====客户信息
	private String SOrgCode;        // 组织机构代码
	private String SCustId;
	private String SCustName;       // 客户名称
	/**
	 * RC00接入行
	RC01企业
	RC02人民银行
	RC03被代理行
	RC04被代理财务公司
	RC05接入财务公司
	 */
	private String roleCode;//参与者类别，参考人行规定
	private String custBankName;// 客户开户行名称
	private String custBankCode;// 客户开户行行号
	private String finaType;//大的客户类型 0:企业客户;1:同业客户
	private String custType;//新客户类型  01企业客户；02：系统内机构；03同业银行；04：非银行金融机构
	private String bankNo;//同业客户使用 行号
	
	
	
	
	
	public String getRoleCode() {
		return roleCode;
	}
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	public String getSAccountType() {
		return SAccountType;
	}
	public void setSAccountType(String sAccountType) {
		SAccountType = sAccountType;
	}
	
	public String getSAccountTypeName() {
		return SAccountTypeName = PoolDictionaryCache.getAccountTypeMapMap(this.getSAccountType());
	}
	public void setSAccountTypeName(String sAccountTypeName) {
		SAccountTypeName = sAccountTypeName;
	}
	public String getSCustBankName() {
		return SCustBankName;
	}
	public void setSCustBankName(String sCustBankName) {
		SCustBankName = sCustBankName;
	}
	public String getSCustBankCode() {
		return SCustBankCode;
	}
	public void setSCustBankCode(String sCustBankCode) {
		SCustBankCode = sCustBankCode;
	}
	public String getSOrgCode() {
		return SOrgCode;
	}
	public void setSOrgCode(String sOrgCode) {
		SOrgCode = sOrgCode;
	}
	public String getSCustName() {
		return SCustName;
	}
	public void setSCustName(String sCustName) {
		SCustName = sCustName;
	}
	public String getPkAccountId() {
		return pkAccountId;
	}
	public void setPkAccountId(String pkAccountId) {
		this.pkAccountId = pkAccountId;
	}
	public String getSAccountNo() {
		return SAccountNo;
	}
	public void setSAccountNo(String sAccountNo) {
		SAccountNo = sAccountNo;
	}
	public String getSCustId() {
		return SCustId;
	}
	public void setSCustId(String sCustId) {
		SCustId = sCustId;
	}
	/**
	 * 账号户名默认是客户名称
	 * @return
	 */
	public String getSAccountName() {
		
		if( null== SAccountName ){
			return SCustName;
		}else{
			return SAccountName;
		}
		
	}
	public void setSAccountName(String sAccountName) {
		SAccountName = sAccountName;
	}
	public String getCustBankName() {
		return custBankName;
	}
	public void setCustBankName(String custBankName) {
		this.custBankName = custBankName;
	}
	public String getCustBankCode() {
		return custBankCode;
	}
	public void setCustBankCode(String custBankCode) {
		this.custBankCode = custBankCode;
	}
	public String getFinaType() {
		return finaType;
	}
	public void setFinaType(String finaType) {
		this.finaType = finaType;
	}
	public String getCustType() {
		return custType;
	}
	public void setCustType(String custType) {
		this.custType = custType;
	}
	public String getBankNo() {
		return bankNo;
	}
	public void setBankNo(String bankNo) {
		this.bankNo = bankNo;
	}
	
	
	
	public String getSignFlag() {
		return signFlag;
	}
	public void setSignFlag(String signFlag) {
		this.signFlag = signFlag;
	}
	public String getSignFlagName() {
		if("01".equals(signFlag)){
			return "已签约";
		}
		if("02".equals(signFlag)){
			return "已解约";
		}
		return "未签约 ";
	}
	public void setSignFlagName(String signFlagName) {
		this.signFlagName = signFlagName;
	}
}
