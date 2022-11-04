package com.mingtech.application.pool.online.acception.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.ecds.common.PublicStaticDefineTab;

/**
 * PedOnlineAcptInfo entity. @author MyEclipse Persistence Tools
 */

public class PedOnlineAcptInfo implements java.io.Serializable {

	// Fields
                                     
	private String id;               
	private String payeeId;          //收票人编号
	private String payeeCustName;	//收票人客户名称
	private String payeeAcctName;    //收票人账户名称
	private String payeeAcctNo;      //收票人账号
	private String payeeOpenBankNo;  //收票人开户行行号
	private String payeeOpenBankName;//收票人开户行名称
	private BigDecimal payeeTotalAmt;//收票人收票总额
	private BigDecimal payeeUsedAmt; //收票人已收票金额
	private String payeeStatus;      //收票人状态
	private String onlineAcptNo;     //在线银承协议编号
	private String acptId;           //在线银承协议主键
	private String modeType;         //修改模式
	private Date createTime;         //创建时间
	private Date updateTime;         //最近修改时间
	//页面展示
	private String payeeStatusDesc;      //收票人状态
	private BigDecimal availableAmt; //可用额度

	// Constructors

	/** default constructor */
	public PedOnlineAcptInfo() {
	}


	// Property accessors

	public String getId() {
		return this.id;
	}

	public String getPayeeCustName() {
		return payeeCustName;
	}


	public void setPayeeCustName(String payeeCustName) {
		this.payeeCustName = payeeCustName;
	}


	public void setId(String id) {
		this.id = id;
	}

	public String getPayeeId() {
		return this.payeeId;
	}

	public void setPayeeId(String payeeId) {
		this.payeeId = payeeId;
	}

	public String getPayeeAcctName() {
		return this.payeeAcctName;
	}

	public void setPayeeAcctName(String payeeAcctName) {
		this.payeeAcctName = payeeAcctName;
	}

	public String getPayeeAcctNo() {
		return this.payeeAcctNo;
	}

	public void setPayeeAcctNo(String payeeAcctNo) {
		this.payeeAcctNo = payeeAcctNo;
	}

	public String getPayeeOpenBankNo() {
		return this.payeeOpenBankNo;
	}

	public void setPayeeOpenBankNo(String payeeOpenBankNo) {
		this.payeeOpenBankNo = payeeOpenBankNo;
	}

	public String getPayeeOpenBankName() {
		return this.payeeOpenBankName;
	}

	public void setPayeeOpenBankName(String payeeOpenBankName) {
		this.payeeOpenBankName = payeeOpenBankName;
	}

	public BigDecimal getPayeeTotalAmt() {
		return this.payeeTotalAmt;
	}

	public void setPayeeTotalAmt(BigDecimal payeeTotalAmt) {
		this.payeeTotalAmt = payeeTotalAmt;
	}

	public BigDecimal getPayeeUsedAmt() {
		return this.payeeUsedAmt;
	}

	public void setPayeeUsedAmt(BigDecimal payeeUsedAmt) {
		this.payeeUsedAmt = payeeUsedAmt;
	}

	public String getPayeeStatus() {
		return this.payeeStatus;
	}

	public void setPayeeStatus(String payeeStatus) {
		this.payeeStatus = payeeStatus;
	}

	public String getOnlineAcptNo() {
		return this.onlineAcptNo;
	}

	public void setOnlineAcptNo(String onlineAcptNo) {
		this.onlineAcptNo = onlineAcptNo;
	}

	public String getAcptId() {
		return this.acptId;
	}

	public void setAcptId(String acptId) {
		this.acptId = acptId;
	}

	public Date getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getModeType() {
		return modeType;
	}

	public void setModeType(String modeType) {
		this.modeType = modeType;
	}

	public String getPayeeStatusDesc() {
		if(PublicStaticDefineTab.STATUS_0.equals(this.payeeStatus)){
			return "失效-"+this.payeeStatus;
		}else if(PublicStaticDefineTab.STATUS_1.equals(this.payeeStatus)){
			return "生效-"+this.payeeStatus;
		}else{
			return payeeStatusDesc;
		}
	}

	public void setPayeeStatusDesc(String payeeStatusDesc) {
		this.payeeStatusDesc = payeeStatusDesc;
	}


	public BigDecimal getAvailableAmt() {
		if(null != this.getPayeeUsedAmt() && null != this.getPayeeTotalAmt()){
			return this.getPayeeTotalAmt().subtract(this.getPayeeUsedAmt());
		}else{
			return payeeTotalAmt;
		}
	}


	public void setAvailableAmt(BigDecimal availableAmt) {
		this.availableAmt = availableAmt;
	}
	
	
	
	

}