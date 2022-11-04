package com.mingtech.application.pool.financial.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * PedCreditCalculation entity. @author MyEclipse Persistence Tools
 */

public class CreditCalculation implements java.io.Serializable {


	private static final long serialVersionUID = 5488533603456442207L;


	private String id;               //主键                          
	private String custPoolName;     //客户资产池名称                     
	private String certType;         //证件类型:01组织机构代码、02统一授信编码、03客户号
	private String certCode;         //证件号                         
	private String bpsNo;            //客户票据池编号                     
	private Date startDate;          //开始日期                        
	private Date endDate;            //结束日期                        
	private BigDecimal lowRiskIn= BigDecimal.ZERO;        //低风险流入资金                     
	private BigDecimal lowRiskOut = BigDecimal.ZERO;       //低风险流出资金                     
	private BigDecimal lowRiskCashFlow = BigDecimal.ZERO;  //低风险资金实点                     
	private BigDecimal highRiskIn = BigDecimal.ZERO;       //高风险流入资金                     
	private BigDecimal highRiskOut = BigDecimal.ZERO;      //高风险流出资金                     
	private BigDecimal highRiskCashFlow = BigDecimal.ZERO; //高风险资金实点                     
	private BigDecimal lowRiskCredit = BigDecimal.ZERO;    //低风险可用额度                     
	private BigDecimal highRiskCredit = BigDecimal.ZERO;   //高风险可用额度                     
	private BigDecimal allCredit= BigDecimal.ZERO;//（不落库字段）全部可用额度  = 低风险可用额度  + 高风险可用额度
	private Date transDate;          //交易日期(不含时分秒)                 
	private Date createDate;         //创建日期(含时分秒)                  
	private Date updateDate;         //更新日期(含时分秒)                  
	private String delFlag;          //逻辑删除标记-N未删除、D已删除         

	
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustPoolName() {
		return this.custPoolName;
	}

	public void setCustPoolName(String custPoolName) {
		this.custPoolName = custPoolName;
	}

	public String getCertType() {
		return this.certType;
	}

	public void setCertType(String certType) {
		this.certType = certType;
	}

	public String getCertCode() {
		return this.certCode;
	}

	public void setCertCode(String certCode) {
		this.certCode = certCode;
	}

	public String getBpsNo() {
		return this.bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getLowRiskIn() {
		return this.lowRiskIn;
	}

	public void setLowRiskIn(BigDecimal lowRiskIn) {
		this.lowRiskIn = lowRiskIn;
	}

	public BigDecimal getLowRiskOut() {
		return this.lowRiskOut;
	}

	public void setLowRiskOut(BigDecimal lowRiskOut) {
		this.lowRiskOut = lowRiskOut;
	}

	public BigDecimal getLowRiskCashFlow() {
		return this.lowRiskCashFlow;
	}

	public void setLowRiskCashFlow(BigDecimal lowRiskCashFlow) {
		this.lowRiskCashFlow = lowRiskCashFlow;
	}

	public BigDecimal getHighRiskIn() {
		return this.highRiskIn;
	}

	public void setHighRiskIn(BigDecimal highRiskIn) {
		this.highRiskIn = highRiskIn;
	}

	public BigDecimal getHighRiskOut() {
		return this.highRiskOut;
	}

	public void setHighRiskOut(BigDecimal highRiskOut) {
		this.highRiskOut = highRiskOut;
	}

	public BigDecimal getHighRiskCashFlow() {
		return this.highRiskCashFlow;
	}

	public void setHighRiskCashFlow(BigDecimal highRiskCashFlow) {
		this.highRiskCashFlow = highRiskCashFlow;
	}

	public BigDecimal getLowRiskCredit() {
		return this.lowRiskCredit;
	}

	public void setLowRiskCredit(BigDecimal lowRiskCredit) {
		this.lowRiskCredit = lowRiskCredit;
	}

	public BigDecimal getHighRiskCredit() {
		return this.highRiskCredit;
	}

	public void setHighRiskCredit(BigDecimal highRiskCredit) {
		this.highRiskCredit = highRiskCredit;
	}
	

	public BigDecimal getAllCredit() {
		allCredit = this.getLowRiskCredit().add(this.getHighRiskCredit());
		return allCredit;
	}

	public void setAllCredit(BigDecimal allCredit) {
		this.allCredit = allCredit;
	}

	public Date getTransDate() {
		return this.transDate;
	}

	public void setTransDate(Date transDate) {
		this.transDate = transDate;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return this.updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getDelFlag() {
		return this.delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

}