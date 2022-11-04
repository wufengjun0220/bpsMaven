package com.mingtech.application.ecds.draftcollection.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.mingtech.application.ecds.draftcollection.domain.BtBillInfo;
/**
 * 托收 查询Bean
 * @author Administrator
 *
 */
public class CollectionQueryBean {
	
	private String collectionBillId;   //主键id
	private String collectbatchid;//批次id
	private String SBatchNo; // 批次号
	private BtBillInfo btBillInfo;   //机构id
	private String SOperatorId;      //票据id
	private String SBillNo;          //票号
	private String SBillType;        //票据类型
	private Date DIssueDt;           //出票日
	
	private Date DDueDt;             //到期日
	private Date dueDtStart;
	private Date dueDtEnd;
	private Date DCollectDt;         //托收日期
	private Date collectDtStart;
	private Date collectDtEnd;
	
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
	
	private String SBillMedia;       //票据介质
	private String SBillStatus;   //票据状态
	 //托收退票  日期、原因、退票登记人信息 2016
	 private Date returnDate;
	 private String returnReson;
	 private String returnUserNo;
	 private String returnUserName;
	 
	//托收回款 记账日期、流水号、记账柜员信息2016
	private Date acctDate;
	
	private String SBillFrom ;
	
	
	/****2017.02.16 新增*****/  
	// 机构使用层级编码
	private String deptlevelCode;
	
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
	public BtBillInfo getBtBillInfo() {
		return btBillInfo;
	}
	public void setBtBillInfo(BtBillInfo btBillInfo) {
		this.btBillInfo = btBillInfo;
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
	public String getSBillMedia() {
		return SBillMedia;
	}
	public void setSBillMedia(String sBillMedia) {
		SBillMedia = sBillMedia;
	}
	public String getSBillStatus() {
		return SBillStatus;
	}
	public void setSBillStatus(String sBillStatus) {
		SBillStatus = sBillStatus;
	}
	public Date getDueDtStart() {
		return dueDtStart;
	}
	public void setDueDtStart(Date dueDtStart) {
		this.dueDtStart = dueDtStart;
	}
	public Date getDueDtEnd() {
		return dueDtEnd;
	}
	public void setDueDtEnd(Date dueDtEnd) {
		this.dueDtEnd = dueDtEnd;
	}
	public Date getCollectDtStart() {
		return collectDtStart;
	}
	public void setCollectDtStart(Date collectDtStart) {
		this.collectDtStart = collectDtStart;
	}
	public Date getCollectDtEnd() {
		return collectDtEnd;
	}
	public void setCollectDtEnd(Date collectDtEnd) {
		this.collectDtEnd = collectDtEnd;
	}
	public String getDeptlevelCode() {
		return deptlevelCode;
	}
	public void setDeptlevelCode(String deptlevelCode) {
		this.deptlevelCode = deptlevelCode;
	}
	public String getSBillFrom() {
		return SBillFrom;
	}
	public void setSBillFrom(String sBillFrom) {
		SBillFrom = sBillFrom;
	}
	
	
}
