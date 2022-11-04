package com.mingtech.application.pool.common.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 票据池服务费收取记录
 * @Description TODO
 * @author Ju Nana
 * @version v1.0
 * @date 2019-6-1
 */

public class PlFeeList implements java.io.Serializable {

	private static final long serialVersionUID = -7494820655006713130L;
	private String id;
	private String bpsNo;//票据池编号
	private String bpsName;//票据池名称
	private String custNo;//客户号
	private String custName;//客户名称
	private String feeType;//收费模式（SFMS_01:年费 SFMS_02:逐笔）
	private String feeBatchNo;//收费批次号     
	private BigDecimal recvAmt;//应收金额
	private BigDecimal realAmt;//实收金额
	private BigDecimal lessAmt;//减免金额
	private Date chargeDate;//收费时间
	private Date FeeStaDate;//服务收取日   --不存入收费信息表，记录到协议表
	private Date FeeDueDate;//服务到期日  --不存入收费信息表，记录到协议表
	private String remart;//备注
	private String source;//收费来源  SFLY_00网银  SFLY_01 柜面

	// Constructors

	/** default constructor */
	public PlFeeList() {
	}

	/** full constructor */
	public PlFeeList(String bpsNo, String bpsName, String custNo,
			String custName, String feeType, String feeBatchNo,
			BigDecimal recvAmt, BigDecimal realAmt, BigDecimal lessAmt,
			Date chargeDate, String remart) {
		this.bpsNo = bpsNo;
		this.bpsName = bpsName;
		this.custNo = custNo;
		this.custName = custName;
		this.feeType = feeType;
		this.feeBatchNo = feeBatchNo;
		this.recvAmt = recvAmt;
		this.realAmt = realAmt;
		this.lessAmt = lessAmt;
		this.chargeDate = chargeDate;
		this.remart = remart;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBpsNo() {
		return this.bpsNo;
	}

	public void setBpsNo(String bpsNo) {
		this.bpsNo = bpsNo;
	}

	public String getBpsName() {
		return this.bpsName;
	}

	public void setBpsName(String bpsName) {
		this.bpsName = bpsName;
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

	public String getFeeType() {
		return this.feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public String getFeeBatchNo() {
		return this.feeBatchNo;
	}

	public void setFeeBatchNo(String feeBatchNo) {
		this.feeBatchNo = feeBatchNo;
	}

	public BigDecimal getRecvAmt() {
		return this.recvAmt;
	}

	public void setRecvAmt(BigDecimal recvAmt) {
		this.recvAmt = recvAmt;
	}

	public BigDecimal getRealAmt() {
		return this.realAmt;
	}

	public void setRealAmt(BigDecimal realAmt) {
		this.realAmt = realAmt;
	}

	public BigDecimal getLessAmt() {
		return this.lessAmt;
	}

	public void setLessAmt(BigDecimal lessAmt) {
		this.lessAmt = lessAmt;
	}

	public Date getChargeDate() {
		return this.chargeDate;
	}

	public void setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
	}

	public String getRemart() {
		return this.remart;
	}

	public void setRemart(String remart) {
		this.remart = remart;
	}

	public Date getFeeStaDate() {
		return FeeStaDate;
	}

	public void setFeeStaDate(Date feeStaDate) {
		FeeStaDate = feeStaDate;
	}

	public Date getFeeDueDate() {
		return FeeDueDate;
	}

	public void setFeeDueDate(Date feeDueDate) {
		FeeDueDate = feeDueDate;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}