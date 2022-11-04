package com.mingtech.application.pool.online.loan.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.ecds.common.DictionaryCache;

/**
 * PedOnlineCrdtInfoHist entity. @author MyEclipse Persistence Tools
 */

public class PedOnlineCrdtInfoHist implements java.io.Serializable {

	// Fields

	private String id;                 //主键ID
	private String payeeId;            //收款人编号
	private String payeeAcctName;      //收款人名称
	private String payeeAcctNo;        //收款人账号
	private String payeeOpenBankNo;    //收款人开户行行号
	private String payeeOpenBankName;  //收款人开户行名称
	private String isLocal;            //跨行标识
	private BigDecimal payeeTotalAmt;  //收款人收款总额
	private BigDecimal payeeUsedAmt;   //收款人已收款金额
	private String payeeStatus;        //收款人状态
	private String onlineCrdtNo;       //在线协议编号
	private String crdtId;             //在线协议主键
	private String modeType;           //修改模式
	private String modeMark;           //修改标记
	private String lastSourceId;       //上一次修改的id
	private Date createTime;         //创建时间
	private Date updateTime;         //最近修改时间
	//页面展示
	private String modeTypeDesc;
	private String payType;          //支付类型 1自主支付 2 受托支付
	

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	// Property accessors
	
	public String getId() {
		return this.id;
	}

	public String getLastSourceId() {
		return lastSourceId;
	}



	public void setLastSourceId(String lastSourceId) {
		this.lastSourceId = lastSourceId;
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

	public String getIsLocal() {
		return this.isLocal;
	}

	public void setIsLocal(String isLocal) {
		this.isLocal = isLocal;
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

	public String getOnlineCrdtNo() {
		return this.onlineCrdtNo;
	}

	public void setOnlineCrdtNo(String onlineCrdtNo) {
		this.onlineCrdtNo = onlineCrdtNo;
	}

	public String getCrdtId() {
		return this.crdtId;
	}

	public void setCrdtId(String crdtId) {
		this.crdtId = crdtId;
	}

	public String getModeType() {
		return this.modeType;
	}

	public void setModeType(String modeType) {
		this.modeType = modeType;
	}

	public String getModeMark() {
		return this.modeMark;
	}

	public void setModeMark(String modeMark) {
		this.modeMark = modeMark;
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
	
	public String getModeTypeDesc() {
		return modeTypeDesc = DictionaryCache.getModeTypeName(this.getModeType());
	}

}