package com.mingtech.application.pool.edu.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 票据池对账任务实体
 * @Description 记录到该表中的数据均为需要网银对账的数据信息
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-12
 */

public class PedCheck implements java.io.Serializable {
	
	private static final long serialVersionUID = -4802049753189120255L;
	// Fields
	private String id;
	private String batchNo;//批次号
	private String poolAgreement;//票据池编号
	private String poolAgreementName;//票据池名称
	private String custNo;//客户号（出质人）
	private String custName;//客户名称（出质人姓名）
	private String collztnBkNm;//质权人
	private String isGroup;//是否集团
	private Date accountDate;//账务日期
	private String marginAccount;//保证金账号
	private String marginAccountName;//保证金账号名称
	private BigDecimal marginBalance;//保证金余额
	private String billTotalNum;//票据总张数
	private BigDecimal billTotalAmount;//票据总金额
	private String checkResult;//对账结果   （DZJG_00：未对账  DZJG_01：对账一致  DZJG_02：对账不一致 ）
	private Date checkDate;//对账时间
	private Date curTime;//创建时间
	private String remark;//备注
	private String isAuto;//是否是手动生成的对账，手动生成的任务可以撤销   0：否    1：是
	//liuxiaodong  add 查询使用
	private Date curTimeStart;//开始时间
	private Date curTimeEnd;//结束时间
	private String pledgeNo;//质押清单编号
	
	// Constructors

	/** default constructor */
	public PedCheck() {
	}

	/** full constructor */
	public PedCheck(String batchNo, String poolAgreement,
			String poolAgreementName, String custNo, String custName,
			String collztnBkNm, String isGroup, Date accountDate,
			String marginAccount, String marginAccountName,
			BigDecimal marginBalance, String billTotalNum, BigDecimal billTotalAmount,
			String checkResult, Date checkDate, Date curTime, String remark,String isAuto) {
		this.batchNo = batchNo;
		this.poolAgreement = poolAgreement;
		this.poolAgreementName = poolAgreementName;
		this.custNo = custNo;
		this.custName = custName;
		this.collztnBkNm = collztnBkNm;
		this.isGroup = isGroup;
		this.accountDate = accountDate;
		this.marginAccount = marginAccount;
		this.marginAccountName = marginAccountName;
		this.marginBalance = marginBalance;
		this.billTotalNum = billTotalNum;
		this.billTotalAmount = billTotalAmount;
		this.checkResult = checkResult;
		this.checkDate = checkDate;
		this.curTime = curTime;
		this.remark = remark;
		this.isAuto = isAuto;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public String getPledgeNo() {
		return pledgeNo;
	}

	public void setPledgeNo(String pledgeNo) {
		this.pledgeNo = pledgeNo;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIsAuto() {
		return isAuto;
	}

	public void setIsAuto(String isAuto) {
		this.isAuto = isAuto;
	}

	public String getBatchNo() {
		return this.batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getPoolAgreement() {
		return this.poolAgreement;
	}

	public void setPoolAgreement(String poolAgreement) {
		this.poolAgreement = poolAgreement;
	}

	public String getPoolAgreementName() {
		return this.poolAgreementName;
	}

	public void setPoolAgreementName(String poolAgreementName) {
		this.poolAgreementName = poolAgreementName;
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

	public String getCollztnBkNm() {
		return this.collztnBkNm;
	}

	public void setCollztnBkNm(String collztnBkNm) {
		this.collztnBkNm = collztnBkNm;
	}

	public String getIsGroup() {
		return this.isGroup;
	}

	public void setIsGroup(String isGroup) {
		this.isGroup = isGroup;
	}

	public Date getAccountDate() {
		return this.accountDate;
	}

	public void setAccountDate(Date accountDate) {
		this.accountDate = accountDate;
	}

	public String getMarginAccount() {
		return this.marginAccount;
	}

	public void setMarginAccount(String marginAccount) {
		this.marginAccount = marginAccount;
	}

	public String getMarginAccountName() {
		return this.marginAccountName;
	}

	public void setMarginAccountName(String marginAccountName) {
		this.marginAccountName = marginAccountName;
	}

	public BigDecimal getMarginBalance() {
		return this.marginBalance;
	}

	public void setMarginBalance(BigDecimal marginBalance) {
		this.marginBalance = marginBalance;
	}

	public String getBillTotalNum() {
		return this.billTotalNum;
	}

	public void setBillTotalNum(String billTotalNum) {
		this.billTotalNum = billTotalNum;
	}

	public BigDecimal getBillTotalAmount() {
		return this.billTotalAmount;
	}

	public void setBillTotalAmount(BigDecimal billTotalAmount) {
		this.billTotalAmount = billTotalAmount;
	}

	public String getCheckResult() {
		return this.checkResult;
	}

	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}

	public Date getCheckDate() {
		return this.checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public Date getCurTime() {
		return this.curTime;
	}

	public void setCurTime(Date curTime) {
		this.curTime = curTime;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getCurTimeStart() {
		return curTimeStart;
	}

	public void setCurTimeStart(Date curTimeStart) {
		this.curTimeStart = curTimeStart;
	}

	public Date getCurTimeEnd() {
		return curTimeEnd;
	}

	public void setCurTimeEnd(Date curTimeEnd) {
		this.curTimeEnd = curTimeEnd;
	}
	
	
}