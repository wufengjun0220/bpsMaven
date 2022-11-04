package com.mingtech.application.ecds.draftcollection.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.ecds.common.DictionaryCache;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者：liuweijun
 * @日期：Jun 2, 2009 11:54:37 AM
 * @描述：[CollectionReceiveDto]提示付款（逾期提示付款）签收明细
 */
public class CollectionReceiveDto implements java.io.Serializable {


	private String collectionReceiveId;   //主键id
	//大票信息
	private BtBillInfo btBillInfo;   //大票信息
	private String SBillNo;          //票号
	private BigDecimal FBillAmount;  //票面金额
	private Date DIssueDt;           //出票日
	private Date DDueDt;             //到期日
	private String SBillMedia;       //票据介质
	private String SBillType;        //票据类型
	private String SBranchId;        //机构ID
	private String SOperatorId;      //操作员id
	//承兑人信息
	private String acceptRole; //承兑人角色
	private String acceptNm;        //承兑方名称
	private String acceptCmonId; //承兑方组织机构代码
	private String acceptAccount;        //承兑方帐号
	private String acceptAcctSvcr; //承兑方大额行号
	private String acceptBankName;//承兑方开户行名称
	private String acceptAgcySvcr;//承兑方承接行行号
	private String acceptElctrncsgntr;//承兑方电子签名
	private String prsnttnDshnrCode;//提示(逾期)付款拒付代码(拒付签收必填)
	private String prsnttnDshnrRsn;//拒付备注信息(拒付代码为DC09时必填)
	
	//提示付款信息
	private Date applDt;      //提示付款(或逾期提示付款)申请日期
	private Date applSignDt;  //提示付款(或逾期)签收日期
	private BigDecimal amt;   //提示付款(或逾期)金额
	private String rsn; //逾期原因说明
	private String clearWay;  //提示付款(或逾期)清算方式(线上清算标记？)
	private String prxyPropstn;//提示付款(或逾期)代理标识
	private String prxyAccept;//提示付款(或逾期)签收代理标识
	private String rmrkByPropsr;//提示付款(或逾期)提示付款人备注
	private String rmrkByAccept;//提示付款(或逾期)签收备注
	
	//提示付款人（或逾期提示付款人）信息
	private String collRole ;//提示付款人(或逾期)角色
	private String collNm;//提示付款人(或逾期)名称
	private String collCmonId;//提示付款人(或逾期)组织机构代码
	private String collElctrncSgntr;//提示付款人(或逾期)电子签名
	private String collAcct;//提示付款人(或逾期)帐号
	private String collAcctSvcr;//提示付款人(或逾期)大额行号
	private String collBankName;//提示付款人开户行名称
	private String collAgcyAcctSvcr;//提示付款人(或逾期)承接行行号
	
	//即时转账信息
	private String trfId;//支付交易序号
	
	//明细信息
	private String SBillStatus;//明细状态
	private String SMsgStatus;//明细报文状态
	private String SMsgCode;//明细报文信息(收到033时报文处理码描述)	
	private String overdueFlag;// 逾期标识
	private String isSysCustomer;//提示付款人是否系统内用户标识
	
	
	//临时变量,不保存数据库
	private String replySign;//签收回复标记SU00 SU01
	
   /**************相应代码所对中文名称只做页面显示**************/
	private String SBillMediaName;//票据介质中文名称
	private String SBillTypeName;//票据类型中文名称
	private String clearWayName;// 清算方式中文名称
	private String SBillStatusName;//明细中票据状态中文名
	private String SMsgStatusName;// 报文状态中文名称
	private String overdueFlagName;// 逾期标识
	private String isSysCustomerName;//提示付款人是否系统内用户标识中文名称
	
	
	// 记账日期、流水号、记账柜员信息2016
	private Date acctDate;
	private String acctFlowNo;//票据系统记账生成的记账流水号
	//记账柜员信息
	private String acctUserNo;
	private String acctUserName;
	//记账授权柜员信息；复核人员
	private String acctAuthUserNo;
	private String acctAuthUserName;
	
	//承兑表ID
	private String acceptionBillId;
	
	//结清类型
	private String balanceType;
	
	/** default constructor */
	public CollectionReceiveDto() {
	}


	public String getCollectionReceiveId() {
		return collectionReceiveId;
	}


	public void setCollectionReceiveId(String collectionReceiveId) {
		this.collectionReceiveId = collectionReceiveId;
	}


	public BtBillInfo getBtBillInfo() {
		return btBillInfo;
	}


	public void setBtBillInfo(BtBillInfo btBillInfo) {
		this.btBillInfo = btBillInfo;
	}


	public String getSBillNo() {
		return SBillNo;
	}


	public void setSBillNo(String billNo) {
		SBillNo = billNo;
	}


	public BigDecimal getFBillAmount() {
		return FBillAmount;
	}


	public void setFBillAmount(BigDecimal billAmount) {
		FBillAmount = billAmount;
	}


	public Date getDIssueDt() {
		return DIssueDt;
	}


	public void setDIssueDt(Date issueDt) {
		DIssueDt = issueDt;
	}


	public Date getDDueDt() {
		return DDueDt;
	}


	public void setDDueDt(Date dueDt) {
		DDueDt = dueDt;
	}


	public String getSBillMedia() {
		return SBillMedia;
	}


	public void setSBillMedia(String billMedia) {
		SBillMedia = billMedia;
	}


	public String getSBillType() {
		return SBillType;
	}


	public void setSBillType(String billType) {
		SBillType = billType;
	}


	public String getAcceptRole() {
		return acceptRole;
	}


	public void setAcceptRole(String acceptRole) {
		this.acceptRole = acceptRole;
	}


	public String getAcceptNm() {
		return acceptNm;
	}


	public void setAcceptNm(String acceptNm) {
		this.acceptNm = acceptNm;
	}


	public String getAcceptCmonId() {
		return acceptCmonId;
	}


	public void setAcceptCmonId(String acceptCmonId) {
		this.acceptCmonId = acceptCmonId;
	}


	public String getAcceptAccount() {
		return acceptAccount;
	}


	public void setAcceptAccount(String acceptAccount) {
		this.acceptAccount = acceptAccount;
	}


	public String getAcceptAcctSvcr() {
		return acceptAcctSvcr;
	}

	public void setAcceptAcctSvcr(String acceptAcctSvcr) {
		this.acceptAcctSvcr = acceptAcctSvcr;
	}


	public String getAcceptBankName() {
		return acceptBankName;
	}


	public void setAcceptBankName(String acceptBankName) {
		this.acceptBankName = acceptBankName;
	}


	public String getAcceptAgcySvcr() {
		return acceptAgcySvcr;
	}

	public void setAcceptAgcySvcr(String acceptAgcySvcr) {
		this.acceptAgcySvcr = acceptAgcySvcr;
	}

	public String getAcceptElctrncsgntr() {
		return acceptElctrncsgntr;
	}

	public void setAcceptElctrncsgntr(String acceptElctrncsgntr) {
		this.acceptElctrncsgntr = acceptElctrncsgntr;
	}

	public String getPrsnttnDshnrCode() {
		return prsnttnDshnrCode;
	}

	public void setPrsnttnDshnrCode(String prsnttnDshnrCode) {
		this.prsnttnDshnrCode = prsnttnDshnrCode;
	}

	public String getPrsnttnDshnrRsn() {
		return prsnttnDshnrRsn;
	}
	
	public void setPrsnttnDshnrRsn(String prsnttnDshnrRsn) {
		this.prsnttnDshnrRsn = prsnttnDshnrRsn;
	}


	public Date getApplDt() {
		return applDt;
	}


	public void setApplDt(Date applDt) {
		this.applDt = applDt;
	}


	public Date getApplSignDt() {
		return applSignDt;
	}


	public void setApplSignDt(Date applSignDt) {
		this.applSignDt = applSignDt;
	}


	public BigDecimal getAmt() {
		return amt;
	}


	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}


	public String getRsn() {
		return rsn;
	}


	public void setRsn(String rsn) {
		this.rsn = rsn;
	}


	public String getClearWay() {
		return clearWay;
	}


	public void setClearWay(String clearWay) {
		this.clearWay = clearWay;
	}


	public String getPrxyPropstn() {
		return prxyPropstn;
	}


	public void setPrxyPropstn(String prxyPropstn) {
		this.prxyPropstn = prxyPropstn;
	}


	public String getPrxyAccept() {
		return prxyAccept;
	}


	public void setPrxyAccept(String prxyAccept) {
		this.prxyAccept = prxyAccept;
	}


	public String getRmrkByPropsr() {
		return rmrkByPropsr;
	}


	public void setRmrkByPropsr(String rmrkByPropsr) {
		this.rmrkByPropsr = rmrkByPropsr;
	}


	public String getRmrkByAccept() {
		return rmrkByAccept;
	}


	public void setRmrkByAccept(String rmrkByAccept) {
		this.rmrkByAccept = rmrkByAccept;
	}


	public String getCollRole() {
		return collRole;
	}


	public void setCollRole(String collRole) {
		this.collRole = collRole;
	}


	public String getCollNm() {
		return collNm;
	}


	public void setCollNm(String collNm) {
		this.collNm = collNm;
	}


	public String getCollCmonId() {
		return collCmonId;
	}


	public void setCollCmonId(String collCmonId) {
		this.collCmonId = collCmonId;
	}


	public String getCollElctrncSgntr() {
		return collElctrncSgntr;
	}


	public void setCollElctrncSgntr(String collElctrncSgntr) {
		this.collElctrncSgntr = collElctrncSgntr;
	}


	public String getCollAcct() {
		return collAcct;
	}


	public void setCollAcct(String collAcct) {
		this.collAcct = collAcct;
	}


	public String getCollAcctSvcr() {
		return collAcctSvcr;
	}

	public void setCollAcctSvcr(String collAcctSvcr) {
		this.collAcctSvcr = collAcctSvcr;
	}


	public String getCollBankName() {
		return collBankName;
	}


	public void setCollBankName(String collBankName) {
		this.collBankName = collBankName;
	}


	public String getCollAgcyAcctSvcr() {
		return collAgcyAcctSvcr;
	}


	public void setCollAgcyAcctSvcr(String collAgcyAcctSvcr) {
		this.collAgcyAcctSvcr = collAgcyAcctSvcr;
	}

	public String getTrfId() {
		return trfId;
	}

	public void setTrfId(String trfId) {
		this.trfId = trfId;
	}

	public String getSBillStatus() {
		return SBillStatus;
	}


	public void setSBillStatus(String billStatus) {
		SBillStatus = billStatus;
	}


	public String getSMsgStatus() {
		return SMsgStatus;
	}


	public void setSMsgStatus(String msgStatus) {
		SMsgStatus = msgStatus;
	}


	public String getSMsgCode() {
		return SMsgCode;
	}


	public void setSMsgCode(String msgCode) {
		SMsgCode = msgCode;
	}


	public String getOverdueFlag() {
		return overdueFlag;
	}


	public void setOverdueFlag(String overdueFlag) {
		this.overdueFlag = overdueFlag;
	}

	public String getIsSysCustomer() {
		return isSysCustomer;
	}


	public void setIsSysCustomer(String isSysCustomer) {
		this.isSysCustomer = isSysCustomer;
	}


	public String getReplySign() {
		return replySign;
	}
	public void setReplySign(String replySign) {
		this.replySign = replySign;
	}
	
	public String getSBillMediaName() {
		return SBillMediaName = DictionaryCache.getBillMedia(this
				.getSBillMedia());
	}


	public void setSBillMediaName(String billMediaName) {
		SBillMediaName = billMediaName;
	}


	public String getSBillTypeName() {
		return SBillTypeName = DictionaryCache.getBillType(this.getSBillType());
	}


	public void setSBillTypeName(String billTypeName) {
		SBillTypeName = billTypeName;
	}


	public String getClearWayName() {
		return clearWayName = (String) DictionaryCache.getStatusName(this
				.getClearWay());
	}


	public void setClearWayName(String clearWayName) {
		this.clearWayName = clearWayName;
	}


	public String getSBillStatusName() {
		return SBillStatusName = DictionaryCache.getStatusName(this.getSBillStatus());
	}


	public void setSBillStatusName(String billStatusName) {
		SBillStatusName = billStatusName;
	}


	public String getSMsgStatusName() {
		return SMsgStatusName = DictionaryCache.getStatusName(this.getSMsgStatus());
	}


	public void setSMsgStatusName(String msgStatusName) {
		SMsgStatusName = msgStatusName;
	}


	public String getOverdueFlagName() {
		return overdueFlagName = DictionaryCache.getOverFlagTypeMap(this.getOverdueFlag());
	}
	public void setOverdueFlagName(String overdueFlagName) {
		this.overdueFlagName = overdueFlagName;
	}
	
	public String getIsSysCustomerName() {
		return isSysCustomerName;
	}

	public void setIsSysCustomerName(String isSysCustomerName) {
		this.isSysCustomerName = (isSysCustomer != null && isSysCustomer.equals(PublicStaticDefineTab.SYS_INNER)?"系统内":"系统外");
	}


	public String getSBranchId() {
		return SBranchId;
	}


	public void setSBranchId(String sBranchId) {
		SBranchId = sBranchId;
	}


	public String getSOperatorId() {
		return SOperatorId;
	}


	public void setSOperatorId(String sOperatorId) {
		SOperatorId = sOperatorId;
	}


	public Date getAcctDate() {
		return acctDate;
	}


	public void setAcctDate(Date acctDate) {
		this.acctDate = acctDate;
	}


	public String getAcctFlowNo() {
		return acctFlowNo;
	}


	public void setAcctFlowNo(String acctFlowNo) {
		this.acctFlowNo = acctFlowNo;
	}


	public String getAcctUserNo() {
		return acctUserNo;
	}


	public void setAcctUserNo(String acctUserNo) {
		this.acctUserNo = acctUserNo;
	}


	public String getAcctUserName() {
		return acctUserName;
	}


	public void setAcctUserName(String acctUserName) {
		this.acctUserName = acctUserName;
	}


	public String getAcctAuthUserNo() {
		return acctAuthUserNo;
	}


	public void setAcctAuthUserNo(String acctAuthUserNo) {
		this.acctAuthUserNo = acctAuthUserNo;
	}


	public String getAcctAuthUserName() {
		return acctAuthUserName;
	}


	public void setAcctAuthUserName(String acctAuthUserName) {
		this.acctAuthUserName = acctAuthUserName;
	}


	public String getAcceptionBillId() {
		return acceptionBillId;
	}


	public void setAcceptionBillId(String acceptionBillId) {
		this.acceptionBillId = acceptionBillId;
	}


	public String getBalanceType() {
		return balanceType;
	}


	public void setBalanceType(String balanceType) {
		this.balanceType = balanceType;
	}

}