package com.mingtech.application.pool.draft.domain;

import java.util.Date;

/**
 * MisPdraft entity. @author MyEclipse Persistence Tools
 */

public class MisPdraft implements java.io.Serializable {

	// Fields

	private String id;
	private String custNo;
	private String custName;
	private String assetType;
	private String transferPhase;
	private String draftNb;
	private String draftMedia;
	private String draftType;
	private Double isseAmt;
	private Date isseDt;
	private Date dueDt;
	private String edBanEndrsmtMk;
	private String drwrNm;
	private String drwrAcctId;
	private String drwrAcctSvcr;
	private String drwrAcctSvcrNm;
	private String pyeeNm;
	private String pyeeAcctId;
	private String pyeeAcctSvcr;
	private String pyeeAcctSvcrNm;
	private String accptrNm;
	private String accptrId;
	private String accptrSvcr;
	private String accptrSvcrNm;
	private String trusteeshipFalg;
	private String draftOwnerSts;
	private String endFlag;  //处理标识   0：未处理  1：已处理

	// Constructors

	/** default constructor */
	public MisPdraft() {
	}

	/** full constructor */
	public MisPdraft(String custNo, String custName, String assetType,
			String transferPhase, String draftNb, String draftMedia,
			String draftType, Double isseAmt, Date isseDt, Date dueDt,
			String edBanEndrsmtMk, String drwrNm, String drwrAcctId,
			String drwrAcctSvcr, String drwrAcctSvcrNm, String pyeeNm,
			String pyeeAcctId, String pyeeAcctSvcr, String pyeeAcctSvcrNm,
			String accptrNm, String accptrId, String accptrSvcr,
			String accptrSvcrNm, String trusteeshipFalg, String draftOwnerSts,String endFlag) {
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
		this.endFlag = endFlag;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public String getEndFlag() {
		return endFlag;
	}

	public void setEndFlag(String endFlag) {
		this.endFlag = endFlag;
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

	public Double getIsseAmt() {
		return this.isseAmt;
	}

	public void setIsseAmt(Double isseAmt) {
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

}