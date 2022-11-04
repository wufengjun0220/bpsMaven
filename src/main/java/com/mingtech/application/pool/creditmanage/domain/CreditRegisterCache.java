package com.mingtech.application.pool.creditmanage.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.framework.common.util.DateUtils;

/**
 * 融资业务登记临时缓存表
 * @author Ju Nana
 * @version v1.0
 * @date 2021-6-7
 * @copyright 北明明润（北京）科技有限责任公司
 */

public class CreditRegisterCache implements java.io.Serializable {


	private static final long serialVersionUID = -4856693600887051559L;

	private String id;           //主键  
	private String apId;//资产池表主键
	private String bpsNo;        //票据池协议编号                                          
	private String busiId;       //原业务ID-主业务合同表ID或借据表ID                             
	private String busiNo;       //原业务编号-主业务合同号或借据号                                 
	private String voucherType;  //凭证类型:0主业务合同、1借据                                  
	private String contractNo;   //主业务合同号                                           
	private String isOnline;     //是否线上业务-0否、1是                                     
	private String busiType;     //业务类型-XD_01银承、XD_02流贷、XD_03保函、XD_04信用证、XD_05表外业务垫款
	private String riskType;     //风险类型-低风险FX_01、高风险FX_02、不在风险名单FX_03               
	private BigDecimal busiAmount;   //业务金额(合同金额、借据金额)                                  
	private BigDecimal occupyRatio;  //占用比例-0.##-1                                      
	private BigDecimal occupyAmount; //业务实际占用金额                                         
	private BigDecimal occupyCredit; //实际占用额度             
	private Date dueDt;        //到期日                                              
	private Date transDate;      //交易日期(不含时分秒)                                      
	private Date createDate;     //创建日期(含时分秒)                                       
	private Date updateDate;     //更新日期(含时分秒)                                       
	private String flowNo;       //流水号  
	
	
	public String getApId() {
		return apId;
	}

	public void setApId(String apId) {
		this.apId = apId;
	}

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

	public String getBusiId() {
		return this.busiId;
	}

	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}

	public String getBusiNo() {
		return this.busiNo;
	}

	public void setBusiNo(String busiNo) {
		this.busiNo = busiNo;
	}

	public String getVoucherType() {
		return this.voucherType;
	}

	public void setVoucherType(String voucherType) {
		this.voucherType = voucherType;
	}

	public String getContractNo() {
		return this.contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getIsOnline() {
		return this.isOnline;
	}

	public void setIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}

	public String getBusiType() {
		return this.busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getRiskType() {
		return this.riskType;
	}

	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}

	public BigDecimal getBusiAmount() {
		return this.busiAmount;
	}

	public void setBusiAmount(BigDecimal busiAmount) {
		this.busiAmount = busiAmount;
	}

	public BigDecimal getOccupyRatio() {
		return this.occupyRatio;
	}

	public void setOccupyRatio(BigDecimal occupyRatio) {
		this.occupyRatio = occupyRatio;
	}

	public BigDecimal getOccupyAmount() {
		return this.occupyAmount;
	}

	public void setOccupyAmount(BigDecimal occupyAmount) {
		this.occupyAmount = occupyAmount;
	}

	public BigDecimal getOccupyCredit() {
		return this.occupyCredit;
	}

	public void setOccupyCredit(BigDecimal occupyCredit) {
		this.occupyCredit = occupyCredit;
	}

	public Date getDueDt() {
		return dueDt;
	}

	public void setDueDt(Date dueDt) {
		this.dueDt = dueDt;
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

	public String getFlowNo() {
		return this.flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

}