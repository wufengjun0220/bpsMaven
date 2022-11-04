package com.mingtech.application.pool.edu.domain;

import java.util.Date;
import java.util.List;


/**
 * 该实体类提供CreditPproduct及PedCreditDetail
 * @Description TODO
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-26
 */
public class CreditQueryBean {
	
	/**公共字段*/
	private String bpsNo;//票据池编号
	private String custNo;//核心客户号
	private String crdtNo;//信贷业务号
	
	/**CreditPproduct字段*/
	private String crdtType;//信贷产品类型  XD_01银承   XD_02流贷   XD_03保函   XD_04 信用证
	private String sttlFlag;//结清标记
	private String crdtStatus;//业务状态	
	private String riskLevel;//风险类型
	private List<String> crdtTypeList;//信贷产品类型 
	private List<String> sttlFlagList;//结清标记
	private List<String> crdtStatusList;////业务状态	
	
	/**PedCreditDetail字段*/
    private String loanNo;//借据号
    private String loanType;//交易类型   （XD_01:银承  XD_02:流贷  XD_03:保函  XD_04:信用证  XD_05:表外业务垫款）
    private String loanStatus;//交易状态（JJ_01:已放款  JJ_02:部分还款 JJ_03:逾期/垫款 JJ_04:结清）
    private String transAccount;//交易账号 （表外业务对应业务保证金账号，表内业务对应贷款账号）
    private String detailStatus;//借据状态,0-不在处理,1-还需处理
    private List<String> loanTypeList;//交易类型 
    private List<String> loanStatusList;//交易状态
    
    
	private String astType;//池资产类型：ED_10:票据池；ED_20:高风险票据池；ED_21:保证金池-活期保证金；ED_22:保证金池-定期保证金
	private String apId;//资产池ID
	private String asstDtlId;//池资产明细ID
	private String asstDtlNo;//记录使用的池资产的业务号，如：票据-票号；定期活期保证金账号    
	
	private String isOnline;//是否线上 1 是 0 否
	
	private List<String> loanStatusNotInLsit;//排除的借据状态
	private Date endDate;//到期日
    
	
	
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public List<String> getLoanStatusNotInLsit() {
		return loanStatusNotInLsit;
	}
	public void setLoanStatusNotInLsit(List<String> loanStatusNotInLsit) {
		this.loanStatusNotInLsit = loanStatusNotInLsit;
	}
	public String getIsOnline() {
		return isOnline;
	}
	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}
	public String getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}
	public String getAstType() {
		return astType;
	}
	public void setAstType(String astType) {
		this.astType = astType;
	}
	public String getApId() {
		return apId;
	}
	public void setApId(String apId) {
		this.apId = apId;
	}
	public String getAsstDtlId() {
		return asstDtlId;
	}
	public void setAsstDtlId(String asstDtlId) {
		this.asstDtlId = asstDtlId;
	}
	public String getAsstDtlNo() {
		return asstDtlNo;
	}
	public void setAsstDtlNo(String asstDtlNo) {
		this.asstDtlNo = asstDtlNo;
	}
	public String getBpsNo() {
		return bpsNo;
	}
	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}
	public String getCustNo() {
		return custNo;
	}
	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}
	public String getCrdtNo() {
		return crdtNo;
	}
	public void setCrdtNo(String crdtNo) {
		this.crdtNo = crdtNo;
	}
	public String getCrdtType() {
		return crdtType;
	}
	public void setCrdtType(String crdtType) {
		this.crdtType = crdtType;
	}
	public String getSttlFlag() {
		return sttlFlag;
	}
	public void setSttlFlag(String sttlFlag) {
		this.sttlFlag = sttlFlag;
	}
	public String getCrdtStatus() {
		return crdtStatus;
	}
	public void setCrdtStatus(String crdtStatus) {
		this.crdtStatus = crdtStatus;
	}
	public List<String> getCrdtTypeList() {
		return crdtTypeList;
	}
	public void setCrdtTypeList(List<String> crdtTypeList) {
		this.crdtTypeList = crdtTypeList;
	}
	public List<String> getSttlFlagList() {
		return sttlFlagList;
	}
	public void setSttlFlagList(List<String> sttlFlagList) {
		this.sttlFlagList = sttlFlagList;
	}
	public List<String> getCrdtStatusList() {
		return crdtStatusList;
	}
	public void setCrdtStatusList(List<String> crdtStatusList) {
		this.crdtStatusList = crdtStatusList;
	}
	public String getLoanNo() {
		return loanNo;
	}
	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}
	public String getLoanType() {
		return loanType;
	}
	public void setLoanType(String loanType) {
		this.loanType = loanType;
	}
	public String getLoanStatus() {
		return loanStatus;
	}
	public void setLoanStatus(String loanStatus) {
		this.loanStatus = loanStatus;
	}
	public String getTransAccount() {
		return transAccount;
	}
	public void setTransAccount(String transAccount) {
		this.transAccount = transAccount;
	}
	public String getDetailStatus() {
		return detailStatus;
	}
	public void setDetailStatus(String detailStatus) {
		this.detailStatus = detailStatus;
	}
	public List<String> getLoanTypeList() {
		return loanTypeList;
	}
	public void setLoanTypeList(List<String> loanTypeList) {
		this.loanTypeList = loanTypeList;
	}
	public List<String> getLoanStatusList() {
		return loanStatusList;
	}
	public void setLoanStatusList(List<String> loanStatusList) {
		this.loanStatusList = loanStatusList;
	}
    
    
    

}
