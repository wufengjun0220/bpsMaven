package com.mingtech.application.pool.report.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * RCreditReportInfo entity. @author MyEclipse Persistence Tools
 */

public class RCreditReportInfo implements java.io.Serializable {


	private static final long serialVersionUID = 1L;
	// Fields

	private String id;
	private String branchNo;//机构号
	private String branchName;//机构名称
	private Date createDate;//创建日期
	private String busiType;//业务类型   XD_01——银承    XD_02——流贷    XD_03——保函    XD_04——信用证    XD_05——表外业务垫款     XD_06——全部信贷业务交易类型
	private BigDecimal busiAmt;//业务发生额
	private String bankRank;//全行排名
	private BigDecimal yestNum;//较昨日增减值
	private BigDecimal yestRatio;//较昨日增减率
	private BigDecimal lastMNum;//较上月末增减值
	private BigDecimal lastMRatio;//较上月末增率
	private BigDecimal lastYNum;//较上年末增减值
	private BigDecimal lastYRatio;//较上年末增减率

	// Constructors

	/** default constructor */
	public RCreditReportInfo() {
	}

	/** full constructor */
	public RCreditReportInfo(String branchNo, String branchName,
			Date createDate, String busiType, BigDecimal busiAmt,
			String bankRank, BigDecimal yestNum, BigDecimal yestRatio,
			BigDecimal lastMNum, BigDecimal lastMRatio, BigDecimal lastYNum,
			BigDecimal lastYRatio) {
		this.branchNo = branchNo;
		this.branchName = branchName;
		this.createDate = createDate;
		this.busiType = busiType;
		this.busiAmt = busiAmt;
		this.bankRank = bankRank;
		this.yestNum = yestNum;
		this.yestRatio = yestRatio;
		this.lastMNum = lastMNum;
		this.lastMRatio = lastMRatio;
		this.lastYNum = lastYNum;
		this.lastYRatio = lastYRatio;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBranchNo() {
		return this.branchNo;
	}

	public void setBranchNo(String branchNo) {
		this.branchNo = branchNo;
	}

	public String getBranchName() {
		return this.branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getBusiType() {
		return this.busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public BigDecimal getBusiAmt() {
		return this.busiAmt;
	}

	public void setBusiAmt(BigDecimal busiAmt) {
		this.busiAmt = busiAmt;
	}

	public String getBankRank() {
		return this.bankRank;
	}

	public void setBankRank(String bankRank) {
		this.bankRank = bankRank;
	}

	public BigDecimal getYestNum() {
		return this.yestNum;
	}

	public void setYestNum(BigDecimal yestNum) {
		this.yestNum = yestNum;
	}

	public BigDecimal getYestRatio() {
		return this.yestRatio;
	}

	public void setYestRatio(BigDecimal yestRatio) {
		this.yestRatio = yestRatio;
	}

	public BigDecimal getLastMNum() {
		return this.lastMNum;
	}

	public void setLastMNum(BigDecimal lastMNum) {
		this.lastMNum = lastMNum;
	}

	public BigDecimal getLastMRatio() {
		return this.lastMRatio;
	}

	public void setLastMRatio(BigDecimal lastMRatio) {
		this.lastMRatio = lastMRatio;
	}

	public BigDecimal getLastYNum() {
		return this.lastYNum;
	}

	public void setLastYNum(BigDecimal lastYNum) {
		this.lastYNum = lastYNum;
	}

	public BigDecimal getLastYRatio() {
		return this.lastYRatio;
	}

	public void setLastYRatio(BigDecimal lastYRatio) {
		this.lastYRatio = lastYRatio;
	}

}