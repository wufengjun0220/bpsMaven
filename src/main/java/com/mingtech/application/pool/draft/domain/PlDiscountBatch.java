package com.mingtech.application.pool.draft.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

/**
 * PlDiscountBatch entity. @author MyEclipse Persistence Tools
 */

public class PlDiscountBatch implements java.io.Serializable {

	// Fields

	private String discBatchId;
	private String SProdId;
	private String SOperatorId;
	private String SCustName;
	private String SCustAccount;
	private String SCustOrgCode;
	private String SCustBank;
	private String SBillStatus;
	private String SIntPayway;
	private String SIntpayerName;
	private String SIntpayerOrgCode;
	private String SIntpayerAccount;
	private String SIntpayerBank;
	private String SCustManagerId;
	private Date DAppliDt;
	private BigDecimal workflowCaseid;
	private String SBillType;
	private BigDecimal workflowSubCaseid;
	private Double FIntSum;
	private String SMbfebankcode;
	private String SBatchNo;
	private String SDiscClass;
	private String STroSign;
	private String SBillMedia;
	private String SInaccNum;
	private String SInaccBanknum;
	private String SDiscType;
	private String SClearWay;
	private Double enSc;
	private String SNumberId;
	private Date DBuyDt;
	private String SPayeeaccount;
	private String SPayeename;
	private String SIsproxy;
	private String SIsbuy;
	private String proxyname;
	private String proxyorgcode;
	private String proxyaccount;
	private Double proxyamount;
	private String SRateType;
	private Double FRate;
	private String instorageSts;
	private String sequenceId;
	private Double FBillAmountSum;
	private Long FBillSum;
	private Double FPayAmountSum;
	private Double FBuyPayAmountSum;
	private String txtransnumber;
	private Double incometotal;
	private Timestamp timeversion;
	private String SBranchId;
	private String agentFlag;
	private String classification;
	private String classificationName;
	private String isnotInterbank;

	// Constructors

	/** default constructor */
	public PlDiscountBatch() {
	}

	/** full constructor */
	public PlDiscountBatch(String SProdId, String SOperatorId,
			String SCustName, String SCustAccount, String SCustOrgCode,
			String SCustBank, String SBillStatus, String SIntPayway,
			String SIntpayerName, String SIntpayerOrgCode,
			String SIntpayerAccount, String SIntpayerBank,
			String SCustManagerId, Date DAppliDt, BigDecimal workflowCaseid,
			String SBillType, BigDecimal workflowSubCaseid, Double FIntSum,
			String SMbfebankcode, String SBatchNo, String SDiscClass,
			String STroSign, String SBillMedia, String SInaccNum,
			String SInaccBanknum, String SDiscType, String SClearWay,
			Double enSc, String SNumberId, Date DBuyDt, String SPayeeaccount,
			String SPayeename, String SIsproxy, String SIsbuy,
			String proxyname, String proxyorgcode, String proxyaccount,
			Double proxyamount, String SRateType, Double FRate,
			String instorageSts, String sequenceId, Double FBillAmountSum,
			Long FBillSum, Double FPayAmountSum, Double FBuyPayAmountSum,
			String txtransnumber, Double incometotal, Timestamp timeversion,
			String SBranchId, String agentFlag, String classification,
			String classificationName, String isnotInterbank) {
		this.SProdId = SProdId;
		this.SOperatorId = SOperatorId;
		this.SCustName = SCustName;
		this.SCustAccount = SCustAccount;
		this.SCustOrgCode = SCustOrgCode;
		this.SCustBank = SCustBank;
		this.SBillStatus = SBillStatus;
		this.SIntPayway = SIntPayway;
		this.SIntpayerName = SIntpayerName;
		this.SIntpayerOrgCode = SIntpayerOrgCode;
		this.SIntpayerAccount = SIntpayerAccount;
		this.SIntpayerBank = SIntpayerBank;
		this.SCustManagerId = SCustManagerId;
		this.DAppliDt = DAppliDt;
		this.workflowCaseid = workflowCaseid;
		this.SBillType = SBillType;
		this.workflowSubCaseid = workflowSubCaseid;
		this.FIntSum = FIntSum;
		this.SMbfebankcode = SMbfebankcode;
		this.SBatchNo = SBatchNo;
		this.SDiscClass = SDiscClass;
		this.STroSign = STroSign;
		this.SBillMedia = SBillMedia;
		this.SInaccNum = SInaccNum;
		this.SInaccBanknum = SInaccBanknum;
		this.SDiscType = SDiscType;
		this.SClearWay = SClearWay;
		this.enSc = enSc;
		this.SNumberId = SNumberId;
		this.DBuyDt = DBuyDt;
		this.SPayeeaccount = SPayeeaccount;
		this.SPayeename = SPayeename;
		this.SIsproxy = SIsproxy;
		this.SIsbuy = SIsbuy;
		this.proxyname = proxyname;
		this.proxyorgcode = proxyorgcode;
		this.proxyaccount = proxyaccount;
		this.proxyamount = proxyamount;
		this.SRateType = SRateType;
		this.FRate = FRate;
		this.instorageSts = instorageSts;
		this.sequenceId = sequenceId;
		this.FBillAmountSum = FBillAmountSum;
		this.FBillSum = FBillSum;
		this.FPayAmountSum = FPayAmountSum;
		this.FBuyPayAmountSum = FBuyPayAmountSum;
		this.txtransnumber = txtransnumber;
		this.incometotal = incometotal;
		this.timeversion = timeversion;
		this.SBranchId = SBranchId;
		this.agentFlag = agentFlag;
		this.classification = classification;
		this.classificationName = classificationName;
		this.isnotInterbank = isnotInterbank;
	}

	// Property accessors

	public String getDiscBatchId() {
		return this.discBatchId;
	}

	public void setDiscBatchId(String discBatchId) {
		this.discBatchId = discBatchId;
	}

	public String getSProdId() {
		return this.SProdId;
	}

	public void setSProdId(String SProdId) {
		this.SProdId = SProdId;
	}

	public String getSOperatorId() {
		return this.SOperatorId;
	}

	public void setSOperatorId(String SOperatorId) {
		this.SOperatorId = SOperatorId;
	}

	public String getSCustName() {
		return this.SCustName;
	}

	public void setSCustName(String SCustName) {
		this.SCustName = SCustName;
	}

	public String getSCustAccount() {
		return this.SCustAccount;
	}

	public void setSCustAccount(String SCustAccount) {
		this.SCustAccount = SCustAccount;
	}

	public String getSCustOrgCode() {
		return this.SCustOrgCode;
	}

	public void setSCustOrgCode(String SCustOrgCode) {
		this.SCustOrgCode = SCustOrgCode;
	}

	public String getSCustBank() {
		return this.SCustBank;
	}

	public void setSCustBank(String SCustBank) {
		this.SCustBank = SCustBank;
	}

	public String getSBillStatus() {
		return this.SBillStatus;
	}

	public void setSBillStatus(String SBillStatus) {
		this.SBillStatus = SBillStatus;
	}

	public String getSIntPayway() {
		return this.SIntPayway;
	}

	public void setSIntPayway(String SIntPayway) {
		this.SIntPayway = SIntPayway;
	}

	public String getSIntpayerName() {
		return this.SIntpayerName;
	}

	public void setSIntpayerName(String SIntpayerName) {
		this.SIntpayerName = SIntpayerName;
	}

	public String getSIntpayerOrgCode() {
		return this.SIntpayerOrgCode;
	}

	public void setSIntpayerOrgCode(String SIntpayerOrgCode) {
		this.SIntpayerOrgCode = SIntpayerOrgCode;
	}

	public String getSIntpayerAccount() {
		return this.SIntpayerAccount;
	}

	public void setSIntpayerAccount(String SIntpayerAccount) {
		this.SIntpayerAccount = SIntpayerAccount;
	}

	public String getSIntpayerBank() {
		return this.SIntpayerBank;
	}

	public void setSIntpayerBank(String SIntpayerBank) {
		this.SIntpayerBank = SIntpayerBank;
	}

	public String getSCustManagerId() {
		return this.SCustManagerId;
	}

	public void setSCustManagerId(String SCustManagerId) {
		this.SCustManagerId = SCustManagerId;
	}

	public Date getDAppliDt() {
		return this.DAppliDt;
	}

	public void setDAppliDt(Date DAppliDt) {
		this.DAppliDt = DAppliDt;
	}

	public BigDecimal getWorkflowCaseid() {
		return this.workflowCaseid;
	}

	public void setWorkflowCaseid(BigDecimal workflowCaseid) {
		this.workflowCaseid = workflowCaseid;
	}

	public String getSBillType() {
		return this.SBillType;
	}

	public void setSBillType(String SBillType) {
		this.SBillType = SBillType;
	}

	public BigDecimal getWorkflowSubCaseid() {
		return this.workflowSubCaseid;
	}

	public void setWorkflowSubCaseid(BigDecimal workflowSubCaseid) {
		this.workflowSubCaseid = workflowSubCaseid;
	}

	public Double getFIntSum() {
		return this.FIntSum;
	}

	public void setFIntSum(Double FIntSum) {
		this.FIntSum = FIntSum;
	}

	public String getSMbfebankcode() {
		return this.SMbfebankcode;
	}

	public void setSMbfebankcode(String SMbfebankcode) {
		this.SMbfebankcode = SMbfebankcode;
	}

	public String getSBatchNo() {
		return this.SBatchNo;
	}

	public void setSBatchNo(String SBatchNo) {
		this.SBatchNo = SBatchNo;
	}

	public String getSDiscClass() {
		return this.SDiscClass;
	}

	public void setSDiscClass(String SDiscClass) {
		this.SDiscClass = SDiscClass;
	}

	public String getSTroSign() {
		return this.STroSign;
	}

	public void setSTroSign(String STroSign) {
		this.STroSign = STroSign;
	}

	public String getSBillMedia() {
		return this.SBillMedia;
	}

	public void setSBillMedia(String SBillMedia) {
		this.SBillMedia = SBillMedia;
	}

	public String getSInaccNum() {
		return this.SInaccNum;
	}

	public void setSInaccNum(String SInaccNum) {
		this.SInaccNum = SInaccNum;
	}

	public String getSInaccBanknum() {
		return this.SInaccBanknum;
	}

	public void setSInaccBanknum(String SInaccBanknum) {
		this.SInaccBanknum = SInaccBanknum;
	}

	public String getSDiscType() {
		return this.SDiscType;
	}

	public void setSDiscType(String SDiscType) {
		this.SDiscType = SDiscType;
	}

	public String getSClearWay() {
		return this.SClearWay;
	}

	public void setSClearWay(String SClearWay) {
		this.SClearWay = SClearWay;
	}

	public Double getEnSc() {
		return this.enSc;
	}

	public void setEnSc(Double enSc) {
		this.enSc = enSc;
	}

	public String getSNumberId() {
		return this.SNumberId;
	}

	public void setSNumberId(String SNumberId) {
		this.SNumberId = SNumberId;
	}

	public Date getDBuyDt() {
		return this.DBuyDt;
	}

	public void setDBuyDt(Date DBuyDt) {
		this.DBuyDt = DBuyDt;
	}

	public String getSPayeeaccount() {
		return this.SPayeeaccount;
	}

	public void setSPayeeaccount(String SPayeeaccount) {
		this.SPayeeaccount = SPayeeaccount;
	}

	public String getSPayeename() {
		return this.SPayeename;
	}

	public void setSPayeename(String SPayeename) {
		this.SPayeename = SPayeename;
	}

	public String getSIsproxy() {
		return this.SIsproxy;
	}

	public void setSIsproxy(String SIsproxy) {
		this.SIsproxy = SIsproxy;
	}

	public String getSIsbuy() {
		return this.SIsbuy;
	}

	public void setSIsbuy(String SIsbuy) {
		this.SIsbuy = SIsbuy;
	}

	public String getProxyname() {
		return this.proxyname;
	}

	public void setProxyname(String proxyname) {
		this.proxyname = proxyname;
	}

	public String getProxyorgcode() {
		return this.proxyorgcode;
	}

	public void setProxyorgcode(String proxyorgcode) {
		this.proxyorgcode = proxyorgcode;
	}

	public String getProxyaccount() {
		return this.proxyaccount;
	}

	public void setProxyaccount(String proxyaccount) {
		this.proxyaccount = proxyaccount;
	}

	public Double getProxyamount() {
		return this.proxyamount;
	}

	public void setProxyamount(Double proxyamount) {
		this.proxyamount = proxyamount;
	}

	public String getSRateType() {
		return this.SRateType;
	}

	public void setSRateType(String SRateType) {
		this.SRateType = SRateType;
	}

	public Double getFRate() {
		return this.FRate;
	}

	public void setFRate(Double FRate) {
		this.FRate = FRate;
	}

	public String getInstorageSts() {
		return this.instorageSts;
	}

	public void setInstorageSts(String instorageSts) {
		this.instorageSts = instorageSts;
	}

	public String getSequenceId() {
		return this.sequenceId;
	}

	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	public Double getFBillAmountSum() {
		return this.FBillAmountSum;
	}

	public void setFBillAmountSum(Double FBillAmountSum) {
		this.FBillAmountSum = FBillAmountSum;
	}

	public Long getFBillSum() {
		return this.FBillSum;
	}

	public void setFBillSum(Long FBillSum) {
		this.FBillSum = FBillSum;
	}

	public Double getFPayAmountSum() {
		return this.FPayAmountSum;
	}

	public void setFPayAmountSum(Double FPayAmountSum) {
		this.FPayAmountSum = FPayAmountSum;
	}

	public Double getFBuyPayAmountSum() {
		return this.FBuyPayAmountSum;
	}

	public void setFBuyPayAmountSum(Double FBuyPayAmountSum) {
		this.FBuyPayAmountSum = FBuyPayAmountSum;
	}

	public String getTxtransnumber() {
		return this.txtransnumber;
	}

	public void setTxtransnumber(String txtransnumber) {
		this.txtransnumber = txtransnumber;
	}

	public Double getIncometotal() {
		return this.incometotal;
	}

	public void setIncometotal(Double incometotal) {
		this.incometotal = incometotal;
	}

	public Timestamp getTimeversion() {
		return this.timeversion;
	}

	public void setTimeversion(Timestamp timeversion) {
		this.timeversion = timeversion;
	}

	public String getSBranchId() {
		return this.SBranchId;
	}

	public void setSBranchId(String SBranchId) {
		this.SBranchId = SBranchId;
	}

	public String getAgentFlag() {
		return this.agentFlag;
	}

	public void setAgentFlag(String agentFlag) {
		this.agentFlag = agentFlag;
	}

	public String getClassification() {
		return this.classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getClassificationName() {
		return this.classificationName;
	}

	public void setClassificationName(String classificationName) {
		this.classificationName = classificationName;
	}

	public String getIsnotInterbank() {
		return this.isnotInterbank;
	}

	public void setIsnotInterbank(String isnotInterbank) {
		this.isnotInterbank = isnotInterbank;
	}

}