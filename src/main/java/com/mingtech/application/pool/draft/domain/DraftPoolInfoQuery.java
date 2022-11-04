package com.mingtech.application.pool.draft.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author wbyecheng
 * 
 *         发托查询实体
 * 
 */
public class DraftPoolInfoQuery {
	private String billinfoId; // 票据id
	private String SBillNo; // 票号
	private String SBillMedia; // 票据介质
	private String SBillType; // 票据种类
	private BigDecimal FBillAmount; // 票面金额
	private Date DIssueDt; // 出票日
	private Date DDueDt; // 到期日
	private String SAcceptor; // 承兑人全称（付款人）
	private String SAcceptorBankCode; // 承兑行行号（付款人开户行）
	private String SIssuerName; // 出票人名称
	private String SIssuerBankCode;// 出票人开户行行号
	private String SIssuerBankName;// 出票人开户行名称
	private String SBillFrom; // 票据来源

	/********************以下为不常用字段*********************/
//	private String SAcceptorBankName; // 承兑行名称（付款人开户行）
//	private String SAcceptorAddress; // 承兑人地址
//	private String SAcceptorPhone; // 承兑行电话
//	private String SAcceptorEmail; // 承兑行邮编
//	private String TsZancun; // 暂存内容
	private String paymentBankCode; // 兑付行行号
	private String paymentBankName; // 兑付行名称
	private String paymentBankAddress; // 兑付行地址
	private String paymentBankPhone; // 兑付行电话
	private String paymentBankPostCode;// 兑付行邮编

	public DraftPoolInfoQuery(String billinfoId, String sBillNo,
			String sBillMedia, String sBillType, BigDecimal fBillAmount,
			Date dIssueDt, Date dDueDt, String sAcceptor,
			String sAcceptorBankCode, String sIssuerName,
			String sIssuerBankCode, String sIssuerBankName, String paymentBankCode, String paymentBankName, String paymentBankAddress,String SBillFrom) {
		super();
		this.billinfoId = billinfoId;
		SBillNo = sBillNo;
		SBillMedia = sBillMedia;
		SBillType = sBillType;
		FBillAmount = fBillAmount;
		DIssueDt = dIssueDt;
		DDueDt = dDueDt;
		SAcceptor = sAcceptor;
		SAcceptorBankCode = sAcceptorBankCode;
		SIssuerName = sIssuerName;
		SIssuerBankCode = sIssuerBankCode;
		SIssuerBankName = sIssuerBankName;
		this.paymentBankCode = paymentBankCode;
		this.paymentBankName = paymentBankName;
		this.paymentBankAddress = paymentBankAddress;
		this.SBillFrom = SBillFrom;
	}

	public String getBillinfoId() {
		return billinfoId;
	}

	public void setBillinfoId(String billinfoId) {
		this.billinfoId = billinfoId;
	}

	public String getSBillNo() {
		return SBillNo;
	}

	public void setSBillNo(String sBillNo) {
		SBillNo = sBillNo;
	}

	public String getSBillMedia() {
		return SBillMedia;
	}

	public void setSBillMedia(String sBillMedia) {
		SBillMedia = sBillMedia;
	}

	public String getSBillType() {
		return SBillType;
	}

	public void setSBillType(String sBillType) {
		SBillType = sBillType;
	}

	public BigDecimal getFBillAmount() {
		return FBillAmount;
	}

	public void setFBillAmount(BigDecimal fBillAmount) {
		FBillAmount = fBillAmount;
	}

	public Date getDIssueDt() {
		return DIssueDt;
	}

	public void setDIssueDt(Date dIssueDt) {
		DIssueDt = dIssueDt;
	}

	public Date getDDueDt() {
		return DDueDt;
	}

	public void setDDueDt(Date dDueDt) {
		DDueDt = dDueDt;
	}

	public String getSAcceptor() {
		return SAcceptor;
	}

	public void setSAcceptor(String sAcceptor) {
		SAcceptor = sAcceptor;
	}

	public String getSAcceptorBankCode() {
		return SAcceptorBankCode;
	}

	public void setSAcceptorBankCode(String sAcceptorBankCode) {
		SAcceptorBankCode = sAcceptorBankCode;
	}

	public String getSIssuerName() {
		return SIssuerName;
	}

	public void setSIssuerName(String sIssuerName) {
		SIssuerName = sIssuerName;
	}

	public String getSIssuerBankCode() {
		return SIssuerBankCode;
	}

	public void setSIssuerBankCode(String sIssuerBankCode) {
		SIssuerBankCode = sIssuerBankCode;
	}

	public String getSIssuerBankName() {
		return SIssuerBankName;
	}

	public void setSIssuerBankName(String sIssuerBankName) {
		SIssuerBankName = sIssuerBankName;
	}

	public String getSBillFrom() {
		return SBillFrom;
	}

	public void setSBillFrom(String sBillFrom) {
		SBillFrom = sBillFrom;
	}
//
//	public String getSAcceptorBankName() {
//		return SAcceptorBankName;
//	}
//
//	public void setSAcceptorBankName(String sAcceptorBankName) {
//		SAcceptorBankName = sAcceptorBankName;
//	}
//
//	public String getSAcceptorAddress() {
//		return SAcceptorAddress;
//	}
//
//	public void setSAcceptorAddress(String sAcceptorAddress) {
//		SAcceptorAddress = sAcceptorAddress;
//	}
//
//	public String getSAcceptorPhone() {
//		return SAcceptorPhone;
//	}
//
//	public void setSAcceptorPhone(String sAcceptorPhone) {
//		SAcceptorPhone = sAcceptorPhone;
//	}
//
//	public String getSAcceptorEmail() {
//		return SAcceptorEmail;
//	}
//
//	public void setSAcceptorEmail(String sAcceptorEmail) {
//		SAcceptorEmail = sAcceptorEmail;
//	}
//
//	public String getTsZancun() {
//		return TsZancun;
//	}
//
//	public void setTsZancun(String tsZancun) {
//		TsZancun = tsZancun;
//	}

	public String getPaymentBankCode() {
		return paymentBankCode;
	}

	public void setPaymentBankCode(String paymentBankCode) {
		this.paymentBankCode = paymentBankCode;
	}

	public String getPaymentBankName() {
		return paymentBankName;
	}

	public void setPaymentBankName(String paymentBankName) {
		this.paymentBankName = paymentBankName;
	}

	public String getPaymentBankAddress() {
		return paymentBankAddress;
	}

	public void setPaymentBankAddress(String paymentBankAddress) {
		this.paymentBankAddress = paymentBankAddress;
	}

	public String getPaymentBankPhone() {
		return paymentBankPhone;
	}

	public void setPaymentBankPhone(String paymentBankPhone) {
		this.paymentBankPhone = paymentBankPhone;
	}

	public String getPaymentBankPostCode() {
		return paymentBankPostCode;
	}

	public void setPaymentBankPostCode(String paymentBankPostCode) {
		this.paymentBankPostCode = paymentBankPostCode;
	}

}