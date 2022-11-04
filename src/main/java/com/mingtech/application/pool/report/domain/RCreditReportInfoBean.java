package com.mingtech.application.pool.report.domain;

import java.math.BigDecimal;
import java.util.Date;

public class RCreditReportInfoBean {
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
	
	private Date createDateStart;
	private Date createDateEnd;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBranchNo() {
		return branchNo;
	}
	public void setBranchNo(String branchNo) {
		this.branchNo = branchNo;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public String getBusiType() {
		return busiType;
	}
	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}
	public BigDecimal getBusiAmt() {
		return busiAmt;
	}
	public void setBusiAmt(BigDecimal busiAmt) {
		this.busiAmt = busiAmt;
	}
	public String getBankRank() {
		return bankRank;
	}
	public void setBankRank(String bankRank) {
		this.bankRank = bankRank;
	}
	public BigDecimal getYestNum() {
		return yestNum;
	}
	public void setYestNum(BigDecimal yestNum) {
		this.yestNum = yestNum;
	}
	public BigDecimal getYestRatio() {
		return yestRatio;
	}
	public void setYestRatio(BigDecimal yestRatio) {
		this.yestRatio = yestRatio;
	}
	public BigDecimal getLastMNum() {
		return lastMNum;
	}
	public void setLastMNum(BigDecimal lastMNum) {
		this.lastMNum = lastMNum;
	}
	public BigDecimal getLastMRatio() {
		return lastMRatio;
	}
	public void setLastMRatio(BigDecimal lastMRatio) {
		this.lastMRatio = lastMRatio;
	}
	public BigDecimal getLastYNum() {
		return lastYNum;
	}
	public void setLastYNum(BigDecimal lastYNum) {
		this.lastYNum = lastYNum;
	}
	public BigDecimal getLastYRatio() {
		return lastYRatio;
	}
	public void setLastYRatio(BigDecimal lastYRatio) {
		this.lastYRatio = lastYRatio;
	}
	public Date getCreateDateStart() {
		return createDateStart;
	}
	public void setCreateDateStart(Date createDateStart) {
		this.createDateStart = createDateStart;
	}
	public Date getCreateDateEnd() {
		return createDateEnd;
	}
	public void setCreateDateEnd(Date createDateEnd) {
		this.createDateEnd = createDateEnd;
	}
	
	
}
