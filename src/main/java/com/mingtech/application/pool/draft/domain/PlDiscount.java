package com.mingtech.application.pool.draft.domain;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import com.mingtech.application.pool.common.domain.PoolBillInfo;

/**
 * PlDiscount entity. @author MyEclipse Persistence Tools
 */

public class PlDiscount implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String discBillId;//票据系统id
	private String custNo;//核心客户号
	private String custName;//客户名称
	private String bpsNo;//票据池编号
	private String elecAccNo;//电票账号
	private String bpsName;//票据池名称
	private PoolBillInfo billinfoId;//大票表id
	private BigDecimal FRate;//贴现利率
	private Date discBatchDt;//贴现申请日
	private String SIntAccount;//贴入方行号
	private String dscntRole;//贴入行名称
	private String SIntName;//贴入人名称
	private String discInAccount;//贴入人账号
	private String inAccountNum;//入账帐号
	private String inAccountBankNum;//入账行号
	private String discBatchId;//批次号
	private BigDecimal FPayment;//实付金额
	private String SJjh;//借据号
	private String SBillNo;//票号
	private String SBranchId;//出票机构
	private String SAgcyAccNo ;//承兑人账号    
	private String SAcceptor;//承兑人名称    
	private String SAgcysvcr;//承兑行行号
	private String SAgcysvcrname;//承兑行名称
	private Date DIssueDt;//出票日
	private Date DDueDt;//到期日
	private String SIfSameCity;//是否同城
	private String FIntDays;//计息天数
	private String Cyy;//币种   ?
	private BigDecimal FBillAmount;//票面金额
	private String SIntPayway;//贴现付息方式
	private BigDecimal ExeIntRate;//执行利率	
	private BigDecimal IntRecvAmt;//应收利息	
	private String addInterestAcctNo;//付息账号	
	private String addInterestAcctNoName;//付息账号名称		
	private String CancelAcctNo;//销账编号		
	private String SSwapContNum;//交易合同号
	private String LoanAcctNo;//放款账号	
	private String LoanerAcctName;//放款账号名称	
	private String LoanOpenBank;//放款账户开户行行号	
	private String OrgCode;//组织机构代码	
	private String AuditStatus;//审核状态	
	private String transSign;//不得转让标记
	private String discinElectronSign;//网银电子签名
	private BigDecimal thirdPayRate;//第三方付息比例
	private String thirdAcctNo;//第三方账号
	private String thridAcctName;//第三方账号名称
	private String thridOpenBranch;//第三方开户行行号
	
	private String guaranteeNo;//担保品编号
	private String discType;//贴现方式 0－买断式1－赎回式
	private Date DRedeStartDt;//回购开放日
	private Date DRedeEndDt;//回购截止日
	private BigDecimal FPayRate;//贴现赎回利率
	private BigDecimal ReAmount;//贴现赎回金额
	private String SBillStatus;//票据状态
	private String reTranstatus;//返回交易状态  1：未处理；2：签收成功；3：签收失败；4：拒绝成功；5：记账成功	6：记账失败
	private String reTranmsg;//返回交易描述
	private String accountStatus;//返回交易描述 
	
	private String billType; // 票据类型
	private String billMedia;// 票据介质
	private String marginAccount;//保证金账号
	private Date lastOperTm;//最后一次操作时间
	private String lastOperName;//最后一次操作说明
	private Date taskDate;//调度任务处理时间
	
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	private String splitId;//拆分前的大票主键id
	private String hilrId;//持有id
	private String transId;//交易ID
	/*** 融合改造新增字段  end*/
	
	

	public Date getTaskDate() {
		return taskDate;
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

	public String getSplitId() {
		return splitId;
	}

	public void setSplitId(String splitId) {
		this.splitId = splitId;
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

	public void setTaskDate(Date taskDate) {
		this.taskDate = taskDate;
	}

	public Date getLastOperTm() {
		return lastOperTm;
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

	public String getMarginAccount() {
		return marginAccount;
	}

	public void setMarginAccount(String marginAccount) {
		this.marginAccount = marginAccount;
	}

	public String getGuaranteeNo() {
		return guaranteeNo;
	}

	public void setGuaranteeNo(String guaranteeNo) {
		this.guaranteeNo = guaranteeNo;
	}

	public String getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}

	public String getDiscType() {
		return discType;
	}

	public void setDiscType(String discType) {
		this.discType = discType;
	}

	public Date getDRedeStartDt() {
		return DRedeStartDt;
	}

	public void setDRedeStartDt(Date dRedeStartDt) {
		DRedeStartDt = dRedeStartDt;
	}

	public Date getDRedeEndDt() {
		return DRedeEndDt;
	}

	public void setDRedeEndDt(Date dRedeEndDt) {
		DRedeEndDt = dRedeEndDt;
	}

	public BigDecimal getFPayRate() {
		return FPayRate;
	}

	public void setFPayRate(BigDecimal fPayRate) {
		FPayRate = fPayRate;
	}

	public BigDecimal getReAmount() {
		return ReAmount;
	}

	public void setReAmount(BigDecimal reAmount) {
		ReAmount = reAmount;
	}

	public String getSBillStatus() {
		return SBillStatus;
	}

	public void setSBillStatus(String sBillStatus) {
		SBillStatus = sBillStatus;
	}



	public String getReTranstatus() {
		return reTranstatus;
	}



	public void setReTranstatus(String reTranstatus) {
		this.reTranstatus = reTranstatus;
	}



	public String getReTranmsg() {
		return reTranmsg;
	}



	public void setReTranmsg(String reTranmsg) {
		this.reTranmsg = reTranmsg;
	}



	public BigDecimal getThirdPayRate() {
		return thirdPayRate;
	}



	public void setThirdPayRate(BigDecimal thirdPayRate) {
		this.thirdPayRate = thirdPayRate;
	}



	public String getThirdAcctNo() {
		return thirdAcctNo;
	}



	public void setThirdAcctNo(String thirdAcctNo) {
		this.thirdAcctNo = thirdAcctNo;
	}



	public String getThridAcctName() {
		return thridAcctName;
	}



	public void setThridAcctName(String thridAcctName) {
		this.thridAcctName = thridAcctName;
	}



	public String getThridOpenBranch() {
		return thridOpenBranch;
	}



	public void setThridOpenBranch(String thridOpenBranch) {
		this.thridOpenBranch = thridOpenBranch;
	}



	/** default constructor */
	public PlDiscount() {
	}



	public String getSJjh() {
		return SJjh;
	}



	public void setSJjh(String sJjh) {
		SJjh = sJjh;
	}



	public String getDiscinElectronSign() {
		return discinElectronSign;
	}



	public void setDiscinElectronSign(String discinElectronSign) {
		this.discinElectronSign = discinElectronSign;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	public String getElecAccNo() {
		return elecAccNo;
	}



	public void setElecAccNo(String elecAccNo) {
		this.elecAccNo = elecAccNo;
	}



	public String getCustName() {
		return custName;
	}



	public void setCustName(String custName) {
		this.custName = custName;
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



	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	// Property accessors

	public String getDiscBillId() {
		return this.discBillId;
	}

	public void setDiscBillId(String discBillId) {
		this.discBillId = discBillId;
	}

	public String getSBranchId() {
		return this.SBranchId;
	}

	public void setSBranchId(String SBranchId) {
		this.SBranchId = SBranchId;
	}


	public PoolBillInfo getBillinfoId() {
		return billinfoId;
	}


	public void setBillinfoId(PoolBillInfo billinfoId) {
		this.billinfoId = billinfoId;
	}


	public String getSBillNo() {
		return this.SBillNo;
	}

	public void setSBillNo(String SBillNo) {
		this.SBillNo = SBillNo;
	}

	public Date getDIssueDt() {
		return this.DIssueDt;
	}

	public void setDIssueDt(Date DIssueDt) {
		this.DIssueDt = DIssueDt;
	}

	public Date getDDueDt() {
		return this.DDueDt;
	}

	public void setDDueDt(Date DDueDt) {
		this.DDueDt = DDueDt;
	}

	public String getSAcceptor() {
		return this.SAcceptor;
	}

	public void setSAcceptor(String SAcceptor) {
		this.SAcceptor = SAcceptor;
	}

	public String getFIntDays() {
		return this.FIntDays;
	}

	public void setFIntDays(String FIntDays) {
		this.FIntDays = FIntDays;
	}

	public String getSIfSameCity() {
		return this.SIfSameCity;
	}

	public void setSIfSameCity(String SIfSameCity) {
		this.SIfSameCity = SIfSameCity;
	}

	public BigDecimal getFBillAmount() {
		return FBillAmount;
	}

	public void setFBillAmount(BigDecimal fBillAmount) {
		FBillAmount = fBillAmount;
	}

	public BigDecimal getFPayment() {
		return FPayment;
	}

	public void setFPayment(BigDecimal fPayment) {
		FPayment = fPayment;
	}

	public BigDecimal getFRate() {
		return this.FRate;
	}

	public void setFRate(BigDecimal FRate) {
		this.FRate = FRate;
	}


	public String getDiscBatchId() {
		return this.discBatchId;
	}

	public void setDiscBatchId(String discBatchId) {
		this.discBatchId = discBatchId;
	}


	public Date getDiscBatchDt() {
		return this.discBatchDt;
	}

	public void setDiscBatchDt(Date discBatchDt) {
		this.discBatchDt = discBatchDt;
	}


	public String getTransSign() {
		return this.transSign;
	}

	public void setTransSign(String transSign) {
		this.transSign = transSign;
	}

	public String getInAccountNum() {
		return this.inAccountNum;
	}

	public void setInAccountNum(String inAccountNum) {
		this.inAccountNum = inAccountNum;
	}

	public String getInAccountBankNum() {
		return this.inAccountBankNum;
	}

	public void setInAccountBankNum(String inAccountBankNum) {
		this.inAccountBankNum = inAccountBankNum;
	}

	public String getDiscInAccount() {
		return this.discInAccount;
	}

	public void setDiscInAccount(String discInAccount) {
		this.discInAccount = discInAccount;
	}

	public String getSIntPayway() {
		return this.SIntPayway;
	}

	public void setSIntPayway(String SIntPayway) {
		this.SIntPayway = SIntPayway;
	}

	public String getSIntName() {
		return this.SIntName;
	}

	public void setSIntName(String SIntName) {
		this.SIntName = SIntName;
	}

	public String getSIntAccount() {
		return this.SIntAccount;
	}

	public void setSIntAccount(String SIntAccount) {
		this.SIntAccount = SIntAccount;
	}


	public String getDscntRole() {
		return this.dscntRole;
	}

	public void setDscntRole(String dscntRole) {
		this.dscntRole = dscntRole;
	}

	public String getSAgcysvcr() {
		return this.SAgcysvcr;
	}

	public void setSAgcysvcr(String SAgcysvcr) {
		this.SAgcysvcr = SAgcysvcr;
	}

	public String getSAgcysvcrname() {
		return this.SAgcysvcrname;
	}

	public void setSAgcysvcrname(String SAgcysvcrname) {
		this.SAgcysvcrname = SAgcysvcrname;
	}

	public String getSSwapContNum() {
		return this.SSwapContNum;
	}

	public void setSSwapContNum(String SSwapContNum) {
		this.SSwapContNum = SSwapContNum;
	}


	public String getSAgcyAccNo() {
		return SAgcyAccNo;
	}



	public void setSAgcyAccNo(String sAgcyAccNo) {
		SAgcyAccNo = sAgcyAccNo;
	}



	public String getCyy() {
		return Cyy;
	}



	public void setCyy(String cyy) {
		Cyy = cyy;
	}



	public BigDecimal getExeIntRate() {
		return ExeIntRate;
	}



	public void setExeIntRate(BigDecimal exeIntRate) {
		ExeIntRate = exeIntRate;
	}



	public BigDecimal getIntRecvAmt() {
		return IntRecvAmt;
	}



	public void setIntRecvAmt(BigDecimal intRecvAmt) {
		IntRecvAmt = intRecvAmt;
	}



	public String getAddInterestAcctNo() {
		return addInterestAcctNo;
	}



	public void setAddInterestAcctNo(String addInterestAcctNo) {
		this.addInterestAcctNo = addInterestAcctNo;
	}



	public String getAddInterestAcctNoName() {
		return addInterestAcctNoName;
	}



	public void setAddInterestAcctNoName(String addInterestAcctNoName) {
		this.addInterestAcctNoName = addInterestAcctNoName;
	}



	public String getCancelAcctNo() {
		return CancelAcctNo;
	}



	public void setCancelAcctNo(String cancelAcctNo) {
		CancelAcctNo = cancelAcctNo;
	}



	public String getLoanAcctNo() {
		return LoanAcctNo;
	}



	public void setLoanAcctNo(String loanAcctNo) {
		LoanAcctNo = loanAcctNo;
	}



	public String getLoanerAcctName() {
		return LoanerAcctName;
	}



	public void setLoanerAcctName(String loanerAcctName) {
		LoanerAcctName = loanerAcctName;
	}



	public String getLoanOpenBank() {
		return LoanOpenBank;
	}



	public void setLoanOpenBank(String loanOpenBank) {
		LoanOpenBank = loanOpenBank;
	}



	public String getOrgCode() {
		return OrgCode;
	}



	public void setOrgCode(String orgCode) {
		OrgCode = orgCode;
	}



	public String getAuditStatus() {
		return AuditStatus;
	}



	public void setAuditStatus(String auditStatus) {
		AuditStatus = auditStatus;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBillType() {
		return billType;
	}

	public void setBillType(String billType) {
		this.billType = billType;
	}

	public String getBillMedia() {
		return billMedia;
	}

	public void setBillMedia(String billMedia) {
		this.billMedia = billMedia;
	}
	
	
}