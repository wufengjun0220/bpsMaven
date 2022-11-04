package com.mingtech.application.pool.discount.domain;

import java.math.BigDecimal;

/**
 * 贴现协议前手信息
 * */
public class TxExDetailBean {
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String modeTypeDesc;			//	修改类型 MOD_01-新增  MOD_02-维护 MOD_03=删除  其他-修改
	private String exCustNo;				//	前手编号
	private String hisExCustNo;				//	历史前手编号
	private String exCustName;				//	前手户名
	private String hisExCustName;			//	历史前手户名
	private BigDecimal exTotalAmt;			//	前手总额
	private BigDecimal hisExTotalAmt;		//	历史前手总额
	private BigDecimal exUsedAmt;			//	前手已用金额
	private BigDecimal hisExUsedAmt;		//	历史前手已用金额
	private BigDecimal exAvailableAmt;		//	前手可用金额
	private BigDecimal hisExAvailableAmt;	//	历史前手可用金额
	private String exStatus;				//	前手状态
	private String hisExStatus;				//	历史前手状态
	
	public String getHisExCustNo() {
		return hisExCustNo;
	}
	public void setHisExCustNo(String hisExCustNo) {
		this.hisExCustNo = hisExCustNo;
	}
	public String getHisExCustName() {
		return hisExCustName;
	}
	public void setHisExCustName(String hisExCustName) {
		this.hisExCustName = hisExCustName;
	}
	public BigDecimal getHisExTotalAmt() {
		return hisExTotalAmt;
	}
	public void setHisExTotalAmt(BigDecimal hisExTotalAmt) {
		this.hisExTotalAmt = hisExTotalAmt;
	}
	public BigDecimal getHisExUsedAmt() {
		return hisExUsedAmt;
	}
	public void setHisExUsedAmt(BigDecimal hisExUsedAmt) {
		this.hisExUsedAmt = hisExUsedAmt;
	}
	public BigDecimal getHisExAvailableAmt() {
		return hisExAvailableAmt;
	}
	public void setHisExAvailableAmt(BigDecimal hisExAvailableAmt) {
		this.hisExAvailableAmt = hisExAvailableAmt;
	}
	public String getHisExStatus() {
		return hisExStatus;
	}
	public void setHisExStatus(String hisExStatus) {
		this.hisExStatus = hisExStatus;
	}
	public String getModeTypeDesc() {
		return modeTypeDesc;
	}
	public void setModeTypeDesc(String modeTypeDesc) {
		this.modeTypeDesc = modeTypeDesc;
	}
	public String getExCustNo() {
		return exCustNo;
	}
	public void setExCustNo(String exCustNo) {
		this.exCustNo = exCustNo;
	}
	public String getExCustName() {
		return exCustName;
	}
	public void setExCustName(String exCustName) {
		this.exCustName = exCustName;
	}
	public BigDecimal getExTotalAmt() {
		return exTotalAmt;
	}
	public void setExTotalAmt(BigDecimal exTotalAmt) {
		this.exTotalAmt = exTotalAmt;
	}
	public BigDecimal getExUsedAmt() {
		return exUsedAmt;
	}
	public void setExUsedAmt(BigDecimal exUsedAmt) {
		this.exUsedAmt = exUsedAmt;
	}
	public BigDecimal getExAvailableAmt() {
		return exAvailableAmt;
	}
	public void setExAvailableAmt(BigDecimal exAvailableAmt) {
		this.exAvailableAmt = exAvailableAmt;
	}
	public String getExStatus() {
		return exStatus;
	}
	public void setExStatus(String exStatus) {
		this.exStatus = exStatus;
	}
	public TxExDetailBean(String exCustNo, String exCustName,
			BigDecimal exTotalAmt, BigDecimal exUsedAmt, BigDecimal exAvailableAmt,
			String exStatus) {
		super();
		this.exCustNo = exCustNo;
		this.exCustName = exCustName;
		this.exTotalAmt = exTotalAmt;
		this.exUsedAmt = exUsedAmt;
		this.exAvailableAmt = exAvailableAmt;
		this.exStatus = exStatus;
	}
	public TxExDetailBean() {
		super();
	}
	
	
}
