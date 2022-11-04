package com.mingtech.application.pool.report.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * RPoolReportInfo entity. @author MyEclipse Persistence Tools
 */

public class RPoolReportInfo implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private String id;
	private String branchNo;//机构号
	private String branchName;//机构名称
	private Date createDate;//创建日期
	private BigDecimal proTotalNum;//协议总笔数
	private String proRank;//截至当日签约数量全行排名
	private BigDecimal proCompYestNum;//协议较上日增减值
	private BigDecimal proCompYestRatio;//协议较上日增减比例
	private BigDecimal proCompLastMNum;//协议较上月末增减值
	private BigDecimal proCompLastMRatio;//协议较上月末增减比率
	private BigDecimal proCompLastYNum;//协议较上年增减值
	private BigDecimal proCompLastYRatio;//协议较上年增减比率
	private BigDecimal draftTotalAmt;//入池票面金额汇总
	private String draftRank;//截至当日的票据余额全行排名
	private BigDecimal draftCompYestNum;//入池票据较上日增减值
	private BigDecimal draftCompYestRatio;//入池票据较上日增减比例
	private BigDecimal draftCompLastMNum;//入池票据较上月末增减值
	private BigDecimal draftCompLastMRatio;//入池票据较上月末增减比率
	private BigDecimal draftCompLastYNum;//入池票据较上年增减值
	private BigDecimal draftCompLastYRatio;//入池票据较上年增减比率
	private BigDecimal marginTotalAmt;//入池保证金余额
	private String marginRank;//截至当日保证金余额全行排名
	private BigDecimal marginCompYestNum;//保证金较上日增减值
	private BigDecimal marginCompYestRatio;//保证金较上日增减比例
	private BigDecimal marginCompLastMNum;//保证金较上月末增减值
	private BigDecimal marginCompLastMRatio;//保证金较上月末增减比率
	private BigDecimal marginCompLastYNum;//保证金较上年增减值
	private BigDecimal marginCompLastYRatio;//保证金较上年增减比率

	// Constructors

	/** default constructor */
	public RPoolReportInfo() {
	}

	/** full constructor */
	public RPoolReportInfo(String branchNo, String branchName, Date createDate,
			BigDecimal proTotalNum, String proRank, BigDecimal proCompYestNum,
			BigDecimal proCompYestRatio, BigDecimal proCompLastMNum,
			BigDecimal proCompLastMRatio, BigDecimal proCompLastYNum,
			BigDecimal proCompLastYRatio, BigDecimal draftTotalAmt,
			String draftRank, BigDecimal draftCompYestNum,
			BigDecimal draftCompYestRatio, BigDecimal draftCompLastMNum,
			BigDecimal draftCompLastMRatio, BigDecimal draftCompLastYNum,
			BigDecimal draftCompLastYRatio, BigDecimal marginTotalAmt,
			String marginRank, BigDecimal marginCompYestNum,
			BigDecimal marginCompYestRatio, BigDecimal marginCompLastMNum,
			BigDecimal marginCompLastMRatio, BigDecimal marginCompLastYNum,
			BigDecimal marginCompLastYRatio) {
		this.branchNo = branchNo;
		this.branchName = branchName;
		this.createDate = createDate;
		this.proTotalNum = proTotalNum;
		this.proRank = proRank;
		this.proCompYestNum = proCompYestNum;
		this.proCompYestRatio = proCompYestRatio;
		this.proCompLastMNum = proCompLastMNum;
		this.proCompLastMRatio = proCompLastMRatio;
		this.proCompLastYNum = proCompLastYNum;
		this.proCompLastYRatio = proCompLastYRatio;
		this.draftTotalAmt = draftTotalAmt;
		this.draftRank = draftRank;
		this.draftCompYestNum = draftCompYestNum;
		this.draftCompYestRatio = draftCompYestRatio;
		this.draftCompLastMNum = draftCompLastMNum;
		this.draftCompLastMRatio = draftCompLastMRatio;
		this.draftCompLastYNum = draftCompLastYNum;
		this.draftCompLastYRatio = draftCompLastYRatio;
		this.marginTotalAmt = marginTotalAmt;
		this.marginRank = marginRank;
		this.marginCompYestNum = marginCompYestNum;
		this.marginCompYestRatio = marginCompYestRatio;
		this.marginCompLastMNum = marginCompLastMNum;
		this.marginCompLastMRatio = marginCompLastMRatio;
		this.marginCompLastYNum = marginCompLastYNum;
		this.marginCompLastYRatio = marginCompLastYRatio;
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

	public BigDecimal getProTotalNum() {
		return this.proTotalNum;
	}

	public void setProTotalNum(BigDecimal proTotalNum) {
		this.proTotalNum = proTotalNum;
	}

	public String getProRank() {
		return this.proRank;
	}

	public void setProRank(String proRank) {
		this.proRank = proRank;
	}

	public BigDecimal getProCompYestNum() {
		return this.proCompYestNum;
	}

	public void setProCompYestNum(BigDecimal proCompYestNum) {
		this.proCompYestNum = proCompYestNum;
	}

	public BigDecimal getProCompYestRatio() {
		return this.proCompYestRatio;
	}

	public void setProCompYestRatio(BigDecimal proCompYestRatio) {
		this.proCompYestRatio = proCompYestRatio;
	}

	public BigDecimal getProCompLastMNum() {
		return this.proCompLastMNum;
	}

	public void setProCompLastMNum(BigDecimal proCompLastMNum) {
		this.proCompLastMNum = proCompLastMNum;
	}

	public BigDecimal getProCompLastMRatio() {
		return this.proCompLastMRatio;
	}

	public void setProCompLastMRatio(BigDecimal proCompLastMRatio) {
		this.proCompLastMRatio = proCompLastMRatio;
	}

	public BigDecimal getProCompLastYNum() {
		return this.proCompLastYNum;
	}

	public void setProCompLastYNum(BigDecimal proCompLastYNum) {
		this.proCompLastYNum = proCompLastYNum;
	}

	public BigDecimal getProCompLastYRatio() {
		return this.proCompLastYRatio;
	}

	public void setProCompLastYRatio(BigDecimal proCompLastYRatio) {
		this.proCompLastYRatio = proCompLastYRatio;
	}

	public BigDecimal getDraftTotalAmt() {
		return this.draftTotalAmt;
	}

	public void setDraftTotalAmt(BigDecimal draftTotalAmt) {
		this.draftTotalAmt = draftTotalAmt;
	}

	public String getDraftRank() {
		return this.draftRank;
	}

	public void setDraftRank(String draftRank) {
		this.draftRank = draftRank;
	}

	public BigDecimal getDraftCompYestNum() {
		return this.draftCompYestNum;
	}

	public void setDraftCompYestNum(BigDecimal draftCompYestNum) {
		this.draftCompYestNum = draftCompYestNum;
	}

	public BigDecimal getDraftCompYestRatio() {
		return this.draftCompYestRatio;
	}

	public void setDraftCompYestRatio(BigDecimal draftCompYestRatio) {
		this.draftCompYestRatio = draftCompYestRatio;
	}

	public BigDecimal getDraftCompLastMNum() {
		return this.draftCompLastMNum;
	}

	public void setDraftCompLastMNum(BigDecimal draftCompLastMNum) {
		this.draftCompLastMNum = draftCompLastMNum;
	}

	public BigDecimal getDraftCompLastMRatio() {
		return this.draftCompLastMRatio;
	}

	public void setDraftCompLastMRatio(BigDecimal draftCompLastMRatio) {
		this.draftCompLastMRatio = draftCompLastMRatio;
	}

	public BigDecimal getDraftCompLastYNum() {
		return this.draftCompLastYNum;
	}

	public void setDraftCompLastYNum(BigDecimal draftCompLastYNum) {
		this.draftCompLastYNum = draftCompLastYNum;
	}

	public BigDecimal getDraftCompLastYRatio() {
		return this.draftCompLastYRatio;
	}

	public void setDraftCompLastYRatio(BigDecimal draftCompLastYRatio) {
		this.draftCompLastYRatio = draftCompLastYRatio;
	}

	public BigDecimal getMarginTotalAmt() {
		return this.marginTotalAmt;
	}

	public void setMarginTotalAmt(BigDecimal marginTotalAmt) {
		this.marginTotalAmt = marginTotalAmt;
	}

	public String getMarginRank() {
		return this.marginRank;
	}

	public void setMarginRank(String marginRank) {
		this.marginRank = marginRank;
	}

	public BigDecimal getMarginCompYestNum() {
		return this.marginCompYestNum;
	}

	public void setMarginCompYestNum(BigDecimal marginCompYestNum) {
		this.marginCompYestNum = marginCompYestNum;
	}

	public BigDecimal getMarginCompYestRatio() {
		return this.marginCompYestRatio;
	}

	public void setMarginCompYestRatio(BigDecimal marginCompYestRatio) {
		this.marginCompYestRatio = marginCompYestRatio;
	}

	public BigDecimal getMarginCompLastMNum() {
		return this.marginCompLastMNum;
	}

	public void setMarginCompLastMNum(BigDecimal marginCompLastMNum) {
		this.marginCompLastMNum = marginCompLastMNum;
	}

	public BigDecimal getMarginCompLastMRatio() {
		return this.marginCompLastMRatio;
	}

	public void setMarginCompLastMRatio(BigDecimal marginCompLastMRatio) {
		this.marginCompLastMRatio = marginCompLastMRatio;
	}

	public BigDecimal getMarginCompLastYNum() {
		return this.marginCompLastYNum;
	}

	public void setMarginCompLastYNum(BigDecimal marginCompLastYNum) {
		this.marginCompLastYNum = marginCompLastYNum;
	}

	public BigDecimal getMarginCompLastYRatio() {
		return this.marginCompLastYRatio;
	}

	public void setMarginCompLastYRatio(BigDecimal marginCompLastYRatio) {
		this.marginCompLastYRatio = marginCompLastYRatio;
	}

}