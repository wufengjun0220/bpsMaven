package com.mingtech.application.pool.financial.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 额度计算资金流表封装对象
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-7
 * @copyright 北明明润（北京）科技有限责任公司
 */
public class CreditCalcutionBean implements java.io.Serializable {


	private static final long serialVersionUID = 638664952169032763L;

	private String id;              //主键                          
	private String custPoolName;    //客户资产池名称                     
	private String certType;        //:01组织机构代码、02统一授信编码、03客户号
	private String certCode;        //证件号                         
	private String bpsNo;           //客户票据池编号
	private String startDate;         //开始日期
	private String endDate;           //结束日期
	private BigDecimal lowRiskIn =  BigDecimal.ZERO;       //低风险流入资金                     
	private BigDecimal lowRiskOut =  BigDecimal.ZERO;      //低风险流出资金                     
	private BigDecimal lowRiskCashFlow =  BigDecimal.ZERO; //低风险资金实点                     
	private BigDecimal highRiskIn =  BigDecimal.ZERO;      //高风险流入资金                     
	private BigDecimal highRiskOut =  BigDecimal.ZERO;     //高风险流出资金                     
	private BigDecimal highRiskCashFlow =  BigDecimal.ZERO;//高风险资金实点                     
	private BigDecimal lowRiskCredit =  BigDecimal.ZERO;   //低风险可用额度                     
	private BigDecimal highRiskCredit =  BigDecimal.ZERO;  //高风险可用额度      
	BigDecimal allCredit= BigDecimal.ZERO;//（不落库字段）全部可用额度  = 低风险可用额度  + 高风险可用额度
	private Date transDate;         //交易日期(不含时分秒)                 
	private String delFlag;         //逻辑删除标记-N未删除、D已删除
	private String flowNo;          //流水号
	private BigDecimal addLowRiskOut =  BigDecimal.ZERO;      //增加低风险流出资金（页面使用）
	private BigDecimal addHighRiskOut =  BigDecimal.ZERO;     //增加高风险流出资金（页面使用）
	private BigDecimal lowRiskCashFlowNew =  BigDecimal.ZERO; //更新后低风险资金实点（页面使用）
	private BigDecimal highRiskCashFlowNew =  BigDecimal.ZERO;//更新后高风险资金实点 （页面使用）
	private String isAdeQuate;//额度是否充足 0:不足，1:充足（页面使用）

	

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

	public String getDelFlag() {
		return this.delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	public String getFlowNo() {
		return this.flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	public BigDecimal getAddLowRiskOut() {
		return addLowRiskOut;
	}

	public void setAddLowRiskOut(BigDecimal addLowRiskOut) {
		this.addLowRiskOut = addLowRiskOut;
	}

	public BigDecimal getAddHighRiskOut() {
		return addHighRiskOut;
	}

	public void setAddHighRiskOut(BigDecimal addHighRiskOut) {
		this.addHighRiskOut = addHighRiskOut;
	}

	public String getIsAdeQuate() {
		return isAdeQuate;
	}

	public void setIsAdeQuate(String isAdeQuate) {
		this.isAdeQuate = isAdeQuate;
	}

	public BigDecimal getLowRiskCashFlowNew() {
		return lowRiskCashFlowNew;
	}

	public void setLowRiskCashFlowNew(BigDecimal lowRiskCashFlowNew) {
		this.lowRiskCashFlowNew = lowRiskCashFlowNew;
	}

	public BigDecimal getHighRiskCashFlowNew() {
		return highRiskCashFlowNew;
	}

	public void setHighRiskCashFlowNew(BigDecimal highRiskCashFlowNew) {
		this.highRiskCashFlowNew = highRiskCashFlowNew;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
}