package com.mingtech.application.pool.edu.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class CreditProductQueryBean {
	private String custName;// 申请人名称
	private String sttlFlag;// 结清标记
	private String crdtStatus;// 业务状态
	private String crdtType;// 信贷产品类型
	private String custNo;// 申请人组织机构代码
	private String crdtNo;// 信贷业务号
	private String loanNo;// 借据号
	private BigDecimal startcrdtAmt;// 合同金额
	private BigDecimal endcrdtAmt;// 合同金额
	private BigDecimal startLoanAmount;// 借据金A
	private BigDecimal endLoanAmount;// 借据金额
	private List crdtBankCode;// endAmount;//行号
	private String custnumber;// 客户号
	private String risklevel;//风险等级
	private String ifOnline;//是否线上 1 是 2 否
	private String ifAdvanceAmt; //是否垫款
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startCrdtIssDt; //合同起始日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endCrdtIssDt; //合同起始日期

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startCrdtDueDt;// 合同到期日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endCrdtDueDt;// 合同到期日期

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startTime1;//借据起始日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startTime2;//借据起始日期

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endTime1;//借据到期日
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endTime2;//借据到期日

	private String poolAgreement;//票据池编号
	
	private BigDecimal startLoanBalance;// 借据余额
	private BigDecimal endLoanBalance;// 借据余额
	private String  BillNo;//票号
	
	/*** 融合改造新增字段  start*/
	private String beginRangeNo;//票据开始子区间号
	private String endRangeNo;//票据结束子区间号
	private BigDecimal standardAmt;//标准金额
	private BigDecimal tradeAmt;//交易金额(等分化票据实际交易金额)
	private String draftSource;//票据来源
	private String splitFlag;//是否允许拆分标记 1是 0否
	private String hilrId;//持有id
	private String splitId;//拆分前的大票主键id
	
	/*** 融合改造新增字段  end*/
	
	
	public String getBillNo() {
		return BillNo;
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

	public String getHilrId() {
		return hilrId;
	}

	public void setHilrId(String hilrId) {
		this.hilrId = hilrId;
	}

	public String getSplitId() {
		return splitId;
	}

	public void setSplitId(String splitId) {
		this.splitId = splitId;
	}

	public void setBillNo(String billNo) {
		BillNo = billNo;
	}

	public String getPoolAgreement() {
		return poolAgreement;
	}

	public void setPoolAgreement(String poolAgreement) {
		this.poolAgreement = poolAgreement;
	}

	public String getCustName() {
		return custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getSttlFlag() {
		return sttlFlag;
	}

	public void setSttlFlag(String sttlFlag) {
		this.sttlFlag = sttlFlag;
	}

	public String getCrdtStatus() {
		return crdtStatus;
	}

	public void setCrdtStatus(String crdtStatus) {
		this.crdtStatus = crdtStatus;
	}

	public String getCrdtType() {
		return crdtType;
	}

	public void setCrdtType(String crdtType) {
		this.crdtType = crdtType;
	}

	public String getCustNo() {
		return custNo;
	}

	public void setCustNo(String custNo) {
		this.custNo = custNo;
	}

	public String getCrdtNo() {
		return crdtNo;
	}

	public void setCrdtNo(String crdtNo) {
		this.crdtNo = crdtNo;
	}

	public String getLoanNo() {
		return loanNo;
	}

	public void setLoanNo(String loanNo) {
		this.loanNo = loanNo;
	}

	public BigDecimal getStartcrdtAmt() {
		return startcrdtAmt;
	}

	public void setStartcrdtAmt(BigDecimal startcrdtAmt) {
		this.startcrdtAmt = startcrdtAmt;
	}

	public BigDecimal getEndcrdtAmt() {
		return endcrdtAmt;
	}

	public void setEndcrdtAmt(BigDecimal endcrdtAmt) {
		this.endcrdtAmt = endcrdtAmt;
	}

	public BigDecimal getStartLoanAmount() {
		return startLoanAmount;
	}

	public void setStartLoanAmount(BigDecimal startLoanAmount) {
		this.startLoanAmount = startLoanAmount;
	}

	public BigDecimal getEndLoanAmount() {
		return endLoanAmount;
	}

	public void setEndLoanAmount(BigDecimal endLoanAmount) {
		this.endLoanAmount = endLoanAmount;
	}

	public List getCrdtBankCode() {
		return crdtBankCode;
	}

	public void setCrdtBankCode(List crdtBankCode) {
		this.crdtBankCode = crdtBankCode;
	}

	public String getCustnumber() {
		return custnumber;
	}

	public void setCustnumber(String custnumber) {
		this.custnumber = custnumber;
	}

	

	public Date getStartCrdtIssDt() {
		return startCrdtIssDt;
	}

	public void setStartCrdtIssDt(Date startCrdtIssDt) {
		this.startCrdtIssDt = startCrdtIssDt;
	}

	public Date getEndCrdtIssDt() {
		return endCrdtIssDt;
	}

	public void setEndCrdtIssDt(Date endCrdtIssDt) {
		this.endCrdtIssDt = endCrdtIssDt;
	}

	public Date getStartCrdtDueDt() {
		return startCrdtDueDt;
	}

	public void setStartCrdtDueDt(Date startCrdtDueDt) {
		this.startCrdtDueDt = startCrdtDueDt;
	}

	public Date getEndCrdtDueDt() {
		return endCrdtDueDt;
	}

	public void setEndCrdtDueDt(Date endCrdtDueDt) {
		this.endCrdtDueDt = endCrdtDueDt;
	}

	public Date getStartTime1() {
		return startTime1;
	}

	public void setStartTime1(Date startTime1) {
		this.startTime1 = startTime1;
	}

	public Date getStartTime2() {
		return startTime2;
	}

	public void setStartTime2(Date startTime2) {
		this.startTime2 = startTime2;
	}

	public Date getEndTime1() {
		return endTime1;
	}

	public void setEndTime1(Date endTime1) {
		this.endTime1 = endTime1;
	}

	public Date getEndTime2() {
		return endTime2;
	}

	public void setEndTime2(Date endTime2) {
		this.endTime2 = endTime2;
	}

	public String getRisklevel() {
		return risklevel;
	}

	public void setRisklevel(String risklevel) {
		this.risklevel = risklevel;
	}

	public String getIfOnline() {
		return ifOnline;
	}

	public void setIfOnline(String ifOnline) {
		this.ifOnline = ifOnline;
	}

	public String getIfAdvanceAmt() {
		return ifAdvanceAmt;
	}

	public void setIfAdvanceAmt(String ifAdvanceAmt) {
		this.ifAdvanceAmt = ifAdvanceAmt;
	}

	public BigDecimal getStartLoanBalance() {
		return startLoanBalance;
	}

	public void setStartLoanBalance(BigDecimal startLoanBalance) {
		this.startLoanBalance = startLoanBalance;
	}

	public BigDecimal getEndLoanBalance() {
		return endLoanBalance;
	}

	public void setEndLoanBalance(BigDecimal endLoanBalance) {
		this.endLoanBalance = endLoanBalance;
	}
	
}
