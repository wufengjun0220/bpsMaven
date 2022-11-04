package com.mingtech.application.ecds.draftcollection.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 托收 查询 结果bean
 * @author Administrator
 *
 */
public class CollectionResultBean {
	private String collectionBillId;   //主键id
	private String collectbatchid;//批次id
	private String SBatchNo; // 批次号
	private String SBranchId;        //操作员id
	private String branchName;//机构名称
	private String SOperatorId;      //票据id
	private String SBillNo;          //票号
	private String SBillType;        //票据类型
	private Date DIssueDt;           //出票日
	private Date DDueDt;             //到期日
	private String SAcceptor;        //承兑方名称
	private String SAcceptorBankName;//承兑方行名称
	private String SAcceptorBankCode;//承兑方行行号
	private String SAcceptorBankPhone;//承兑行电话号码
	private String SOwnerName;       //持票方全称(提示付款人)
	private String SOwnerBankCode;   //持票方行号
	private String SOwnerBankName;   //持票方开户行名
	private String SIssuerBankName;// 出票人开户行名称(直接从大票取值)
	private String SOwnerAccount;    //持票方账号
	private BigDecimal FBillAmount;      //票面金额
	private Date DCollectDt;         //托收日期
	private String SClearWay;        //清算方式(线上清算标记？)
	private String clearingFlagName;//清算方式名称
	private String SBillMedia;       //票据介质
	private String SBillFrom;        //票据来源
	private String SPaypromOrgCode;  //提示付款人组织机构代码
	private String SBillStatus;   //票据状态
	
	private String SBillStatusName;//票据状态名
	private String SBillTypeName;//票据类型名
	private String billinfoId;//票据id
	private String SBillMediaName;// 票据介质名称
	
	//托收挑票人 时间、柜员信息
	private Date selectDate;
	private String selectUserNo;
	private String selectUserName;
	
	 //托收退票  日期、原因、退票登记人信息 2016
	 private Date returnDate;
	 private String returnReson;
	 private String returnUserNo;
	 private String returnUserName;
	 
	//托收回款 记账日期、流水号、记账柜员信息2016
	private Date acctDate;
	private String acctFlowNo;//票据系统记账生成的记账流水号
	//记账柜员信息
	private String acctUserNo;
	private String acctUserName;
	//记账授权柜员信息；复核人员
	private String acctAuthUserNo;
	private String acctAuthUserName;
	public String getCollectionBillId() {
		return collectionBillId;
	}
	public void setCollectionBillId(String collectionBillId) {
		this.collectionBillId = collectionBillId;
	}
	public String getCollectbatchid() {
		return collectbatchid;
	}
	public void setCollectbatchid(String collectbatchid) {
		this.collectbatchid = collectbatchid;
	}
	public String getSBatchNo() {
		return SBatchNo;
	}
	public void setSBatchNo(String sBatchNo) {
		SBatchNo = sBatchNo;
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
	public String getSBillNo() {
		return SBillNo;
	}
	public void setSBillNo(String sBillNo) {
		SBillNo = sBillNo;
	}
	public String getSBillType() {
		return SBillType;
	}
	public void setSBillType(String sBillType) {
		SBillType = sBillType;
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
	public String getSAcceptorBankName() {
		return SAcceptorBankName;
	}
	public void setSAcceptorBankName(String sAcceptorBankName) {
		SAcceptorBankName = sAcceptorBankName;
	}
	public String getSAcceptorBankCode() {
		return SAcceptorBankCode;
	}
	public void setSAcceptorBankCode(String sAcceptorBankCode) {
		SAcceptorBankCode = sAcceptorBankCode;
	}
	public String getSAcceptorBankPhone() {
		return SAcceptorBankPhone;
	}
	public void setSAcceptorBankPhone(String sAcceptorBankPhone) {
		SAcceptorBankPhone = sAcceptorBankPhone;
	}
	public String getSOwnerName() {
		return SOwnerName;
	}
	public void setSOwnerName(String sOwnerName) {
		SOwnerName = sOwnerName;
	}
	public String getSOwnerBankCode() {
		return SOwnerBankCode;
	}
	public void setSOwnerBankCode(String sOwnerBankCode) {
		SOwnerBankCode = sOwnerBankCode;
	}
	public String getSOwnerBankName() {
		return SOwnerBankName;
	}
	public void setSOwnerBankName(String sOwnerBankName) {
		SOwnerBankName = sOwnerBankName;
	}
	public String getSIssuerBankName() {
		return SIssuerBankName;
	}
	public void setSIssuerBankName(String sIssuerBankName) {
		SIssuerBankName = sIssuerBankName;
	}
	public String getSOwnerAccount() {
		return SOwnerAccount;
	}
	public void setSOwnerAccount(String sOwnerAccount) {
		SOwnerAccount = sOwnerAccount;
	}
	public BigDecimal getFBillAmount() {
		return FBillAmount;
	}
	public void setFBillAmount(BigDecimal fBillAmount) {
		FBillAmount = fBillAmount;
	}
	public Date getDCollectDt() {
		return DCollectDt;
	}
	public void setDCollectDt(Date dCollectDt) {
		DCollectDt = dCollectDt;
	}
	public String getSClearWay() {
		return SClearWay;
	}
	public void setSClearWay(String sClearWay) {
		SClearWay = sClearWay;
	}
	public String getClearingFlagName() {
		return clearingFlagName;
	}
	public void setClearingFlagName(String clearingFlagName) {
		this.clearingFlagName = clearingFlagName;
	}
	public String getSBillMedia() {
		return SBillMedia;
	}
	public void setSBillMedia(String sBillMedia) {
		SBillMedia = sBillMedia;
	}
	public String getSBillFrom() {
		return SBillFrom;
	}
	public void setSBillFrom(String sBillFrom) {
		SBillFrom = sBillFrom;
	}
	public String getSPaypromOrgCode() {
		return SPaypromOrgCode;
	}
	public void setSPaypromOrgCode(String sPaypromOrgCode) {
		SPaypromOrgCode = sPaypromOrgCode;
	}
	public String getSBillStatus() {
		return SBillStatus;
	}
	public void setSBillStatus(String sBillStatus) {
		SBillStatus = sBillStatus;
	}
	public String getSBillStatusName() {
		return SBillStatusName;
	}
	public void setSBillStatusName(String sBillStatusName) {
		SBillStatusName = sBillStatusName;
	}
	public String getSBillTypeName() {
		return SBillTypeName;
	}
	public void setSBillTypeName(String sBillTypeName) {
		SBillTypeName = sBillTypeName;
	}
	public String getBillinfoId() {
		return billinfoId;
	}
	public void setBillinfoId(String billinfoId) {
		this.billinfoId = billinfoId;
	}
	public String getSBillMediaName() {
		return SBillMediaName;
	}
	public void setSBillMediaName(String sBillMediaName) {
		SBillMediaName = sBillMediaName;
	}
	public Date getSelectDate() {
		return selectDate;
	}
	public void setSelectDate(Date selectDate) {
		this.selectDate = selectDate;
	}
	public String getSelectUserNo() {
		return selectUserNo;
	}
	public void setSelectUserNo(String selectUserNo) {
		this.selectUserNo = selectUserNo;
	}
	public String getSelectUserName() {
		return selectUserName;
	}
	public void setSelectUserName(String selectUserName) {
		this.selectUserName = selectUserName;
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
	public String getReturnUserNo() {
		return returnUserNo;
	}
	public void setReturnUserNo(String returnUserNo) {
		this.returnUserNo = returnUserNo;
	}
	public String getReturnUserName() {
		return returnUserName;
	}
	public void setReturnUserName(String returnUserName) {
		this.returnUserName = returnUserName;
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
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	
}
