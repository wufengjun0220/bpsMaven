package com.mingtech.application.ecds.draftcollection.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.ecds.common.DictionaryCache;
import com.mingtech.application.ecds.common.PublicStaticDefineTab;
import com.mingtech.application.pool.common.domain.PoolBillInfo;

/**
 * <p>版权所有:(C)2003-2010 北京明润华创科技有限责任公司</p>
 * @作者：liuweijun
 * @日期：Jun 2, 2009 11:54:37 AM
 * @描述：[CollectionSendDto]提示付款（逾期提示付款）申请明细
 */
public class CollectionSendDto implements java.io.Serializable {

	/******汉口银行使用字段***Start******************/
	private String collectionSendId;   //主键id
	//大票信息
	private PoolBillInfo poolBillInfo;   //大票信息
	private String SBillNo;          //票号
	private BigDecimal FBillAmount;  //票面金额
	private Date DIssueDt;           //出票日
	private Date DDueDt;             //到期日
	private String SBillMedia;       //票据介质
	private String SBillType;        //票据类型
	//承兑人信息

	private String acceptNm;        //承兑方名称
	private String acceptCmonId; //承兑方组织机构代码
	private String acceptAccount;        //承兑方帐号
	private String acceptAcctSvcr; //承兑方大额行号
	private String acceptBankName;//承兑方开户行名称
	private Date applDt;      //提示付款(或逾期提示付款)申请日期
	private Date applSignDt;  //提示付款(或逾期)签收日期
	private BigDecimal amt;   //提示付款(或逾期)金额
	private String clearWay;  //提示付款(或逾期)清算方式(线上清算标记？)
	
	private String collNm;//提示付款人(或逾期)名称
	private String collCmonId;//提示付款人(或逾期)组织机构代码
	private String collAcct;//提示付款人(或逾期)帐号
	private String collAcctSvcr;//提示付款人(或逾期)大额行号
	private String collBankName;//提示付款人开户行名称
	
	private String acctFlowNo;//票据系统记账生成的记账流水号
	private String SBillStatus;//明细状态（重要）
	
	/******汉口银行使用字段***Start******************/
	private String SBranchId;        //机构ID
	private String SOperatorId;      //操作员id
	private String acceptRole; //承兑人角色
	private String acceptElctrncsgntr;//承兑方电子签名
	private String prsnttnDshnrCode;//提示(逾期)付款拒付代码(拒付签收必填)
	private String prsnttnDshnrRsn;//拒付备注信息(拒付代码为DC09时必填)
	private String acceptAgcySvcr;//承兑方承接行行号

	//提示付款信息


	private String rsn; //逾期原因说明

	private String prxyPropstn;//提示付款(或逾期)代理标识
	private String prxyAccept;//提示付款(或逾期)签收代理标识
	private String rmrkByPropsr;//提示付款(或逾期)提示付款人备注
	private String rmrkByAccept;//提示付款(或逾期)签收备注
	
	//提示付款人（或逾期提示付款人）信息
	private String collRole ;//提示付款人(或逾期)角色

	private String collElctrncSgntr;//提示付款人(或逾期)电子签名

	private String collAgcyAcctSvcr;//提示付款人(或逾期)承接行行号
	
	//即时转账信息
	private String trfId;//支付交易序号
	
	//明细信息

	private String SMsgStatus;//明细报文状态
	private String SMsgCode;//明细报文信息(收到033时报文处理码描述)	
	private String overdueFlag;// 逾期标识
	private String isSysCustomer;//承兑人是否系统内用户标识
	
   /**************相应代码所对中文名称只做页面显示**************/
	private String SBillMediaName;//票据介质中文名称
	private String SBillTypeName;//票据类型中文名称
	private String clearWayName;// 清算方式中文名称
	private String SBillStatusName;//明细中票据状态中文名
	private String SMsgStatusName;// 报文状态中文名称
	private String overdueFlagName;// 逾期标识
	private String isSysCustomerName;//承兑人是否系统内用户标识中文名称
	
	 //托收退票  日期、原因信息 2016
	private Date returnDate;
	private String returnReson; 
	//托收回款 记账日期、流水号、记账柜员信息2016
	private Date acctDate;
	
	//记账柜员信息
	private String acctUserNo;
	private String acctUserName;
	//记账授权柜员信息；复核人员
	private String acctAuthUserNo;
	private String acctAuthUserName;
	
	private String guaranteeNo;//担保编号
	
	private String accNo;// 电票签约账号
	
	private String bankFlag;//是否我行承兑票据	1:我行  0:跨行
	
	private String bpsNo;//票据池编号
	
	private String accptrOrg;//承兑人组织机构代码

	private Date lastOperTm;//最后一次操作时间
	private String lastOperName;//最后一次操作说明
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	private String hilrId;//持有id
	private String transId;//拆分前的大票主键id
	private String isPursue;//是否追索  1是  0否
	/*** 融合改造新增字段  end*/
	

	public Date getLastOperTm() {
		return lastOperTm;
	}

	public String getIsPursue() {
		return isPursue;
	}

	public void setIsPursue(String isPursue) {
		this.isPursue = isPursue;
	}

	public String getHilrId() {
		return hilrId;
	}

	public void setHilrId(String hilrId) {
		this.hilrId = hilrId;
	}

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getBeginRangeNo() {
		return beginRangeNo;
	}

	public void setBeginRangeNo(String beginRangeNo) {
		this.beginRangeNo = beginRangeNo;
	}

	public String getEndRangeNo() {
		return endRangeNo;
	}

	public void setEndRangeNo(String endRangeNo) {
		this.endRangeNo = endRangeNo;
	}

	public BigDecimal getStandardAmt() {
		return standardAmt;
	}

	public void setStandardAmt(BigDecimal standardAmt) {
		this.standardAmt = standardAmt;
	}

	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	public String getDraftSource() {
		return draftSource;
	}

	public void setDraftSource(String draftSource) {
		this.draftSource = draftSource;
	}

	public String getSplitFlag() {
		return splitFlag;
	}

	public void setSplitFlag(String splitFlag) {
		this.splitFlag = splitFlag;
	}

	public void setLastOperTm(Date lastOperTm) {
		this.lastOperTm = lastOperTm;
	}

	public String getLastOperName() {
		return lastOperName;
	}

	public void setLastOperName(String lastOperName) {
		this.lastOperName = lastOperName;
	}

	public String getAccptrOrg() {
		return accptrOrg;
	}

	public void setAccptrOrg(String accptrOrg) {
		this.accptrOrg = accptrOrg;
	}
	
	public String getBpsNo() {
		return bpsNo;
	}
	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}
	
	/** default constructor */
	public CollectionSendDto() {
	}


	public String getCollectionSendId() {
		return collectionSendId;
	}


	public void setCollectionSendId(String collectionSendId) {
		this.collectionSendId = collectionSendId;
	}





	public PoolBillInfo getPoolBillInfo() {
		return poolBillInfo;
	}

	public void setPoolBillInfo(PoolBillInfo poolBillInfo) {
		this.poolBillInfo = poolBillInfo;
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


	public Date getReturnDate() {
		return returnDate;
	}


	public void setReturnDate(Date returnDate) {
		this.returnDate = returnDate;
	}


	public String getReturnReson() {
		return returnReson;
	}


	public void setReturnReson(String returnReson) {
		this.returnReson = returnReson;
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


	public String getGuaranteeNo() {
		return guaranteeNo;
	}


	public void setGuaranteeNo(String guaranteeNo) {
		this.guaranteeNo = guaranteeNo;
	}


	public String getAccNo() {
		return accNo;
	}


	public void setAccNo(String accNo) {
		this.accNo = accNo;
	}


	public String getBankFlag() {
		return bankFlag;
	}


	public void setBankFlag(String bankFlag) {
		this.bankFlag = bankFlag;
	}
	
}