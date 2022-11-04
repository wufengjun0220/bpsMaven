package com.mingtech.application.pool.infomanage.domain;

import java.util.Date;

/**
 * 客户账号信息表 
 * @author Administrator
 *
 */
public class AccountDto implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	// Fields
	private String pkAccountId;
	private String SCustId;
	private String SAccountName; // 账号户名
	private String SAccountNo;//账号
	//账户类别  参考  '0_195'  1：票据池保证金账户 2结算账户 3无4票据池保证金定期账户   PUBL...ACCT_BZJC;票据池用的多；
	private String SAccountType;
	private String SCustBankName;//账号开户机构
	private String SCustBankCode;//账号开户机构行号
	private String brchId;//开户机构ID
	private String SLoginName;//柜面登录用户名
	private String SPassword;
	private String newPassword;
	
	/**
	 * 代理接入行  签约使用 2017
	 */
	private String signFlag; // 账号签约标记   01已签约 00未签约 
	private String signOperId;//签约操作柜员ID
	private Date signDate;//签约日期
	private Date signCancelDate;//解约日期
	private String signCancelOperId;//解约操作柜员ID
	
	
	// Constructors
	/** default constructor */
	public AccountDto(){
	}

	/** full constructor */
	public AccountDto(String SCustId, String SAccountNo, String SAccountType,
			String SCustBankName, String SCustBankCode){
		this.SCustId = SCustId;
		this.SAccountNo = SAccountNo;
		this.SAccountType = SAccountType;
		this.SCustBankName = SCustBankName;
		this.SCustBankCode = SCustBankCode;
	}

	// Property accessors
	public String getPkAccountId(){
		return this.pkAccountId;
	}

	public void setPkAccountId(String pkAccountId){
		this.pkAccountId = pkAccountId;
	}

	public String getSCustId(){
		return this.SCustId;
	}

	public void setSCustId(String SCustId){
		this.SCustId = SCustId;
	}

	public String getSAccountNo(){
		return this.SAccountNo;
	}

	public void setSAccountNo(String SAccountNo){
		this.SAccountNo = SAccountNo;
	}

	public String getSAccountType(){
		return this.SAccountType;
	}

	public void setSAccountType(String SAccountType){
		this.SAccountType = SAccountType;
	}

	public String getSCustBankName(){
		return this.SCustBankName;
	}

	public void setSCustBankName(String SCustBankName){
		this.SCustBankName = SCustBankName;
	}

	public String getSCustBankCode(){
		return this.SCustBankCode;
	}

	public void setSCustBankCode(String SCustBankCode){
		this.SCustBankCode = SCustBankCode;
	}

		
	/**
	 * @return sLoginName
	 */
	public String getSLoginName(){
		return SLoginName;
	}

	
	/**
	 * @param loginName 要设置的 sLoginName
	 */
	public void setSLoginName(String loginName){
		SLoginName = loginName;
	}

	
	/**
	 * @return sPassword
	 */
	public String getSPassword(){
		return SPassword;
	}

	
	/**
	 * @param password 要设置的 sPassword
	 */
	public void setSPassword(String password){
		SPassword = password;
	}

	
	/**
	 * @return newPassword
	 */
	public String getNewPassword(){
		return newPassword;
	}

	
	/**
	 * @param newPassword 要设置的 newPassword
	 */
	public void setNewPassword(String newPassword){
		this.newPassword = newPassword;
	}

	public String getSAccountName() {
		return SAccountName;
	}

	public void setSAccountName(String sAccountName) {
		SAccountName = sAccountName;
	}

	public String getSignFlag() {
		return signFlag;
	}

	public void setSignFlag(String signFlag) {
		this.signFlag = signFlag;
	}

	public String getSignOperId() {
		return signOperId;
	}

	public void setSignOperId(String signOperId) {
		this.signOperId = signOperId;
	}

	public String getSignCancelOperId() {
		return signCancelOperId;
	}

	public void setSignCancelOperId(String signCancelOperId) {
		this.signCancelOperId = signCancelOperId;
	}

	public String getBrchId() {
		return brchId;
	}

	public void setBrchId(String brchId) {
		this.brchId = brchId;
	}

	public Date getSignDate() {
		return signDate;
	}

	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}

	public Date getSignCancelDate() {
		return signCancelDate;
	}

	public void setSignCancelDate(Date signCancelDate) {
		this.signCancelDate = signCancelDate;
	}

}