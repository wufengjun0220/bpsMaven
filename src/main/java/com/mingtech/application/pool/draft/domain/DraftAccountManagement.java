package com.mingtech.application.pool.draft.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DraftAccountManagement entity. @author MyEclipse Persistence Tools
 */

public class DraftAccountManagement implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = -9137106447996734105L;
	private String id;//主键ID
	private String custNo;//客户号
	private String custName;//客户名称
	private String assetType;//资产类型 (QY_01:持有票   QY_02：应付)
	private String transferPhase;//交易阶段 (JD_01:客户持有票据   JD_02:已质押票据  JD_03:虚拟票据池录入应收票据  JD_04:BBSP系统提示承兑已签收票据  JD_05:MIS系统签发出账票据    JD_06:虚拟票据池录入应付票据)
	private String draftNb;//票号
	private String draftMedia;//票据介质 
	private String draftType;//票据类型
	private BigDecimal isseAmt;//票面金额
	private Date isseDt;//出票日
	private Date dueDt;//到期日
	private String edBanEndrsmtMk;//能否转让标记      0-可转让   1-不可转让
	private String drwrNm;//出票人全称
	private String drwrAcctId;//出票人账号

	// Constructors
	private String drwrAcctSvcr;//出票人开户行行号
	private String drwrAcctSvcrNm;//出票人开户行名称

	// Property accessors
	private String pyeeNm;//收款人全称
	private String pyeeAcctId;//收款人账号
	private String pyeeAcctSvcr;//收款人开户行行号
	private String pyeeAcctSvcrNm;//收款人开户行名称
	private String accptrNm;//承兑人全称
	private String accptrId;//承兑人账号
	private String accptrSvcr;//承兑人开户行全称
	private String accptrSvcrNm;//承兑人开户行行名
	private String trusteeshipFalg;//是都托管
	private String draftOwnerSts;//票据持有类型
	private String isEdu;//是否产生额度
	private String riskLevel;//风险等级
	private String recePayType;//票据权益（QY_01:持有票据    QY_02:应付票据 ——该字段因为与 assetType 重复  已经删除   20190329   Ju Nana）
	private String dataSource;//数据来源（SRC_01:BBSP  SRC_03:票据池系统（融资票据池） SRC_04:票据池虚拟录入   SRC_02:MIS系统 ）
	private String billSaveAddr;//票据保管地(01:本行   02：自持  03：他行)
	private String otherBankSaveAddr;//他行保管地
	
	private String contractNo;//交易合同号
	private String  acceptanceAgreeNo;//承兑协议编号
	
	private String bpsNo;//票据池编号
	private String bpsName;//票据池名称
	private String statusFlag;//是否区分票据池    0：否  1：是     
	
	private String elecDraftAccount;//电票签约账户
	private String billId;//票据id
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	
	private String plDrwrAcctName;// 出票人账号名称
	private String plPyeeAcctName;// 收款人账号名称
	private String plAccptrAcctName;// 承兑人账号名称
	
	
	/*** 融合改造新增字段  end*/
	
	
	/** default constructor */
	public DraftAccountManagement() {
	}

	/** full constructor */
	public DraftAccountManagement(String custNo, String custName,
			String assetType, String transferPhase, String draftNb,
			String draftMedia, String draftType, BigDecimal isseAmt, Date isseDt,
			Date dueDt, String edBanEndrsmtMk, String drwrNm,
			String drwrAcctId, String drwrAcctSvcr, String drwrAcctSvcrNm,
			String pyeeNm, String pyeeAcctId, String pyeeAcctSvcr,
			String pyeeAcctSvcrNm, String accptrNm, String accptrId,
			String accptrSvcr, String accptrSvcrNm, String trusteeshipFalg,
			String draftOwnerSts,String isEdu,String riskLevel,String dataSource,
			String otherBankSaveAddr,String recePayType,String billSaveAddr,String contractNo, 
			String bpsNo,String bpsName,String  acceptanceAgreeNo,String elecDraftAccount) {
		this.custNo = custNo;
		this.custName = custName;
		this.assetType = assetType;
		this.transferPhase = transferPhase;
		this.draftNb = draftNb;
		this.draftMedia = draftMedia;
		this.draftType = draftType;
		this.isseAmt = isseAmt;
		this.isseDt = isseDt;
		this.dueDt = dueDt;
		this.edBanEndrsmtMk = edBanEndrsmtMk;
		this.drwrNm = drwrNm;
		this.drwrAcctId = drwrAcctId;
		this.drwrAcctSvcr = drwrAcctSvcr;
		this.drwrAcctSvcrNm = drwrAcctSvcrNm;
		this.pyeeNm = pyeeNm;
		this.pyeeAcctId = pyeeAcctId;
		this.pyeeAcctSvcr = pyeeAcctSvcr;
		this.pyeeAcctSvcrNm = pyeeAcctSvcrNm;
		this.accptrNm = accptrNm;
		this.accptrId = accptrId;
		this.accptrSvcr = accptrSvcr;
		this.accptrSvcrNm = accptrSvcrNm;
		this.trusteeshipFalg = trusteeshipFalg;
		this.draftOwnerSts = draftOwnerSts;
		this.isEdu = isEdu;
		this.riskLevel = riskLevel;
		this.dataSource = dataSource;
		this.otherBankSaveAddr = otherBankSaveAddr;
		this.recePayType = recePayType;
		this.billSaveAddr = billSaveAddr;
		this.contractNo = contractNo;
		this.acceptanceAgreeNo = acceptanceAgreeNo;
		this.bpsNo = bpsName;
		this.bpsName = bpsName;
		this.elecDraftAccount = elecDraftAccount;
	}

	// Property accessors

	
	public String getId() {
		return this.id;
	}


	public String getBillId() {
		return billId;
	}

	public void setBillId(String billId) {
		this.billId = billId;
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

	public String getElecDraftAccount() {
		return elecDraftAccount;
	}

	public void setElecDraftAccount(String elecDraftAccount) {
		this.elecDraftAccount = elecDraftAccount;
	}

	public String getStatusFlag() {
		return statusFlag;
	}

	public void setStatusFlag(String statusFlag) {
		this.statusFlag = statusFlag;
	}

	public String getBpsNo() {
		return bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getBpsName() {
		return bpsName;
	}

	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getAcceptanceAgreeNo() {
		return acceptanceAgreeNo;
	}

	public void setAcceptanceAgreeNo(String acceptanceAgreeNo) {
		this.acceptanceAgreeNo = acceptanceAgreeNo;
	}

	public String getRecePayType() {
		return recePayType;
	}

	public void setRecePayType(String recePayType) {
		this.recePayType = recePayType;
	}

	public String getBillSaveAddr() {
		return billSaveAddr;
	}

	public void setBillSaveAddr(String billSaveAddr) {
		this.billSaveAddr = billSaveAddr;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCustNo() {
		return this.custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getAssetType() {
		return this.assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}

	public String getTransferPhase() {
		return this.transferPhase;
	}

	public void setTransferPhase(String transferPhase) {
		this.transferPhase = transferPhase;
	}

	public String getDraftNb() {
		return this.draftNb;
	}

	public void setDraftNb(String draftNb) {
		this.draftNb = draftNb;
	}

	public String getDraftMedia() {
		return this.draftMedia;
	}

	public void setDraftMedia(String draftMedia) {
		this.draftMedia = draftMedia;
	}

	public String getDraftType() {
		return this.draftType;
	}

	public void setDraftType(String draftType) {
		this.draftType = draftType;
	}



	public BigDecimal getIsseAmt() {
		return isseAmt;
	}

	public void setIsseAmt(BigDecimal isseAmt) {
		this.isseAmt = isseAmt;
	}

	public Date getIsseDt() {
		return this.isseDt;
	}

	public void setIsseDt(Date isseDt) {
		this.isseDt = isseDt;
	}

	public Date getDueDt() {
		return this.dueDt;
	}

	public void setDueDt(Date dueDt) {
		this.dueDt = dueDt;
	}

	public String getEdBanEndrsmtMk() {
		return this.edBanEndrsmtMk;
	}

	public void setEdBanEndrsmtMk(String edBanEndrsmtMk) {
		this.edBanEndrsmtMk = edBanEndrsmtMk;
	}

	public String getDrwrNm() {
		return this.drwrNm;
	}

	public void setDrwrNm(String drwrNm) {
		this.drwrNm = drwrNm;
	}

	public String getDrwrAcctId() {
		return this.drwrAcctId;
	}

	public void setDrwrAcctId(String drwrAcctId) {
		this.drwrAcctId = drwrAcctId;
	}

	public String getDrwrAcctSvcr() {
		return this.drwrAcctSvcr;
	}

	public void setDrwrAcctSvcr(String drwrAcctSvcr) {
		this.drwrAcctSvcr = drwrAcctSvcr;
	}

	public String getDrwrAcctSvcrNm() {
		return this.drwrAcctSvcrNm;
	}

	public void setDrwrAcctSvcrNm(String drwrAcctSvcrNm) {
		this.drwrAcctSvcrNm = drwrAcctSvcrNm;
	}

	public String getPyeeNm() {
		return this.pyeeNm;
	}

	public void setPyeeNm(String pyeeNm) {
		this.pyeeNm = pyeeNm;
	}

	public String getPyeeAcctId() {
		return this.pyeeAcctId;
	}

	public void setPyeeAcctId(String pyeeAcctId) {
		this.pyeeAcctId = pyeeAcctId;
	}

	public String getPyeeAcctSvcr() {
		return this.pyeeAcctSvcr;
	}

	public void setPyeeAcctSvcr(String pyeeAcctSvcr) {
		this.pyeeAcctSvcr = pyeeAcctSvcr;
	}

	public String getPyeeAcctSvcrNm() {
		return this.pyeeAcctSvcrNm;
	}

	public void setPyeeAcctSvcrNm(String pyeeAcctSvcrNm) {
		this.pyeeAcctSvcrNm = pyeeAcctSvcrNm;
	}

	public String getAccptrNm() {
		return this.accptrNm;
	}

	public void setAccptrNm(String accptrNm) {
		this.accptrNm = accptrNm;
	}

	public String getAccptrId() {
		return this.accptrId;
	}

	public void setAccptrId(String accptrId) {
		this.accptrId = accptrId;
	}

	public String getAccptrSvcr() {
		return this.accptrSvcr;
	}

	public void setAccptrSvcr(String accptrSvcr) {
		this.accptrSvcr = accptrSvcr;
	}

	public String getAccptrSvcrNm() {
		return this.accptrSvcrNm;
	}

	public void setAccptrSvcrNm(String accptrSvcrNm) {
		this.accptrSvcrNm = accptrSvcrNm;
	}

	public String getTrusteeshipFalg() {
		return this.trusteeshipFalg;
	}

	public void setTrusteeshipFalg(String trusteeshipFalg) {
		this.trusteeshipFalg = trusteeshipFalg;
	}

	public String getDraftOwnerSts() {
		return this.draftOwnerSts;
	}

	public void setDraftOwnerSts(String draftOwnerSts) {
		this.draftOwnerSts = draftOwnerSts;
	}

	public String getIsEdu() {
		return isEdu;
	}

	public void setIsEdu(String isEdu) {
		this.isEdu = isEdu;
	}

	public String getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getOtherBankSaveAddr() {
		return otherBankSaveAddr;
	}

	public void setOtherBankSaveAddr(String otherBankSaveAddr) {
		this.otherBankSaveAddr = otherBankSaveAddr;
	}

	public String getPlDrwrAcctName() {
		return plDrwrAcctName;
	}

	public void setPlDrwrAcctName(String plDrwrAcctName) {
		this.plDrwrAcctName = plDrwrAcctName;
	}

	public String getPlPyeeAcctName() {
		return plPyeeAcctName;
	}

	public void setPlPyeeAcctName(String plPyeeAcctName) {
		this.plPyeeAcctName = plPyeeAcctName;
	}

	public String getPlAccptrAcctName() {
		return plAccptrAcctName;
	}

	public void setPlAccptrAcctName(String plAccptrAcctName) {
		this.plAccptrAcctName = plAccptrAcctName;
	}

}